package net.morimekta.providence;

import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;

import java.io.IOException;

/**
 * Stream processor interface for providence services.
 */
@FunctionalInterface
public interface PProcessor {
    /**
     * Process message read from reader, and write response to writer.
     *
     * @param reader The message reader for the request.
     * @param writer The message writer for the response.
     * @return True if the response written is appropriate response (if any).
     * @throws IOException In failure to handle input or output.
     */
    boolean process(MessageReader reader, MessageWriter writer) throws IOException;
}
