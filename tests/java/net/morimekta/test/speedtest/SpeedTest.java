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
 * Speed test running through:
 *  - Read a file for each protocol / serialization format.
 *  - Write the same file back to a temp file.
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

            // Do a warmup read to make sure the disk is spinning.
            Format fmt = Format.json_pretty;
            File inFile = new File(inDir, String.format("%s.%s", fmt.name(), fmt.suffix));
            File outFile;
            BufferedInputStream inStream = new BufferedInputStream(
                    new FileInputStream(inFile));
            CountingOutputStream outStream;

            System.out.print("Warmup: -");
            byte[] buffer = new byte[65536];
            int i = 0;

            long start = System.nanoTime();

            int inBytes = 0;
            int r;
            while ((r = inStream.read(buffer)) >= 0) {
                inBytes += r;
                System.out.print('\b');
                i = (i + 1) % 4;
                switch (i) {
                    case 0:
                        System.out.print('-'); break;
                    case 1:
                        System.out.print('\\'); break;
                    case 2:
                        System.out.print('|'); break;
                    case 3:
                        System.out.print('/'); break;
                }
            }

            long end = System.nanoTime();
            long time = (end - start) / 1000000;

            System.out.format(" %,9d kB in %5.2fs", inBytes / 1024, (double) time / 1000);

            System.out.println();
            System.out.println();
            System.out.println(" --- thrift ---");
            System.out.println();

            for (Format format : Format.values()) {
                inFile = new File(inDir, String.format("%s.%s", format.name(), format.suffix));
                outFile = new File(outDir, String.format("%s-j1.%s", format.name(), format.suffix));
                if (outFile.exists()) {
                    outFile.delete();
                }
                outFile.createNewFile();

                inStream = new BufferedInputStream(
                        new FileInputStream(inFile));
                outStream = new CountingOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(outFile, false)));

                TProtocolFactory inProt;
                TProtocolFactory outProt;

                switch (format) {
                    case binary_protocol:
                        inProt = new TBinaryProtocol.Factory();
                        outProt = new TBinaryProtocol.Factory();
                        break;
                    case compact_protocol:
                        inProt = new TCompactProtocol.Factory();
                        outProt = new TCompactProtocol.Factory();
                        break;
                    case json_protocol:
                        inProt = new TJSONProtocol.Factory();
                        outProt = new TJSONProtocol.Factory();
                        break;
                    case tuple_protocol:
                        inProt = new TTupleProtocol.Factory();
                        outProt = new TTupleProtocol.Factory();
                        break;
                    default:
                        System.out.format("%20s: [skipped]\n", format.name());
                        continue;
                }

                inBytes = (int) inFile.length();

                start = System.currentTimeMillis();

                net.morimekta.test.Containers containers;
                int num = 0;

                long rtime = 0;
                long wtime = 0;

                while (num < opts.entries) {
                    ++num;

                    long rstart = System.nanoTime();

                    TTransport tin = new TIOStreamTransport(inStream);
                    containers = new net.morimekta.test.Containers();
                    containers.read(inProt.getProtocol(tin));
                    inStream.read();  // the separating newline.

                    long rend = System.nanoTime();

                    TTransport tout = new TIOStreamTransport(outStream);
                    containers.write(outProt.getProtocol(tout));
                    tout.flush();
                    outStream.write('\n');
                    outStream.flush();

                    long wend = System.nanoTime();

                    rtime += (rend - rstart);
                    wtime += (wend - rend);
                }

                inStream.close();

                int outBytes = outStream.getByteCount();

                outStream.close();

                end = System.currentTimeMillis();

                wtime /= 1000000;
                rtime /= 1000000;

                System.out.format(Locale.ENGLISH,
                                  "%20s: %5.2fs  (r: %5.2fs, w: %5.2fs)  #  %,7d kB -> %,7d kB\n",
                                  format.name(),
                                  (double) (end - start) / 1000,
                                  (double) rtime / 1000,
                                  (double) wtime / 1000,
                                  inBytes / 1024, outBytes / 1024);
            }

            System.out.println();
            System.out.println(" --- thrift-j2 ---");
            System.out.println();

            for (Format format : Format.values()) {
                inFile = new File(inDir, String.format("%s.%s", format.name(), format.suffix));
                outFile = new File(outDir, String.format("%s-j2.%s", format.name(), format.suffix));
                if (outFile.exists()) {
                    outFile.delete();
                }
                outFile.createNewFile();

                TSerializer serializer;

                inStream = new BufferedInputStream(
                        new FileInputStream(inFile));
                outStream = new CountingOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(outFile, false)));

                switch (format) {
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
                        System.out.format("%20s: [skipped]\n", format.name());
                        continue;
                }

                inBytes = (int) inFile.length();
                start = System.currentTimeMillis();

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

                final long outBytes = outStream.getByteCount();
                end = System.currentTimeMillis();

                outStream.close();

                wtime /= 1000000;
                rtime /= 1000000;

                System.out.format(Locale.ENGLISH,
                                  "%20s: %5.2fs  (r: %5.2fs, w: %5.2fs)  #  %,7d kB -> %,7d kB\n",
                                  format.name(),
                                  (double) (end - start) / 1000,
                                  (double) rtime / 1000,
                                  (double) wtime / 1000,
                                  inBytes / 1024, outBytes / 1024);
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
