package net.morimekta.providence.client;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import net.morimekta.providence.client.internal.NoLogging;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.FastBinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerProvider;
import net.morimekta.test.providence.client.Request;
import net.morimekta.test.providence.client.Response;
import org.awaitility.Awaitility;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static net.morimekta.providence.client.internal.TestNetUtil.factory;
import static net.morimekta.providence.client.internal.TestNetUtil.getExposedPort;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
@RunWith(DataProviderRunner.class)
public class GoogleHttpClientTest {
    private static final String RESPONSE = "response";

    private int                       port;
    private Server                    server;
    private SerializerProvider        provider;
    private AtomicReference<Request>  received;
    private AtomicReference<Response> toRespond;
    private AtomicInteger             statusCode;

    private GenericUrl endpoint() {
        return new GenericUrl("http://localhost:" + port + "/" + RESPONSE);
    }

    @Before
    public void setUp() throws Exception {
        Awaitility.setDefaultPollDelay(50, TimeUnit.MILLISECONDS);
        Log.setLog(new NoLogging());

        provider = new DefaultSerializerProvider();
        server = new Server(0);
        ServletContextHandler handler = new ServletContextHandler();

        toRespond = new AtomicReference<>(Response.builder()
                                                  .setText("ok")
                                                  .build());
        statusCode = new AtomicInteger(HttpServletResponse.SC_OK);

        received = new AtomicReference<>();

        handler.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            @SuppressWarnings("unchecked")
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws IOException {
                Serializer serializer = provider.getSerializer(req.getContentType());
                received.set(serializer.deserialize(req.getInputStream(), Request.kDescriptor));

                resp.setStatus(statusCode.get());
                resp.setContentType(serializer.mediaType());
                serializer.serialize(resp.getOutputStream(), toRespond.get());
            }
        }), "/" + RESPONSE);

        server.setHandler(handler);
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

    @DataProvider
    public static Object[][] testOptions() {
        return new Object[][] {
                {new BinarySerializer()},
                {new JsonSerializer()},
                {new JsonSerializer().named()},
                {new FastBinarySerializer()},
                };
    }

    @Test
    @UseDataProvider("testOptions")
    public void restPostRequest(Serializer serializer) throws IOException {
        Request request = Request.builder()
                                 .setText("good")
                                 .build();

        HttpResponse response = factory()
                .buildPostRequest(endpoint(), new ProvidenceHttpContent(request, serializer))
                .setParser(new ProvidenceObjectParser(serializer))
                .setThrowExceptionOnExecuteError(false)
                .execute();

        assertThat(response.getStatusCode(), is(statusCode.get()));
        assertThat(response.parseAs(Response.class), is(toRespond.get()));
        assertThat(received.get(), is(request));
    }
}
