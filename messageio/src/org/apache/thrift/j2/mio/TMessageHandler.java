package org.apache.thrift.j2.mio;

import org.apache.thrift.j2.TMessage;

import java.io.IOException;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 28.10.15
 */
public interface TMessageHandler<M extends TMessage<M>> {
    /**
     * Handle a message.
     *
     * @param message The message to handle.
     */
    void handle(M message) throws IOException;
}
