package net.morimekta.providence.client;

import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.SerializerProvider;
import net.morimekta.providence.testing.util.NoLogging;
import net.morimekta.test.providence.service.Failure;
import net.morimekta.test.providence.service.Request;
import net.morimekta.test.providence.service.Response;
import net.morimekta.test.providence.service.TestService;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponseException;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static net.morimekta.providence.testing.util.TestNetUtil.factory;
import static net.morimekta.providence.testing.util.TestNetUtil.getExposedPort;
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
    private static int                                                     port;
    private static net.morimekta.test.providence.thrift.TestService.Iface impl;
    private static Server                                                  server;
    private static SerializerProvider                                      provider;

    private static final String ENDPOINT = "test";

    private static GenericUrl endpoint() {
        return new GenericUrl("http://localhost:" + port + "/" + ENDPOINT);
    }

    @BeforeClass
    public static void setUpServer() throws Exception {
        Log.setLog(new NoLogging());

        impl = mock(net.morimekta.test.providence.thrift.TestService.Iface.class);
        TProcessor processor = new net.morimekta.test.providence.thrift.TestService.Processor<>(impl);

        provider = new DefaultSerializerProvider();
        server = new Server(0);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new TServlet(processor, new TBinaryProtocol.Factory())),
                           "/" + ENDPOINT);

        server.setHandler(handler);
        server.start();
        port = getExposedPort(server);
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
    public void testSimpleRequest() throws IOException, TException, Failure {
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                HttpClientHandlerTest::endpoint, factory(), provider));

        when(impl.test(any(net.morimekta.test.providence.thrift.Request.class)))
                .thenReturn(new net.morimekta.test.providence.thrift.Response("response"));

        Response response = client.test(new Request("request"));

        assertNotNull(response);
        assertEquals("{text:\"response\"}", response.asString());
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, Failure, TException {
        TestService.Iface client = new TestService.Client(
                new HttpClientHandler(HttpClientHandlerTest::endpoint, factory(), provider));

        when(impl.test(any(net.morimekta.test.providence.thrift.Request.class)))
                .thenThrow(new net.morimekta.test.providence.thrift.Failure("failure"));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (Failure ex) {
            assertEquals("failure", ex.getText());
        }
    }

    @Test
    public void testSimpleRequest_404() throws IOException, Failure, TException {
        GenericUrl url = endpoint();
        url.setRawPath("/" + ENDPOINT + "/does_not_exists");
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                () -> url, factory(), provider));

        when(impl.test(any(net.morimekta.test.providence.thrift.Request.class)))
                .thenReturn(new net.morimekta.test.providence.thrift.Response("response"));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (HttpResponseException ex) {
            assertEquals("HTTP method POST is not supported by this URL", ex.getStatusMessage());
        }
    }
}
