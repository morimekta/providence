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

import static org.junit.Assert.fail;

/**
 * Speed test running through:
 * - Read a file for each factory / serialization format.
 * - Write the same file back to a temp file.
 */
public class SerializationIT {
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
    public void testSingleRun() throws IOException, SerializerException, TException {
        System.out.println(Format.header());
        System.out.println();

        for (Format format : Format.values()) {
            TestSerialization test = TestSerialization.forFormat(format);

            test.runProvidence(providence);
            test.runThrift(thrift);
            test.calculate();

            System.out.println(test.asString());
        }
    }

    @Test
    public void testManyRuns() throws IOException, SerializerException, TException {
        LinkedList<TestSerialization> formats = new LinkedList<>();
        for (Format format : Format.values()) {
            formats.add(TestSerialization.forFormat(format));
        }

        char[] c = {'|', '/', '-', '\\'};

        for (int i = 0; i < 80; ++i) {
            Collections.shuffle(formats);
            for (TestSerialization test : formats) {
                for (int j = 0; j < 40; ++j) {
                    if (j % 10 == 0) {
                        System.out.print(Control.LEFT);
                        System.out.print(c[(j / 10) % 4]);
                        System.out.flush();
                    }
                    test.runProvidence(providence);
                    test.runThrift(thrift);
                }
                System.gc();
            }
            System.out.print(Control.LEFT);
            System.out.print("..");
        }

        System.out.println();

        formats.forEach(TestSerialization::calculate);
        Collections.sort(formats);

        System.out.println(Format.header());
        System.out.println();
        for (TestSerialization test : formats) {
            System.out.println(test.asString());
        }
    }
}
