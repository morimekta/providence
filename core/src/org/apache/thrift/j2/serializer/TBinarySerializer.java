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

package org.apache.thrift.j2.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TContainer;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TMap;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.descriptor.TDescriptor;

/**
 * Compact binary serializer. This uses the most isCompact binary format
 * allowable.
 * <p/>
 * See data definition file lib/java2/doc/isCompact-binary.md for format spec.
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class TBinarySerializer
        extends TSerializer {
    private final boolean mStrict;

    /**
     * Construct a serializer instance.
     */
    public TBinarySerializer() {
        this(false);
    }

    /**
     * Construct a serializer instance.
     */
    public TBinarySerializer(boolean strict) {
        mStrict = strict;
    }

    @Override
    public int serialize(OutputStream output, TMessage<?> message) throws IOException, TSerializeException {
        if (mStrict && !message.isValid()) {
            throw new TSerializeException("Message not valid.");
        }
        int len = 0;
        for (TField<?> field : message.getDescriptor().getFields()) {
            if (message.has(field.getKey())) {
                len += writeUnsigned(output, field.getKey(), 2);
                len += writeFieldValue(output,
                                       message.get(field.getKey()));
            }
        }
        // write 2 null-bytes (field ID 0).
        output.write(0);
        output.write(0);
        return len + 2;
    }

    @Override
    public <T> int serialize(OutputStream output, TDescriptor<T> descriptor, T value)
            throws IOException, TSerializeException {
        try {
            return writeFieldValue(output, value);
        } catch (IOException e) {
            throw new TSerializeException(e, "Unable to write to stream");
        }
    }


    @Override
    public <T> T deserialize(InputStream input, TDescriptor<T> descriptor)
            throws TSerializeException, IOException {
        // Assume it consists of a single field.
        switch (descriptor.getType()) {
            case MESSAGE:
                return cast((Object) readMessage(input, (TStructDescriptor<?>) descriptor));
            default:
                FieldInfo info = readFieldInfo(input);
                if (info == null) {
                    throw new TSerializeException("Unexpected end of stream.");
                }
                return readFieldValue(input, info, descriptor);
        }
    }

    private <T extends TMessage<T>> T readMessage(InputStream input, TStructDescriptor<T> descriptor)
            throws TSerializeException, IOException {
        TMessageBuilder<T> builder = descriptor.factory().builder();
        FieldInfo fieldInfo;
        while ((fieldInfo = readFieldInfo(input)) != null) {
            TField<?> field = descriptor.getField(fieldInfo.getId());
            if (field != null) {
                Object value = readFieldValue(input, fieldInfo, field.getDescriptor());
                builder.set(field.getKey(), value);
            } else {
                if (mStrict) {
                    throw new TSerializeException(
                            "Unknown field " + fieldInfo.getId() + " for type" + descriptor.getQualifiedName(null));
                }
                readFieldValue(input, fieldInfo, null);
            }
        }
        return builder.build();
    }

    // --- READ METHODS ---

    /**
     * Consume a message from the stream without parsing the content into a message.
     *
     * @param in Stream to read message from.
     */
    protected void consumeMessage(InputStream in) throws IOException, TSerializeException {
        FieldInfo fieldInfo;
        while ((fieldInfo = readFieldInfo(in)) != null) {
            readFieldValue(in, fieldInfo, null);
        }
    }

    /**
     * Read field infd from stream. If this is the last field (field ID 0)
     * return null.
     *
     * @param in The stream to consume.
     * @return The field info or null.
     * @throws IOException
     */
    protected FieldInfo readFieldInfo(InputStream in) throws IOException, TSerializeException {
        int id = (int) readUnsigned(in, 2);
        if (id == 0) {
            return null;
        }

        int type_and_flag = in.read();
        if (type_and_flag == -1) {
            throw new TSerializeException("unable to read field info. End of stream.");
        } else if (type_and_flag == 0) {
            return null;
        }

        int type = (type_and_flag & 0xf0) >> 4;
        int flag = (type_and_flag & 0x0f);

        return new FieldInfo(id, DataType.findById(type), (byte) flag);
    }

    protected FieldInfo readEntryFieldInfo(InputStream in, int fieldId) throws IOException, TSerializeException {
        int type_and_flag = in.read();

        if (type_and_flag == 0) {
            return null;
        } else if (type_and_flag == -1) {
            throw new TSerializeException("unable to read field info.");
        }

        int type = (type_and_flag & 0xf0) >> 4;
        int flag = (type_and_flag & 0x0f);

        return new FieldInfo(fieldId, DataType.findById(type), (byte) flag);
    }

    /**
     * Read a field value from stream.
     *
     * @param in        The stream to consume.
     * @param fieldInfo The field info about the content.
     * @param type      The type to generate content for.
     * @return The field value, or null if no type.
     * @throws IOException If unable to read from stream or invalid field type.
     */
    protected <T> T readFieldValue(InputStream in, FieldInfo fieldInfo, TDescriptor<T> type)
            throws IOException, TSerializeException {
        switch (fieldInfo.getType()) {
            case BOOLEAN:
                if (type != null) {
                    switch (type.getType()) {
                        case BOOL:
                            return cast(fieldInfo.getBooleanValue());
                        case BYTE:
                            return cast((byte) fieldInfo.getIntegerValue());
                        case I16:
                            return cast((short) fieldInfo.getIntegerValue());
                        case I32:
                            return cast(fieldInfo.getIntegerValue());
                        case I64:
                            return cast((long) fieldInfo.getIntegerValue());
                        case DOUBLE:
                            return cast((double) fieldInfo.getIntegerValue());
                        default:
                            throw new TSerializeException("invalid type to parse boolean value: " + type.getName());
                    }
                }
                return null;
            case INTEGER:
                Long number = readSigned(in, fieldInfo.getNumericBytes());
                if (type != null) {
                    switch (type.getType()) {
                        case BOOL:
                            return cast(number.intValue() != 0);
                        case BYTE:
                            return cast(number.byteValue());
                        case I16:
                            return cast(number.shortValue());
                        case I32:
                            return cast(number.intValue());
                        case I64:
                            return cast(number);
                        case ENUM:
                            TEnumDescriptor<?> et = (TEnumDescriptor<?>) type;
                            TEnumBuilder<?> builder = et.factory().builder().setByValue(number.intValue());
                            if (mStrict && !builder.isValid()) {
                                throw new TSerializeException(number + " is not a valid " +
                                                              type.getQualifiedName(null) + " enum value.");
                            }
                            return cast(builder.build());
                        default:
                            throw new TSerializeException("invalid type for numeric value " + type.getName());
                    }
                }
                return null;
            case DOUBLE:
                return cast(readDouble(in));
            case BINARY:
                int bytes = fieldInfo.getBinaryLengthBytes();
                int length = (int) readUnsigned(in, bytes);
                byte[] binary = readBinary(in, length);
                if (type != null) {
                    switch (type.getType()) {
                        case BINARY:
                            return cast(binary);
                        case STRING:
                            return cast(new String(binary, fieldInfo.getStringEncoding()));
                        default:
                            throw new TSerializeException("Illegal type for binary encoding.");
                    }
                }
                return null;
            case MESSAGE:
                if (type != null) {
                    if (!type.getType().equals(TType.MESSAGE)) {
                        throw new TSerializeException("Invalid type for message encoding.");
                    }
                    return cast((Object) readMessage(in, (TStructDescriptor<?>) type));
                } else {
                    // consume message.
                    consumeMessage(in);
                    return null;
                }
            case COLLECTION:
                if (fieldInfo.isCollectionMap()) {
                    Map<Object,Object> out = new LinkedHashMap<>();

                    TDescriptor keyType = null;
                    TDescriptor valueType = null;
                    if (type != null) {
                        if (!type.getType().equals(TType.MAP)) {
                            throw new TSerializeException("Invalid type for map encoding.");
                        }

                        TMap<?,?> mapType = (TMap<?,?>) type;
                        keyType = mapType.keyDescriptor();
                        valueType = mapType.itemDescriptor();
                    }

                    FieldInfo entryInfo;
                    while ((entryInfo = readEntryFieldInfo(in, fieldInfo.getId())) != null) {
                        Object key = readFieldValue(in, entryInfo, keyType);
                        if ((entryInfo = readEntryFieldInfo(in, fieldInfo.getId())) == null) {
                            throw new TSerializeException("Unexpected end of map stream.");
                        }
                        Object value = readFieldValue(in, entryInfo, valueType);
                        if (key != null && value != null) {
                            out.put(key, value);
                        } else if (mStrict) {
                            throw new TSerializeException("Null key or value in map.");
                        }
                    }

                    return cast(out);
                } else {
                    Collection<Object> out = new LinkedList<>();

                    TDescriptor entryType = null;
                    if (type != null) {
                        if (!type.getType().equals(TType.LIST) &&
                                !type.getType().equals(TType.SET)) {
                            throw new TSerializeException("Invalid type for list encoding.");
                        }
                        entryType = ((TContainer<?, ?>) type).itemDescriptor();
                    }

                    FieldInfo entryInfo;
                    while ((entryInfo = readEntryFieldInfo(in, fieldInfo.getId())) != null) {
                        Object key = readFieldValue(in, entryInfo, entryType);
                        if (key != null) {
                            out.add(key);
                        }
                    }

                    return cast(out);
                }
            default:
                throw new TSerializeException("unknown data type.");
        }
    }

    /**
     * Read a double value from stream. It is always 64-bit / 8 bytes long.
     *
     * @param in The stream to read.
     * @return The double value.
     */
    protected double readDouble(InputStream in) throws IOException, TSerializeException {
        long number = readUnsigned(in, 8);
        return Double.longBitsToDouble(number);
    }

    /**
     * Read an unsigned integer from stream little endian style.
     *
     * @param in The stream to read.
     * @param numBytes Number of bytes to read.
     * @return The unsigned number.
     * @throws IOException
     */
    protected long readUnsigned(InputStream in, int numBytes) throws TSerializeException, IOException {
        long out = 0;
        int shift = 0;
        for (int i = 0; i < numBytes; ++i) {
            long read = (long) in.read();
            if (read == -1)
                throw new TSerializeException("Unexpected end of stream");
            out = out ^ (read << shift);
            shift += 8;
        }
        return out;
    }

    /**
     * Read an unsigned integer from stream little endian style.
     *
     * @param in The stream to read.
     * @param numBytes Number of bytes to read.
     * @return The signed number.
     * @throws IOException
     */
    protected long readSigned(InputStream in, final int numBytes) throws IOException, TSerializeException {
        long out = 0;
        int shift = 0;
        boolean negative = false;
        for (int i = 0; i < numBytes; ++i) {
            long read = (long) in.read();
            if (read == -1)
                throw new TSerializeException("Unexpected end of stream");
            if (i == (numBytes - 1)) {
                negative = (read & 0x80) != 0;
                read &= 0x7f;
            }
            out = out ^ (read << shift);
            if (negative && out == 0) {
                switch (numBytes) {
                    case 1: return Byte.MIN_VALUE;
                    case 2: return Short.MIN_VALUE;
                    case 4: return Integer.MIN_VALUE;
                    case 8: return Long.MIN_VALUE;
                    default: return - (1l << (numBytes * 8));
                }
            }
            shift += 8;
        }
        return negative ? -out : out;
    }

    /**
     * Read binary data from stream.
     * @param in Stream to read.
     * @param numBytes Number of bytes to read.
     * @return The byte array read.
     * @throws IOException
     */
    protected byte[] readBinary(InputStream in, final int numBytes) throws IOException, TSerializeException {
        byte[] out = new byte[numBytes];
        int pos = 0;
        while (pos < numBytes) {
            int len = in.read(out, pos, numBytes - pos);
            if (len < 0) {
                throw new TSerializeException("Unable to read enough data for binary.");
            }
            pos += len;
        }

        return out;
    }

    // --- WRITE METHODS ---

    /**
     * Write a field value to stream.
     *
     * @param out The stream to write to.
     * @param value The value to write.
     * @return The number of bytes written.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    protected int writeFieldValue(OutputStream out, Object value) throws IOException, TSerializeException {
        if (value instanceof TMessage) {
            int type = DataType.MESSAGE.id << 4;
            int flags = 0;
            out.write(type | flags);
            return 1 + serialize(out, (TMessage<?>) value);
        } else if (value instanceof Boolean) {
            int type = DataType.BOOLEAN.id << 4;
            int flags = (Boolean) value ? FieldInfo.TRUE : FieldInfo.FALSE;
            out.write(type | flags);
            return 1;
        } else if (value instanceof byte[]) {
            byte[] bytes = (byte[]) value;
            return writeBinary(out, FieldInfo.ENCODING_ISO_8859_1, bytes);
        } else if (value instanceof String) {
            byte[] bytes = ((String) value).getBytes(StandardCharsets.UTF_8);
            return writeBinary(out, FieldInfo.ENCODING_UTF_8, bytes);
        } else if (value instanceof Double) {
            return writeDouble(out, (Double) value);
        } else if (value instanceof Map) {
            int type = DataType.COLLECTION.id << 4;
            int flag = FieldInfo.COLLECTION_MAP;
            out.write(type | flag);
            int len = 0;
            for (Map.Entry<Object, Object> entry : ((Map<Object,Object>) value).entrySet()) {
                len += writeFieldValue(out, entry.getKey());
                len += writeFieldValue(out, entry.getValue());
            }
            out.write(0);
            return len + 2;
        } else if (value instanceof Collection) {
            int type = DataType.COLLECTION.id << 4;
            out.write(type);
            int len = 0;
            for (Object item : ((Collection<Object>) value)) {
                len += writeFieldValue(out, item);
            }
            out.write(0);
            return len + 2;
        } else {
            // enum and integers.
            long number;
            if (value instanceof TEnumValue) {
                number = ((TEnumValue<?>) value).getValue();
            } else {
                number = ((Number) value).longValue();
            }
            if (number == 0) {
                int type = DataType.BOOLEAN.id << 4;
                int flags = FieldInfo.FALSE;
                out.write(type | flags);
                return 1;
            }
            int flags = getNumericByteLengthFlag(number);
            int type = DataType.INTEGER.id << 4;
            out.write(type | flags);

            int numBytes = getNumericByteLength(number);
            return 1 + writeSigned(out, number, numBytes);
        }
    }

    /**
     * Write a double value to stream.
     *
     * @param out The stream to write to.
     * @param value The double value to write.
     * @return The number of bytes written.
     * @throws IOException
     */
    protected int writeDouble(OutputStream out, double value) throws IOException {
        int type = DataType.DOUBLE.id << 4;
        out.write(type);
        long number = Double.doubleToLongBits(value);
        return 1 + writeUnsigned(out, number, 8);
    }

    /**
     *
     * @param out
     * @param integer
     * @param bytes
     * @return
     * @throws IOException
     */
    protected int writeSigned(OutputStream out, long integer, final int bytes) throws IOException, TSerializeException {
        boolean negative = integer < 0;
        long number = integer < 0 ? -integer : integer;
        for (int i = 0; i < bytes; ++i) {
            int write;
            if (i == (bytes -1)) {
                write = (int) (number & 0x7fl);
                if (negative)
                    write |= 0x80;
            } else {
                write = (int) (number & 0xffl);
            }
            out.write(write);
            number = number & 0xffffffffffffff00l;
            number = number >> 8;
        }
        if (number > 0) {
            throw new TSerializeException("number " + integer + " is too large to write to " + bytes + " bytes");
        }
        return bytes;
    }

    /**
     * @param out
     * @param number
     * @param bytes
     * @return
     * @throws IOException
     */
    protected int writeUnsigned(OutputStream out, long number, final int bytes) throws IOException {
        for (int i = 0; i < bytes; ++i) {
            out.write((int) number & 0xff);
            number = number & 0xffffffffffffff00l;
            number = number >> 8;
        }
        return bytes;
    }

    /**
     *
     * @param out
     * @param flags
     * @param bytes
     * @return
     * @throws IOException
     */
    protected int writeBinary(OutputStream out, int flags, byte[] bytes) throws IOException {
        int type = DataType.BINARY.id << 4;
        int lengthBytes = getBinaryLengthBytes(bytes.length);
        flags |= (lengthBytes - 1);
        out.write(type | flags);
        writeUnsigned(out, bytes.length, lengthBytes);
        out.write(bytes, 0, bytes.length);

        return 1 + lengthBytes + bytes.length;
    }

    // --- HELPER METHODS ---

    /**
     * @param length
     * @return
     */
    protected int getBinaryLengthBytes(int length) {
        if (length > ((2 << 24) - 1))
            return 4;
        if (length > ((2 << 16) - 1))
            return 3;
        if (length > ((2 << 8) - 1))
            return 2;
        return 1;
    }

    /**
     *
     * @param value
     * @return
     */
    protected int getNumericByteLength(long value) {
        if (value > Integer.MAX_VALUE)
            return 8;
        if (value > Short.MAX_VALUE)
            return 4;
        if (value > Byte.MAX_VALUE)
            return 2;

        if (value < Integer.MIN_VALUE)
            return 8;
        if (value < Short.MIN_VALUE)
            return 4;
        if (value < Byte.MIN_VALUE)
            return 2;

        return 1;
    }

    /**
     *
     * @param value
     * @return
     */
    protected int getNumericByteLengthFlag(long value) {
        if (value > Integer.MAX_VALUE)
            return FieldInfo.FIXED_64;
        if (value > Short.MAX_VALUE)
            return FieldInfo.FIXED_32;
        if (value > Byte.MAX_VALUE)
            return FieldInfo.FIXED_16;

        if (value < Integer.MIN_VALUE)
            return FieldInfo.FIXED_64;
        if (value < Short.MIN_VALUE)
            return FieldInfo.FIXED_32;
        if (value < Byte.MIN_VALUE)
            return Byte.MAX_VALUE;
        return FieldInfo.FIXED_8;
    }

    // --- HELPER TYPES ---

    /**
     * DataType. Available values 1..7 (3-bit)
     */
    protected enum DataType {
        // data type 0 is only used for list termination.
        BOOLEAN(1),    // boolean / small int value in flags.
        INTEGER(2),    // -> byte, i16, i32, i64, little endian, signed.
        DOUBLE(3),     // -> 64 bit double
        BINARY(4),     // -> binary, string with encoding.
        MESSAGE(5),    // -> messages, terminated with field-ID 0.
        COLLECTION(6); // -> list, set, map, terminated with data-type 0.
        // unused: 7, TODO(morimekta): any message:
        //   - some identifier (TBD), plus binary data (with content length).

        public int id;

        DataType(int id) {
            this.id = id;
        }

        /**
         * Find data type by type ID.
         * @param id The ID number.
         * @return The DataType.
         */
        public static DataType findById(int id) {
            for (DataType type : values()) {
                if (id == type.id) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No such data type: " + id);
        }
    }

    /**
     * Field info data holder with convenience methods.
     */
    protected static class FieldInfo {
        private final int      mId;
        private final DataType mType;
        private final byte     mFlags;

        public FieldInfo(int id, DataType type, byte flags) {
            mId = id;
            mType = type;
            mFlags = flags;
        }

        public int getId() {
            return mId;
        }

        public DataType getType() {
            return mType;
        }

        public boolean getBooleanValue() {
            return (mFlags & 0x01) != 0;
        }

        public int getIntegerValue() {
            return mFlags;
        }

        public int getBinaryLengthBytes() {
            return (mFlags & 0x03) + 1;
        }

        public int getNumericBytes() {
            switch (mFlags & 0x03) {
                case 0:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 4;
                case 3:
                    return 8;
                default:
                    throw new IllegalArgumentException("OOPS");
            }
        }

        public Charset getStringEncoding() {
            switch ((mFlags % 0x04) >> 2) {
                case 0:
                    return StandardCharsets.ISO_8859_1;
                case 1:
                    return StandardCharsets.UTF_8;
                default:
                    throw new IllegalArgumentException("OOPS");
            }
        }

        public boolean isCollectionMap() {
            return (mFlags & 0x01) != 0;
        }

        public static final int ENCODING_ISO_8859_1 = 0x00;
        public static final int ENCODING_UTF_8      = 0x04;

        public static final int FIXED_8  = 0x00;
        public static final int FIXED_16 = 0x01;
        public static final int FIXED_32 = 0x02;
        public static final int FIXED_64 = 0x03;

        public static final int TRUE  = 0x01;
        public static final int FALSE = 0x00;

        public static final int COLLECTION_MAP = 0x01;
    }
}
