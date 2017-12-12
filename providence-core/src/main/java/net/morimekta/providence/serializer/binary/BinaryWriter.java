package net.morimekta.providence.serializer.binary;

import net.morimekta.providence.PMessage;
import net.morimekta.util.io.BigEndianBinaryWriter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for messages that can directly be written to binary.
 */
public interface BinaryWriter {
    /**
     * Write the current message to the binary writer.
     *
     * NOTE: This method is not intended to be used directly. Instead use
     * the {@link net.morimekta.providence.serializer.BinarySerializer#serialize(OutputStream, PMessage)}
     * call.
     *
     * @param writer The binary writer to write to.
     * @return The number of bytes written.
     * @throws IOException If it failed to write the message for any reason.
     */
    int writeBinary(BigEndianBinaryWriter writer) throws IOException;
}
