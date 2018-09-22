package net.morimekta.providence.jax.rs;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.common.collect.ImmutableList;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import net.morimekta.providence.client.ProvidenceHttpContent;
import net.morimekta.providence.client.ProvidenceObjectParser;
import net.morimekta.providence.jax.rs.test_web_app.TestApplication;
import net.morimekta.providence.jax.rs.test_web_app.TestCalculatorResource;
import net.morimekta.providence.jax.rs.test_web_app.TestNetUtil;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.FastBinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.test.providence.jax.rs.calculator.CalculateException;
import net.morimekta.test.providence.jax.rs.calculator.Operand;
import net.morimekta.test.providence.jax.rs.calculator.Operation;
import net.morimekta.test.providence.jax.rs.calculator.Operator;
import net.morimekta.test.providence.jax.rs.number.Imaginary;
import net.morimekta.util.Strings;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static net.morimekta.providence.util.ProvidenceHelper.debugString;
import static net.morimekta.test.providence.jax.rs.calculator.Operand.withImaginary;
import static net.morimekta.test.providence.jax.rs.calculator.Operand.withNumber;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Javax.WS.RS integration test_web_app using io.dropwizard.
 */
// @Ignore
@RunWith(DataProviderRunner.class)
public class JerseyServerTest {
    private static Server server;
    private static int port;

    @Before
    public void setUp() throws Exception {
        server = new Server(0);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        ServletHolder holder = handler.addServlet(ServletContainer.class, "/*");
        holder.setInitOrder(0);
        holder.setInitParameter("jersey.config.server.provider.classnames",
                                Strings.join(",",
                                             TestCalculatorResource.class.getCanonicalName()));
        holder.setInitParameter("javax.ws.rs.Application",
                                TestApplication.class.getCanonicalName());


        server.setHandler(handler);
        server.start();
        port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    @After
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @DataProvider
    public static Object[][] testOptions() {
        return new Object[][] {
                { new JsonSerializer(), },
                { new JsonSerializer().pretty(), },
                { new BinarySerializer(), },
                { new FastBinarySerializer(), },
                };
    }

    private GenericUrl uri(String service) {
        GenericUrl url = new GenericUrl(String.format(Locale.US, "http://localhost:%d/%s", port, service));;
        System.err.println(url);
        return url;
    }

    @Test
    @UseDataProvider("testOptions")
    public void testProvidence(Serializer serializer) throws IOException {
        Operation operation = new Operation(Operator.ADD,
                                            list(withNumber(52d),
                                                 withImaginary(new Imaginary(1d, -1d)),
                                                 withNumber(15d)));

        HttpResponse response = TestNetUtil
                .factory(serializer.mediaType())
                .buildPostRequest(uri("calculator/calculate"),
                                  new ProvidenceHttpContent(operation, serializer))
                .setParser(new ProvidenceObjectParser(serializer))
                .setThrowExceptionOnExecuteError(false)
                .execute();

        System.err.println(response.getStatusMessage());

        assertThat(response.getStatusCode(), is(equalTo(200)));
        assertThat(response.getHeaders().getContentType(), is(equalTo(serializer.mediaType())));
        Operand op = response.parseAs(Operand.class);

        assertThat(debugString(op), is(equalTo(
                "{\n" +
                "  imaginary = {\n" +
                "    v = 68\n" +
                "    i = -1\n" +
                "  }\n" +
                "}")));
    }

    @Test
    @UseDataProvider("testOptions")
    public void testProvidence_Exception(Serializer serializer) throws IOException {
        Operation operation = new Operation(Operator.MULTIPLY,
                                            list(withNumber(52d),
                                                 withImaginary(new Imaginary(1d, -1d)),
                                                 withNumber(15d)));

        HttpResponse response = TestNetUtil
                .factory(serializer.mediaType())
                .buildPostRequest(uri("calculator/calculate"),
                                  new ProvidenceHttpContent(operation, serializer))
                .setThrowExceptionOnExecuteError(false)
                .setParser(new ProvidenceObjectParser(serializer))
                .execute();

        assertThat(response.getStatusCode(), is(equalTo(400)));
        assertThat(response.getHeaders().getContentType(), is(equalTo(serializer.mediaType())));
        CalculateException ex = response.parseAs(CalculateException.class);

        assertEquals(
                "{\n" +
                "  message = \"Unsupported operation: MULTIPLY\"\n" +
                "  operation = {\n" +
                "    operator = MULTIPLY\n" +
                "    operands = [\n" +
                "      {\n" +
                "        number = 52\n" +
                "      },\n" +
                "      {\n" +
                "        imaginary = {\n" +
                "          v = 1\n" +
                "          i = -1\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        number = 15\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}", debugString(ex));
    }

    @SafeVarargs
    private static <T> List<T> list(T... items) {
        return ImmutableList.copyOf(items);
    }
}
