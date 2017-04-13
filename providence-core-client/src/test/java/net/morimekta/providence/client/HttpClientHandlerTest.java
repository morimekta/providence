package net.morimekta.providence.client;

import net.morimekta.providence.client.internal.NoLogging;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.SerializerProvider;
import net.morimekta.test.providence.client.Failure;
import net.morimekta.test.providence.client.Request;
import net.morimekta.test.providence.client.Response;
import net.morimekta.test.providence.client.TestService;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponseException;
import com.google.common.collect.ImmutableList;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static net.morimekta.providence.client.internal.TestNetUtil.factory;
import static net.morimekta.providence.client.internal.TestNetUtil.getExposedPort;
import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
public class HttpClientHandlerTest {
    private static final String ENDPOINT = "test";
    private static final String NOT_FOUND = "not_found";

    private int                                                    port;
    private net.morimekta.test.providence.thrift.TestService.Iface impl;
    private Server                                                 server;
    private SerializerProvider                                     provider;
    private LinkedList<String>                                     contentTypes;

    private GenericUrl endpoint() {
        return new GenericUrl("http://localhost:" + port + "/" + ENDPOINT);
    }

    @Before
    public void setUp() throws Exception {
        Log.setLog(new NoLogging());

        impl = mock(net.morimekta.test.providence.thrift.TestService.Iface.class);
        TProcessor processor = new net.morimekta.test.providence.thrift.TestService.Processor<>(impl);

        provider = new DefaultSerializerProvider();
        server = new Server(0);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new TServlet(processor, new TBinaryProtocol.Factory())),
                           "/" + ENDPOINT);
        handler.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }), "/" + NOT_FOUND);

        contentTypes = new LinkedList<>();

        server.setHandler(handler);
        server.setRequestLog((request, response) -> contentTypes.addAll(
                Collections.list(request.getHeaders("Content-Type"))
                           .stream()
                           .map(Object::toString)
                           .collect(Collectors.toList())));
        server.start();
        port = getExposedPort(server);
    }

    @After
    public void tearDown() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSimpleRequest() throws IOException, TException, Failure {
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                this::endpoint, factory(), provider));

        when(impl.test(any(net.morimekta.test.providence.thrift.Request.class)))
                .thenReturn(new net.morimekta.test.providence.thrift.Response("response"));

        Response response = client.test(new Request("request"));

        assertThat(response, is(equalToMessage(new Response("response"))));
        assertThat(contentTypes, is(equalTo(ImmutableList.of("application/vnd.apache.thrift.binary"))));
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, Failure, TException {
        TestService.Iface client = new TestService.Client(
                new HttpClientHandler(this::endpoint, factory(), provider));

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
    public void testSimpleRequest_404_notFound() throws IOException, Failure, TException {
        GenericUrl url = endpoint();
        url.setRawPath("/" + NOT_FOUND);
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                () -> url, factory(), provider));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (HttpResponseException ex) {
            assertThat(ex.getStatusCode(), is(404));
            assertThat(ex.getStatusMessage(), is("Not Found"));
        }

        verifyZeroInteractions(impl);
    }

    @Test
    public void testSimpleRequest_405_notSupported() throws IOException, Failure, TException {
        GenericUrl url = endpoint();
        url.setRawPath("/does_not_exists");
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                () -> url, factory(), provider));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (HttpResponseException ex) {
            assertThat(ex.getStatusCode(), is(405));
            assertThat(ex.getStatusMessage(), is("HTTP method POST is not supported by this URL"));
        }

        verifyZeroInteractions(impl);
    }
}
