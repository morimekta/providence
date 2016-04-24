package net.morimekta.providence.serializer;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A writer helper class for matching a serializer with an output stream.
 */
public class MessageWriter {
    private final OutputStream out;
    private final Serializer   serializer;

    public MessageWriter(OutputStream out, Serializer serializer) {
        this.out = out;
        this.serializer = serializer;
    }

    public <T extends PMessage<T>> int write(T message) throws IOException, SerializerException {
        return serializer.serialize(out, message);
    }

    public <T extends PMessage<T>> int write(PServiceCall<T> call) throws IOException, SerializerException {
        return serializer.serialize(out, call);
    }

}
