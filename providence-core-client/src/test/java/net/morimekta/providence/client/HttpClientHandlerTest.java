package net.morimekta.providence.client;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.client.internal.NoLogging;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.SerializerProvider;
import net.morimekta.providence.util.ServiceCallInstrumentation;
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
import org.awaitility.Awaitility;
import org.awaitility.Duration;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static net.morimekta.providence.client.internal.TestNetUtil.factory;
import static net.morimekta.providence.client.internal.TestNetUtil.getExposedPort;
import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
public class HttpClientHandlerTest {
    private static final String ENDPOINT  = "test";
    private static final String NOT_FOUND = "not_found";
    private static final String HTML      = "html";
    private static final String RESPONSE  = "response";

    private int                                                port;
    private net.morimekta.test.thrift.client.TestService.Iface impl;
    private Server                                             server;
    private SerializerProvider                                 provider;
    private ArrayList<String>                                  contentTypes;
    private ServiceCallInstrumentation                         instrumentation;
    private AtomicReference<PServiceCall<?,?>>                 reply;

    private GenericUrl endpoint() {
        return new GenericUrl("http://localhost:" + port + "/" + ENDPOINT);
    }
    private GenericUrl notfound() {
        return new GenericUrl("http://localhost:" + port + "/" + NOT_FOUND);
    }
    private GenericUrl html() {
        return new GenericUrl("http://localhost:" + port + "/" + HTML);
    }
    private GenericUrl response() {
        return new GenericUrl("http://localhost:" + port + "/" + RESPONSE);
    }

