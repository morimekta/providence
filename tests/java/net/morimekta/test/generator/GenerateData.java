package net.morimekta.test.generator;

import net.morimekta.test.j2.Containers;
import net.morimekta.test.j2.Primitives;
import net.morimekta.test.j2.Value;

import org.apache.thrift.j2.TBinary;
import org.apache.thrift.j2.protocol.TBinaryProtocolSerializer;
import org.apache.thrift.j2.serializer.TBinarySerializer;
import org.apache.thrift.j2.serializer.TJsonSerializer;
import org.apache.thrift.j2.serializer.TSerializeException;
import org.apache.thrift.j2.serializer.TSerializer;
import org.apache.utils.FormatString;
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
public class GenerateData {
    public static final int KEY = 12;
    public static final int DATA = 102;

    public static class Options {
        @Option(name = "--entries",
                usage = "Number of entries to generate")
        public int entries = 10000;

        @Option(name = "--items_min",
                usage = "Numer of items in each collection")
        public int items_min = 10;

        @Option(name = "--items_max",
                usage = "Numer of items in each collection")
        public int items_max = 10;

        @Option(name = "--fill_grade",
                usage = "How much of the fields should be filled.")
        public double fill_grade = 1.0d;

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
        binary_protocol("bin"),
        ;

        String suffix;

        Format(String s) {
            suffix = s;
        }
    }

    private final Options opts;
    private final CmdLineParser parser;
    private final Random rand;

    public GenerateData(Options opts, CmdLineParser parser) {
        this.opts = opts;
        this.parser = parser;

        if (opts.seed != null) {
            this.rand = new Random(opts.seed);
        } else {
            this.rand = new Random(System.nanoTime());
        }
    }

    public int randomItemCount() {
        if (opts.items_max <= opts.items_min) {
            return opts.items_min;
        }
        return rand.nextInt(opts.items_max - opts.items_min) + opts.items_min;
    }

    public String nextString(final int size) {
        char[] out = new char[size];
        for (int i = 0; i < size; ++i) {
            int c = rand.nextInt(128);
            if (c == '\n' ||
                c == '\t' ||
                c == '\f' ||
                c == '\r' ||
                c == ' ' ||
                (c >= 32 && c < 127)) {
                out[i] = (char) c;
            } else {
                c = rand.nextInt(2048);
                if (c >= 160 && Character.isAlphabetic(c)) {
                    out[i] = (char) c;
                } else {
                    c = rand.nextInt(16536);
                    if (Character.isAlphabetic(c) ||
                        Character.isLetterOrDigit(c) ||
                        Character.isIdeographic(c) ||
                        Character.isSpaceChar(c) ||
                        Character.isJavaIdentifierPart(c)) {
                        out[i] = (char) c;
                    } else {
                        out[i] = '?';
                    }
                }
            }
        }
        return String.valueOf(out);
    }

    public TBinary nextBinary(int size) {
        byte[] out = new byte[size];
        rand.nextBytes(out);
        return TBinary.wrap(out);
    }

    public byte nextByte() {
        byte[] out = new byte[1];
        rand.nextBytes(out);
        return out[0];
    }

    public double nextDouble() {
        double v = rand.nextDouble();
        return (v * 2000000d) - 1000000d;
    }

    public long nextLong() {
        long out = rand.nextLong();
        return out ^ (long) rand.nextInt();
    }

    public boolean doFill() {
        if (opts.fill_grade >= 1.0) {
            return true;
        }
        if (opts.fill_grade < 0.01) {
            return false;
        }
        return opts.fill_grade < rand.nextDouble();
    }

    public Value nextValue() {
        return Value.values()[rand.nextInt(Value.values().length)];
    }

