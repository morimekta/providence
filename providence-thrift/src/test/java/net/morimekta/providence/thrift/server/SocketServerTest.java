package net.morimekta.providence.thrift.server;

import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.server.ServiceInstrumentation;
import net.morimekta.test.providence.thrift.service.Failure;
import net.morimekta.test.providence.thrift.service.MyService.Iface;
import net.morimekta.test.providence.thrift.service.MyService.Processor;
import net.morimekta.test.providence.thrift.service.Request;
import net.morimekta.test.providence.thrift.service.Response;
import net.morimekta.test.thrift.thrift.service.MyService.Client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class SocketServerTest {
    private static Iface                  impl;
    private static ServiceInstrumentation instrumentation;
    private static SocketServer           server;
    private static int                    port;

    @BeforeClass
    public static void setUpClass() {
        impl = mock(Iface.class);
        instrumentation = mock(ServiceInstrumentation.class);
        server = SocketServer.builder(new Processor(impl))
                             .withSerializer(new BinarySerializer(true))
                             .withInstrumentation(instrumentation)
                             .withMaxBacklog(2)
                             .withThreadFactory(new ThreadFactoryBuilder()
                                     .setDaemon(true)
                                     .setNameFormat("test-%d")
                                     .build())
                             .withWorkerThreads(1)
                             .withClientTimeout(200)
                             .start();
        port = server.getPort();
    }

    @AfterClass
    public static void tearDownClass() {
        server.close();
    }

    @Before
    public void setUp() {
        reset(impl, instrumentation);
    }

    @Test
    public void testSimple() throws IOException, TException, Failure, InterruptedException {
        try (TSocket socket = new TSocket("localhost", port)) {
            socket.open();
            TProtocol protocol = new TBinaryProtocol(socket);
            Client client = new Client(protocol);

            when(impl.test(any(Request.class))).thenReturn(Response.builder()
                                                                   .setText("Yay!")
                                                                   .build());

            net.morimekta.test.thrift.thrift.service.Response response = client.test(new net.morimekta.test.thrift.thrift.service.Request(
                    "Really!"));
            assertThat(response.getText(), is("Yay!"));

            verify(impl).test(Request.builder()
                                     .setText("Really!")
                                     .build());

            Thread.sleep(1);

            verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
            verifyNoMoreInteractions(impl, instrumentation);
        }
    }

    @Test
    public void testMultiple() throws IOException, TException, Failure, InterruptedException {
        try (TSocket socket = new TSocket("localhost", port)) {
            socket.open();
            TProtocol protocol = new TBinaryProtocol(socket);
            Client client = new Client(protocol);

            when(impl.test(any(Request.class))).thenReturn(Response.builder()
                                                                   .setText("Yay!")
                                                                   .build());

            net.morimekta.test.thrift.thrift.service.Response response = client.test(new net.morimekta.test.thrift.thrift.service.Request(
                    "Really!"));
            assertThat(response.getText(), is("Yay!"));

            verify(impl).test(Request.builder()
                                     .setText("Really!")
                                     .build());

            Thread.sleep(1);

            verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
            verifyNoMoreInteractions(impl, instrumentation);
            reset(impl, instrumentation);

            when(impl.test(any(Request.class))).thenReturn(Response.builder()
                                                                   .setText("Woot!")
                                                                   .build());

            response = client.test(new net.morimekta.test.thrift.thrift.service.Request(
                    "Doh!"));
            assertThat(response.getText(), is("Woot!"));

            verify(impl).test(Request.builder()
                                     .setText("Doh!")
                                     .build());

            Thread.sleep(1);

            verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
            verifyNoMoreInteractions(impl, instrumentation);
        }
    }

    @Test
    public void testException() throws TException, IOException, Failure, InterruptedException {
        try (TSocket socket = new TSocket("localhost", port)) {
            socket.open();
            TProtocol protocol = new TBinaryProtocol(socket);
            Client client = new Client(protocol);

            when(impl.test(any(Request.class))).thenThrow(Failure.builder()
                                                                 .setText("Noooo!")
                                                                 .build());

            try {
                client.test(new net.morimekta.test.thrift.thrift.service.Request("O'Really???"));
                fail("no exception");
            } catch (net.morimekta.test.thrift.thrift.service.Failure e) {
                assertThat(e.getText(), is("Noooo!"));
            }

            verify(impl).test(Request.builder()
                                     .setText("O'Really???")
                                     .build());

            Thread.sleep(1);

            verify(instrumentation).afterCall(anyDouble(), any(PServiceCall.class), any(PServiceCall.class));
            verifyNoMoreInteractions(impl, instrumentation);
        }
    }
}
