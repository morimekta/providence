package net.morimekta.providence;

import net.morimekta.providence.util.PBase64Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;

/**
 * Simplistic byte sequence wrapper with lots of convenience methods. Used to
 * wrap byte arrays for the binary data type.
 */
public class Binary implements Comparable<Binary> {
    private final byte[] bytes;

    public Binary(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Convenience method to wrap a byte array into a byte sequence.
     * @param bytes Bytes to wrap.
     * @return The wrapped byte sequence.
     */
    public static Binary wrap(byte[] bytes) {
        return new Binary(bytes);
    }

    /**
     * Convenience method to copy a byte array into a byte sequence.
     * @param bytes Bytes to wrap.
     * @return The wrapped byte sequence.
     */
    public static Binary copy(byte[] bytes) {
        return copy(bytes, 0, bytes.length);
    }

    /**
     * Convenience method to copy a part of a byte array into a byte sequence.
     * @param bytes Bytes to wrap.
     * @param off Offset in source bytes to start reading from.
     * @param len Number of bytes to copy.
     * @return The wrapped byte sequence.
     */
    public static Binary copy(byte[] bytes, int off, int len) {
        byte[] cpy = new byte[len];
        System.arraycopy(bytes, off, cpy, 0, len);
        return wrap(cpy);
    }

    /**
     * Decode base64 string and wrap the result in a byte sequence.
     * @param base64 The string to decode.
     * @return The resulting sequence.
     */
    public static Binary fromBase64(String base64) {
        byte[] arr = PBase64Utils.decode(base64);
        return new Binary(arr);
    }

    /**
     * Get the length of the backing array.
     * @return Byte count.
     */
    public int length() {
        return bytes.length;
    }

    /**
     * Get a copy of the backing array.
     * @return The copy.
     */
    public byte[] get() {
        byte[] cpy = new byte[bytes.length];
        System.arraycopy(bytes, 0, cpy, 0, bytes.length);
        return cpy;
    }

    /**
     * Get a copy of the backing array.
     * @param into Target ro copy into.
     * @return Number of bytes written.
     */
    public int get(byte[] into) {
        int len = Math.min(into.length, bytes.length);
        System.arraycopy(bytes, 0, into, 0, len);
        return len;
    }

    /**
     * Get the sequence encoded as base64.
     * @return The encoded string.
     */
    public
    String toBase64() {
        return PBase64Utils.encode(bytes);
    }

    /**
     * Parse a hex string as bytes.
     *
     * @param hex The hex string.
     * @return The corresponding bytes.
     */
    public static Binary fromHexString(String hex) {
        if (hex.length() % 2 != 0) throw new AssertionError("Wrong hex string length");
        final int len = hex.length() / 2;
        final byte[] out = new byte[len];
        for (int i = 0; i < len; ++i) {
            int pos = i * 2;
            String part = hex.substring(pos, pos + 2);
            out[i] = (byte) Integer.parseInt(part, 16);
        }
        return new Binary(out);
    }

    /**
     * Make a hex string from a byte array.
     *
     * @return The hex string.
     */
    public String toHexString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            builder.append(String.format("%02x", bytes[i]));
        }
        return builder.toString();
    }

    /**
     * Get a byte buffer wrapping the binary data.
     * @return A byte buffer.
     */
    public ByteBuffer getByteBuffer() {
        return ByteBuffer.wrap(get());
    }

    /**
     * Read a binary buffer from input stream.
     * @param in Input stream to read.
     * @param len Number of bytes to read.
     * @return The read bytes.
     * @throws IOException If unable to read completely what's expected.
     */
    public static Binary read(InputStream in, int len) throws IOException {
        byte[] bytes = new byte[len];
        int pos = 0;
        while (pos < len) {
            int i = in.read(bytes, pos, len - pos);
            if (i <= 0) throw new IOException("End of stream before complete buffer read.");
            pos += i;
        }
        return wrap(bytes);
    }

    /**
     * Write bytes to output stream.
     * @param out Stream to write to.
     * @return Number of bytes written.
     * @throws IOException
     */
    public int write(OutputStream out) throws IOException {
        out.write(bytes);
        return bytes.length;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof Binary)) return false;
        Binary other = (Binary) o;

        return Arrays.equals(bytes, other.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public int compareTo(Binary other) {
        final int c = Math.min(bytes.length, other.bytes.length);
        for (int i = 0; i < c; ++i) {
            if (bytes[i] != other.bytes[i]) {
                return bytes[i] > other.bytes[i] ? 1 : -1;
            }
        }
        if (bytes.length == other.bytes.length) return 0;
        return bytes.length > other.bytes.length ? 1 : -1;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("binary(");
        for (byte b : bytes) {
            int i = b < 0 ? 0x100 + b : b;
            buffer.append(String.format(Locale.ENGLISH, "%02x", i));
        }
        buffer.append(")");
        return buffer.toString();
    }
}
