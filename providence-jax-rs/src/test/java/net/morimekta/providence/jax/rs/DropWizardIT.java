package net.morimekta.providence.jax.rs;

import net.morimekta.providence.client.HttpClientHandler;
import net.morimekta.providence.jax.rs.test_web_app.TestApplication;
import net.morimekta.providence.jax.rs.test_web_app.TestConfiguration;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.test.providence.jax.rs.calculator.CalculateException;
import net.morimekta.test.providence.jax.rs.calculator.Calculator;
import net.morimekta.test.providence.jax.rs.calculator.Operand;
import net.morimekta.test.providence.jax.rs.calculator.Operation;
import net.morimekta.test.providence.jax.rs.calculator.Operator;
import net.morimekta.test.providence.jax.rs.number.Imaginary;

import com.google.api.client.http.GenericUrl;
import com.google.common.collect.ImmutableList;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static net.morimekta.providence.jax.rs.test_web_app.TestNetUtil.factory;
import static net.morimekta.providence.util.PrettyPrinter.debugString;
import static net.morimekta.test.providence.jax.rs.calculator.Operand.withImaginary;
import static net.morimekta.test.providence.jax.rs.calculator.Operand.withNumber;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Javax.WS.RS integration testing using io.dropwizard.
 */
@Ignore
public class DropWizardIT {
    @Rule
    public DropwizardAppRule<TestConfiguration> drop_wizard =
        new DropwizardAppRule<>(
                TestApplication.class,
                ResourceHelpers.resourceFilePath("test-app-config.yaml"),
                ConfigOverride.config("server.applicationConnectors[0].port", "0"));

    private String uri(String service) {
        return String.format("http://localhost:%d/%s", drop_wizard.getLocalPort(), service);
    }

    @Test
    public void testProvidenceJson() throws IOException {
        Client client = new JerseyClientBuilder(drop_wizard.getEnvironment()).build("test-json");

        Response response = client.target(uri("calculator/calculate"))
                                  .register(DefaultProvidenceMessageBodyWriter.class)
                                  .register(DefaultProvidenceMessageBodyReader.class)
                                  .request()
                                  // note: we cannot use "application/json" as jackson will be chosen as serializer and make the content fail...
                                  .accept(JsonSerializer.MIME_TYPE)
                                  .post(Entity.entity(new Operation(Operator.ADD,
                                                                    list(withNumber(52d),
                                                                         withImaginary(new Imaginary(1d, -1d)),
                                                                         withNumber(15d))),
                                                      // same problem as with accept.
                                                      JsonSerializer.MIME_TYPE));

        assertThat(response.getStatus(), is(equalTo(200)));
        assertThat(response.getHeaders().getFirst("Content-Type"), is(equalTo(JsonSerializer.MIME_TYPE)));
        Operand op = response.readEntity(Operand.class);

        assertThat(debugString(op), is(equalTo(
                "imaginary = {\n" +
                "  v = 68\n" +
                "  i = -1\n" +
                "}")));
    }

    @Test
    public void testProvidenceBinary() throws IOException {
        Client client = new JerseyClientBuilder(drop_wizard.getEnvironment()).build("test-binary");

        Response response = client.target(uri("calculator/calculate"))
                                  .register(DefaultProvidenceMessageBodyWriter.class)
                                  .register(DefaultProvidenceMessageBodyReader.class)
                                  .request()
                                  .accept(BinarySerializer.MIME_TYPE)
                                  .post(Entity.entity(new Operation(Operator.ADD,
                                                                    list(withNumber(52d),
                                                                         withImaginary(new Imaginary(1d, -1d)),
                                                                         withNumber(15d))),
                                                      BinarySerializer.MIME_TYPE));

        assertThat(response.getStatus(), is(equalTo(200)));
        assertThat(response.getHeaders().getFirst("Content-Type"), is(equalTo(BinarySerializer.MIME_TYPE)));
        Operand op = response.readEntity(Operand.class);

        assertThat(debugString(op), is(equalTo(
                "imaginary = {\n" +
                "  v = 68\n" +
                "  i = -1\n" +
                "}")));
    }

    @Test
    public void testProvidenceJson_exception() throws IOException {
        Client client = new JerseyClientBuilder(drop_wizard.getEnvironment()).build("test-json-exception");

        Response response = client.target(uri("calculator/calculate"))
                                  .register(DefaultProvidenceMessageBodyWriter.class)
                                  .register(DefaultProvidenceMessageBodyReader.class)
                                  .request()
                                  // note: we cannot use "application/json" as jackson will be chosen as serializer and make the content fail...
                                  .accept(JsonSerializer.MIME_TYPE)
                                  .post(Entity.entity(new Operation(Operator.MULTIPLY,
                                                                    list(withNumber(52d),
                                                                         withImaginary(new Imaginary(1d, -1d)),
                                                                         withNumber(15d))),
                                                      // same problem as with accept.
                                                      JsonSerializer.MIME_TYPE));

        assertThat(response.getStatus(), is(equalTo(400)));
        assertThat(response.getHeaders().getFirst("Content-Type"), is(equalTo(JsonSerializer.MIME_TYPE)));
        CalculateException ex = response.readEntity(CalculateException.class);

        assertEquals(
                "message = \"Unsupported operation: MULTIPLY\"\n" +
                "operation = {\n" +
                "  operator = MULTIPLY\n" +
                "  operands = [\n" +
                "    {\n" +
                "      number = 52\n" +
                "    },\n" +
                "    {\n" +
                "      imaginary = {\n" +
                "        v = 1\n" +
                "        i = -1\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      number = 15\n" +
                "    }\n" +
                "  ]\n" +
                "}", debugString(ex));
    }

    @Test
    public void testProvidenceServlet() throws IOException, CalculateException {
        // This test is just to prove that the providence servlet can be used in dropwizard too.
        Calculator.Iface client = new Calculator.Client(new HttpClientHandler(
                () -> new GenericUrl(uri("test")),
                factory(),
                new DefaultSerializerProvider()
        ));

        Operand result = client.calculate(
                new Operation(Operator.ADD,
                              list(withNumber(52d),
                                   withImaginary(new Imaginary(1d, -1d)),
                                   withNumber(15d))));

        assertThat(debugString(result), is(equalTo(
                "imaginary = {\n" +
                "  v = 68\n" +
                "  i = -1\n" +
                "}")));
    }

    @SafeVarargs
    private static <T> List<T> list(T... items) {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (T item : items) {
            builder.add(item);
        }
        return builder.build();
    }
}
