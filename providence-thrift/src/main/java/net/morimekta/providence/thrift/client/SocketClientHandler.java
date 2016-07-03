package net.morimekta.providence.thrift.client;

import net.morimekta.providence.PClientHandler;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Client handler for thrift RPC using the TSimpleServer that does <b>not</b>
 * use the TFramedTransport message wrapper.
 */
public class SocketClientHandler implements PClientHandler {
    private final Serializer    serializer;
    private final SocketAddress address;
    private final int connect_timeout;
    private final int read_timeout;

    public SocketClientHandler(Serializer serializer, SocketAddress address) {
        this(serializer, address, 10000, 10000);
    }

    public SocketClientHandler(Serializer serializer, SocketAddress address, int connect_timeout, int read_timeout) {
        this.serializer = serializer;
        this.address = address;
        this.connect_timeout = connect_timeout;
        this.read_timeout = read_timeout;
    }

    private synchronized Socket connect() throws IOException {
        Socket socket = new Socket();
        socket.setSoLinger(false, 0);
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
        socket.setSoTimeout(read_timeout);
        socket.connect(address, connect_timeout);
        return socket;
    }

    @Override
    public <Request extends PMessage<Request, RequestField>,
            Response extends PMessage<Response, ResponseField>,
            RequestField extends PField,
            ResponseField extends PField>
    PServiceCall<Response, ResponseField> handleCall(PServiceCall<Request, RequestField> call, PService service)
            throws IOException, SerializerException {
        try (Socket socket = connect()) {
            OutputStream out = new BufferedOutputStream(socket.getOutputStream());
            serializer.serialize(out, call);
            out.flush();

            if (call.getType() != PServiceCallType.ONEWAY) {
                InputStream in = new BufferedInputStream(socket.getInputStream());
                return serializer.deserialize(in, service);
            }
            return null;
        }
    }
}
