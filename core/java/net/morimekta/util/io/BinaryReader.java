/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.morimekta.util.io;

import net.morimekta.util.Binary;

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
     * @throws IOException If unable to fill the entire byte array.
     */
    @Override
    public int read(byte[] out) throws IOException {
        int i, off = 0;
        while (off < out.length && (i = in.read(out, off, out.length - off)) > 0) {
            off += i;
        }
        return off;
    }

    /**
     * Read binary data from stream.
     *
     * @param out The output buffer to read into.
     * @throws IOException If unable to fill the entire byte array.
     */
    public int expect(byte[] out) throws IOException {
        int i, off = 0;
        while (off < out.length && (i = in.read(out, off, out.length - off)) > 0) {
            off += i;
        }
        if (off < out.length) {
            throw new IOException();
        }
        return off;
    }

    /**
     * Read binary data from stream.
     *
     * @param out The output buffer to read into.
     * @param off Offset in out array to writeBinary to.
     * @param len Number of bytes to read.
     * @throws IOException If unable to fill the requested part of the byte array.
     */
    @Override
    public int read(byte[] out, int off, int len) throws IOException {
        if (off < 0 || len < 0 || (off + len) > out.length) {
            throw new IOException();
        }

        final int end = off + len;
        int i;
        while (off < end && (i = in.read(out, off, len - off)) > 0) {
            off += i;
        }
        return off;
    }

    @Override
    public void close() {}

    /**
     * Read a byte from the input stream.
     *
     * @return The number read.
     *
     * @throws IOException If no byte to read.
     */
    public byte expectByte() throws IOException {
        int read = in.read();
        if (read < 0) {
            throw new IOException();
        }
        return (byte) read;
    }

    /**
     * Read a short from the input stream.
     *
     * @return The number read.
     *
     * @throws IOException If no number to read.
     */
    public short expectShort() throws IOException {
        int b1 = in.read();
        if (b1 < 0) {
            throw new IOException();
        }
        int b2 = in.read();
        if (b2 < 0) {
            throw new IOException();
        }
        return (short) (b1 | b2 << 8);
    }

    /**
     * Read an int from the input stream.
     *
     * @return The number read.
     *
     * @throws IOException If no number to read.
     */
    public int expectInt() throws IOException {
        int b1 = in.read();
        if (b1 < 0) {
            throw new IOException();
        }
        int b2 = in.read();
        if (b2 < 0) {
            throw new IOException();
        }
        int b3 = in.read();
        if (b3 < 0) {
            throw new IOException();
        }
        int b4 = in.read();
        if (b4 < 0) {
            throw new IOException();
        }
        return (b1 | b2 << 8 | b3 << 16 | b4 << 24);
    }

    /**
     * Read a long int from the input stream.
     *
     * @return The number read.
     *
     * @throws IOException If no number to read.
     */
    public long expectLong() throws IOException {
        int b1 = in.read();
        if (b1 < 0) {
            throw new IOException();
        }
        int b2 = in.read();
        if (b2 < 0) {
            throw new IOException();
        }
        int b3 = in.read();
        if (b3 < 0) {
            throw new IOException();
        }
        long b4 = in.read();
        if (b4 < 0) {
            throw new IOException();
        }
        long b5 = in.read();
        if (b5 < 0) {
            throw new IOException();
        }
        long b6 = in.read();
        if (b6 < 0) {
            throw new IOException();
        }
        long b7 = in.read();
        if (b7 < 0) {
            throw new IOException();
        }
        long b8 = in.read();
        if (b8 < 0) {
            throw new IOException();
        }

        return (b1 | b2 << 8 | b3 << 16 | b4 << 24 | b5 << 32 | b6 << 40 | b7 << 48 | b8 << 56);
    }

    /**
     * Read a double from the input stream.
     *
     * @return The number read.
     *
     * @throws IOException If no number to read.
     */
    public double expectDouble() throws IOException {
        return Double.longBitsToDouble(expectLong());
    }

    /**
     * Read binary data from stream.
     *
     * @param bytes Number of bytes to read.
     * @return The binary wrapper.
     */
    public Binary expectBinary(int bytes) throws IOException {
        return Binary.wrap(expectBytes(bytes));
    }

    /**
     * Read binary data from stream.
     *
     * @param bytes Number of bytes to read.
     * @return The binary wrapper.
     */
    public byte[] expectBytes(final int bytes) throws IOException {
        byte[] out = new byte[bytes];
        expect(out);
        return out;
    }

    /**
     * Read an unsigned byte from the input stream.
     *
     * @return Unsigned byte.
     *
     * @throws IOException If no number to read.
     */
    public int expectUInt8() throws IOException {
        int read = in.read();
        if (read < 0) {
            throw new IOException();
        }
        return read;
    }

    /**
     * Read an unsigned short from the input stream.
     *
     * @return The number read.
     *
     * @throws IOException If no number to read.
     */
    public int expectUInt16() throws IOException {
        int b1 = in.read();
        if (b1 < 0) {
            throw new IOException();
        }
        int b2 = in.read();
        if (b2 < 0) {
            throw new IOException();
        }
        return (b1 | b2 << 8);
    }

    /**
     * Read an unsigned short from the input stream.
     *
     * @return The number read.
     *
     * @throws IOException If no number to read.
     */
    public int readUInt16() throws IOException {
        int b1 = in.read();
        if (b1 < 0) {
            return 0;
        }
        int b2 = in.read();
        if (b2 < 0) {
            throw new IOException();
        }
        return (b1 | b2 << 8);
    }

    /**
     * Read an unsigned short from the input stream.
     *
     * @return The number read.
     *
     * @throws IOException If no number to read.
     */
    public int expectUInt24() throws IOException {
        int b1 = in.read();
        if (b1 < 0) {
            throw new IOException();
        }
        int b2 = in.read();
        if (b2 < 0) {
            throw new IOException();
        }
        int b3 = in.read();
        if (b3 < 0) {
            throw new IOException();
        }
        return (b1 | b2 << 8 | b3 << 16);
    }

    /**
     * Read an unsigned int from the input stream.
     *
     * @return The number read.
     *
     * @throws IOException If no number to read.
     */
    public int expectUInt32() throws IOException {
        return expectInt();
    }

    /**
     * Read an unsigned number from the input stream.
     *
     * @param bytes Number of bytes to read.
     * @return The number read.
     */
    public int expectUnsigned(int bytes) throws IOException {
        switch (bytes) {
            case 4:
                return expectUInt32();
            case 3:
                return expectUInt24();
            case 2:
                return expectUInt16();
            case 1:
                return expectUInt8();
        }
        throw new IOException();
    }

    /**
     * Read an signed number from the input stream.
     *
     * @param bytes Number of bytes to read.
     * @return The number read.
     */
    public long expectSigned(int bytes) throws IOException {
        switch (bytes) {
            case 8:
                return expectLong();
            case 4:
                return expectInt();
            case 2:
                return expectShort();
            case 1:
                return expectByte();
        }
        throw new IOException();
    }

    /**
     * Read a long number as zigzag encoded from the stream. The least
     * significant bit becomes the sign, and the actual value is absolute and
     * shifted one bit. This makes it maximum compressed both when positive and
     * negative.
     *
     * @return The zigzag decoded value.
     */
    public int readIntZigzag() throws IOException {
        int value = readIntVarint();
        return (value & 1) != 0 ? ~(value >>> 1) : value >>> 1;
    }

    /**
     * Read a long number as zigzag encoded from the stream. The least
     * significant bit becomes the sign, and the actual value is absolute and
     * shifted one bit. This makes it maximum compressed both when positive and
     * negative.
     *
     * @return The zigzag decoded value.
     */
    public long readLongZigzag() throws IOException {
        long value = readLongVarint();
        return (value & 1) != 0 ? ~(value >>> 1) : value >>> 1;
    }

    /**
     * Write a signed number as varint (integer with variable number of bytes,
     * determined as part of the bytes themselves.
     * <p/>
     * NOTE: Reading varint accepts end of stream as '0'.
     *
     * @return The varint read from stream.
     */
    public int readIntVarint() throws IOException {
        int i = in.read();
        if (i < 0) {
            return 0;
        }

        boolean c = (i & 0x80) > 0;
        int out = (i & 0x7f);

        int shift = 0;
        while (c) {
            shift += 7;
            i = expectUInt8();
            c = (i & 0x80) > 0;
            out |= ((i & 0x7f) << shift);
        }
        return out;
    }

    /**
     * Write a signed number as varint (integer with variable number of bytes,
     * determined as part of the bytes themselves.
     * <p/>
     * NOTE: Reading varint accepts end of stream as '0'.
     *
     * @return The varint read from stream.
     */
    public long readLongVarint() throws IOException {
        int i = in.read();
        if (i < 0) {
            return 0;
        }

        boolean c = (i & 0x80) > 0;
        long out = (i & 0x7f);

        int shift = 0;
        while (c) {
            shift += 7;
            i = expectUInt8();
            c = (i & 0x80) > 0;
            out = out | ((long) i & 0x7f) << shift;
        }
        return out;
    }
}
