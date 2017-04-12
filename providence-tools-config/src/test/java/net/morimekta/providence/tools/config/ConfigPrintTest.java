package net.morimekta.providence.tools.config;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.morimekta.testing.ResourceUtils.writeContentTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test the providence converter (pvd) command.
 */
public class ConfigPrintTest extends ConfigTestBase {
    @Test
    public void testPrint_simple() throws IOException {
        standardConfig();

        sut.run(
                "--rc", rc.getAbsolutePath(),
                "-I", thriftRoot.getAbsolutePath(),
                "print",
                configRoot.getAbsolutePath() + "/prod.cfg");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is("{\n" +
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
                     "    driver = \"org.mysql.Driver\"\n" +
                     "    credentials = {\n" +
                     "      username = \"dbuser\"\n" +
                     "      password = \"DbP4s5w0rD\"\n" +
                     "    }\n" +
                     "  }\n" +
                     "}"));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testPrint_withParams() throws IOException {
        standardConfig();

        sut.run("--rc", rc.getAbsolutePath(),
                "-I", thriftRoot.getAbsolutePath(),
                "-Phttp_port=12345",
                "print",
                configRoot.getAbsolutePath() + "/prod.cfg");

        assertThat(console.error(), is(""));
        assertThat(console.output(), is("{\n" +
                     "  name = \"prod\"\n" +
                     "  http = {\n" +
                     "    port = 12345\n" +
                     "    context = \"/app\"\n" +
                     "  }\n" +
                     "  admin = {\n" +
                     "    port = 8088\n" +
                     "  }\n" +
                     "  db = {\n" +
                     "    uri = \"jdbc:mysql:db01:1364/my_db\"\n" +
                     "    driver = \"org.mysql.Driver\"\n" +
                     "    credentials = {\n" +
                     "      username = \"dbuser\"\n" +
                     "      password = \"DbP4s5w0rD\"\n" +
                     "    }\n" +
                     "  }\n" +
                     "}"));
        assertThat(exitCode, is(0));
    }


    @Test
    public void testPrint_withError() throws IOException {
        standardConfig();
        writeContentTo(
                "params {\n" +
                "    http_port = 8080\n" +
                "    admin_port = 8088\n" +
                "}\n" +
                "\n" +
                "config.Service {\n" +
                "    http = {\n" +
                "        port = params.http_porr\n" +
                "        context = \"/app\"\n" +
                "    }\n" +
                "    admin {\n" +
                "        port = params.admin_port\n" +
                "    }\n" +
                "}\n",
                new File(configRoot, "base_service.cfg"));

        sut.run("--rc", rc.getAbsolutePath(),
                "-I", thriftRoot.getAbsolutePath(),
                "-Phttp_port=12345",
                "print",
                configRoot.getAbsolutePath() + "/prod.cfg");

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(
                "Error in base_service.cfg on line 8, pos 15:\n" +
                "    Name http_porr not in params (\"params.http_porr\")\n" +
                "        port = params.http_porr\n" +
                "---------------^\n"));
        assertThat(exitCode, is(1));
    }
}
