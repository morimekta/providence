package org.apache.thrift.j2.mio;

import java.io.IOException;

import org.apache.thrift.j2.TMessage;

/**
 * Message writer interface.
 */
public interface TMessageWriter<M extends TMessage<M>> {
    /**
     * Handle a message.
     *
     * @param message The message to handle.
     */
    int write(M message) throws IOException;

    void flush() throws IOException;

    void close() throws IOException;
}
