package net.morimekta.providence.client;

import net.morimekta.providence.client.util.NoLogging;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.SerializerProvider;
import net.morimekta.providence.server.ProvidenceServlet;
import net.morimekta.test.providence.srv.Failure;
import net.morimekta.test.providence.srv.MyService;
import net.morimekta.test.providence.srv.Request;
import net.morimekta.test.providence.srv.Response;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponseException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static net.morimekta.providence.testing.util.TestNetUtil.factory;
import static net.morimekta.providence.testing.util.TestNetUtil.findFreePort;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
public class HttpClientHandlerTest {
    @Rule
    public TemporaryFolder temp;

    private static int             port;
    private static MyService.Iface impl;
    private static Server          server;
    private static SerializerProvider provider;

    private static final String ENDPOINT = "test";

    private static GenericUrl endpoint() {
        return new GenericUrl("http://localhost:" + port + "/" + ENDPOINT);
    }

    @BeforeClass
    public static void setUpServer() throws Exception {
        Log.setLog(new NoLogging());

        port = findFreePort();
        impl = mock(MyService.Iface.class);

        provider = new DefaultSerializerProvider();

        server = new Server(port);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new ProvidenceServlet(new MyService.Processor(impl), provider)),
                           "/" + ENDPOINT);

        server.setHandler(handler);
        server.start();
    }

    @Before
    public void setUp() throws Exception {
        reset(impl);
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

        MyService.Iface client = new MyService.Client(new HttpClientHandler(
                HttpClientHandlerTest::endpoint, factory(), provider));

        Response response = client.test(new Request("request"));

        assertNotNull(response);
        assertEquals("{text:\"response\"}", response.asString());
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, Failure {
        when(impl.test(any(Request.class)))
                .thenThrow(Failure.builder()
                                  .setText("failure")
                                  .build());

        MyService.Iface client = new MyService.Client(new HttpClientHandler(
                HttpClientHandlerTest::endpoint, factory(), provider));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (Failure ex) {
            assertEquals("failure", ex.getText());
        }
    }

    @Test
    public void testSimpleRequest_404() throws IOException, Failure {
        when(impl.test(any(Request.class)))
                .thenThrow(Failure.builder()
                                  .setText("failure")
                                  .build());

        GenericUrl url = endpoint();
        url.setRawPath("/" + ENDPOINT + "/does_not_exists");

        MyService.Iface client = new MyService.Client(new HttpClientHandler(
                () -> url, factory(), provider));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (HttpResponseException ex) {
            assertEquals("HTTP method POST is not supported by this URL", ex.getStatusMessage());
        }
    }
}
