package net.morimekta.providence.thrift.server;

import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.util.ServiceCallInstrumentation;
import net.morimekta.test.providence.thrift.map.NotFound;
import net.morimekta.test.providence.thrift.map.RemoteMap;

import com.google.common.collect.ImmutableMap;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TSocket;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class NonblockingSocketServerTest {
    private Map<String,String>         remoteMap;
    private ServiceCallInstrumentation instrumentation;
    private NonblockingSocketServer    server;
    private int                        port;
    private ExecutorService executor;

    @Before
    public void setUp() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");

        remoteMap = new ConcurrentHashMap<>();
        RemoteMap.Iface remoteImpl = new RemoteMap.Iface() {
            @Override
            public boolean put(String pKey, String pValue) throws IOException {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return remoteMap.put(pKey, pValue) != null;
            }

            @Override
            public Map<String, String> putAll(Map<String, String> pSource) throws IOException {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Map<String, String> out = new HashMap<>();
                for (Map.Entry<String, String> entry : pSource.entrySet()) {
                    String repl = remoteMap.put(entry.getKey(), entry.getValue());
                    if (repl != null) {
                        out.put(entry.getKey(), repl);
                    }
                }
                return out;
            }

            @Override
            public String get(String pKey) throws IOException, NotFound {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String res = remoteMap.get(pKey);
                if (res == null)
                    throw NotFound.builder()
                                  .build();
                return res;
            }

            @Override
            public Map<String, String> getAll(Set<String> pKeys) throws IOException {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Map<String, String> out = new HashMap<>();
                for (String key : pKeys) {
                    String val = remoteMap.get(key);
                    if (val != null) {
                        out.put(key, val);
                    }
                }
                return out;
            }
        };
        instrumentation = mock(ServiceCallInstrumentation.class);
        server = NonblockingSocketServer.builder(new RemoteMap.Processor(remoteImpl))
                                        .withSerializer(new TJsonProtocolSerializer(true))
                                        .withInstrumentation(instrumentation)
                                        .start();
        port = server.getPort();

        executor = Executors.newFixedThreadPool(10);
    }

    @After
    public void tearDownClass() throws IOException {
        server.close();
    }

    @Test
    public void testWithPlainThriftClient() throws IOException, TException, NotFound, InterruptedException {
        try (TSocket socket = new TSocket("localhost", port);
             TFramedTransport transport = new TFramedTransport(socket)) {
            socket.open();

            TProtocol protocol = new TJSONProtocol(transport);
            net.morimekta.test.thrift.thrift.map.RemoteMap.Client client = new net.morimekta.test.thrift.thrift.map.RemoteMap.Client(protocol);

            client.put("a", "b");
            client.put("b", "");
            try {
                client.get("c");
                fail("no exception");
            } catch (net.morimekta.test.thrift.thrift.map.NotFound nfe) {
                // nothing to check.
            }

            verify(instrumentation, times(3)).onComplete(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
            verifyNoMoreInteractions(instrumentation);

            assertThat(remoteMap, is(ImmutableMap.of(
                    "a", "b",
                    "b", "")));
        }

        Thread.sleep(100);

        reset(instrumentation);
        remoteMap.clear();

        try (TSocket socket = new TSocket("localhost", port);
             TFramedTransport transport = new TFramedTransport(socket)) {
            socket.open();

            TProtocol protocol = new TJSONProtocol(transport);
            net.morimekta.test.thrift.thrift.map.RemoteMap.Client client = new net.morimekta.test.thrift.thrift.map.RemoteMap.Client(protocol);

            client.put("a", "b123");
            client.put("b", "a2345");
            try {
                client.get("c");
                fail("no exception");
            } catch (net.morimekta.test.thrift.thrift.map.NotFound nfe) {
                // nothing to check.
            }

            verify(instrumentation, times(3)).onComplete(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
            verifyNoMoreInteractions(instrumentation);

            assertThat(remoteMap, is(ImmutableMap.of(
                    "a", "b123",
                    "b", "a2345")));
        }
    }

    @Test
    public void testWithNonblockingThriftClient()
            throws IOException, TException, ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Boolean> a = new CompletableFuture<>();
        CompletableFuture<Boolean> b = new CompletableFuture<>();

        try (TNonblockingSocket socket_a = new TNonblockingSocket("localhost", port);
             TNonblockingSocket socket_b = new TNonblockingSocket("localhost", port)) {
            socket_a.startConnect();
            socket_a.finishConnect();
            socket_b.startConnect();
            socket_b.finishConnect();

            net.morimekta.test.thrift.thrift.map.RemoteMap.AsyncClient client_a = new net.morimekta.test.thrift.thrift.map.RemoteMap.AsyncClient(
                    new TJSONProtocol.Factory(),
                    new TAsyncClientManager(),
                    socket_a);

            client_a.put("a", "bb", new AsyncMethodCallback<Boolean>() {
                @Override
                public void onComplete(Boolean response) {
                    a.complete(response);
                }

                @Override
                public void onError(Exception exception) {
                    a.completeExceptionally(exception);
                }
            });

            // Since thrift clients does not support handling multiple requests at
            // the same time (yes, even the AsyncClient does not support parallel
            // execution).
            net.morimekta.test.thrift.thrift.map.RemoteMap.AsyncClient client_b =
                    new net.morimekta.test.thrift.thrift.map.RemoteMap.AsyncClient(
                            new TJSONProtocol.Factory(),
                            new TAsyncClientManager(),
                            socket_b);
            client_b.put("b", "aaa", new AsyncMethodCallback<Boolean>() {
                @Override
                public void onComplete(Boolean response) {
                    b.complete(response);
                }

                @Override
                public void onError(Exception exception) {
                    b.completeExceptionally(exception);
                }
            });

            Awaitility.waitAtMost(Duration.ONE_SECOND).until(() -> remoteMap.size() == 2);

            assertThat(a.get(1000, TimeUnit.MILLISECONDS), is(Boolean.FALSE));
            assertThat(b.get(1000, TimeUnit.MILLISECONDS), is(Boolean.FALSE));
            assertThat(remoteMap, is(ImmutableMap.of("a", "bb",
                                                     "b", "aaa")));
        }
    }
}
