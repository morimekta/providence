package net.morimekta.test.generator;

import net.morimekta.test.j2.Containers;
import net.morimekta.test.j2.Primitives;
import net.morimekta.test.j2.Value;

import org.apache.thrift.j2.protocol.TBinaryProtocolSerializer;
import org.apache.thrift.j2.protocol.TCompactProtocolSerializer;
import org.apache.thrift.j2.protocol.TJsonProtocolSerializer;
import org.apache.thrift.j2.protocol.TTupleProtocolSerializer;
import org.apache.thrift.j2.serializer.TBinarySerializer;
import org.apache.thrift.j2.serializer.TJsonSerializer;
import org.apache.thrift.j2.serializer.TSerializeException;
import org.apache.thrift.j2.serializer.TSerializer;
import org.apache.thrift.j2.TBinary;
import org.apache.thrift.j2.util.TBase64Utils;
import org.apache.utils.FormatString;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Generate test data for speed test.
 */
public class GenerateData {
    public static final int ONE = 1;
    public static final int KEY = 12;
    public static final int DATA = 102;

    public static class Options {
        @Option(name = "--entries",
                usage = "Number of entries to generate")
        public int entries = 10000;

        @Option(name = "--items",
                usage = "Numer of items in each collection")
        public int items = 10;

        @Option(name = "--out",
                usage = "output directory of data",
                required = true)
        public String out;
    }

    public enum Format {
        binary("bin"),
        binary_protocol("bin"),
        compact_protocol("bin"),
        json("json"),
        json_named("json"),
        json_protocol("json"),
        tuple_protocol("tuples");

        String suffix;

        Format(String s) {
            suffix = s;
        }
    }