    public Primitives nextPrimitives() {
        Primitives._Builder builder = Primitives.builder();
        if (doFill()) {
            builder.setBooleanValue(rand.nextBoolean());
        }
        if (doFill()) {
            builder.setByteValue(nextByte());
        }
        if (doFill()) {
            builder.setShortValue((short) rand.nextInt());
        }
        if (doFill()) {
            builder.setIntegerValue(rand.nextInt());
        }
        if (doFill()) {
            builder.setLongValue(nextLong());
        }
        if (doFill()) {
            builder.setDoubleValue(nextDouble());
        }
        if (doFill()) {
            builder.setBinaryValue(nextBinary(DATA));
        }
        if (doFill()) {
            builder.setStringValue(nextString(DATA));
        }
        if (doFill()) {
            builder.setEnumValue(nextValue());
        }

        return builder.build();
    }

    public ArrayList<Containers> generateData() {
        ArrayList<Containers> data = new ArrayList<>(opts.entries);
        Random rand = new Random(System.nanoTime());
        for (int e = 0; e < opts.entries; ++e) {
            Containers._Builder containers = Containers.builder();

            // --- LISTS ---

            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToBooleanList(rand.nextBoolean());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToByteList(nextByte());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToShortList((short) rand.nextInt());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToIntegerList(rand.nextInt());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToLongList(nextLong());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToDoubleList(nextDouble());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToBinaryList(nextBinary(DATA));
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToStringList(nextString(DATA));
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToEnumList(nextValue());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToMessageList(nextPrimitives());
                }
            }

            // --- SETS ---

            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToBooleanSet(rand.nextBoolean());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToByteSet(nextByte());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToShortSet((short) rand.nextInt());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToIntegerSet(rand.nextInt());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToLongSet(nextLong());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToDoubleSet(nextDouble());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToBinarySet(nextBinary(DATA));
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToStringSet(nextString(DATA));
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToEnumSet(nextValue());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToMessageSet(nextPrimitives());
                }
            }

            // --- MAPS ---

            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToBooleanMap(rand.nextBoolean(), rand.nextBoolean());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToByteMap(nextByte(), nextByte());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToShortMap((short) rand.nextInt(), (short) rand.nextInt());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToIntegerMap(rand.nextInt(), rand.nextInt());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToLongMap(nextLong(), nextLong());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToDoubleMap(nextDouble(), nextDouble());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToBinaryMap(nextBinary(KEY), nextBinary(DATA));
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToStringMap(nextString(KEY), nextString(DATA));
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToEnumMap(nextValue(), nextValue());
                }
            }
            if (doFill()) {
                final int items = randomItemCount();
                for (int i = 0; i < items; ++i) {
                    containers.addToMessageMap(nextString(KEY), nextPrimitives());
                }
            }

            data.add(containers.build());
        }

        return data;
    }

    public void run() throws CmdLineException, IOException, TSerializeException {
        File outDir = new File(opts.out);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        if (!outDir.isDirectory()) {
            throw new CmdLineException(parser, new FormatString("Output is not a directory: %s"), opts.out);
        }

        ArrayList<Containers> data = generateData();

        for (Format f : Format.values()) {
            File outFile = new File(outDir, String.format("%s.%s", f.name(), f.suffix));
            if (outFile.exists()) {
                outFile.delete();
            }
            outFile.createNewFile();

            TSerializer serializer;

            switch (f) {
                case binary:
                    serializer = new TBinarySerializer();
                    break;
                case json:
                    serializer = new TJsonSerializer(TJsonSerializer.IdType.ID);
                    break;
                case json_pretty:
                    serializer = new TJsonSerializer(false, TJsonSerializer.IdType.NAME, TJsonSerializer.IdType.NAME, true);
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

            System.out.format(Locale.ENGLISH, "%20s:  %,7d kB in%6.2fs\n",
                              f.name(), size / 1024, (double) timeMs / 1000);
        }
    }

    public static void main(String[] args) {
        Options opts = new Options();
        CmdLineParser parser = new CmdLineParser(opts);

        try {
            parser.parseArgument(args);
            GenerateData cmd = new GenerateData(opts, parser);
            cmd.run();
        } catch (TSerializeException|IOException|CmdLineException e) {
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
