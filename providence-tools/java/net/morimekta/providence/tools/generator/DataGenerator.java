package net.morimekta.providence.tools.generator;

import net.morimekta.console.FormatString;
import net.morimekta.providence.serializer.PBinarySerializer;
import net.morimekta.providence.serializer.PJsonSerializer;
import net.morimekta.providence.serializer.PSerializeException;
import net.morimekta.providence.serializer.PSerializer;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.tools.data.MessageGenerator;
import net.morimekta.providence.tools.data.RandomGenerator;
import net.morimekta.test.providence.Containers;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * Generate test data for speed test.
 */
public class DataGenerator {
    public static class Options {
        @Option(name = "--entries",
                usage = "Number of entries to generate")
        public int entries = 10000;

        @Option(name = "--seed",
                usage = "Set the random seed for testing.")
        public Long seed = null;

        @Option(name = "--out",
                usage = "output directory of data",
                required = true)
        public String out;
    }

    public enum Format {
        json_pretty("json"),
        json("json"),
        binary("bin"),
        binary_protocol("bin"),;

        String suffix;

        Format(String s) {
            suffix = s;
        }
    }

    private final Options       opts;
    private final CmdLineParser parser;
    private final Random        rand;

    public DataGenerator(Options opts, CmdLineParser parser) {
        this.opts = opts;
        this.parser = parser;

        if (opts.seed != null) {
            this.rand = new Random(opts.seed);
        } else {
            this.rand = new Random(System.nanoTime());
        }
    }

    public void run() throws CmdLineException, IOException, PSerializeException {
        File outDir = new File(opts.out);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        if (!outDir.isDirectory()) {
            throw new CmdLineException(parser, new FormatString("Output is not a directory: %s"), opts.out);
        }

        MessageGenerator factory = new MessageGenerator(new RandomGenerator(new Random()));

        ArrayList<Containers> data = new ArrayList<>();
        for (int i = 0; i < opts.entries; ++i) {
            data.add(factory.nextContainers());
        }

        for (Format f : Format.values()) {
            File outFile = new File(outDir, String.format("%s.%s", f.name(), f.suffix));
            if (outFile.exists()) {
                outFile.delete();
            }
            outFile.createNewFile();

            PSerializer serializer;

            switch (f) {
                case binary:
                    serializer = new PBinarySerializer();
                    break;
                case json:
                    serializer = new PJsonSerializer(PJsonSerializer.IdType.ID);
                    break;
                case json_pretty:
                    serializer = new PJsonSerializer(false,
                                                     PJsonSerializer.IdType.NAME,
                                                     PJsonSerializer.IdType.NAME,
                                                     true);
                    break;
                case binary_protocol:
                    serializer = new TBinaryProtocolSerializer();
                    break;
                default:
                    continue;
            }

            int size = 0;

            long start = System.nanoTime();

            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(outFile, false));

            for (Containers c : data) {
                size += serializer.serialize(outStream, c);
                outStream.write('\n');
                outStream.flush();
            }
            outStream.close();

            long end = System.nanoTime();

            long timeMs = (end - start) / 1000000;

            System.out.format(Locale.ENGLISH,
                              "%20s:  %,7d kB in%6.2fs\n",
                              f.name(),
                              size / 1024,
                              (double) timeMs / 1000);
        }
    }

    public static void main(String[] args) {
        Options opts = new Options();
        CmdLineParser parser = new CmdLineParser(opts);

        try {
            parser.parseArgument(args);
            DataGenerator cmd = new DataGenerator(opts, parser);
            cmd.run();
        } catch (PSerializeException | IOException | CmdLineException e) {
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
