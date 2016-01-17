package net.morimekta.speedtest;

import net.morimekta.providence.serializer.PBinarySerializer;
import net.morimekta.providence.serializer.PJsonSerializer;
import net.morimekta.providence.serializer.PSerializeException;
import net.morimekta.providence.serializer.PSerializer;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;
import net.morimekta.providence.util.io.CountingOutputStream;
import net.morimekta.test.providence.Containers;
import net.morimekta.utils.Color;
import net.morimekta.utils.FormatString;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Speed test running through:
 *  - Read a file for each protocol / serialization format.
 *  - Write the same file back to a temp file.
 */
public class SpeedTest {
    public static class Options {
        @Option(name = "--entries",
                usage = "Expected number of entries to in input files")
        public int entries = 10000;

        @Option(name = "--runs",
                usage = "Number of runs per file. If negative, never stop")
        public int runs = 1;

        enum Operation {
            read,
            write,
            both,
        }

        @Option(name = "--op",
                usage = "Which IO operation to do, or both.")
        public Operation op = Operation.both;

        enum Engine {
            providence,
            thrift,
            both,
        }

        @Option(name = "--engine",
                usage = "Which compiler engine to check, or both.")
        public Engine engine = Engine.both;

        @Option(name = "--cleanup",
                usage = "Clean up files after testing to avoid disk clutter.")
        public boolean cleanup = false;

        @Option(name = "--format",
                usage = "Only test test this format. If not set, tests all formats.")
        public Format format = null;

        @Argument(usage = "Base input directory of data",
                  metaVar = "DIR",
                  required = true)
        public String in;
    }

    public static class Result {
        /**
         * Which format was tested.
         */
        public final Format format;

        /**
         * Per message write in nanosecond statistics.
         */
        public final DescriptiveStatistics writeStat;

        /**
         * Per run (single file) write in nanosecond statistics.
         */
        public final DescriptiveStatistics writeRunStat;

        /**
         * Per read time in nanosecond statistics.
         */
        public final DescriptiveStatistics readStat;

        /**
         * Per run (single file) read in nanosecond statistics.
         */
        public final DescriptiveStatistics readRunStat;

        public int readDiscrepancies = 0;

        public Result(Format format) {
            this.format = format;

            writeStat = new DescriptiveStatistics();
            writeRunStat = new DescriptiveStatistics();

            readStat = new DescriptiveStatistics();
            readRunStat = new DescriptiveStatistics();
        }

        @Override
        public String toString() {
            final long readMs = (long) readRunStat.getSum() / 1000000;
            final long writeMs = (long) writeRunStat.getSum() / 1000000;

            String disc = "";
            if (readDiscrepancies > 0) {
                disc = String.format(" disc: %d", readDiscrepancies);
            }

            return String.format(Locale.ENGLISH,
                                 "%20s in %6.2fs (r: %5.2fs, w: %5.2fs)%s",
                                 format.name(),
                                 (double) (readMs + writeMs) / 1000,
                                 (double) readMs / 1000,
                                 (double) writeMs / 1000,
                                 disc);
        }
    }

    //                                    read         write
    public enum Format {          //  prov  thrift - prov  thrift
        json_pretty("json"),      // 13.65         -
        json_named("json"),       // 12.41         -
        json("json"),             // 11.52         -

        json_protocol("json"),    //  8.86   10.86 -

        binary("bin"),            //  2.37         -

        compact_protocol("bin"),  //  2.44    2.71 -
        binary_protocol("bin"),   //  2.05    2.43 -
        tuple_protocol("tuples"), //  2.04    1.97 -
        ;

        String suffix;

        Format(String s) {
            suffix = s;
        }
    }

    private final Options                                         options;
    private final CmdLineParser                                   parser;
    private final ArrayList<net.morimekta.test.thrift.Containers> thriftContainers;
    private final ArrayList<Containers>                           thriftJ2Containers;
    private final File                                            dataDir;
    private final File                                            testDir;

