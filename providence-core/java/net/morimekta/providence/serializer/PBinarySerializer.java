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

package net.morimekta.providence.serializer;

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.util.Binary;
import net.morimekta.util.io.BinaryReader;
import net.morimekta.util.io.BinaryWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * Compact binary serializer. This uses the most compact binary format
 * allowable.
 * <p/>
 * See data definition file <code>docs/compact-binary.md</code> for format
 * spec.
 */
public class PBinarySerializer extends PSerializer {
    private final boolean mStrict;

    /**
     * Construct a serializer instance.
     */
    public PBinarySerializer() {
        this(true);
    }

    /**
     * Construct a serializer instance.
     */
    public PBinarySerializer(boolean strict) {
        mStrict = strict;
    }

    @Override
    public int serialize(OutputStream output, PMessage<?> message) throws IOException, PSerializeException {
        BinaryWriter writer = new BinaryWriter(output);
        int len = 0;
        if (message instanceof PUnion) {
            PField field = ((PUnion) message).unionField();
            if (field != null) {
                len += writer.writeUInt16(field.getKey());
                len += writeFieldValue(writer, message.get(field.getKey()));
            }
        } else {
            for (PField<?> field : message.descriptor()
                                          .getFields()) {
                if (message.has(field.getKey())) {
                    len += writer.writeUInt16(field.getKey());
                    len += writeFieldValue(writer, message.get(field.getKey()));
                }
            }
        }
        // write 2 null-bytes (field ID 0).
        len += writer.writeUInt16(0);
        return len;
    }

    @Override
    public <T> int serialize(OutputStream output, PDescriptor<T> descriptor, T value)
            throws IOException, PSerializeException {
        try {
            if (descriptor.getType()
                          .equals(PType.MESSAGE)) {
                return serialize(output, (PMessage<?>) value);
            } else {
                BinaryWriter writer = new BinaryWriter(output);
                return writeFieldValue(writer, value);
            }
        } catch (IOException e) {
            throw new PSerializeException(e, "Unable to write to stream");
        }
    }

    @Override
    public <T> T deserialize(InputStream input, PDescriptor<T> descriptor) throws PSerializeException, IOException {
        BinaryReader reader = new BinaryReader(input);
        // Assume it consists of a single field.
        if (PType.MESSAGE == descriptor.getType()) {
            return cast((Object) readMessage(reader, (PStructDescriptor<?, ?>) descriptor, true));
        } else {
            FieldInfo info = readFieldInfo(reader);
            if (info == null) {
                throw new PSerializeException("Unexpected end of stream.");
            }
            return readFieldValue(reader, info, descriptor);
        }
    }

    private <T extends PMessage<T>> T readMessage(BinaryReader input,
                                                  PStructDescriptor<T, ?> descriptor,
                                                  boolean nullable) throws PSerializeException, IOException {
        PMessageBuilder<T> builder = descriptor.factory()
                                               .builder();
        FieldInfo fieldInfo = readFieldInfo(input);
        if (nullable && fieldInfo == null) {
            return null;
        }
        while (fieldInfo != null) {
            PField<?> field = descriptor.getField(fieldInfo.getId());
            if (field != null) {
                Object value = readFieldValue(input, fieldInfo, field.getDescriptor());
                builder.set(field.getKey(), value);
            } else {
                if (mStrict) {
                    throw new PSerializeException(
                            "Unknown field " + fieldInfo.getId() + " for type" + descriptor.getQualifiedName(null));
                }
                readFieldValue(input, fieldInfo, null);
            }

            fieldInfo = readFieldInfo(input);
        }
        return builder.build();
    }

    // --- READ METHODS ---

    /**
     * Consume a message from the stream without parsing the content into a message.
     *
     * @param in Stream to read message from.
     */
    protected void consumeMessage(BinaryReader in) throws IOException, PSerializeException {
        FieldInfo fieldInfo;
        while ((fieldInfo = readFieldInfo(in)) != null) {
            readFieldValue(in, fieldInfo, null);
        }
    }

    /**
     * Read field info from stream. If this is the last field (field ID 0)
     * return null.
     *
     * @param in The stream to consume.
     * @return The field info or null.
     */
    protected FieldInfo readFieldInfo(BinaryReader in) throws IOException, PSerializeException {
        int id = in.readUInt16();
        if (id == 0) {
            return null;
        }

        int type_and_flag = in.expectUInt8();
        if (type_and_flag < 0x10) {
            throw new PSerializeException(String.format("No type on field", id));
        }

        return new FieldInfo(id, type_and_flag);
    }

