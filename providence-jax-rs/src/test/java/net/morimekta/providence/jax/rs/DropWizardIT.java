package net.morimekta.providence.jax.rs;

import net.morimekta.providence.jax.rs.test_web_app.TestApplication;
import net.morimekta.providence.jax.rs.test_web_app.TestConfiguration;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.test.calculator.CalculateException;
import net.morimekta.test.calculator.Operand;
import net.morimekta.test.calculator.Operation;
import net.morimekta.test.calculator.Operator;
import net.morimekta.test.number.Imaginary;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static net.morimekta.providence.util.PrettyPrinter.debugString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Javax.WE.RS integration testing using io.dropwizard.
 */
public class DropWizardIT {
    @Rule
    public DropwizardAppRule<TestConfiguration> drop_wizard =
        new DropwizardAppRule<>(
                TestApplication.class,
                ResourceHelpers.resourceFilePath("test-app-config.yaml"));

    private String uri(String service) {
        return String.format("http://localhost:%d/calculator/%s", drop_wizard.getLocalPort(), service);
    }

    @Test
    public void testProvidenceJson() throws IOException {
        Client client = new JerseyClientBuilder(drop_wizard.getEnvironment()).build("");

        Response response = client.target(uri("calculate"))
                                  .register(DefaultProvidenceMessageBodyWriter.class)
                                  .register(DefaultProvidenceMessageBodyReader.class)
                                  .request()
                                  // note: we cannot use "application/json" as jackson will be chosen as serializer and make the content fail...
                                  .accept(JsonSerializer.MIME_TYPE)
                                  .post(Entity.entity(Operation.builder()
                                                               .setOperator(Operator.ADD)
                                                               .addToOperands(Operand.withNumber(52d),
                                                                              Operand.withImaginary(new Imaginary(1d, -1d)),
                                                                              Operand.withNumber(15d))
                                                               .build(),
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
        Client client = new JerseyClientBuilder(drop_wizard.getEnvironment()).build("");

        Response response = client.target(uri("calculate"))
                                  .register(DefaultProvidenceMessageBodyWriter.class)
                                  .register(DefaultProvidenceMessageBodyReader.class)
                                  .request()
                                  .accept(BinarySerializer.MIME_TYPE)
                                  .post(Entity.entity(Operation.builder()
                                                               .setOperator(Operator.ADD)
                                                               .addToOperands(Operand.withNumber(52d),
                                                                              Operand.withImaginary(new Imaginary(1d, -1d)),
                                                                              Operand.withNumber(15d))
                                                               .build(),
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
        Client client = new JerseyClientBuilder(drop_wizard.getEnvironment()).build("");

        Response response = client.target(uri("calculate"))
                                  .register(DefaultProvidenceMessageBodyWriter.class)
                                  .register(DefaultProvidenceMessageBodyReader.class)
                                  .request()
                                  // note: we cannot use "application/json" as jackson will be chosen as serializer and make the content fail...
                                  .accept(JsonSerializer.MIME_TYPE)
                                  .post(Entity.entity(Operation.builder()
                                                               .setOperator(Operator.MULTIPLY)
                                                               .addToOperands(Operand.withNumber(52d),
                                                                              Operand.withImaginary(new Imaginary(1d, -1d)),
                                                                              Operand.withNumber(15d))
                                                               .build(),
                                                      // same problem as with accept.
                                                      JsonSerializer.MIME_TYPE));

        assertThat(response.getStatus(), is(equalTo(400)));
        assertThat(response.getHeaders().getFirst("Content-Type"), is(equalTo(JsonSerializer.MIME_TYPE)));
        CalculateException ex = response.readEntity(CalculateException.class);

        assertThat(debugString(ex), is(equalTo(
                "message = \"Unsupported operation: MULTIPLY\"\n" +
                "operation = {\n" +
                "  operator = MULTIPLY\n" +
                "  operands = {\n" +
                "    number = 52\n" +
                "  }\n" +
                "  operands = {\n" +
                "    imaginary = {\n" +
                "      v = 1\n" +
                "      i = -1\n" +
                "    }\n" +
                "  }\n" +
                "  operands = {\n" +
                "    number = 15\n" +
                "  }\n" +
                "}")));

    }
}
