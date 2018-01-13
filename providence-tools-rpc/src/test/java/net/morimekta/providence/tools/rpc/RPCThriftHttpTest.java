package net.morimekta.providence.tools.rpc;

import net.morimekta.providence.tools.rpc.internal.NoLogging;
import net.morimekta.test.thrift.Failure;
import net.morimekta.test.thrift.MyService;
import net.morimekta.test.thrift.Request;
import net.morimekta.test.thrift.Response;
import net.morimekta.testing.rules.ConsoleWatcher;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static j2html.TagCreator.body;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.span;
import static j2html.TagCreator.title;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.morimekta.providence.tools.rpc.internal.TestNetUtil.getExposedPort;
import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static net.morimekta.testing.ResourceUtils.getResourceAsBytes;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
public class RPCThriftHttpTest {
    private static final String ENDPOINT = "/test";
    private static final String HTML_ENDPOINT = "/html";
    private static final String JSON_ENDPOINT = "/json";

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ConsoleWatcher console = new ConsoleWatcher();

    private File            rc;
    private int             port;
    private Server          server;
    private MyService.Iface impl;
    private RPC             rpc;
    private int             exitCode;

    private String endpoint(String path) {
        return "http://localhost:" + port + path;
    }

    @Before
    public void setUp() throws Exception {
        Log.setLog(new NoLogging());

        rc = copyResourceTo("/pvdrc", temp.getRoot());
        copyResourceTo("/test.thrift", temp.getRoot());

        impl = mock(MyService.Iface.class);

        server = new Server(port);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new TServlet(new MyService.Processor<>(impl),
                                                          new TBinaryProtocol.Factory(true, true))), ENDPOINT);
        handler.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(
                        html(head(title("Fail!")),
                             body(h1("Fail!"),
                                  span("Truly failure"))).render());
            }
        }), HTML_ENDPOINT);
        handler.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"error\":\"Fail!\"}");
            }
        }), JSON_ENDPOINT);

        server.setHandler(handler);
        server.start();
        port = getExposedPort(server);
        Thread.sleep(1);

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
                "-i", "json",
                "-o", "pretty_json",
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                endpoint(ENDPOINT));

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
                endpoint(ENDPOINT));

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(""));
        assertThat(exitCode, is(0));

        String out = new String(Files.readAllBytes(outFile.toPath()), UTF_8);
        assertThat(out, is("[\"test\",2,44,{\"0\":{\"1\":\"response\"}}]\n"));
    }

    @Test
    public void testSimpleRequest_exception() throws IOException, TException {
        console.setInput(getResourceAsBytes("/req1.json"));

        when(impl.test(any(Request.class))).thenThrow(new Failure("failure"));

        rpc.run("--rc", rc.getAbsolutePath(),
                "-i", "json",
                "-o", "pretty_json",
                "-I", temp.getRoot().getAbsolutePath(),
                "-s", "test.MyService",
                endpoint(ENDPOINT));

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
    public void testSimpleRequest_404() throws IOException, TException {
        console.setInput(getResourceAsBytes("/req1.json"));

        when(impl.test(any(Request.class))).thenThrow(new Failure("failure"));

        rpc.run("--rc", rc.getAbsolutePath(),
                "-i", "json",
                "-I", temp.getRoot().getAbsolutePath(),
                "-o", "binary",
                "-s", "test.MyService",
                endpoint(ENDPOINT) + "_does_not_exsist");

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(
                "Received 405 HTTP method POST is not supported by this URL\n" +
                " - from: http://localhost:" + port + "/test_does_not_exsist\n"));
        assertThat(exitCode, is(not(0)));
    }

    @Test
    public void testSimpleRequest_respondWithHtml() throws IOException, TException {
        console.setInput(getResourceAsBytes("/req1.json"));

        when(impl.test(any(Request.class))).thenThrow(new Failure("failure"));

        rpc.run("--rc", rc.getAbsolutePath(),
                "-i", "json",
                "-I", temp.getRoot().getAbsolutePath(),
                "-o", "binary",
                "-s", "test.MyService",
                endpoint(HTML_ENDPOINT));

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(
                "Serializer error: Error: Received HTML in service call\n"));
        assertThat(exitCode, is(not(0)));
    }


    @Test
    public void testSimpleRequest_respondWithJson() throws IOException, TException {
        console.setInput(getResourceAsBytes("/req1.json"));

        when(impl.test(any(Request.class))).thenThrow(new Failure("failure"));

        rpc.run("--rc", rc.getAbsolutePath(),
                "-I", temp.getRoot().getAbsolutePath(),
                "-o", "binary",
                "-s", "test.MyService",
                endpoint(JSON_ENDPOINT));

        assertThat(console.output(), is(""));
        assertThat(console.error(), is(
                "Serializer error: Error: Received JSON in service call\n"));
        assertThat(exitCode, is(not(0)));
    }
}
