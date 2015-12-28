package net.morimekta.test.speedtest;

import net.morimekta.test.j2.Containers;

import org.apache.thrift.TException;
import org.apache.thrift.j2.TMessage;
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
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
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
import java.util.Locale;

/**
 * Created by steineldar on 22.12.15.
 */
public class SpeedTest {
    public static class Options {
        @Option(name = "--entries",
                usage = "Expected number of entries to in input files")
        public int entries = 50000;

        @Argument(usage = "Base input directory of data",
                  metaVar = "DIR",
                  required = true)
        public String in;
    }

    public enum Format {          //   J2    thrift
        json_named("json"),       // 21.88
        json("json"),             // 18.72

        json_protocol("json"),    // 10.64   10.92

        binary("bin"),            //  2.32

        compact_protocol("bin"),  //  2.13    2.43
        tuple_protocol("tuples"), //  1.92    2.02
        binary_protocol("bin"),   //  1.82    1.99
        ;

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

            File inDir = new File(opts.in);
            if (!inDir.exists()) {
                throw new CmdLineException(parser, new FormatString("Input folder does not exist: %s"), opts.in);
            }
            if (!inDir.isDirectory()) {
                throw new CmdLineException(parser, new FormatString("Output is not a directory: %s"), opts.in);
            }

            File outDir = File.createTempFile("thrift-j2-", "-speed-test");
            if (outDir.exists()) {
                outDir.delete();
            }
            outDir.mkdirs();

            System.out.println("OUT: " + outDir.getAbsolutePath());

            System.out.println();
            System.out.println(" --- thrift ---");
            System.out.println();

            for (Format f : Format.values()) {
                File inFile = new File(inDir, String.format("%s-j2.%s", f.name(), f.suffix));
                File outFile = new File(outDir, String.format("%s-j1.%s", f.name(), f.suffix));
                if (outFile.exists()) {
                    outFile.delete();
                }
                outFile.createNewFile();

                BufferedInputStream inStream = new BufferedInputStream(
                        new FileInputStream(inFile));
                CountingOutputStream outStream = new CountingOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(outFile, false)));

                TProtocolFactory in_prot;
                TProtocolFactory out_prot;

                switch (f) {
                    case binary_protocol:
                        in_prot = new TBinaryProtocol.Factory();
                        out_prot = new TBinaryProtocol.Factory();
                        break;
                    case compact_protocol:
                        in_prot = new TCompactProtocol.Factory();
                        out_prot = new TCompactProtocol.Factory();
                        break;
                    case json_protocol:
                        in_prot = new TJSONProtocol.Factory();
                        out_prot = new TJSONProtocol.Factory();
                        break;
                    case tuple_protocol:
                        in_prot = new TTupleProtocol.Factory();
                        out_prot = new TTupleProtocol.Factory();
                        break;
                    default:
                        System.out.format("%20s: [skipped]\n", f.name());
                        continue;
                }

                long in_size = inFile.length();

                long start = System.currentTimeMillis();

                net.morimekta.test.Containers containers;
                int num = 0;

                long rtime = 0;
                long wtime = 0;

                while (num < opts.entries) {
                    ++num;

                    long rstart = System.nanoTime();

                    TTransport tin = new TIOStreamTransport(inStream);
                    containers = new net.morimekta.test.Containers();
                    containers.read(in_prot.getProtocol(tin));
                    inStream.read();  // the separating newline.

                    long rend = System.nanoTime();

                    TTransport tout = new TIOStreamTransport(outStream);
                    containers.write(out_prot.getProtocol(tout));
                    tout.flush();
                    outStream.write('\n');
                    outStream.flush();

                    long wend = System.nanoTime();

                    rtime += (rend - rstart);
                    wtime += (wend - rend);
                }

                inStream.close();

                int out_size = outStream.getByteCount();

                outStream.close();

                long end = System.currentTimeMillis();

                wtime /= 1000000;
                rtime /= 1000000;

                System.out.format(Locale.ENGLISH,
                                  "%20s: %5.2fs  (r: %5.2fs, w: %5.2fs)  #  %,7d kB -> %,7d kB\n",
                                  f.name(),
                                  (double) (end - start) / 1000,
                                  (double) rtime / 1000,
                                  (double) wtime / 1000,
                                  in_size / 1024, out_size / 1024);
            }

            System.out.println();
            System.out.println(" --- thrift-j2 ---");
            System.out.println();

            for (Format fmt : Format.values()) {
                File in_dir = new File(inDir, fmt.name());
                if (!in_dir.exists()) {
                    throw new CmdLineException(parser,
                                               new FormatString("Target is not a directory: %s"),
                                               in_dir.getAbsolutePath());
                }
                if (!in_dir.isDirectory()) {
                    throw new CmdLineException(parser,
                                               new FormatString("Target is not a directory: %s"),
                                               in_dir.getAbsolutePath());
                }

                File out_dir = new File(outDir, fmt.name());
                out_dir.mkdir();

                File in_file = new File(in_dir, String.format("data.%s", fmt.suffix));

                File out_file = new File(out_dir, String.format("data-j2.%s", fmt.suffix));
                out_file.createNewFile();

                TSerializer serializer;

                BufferedInputStream inStream = new BufferedInputStream(
                        new FileInputStream(in_file));
                CountingOutputStream outStream = new CountingOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(out_file, false)));

                switch (fmt) {
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
                        System.out.format("%20s: [skipped]\n", fmt.name());
                        continue;
                }

                final long in_size = in_file.length();
                final long start = System.currentTimeMillis();

                int num;

                long rtime = 0;
                long wtime = 0;

                for (num = 0; num < opts.entries; ++num) {
                    try {
                        long rstart = System.nanoTime();
                        TMessage<?> message = serializer.deserialize(inStream, Containers.kDescriptor);
                        inStream.read();  // the separating newline.

                        long rend = System.nanoTime();

                        serializer.serialize(outStream, message);
                        outStream.write('\n');
                        outStream.flush();

                        long wend = System.nanoTime();

                        rtime += (rend - rstart);
                        wtime += (wend - rend);
                    } catch (TSerializeException e) {
                        throw new TSerializeException(e, "Error in message " + num);
                    }
                }

                final long out_size = outStream.getByteCount();
                final long end = System.currentTimeMillis();

                outStream.close();

                wtime /= 1000000;
                rtime /= 1000000;

                System.out.format(Locale.ENGLISH,
                                  "%20s: %5.2fs  (r: %5.2fs, w: %5.2fs)  #  %,7d kB -> %,7d kB\n",
                                  fmt.name(),
                                  (double) (end - start) / 1000,
                                  (double) rtime / 1000,
                                  (double) wtime / 1000,
                                  in_size / 1024, out_size / 1024);
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
