package net.morimekta.providence.tools.config;

import net.morimekta.testing.IntegrationExecutor;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static net.morimekta.testing.ResourceUtils.writeContentTo;
import static org.junit.Assert.assertEquals;

/**
 * Test the providence converter (pvd) command.
 */
public class ConfigIT {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private IntegrationExecutor config;
    private File configRoot;
    private File thriftRoot;
    private File rc;

    @Before
    public void setUp() throws IOException {
        rc = copyResourceTo("/pvdrc", temp.getRoot());
        configRoot = temp.newFolder("config");
        thriftRoot = temp.newFolder("providence");

        config = new IntegrationExecutor("providence-tools-config", "providence-tools-config.jar");
        // 1 second deadline.
        config.setDeadlineMs(1000L);
    }

    @After
    public void tearDown() {
        temp.delete();
    }

    private void standardConfig() {
        copyResourceTo("/config.thrift", thriftRoot);

        String path = "/net/morimekta/providence/tools/config/";
        copyResourceTo(path + "base_service.cfg", configRoot);
        copyResourceTo(path + "prod.cfg", configRoot);
        copyResourceTo(path + "prod_db.cfg", configRoot);
        copyResourceTo(path + "stage.cfg", configRoot);
        copyResourceTo(path + "stage_db.cfg", configRoot);
    }

    @Test
    public void testPrint_simple() throws IOException {
        standardConfig();

        int exitCode = config.run(
                "--rc", rc.getAbsolutePath(),
                "-I", thriftRoot.getAbsolutePath(),
                "print",
                configRoot.getAbsolutePath() + "/prod.cfg");

        assertEquals("", config.getError());
        assertEquals("{\n" +
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
                     "}", config.getOutput());
        assertEquals(0, exitCode);
    }

    @Test
    public void testPrint_withParams() throws IOException {
        standardConfig();

        int exitCode = config.run(
                "--rc", rc.getAbsolutePath(),
                "-I", thriftRoot.getAbsolutePath(),
                "-Phttp_port=12345",
                "print",
                configRoot.getAbsolutePath() + "/prod.cfg");

        assertEquals("", config.getError());
        assertEquals("{\n" +
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
                     "}", config.getOutput());
        assertEquals(0, exitCode);
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

        int exitCode = config.run(
                "--rc", rc.getAbsolutePath(),
                "-I", thriftRoot.getAbsolutePath(),
                "-Phttp_port=12345",
                "print",
                configRoot.getAbsolutePath() + "/prod.cfg");

        assertEquals("", config.getOutput());
        assertEquals("Error in base_service.cfg on line 8, pos 15:\n" +
                     "    Name http_porr not in params (\"params.http_porr\")\n" +
                     "        port = params.http_porr\n" +
                     "---------------^\n", config.getError());
        assertEquals(1, exitCode);
    }

    @Test
    public void testParams() throws IOException {
        standardConfig();

        int exitCode = config.run(
                "--rc", rc.getAbsolutePath(),
                "-I", thriftRoot.getAbsolutePath(),
                "-Phttp_port=12345",
                "params",
                configRoot.getAbsolutePath() + "/prod.cfg");

        assertEquals("", config.getError());
        assertEquals("admin_port = 8088 (base_service.cfg)\n" +
                     "http_port = 8080 (base_service.cfg)\n",
                     config.getOutput());
        assertEquals(0, exitCode);
    }
}
