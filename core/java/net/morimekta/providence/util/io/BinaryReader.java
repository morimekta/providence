package net.morimekta.providence.util.io;

import net.morimekta.providence.Binary;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Stein Eldar Johnsen
 * @since 16.01.16.
 */
public class BinaryReader extends InputStream {
    private final InputStream in;

    public BinaryReader(InputStream in) {
        this.in = in;
    }

    /**
     * Read a single byte.
     *
     * @return the byte value, or -1 if end of stream.
     */
    @Override
    public int read() throws IOException {
        return in.read();
    }

    /**
     * Read binary data from stream.
     *
     * @param out The output buffer to read into.
     * @throws IOException If the entire byte array was not read.
     */
    @Override
    public int read(byte[] out) throws IOException {
        final int end = out.length;
        int i, off = 0;
        while (off < end && (i = in.read(out, off, end - off)) > 0) {
            off += i;
        }
        if (off < end) {
            throw new IOException();
        }
        return end;
    }

    /**
     * Read binary data from stream.
     *
     * @param out The output buffer to read into.
     * @param off Offset in out array to writeBinary to.
     * @param len Number of bytes to read.
     * @throws IOException If the entire byte array was not read.
     */
    @Override
    public int read(byte[] out, int off, int len) throws IOException {
        if (off < 0 || len < 0 || (off + len) > out.length) throw new IOException();

        final int end = off + len;
        int i;
        while (off < end && (i = in.read(out, off, len - off)) > 0) {
            off += i;
        }
        if (off < end) {
            throw new IOException();
        }
        return len;
    }

    @Override
    public void close() {}

    /**
     * Read a byte from the input stream.
     *
     * @return The number read.
     * @throws IOException If no byte to read.
     */
    public byte readByte() throws IOException {
        int read = in.read();
        if (read < 0) throw new IOException();
        return (byte) read;
    }

    /**
     * Read a short from the input stream.
     *
     * @return The number read.
     * @throws IOException If no number to read.
     */
    public short readShort() throws IOException {
        int b1 = in.read();
        if (b1 < 0) throw new IOException();
        int b2 = in.read();
        if (b2 < 0) throw new IOException();
        return (short) (b1 | b2 << 8);
    }

    /**
     * Read an int from the input stream.
     *
     * @return The number read.
     * @throws IOException If no number to read.
     */
    public int readInt() throws IOException {
        int b1 = in.read();
        if (b1 < 0) throw new IOException();
        int b2 = in.read();
        if (b2 < 0) throw new IOException();
        int b3 = in.read();
        if (b3 < 0) throw new IOException();
        int b4 = in.read();
        if (b4 < 0) throw new IOException();
        return (b1 | b2 << 8 | b3 << 16 | b4 << 24);
    }

    /**
     * Read a long int from the input stream.
     *
     * @return The number read.
     * @throws IOException If no number to read.
     */
    public long readLong() throws IOException {
        int b1 = in.read();
        if (b1 < 0) throw new IOException();
        int b2 = in.read();
        if (b2 < 0) throw new IOException();
        int b3 = in.read();
        if (b3 < 0) throw new IOException();
        long b4 = in.read();
        if (b4 < 0) throw new IOException();
        long b5 = in.read();
        if (b5 < 0) throw new IOException();
        long b6 = in.read();
        if (b6 < 0) throw new IOException();
        long b7 = in.read();
        if (b7 < 0) throw new IOException();
        long b8 = in.read();
        if (b8 < 0) throw new IOException();

        return (b1 | b2 << 8 | b3 << 16 | b4 << 24 | b5 << 32 | b6 << 40 | b7 << 48 | b8 << 56);
    }

    /**
     * Read a double from the input stream.
     *
     * @return The number read.
     * @throws IOException If no number to read.
     */
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    /**
     * Read binary data from stream.
     * @param bytes Number of bytes to read.
     * @return The binary wrapper.
     */
    public Binary readBinary(int bytes) throws IOException {
        return Binary.wrap(readBytes(bytes));
    }