    @Before
    public void setUp() throws Exception {
        Awaitility.setDefaultPollDelay(50, TimeUnit.MILLISECONDS);
        Log.setLog(new NoLogging());

        impl = mock(net.morimekta.test.thrift.client.TestService.Iface.class);
        TProcessor processor = new net.morimekta.test.thrift.client.TestService.Processor<>(impl);
        instrumentation = mock(ServiceCallInstrumentation.class);

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
        handler.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("text/html");
                resp.getWriter().print("<html></html>");
            }
        }), "/" + HTML);
        handler.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                resp.setStatus(HttpServletResponse.SC_OK);
                Serializer serializer = provider.getDefault();
                resp.setContentType(serializer.mediaType());
                serializer.serialize(resp.getOutputStream(), reply.get());
            }
        }), "/" + RESPONSE);

        contentTypes = new ArrayList<>();
        reply = new AtomicReference<>();

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
    public void testSimple() throws IOException, TException, Failure {
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                this::endpoint));

        when(impl.test(any(net.morimekta.test.thrift.client.Request.class)))
                .thenReturn(new net.morimekta.test.thrift.client.Response("response"));

        Response response = client.test(new Request("request"));

        waitAtMost(Duration.ONE_HUNDRED_MILLISECONDS).until(() -> contentTypes.size() > 0);

        verify(impl).test(any(net.morimekta.test.thrift.client.Request.class));
        verifyNoMoreInteractions(impl);

        assertThat(response, is(equalToMessage(new Response("response"))));
        assertThat(contentTypes, is(equalTo(ImmutableList.of("application/vnd.apache.thrift.binary"))));
    }

    @Test
    public void testBadRequest() throws IOException {
        HttpClientHandler handler = new HttpClientHandler(this::endpoint);

        try {
            handler.handleCall(new PServiceCall<>("foo",
                                                  PServiceCallType.EXCEPTION,
                                                  1,
                                                  new PApplicationException("", PApplicationExceptionType.INTERNAL_ERROR)),
                               TestService.kDescriptor);
            fail("no exception");
        } catch (PApplicationException e) {
            assertThat(e.getMessage(), is("Request with invalid call type: EXCEPTION"));
            assertThat(e.getId(), is(PApplicationExceptionType.INVALID_MESSAGE_TYPE));
        }
    }

    private class TestServiceBypass extends TestService {
        PMessage<?,?> testResponse(String text) {
            return TestService._test_response.withSuccess(Response.builder().setText(text));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBadResponse() throws IOException, Failure {
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                this::html));
        try {
            client.test(new Request("request"));
            fail("no exception");
        } catch (PApplicationException e) {
            assertThat(e.getMessage(), is("Unknown content-type in response: text/html;charset=utf-8"));
            assertThat(e.getId(), is(PApplicationExceptionType.INVALID_PROTOCOL));
        }

        client = new TestService.Client(new HttpClientHandler(
                this::response));

        reply.set(new PServiceCall("foo", PServiceCallType.REPLY, 1, new TestServiceBypass().testResponse("foo")));
        try {
            client.test(new Request("request"));
            fail("no exception");
        } catch (SerializerException e) {
            assertThat(e.getMessage(), is("No such method foo on client.TestService"));
            assertThat(e.getExceptionType(), is(PApplicationExceptionType.UNKNOWN_METHOD));
            assertThat(e.getMethodName(), is("foo"));
            assertThat(e.getSequenceNo(), is(1));
        }

        reply.set(new PServiceCall("test", PServiceCallType.CALL, 2, new TestServiceBypass().testResponse("bar")));
        try {
            client.test(new Request("request"));
            fail("no exception");
        } catch (PApplicationException e) {
            assertThat(e.getMessage(), is("Reply with invalid call type: CALL"));
            assertThat(e.getId(), is(PApplicationExceptionType.INVALID_MESSAGE_TYPE));
        }

        reply.set(new PServiceCall("test", PServiceCallType.REPLY, 100, new TestServiceBypass().testResponse("baz")));
        try {
            client.test(new Request("request"));
            fail("no exception");
        } catch (PApplicationException e) {
            assertThat(e.getMessage(), is("Reply sequence out of order: call = 2, reply = 100"));
            assertThat(e.getId(), is(PApplicationExceptionType.BAD_SEQUENCE_ID));
        }

        reply.set(new PServiceCall("test", PServiceCallType.REPLY, 4, new PApplicationException("foo", PApplicationExceptionType.INTERNAL_ERROR)));
        try {
            client.test(new Request("request"));
            fail("no exception");
        } catch (SerializerException e) {
            assertThat(e.getMessage(), is("Wrong type string(11) for client.TestService.test.response.fail, should be struct(12)"));
            assertThat(e.getExceptionType(), is(PApplicationExceptionType.PROTOCOL_ERROR));
            assertThat(e.getMethodName(), is("test"));
            assertThat(e.getSequenceNo(), is(4));
        }
    }

    @Test
    public void testInstrumentationFail() throws IOException, Failure, TException {
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                this::endpoint, factory(), provider, instrumentation));
        when(impl.test(any(net.morimekta.test.thrift.client.Request.class)))
                .thenReturn(new net.morimekta.test.thrift.client.Response("response"));
        doThrow(new NullPointerException()).when(instrumentation).onComplete(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));

        client.test(Request.builder().build());
        waitAtMost(Duration.ONE_HUNDRED_MILLISECONDS).until(() -> contentTypes.size() > 0);

        verify(instrumentation).onComplete(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));

        client = new TestService.Client(new HttpClientHandler(
                this::notfound, factory(), provider, instrumentation));
        doThrow(new NullPointerException()).when(instrumentation).onTransportException(any(IOException.class),
                                                                                       anyDouble(),
                                                                                       any(PServiceCall.class),
                                                                                       isNull());

        try {
            client.test(Request.builder().build());
            waitAtMost(Duration.ONE_HUNDRED_MILLISECONDS).until(() -> contentTypes.size() > 0);
            fail("no exception");
        } catch (Exception ignore) {
        }

        verify(instrumentation).onTransportException(any(IOException.class), anyDouble(), any(PServiceCall.class), isNull());
    }

    @Test
    public void testSimpleRequest() throws IOException, TException, Failure {
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                this::endpoint, factory(), provider, instrumentation));

        when(impl.test(any(net.morimekta.test.thrift.client.Request.class)))
                .thenReturn(new net.morimekta.test.thrift.client.Response("response"));

        Response response = client.test(new Request("request"));
        waitAtMost(Duration.ONE_HUNDRED_MILLISECONDS).until(() -> contentTypes.size() > 0);

        verify(impl).test(any(net.morimekta.test.thrift.client.Request.class));
        verify(instrumentation).onComplete(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
        verifyNoMoreInteractions(impl, instrumentation);

        assertThat(response, is(equalToMessage(new Response("response"))));
        assertThat(contentTypes, is(equalTo(ImmutableList.of("application/vnd.apache.thrift.binary"))));
    }

    @Test
    public void testSimpleRequest_oneway() throws IOException, TException, Failure {
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                this::endpoint, factory(), provider, instrumentation));

        AtomicBoolean called = new AtomicBoolean();
        doAnswer(i -> {
            called.set(true);
            return null;
        }).when(impl).onewayMethod();

        client.onewayMethod();

        waitAtMost(Duration.ONE_HUNDRED_MILLISECONDS).untilTrue(called);
        waitAtMost(Duration.ONE_HUNDRED_MILLISECONDS).until(() -> contentTypes.size() > 0);

        verify(impl).onewayMethod();
        verify(instrumentation).onComplete(anyDouble(), any(PServiceCall.class), isNull());
        verifyNoMoreInteractions(impl, instrumentation);

        assertThat(contentTypes, is(equalTo(ImmutableList.of("application/vnd.apache.thrift.binary"))));
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, Failure, TException {
        TestService.Iface client = new TestService.Client(
                new HttpClientHandler(this::endpoint, factory(), provider, instrumentation));

        when(impl.test(any(net.morimekta.test.thrift.client.Request.class)))
                .thenThrow(new net.morimekta.test.thrift.client.Failure("failure"));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (Failure ex) {
            assertThat(ex.getText(), is("failure"));
        }

        verify(impl).test(any(net.morimekta.test.thrift.client.Request.class));
        verify(instrumentation).onComplete(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
        verifyNoMoreInteractions(impl, instrumentation);
    }

    @Test
    public void testSimpleRequest_404_notFound() throws IOException, Failure, TException {
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                this::notfound, factory(), provider, instrumentation));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (HttpResponseException ex) {
            assertThat(ex.getStatusCode(), is(404));
            assertThat(ex.getStatusMessage(), is("Not Found"));
        }

        verify(instrumentation).onTransportException(any(HttpResponseException.class), anyDouble(), any(PServiceCall.class), isNull());
        verifyNoMoreInteractions(impl, instrumentation);
    }

    @Test
    public void testSimpleRequest_405_notSupported() throws IOException, Failure, TException {
        GenericUrl url = endpoint();
        url.setRawPath("/does_not_exists");
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                () -> url, factory(), provider, instrumentation));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (HttpResponseException ex) {
            assertThat(ex.getStatusCode(), is(405));
            assertThat(ex.getStatusMessage(), is("HTTP method POST is not supported by this URL"));
        }

        verify(instrumentation).onTransportException(any(HttpResponseException.class), anyDouble(), any(PServiceCall.class), isNull());
        verifyNoMoreInteractions(impl, instrumentation);
    }
}
