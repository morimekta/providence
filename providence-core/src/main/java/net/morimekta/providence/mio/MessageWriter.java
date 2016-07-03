package net.morimekta.providence.mio;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.serializer.SerializerException;

import java.io.Closeable;
import java.io.IOException;

/**
 * An interface for writing messages and service calls.
 */
public interface MessageWriter extends Closeable {
    <Message extends PMessage<Message, Field>, Field extends PField>
    int write(Message message) throws IOException, SerializerException;

    <Message extends PMessage<Message, Field>, Field extends PField>
    int write(PServiceCall<Message, Field> call) throws IOException, SerializerException;
}
