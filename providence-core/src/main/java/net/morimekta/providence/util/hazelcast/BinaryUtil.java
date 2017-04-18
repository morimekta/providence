package net.morimekta.providence.util.hazelcast;

import net.morimekta.util.Binary;
import net.morimekta.util.io.BigEndianBinaryReader;
import net.morimekta.util.io.BigEndianBinaryWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Utility class for Binary converstion from and to binary array from Collections.
 */
public class BinaryUtil {

    /**
     * Method to convert a Collection of Binary to a byte array.
     *
     * @param binaryList Collection containing Binary elements.
     * @return Array of bytes.
     */
    public static byte[] fromBinaryCollection(Collection<Binary> binaryList) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BigEndianBinaryWriter writer = new BigEndianBinaryWriter(baos)) {
            writer.writeInt(binaryList.size());
            for (Binary binary : binaryList) {
                writer.writeInt(binary.length());
                writer.write(binary.get());
            }
            return baos.toByteArray();
        }
    }

    /**
     * Method to convert a byte array to a Collection of Binary.
     *
     * @param bytes Array of bytes.
     * @return Collection of Binary.
     */
    public static Collection<Binary> toBinaryCollection(byte[] bytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             BigEndianBinaryReader reader = new BigEndianBinaryReader(bais) ) {
            final int size = reader.expectInt();
            Collection<Binary> result = new ArrayList<>();
            for( int i = 0; i < size; i++ ) {
                int length = reader.expectInt();
                result.add(Binary.wrap(reader.expectBytes(length)));
            }
            return result;
        }
    }

}