    public SpeedTest(Options opts, CmdLineParser prs) throws CmdLineException, IOException {
        options = opts;
        parser = prs;

        thriftContainers = new ArrayList<>(options.entries);
        thriftJ2Containers = new ArrayList<>(options.entries);

        dataDir = new File(options.in);
        if (!dataDir.exists()) {
            throw new CmdLineException(parser, new FormatString("Input folder does not exist: %s"), options.in);
        }
        if (!dataDir.isDirectory()) {
            throw new CmdLineException(parser, new FormatString("Output is not a directory: %s"), options.in);
        }

        testDir = File.createTempFile("providence-", "-speed-test");
        if (testDir.exists()) {
            testDir.delete();
        }
        testDir.mkdirs();
    }

    public InputStream getInput(Format format) throws IOException {
        File inFile = new File(dataDir, String.format("%s.%s", format.name(), format.suffix));
        return new BufferedInputStream(new FileInputStream(inFile));
    }

    public void readTestData() throws IOException {
        InputStream in = getInput(Format.binary_protocol);
        TProtocolFactory factory = new TBinaryProtocol.Factory();
        TProtocol protocol = factory.getProtocol(new TIOStreamTransport(in));

        thriftContainers.clear();

        long start = System.nanoTime();

        for (int i = 0; i < options.entries; ++i) {
            try {
                net.morimekta.test.thrift.Containers containers = new net.morimekta.test.thrift.Containers();
                containers.read(protocol);
                thriftContainers.add(containers);
                // Check if we have another entry. There should be a separating '\n' char.
                if (in.read() == -1) {
                    break;
                }
            } catch (TException e) {
                e.printStackTrace();
                break;
            }
        }

        long end = System.nanoTime();
        long ms = (end - start) / 1000000;

        System.out.format(" -- Read %d     [thrift] entries in %5.2fs.\n",
                          thriftContainers.size(), (double) ms / 1000);

        thriftJ2Containers.clear();

        in = getInput(Format.binary);
        PSerializer serializer = new PBinarySerializer();

        start = System.nanoTime();

        for (int i = 0; i < options.entries; ++i) {
            try {
                Containers containers = serializer.deserialize(in, Containers.kDescriptor);
                thriftJ2Containers.add(containers);
                // Check if we have another entry. There should be a separating '\n' char.
                if (in.read() == -1) {
                    break;
                }
            } catch (PSerializeException se) {
                se.printStackTrace();
                break;
            }
        }

        end = System.nanoTime();
        ms = (end - start) / 1000000;

        System.out.format(" -- Read %d [providence] entries in %5.2fs.\n",
                          thriftContainers.size(), (double) ms / 1000);
        System.out.println();
    }

    public File getTestFile(Format format, String variant, int run) {
        if (options.runs < 0) {
            // rotate between file [1 .. 10]...
            return new File(testDir, String.format("%s-%s-%03d.%s", format.name(), variant, ((run - 1) % 10) + 1, format.suffix));
        } else if (options.op == Options.Operation.read) {
            // If we test fixed N reads' make only one file.
            return new File(testDir, String.format("%s-%s.%s", format.name(), variant, format.suffix));
        } else {
            return new File(testDir, String.format("%s-%s-%03d.%s", format.name(), variant, run, format.suffix));
        }
    }

