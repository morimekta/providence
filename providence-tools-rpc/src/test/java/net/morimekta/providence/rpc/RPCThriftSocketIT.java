package net.morimekta.providence.rpc;

import net.morimekta.providence.rpc.util.NoLogging;
import net.morimekta.test.thrift.Failure;
import net.morimekta.test.thrift.MyService;
import net.morimekta.test.thrift.Request;
import net.morimekta.test.thrift.Response;
import net.morimekta.testing.IntegrationExecutor;
import net.morimekta.util.io.IOUtils;

import org.apache.commons.codec.DecoderException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.eclipse.jetty.util.log.Log;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.morimekta.providence.rpc.util.TestUtil.findFreePort;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
public class RPCThriftSocketIT {
    private TemporaryFolder temp;

    private static int             port;
    private static MyService.Iface impl;
    private static TServer         server;

    private IntegrationExecutor rpc;

    private String endpoint() {
        return "thrift://localhost:" + port;
    }

    @BeforeClass
    public static void setUpServer() throws Exception {
        Log.setLog(new NoLogging());

        port = findFreePort();
        impl = Mockito.mock(MyService.Iface.class);

        TServerSocket transport = new TServerSocket(port);
        server = new TSimpleServer(
                new TServer.Args(transport)
                        .protocolFactory(new TBinaryProtocol.Factory())
                        .processor(new MyService.Processor<>(impl)));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(server::serve);
        Thread.sleep(1);
    }

    @Before
    public void setUp() throws Exception {
        reset(impl);

        temp = new TemporaryFolder();
        temp.create();

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
    public void testSimpleRequest() throws IOException, TException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/req1.json")) {
            IOUtils.copy(in, tmp);
        }
        rpc.setInput(new ByteArrayInputStream(tmp.toByteArray()));

        when(impl.test(any(Request.class))).thenReturn(new Response("response"));

        int exitCode = rpc.run(
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                endpoint());

        verify(impl).test(any(Request.class));

        assertEquals("", rpc.getError());
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
        assertEquals(0, exitCode);
    }

    @Test
    public void testSimpleRequest_FileIO() throws IOException, TException {
        File inFile = temp.newFile();
        File outFile = temp.newFile();

        try (InputStream in = getClass().getResourceAsStream("/req1.json");
             FileOutputStream fos = new FileOutputStream(inFile)) {
            IOUtils.copy(in, fos);
            fos.flush();
        }

        when(impl.test(any(Request.class))).thenReturn(new Response("response"));

        int exitCode = rpc.run(
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                "-i", "file:" + inFile.getAbsolutePath(),
                "-o", "json,file:" + outFile.getAbsolutePath(),
                endpoint());

        verify(impl).test(any(Request.class));

        assertEquals("", rpc.getOutput());
        assertEquals("", rpc.getError());
        assertEquals(0, exitCode);

        String out = new String(Files.readAllBytes(outFile.toPath()));
        assertEquals("[\"test\",2,44,{\"0\":{\"1\":\"response\"}}]", out);
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, TException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/req1.json")) {
            IOUtils.copy(in, tmp);
        }
        rpc.setInput(new ByteArrayInputStream(tmp.toByteArray()));

        when(impl.test(any(Request.class))).thenThrow(new Failure("failure"));

        int exitCode = rpc.run(
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                endpoint());

        verify(impl).test(any(Request.class));

        assertEquals("", rpc.getError());
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
        assertEquals(0, exitCode);
    }

    @Test
    public void testSimpleRequest_wrongMethod() throws IOException, TException, DecoderException {
        byte[] tmp = ("[\n" +
                      "    \"testing\",\n" +
                      "    \"call\",\n" +
                      "    0,\n" +
                      "    {\n" +
                      "        \"request\": {\n" +
                      "            \"text\": \"request\"\n" +
                      "        }\n" +
                      "    }\n" +
                      "]").getBytes(UTF_8);
        rpc.setInput(new ByteArrayInputStream(tmp));

        when(impl.test(any(Request.class))).thenThrow(new Failure("failure"));

        int exitCode = rpc.run(
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService2",
                endpoint());

        verifyZeroInteractions(impl);

        assertEquals("", rpc.getError());
        assertEquals("[\n" +
                     "    \"testing\",\n" +
                     "    \"exception\",\n" +
                     "    0,\n" +
                     "    {\n" +
                     "        \"message\": \"Invalid method name: 'testing'\",\n" +
                     "        \"id\": \"UNKNOWN_METHOD\"\n" +
                     "    }\n" +
                     "]\n", rpc.getOutput());
        assertEquals(0, exitCode);
    }

    @Test
    public void testSimpleRequest_cannotConnect() throws IOException, TException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/req1.json")) {
            IOUtils.copy(in, tmp);
        }
        rpc.setInput(new ByteArrayInputStream(tmp.toByteArray()));

        when(impl.test(any(Request.class))).thenReturn(new Response("failure"));

        int exitCode = rpc.run(
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                "thrift://localhost:" + (port - 10));

        verifyZeroInteractions(impl);

        assertEquals("", rpc.getOutput());
        assertThat(rpc.getError(),
                   startsWith("Unable to connect to thrift://localhost:" + (port - 10)+ ": Connection refused"));
        assertEquals(1, exitCode);
    }
}
