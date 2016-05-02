package net.morimekta.providence.thrift.client;

import net.morimekta.providence.PClientHandler;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.thrift.io.FramedBufferInputSteram;
import net.morimekta.providence.thrift.io.FramedBufferOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Client handler for thrift RPC using the TNonblockingServer, or similar that
 * uses the TFramedTransport message wrapper.
 *
 * TODO: Reuse socket channel until closed.
 */
public class NonblockingSocketClientHandler implements PClientHandler {
    private final Serializer    serializer;
    private final SocketAddress address;

    public NonblockingSocketClientHandler(Serializer serializer, SocketAddress address) {
        this.serializer = serializer;
        this.address = address;
    }

    private SocketChannel connect() throws IOException {
        SocketChannel channel = SocketChannel.open();
        Socket socket = channel.socket();
        socket.setSoLinger(false, 0);
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
        socket.setSoTimeout(1000);

        channel.configureBlocking(true);
        if (!channel.connect(address)) {
            if (!channel.finishConnect()) {
                throw new IOException();
            }
        }
        return channel;
    }

    @Override
    public <RQ extends PMessage<RQ>, RS extends PMessage<RS>> PServiceCall<RS>
    handleCall(PServiceCall<RQ> call, PService service)
            throws IOException, SerializerException {
        try (SocketChannel channel = connect()) {
            OutputStream out = new FramedBufferOutputStream(channel);
            serializer.serialize(out, call);
            out.flush();

            channel.shutdownOutput();
            channel.configureBlocking(true);

            if (call.getType() != PServiceCallType.ONEWAY) {
                InputStream in = new FramedBufferInputSteram(channel);
                return serializer.deserialize(in, service);
            }
        }
        return null;
    }
}