    public CountingOutputStream getTestOutput(Format format, String variant, int run) throws IOException {
        File outFile = getTestFile(format, variant, run);
        if (options.cleanup) {
            outFile.deleteOnExit();
        }
        outFile.createNewFile();

        return new CountingOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(outFile, false)));
    }

    public InputStream getTestInput(Format format, String variant, int run) throws IOException {
        File inFile = getTestFile(format, variant, run);
        if (!inFile.exists()) {
            throw new IOException("No such test file: " + inFile.getAbsolutePath());
        }

        return new BufferedInputStream(new FileInputStream(inFile));
    }

    public long runThriftTest(Result result, int run) throws IOException, TException {
        TProtocolFactory factory;
        switch (result.format) {
            case binary_protocol:
                factory = new TBinaryProtocol.Factory();
                break;
            case compact_protocol:
                factory = new TCompactProtocol.Factory();
                break;
            case json_protocol:
                factory = new TJSONProtocol.Factory();
                break;
            case tuple_protocol:
                factory = new TTupleProtocol.Factory();
                break;
            default:
                throw new IOException("Format " + result.format.name() + " not supported for thrift (v0.9.3) test.");
        }

        long testStart = System.nanoTime();

        final int num = thriftContainers.size();

        if (options.op != Options.Operation.read) {
            // Write test.
            CountingOutputStream out = getTestOutput(result.format, "t1", run);
            TProtocol protocol = factory.getProtocol(new TIOStreamTransport(out));

            long writeRunTime = 0;

            for (int i = 0; i < num; ++i) {
                net.morimekta.test.thrift.Containers containers = thriftContainers.get(i);

                long start = System.nanoTime();

                if (i != 0) {
                    out.write('\n');
                }
                containers.write(protocol);
                out.flush();

                long end = System.nanoTime();
                long writeTime = end - start;

                writeRunTime += writeTime;
                result.writeStat.addValue(writeTime);
                result.writeStat.addValue(writeTime);
            }

            out.close();

            result.writeRunStat.addValue(writeRunTime);
        }

        if (options.op != Options.Operation.write) {
            // Read test.
            InputStream in = getTestInput(result.format, "t1", run);
            TProtocol protocol = factory.getProtocol(new TIOStreamTransport(in));

            long readRunTime = 0;

            for (int i = 0; i < num; ++i) {
                boolean stop = false;
                net.morimekta.test.thrift.Containers orig = thriftContainers.get(i);
                net.morimekta.test.thrift.Containers read = new net.morimekta.test.thrift.Containers();

                long start = System.nanoTime();

                read.read(protocol);
                // Check if we have another entry. There should be a separating '\n' char.
                if (in.read() == -1) {
                    stop = true;
                }

                long end = System.nanoTime();
                long readTime = end - start;

                readRunTime += readTime;
                result.readStat.addValue(readTime);

                if (!read.equals(orig)) {
                    ++result.readDiscrepancies;
                }

                if (stop) {
                    break;
                }
            }

            in.close();

            result.readRunStat.addValue(readRunTime);
        }

        long testEnd = System.nanoTime();
        return (testEnd - testStart) / 1000000;
    }

    public long runProvidenceTest(Result result, int run) throws IOException, PSerializeException {
        PSerializer serializer;
        switch (result.format) {
            case binary:
                serializer = new PBinarySerializer();
                break;
            case binary_protocol:
                serializer = new TBinaryProtocolSerializer();
                break;
            case compact_protocol:
                serializer = new TCompactProtocolSerializer();
                break;
            case json:
                serializer = new PJsonSerializer(PJsonSerializer.IdType.ID);
                break;
            case json_named:
                serializer = new PJsonSerializer(PJsonSerializer.IdType.NAME);
                break;
            case json_pretty:
                serializer = new PJsonSerializer(false, PJsonSerializer.IdType.NAME, PJsonSerializer.IdType.NAME, true);
                break;
            case json_protocol:
                serializer = new TJsonProtocolSerializer();
                break;
            case tuple_protocol:
                serializer = new TTupleProtocolSerializer();
                break;
            default:
                return 0;
        }

        long testStart = System.nanoTime();
        final int num = thriftContainers.size();

        // Write test.
        if (options.op != Options.Operation.read) {
            CountingOutputStream out = getTestOutput(result.format, "p2", run);

            long writeRunTime = 0;
            for (int i = 0; i < num; ++i) {
                Containers containers = thriftJ2Containers.get(i);

                long start = System.nanoTime();

                if (i != 0) {
                    out.write('\n');
                }
                serializer.serialize(out, containers);
                out.flush();

                long end = System.nanoTime();
                long writeTime = end - start;

                writeRunTime += writeTime;
                result.writeStat.addValue(writeTime);
            }

            out.close();

            result.writeRunStat.addValue(writeRunTime);
        }

        // Read test
        if (options.op != Options.Operation.write) {
            InputStream in = getTestInput(result.format, "p2", run);

            long readRunTime = 0;
            for (int i = 0; i < num; ++i) {
                Containers orig = thriftJ2Containers.get(i);

                boolean stop = false;
                long start = System.nanoTime();

                Containers read = serializer.deserialize(in, Containers.kDescriptor);
                if (read == null) {
                    System.out.println("Oops.");
                    break;
                }
                if (in.read() == -1) {
                    stop = true;
                }

                long end = System.nanoTime();
                long readTime = end - start;

                readRunTime += readTime;
                result.readStat.addValue(readTime);

                if (!read.equals(orig)) {
                    ++result.readDiscrepancies;
                }

                if (stop) {
                    break;
                }
            }

            in.close();

            result.readRunStat.addValue(readRunTime);
        }

        long testEnd = System.nanoTime();
        return (testEnd - testStart) / 1000000;
    }

    public void runTests(Result providenceResult, Result thriftResult, Format format, int run, boolean fullStats)
            throws IOException, PSerializeException, TException {
        if (options.engine != Options.Engine.providence) {
            if (format.name().endsWith("_protocol")) {
                runThriftTest(thriftResult, run);
                if (fullStats) {
                    System.out.format("%s[thrift]    %3d:%s%s\n",
                                      Color.YELLOW,
                                      run, thriftResult.toString(),
                                      Color.CLEAR);
                }
            }
        }
        if (options.engine != Options.Engine.thrift) {
            runProvidenceTest(providenceResult, run);
            if (fullStats) {
                System.out.format("[providence]%3d:%s\n", run, providenceResult.toString());
            }
        }
    }

    public void runTest(Format format, boolean fullStats) throws IOException, TException, PSerializeException {
        // First test thrift-p2.
        if (options.op == Options.Operation.read) {
            PSerializer serializer;
            switch (format) {
                case binary:
                    serializer = new PBinarySerializer();
                    break;
                case binary_protocol:
                    serializer = new TBinaryProtocolSerializer();
                    break;
                case compact_protocol:
                    serializer = new TCompactProtocolSerializer();
                    break;
                case json:
                    serializer = new PJsonSerializer(PJsonSerializer.IdType.ID);
                    break;
                case json_named:
                    serializer = new PJsonSerializer(PJsonSerializer.IdType.NAME);
                    break;
                case json_pretty:
                    serializer = new PJsonSerializer(false, PJsonSerializer.IdType.NAME, PJsonSerializer.IdType.NAME, true);
                    break;
                case json_protocol:
                    serializer = new TJsonProtocolSerializer();
                    break;
                case tuple_protocol:
                    serializer = new TTupleProtocolSerializer();
                    break;
                default:
                    throw new IllegalArgumentException("NOOO");
            }

            File first = getTestFile(format, "p2", 1);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(first));
            for (int i = 0; i < options.entries; ++i) {
                Containers containers = thriftJ2Containers.get(i);

                if (i != 0) {
                    out.write('\n');
                }
                serializer.serialize(out, containers);
            }
            out.flush();
            out.close();

            if (options.runs < 0) {
                for (int i = 2; i <= 10; ++i) {
                    File nth = getTestFile(format, "p2", i);

                    Files.copy(first.toPath(), nth.toPath());
                }
            }
        }

        if (options.runs < 0) {
            for (int run = 0; run < 10_000; ++run) {
                System.out.println();
                System.out.println(" ------------ RUN " + run + " ------------");
                System.out.println();

                Result providenceResult = new Result(format);
                Result thriftResult = new Result(format);

                runTests(providenceResult, thriftResult, format, run, false);
                printStats(providenceResult, thriftResult, true);
            }
        } else {
            Result providenceResult = new Result(format);
            Result thriftResult = new Result(format);
            for (int run = 1; run <= options.runs; ++run) {
                runTests(providenceResult, thriftResult, format, run, fullStats);
            }

            printStats(providenceResult, thriftResult, fullStats);
        }
    }

    public void printStats(Result providenceResult, Result thriftResult, boolean fullStats) {
        if (providenceResult.format.name().endsWith("_protocol")) {
            if (fullStats) {
                if (options.op != Options.Operation.write) {
                    System.out.println();
                    printStats("p2", "read", providenceResult.readStat, Color.GREEN);
                    printStats("t1", "read", thriftResult.readStat, Color.YELLOW);
                }
                if (options.op != Options.Operation.read) {
                    System.out.println();
                    printStats("p2", "write", providenceResult.writeStat, Color.GREEN);
                    printStats("t1", "write", thriftResult.writeStat, Color.YELLOW);
                }
                System.out.println();
            } else {
                System.out.format("[providence]%3d:%s\n",
                                  options.runs, providenceResult.toString());
                System.out.format("%s[thrift]    %3d:%s%s\n",
                                  Color.YELLOW,
                                  options.runs, thriftResult.toString(),
                                  Color.CLEAR);
            }
        } else if (fullStats) {
            System.out.println();
            if (options.op != Options.Operation.write) {
                printStats("p2", "read", providenceResult.readStat, Color.GREEN);
            }
            if (options.op != Options.Operation.read) {
                printStats("p2", "write", providenceResult.writeStat, Color.YELLOW);
            }
            System.out.println();
        } else {
            System.out.format("[providence]%3d:%s\n",
                              options.runs, providenceResult.toString());
        }
    }

    public static void printStats(String variant, String op, DescriptiveStatistics stats, Color color) {
        double[] values = stats.getSortedValues();
        DescriptiveStatistics norm = new DescriptiveStatistics();
        // Skip the top and bottom 0.1%.
        final int skip = values.length / 1000;
        for (int i = skip; i < values.length - skip; ++i) {
            norm.addValue(values[i]);
        }
        System.out.format(Locale.ENGLISH,
                          "%s -- [%s] %5s: [%7.2fµs /%7.2fµs /%7.2fµs /%8.2fµs /%8.2fµs] mean: %7.2fµs gmean: %7.2fµs dev: %7.2f  var: %7.2f^2%s\n",
                          color,
                          variant, op,
                          norm.getPercentile(1)   / 1000,
                          norm.getPercentile(10)  / 1000,
                          norm.getPercentile(50)  / 1000,
                          norm.getPercentile(90)  / 1000,
                          norm.getPercentile(99)  / 1000,
                          norm.getMean()          / 1000,
                          norm.getGeometricMean() / 1000,
                          norm.getStandardDeviation() / 1000,
                          Math.sqrt(norm.getPopulationVariance() / 1000),
                          Color.CLEAR);
    }

    public static void main(String[] args) {
        Options opts = new Options();
        CmdLineParser parser = new CmdLineParser(opts);

        try {
            parser.parseArgument(args);
            if (opts.runs < 1 && opts.format == null) {
                throw new CmdLineException("If --runs is less than 1, --format must be set.");
            }
            SpeedTest speedTest = new SpeedTest(opts, parser);

            speedTest.readTestData();

            System.out.println("OUT: " + speedTest.testDir.getAbsolutePath());
            System.out.println();

            if (opts.format != null) {
                speedTest.runTest(opts.format, true);
            } else {
                for (Format format : Format.values()) {
                    speedTest.runTest(format, false);
                }
            }
        } catch (TException|PSerializeException |IOException | CmdLineException e) {
            System.out.flush();
            System.err.println();
            e.printStackTrace();
            System.err.println();
            System.err.println(e.getMessage());
            System.err.println();
            parser.printSingleLineUsage(System.err);
            System.err.println();
            parser.printUsage(System.err);
            System.exit(-1);
        }
    }
}
