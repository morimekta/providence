package net.morimekta.providence.client;

import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.SerializerProvider;
import net.morimekta.test.providence.client.Failure;
import net.morimekta.test.providence.client.Request;
import net.morimekta.test.providence.client.TestService;
import net.morimekta.util.Strings;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test that we can connect to a thrift servlet and get reasonable input and output.
 */
public class HttpClientHandlerNetworkTest {
    private static final String ENDPOINT = "test";

    private int port;
    private ExecutorService executorService;
    private SerializerProvider provider;
    private ServerSocket serverSocket;

    private AtomicBoolean flag;

    private GenericUrl endpoint() {
        return new GenericUrl("http://localhost:" + port + "/" + ENDPOINT);
    }

    @Before
    public void setUp() throws Exception {
        executorService = Executors.newSingleThreadExecutor();
        provider = new DefaultSerializerProvider();

        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("localhost", 0));
        port = serverSocket.getLocalPort();

        serverSocket.setSoTimeout(200);
        flag = new AtomicBoolean(true);

        executorService.submit(() -> {
            while (flag.get()) {
                try {
                    Socket socket = serverSocket.accept();
                    Thread.sleep(10L);
                    // Do not read request.
                    socket.getOutputStream().write(
                            ("HTTP/1.1 403 Unauthorized\r\n" +
                             "\r\n").getBytes(StandardCharsets.UTF_8)
                    );
                    socket.close();
                } catch (SocketTimeoutException e) {
                    // ignore
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @After
    public void tearDown() {
        flag.set(false);

        try {
            executorService.shutdown();
            executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBrokenPipe_NetHttpTransport() throws IOException, TException, Failure {
        HttpRequestFactory factory = new NetHttpTransport().createRequestFactory();

        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                this::endpoint, factory, provider));

        try {
            // The request must be larger than the socket read buffer, to force it to fail the write to socket.
            client.test(new Request(Strings.times("request ", 1024 * 1024)));
            fail("No exception");
        } catch (IOException ex) {
            // TODO: This should be a HttpResponseException
            assertThat(ex.getMessage(), is("insufficient data written"));
        }
    }

    @Test
    public void testBrokenPipe_ApacheHttpTransport() throws IOException, TException, Failure {
        HttpRequestFactory factory = new ApacheHttpTransport().createRequestFactory();

        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                this::endpoint, factory, provider));

        try {
            // The request must be larger than the socket read buffer, to force it to fail the write to socket.
            client.test(new Request(Strings.times("request ", 1024 * 1024)));
            fail("No exception");
        } catch (SocketException ex) {
            // TODO: This should be a HttpResponseException
            assertThat(ex.getMessage(), anyOf(
                    is("Broken pipe (Write failed)"),
                    is("Connection reset"),
                    is("Software caused connection abort: socket write error")));
        }
    }

    @Test
    public void testSimpleRequest_connectionRefused_netHttpTransport() throws IOException, Failure, TException {
        HttpRequestFactory factory = new NetHttpTransport().createRequestFactory();

        GenericUrl url = new GenericUrl("http://localhost:" + (port - 10) + "/" + ENDPOINT);
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                () -> url, factory, provider));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (HttpHostConnectException ex) {
            assertThat(ex.getMessage(), is(startsWith("Connect to localhost:" + (port - 10) + " failed: Connection refused")));
        }
    }

    @Test
    public void testSimpleRequest_connectionRefused_apacheHttpTransport() throws IOException, Failure, TException {
        HttpRequestFactory factory = new ApacheHttpTransport().createRequestFactory();

        GenericUrl url = new GenericUrl("http://localhost:" + (port - 10) + "/" + ENDPOINT);
        TestService.Iface client = new TestService.Client(new HttpClientHandler(
                () -> url, factory, provider));

        try {
            client.test(new Request("request"));
            fail("No exception");
        } catch (HttpHostConnectException ex) {
            assertThat(ex.getMessage(), is(startsWith("Connect to localhost:" + (port - 10) + " failed: Connection refused")));
        }
    }
}
