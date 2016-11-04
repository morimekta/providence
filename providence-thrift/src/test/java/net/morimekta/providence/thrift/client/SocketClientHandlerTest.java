package net.morimekta.providence.thrift.client;

import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.test.providence.srv.MyService;
import net.morimekta.test.providence.srv.MyService2;
import net.morimekta.test.providence.srv.Request;
import net.morimekta.test.thrift.srv.MyService.Iface;
import net.morimekta.test.thrift.srv.MyService.Processor;

import org.apache.commons.codec.DecoderException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.morimekta.providence.thrift.util.TestUtil.findFreePort;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
public class SocketClientHandlerTest {
    private static ExecutorService executor;
    private static int             port;
    private static Iface           impl;
    private static TServer         server;
    private static JsonSerializer  json;

    private SocketClientHandler client;

    @BeforeClass
    public static void setUpServer() throws Exception {
        port = findFreePort();
        impl = Mockito.mock(Iface.class);
        json = new JsonSerializer(false, JsonSerializer.IdType.NAME, JsonSerializer.IdType.NAME, true);

        TServerSocket transport = new TServerSocket(port);
        server = new TSimpleServer(
                new TServer.Args(transport)
                        .protocolFactory(new TBinaryProtocol.Factory())
                        .processor(new Processor<>(impl)));
        executor = Executors.newSingleThreadExecutor();
        executor.submit(server::serve);
    }

    private static String toJson(PServiceCall call) throws IOException, SerializerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        json.serialize(baos, call);
        return new String(baos.toByteArray(), UTF_8);
    }

    @Before
    public void setUp() throws Exception {
        reset(impl);

        Serializer serializer = new BinarySerializer();
        InetSocketAddress address = new InetSocketAddress("localhost", port);
        client = new SocketClientHandler(serializer, address);
    }

    @After
    public void tearDown() {
        // client.close();
    }

    @AfterClass
    public static void tearDownServer() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSimpleRequest() throws IOException, TException, SerializerException {
        when(impl.test(any(net.morimekta.test.thrift.srv.Request.class)))
                .thenReturn(new net.morimekta.test.thrift.srv.Response("response"));

        PServiceCall response = client.handleCall(new PServiceCall<>("test",
                                                                     PServiceCallType.CALL,
                                                                     44,
                                                                     Request.builder().build()),
                                                  MyService.kDescriptor);

        verify(impl).test(any(net.morimekta.test.thrift.srv.Request.class));

        assertEquals("[\n" +
                     "    \"test\",\n" +
                     "    \"reply\",\n" +
                     "    44,\n" +
                     "    {\n" +
                     "        \"success\": {\n" +
                     "            \"text\": \"response\"\n" +
                     "        }\n" +
                     "    }\n" +
                     "]", toJson(response));
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, TException, SerializerException {
        when(impl.test(any(net.morimekta.test.thrift.srv.Request.class)))
                .thenThrow(new net.morimekta.test.thrift.srv.Failure("failure"));

        PServiceCall response = client.handleCall(new PServiceCall<>("test",
                                                                     PServiceCallType.CALL,
                                                                     44,
                                                                     Request.builder().build()),
                                                  MyService.kDescriptor);

        verify(impl).test(any(net.morimekta.test.thrift.srv.Request.class));

        assertEquals("[\n" +
                     "    \"test\",\n" +
                     "    \"reply\",\n" +
                     "    44,\n" +
                     "    {\n" +
                     "        \"f\": {\n" +
                     "            \"text\": \"failure\"\n" +
                     "        }\n" +
                     "    }\n" +
                     "]", toJson(response));
    }

    @Test
    public void testSimpleRequest_wrongMethod() throws IOException, TException, DecoderException, SerializerException {
        when(impl.test(any(net.morimekta.test.thrift.srv.Request.class)))
                .thenThrow(new net.morimekta.test.thrift.srv.Failure("failure"));

        PServiceCall response = client.handleCall(new PServiceCall<>("testing",
                                                                     PServiceCallType.CALL,
                                                                     44,
                                                                     Request.builder().build()),
                                                  MyService2.kDescriptor);

        verifyZeroInteractions(impl);

        assertEquals("[\n" +
                     "    \"testing\",\n" +
                     "    \"exception\",\n" +
                     "    44,\n" +
                     "    {\n" +
                     "        \"message\": \"Invalid method name: 'testing'\",\n" +
                     "        \"id\": \"UNKNOWN_METHOD\"\n" +
                     "    }\n" +
                     "]", toJson(response));
    }

    @Test
    public void testSimpleRequest_cannotConnect() throws IOException, TException, SerializerException {
        Serializer serializer = new BinarySerializer();
        InetSocketAddress address = new InetSocketAddress("localhost", port - 10);
        client = new SocketClientHandler(serializer, address);
        try {
             client.handleCall(new PServiceCall<>("test",
                                                  PServiceCallType.CALL,
                                                  44,
                                                  Request.builder().build()),
                               MyService.kDescriptor);
            fail("");
        } catch (ConnectException e) {
            assertThat(e.getMessage(), startsWith("Connection refused"));
        }

        verifyZeroInteractions(impl);
    }
}
