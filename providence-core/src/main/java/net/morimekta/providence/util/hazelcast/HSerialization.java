package net.morimekta.providence.util.hazelcast;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.util.Binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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

    /**
     * Method to read an byte from a {@link ByteArrayInputStream}
     *
     * @param bis {@link ByteArrayInputStream} to read from.
     * @return byte with the next byte from the bis.
     */
    public static byte readByte(ByteArrayInputStream bis) {
        byte[] b = new byte[1];
        bis.read(b, 0, b.length);
        return ByteBuffer.allocate(b.length).wrap(b).get();
    }

    /**
     * Method to read an short from a {@link ByteArrayInputStream}
     *
     * @param bis {@link ByteArrayInputStream} to read from.
     * @return short with the next 2 bytes from the bis.
     */
    public static short readShort(ByteArrayInputStream bis) {
        byte[] b = new byte[2];
        bis.read(b, 0, b.length);
        return ByteBuffer.allocate(b.length).wrap(b).getShort();
    }

    /**
     * Method to read an integer from a {@link ByteArrayInputStream}
     *
     * @param bis {@link ByteArrayInputStream} to read from.
     * @return int with the next 4 bytes from the bis.
     */
    public static int readInt(ByteArrayInputStream bis) {
        byte[] b = new byte[4];
        bis.read(b, 0, b.length);
        return ByteBuffer.allocate(b.length).wrap(b).getInt();
    }

    /**
     * Method to read an long from a {@link ByteArrayInputStream}
     *
     * @param bis {@link ByteArrayInputStream} to read from.
     * @return long with the next 8 bytes from the bis.
     */
    public static long readLong(ByteArrayInputStream bis) {
        byte[] b = new byte[8];
        bis.read(b, 0, b.length);
        return ByteBuffer.allocate(b.length).wrap(b).getLong();
    }

    /**
     * Method to read an double from a {@link ByteArrayInputStream}
     *
     * @param bis {@link ByteArrayInputStream} to read from.
     * @return double with the next 8 bytes from the bis.
     */
    public static double readDouble(ByteArrayInputStream bis) {
        byte[] b = new byte[8];
        bis.read(b, 0, b.length);
        return ByteBuffer.allocate(b.length).wrap(b).getDouble();
    }

    /**
     * Method to read an string from a {@link ByteArrayInputStream}
     *
     * @param bis {@link ByteArrayInputStream} to read from.
     * @return String with the next length bytes from the bis.
     */
    public static String readString(ByteArrayInputStream bis) {
        int size = readInt(bis);
        byte[] b = new byte[size];
        bis.read(b, 0, b.length);
        return new String(b, StandardCharsets.UTF_8);
    }

    /**
     * Method to read length number of bytes from a {@link ByteArrayInputStream}.
     *
     * @param bis {@link ByteArrayInputStream} to read from.
     * @param length int with the length of number of bytes to read.
     * @return byte array with the bytes read.
     */
    public static byte[] readBytes(ByteArrayInputStream bis, int length) {
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
