package net.morimekta.providence.serializer.binary;

import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.util.io.BigEndianBinaryReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for message builders that can read binary directly from
 * an big endian binary reader.
 */
public interface BinaryReader {
    /**
     * Read the binary content into the current builder.
     *
     * NOTE: This method is not intended to be used directly. Instead use
     * the {@link net.morimekta.providence.serializer.BinarySerializer#deserialize(InputStream, PMessageDescriptor)}
     * call.
     *
     * @param reader The reader to read from.
     * @param strict If content should be handled strictly. True means to fail
     *               on everything that Apache thrift failed read() on.
     * @throws IOException When unable to read message for any reason.
     */
    void readBinary(BigEndianBinaryReader reader, boolean strict) throws IOException;
}
