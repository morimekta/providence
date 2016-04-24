package net.morimekta.providence;

import net.morimekta.providence.serializer.MessageReader;
import net.morimekta.providence.serializer.MessageWriter;

import java.io.IOException;

/**
 * Stream processor interface for providence services.
 */
public interface PProcessor {
    /**
     *
     * @param reader The message reader for the request.
     * @param writer The message writer for the response.
     * @return True if the response written is appropriate response (if any).
     * @throws IOException In failure to handle input or output.
     */
    boolean process(MessageReader reader, MessageWriter writer) throws IOException;
}
