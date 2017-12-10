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
import net.morimekta.util.io.IOUtils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.InputStreamContent;
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

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.morimekta.providence.server.internal.TestNetUtil.factory;
import static net.morimekta.providence.server.internal.TestNetUtil.getExposedPort;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
        verify(instrumentation).onComplete(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
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

        verify(instrumentation).onComplete(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
        verifyNoMoreInteractions(instrumentation);
    }

    @Test
    public void testBadRequest() throws IOException, Failure {
        HttpResponse response;

        response = post("text/url-encoded", "method=test");
        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_BAD_REQUEST));
        assertThat(response.getStatusMessage(), is("Unknown content-type: text/url-encoded"));

        response = post("application/json", "{\"not\":\"usable\"}");
        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_OK));
        assertThat(IOUtils.readString(response.getContent()),
                   is("[\"\",\"exception\",0,{\"message\":\"Expected service call start ('['): but found '{'\",\"id\":\"PROTOCOL_ERROR\"}]"));

        response = post("application/json", "[\"foo\", 1, 1, {}]");
        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_OK));
        assertThat(IOUtils.readString(response.getContent()),
                   is("[\"foo\",\"exception\",1,{\"message\":\"No such method foo on service.TestService\",\"id\":\"UNKNOWN_METHOD\"}]"));


        response = post("application/json", "[\"test\", 2, 2, {}]");
        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_OK));
        assertThat(IOUtils.readString(response.getContent()),
                   is("[\"test\",\"exception\",2,{\"message\":\"Invalid service request call type: REPLY\",\"id\":\"INVALID_MESSAGE_TYPE\"}]"));

        response = post("application/json", "[\"test\", \"blurb\", 3, {}]");
        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_OK));
        assertThat(IOUtils.readString(response.getContent()),
                   is("[\"test\",\"exception\",0,{\"message\":\"Service call type \\\"blurb\\\" is not valid\",\"id\":\"INVALID_MESSAGE_TYPE\"}]"));

        reset(impl);
        doAnswer(i -> Response.builder().setText("reply").build())
                .when(impl)
                .test(any(Request.class));

        // and simple OK, just in case.
        response = post("application/json", "[\"test\", \"call\", 3, {\"request\":{}}]");
        assertThat(response.getStatusCode(), is(HttpServletResponse.SC_OK));
        assertThat(IOUtils.readString(response.getContent()),
                   is("[\"test\",\"reply\",3,{\"success\":{\"text\":\"reply\"}}]"));

    }

    public HttpResponse post(String contentType, String content) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        HttpRequest request = factory().buildPostRequest(endpoint(), new InputStreamContent(contentType, bais))
                             .setThrowExceptionOnExecuteError(false);
        request.getHeaders().setContentType(contentType);
        request.getHeaders().setAccept("*/*");
        return request.execute();
    }
}
