package net.morimekta.providence.jax.rs.test_web_app;

import net.morimekta.providence.jax.rs.DefaultProvidenceMessageBodyReader;
import net.morimekta.providence.jax.rs.DefaultProvidenceMessageBodyWriter;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.server.ProvidenceServlet;
import net.morimekta.test.calculator.Calculator;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Test dropwizard application.
 */
public class TestApplication extends Application<TestConfiguration> {
    @Override
    public void run(TestConfiguration testConfiguration, Environment environment) throws Exception {
        Calculator.Iface impl = new TestCalculator();

        environment.jersey().register(DefaultProvidenceMessageBodyReader.class);
        environment.jersey().register(DefaultProvidenceMessageBodyWriter.class);
        environment.jersey().register(new TestCalculatorResource(impl));
        environment.getApplicationContext().addServlet(
                new ServletHolder(new ProvidenceServlet(new Calculator.Processor(impl), new DefaultSerializerProvider())),
                "/test");
    }
}
