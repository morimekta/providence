package net.morimekta.providence.it.serialization;

import net.morimekta.console.chr.Control;
import net.morimekta.providence.it.Format;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.test.providence.Containers;

import com.google.common.collect.ImmutableList;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.fail;

/**
 * Speed test running through:
 * - Read a file for each factory / serialization format.
 * - Write the same file back to a temp file.
 */
public class SerializationIT {
    private static final char[] SWIRL = {'|', '/', '-', '\\'};

    private static List<Containers> providence;
    private static List<net.morimekta.test.thrift.Containers> thrift;

    @BeforeClass
    public static void prepareData() throws IOException, TException {
        ImmutableList.Builder<Containers> providenceB = ImmutableList.builder();
        ImmutableList.Builder<net.morimekta.test.thrift.Containers> thriftB = ImmutableList.builder();

        MessageStreams.resource(
                "/containers.bin",
                new BinarySerializer(true),
                Containers.kDescriptor).forEachOrdered(providenceB::add);

        try (InputStream in = SerializationIT.class.getResourceAsStream("/containers.bin")) {
            if (in == null) {
                fail("Missing test data");
            }
            TTransport transport = new TIOStreamTransport(in);
            TProtocol protocol = new TBinaryProtocol(transport, true, true);

            while (in.available() > 0) {
                net.morimekta.test.thrift.Containers containers = new net.morimekta.test.thrift.Containers();
                containers.read(protocol);
                thriftB.add(containers);
            }
        }

        providence = providenceB.build();
        thrift = thriftB.build();
    }

    @Test
    public void testEachFormat() throws IOException, SerializerException, TException {
        System.out.println("Test testEachFormat");
        System.out.println(Format.header());
        System.out.println();

        for (Format format : Format.values()) {
            TestSerialization test = TestSerialization.forFormat(format);

            // Just run enough tests that we have >1ms per format.
            for (int i = 0; i < 10; ++i) {
                test.runProvidence(providence);
                test.runThrift(thrift);
            }
            test.calculate();

            System.out.println(test.asString());
        }
        System.out.println();
    }

    @Test
    public void testManyRuns() throws IOException, SerializerException, TException {
        LinkedList<TestSerialization> formats = new LinkedList<>();
        for (Format format : Format.values()) {
            formats.add(TestSerialization.forFormat(format));
        }

        System.out.println("Test testManyRuns");
        System.out.format(". ( 0%%)%s", Control.cursorLeft(6));
        for (int i = 0; i < 100; ++i) {
            Collections.shuffle(formats);
            for (TestSerialization test : formats) {
                for (int j = 0; j < 40; ++j) {
                    if (j % 10 == 0) {
                        System.out.print(Control.LEFT);
                        System.out.print(SWIRL[(j / 10) % 4]);
                        System.out.flush();
                    }
                    test.runProvidence(providence);
                    test.runThrift(thrift);
                }
                System.gc();
            }
            System.out.format("%s.. (%2d%%)%s", Control.LEFT, i + 1, Control.cursorLeft(6));
        }

        Optional<TestSerialization> opt = formats.stream().filter(f -> f.format == Format.binary).findFirst();
        if (!opt.isPresent()) {
            fail("Oops");
            return;
        }
        TestSerialization rel = opt.get();

        System.out.println();

        formats.forEach(TestSerialization::calculate);
        Collections.sort(formats);

        System.out.println(Format.header());
        System.out.println();
        for (TestSerialization test : formats) {
            System.out.println(test.statistics(rel));
        }
        for (TestSerialization test : formats) {
            test.verify(rel);
        }
        System.out.println();
    }

    @Test
    public void testBinarySerializer() throws IOException, SerializerException, TException {
        System.out.println("Test testBinarySerializer");
        testSerializer(Format.binary);
    }

    @Test
    public void testFastBinarySerializer() throws IOException, SerializerException, TException {
        System.out.println("Test testFastBinarySerializer");
        testSerializer(Format.fast_binary);
    }

    public void testSerializer(Format format) throws IOException, SerializerException, TException {
        TestSerialization test = TestSerialization.forFormat(format);

        System.out.format(". ( 0%%)%s", Control.cursorLeft(6));
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 80; ++j) {
                if (j % 10 == 0) {
                    System.out.print(Control.LEFT);
                    System.out.print(SWIRL[(j / 10) % 4]);
                    System.out.flush();
                }
                test.runProvidence(providence);
                test.runThrift(thrift);
            }

            System.gc();
            System.out.format("%s.. (%2d%%)%s", Control.LEFT, i + 1, Control.cursorLeft(6));
        }
        System.out.println();

        test.calculate();

        System.out.println(Format.header());
        System.out.println();
        System.out.println(test.asString());
        System.out.println();
    }
}
