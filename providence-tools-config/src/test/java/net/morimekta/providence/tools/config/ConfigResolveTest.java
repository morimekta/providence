package net.morimekta.providence.tools.config;

import org.junit.Test;

import java.io.File;

import static net.morimekta.testing.ResourceUtils.writeContentTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test the providence converter (pvd) command.
 */
public class ConfigResolveTest extends ConfigTestBase {
    @Test
    public void testPrint_simple() {
        standardConfig();

        sut.run(
                "--rc", rc.getAbsolutePath(),
                "-I", thriftRoot.getAbsolutePath(),
                "resolve",
                configRoot.getAbsolutePath() + "/base_service.cfg",
                configRoot.getAbsolutePath() + "/prod_service.cfg");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is("tools_config.Service {\n" +
                     "  name = \"prod\"\n" +
                     "  http = {\n" +
                     "    port = 8080\n" +
                     "    context = \"/app\"\n" +
                     "  }\n" +
                     "  admin = {\n" +
                     "    port = 8088\n" +
                     "  }\n" +
                     "  db = {\n" +
                     "    uri = \"jdbc:mysql:db01:1364/my_db\"\n" +
                     "    driver = \"org.h2.Driver\"\n" +
                     "    credentials = {\n" +
                     "      username = \"dbuser\"\n" +
                     "      password = \"DbP4s5w0rD\"\n" +
                     "    }\n" +
                     "  }\n" +
                     "}\n"));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testPrint_withError() {
        standardConfig();
        writeContentTo(
                "def {\n" +
                "    http_port = 8080\n" +
                "    admin_port = 8088\n" +
                "}\n" +
                "\n" +
                "tools_config.Service {\n" +
                "    http = {\n" +
                "        port = http_porr\n" +
                "        context = \"/app\"\n" +
                "    }\n" +
                "    admin {\n" +
                "        port = def.admin_port\n" +
                "    }\n" +
                "}\n",
                new File(configRoot, "base_service.cfg"));

        sut.run("--rc", rc.getAbsolutePath(),
                "-I", thriftRoot.getAbsolutePath(),
                "resolve",
                configRoot.getAbsolutePath() + "/prod.cfg");

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(
                "Error in base_service.cfg on line 8, pos 16: No such reference 'http_porr'\n" +
                "        port = http_porr\n" +
                "---------------^^^^^^^^^\n"));
        assertThat(exitCode, is(1));
    }
}
