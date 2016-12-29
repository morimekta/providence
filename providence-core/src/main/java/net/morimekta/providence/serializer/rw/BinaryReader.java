package net.morimekta.providence.serializer.rw;

import net.morimekta.util.io.BigEndianBinaryReader;

import java.io.IOException;

/**
 * Interface for message builders that can read binary directly from
 * an big endian binary reader.
 */
public interface BinaryReader {
    void readBinary(BigEndianBinaryReader reader, boolean strict) throws IOException;
}
