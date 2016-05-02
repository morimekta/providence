package net.morimekta.providence.thrift;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.streams.MessageCollectors;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.test.providence.Containers;
import net.morimekta.test.providence.srv.Request;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.morimekta.providence.util.ProvidenceHelper.arrayListFromJsonResource;
import static net.morimekta.providence.testing.ProvidenceMatchers.messageEq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Incidentally actually a test that the BinarySerializer generates the same output as
 * TBinaryProtocolSerializer, and can also read back what TBinaryProtocolSerializer
 * generates.
 */
public class PBinarySerializerTest {
    private static final BinarySerializer          providence = new BinarySerializer();
    private static final TBinaryProtocolSerializer thrift     = new TBinaryProtocolSerializer();
    private static ArrayList<Containers> containers;

    @Before
    public void setUp() throws SerializerException, IOException {
        synchronized (PBinarySerializerTest.class) {
            // Since these are immutable, we don't need to read for each test.
            if (containers == null) {
                containers = arrayListFromJsonResource("/providence/test.json", Containers.kDescriptor);
                assertEquals(10, containers.size());
            }
        }
    }

    @Test
    public void testThriftToProvidence_simple() throws IOException, SerializerException {
        Request request = new Request("test");

        // Providence client talks to thrift service.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thrift.serialize(baos, request);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Request out = providence.deserialize(bais, Request.kDescriptor);

        assertThat(out, messageEq(request));
    }

    @Test
    public void testProvidenceToThrift_simple() throws IOException, SerializerException {
        Request request = new Request("test");

        // Providence client talks to thrift service.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        providence.serialize(baos, request);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Request out = thrift.deserialize(bais, Request.kDescriptor);

        assertThat(out, messageEq(request));
    }

    @Test
    public void testProvidenceToThrift_containers() throws IOException {
        // Providence client talks to thrift service.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        containers.stream().collect(MessageCollectors.toStream(baos, providence));
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        List<Containers> out = MessageStreams.stream(bais, thrift, Containers.kDescriptor).collect(Collectors.toList());

        assertEquals(containers.size(), out.size());
        for (int i = 0; i < containers.size(); ++i) {
            assertThat(containers.get(i), messageEq(out.get(i)));
        }
    }

    @Test
    public void testThriftToProvidence() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        containers.stream().collect(MessageCollectors.toStream(baos, thrift));
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        List<Containers> out = MessageStreams.stream(bais, providence, Containers.kDescriptor).collect(Collectors.toList());

        assertEquals(containers.size(), out.size());
        for (int i = 0; i < containers.size(); ++i) {
            assertThat(containers.get(i), messageEq(out.get(i)));
        }
    }
}
