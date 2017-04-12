package net.morimekta.providence.tools.config;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test the providence converter (pvd) command.
 */
public class ConfigParamsTest extends ConfigTestBase {
    @Test
    public void testParams() throws IOException {
        standardConfig();

        sut.run("--rc", rc.getAbsolutePath(),
                "-I", thriftRoot.getAbsolutePath(),
                "-Phttp_port=12345",
                "params",
                configRoot.getAbsolutePath() + "/prod.cfg");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(
                "admin_port = 8088 (base_service.cfg)\n" +
                "http_port = 8080 (base_service.cfg)\n"));
        assertThat(exitCode, is(0));
    }
}
