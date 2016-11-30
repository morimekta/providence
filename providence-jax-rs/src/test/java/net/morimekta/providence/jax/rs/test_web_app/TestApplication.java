package net.morimekta.providence.jax.rs.test_web_app;

import net.morimekta.providence.jax.rs.DefaultProvidenceMessageBodyReader;
import net.morimekta.providence.jax.rs.DefaultProvidenceMessageBodyWriter;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

/**
 * Test dropwizard application.
 */
public class TestApplication extends Application<TestConfiguration> {
    @Override
    public void run(TestConfiguration testConfiguration, Environment environment) throws Exception {
        environment.jersey().register(DefaultProvidenceMessageBodyReader.class);
        environment.jersey().register(DefaultProvidenceMessageBodyWriter.class);
        environment.jersey().register(new TestCalculatorResource(new TestCalculator()));
    }
}
