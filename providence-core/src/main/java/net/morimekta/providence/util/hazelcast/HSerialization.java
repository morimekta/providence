package net.morimekta.providence.util.hazelcast;

import net.morimekta.providence.PEnumValue;
import net.morimekta.util.Binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by scrier on 2017-04-14.
 */
public class HSerialization {

    private static ByteArrayOutputStream baos = new ByteArrayOutputStream();

    /**
     * Method to convert a List of Binary to a byte array.
     *
     * @param binaryList List containing Binary.
     * @return Array of bytes.
     * @throws IOException from {@link ByteArrayOutputStream}
     */
    public static byte[] fromBinaryList(List<Binary> binaryList) throws IOException {
        baos.reset();
        baos.write(toBytes(binaryList.size()));
        for( Binary binary : binaryList ) {
            baos.write(toBytes(binary.length()));
            baos.write(binary.get());
        }
        return baos.toByteArray();
    }

    /**
     * Method to convert a Set of Binary to a byte array.
     *
     * @param binaryList Set containing Binary.
     * @return Array of bytes.
     * @throws IOException from {@link ByteArrayOutputStream}
     */
    public static byte[] fromBinarySet(Set<Binary> binaryList) throws IOException {
        baos.reset();
        baos.write(toBytes(binaryList.size()));
        for( Binary binary : binaryList ) {
            baos.write(toBytes(binary.length()));
            baos.write(binary.get());
        }
        return baos.toByteArray();
    }

    /**
     * Method to convert a byte array to a List of Binary.
     *
     * @param bytes Array of bytes.
     * @return List of Binary.
     */
    public static List<Binary> toBinaryList(byte[] bytes) {
        final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        final int size = readInt(bis);
        List<Binary> result = new ArrayList<>();
        for( int i = 0; i < size; i++ ) {
            int length = readInt(bis);
            result.add(new Binary(readBytes(bis, length)));
        }
        return result;
    }

    /**
     * Method to convert a byte array to a Set of Binary.
     *
     * @param bytes Array of bytes.
     * @return List of Set.
     */
    public static Set<Binary> toBinarySet(byte[] bytes) {
        final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        final int size = readInt(bis);
        Set<Binary> result = new HashSet<>();
        for( int i = 0; i < size; i++ ) {
            int length = readInt(bis);
            result.add(new Binary(readBytes(bis, length)));
        }
        return result;
    }

    public static int[] fromEnumList(List<PEnumValue<?>> enums) {
        return enums.stream().map(PEnumValue::getValue).mapToInt(t -> t).toArray();
    }

    /**
     * Method to read an integer from a {@link ByteArrayInputStream}
     *
     * @param bis {@link ByteArrayInputStream} to read from.
     * @return int with the next 4 bytes from the bis.
     */
    private static int readInt(ByteArrayInputStream bis) {
        byte[] b = new byte[4];
        bis.read(b, 0, b.length);
        return ByteBuffer.allocate(4).wrap(b).getInt();
    }

    /**
     * Method to read length number of bytes from a {@link ByteArrayInputStream}.
     *
     * @param bis {@link ByteArrayInputStream} to read from.
     * @param length int with the length of number of bytes to read.
     * @return byte array with the bytes read.
     */
    private static byte[] readBytes(ByteArrayInputStream bis, int length) {
        byte[] b = new byte[length];
        bis.read(b, 0, length);
        return b;
    }

    /**
     * Method to convert a int value to a byte array.
     *
     * @param value int with the value to convert.
     * @return byte array.
     */
    private static byte[] toBytes(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

}
