package net.morimekta.providence.thrift.client;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.test.providence.thrift.service.Failure;
import net.morimekta.test.providence.thrift.service.MyService;
import net.morimekta.test.providence.thrift.service.MyService2;
import net.morimekta.test.providence.thrift.service.Request;
import net.morimekta.test.providence.thrift.service.Response;
import net.morimekta.test.thrift.thrift.service.MyService.Iface;
import net.morimekta.test.thrift.thrift.service.MyService.Processor;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.awaitility.Duration;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.morimekta.providence.PApplicationExceptionType.UNKNOWN_METHOD;
import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static net.morimekta.providence.thrift.util.TestUtil.findFreePort;
import static org.awaitility.Awaitility.setDefaultPollDelay;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
public class NonblockingSocketClientHandlerTest {
    private static ExecutorService    executor;
    private static int                port;
    private static Iface              impl;
    private static TNonblockingServer server;
    private static InetSocketAddress  address;
    private static Serializer         serializer;

    @BeforeClass
    public static void setUpServer() throws Exception {
        setDefaultPollDelay(10, TimeUnit.MILLISECONDS);

        serializer = new TBinaryProtocolSerializer();
        TProtocolFactory factory = new TBinaryProtocol.Factory();
        port = findFreePort();
        impl = Mockito.mock(Iface.class);

        TNonblockingServerTransport transport = new TNonblockingServerSocket(port);
        server = new TNonblockingServer(
                new TNonblockingServer.Args(transport)
                        .protocolFactory(factory)
                        .processor(new Processor<>(impl)));

        executor = Executors.newSingleThreadExecutor();
        executor.submit(server::serve);
        address = new InetSocketAddress("localhost", port);
    }

    @Before
    public void setUp() {
        reset(impl);
    }

    @AfterClass
    public static void tearDownServer() {
        try {
            server.stop();
            executor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSimpleRequest()
            throws IOException, TException, net.morimekta.test.providence.thrift.service.Failure {
        when(impl.test(new net.morimekta.test.thrift.thrift.service.Request("test")))
                .thenReturn(new net.morimekta.test.thrift.thrift.service.Response("response"));

        MyService.Iface client = new MyService.Client(new NonblockingSocketClientHandler(serializer, address));

        Response response = client.test(Request.builder().setText("test").build());

        verify(impl).test(any(net.morimekta.test.thrift.thrift.service.Request.class));

        assertThat(response, is(equalToMessage(new Response("response"))));
    }

    @Test
    public void testOnewayRequest()
            throws IOException, TException {
        MyService.Iface client = new MyService.Client(new NonblockingSocketClientHandler(serializer, address));

        AtomicBoolean called = new AtomicBoolean(false);
        doAnswer(i -> {
            called.set(true);
            return null;
        }).when(impl).ping();

        client.ping();

        waitAtMost(Duration.ONE_HUNDRED_MILLISECONDS).untilTrue(called);

        verify(impl).ping();
    }

    @Test
    public void testMultipleRequests()
            throws IOException, TException, net.morimekta.test.providence.thrift.service.Failure {
        when(impl.test(new net.morimekta.test.thrift.thrift.service.Request("test")))
                .thenReturn(new net.morimekta.test.thrift.thrift.service.Response("response"));
        when(impl.test(new net.morimekta.test.thrift.thrift.service.Request("test2")))
                .thenReturn(new net.morimekta.test.thrift.thrift.service.Response("response2"));

        MyService.Iface client = new MyService.Client(new NonblockingSocketClientHandler(serializer, address));

        Response response = client.test(Request.builder().setText("test").build());
        Response response2 = client.test(Request.builder().setText("test2").build());

        verify(impl).test(eq(new net.morimekta.test.thrift.thrift.service.Request("test")));
        verify(impl).test(eq(new net.morimekta.test.thrift.thrift.service.Request("test2")));
        verifyNoMoreInteractions(impl);

        assertThat(response, is(equalToMessage(new Response("response"))));
        assertThat(response2, is(equalToMessage(new Response("response2"))));
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, TException {
        when(impl.test(new net.morimekta.test.thrift.thrift.service.Request("test")))
                .thenThrow(new net.morimekta.test.thrift.thrift.service.Failure("failure"));

        MyService.Iface client = new MyService.Client(new NonblockingSocketClientHandler(serializer, address));
        try {
            client.test(Request.builder().setText("test").build());
            fail("no exception");
        } catch (Failure f) {
            assertThat(f, is(equalToMessage(new Failure("failure"))));
        }

        verify(impl).test(any(net.morimekta.test.thrift.thrift.service.Request.class));
    }

    @Test
    public void testSimpleRequest_wrongMethod()
            throws IOException, TException, Failure {
        when(impl.test(any(net.morimekta.test.thrift.thrift.service.Request.class)))
                .thenThrow(new net.morimekta.test.thrift.thrift.service.Failure("failure"));

        MyService2.Iface client = new MyService2.Client(new NonblockingSocketClientHandler(serializer, address));

        try {
            client.testing(new Request("test"));
            fail("no exception");
        } catch (PApplicationException e) {
            assertThat(e.getId(), is(UNKNOWN_METHOD));
            assertThat(e.getMessage(), is(equalTo("Invalid method name: 'testing'")));
        }

        verifyZeroInteractions(impl);
    }

    @Test
    public void testSimpleRequest_cannotConnect() throws IOException, Failure {
        Serializer serializer = new BinarySerializer();
        InetSocketAddress address = new InetSocketAddress("localhost", port - 10);
        MyService.Iface client = new MyService.Client(new NonblockingSocketClientHandler(serializer, address));
        try {
            client.test(Request.builder().setText("test").build());
            fail("no exception");
        } catch (ConnectException e) {
            // The exception message is entirely localized, so it's impossible to reliably match against.
            // assertThat(e.getMessage(), startsWith("Connection refused"));
        }

        verifyZeroInteractions(impl);
    }
}
