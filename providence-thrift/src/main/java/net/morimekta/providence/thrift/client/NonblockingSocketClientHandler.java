package net.morimekta.providence.thrift.client;

import net.morimekta.providence.PClientHandler;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.thrift.io.FramedBufferInputSteram;
import net.morimekta.providence.thrift.io.FramedBufferOutputStream;

import java.io.Closeable;
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
 * When using this client handler make sure to close it when no longer in use.
 * Otherwise it will keep the socket channel open almost indefinitely.
 */
public class NonblockingSocketClientHandler implements PClientHandler, Closeable {
    private final Serializer    serializer;
    private final SocketAddress address;
    private final int           connect_timeout;
    private final int           read_timeout;

    private SocketChannel channel;

    public NonblockingSocketClientHandler(Serializer serializer, SocketAddress address) {
        this(serializer, address, 10000, 10000);
    }

    public NonblockingSocketClientHandler(Serializer serializer, SocketAddress address, int connect_timeout, int read_timeout) {
        this.serializer = serializer;
        this.address = address;
        this.connect_timeout = connect_timeout;
        this.read_timeout = read_timeout;
    }

    private SocketChannel connect() throws IOException {
        if (channel == null) {
            channel = SocketChannel.open();
            Socket socket = channel.socket();
            socket.setSoLinger(false, 0);
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);
            socket.setSoTimeout(read_timeout);

            // The channel is always in blocking mode.
            channel.configureBlocking(true);
            channel.socket().connect(address, connect_timeout);
        }
        return channel;
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            try {
                channel.close();
            } finally {
                channel = null;
            }
        }
    }

    @Override
    public <Request extends PMessage<Request, RequestField>,
            Response extends PMessage<Response, ResponseField>,
            RequestField extends PField,
            ResponseField extends PField> PServiceCall<Response, ResponseField>
    handleCall(PServiceCall<Request, RequestField> call, PService service)
            throws IOException, SerializerException {
        SocketChannel channel = connect();

        OutputStream out = new FramedBufferOutputStream(channel);
        serializer.serialize(out, call);
        out.flush();

        if (call.getType() != PServiceCallType.ONEWAY) {
            InputStream in = new FramedBufferInputSteram(channel);
            return serializer.deserialize(in, service);
        }

        return null;
    }
}
