package net.morimekta.providence.jax.rs.test_web_app;

import net.morimekta.providence.jax.rs.ProvidenceFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Test application implementation for jersey test app.
 */
public class TestApplication extends ResourceConfig {
    public TestApplication() {
        register(ProvidenceFeature.class);
        setApplicationName("Calculator");
    }
}
