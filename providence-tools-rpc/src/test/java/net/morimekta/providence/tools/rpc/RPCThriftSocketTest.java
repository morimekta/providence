package net.morimekta.providence.tools.rpc;

import net.morimekta.providence.tools.rpc.internal.NoLogging;
import net.morimekta.test.thrift.Failure;
import net.morimekta.test.thrift.MyService;
import net.morimekta.test.thrift.Request;
import net.morimekta.test.thrift.Response;
import net.morimekta.testing.rules.ConsoleWatcher;

import org.apache.commons.codec.DecoderException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.eclipse.jetty.util.log.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static net.morimekta.testing.ResourceUtils.getResourceAsBytes;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
public class RPCThriftSocketTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ConsoleWatcher console = new ConsoleWatcher();

    private File            rc;
    private int             port;
    private TServer         server;
    private MyService.Iface impl;
    private RPC             rpc;
    private int             exitCode;

    private String endpoint() {
        return "thrift://localhost:" + port;
    }

    @Before
    public void setUp() throws Exception {
        Log.setLog(new NoLogging());

        rc = copyResourceTo("/pvdrc", temp.getRoot());
        copyResourceTo("/test.thrift", temp.getRoot());

        impl = Mockito.mock(MyService.Iface.class);

        TServerSocket transport = new TServerSocket(0);
        server = new TSimpleServer(
                new TServer.Args(transport)
                        .protocolFactory(new TBinaryProtocol.Factory())
                        .processor(new MyService.Processor<>(impl)));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(server::serve);
        Thread.sleep(1);
        port = transport.getServerSocket().getLocalPort();

        exitCode = 0;
        rpc = new RPC(console.tty()) {
            @Override
            protected void exit(int i) {
                exitCode = i;
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSimpleRequest() throws IOException, TException {
        console.setInput(getResourceAsBytes("/req1.json"));

        when(impl.test(any(Request.class))).thenReturn(new Response("response"));

        rpc.run("--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                endpoint());

        verify(impl).test(any(Request.class));

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(
                "[\n" +
                "    \"test\",\n" +
                "    \"reply\",\n" +
                "    44,\n" +
                "    {\n" +
                "        \"success\": {\n" +
                "            \"text\": \"response\"\n" +
                "        }\n" +
                "    }\n" +
                "]\n"));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testSimpleRequest_FileIO() throws IOException, TException {
        File inFile = copyResourceTo("/req1.json", temp.getRoot());
        File outFile = temp.newFile();

        when(impl.test(any(Request.class))).thenReturn(new Response("response"));

        rpc.run("--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                "-i", "file:" + inFile.getAbsolutePath(),
                "-o", "json,file:" + outFile.getAbsolutePath(),
                endpoint());

        verify(impl).test(any(Request.class));

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(""));
        assertThat(exitCode, is(0));

        String out = new String(Files.readAllBytes(outFile.toPath()), UTF_8);
        assertThat(out, is("[\"test\",2,44,{\"0\":{\"1\":\"response\"}}]"));
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, TException {
        console.setInput(getResourceAsBytes("/req1.json"));

        when(impl.test(any(Request.class))).thenThrow(new Failure("failure"));

        rpc.run("--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                endpoint());

        verify(impl).test(any(Request.class));

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(
                "[\n" +
                "    \"test\",\n" +
                "    \"reply\",\n" +
                "    44,\n" +
                "    {\n" +
                "        \"f\": {\n" +
                "            \"text\": \"failure\"\n" +
                "        }\n" +
                "    }\n" +
                "]\n"));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testSimpleRequest_wrongMethod() throws IOException, TException, DecoderException {
        byte[] tmp = ("[\n" +
                      "    \"testing\",\n" +
                      "    \"call\",\n" +
                      "    44,\n" +
                      "    {\n" +
                      "        \"request\": {\n" +
                      "            \"text\": \"request\"\n" +
                      "        }\n" +
                      "    }\n" +
                      "]").getBytes(UTF_8);
        console.setInput(tmp);

        when(impl.test(any(Request.class))).thenThrow(new Failure("failure"));

        rpc.run("--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService2",
                endpoint());

        verifyZeroInteractions(impl);

        assertThat(console.error(), is(""));
        assertThat(console.output(), is(
                "[\n" +
                "    \"testing\",\n" +
                "    \"exception\",\n" +
                "    44,\n" +
                "    {\n" +
                "        \"message\": \"Invalid method name: 'testing'\",\n" +
                "        \"id\": \"UNKNOWN_METHOD\"\n" +
                "    }\n" +
                "]\n"));
        assertThat(exitCode, is(0));
    }

    @Test
    public void testSimpleRequest_cannotConnect() throws IOException, TException {
        console.setInput(getResourceAsBytes("/req1.json"));

        when(impl.test(any(Request.class))).thenReturn(new Response("failure"));

        rpc.run("--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                "thrift://localhost:" + (port - 10));

        verifyZeroInteractions(impl);

        assertThat(console.output(), is(""));
        assertThat(console.error(),
                   startsWith("Unable to connect to thrift://localhost:" + (port - 10) +
                              ": Connection refused"));
        assertThat(exitCode, is(1));
    }
}