    protected FieldInfo readEntryFieldInfo(BinaryReader in, int fieldId) throws IOException, PSerializeException {
        int type_and_flag = in.expectUInt8();
        if (type_and_flag < 0x10) {
            throw new PSerializeException("No type on entry.");
        }

        return new FieldInfo(fieldId, type_and_flag);
    }

    /**
     * Read a field value from stream.
     *
     * @param in        The stream to consume.
     * @param fieldInfo The field info about the content.
     * @param type      The type to generate content for.
     * @return The field value, or null if no type.
     *
     * @throws IOException If unable to read from stream or invalid field type.
     */
    protected <T> T readFieldValue(BinaryReader in, FieldInfo fieldInfo, PDescriptor<T> type)
            throws IOException, PSerializeException {
        switch (fieldInfo.getType()) {
            case DataType.BOOLEAN:
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
                            throw new PSerializeException("invalid type to parse boolean value: " + type.getName());
                    }
                }
                return null;
            case DataType.INTEGER:
                Long number = in.expectSigned(fieldInfo.getNumericBytes());
                if (type != null) {
                    switch (type.getType()) {
                        case BYTE:
                            return cast(number.byteValue());
                        case I16:
                            return cast(number.shortValue());
                        case I32:
                            return cast(number.intValue());
                        case I64:
                            return cast(number);
                        case ENUM:
                            PEnumDescriptor<?> et = (PEnumDescriptor<?>) type;
                            PEnumBuilder<?> builder = et.factory()
                                                        .builder()
                                                        .setByValue(number.intValue());
                            if (mStrict && !builder.isValid()) {
                                throw new PSerializeException(number + " is not a valid " +
                                                              type.getQualifiedName(null) + " enum value.");
                            }
                            return cast(builder.build());
                        default:
                            throw new PSerializeException("invalid type for numeric value " + type);
                    }
                }
                return null;
            case DataType.DOUBLE:
                return cast(in.expectDouble());
            case DataType.BINARY:
                int bytes = fieldInfo.getArrayLengthBytes();
                int length = in.expectUnsigned(bytes);
                byte[] binary = in.expectBytes(length);
                if (type != null) {
                    switch (type.getType()) {
                        case BINARY:
                            return cast(Binary.wrap(binary));
                        case STRING:
                            return cast(new String(binary, StandardCharsets.UTF_8));
                        default:
                            throw new PSerializeException("Illegal type for binary encoding: " + type);
                    }
                }
                return null;
            case DataType.MESSAGE:
                if (type != null) {
                    if (!type.getType()
                             .equals(PType.MESSAGE)) {
                        throw new PSerializeException("Invalid type for message encoding: " + type);
                    }
                    return cast((Object) readMessage(in, (PStructDescriptor<?, ?>) type, false));
                } else {
                    // consume message.
                    consumeMessage(in);
                    return null;
                }
            case DataType.MAP: {
                PDescriptor keyType = null;
                PDescriptor valueType = null;
                if (type != null) {
                    if (!type.getType()
                             .equals(PType.MAP)) {
                        throw new PSerializeException("Invalid type for map encoding: " + type);
                    }

                    PMap<?, ?> mapType = (PMap<?, ?>) type;
                    keyType = mapType.keyDescriptor();
                    valueType = mapType.itemDescriptor();
                }

                final int lb = fieldInfo.getArrayLengthBytes();
                final int size = in.expectUnsigned(lb);

                Map<Object, Object> out = new LinkedHashMap<>(size);

                FieldInfo entryInfo;
                for (int i = 0; i < size; ++i) {
                    if ((entryInfo = readEntryFieldInfo(in, fieldInfo.getId())) == null) {
                        throw new PSerializeException("Unexpected end of map stream.");
                    }
                    Object key = readFieldValue(in, entryInfo, keyType);
                    if ((entryInfo = readEntryFieldInfo(in, fieldInfo.getId())) == null) {
                        throw new PSerializeException("Unexpected end of map stream.");
                    }
                    Object value = readFieldValue(in, entryInfo, valueType);
                    if (key != null && value != null) {
                        out.put(key, value);
                    } else if (mStrict) {
                        throw new PSerializeException("Null key or value in map.");
                    }
                }
                return cast(out);
            }
            case DataType.COLLECTION: {
                PDescriptor entryType = null;
                if (type != null) {
                    if (!type.getType()
                             .equals(PType.LIST) && !type.getType()
                                                         .equals(PType.SET)) {
                        throw new PSerializeException("Invalid type for list encoding: " + type);
                    }
                    entryType = ((PContainer<?, ?>) type).itemDescriptor();
                }

                final int lb = fieldInfo.getArrayLengthBytes();
                final int size = in.expectUnsigned(lb);

                Collection<Object> out = type.getType()
                                             .equals(PType.LIST) ? new LinkedList<>() : new LinkedHashSet<>(size);

                FieldInfo entryInfo;
                for (int i = 0; i < size; ++i) {
                    if ((entryInfo = readEntryFieldInfo(in, fieldInfo.getId())) == null) {
                        throw new PSerializeException("Unexpected end of collection.");
                    }
                    Object key = readFieldValue(in, entryInfo, entryType);
                    if (key != null) {
                        out.add(key);
                    } else if (mStrict) {
                        throw new PSerializeException("Null value in collection.");
                    }
                }

                return cast(out);
            }
            default:
                throw new PSerializeException("unknown data type: " + fieldInfo.getType());
        }
    }

    // --- WRITE METHODS ---

    /**
     * Write a field value to stream.
     *
     * @param out   The stream to write to.
     * @param value The value to write.
     * @return The number of bytes written.
     */
    @SuppressWarnings("unchecked")
    protected int writeFieldValue(BinaryWriter out, Object value) throws IOException, PSerializeException {
        if (value instanceof PMessage) {
            int flags = DataType.MESSAGE;
            out.write(flags);
            return 1 + serialize(out, (PMessage<?>) value);
        } else if (value instanceof Boolean) {
            int flags = DataType.BOOLEAN | ((Boolean) value ? FieldInfo.TRUE : FieldInfo.FALSE);
            out.write(flags);
            return 1;
        } else if (value instanceof Binary) {
            return writeBinary(out, (Binary) value);
        } else if (value instanceof String) {
            return writeBinary(out, Binary.wrap(((String) value).getBytes(StandardCharsets.UTF_8)));
        } else if (value instanceof Double) {
            return writeDouble(out, (double) value);
        } else if (value instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) value;
            final int lengthBytes = getArrayLengthBytes(map.size());
            final int flags = DataType.MAP | (lengthBytes - 1);
            out.write(flags);
            int len = 1 + out.writeUnsigned(map.size(), lengthBytes);
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                len += writeFieldValue(out, entry.getKey());
                len += writeFieldValue(out, entry.getValue());
            }
            return len;
        } else if (value instanceof Collection) {
            Collection<Object> coll = (Collection<Object>) value;
            final int lengthBytes = getArrayLengthBytes(coll.size());
            final int flags = DataType.COLLECTION | (lengthBytes - 1);
            out.write(flags);
            int len = 1 + out.writeUnsigned(coll.size(), lengthBytes);
            for (Object item : ((Collection<Object>) value)) {
                len += writeFieldValue(out, item);
            }
            return len;
        } else if (value instanceof Byte) {
            int number = (byte) value;
            out.write(DataType.INTEGER | getNumericByteLengthFlag(number));
            return 1 + out.writeSigned(number, getNumericByteLength(number));
        } else if (value instanceof Short) {
            int number = (short) value;
            out.write(DataType.INTEGER | getNumericByteLengthFlag(number));
            return 1 + out.writeSigned(number, getNumericByteLength(number));
        } else if (value instanceof Integer) {
            int number = (int) value;
            out.write(DataType.INTEGER | getNumericByteLengthFlag(number));
            return 1 + out.writeSigned(number, getNumericByteLength(number));
        } else if (value instanceof Long) {
            long number = (long) value;
            out.write(DataType.INTEGER | getNumericByteLengthFlag(number));
            return 1 + out.writeSigned(number, getNumericByteLength(number));
        } else if (value instanceof PEnumValue) {
            int number = ((PEnumValue<?>) value).getValue();
            out.write(DataType.INTEGER | getNumericByteLengthFlag(number));
            return 1 + out.writeSigned(number, getNumericByteLength(number));
        } else {
            throw new PSerializeException("");
        }
    }

    /**
     * Write a double value to stream.
     *
     * @param out   The stream to write to.
     * @param value The double value to write.
     * @return The number of bytes written.
     */
    protected int writeDouble(BinaryWriter out, double value) throws IOException {
        int flags = DataType.DOUBLE;
        out.write(flags);
        return 1 + out.writeDouble(value);
    }

    /**
     * @param out
     * @param bytes
     * @return
     * @throws IOException
     */
    protected int writeBinary(BinaryWriter out, Binary bytes) throws IOException {
        int lengthBytes = getArrayLengthBytes(bytes.length());
        int flags = DataType.BINARY | ((lengthBytes - 1) & 0x03);
        out.write(flags);
        out.writeUnsigned(bytes.length(), lengthBytes);
        bytes.write(out);

        return 1 + lengthBytes + bytes.length();
    }

    // --- HELPER METHODS ---

    /**
     * @param length
     * @return
     */
    protected int getArrayLengthBytes(int length) {
        if (length >= ((1 << 24) - 1)) {
            return 4;
        }
        if (length >= ((1 << 16) - 1)) {
            return 3;
        }
        if (length >= ((1 << 8) - 1)) {
            return 2;
        }
        return 1;
    }

    /**
     * @param value
     * @return
     */
    protected int getNumericByteLength(long value) {
        if (value > Integer.MAX_VALUE) {
            return 8;
        }
        if (value > Short.MAX_VALUE) {
            return 4;
        }
        if (value > Byte.MAX_VALUE) {
            return 2;
        }

        if (value < Integer.MIN_VALUE) {
            return 8;
        }
        if (value < Short.MIN_VALUE) {
            return 4;
        }
        if (value < Byte.MIN_VALUE) {
            return 2;
        }

        return 1;
    }

    /**
     * @param value
     * @return
     */
    protected int getNumericByteLengthFlag(long value) {
        if (value > Integer.MAX_VALUE) {
            return FieldInfo.FIXED_64;
        }
        if (value > Short.MAX_VALUE) {
            return FieldInfo.FIXED_32;
        }
        if (value > Byte.MAX_VALUE) {
            return FieldInfo.FIXED_16;
        }

        if (value < Integer.MIN_VALUE) {
            return FieldInfo.FIXED_64;
        }
        if (value < Short.MIN_VALUE) {
            return FieldInfo.FIXED_32;
        }
        if (value < Byte.MIN_VALUE) {
            return FieldInfo.FIXED_16;
        }
        return FieldInfo.FIXED_8;
    }

    // --- HELPER TYPES ---

    /**
     * DataType. Available values 1..7 (3-bit)
     */
    protected interface DataType {
        int BOOLEAN    = 0x10;  // boolean / small int value in flags.
        int INTEGER    = 0x20;  // -> byte, i16, i32, i64, little endian, signed.
        int DOUBLE     = 0x30;  // -> 64 bit double
        int BINARY     = 0x40;  // -> binary, string with encoding.
        int MESSAGE    = 0x50;  // -> messages, terminated with field-ID 0.
        int COLLECTION = 0x60;  // -> list, set.
        int MAP        = 0x70;  // -> map.
    }

    /**
     * Field info data holder with convenience methods.
     */
    protected static class FieldInfo {
        private final int id;
        private final int type;
        private final int flag;

        public FieldInfo(int id, int flags) {
            this.id = id;
            this.type = flags & 0xf0;
            this.flag = flags & 0x0f;
        }

        @Override
        public String toString() {
            return String.format("field(%d: %1x, %1x)", id, type >> 4, flag);
        }

        public int getId() {
            return id;
        }

        public int getType() {
            return type;
        }

        public boolean getBooleanValue() {
            return (flag & TRUE) != 0;
        }

        public int getIntegerValue() {
            return flag;
        }

        public int getArrayLengthBytes() {
            return (flag & 0x03) + 1;
        }

        public int getNumericBytes() throws PSerializeException {
            switch (flag & 0x03) {
                case FIXED_8:
                    return Byte.BYTES;
                case FIXED_16:
                    return Short.BYTES;
                case FIXED_32:
                    return Integer.BYTES;
                case FIXED_64:
                    return Long.BYTES;
                default:
                    throw new PSerializeException("OOPS");
            }
        }

        public static final int FIXED_8  = 0x00;
        public static final int FIXED_16 = 0x01;
        public static final int FIXED_32 = 0x02;
        public static final int FIXED_64 = 0x03;

        public static final int TRUE  = 0x01;
        public static final int FALSE = 0x00;
    }
}
