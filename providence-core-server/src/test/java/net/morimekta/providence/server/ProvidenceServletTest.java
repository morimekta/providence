package net.morimekta.providence.server;

import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.client.HttpClientHandler;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.SerializerProvider;
import net.morimekta.providence.server.internal.NoLogging;
import net.morimekta.providence.util.ServiceCallInstrumentation;
import net.morimekta.test.providence.service.Failure;
import net.morimekta.test.providence.service.Request;
import net.morimekta.test.providence.service.Response;
import net.morimekta.test.providence.service.TestService;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.apache.ApacheHttpTransport;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.THttpClient;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.morimekta.providence.server.internal.TestNetUtil.factory;
import static net.morimekta.providence.server.internal.TestNetUtil.getExposedPort;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
public class ProvidenceServletTest {
    private static int                        port;
    private static TestService.Iface          impl;
    private static Server                     server;
    private static SerializerProvider         provider;
    private static ServiceCallInstrumentation instrumentation;

    private static final String ENDPOINT = "test";

    private static GenericUrl endpoint() {
        return new GenericUrl("http://localhost:" + port + "/" + ENDPOINT);
    }

    @BeforeClass
    public static void setUpServer() throws Exception {
        Awaitility.setDefaultPollDelay(2, TimeUnit.MILLISECONDS);
        Log.setLog(new NoLogging());

        impl = mock(TestService.Iface.class);
        instrumentation = mock(ServiceCallInstrumentation.class);

        provider = new DefaultSerializerProvider();

        server = new Server(0);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new ProvidenceServlet(new TestService.Processor(impl), provider, instrumentation)),
                           "/" + ENDPOINT);

        server.setHandler(handler);
        server.start();
        port = getExposedPort(server);
    }

    @Before
    public void setUp() throws Exception {
        reset(impl, instrumentation);
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
    public void testSimpleRequest() throws IOException, Failure {
        AtomicBoolean called = new AtomicBoolean();
        when(impl.test(any(Request.class))).thenAnswer(i -> {
            called.set(true);
            return new Response("response");
        });

        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                ProvidenceServletTest::endpoint, factory(), provider));

        Response response = client.test(new Request("request"));

        waitAtMost(Duration.ONE_HUNDRED_MILLISECONDS).untilTrue(called);

        assertNotNull(response);
        assertEquals("{text:\"response\"}", response.asString());
        verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
        verifyNoMoreInteractions(instrumentation);
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, Failure {
        AtomicBoolean called = new AtomicBoolean();
        when(impl.test(any(Request.class))).thenAnswer(i -> {
            called.set(true);
            throw Failure.builder()
                   .setText("failure")
                   .build();
        });

        TestService.Iface client = new TestService.Client(new HttpClientHandler(ProvidenceServletTest::endpoint,
                                                                                factory(),
                                                                                provider));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (Failure ex) {
            assertEquals("failure", ex.getText());
        }

        waitAtMost(Duration.ONE_HUNDRED_MILLISECONDS).untilTrue(called);

        verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
        verifyNoMoreInteractions(instrumentation);
    }

    @Test
    public void testSimpleRequest_404() throws IOException, Failure, InterruptedException {
        when(impl.test(any(Request.class))).thenThrow(Failure.builder()
                                                             .setText("failure")
                                                             .build());

        GenericUrl url = endpoint();
        url.setRawPath("/" + ENDPOINT + "/does_not_exists");

        TestService.Iface client = new TestService.Client(new HttpClientHandler(() -> url, factory(), provider));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (HttpResponseException ex) {
            assertEquals("HTTP method POST is not supported by this URL", ex.getStatusMessage());
        }

        Thread.sleep(10L);

        verifyZeroInteractions(impl, instrumentation);
    }

    @Test
    public void testThriftClient_void() throws TException, IOException, Failure {
        ApacheHttpTransport transport = new ApacheHttpTransport();
        THttpClient httpClient = new THttpClient(endpoint().toString(), transport.getHttpClient());
        TBinaryProtocol protocol = new TBinaryProtocol(httpClient);
        net.morimekta.test.thrift.service.TestService.Iface client =
                new net.morimekta.test.thrift.service.TestService.Client(protocol);

        AtomicBoolean called = new AtomicBoolean();
        doAnswer(i -> {
            called.set(true);
            return null;
        }).when(impl).voidMethod(55);

        client.voidMethod(55);

        waitAtMost(Duration.ONE_HUNDRED_MILLISECONDS).untilTrue(called);

        verify(impl).voidMethod(55);
        verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
        verifyNoMoreInteractions(impl, instrumentation);
    }

    @Test
    public void testThriftClient_oneway() throws TException, IOException, Failure {
        ApacheHttpTransport transport = new ApacheHttpTransport();
        THttpClient httpClient = new THttpClient(endpoint().toString(), transport.getHttpClient());
        TBinaryProtocol protocol = new TBinaryProtocol(httpClient);
        net.morimekta.test.thrift.service.TestService.Iface client =
                new net.morimekta.test.thrift.service.TestService.Client(protocol);

        AtomicBoolean called = new AtomicBoolean();
        doAnswer(i -> {
            called.set(true);
            return null;
        }).when(impl).ping();

        client.ping();

        waitAtMost(Duration.ONE_HUNDRED_MILLISECONDS).untilTrue(called);

        verify(impl).ping();
        verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), isNull());
        verifyNoMoreInteractions(impl, instrumentation);
    }

    @Test
    public void testThriftClient_failure() throws TException, IOException, Failure {
        ApacheHttpTransport transport = new ApacheHttpTransport();
        THttpClient httpClient = new THttpClient(endpoint().toString(), transport.getHttpClient());
        TBinaryProtocol protocol = new TBinaryProtocol(httpClient);
        net.morimekta.test.thrift.service.TestService.Iface client =
                new net.morimekta.test.thrift.service.TestService.Client(protocol);

        AtomicBoolean called = new AtomicBoolean();
        doAnswer(i -> {
            called.set(true);
            throw new Failure("test");
        }).when(impl).voidMethod(55);

        try {
            client.voidMethod(55);
        } catch (net.morimekta.test.thrift.service.Failure e) {
            assertEquals("test", e.getText());
        }

        waitAtMost(Duration.ONE_HUNDRED_MILLISECONDS).untilTrue(called);

        verify(impl).voidMethod(55);
        verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
        verifyNoMoreInteractions(impl, instrumentation);
    }
}