    public static void main(String[] args) {
        Options opts = new Options();
        CmdLineParser parser = new CmdLineParser(opts);

        try {
            parser.parseArgument(args);

            File out = new File(opts.out);
            if (!out.exists()) {
                out.mkdirs();
            }
            if (!out.isDirectory()) {
                throw new CmdLineException(parser, new FormatString("Output is not a directory: %s"), opts.out);
            }

            Value[] values = Value.values();

            ArrayList<Containers> data = new ArrayList<>(opts.entries);
            Random rand = new Random(System.nanoTime());
            for (int e = 0; e < opts.entries; ++e) {
                Containers._Builder containers = Containers.builder();

                for (int i = 0; i < opts.items; ++i) {
                    // LISTS:
                    byte[] one = new byte[ONE];
                    byte[] bytes = new byte[DATA];
                    byte[] str = new byte[DATA];

                    rand.nextBytes(one);
                    rand.nextBytes(bytes);
                    rand.nextBytes(str);

                    containers.addToBooleanList(rand.nextBoolean());
                    containers.addToByteList(one[0]);
                    containers.addToShortList((short) rand.nextInt());
                    containers.addToIntegerList(rand.nextInt());
                    containers.addToLongList(rand.nextLong());
                    containers.addToDoubleList(rand.nextDouble());
                    containers.addToBinaryList(TBinary.wrap(bytes));
                    containers.addToStringList(TBase64Utils.encode(str));
                    containers.addToEnumList(values[rand.nextInt(values.length)]);

                    one = new byte[ONE];
                    bytes = new byte[DATA];
                    str = new byte[DATA];

                    rand.nextBytes(one);
                    rand.nextBytes(bytes);
                    rand.nextBytes(str);

                    containers.addToMessageList(Primitives
                            .builder()
                            .setBooleanValue(rand.nextBoolean())
                            .setByteValue(one[0])
                            .setShortValue((short) rand.nextInt())
                            .setIntegerValue(rand.nextInt())
                            .setLongValue(rand.nextLong())
                            .setDoubleValue(rand.nextDouble())
                            .setBinaryValue(TBinary.wrap(bytes))
                            .setStringValue(TBase64Utils.encode(str))
                            .setEnumValue(values[rand.nextInt(values.length)])
                            .build());

                    // SETS:

                    one = new byte[ONE];
                    bytes = new byte[DATA];
                    str = new byte[DATA];

                    rand.nextBytes(one);
                    rand.nextBytes(bytes);
                    rand.nextBytes(str);

                    containers.addToBooleanSet(rand.nextBoolean());
                    containers.addToByteSet(one[0]);
                    containers.addToShortSet((short) rand.nextInt());
                    containers.addToIntegerSet(rand.nextInt());
                    containers.addToLongSet(rand.nextLong());
                    containers.addToDoubleSet(rand.nextDouble());
                    containers.addToBinarySet(TBinary.wrap(bytes));
                    containers.addToStringSet(TBase64Utils.encode(str));
                    containers.addToEnumSet(values[rand.nextInt(values.length)]);

                    one = new byte[ONE];
                    bytes = new byte[DATA];
                    str = new byte[DATA];

                    rand.nextBytes(one);
                    rand.nextBytes(bytes);
                    rand.nextBytes(str);

                    containers.addToMessageSet(Primitives
                            .builder()
                            .setBooleanValue(rand.nextBoolean())
                            .setByteValue(one[0])
                            .setShortValue((short) rand.nextInt())
                            .setIntegerValue(rand.nextInt())
                            .setLongValue(rand.nextLong())
                            .setDoubleValue(rand.nextDouble())
                            .setBinaryValue(TBinary.wrap(bytes))
                            .setStringValue(TBase64Utils.encode(str))
                            .setEnumValue(values[rand.nextInt(values.length)])
                            .build());

                    // MAPS:

                    one = new byte[ONE];
                    bytes = new byte[DATA];
                    str = new byte[DATA];
                    byte[] kone = new byte[ONE];
                    byte[] kbytes = new byte[KEY];
                    byte[] kstr = new byte[KEY];

                    rand.nextBytes(one);
                    rand.nextBytes(kone);
                    rand.nextBytes(bytes);
                    rand.nextBytes(kbytes);
                    rand.nextBytes(str);
                    rand.nextBytes(kstr);

                    containers.addToBooleanMap(rand.nextBoolean(), rand.nextBoolean());
                    containers.addToByteMap(kone[0], one[0]);
                    containers.addToShortMap((short) rand.nextInt(), (short) rand.nextInt());
                    containers.addToIntegerMap(rand.nextInt(), rand.nextInt());
                    containers.addToLongMap(rand.nextLong(), rand.nextLong());
                    containers.addToDoubleMap(rand.nextDouble(), rand.nextDouble());
                    containers.addToBinaryMap(TBinary.wrap(kbytes), TBinary.wrap(bytes));
                    containers.addToStringMap(TBase64Utils.encode(kstr), TBase64Utils.encode(str));
                    containers.addToEnumMap(values[rand.nextInt(values.length)], values[rand.nextInt(values.length)]);

                    one = new byte[ONE];
                    bytes = new byte[DATA];
                    str = new byte[DATA];
                    kstr = new byte[KEY];

                    rand.nextBytes(one);
                    rand.nextBytes(bytes);
                    rand.nextBytes(str);
                    rand.nextBytes(kstr);

                    containers.addToMessageMap(
                            TBase64Utils.encode(kstr),
                            Primitives
                                    .builder()
                                    .setBooleanValue(rand.nextBoolean())
                                    .setByteValue(one[0])
                                    .setShortValue((short) rand.nextInt())
                                    .setIntegerValue(rand.nextInt())
                                    .setLongValue(rand.nextLong())
                                    .setDoubleValue(rand.nextDouble())
                                    .setBinaryValue(TBinary.wrap(bytes))
                                    .setStringValue(TBase64Utils.encode(str))
                                    .setEnumValue(values[rand.nextInt(values.length)])
                                    .build());
                }

                data.add(containers.build());
            }

            for (Format f : Format.values()) {
                File dir = new File(out, f.name());
                if (!dir.exists()) {
                    dir.mkdir();
                }
                if (!dir.isDirectory()) {
                    throw new CmdLineException(parser, new FormatString("Target is not a directory: %s"), dir.getAbsolutePath());
                }
                File file = new File(dir, String.format("data.%s", f.suffix));
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();

                TSerializer serializer;

                switch (f) {
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
                    case json_protocol:
                        serializer = new TJsonProtocolSerializer();
                        break;
                    case tuple_protocol:
                        serializer = new TTupleProtocolSerializer();
                        break;
                    default:
                        continue;
                }

                long start = System.currentTimeMillis();
                int size = 0;
                BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file, false));
                for (Containers c : data) {
                    size += serializer.serialize(os, c);
                    os.write('\n');
                }
                os.flush();
                os.close();
                long end = System.currentTimeMillis();

                System.out.format("%20s:%,9d kB in %5.2fs\n",
                        f.name(), size / 1024, (double) (end - start) / 1000);
            }
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
