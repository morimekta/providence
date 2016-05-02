package net.morimekta.providence.mio;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.serializer.SerializerException;

import java.io.Closeable;
import java.io.IOException;

/**
 * An interface for writing messages and service calls.
 */
public interface MessageWriter extends Closeable {
    <T extends PMessage<T>> int write(T message) throws IOException, SerializerException;

    <T extends PMessage<T>> int write(PServiceCall<T> call) throws IOException, SerializerException;
}
