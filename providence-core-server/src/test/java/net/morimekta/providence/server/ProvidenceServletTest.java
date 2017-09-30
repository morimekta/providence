package net.morimekta.providence.server;

import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.client.HttpClientHandler;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.SerializerProvider;
import net.morimekta.providence.server.internal.NoLogging;
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
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static net.morimekta.providence.server.internal.TestNetUtil.factory;
import static net.morimekta.providence.server.internal.TestNetUtil.getExposedPort;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
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
    private static int                    port;
    private static TestService.Iface      impl;
    private static Server                 server;
    private static SerializerProvider     provider;
    private static ServiceInstrumentation instrumentation;

    private static final String ENDPOINT = "test";

    private static GenericUrl endpoint() {
        return new GenericUrl("http://localhost:" + port + "/" + ENDPOINT);
    }

    @BeforeClass
    public static void setUpServer() throws Exception {
        Log.setLog(new NoLogging());

        impl = mock(TestService.Iface.class);
        instrumentation = mock(ServiceInstrumentation.class);

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
        when(impl.test(any(Request.class))).thenReturn(new Response("response"));

        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                ProvidenceServletTest::endpoint, factory(), provider));

        Response response = client.test(new Request("request"));

        assertNotNull(response);
        assertEquals("{text:\"response\"}", response.asString());
        verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
        verifyNoMoreInteractions(instrumentation);
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, Failure {
        when(impl.test(any(Request.class))).thenThrow(Failure.builder()
                                                             .setText("failure")
                                                             .build());

        TestService.Iface client = new TestService.Client(new HttpClientHandler(ProvidenceServletTest::endpoint,
                                                                                factory(),
                                                                                provider));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (Failure ex) {
            assertEquals("failure", ex.getText());
        }

        verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
        verifyNoMoreInteractions(instrumentation);
    }

    @Test
    public void testSimpleRequest_404() throws IOException, Failure {
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

        verifyZeroInteractions(impl, instrumentation);
    }

    @Test
    public void testThriftClient_void() throws TException, IOException, Failure {
        ApacheHttpTransport transport = new ApacheHttpTransport();
        THttpClient httpClient = new THttpClient(endpoint().toString(), transport.getHttpClient());
        TBinaryProtocol protocol = new TBinaryProtocol(httpClient);
        net.morimekta.test.thrift.service.TestService.Iface client =
                new net.morimekta.test.thrift.service.TestService.Client(protocol);

        client.voidMethod(55);

        verify(impl).voidMethod(55);
        verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
        verifyNoMoreInteractions(impl, instrumentation);
    }

    @Test
    public void testThriftClient_failure() throws TException, IOException, Failure {
        ApacheHttpTransport transport = new ApacheHttpTransport();
        THttpClient httpClient = new THttpClient(endpoint().toString(), transport.getHttpClient());
        TBinaryProtocol protocol = new TBinaryProtocol(httpClient);
        net.morimekta.test.thrift.service.TestService.Iface client =
                new net.morimekta.test.thrift.service.TestService.Client(protocol);

        doThrow(new Failure("test"))
                .when(impl)
                .voidMethod(55);

        try {
            client.voidMethod(55);
        } catch (net.morimekta.test.thrift.service.Failure e) {
            assertEquals("test", e.getText());
        }

        verify(impl).voidMethod(55);
        verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
        verifyNoMoreInteractions(impl, instrumentation);
    }
}