    /**
     * Read binary data from stream.
     * @param bytes Number of bytes to read.
     * @return The binary wrapper.
     */
    public byte[] readBytes(int bytes) throws IOException {
        byte[] out = new byte[bytes];
        read(out, 0, bytes);
        return out;
    }

    /**
     * Read an unsigned byte from the input stream.
     *
     * @return Unsigned byte.
     * @throws IOException If no number to read.
     */
    public int readUInt8() throws IOException {
        int read = in.read();
        if (read < 0) throw new IOException();
        return read;
    }

    /**
     * Read an unsigned short from the input stream.
     *
     * @return The number read.
     * @throws IOException If no number to read.
     */
    public int readUInt16() throws IOException {
        int b1 = in.read();
        if (b1 < 0) throw new IOException();
        int b2 = in.read();
        if (b2 < 0) throw new IOException();
        return (b1 | b2 << 8);
    }

    /**
     * Read an unsigned short from the input stream.
     *
     * @return The number read.
     * @throws IOException If no number to read.
     */
    public int readUInt24() throws IOException {
        int b1 = in.read();
        if (b1 < 0) throw new IOException();
        int b2 = in.read();
        if (b2 < 0) throw new IOException();
        int b3 = in.read();
        if (b3 < 0) throw new IOException();
        return (b1 | b2 << 8 | b3 << 16);
    }

    /**
     * Read an unsigned int from the input stream.
     *
     * @return The number read.
     * @throws IOException If no number to read.
     */
    public int readUInt32() throws IOException {
        return readInt();
    }

    /**
     * Read an unsigned number from the input stream.
     *
     * @param bytes Number of bytes to read.
     * @return The number read.
     * @throws IOException
     */
    public int readUnsigned(int bytes) throws IOException {
        switch (bytes) {
            case 4: return readUInt32();
            case 3: return readUInt24();
            case 2: return readUInt16();
            case 1: return readUInt8();
        }
        throw new IOException();
    }

    /**
     * Read an signed number from the input stream.
     *
     * @param bytes Number of bytes to read.
     * @return The number read.
     * @throws IOException
     */
    public long readSigned(int bytes) throws IOException {
        switch (bytes) {
            case 8: return readLong();
            case 4: return readInt();
            case 2: return readShort();
            case 1: return readByte();
        }
        throw new IOException();
    }

    /**
     * Read a long number as zigzag encoded from the stream. The least
     * significant bit becomes the sign, and the actual value is absolute
     * and shifted one bit. This makes it maximum compressed both when
     * positive and negative.
     *
     * @return The zigzag decoded value.
     */
    public int readIntZigzag() throws IOException {
        int value = readIntVarint();
        return (value & 1) != 0 ? ~(value >> 1) : value >> 1;
    }

    /**
     * Read a long number as zigzag encoded from the stream. The least
     * significant bit becomes the sign, and the actual value is absolute
     * and shifted one bit. This makes it maximum compressed both when
     * positive and negative.
     *
     * @return The zigzag decoded value.
     */
    public long readLongZigzag() throws IOException {
        long value = readLongVarint();
        return (value & 1) != 0 ? ~(value >> 1) : value >> 1;
    }

    /**
     * Write a signed number as varint (integer with variable number of bytes,
     * determined as part of the bytes themselves.
     *
     * @return The varint read from stream.
     */
    public int readIntVarint() throws IOException {
        int i = in.read();
        if (i < 0) return 0;

        boolean c = (i & 0x80) > 0;
        int out = (i & 0x7f);

        int shift = 0;
        while (c) {
            shift += 7;
            i = readUInt8();
            c = (i & 0x80) > 0;
            out = out | (i & 0x7f) << shift;
        }
        return out;
    }

    /**
     * Write a signed number as varint (integer with variable number of bytes,
     * determined as part of the bytes themselves.
     *
     * @return The varint read from stream.
     */
    public long readLongVarint() throws IOException {
        int i = in.read();
        if (i < 0) return 0;

        long out = (i & 0x7f);
        boolean c = (i & 0x80) > 0;
        int shift = 0;
        while (c) {
            shift += 7;
            i = readUInt8();
            c = (i & 0x80) > 0;
            out = out | ((long) i & 0x7f) << shift;
        }
        return out;
    }
}
