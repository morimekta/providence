package net.morimekta.providence.mio;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;

import java.io.IOException;
import java.io.InputStream;

/**
 * A reader helper class for matching a serializer with an input stream.
 */
public class IOMessageReader implements MessageReader {
    private final Serializer  serializer;
    private final InputStream in;

    public IOMessageReader(InputStream in, Serializer serializer) {
        this.in = in;
        this.serializer = serializer;
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField> Message
    read(PStructDescriptor<Message, Field> descriptor) throws IOException, SerializerException {
        return serializer.deserialize(in, descriptor);
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField> PServiceCall<Message, Field>
    read(PService service) throws IOException, SerializerException {
        return serializer.deserialize(in, service);
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
