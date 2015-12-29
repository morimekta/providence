package net.morimekta.test.speedtest;

import net.morimekta.test.j2.Containers;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.thrift.TException;
import org.apache.thrift.j2.protocol.TBinaryProtocolSerializer;
import org.apache.thrift.j2.protocol.TCompactProtocolSerializer;
import org.apache.thrift.j2.protocol.TJsonProtocolSerializer;
import org.apache.thrift.j2.protocol.TTupleProtocolSerializer;
import org.apache.thrift.j2.serializer.TBinarySerializer;
import org.apache.thrift.j2.serializer.TJsonSerializer;
import org.apache.thrift.j2.serializer.TSerializeException;
import org.apache.thrift.j2.serializer.TSerializer;
import org.apache.thrift.j2.util.io.CountingOutputStream;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.utils.Color;
import org.apache.utils.FormatString;
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
                usage = "Number of runs per file.")
        public int runs = 1;

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

    public enum Format {          //  TJ2    thrift
        json_pretty("json"),      // 13.65
        json_named("json"),       // 12.41
        json("json"),             // 11.52

        json_protocol("json"),    //  8.86   10.86

        binary("bin"),            //  2.37

        compact_protocol("bin"),  //  2.44    2.71
        binary_protocol("bin"),   //  2.05    2.43
        tuple_protocol("tuples"), //  2.04    1.97
        ;

        String suffix;

        Format(String s) {
            suffix = s;
        }
    }

    private final Options                                  options;
    private final CmdLineParser                            parser;
    private final ArrayList<net.morimekta.test.Containers> thriftContainers;
    private final ArrayList<Containers>                    thriftJ2Containers;
    private final File                                     dataDir;
    private final File                                     testDir;

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

        testDir = File.createTempFile("thrift-j2-", "-speed-test");
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
                net.morimekta.test.Containers containers = new net.morimekta.test.Containers();
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

        System.out.format(" -- Read %d    [thrift] entries in %5.2fs.\n",
                          thriftContainers.size(), (double) ms / 1000);

        thriftJ2Containers.clear();

        in = getInput(Format.binary);
        TSerializer serializer = new TBinarySerializer();

        start = System.nanoTime();

        for (int i = 0; i < options.entries; ++i) {
            try {
                Containers containers = serializer.deserialize(in, Containers.kDescriptor);
                thriftJ2Containers.add(containers);
                // Check if we have another entry. There should be a separating '\n' char.
                if (in.read() == -1) {
                    break;
                }
            } catch (TSerializeException se) {
                se.printStackTrace();
                break;
            }
        }

        end = System.nanoTime();
        ms = (end - start) / 1000000;

        System.out.format(" -- Read %d [thrift-j2] entries in %5.2fs.\n",
                          thriftContainers.size(), (double) ms / 1000);
        System.out.println();
    }

    public CountingOutputStream getTestOutput(Format format, String variant, int run) throws IOException {
        File outFile = new File(testDir, String.format("%s-%s-%03d.%s", format.name(), variant, run, format.suffix));
        if (options.cleanup) {
            outFile.deleteOnExit();
        }
        outFile.createNewFile();

        return new CountingOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(outFile, false)));
    }

    public InputStream getTestInput(Format format, String variant, int run) throws IOException {
        File inFile = new File(testDir, String.format("%s-%s-%03d.%s", format.name(), variant, run, format.suffix));
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

        // Write test.
        CountingOutputStream out = getTestOutput(result.format, "v1", run);
        TProtocol protocol = factory.getProtocol(new TIOStreamTransport(out));

        long writeRunTime = 0;

        final int num = thriftContainers.size();
        for (int i = 0; i < num; ++i) {
            net.morimekta.test.Containers containers = thriftContainers.get(i);

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
        }

        out.close();

        result.writeRunStat.addValue(writeRunTime);

        InputStream in = getTestInput(result.format, "v1", run);
        protocol = factory.getProtocol(new TIOStreamTransport(in));

        long readRunTime = 0;

        for (int i = 0; i < num; ++i) {
            boolean stop = false;
            net.morimekta.test.Containers orig = thriftContainers.get(i);
            net.morimekta.test.Containers read = new net.morimekta.test.Containers();

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

        long testEnd = System.nanoTime();
        return (testEnd - testStart) / 1000000;
    }

    public long runThriftJ2Test(Result result, int run) throws IOException, TSerializeException {
        TSerializer serializer;
        switch (result.format) {
            case binary:
                serializer = new TBinarySerializer();
                break;
            case binary_protocol:
                serializer = new TBinaryProtocolSerializer();
                break;
            case compact_protocol:
                serializer = new TCompactProtocolSerializer();
                break;
            case json:
                serializer = new TJsonSerializer(TJsonSerializer.IdType.ID);
                break;
            case json_named:
                serializer = new TJsonSerializer(TJsonSerializer.IdType.NAME);
                break;
            case json_pretty:
                serializer = new TJsonSerializer(false, TJsonSerializer.IdType.NAME, TJsonSerializer.IdType.NAME, true);
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

        // Write test.
        CountingOutputStream out = getTestOutput(result.format, "j2", run);

        long writeRunTime = 0;
        final int num = thriftContainers.size();
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

        // Read test
        InputStream in = getTestInput(result.format, "j2", run);

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

        long testEnd = System.nanoTime();
        return (testEnd - testStart) / 1000000;
    }

    public void runTest(Format format, boolean fullStats) throws IOException, TException, TSerializeException {
        // First test thrift-j2.
        Result thriftJ2Result = new Result(format);
        Result thriftResult = new Result(format);

        for (int run = 1; run <= options.runs; ++run) {
            runThriftJ2Test(thriftJ2Result, run);
            if (fullStats) {
                System.out.format("[thrift-j2] %2d:%s\n", run, thriftJ2Result.toString());
            }
            if (format.name().endsWith("_protocol")) {
                runThriftTest(thriftResult, run);
                if (fullStats) {
                    System.out.format("%s[thrift]    %2d:%s%s\n",
                                      Color.YELLOW,
                                      run, thriftResult.toString(),
                                      Color.CLEAR);
                }
            }
        }

        if (format.name().endsWith("_protocol")) {
            if (fullStats) {
                System.out.println();
                printStats("j2", "read", thriftJ2Result.readStat, Color.DEFAULT);
                printStats("v1", "read", thriftResult.readStat, Color.YELLOW);
                System.out.println();
                printStats("j2", "write", thriftJ2Result.writeStat, Color.DEFAULT);
                printStats("v1", "write", thriftResult.writeStat, Color.YELLOW);
                System.out.println();
            } else {
                System.out.format("[thrift-j2] %2d:%s\n",
                                  options.runs, thriftJ2Result.toString());
                System.out.format("%s[thrift]    %2d:%s%s\n",
                                  Color.YELLOW,
                                  options.runs, thriftResult.toString(),
                                  Color.CLEAR);
            }
        } else if (fullStats) {
            System.out.println();
            printStats("j2", "read", thriftJ2Result.readStat, Color.DEFAULT);
            printStats("j2", "write", thriftJ2Result.writeStat, Color.DEFAULT);
            System.out.println();
        } else {
            System.out.format("[thrift-j2] %2d:%s\n",
                              options.runs, thriftJ2Result.toString());
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
                          "%s -- [%s] %5s: [%6.2fµs /%6.2fµs /%6.2fµs /%6.2fµs /%6.2fµs] mean: %6.2fµs dev: %,9.2f%s\n",
                          color,
                          variant, op,
                          norm.getPercentile(1)  / 1000,
                          norm.getPercentile(10) / 1000,
                          norm.getPercentile(50) / 1000,
                          norm.getPercentile(90) / 1000,
                          norm.getPercentile(99) / 1000,
                          norm.getMean()         / 1000,
                          norm.getStandardDeviation() / 1000,
                          Color.CLEAR);
    }

    public static void main(String[] args) {
        Options opts = new Options();
        CmdLineParser parser = new CmdLineParser(opts);

        try {
            parser.parseArgument(args);
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
        } catch (TException|TSerializeException|IOException | CmdLineException e) {
            System.out.flush();
            System.err.println();
            e.printStackTrace();
            System.err.println();
            parser.printSingleLineUsage(System.err);
            System.err.println();
            parser.printUsage(System.err);
            System.exit(-1);
        }
    }
}
