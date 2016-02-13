package net.morimekta.providence.mio;

import net.morimekta.providence.PMessage;

import java.io.IOException;

/**
 * Message writer interface.
 */
public interface PMessageWriter<M extends PMessage<M>> {
    /**
     * Handle a message.
     *
     * @param message The message to handle.
     */
    int write(M message) throws IOException;

    void flush() throws IOException;

    void close() throws IOException;
}
