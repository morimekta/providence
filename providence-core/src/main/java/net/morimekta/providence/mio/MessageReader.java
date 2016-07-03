package net.morimekta.providence.mio;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.SerializerException;

import java.io.Closeable;
import java.io.IOException;

/**
 * An interface for reading messages and service calls.
 */
public interface MessageReader extends Closeable {
    <Message extends PMessage<Message, Field>, Field extends PField>
    Message read(PStructDescriptor<Message, Field> descriptor)
            throws IOException, SerializerException;

    <Message extends PMessage<Message, Field>, Field extends PField>
    PServiceCall<Message, Field> read(PService service)
            throws IOException, SerializerException;
}
