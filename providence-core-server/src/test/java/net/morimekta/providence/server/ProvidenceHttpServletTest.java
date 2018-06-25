package net.morimekta.providence.server;

import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.server.internal.NoLogging;
import net.morimekta.test.providence.service.Failure;
import net.morimekta.test.providence.service.OtherFailure;
import net.morimekta.test.providence.service.Request;
import net.morimekta.test.providence.service.Response;
import net.morimekta.util.io.IOUtils;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import org.awaitility.Awaitility;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static net.morimekta.providence.server.internal.TestNetUtil.factory;
import static net.morimekta.providence.server.internal.TestNetUtil.getExposedPort;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ProvidenceHttpServletTest {
    private static class TestServlet extends ProvidenceHttpServlet<Request, Request._Field, Response, Response._Field> {
        private TestServlet() {
            super(Request.kDescriptor);
        }

        @Nonnull
        @Override
        protected Response handle(HttpServletRequest httpRequest, Request request) throws Failure, ExecutionException {
            if (request.getText().startsWith("fail ")) {
                throw Failure.builder()
                             .setText(request.getText().substring(5))
                             .build();
            }
            if (request.getText().startsWith("other ")) {
                throw new ExecutionException(request.getText(),
                                             OtherFailure.builder()
                                                         .setOther(request.getText().substring(6))
                                                         .build());
            }

            return Response.builder()
                           .setText(request.getText())
                           .build();
        }

        @Override
        protected int statusCodeForException(@Nonnull Throwable exception) {
            if (exception instanceof Failure) {
                return HttpStatus.EXPECTATION_FAILED_417;
            }
            if (exception instanceof OtherFailure) {
                return HttpStatus.FAILED_DEPENDENCY_424;
            }
            return super.statusCodeForException(exception);
        }
    }

    private int                        port;
    private Server                     server;

    private static final String ENDPOINT = "test";

    private GenericUrl endpoint() {
        return new GenericUrl("http://localhost:" + port + "/" + ENDPOINT);
    }

    @Before
    public void setUpServer() throws Exception {
        Awaitility.setDefaultPollDelay(2, TimeUnit.MILLISECONDS);
        Log.setLog(new NoLogging());

        server = new Server(0);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new TestServlet()),
                           "/" + ENDPOINT);

        server.setHandler(handler);
        server.start();
        port = getExposedPort(server);
    }

    @After
    public void tearDownServer() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSimpleRequest() throws IOException {
        String num = "" + new Random().nextInt(100);
        String text = "yes " + num;

        HttpResponse response = post(JsonSerializer.JSON_MEDIA_TYPE, "{\"text\": \"" + text + "\"}");
        assertThat(response.getStatusCode(), is(HttpStatus.OK_200));

        String content = IOUtils.readString(response.getContent());
        assertThat(content, is("{\"text\":\"" + text + "\"}"));
    }

    @Test
    public void testFailedRequest() throws IOException {
        String num = "" + new Random().nextInt(100);
        String text = "fail " + num;

        HttpResponse response = post(JsonSerializer.JSON_MEDIA_TYPE, "{\"text\": \"" + text + "\"}");
        assertThat(response.getStatusCode(), is(HttpStatus.EXPECTATION_FAILED_417));

        String content = IOUtils.readString(response.getContent());
        assertThat(content, is("{\"text\":\"" + num + "\"}"));
    }

    @Test
    public void testOtherFailedRequest() throws IOException {
        String num = "" + new Random().nextInt(100);
        String text = "other " + num;

        HttpResponse response = post(JsonSerializer.JSON_MEDIA_TYPE, "{\"text\": \"" + text + "\"}");
        assertThat(response.getStatusCode(), is(HttpStatus.FAILED_DEPENDENCY_424));

        String content = IOUtils.readString(response.getContent());
        assertThat(content, is("{\"other\":\"" + num + "\"}"));
    }

    @Test
    public void testBadRequest() throws IOException {
        HttpResponse response;

        response = post("text/url-encoded", "method=test");
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST_400));
        assertThat(response.getStatusMessage(), is("Unknown content-type: text/url-encoded"));

        response = post("application/json", "[\"foo\", 1, 1, {}]");
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST_400));
        assertThat(response.getStatusMessage(), is("Bad Request"));
    }

    private HttpResponse post(String contentType, String content) throws IOException {
        HttpRequest request = factory()
                .buildPostRequest(
                        endpoint(), new ByteArrayContent(contentType, content.getBytes(StandardCharsets.UTF_8)))
                .setThrowExceptionOnExecuteError(false);
        request.getHeaders().setAccept("*/*");
        return request.execute();
    }

}
