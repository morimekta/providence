package net.morimekta.providence.mio;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A writer helper class for matching a serializer with an output stream.
 */
public class IOMessageWriter implements MessageWriter {
    private final OutputStream out;
    private final Serializer   serializer;

    public IOMessageWriter(OutputStream out, Serializer serializer) {
        this.out = out;
        this.serializer = serializer;
    }

    @Override
    public <T extends PMessage<T>> int write(T message) throws IOException, SerializerException {
        return serializer.serialize(out, message);
    }

    @Override
    public <T extends PMessage<T>> int write(PServiceCall<T> call) throws IOException, SerializerException {
        return serializer.serialize(out, call);
    }

}
