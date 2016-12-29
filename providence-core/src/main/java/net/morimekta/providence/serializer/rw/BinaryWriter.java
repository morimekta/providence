package net.morimekta.providence.serializer.rw;

import net.morimekta.util.io.BigEndianBinaryWriter;

import java.io.IOException;

/**
 * Interface for messages that can directly be written to binary.
 */
public interface BinaryWriter {
    int writeBinary(BigEndianBinaryWriter writer) throws IOException;
}
