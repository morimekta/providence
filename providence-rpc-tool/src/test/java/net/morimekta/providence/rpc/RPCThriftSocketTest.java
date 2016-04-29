package net.morimekta.providence.rpc;

import net.morimekta.providence.rpc.util.NoLogging;
import net.morimekta.test.thrift.Failure;
import net.morimekta.test.thrift.MyService;
import net.morimekta.test.thrift.Request;
import net.morimekta.test.thrift.Response;
import net.morimekta.util.io.IOUtils;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportFactory;
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
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.morimekta.providence.rpc.util.TestUtil.findFreePort;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
public class RPCThriftSocketTest {

    @Rule
    public TemporaryFolder temp;

    private OutputStream outContent;
    private OutputStream errContent;

    private static ExecutorService executor;
    private static int             port;
    private static MyService.Iface impl;
    private static TServer         server;

    private int             exitCode;
    private RPC             rpc;

    public String endpoint() {
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
                        .transportFactory(new TTransportFactory())
                        .protocolFactory(new TBinaryProtocol.Factory())
                        .processor(new MyService.Processor<>(impl)));
        server.setServerEventHandler(new TServerEventHandler() {
            @Override
            public void preServe() {

            }

            @Override
            public ServerContext createContext(TProtocol input, TProtocol output) {
                return null;
            }

            @Override
            public void deleteContext(ServerContext serverContext, TProtocol input, TProtocol output) {

            }

            @Override
            public void processContext(ServerContext serverContext,
                                       TTransport inputTransport,
                                       TTransport outputTransport) {

            }
        });
        executor = Executors.newSingleThreadExecutor();
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

        exitCode = 0;
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        rpc = new RPC() {
            @Override
            protected void exit(int i) {
                exitCode = i;
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        System.setErr(null);
        System.setOut(null);
        System.setIn(null);
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
        System.setIn(new ByteArrayInputStream(tmp.toByteArray()));

        when(impl.test(any(Request.class))).thenReturn(new Response("response"));

        rpc.run("-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                endpoint());

        assertEquals("", errContent.toString());
        assertEquals("[\n" +
                     "    \"test\",\n" +
                     "    2,\n" +
                     "    0,\n" +
                     "    {\n" +
                     "        \"success\": {\n" +
                     "            \"text\": \"response\"\n" +
                     "        }\n" +
                     "    }\n" +
                     "]", outContent.toString());
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

        rpc.run("-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                "-i", "file:" + inFile.getAbsolutePath(),
                "-o", "json,file:" + outFile.getAbsolutePath(),
                endpoint());

        assertEquals("", outContent.toString());
        assertEquals("", errContent.toString());
        assertEquals(0, exitCode);

        String out = new String(Files.readAllBytes(outFile.toPath()));
        assertEquals("[\"test\",2,0,{\"0\":{\"1\":\"response\"}}]", out);
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, TException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/req1.json")) {
            IOUtils.copy(in, tmp);
        }
        System.setIn(new ByteArrayInputStream(tmp.toByteArray()));

        when(impl.test(any(Request.class))).thenThrow(new Failure("failure"));

        rpc.run("-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                endpoint());

        assertEquals("", errContent.toString());
        assertEquals("[\n" +
                     "    \"test\",\n" +
                     "    2,\n" +
                     "    0,\n" +
                     "    {\n" +
                     "        \"f\": {\n" +
                     "            \"text\": \"failure\"\n" +
                     "        }\n" +
                     "    }\n" +
                     "]", outContent.toString());
        assertEquals(0, exitCode);
    }

    @Test
    public void testSimpleRequest_cannotConnect() throws IOException, TException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try (InputStream in = getClass().getResourceAsStream("/req1.json")) {
            IOUtils.copy(in, tmp);
        }
        System.setIn(new ByteArrayInputStream(tmp.toByteArray()));

        when(impl.test(any(Request.class))).thenReturn(new Response("failure"));

        rpc.run("-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                "thrift://localhost:" + (port - 10));

        assertEquals("", outContent.toString());
        assertEquals("Unable to connect to thrift://localhost:" + (port - 10)+ ": Connection refused\n",
                     errContent.toString());
        assertEquals(1, exitCode);
    }
}
