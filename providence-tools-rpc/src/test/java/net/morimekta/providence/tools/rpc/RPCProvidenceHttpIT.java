package net.morimekta.providence.tools.rpc;

import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.server.ProvidenceServlet;
import net.morimekta.providence.tools.rpc.internal.NoLogging;
import net.morimekta.test.providence.Failure;
import net.morimekta.test.providence.MyService;
import net.morimekta.test.providence.Request;
import net.morimekta.test.providence.Response;
import net.morimekta.testing.IntegrationExecutor;
import net.morimekta.testing.ResourceUtils;
import net.morimekta.util.io.IOUtils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static net.morimekta.providence.tools.rpc.internal.TestNetUtil.getExposedPort;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
public class RPCProvidenceHttpIT {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private IntegrationExecutor rpc;
    private File rc;

    private static int             port;
    private static MyService.Iface impl;
    private static Server          server;

    private static final String ENDPOINT = "test";

    private String endpoint() {
        return "http://localhost:" + port + "/" + ENDPOINT;
    }

    @BeforeClass
    public static void setUpServer() throws Exception {
        Log.setLog(new NoLogging());

        impl = Mockito.mock(MyService.Iface.class);

        server = new Server(0);
        DefaultSerializerProvider provider = new DefaultSerializerProvider();

        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new ProvidenceServlet(new MyService.Processor(impl), provider)),
                           "/" + ENDPOINT);

        server.setHandler(handler);
        server.start();
        port = getExposedPort(server);
        Thread.sleep(1);
    }

    @Before
    public void setUp() throws Exception {
        reset(impl);

        rc = ResourceUtils.copyResourceTo("/pvdrc", temp.getRoot());

        File thriftFile = temp.newFile("test.thrift");

        FileOutputStream file = new FileOutputStream(thriftFile);
        IOUtils.copy(getClass().getResourceAsStream("/test.thrift"), file);
        file.flush();
        file.close();

        rpc = new IntegrationExecutor("providence-tools-rpc", "providence-tools-rpc.jar");
    }

    @After
    public void tearDown() throws Exception {
        temp.delete();
    }

    @AfterClass
    public static void tearDownServer() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSimpleRequest() throws IOException, Failure {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/req1.json")) {
            IOUtils.copy(in, tmp);
        }
        rpc.setInput(new ByteArrayInputStream(tmp.toByteArray()));

        when(impl.test(any(Request.class))).thenReturn(new Response("response"));

        int exitCode = rpc.run(
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                endpoint());

        assertEquals("[\n" +
                     "    \"test\",\n" +
                     "    \"reply\",\n" +
                     "    44,\n" +
                     "    {\n" +
                     "        \"success\": {\n" +
                     "            \"text\": \"response\"\n" +
                     "        }\n" +
                     "    }\n" +
                     "]\n", rpc.getOutput());
        assertEquals("", rpc.getError());
        assertEquals(0, exitCode);
    }

    @Test
    public void testSimpleRequest_FileIO() throws IOException, Failure {
        File inFile = temp.newFile();
        File outFile = temp.newFile();

        try (InputStream in = getClass().getResourceAsStream("/req1.json");
             FileOutputStream fos = new FileOutputStream(inFile)) {
            IOUtils.copy(in, fos);
            fos.flush();
        }

        when(impl.test(any(Request.class))).thenReturn(new Response("response"));

        int exitCode = rpc.run(
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                "-i", "file:" + inFile.getAbsolutePath(),
                "-o", "json,file:" + outFile.getAbsolutePath(),
                endpoint());

        assertEquals("", rpc.getOutput());
        assertEquals("", rpc.getError());

        String out = new String(Files.readAllBytes(outFile.toPath()));
        assertEquals("[\"test\",2,44,{\"0\":{\"1\":\"response\"}}]", out);

        assertEquals(0, exitCode);
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, Failure {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/req1.json")) {
            IOUtils.copy(in, tmp);
        }
        rpc.setInput(new ByteArrayInputStream(tmp.toByteArray()));

        when(impl.test(any(Request.class))).thenThrow(Failure.builder().setText("failure").build());

        int exitCode = rpc.run(
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                endpoint());

        assertEquals("[\n" +
                     "    \"test\",\n" +
                     "    \"reply\",\n" +
                     "    44,\n" +
                     "    {\n" +
                     "        \"f\": {\n" +
                     "            \"text\": \"failure\"\n" +
                     "        }\n" +
                     "    }\n" +
                     "]\n", rpc.getOutput());
        assertEquals("", rpc.getError());
        assertEquals(0, exitCode);
    }

    @Test
    public void testSimpleRequest_404() throws IOException, Failure {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/req1.json")) {
            IOUtils.copy(in, tmp);
        }
        rpc.setInput(new ByteArrayInputStream(tmp.toByteArray()));

        when(impl.test(any(Request.class))).thenThrow(Failure.builder().setText("failure").build());

        int exitCode = rpc.run(
                "--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                endpoint() + "_does_not_exsist");

        assertEquals("", rpc.getOutput());
        assertEquals("Received 405 HTTP method POST is not supported by this URL\n" +
                     " - from: http://localhost:" + port + "/test_does_not_exsist\n",
                     rpc.getError());
        assertEquals(1, exitCode);
    }
}
