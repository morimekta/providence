/*
 * Copyright 2017 Providence Authors
 *
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
package net.morimekta.providence.serializer.binary;

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
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.Binary;
import net.morimekta.util.io.BigEndianBinaryReader;
import net.morimekta.util.io.BigEndianBinaryWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import static net.morimekta.providence.serializer.binary.BinaryType.asString;
import static net.morimekta.providence.serializer.binary.BinaryType.forType;

/**
 * Utilities helping with reading and writing binary format (protocol)
 * messages.
 *
 * NOTE: This class is not meant to be used directly, use the
 * {@link net.morimekta.providence.serializer.BinarySerializer} serializer
 * class instead. It is separated out and made public in order for
 * pre-generated serialization code to be able to use the same utility
 * methods.
 */
public class BinaryFormatUtils {
    /**
     * Field info data holder with convenience methods.
     */
    public static class FieldInfo {
        private final int id;
        private final byte type;

        /**
         * Create a field info instance.
         *
         * @param id The field ID or key.
         * @param type The field binary written type.
         */
        public FieldInfo(int id, byte type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public String toString() {
            return String.format("field(%d: %s)", id, asString(type));
        }

        /**
         * @return The field ID or key.
         */
        public int getId() {
            return id;
        }

        /**
         * @return The binary field type.
         */
        public byte getType() {
            return type;
        }
    }

    /**
     * Read message from reader.
     *
     * @param input The input reader.
     * @param descriptor The message descriptor.
     * @param strict If the message should be read in strict mode.
     * @param <Message> The message type.
     * @param <Field> The field type.
     * @return The read and parsed message.
     * @throws IOException If read failed.
     */
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Message readMessage(BigEndianBinaryReader input,
                        PMessageDescriptor<Message, Field> descriptor,
                        boolean strict) throws IOException {
        PMessageBuilder<Message, Field> builder = descriptor.builder();
        if (builder instanceof BinaryReader) {
            ((BinaryReader) builder).readBinary(input, strict);
        } else {
            FieldInfo fieldInfo = readFieldInfo(input);
            while (fieldInfo != null) {
                PField field = descriptor.findFieldById(fieldInfo.getId());
                if (field != null) {
                    Object value = readFieldValue(input, fieldInfo, field.getDescriptor(), strict);
                    builder.set(field.getId(), value);
                } else {
                    readFieldValue(input, fieldInfo, null, false);
                }

                fieldInfo = readFieldInfo(input);
            }

            if (strict) {
                try {
                    builder.validate();
                } catch (IllegalStateException e) {
                    throw new SerializerException(e, e.getMessage());
                }
            }
        }

        return builder.build();
    }

    /**
     * Consume a message from the stream without parsing the content into a message.
     *
     * @param in Stream to read message from.
     * @throws IOException On read failures.
     */
    public static void consumeMessage(BigEndianBinaryReader in) throws IOException {
        FieldInfo fieldInfo;
        while ((fieldInfo = readFieldInfo(in)) != null) {
            readFieldValue(in, fieldInfo, null, false);
        }
    }

    /**
     * Read field info from stream. If this is the last field (field ID 0)
     * return null.
     *
     * @param in The stream to consume.
     * @return The field info or null.
     * @throws IOException If read failed.
     */
    private static FieldInfo readFieldInfo(BigEndianBinaryReader in) throws IOException {
        byte type = in.expectByte();
        if (type == BinaryType.STOP) {
            return null;
        }
        return new FieldInfo(in.expectShort(), type);
    }

    /**
     * Read a field value from stream.
     *
     * @param in        The stream to consume.
     * @param fieldInfo The field info about the content.
     * @param fieldType The type to generate content for.
     * @param strict    If the field should be read strictly.
     * @return The field value, or null if no type.
     * @throws IOException If unable to read from stream or invalid field type.
     */
    public static Object readFieldValue(BigEndianBinaryReader in,
                                        FieldInfo fieldInfo,
                                        PDescriptor fieldType,
                                        boolean strict)
            throws IOException {
        if (fieldType != null && forType(fieldType.getType()) != fieldInfo.type) {
            throw new SerializerException("Wrong field type for id=%d: expected %s, got %s",
                                          fieldInfo.id,
                                          asString(forType(fieldType.getType())),
                                          asString(fieldInfo.getType()));
        }

        switch (fieldInfo.type) {
            case BinaryType.VOID:
                return Boolean.TRUE;
            case BinaryType.BOOL:
                return in.expectByte() != 0;
            case BinaryType.BYTE:
                return in.expectByte();
            case BinaryType.I16:
                return in.expectShort();
            case BinaryType.I32:
                int val = in.expectInt();
                if (fieldType != null && fieldType instanceof PEnumDescriptor) {
                    @SuppressWarnings("unchecked")
                    PEnumBuilder builder = ((PEnumDescriptor<?>)fieldType).builder();
                    builder.setById(val);
                    return builder.build();
                } else {
                    return val;
                }
            case BinaryType.I64:
                return in.expectLong();
            case BinaryType.DOUBLE:
                return in.expectDouble();
            case BinaryType.STRING:
                int len = in.expectUInt32();
                byte[] data = in.expectBytes(len);
                if (fieldType != null && fieldType.getType() == PType.STRING) {
                    return new String(data, StandardCharsets.UTF_8);
                } else {
                    return Binary.wrap(data);
                }
            case BinaryType.STRUCT: {
                if (fieldType == null) {
                    consumeMessage(in);
                    return null;
                }
                return readMessage(in, (PMessageDescriptor<?,?>) fieldType, strict);
            }
            case BinaryType.MAP: {
                final byte keyT = in.expectByte();
                final byte itemT = in.expectByte();
                final int size = in.expectUInt32();

                PDescriptor keyType = null;
                PDescriptor valueType = null;
                PMap.Builder<Object, Object> out;
                if (fieldType != null) {
                    @SuppressWarnings("unchecked")
                    PMap<Object, Object> mapType = (PMap<Object, Object>) fieldType;
                    keyType = mapType.keyDescriptor();
                    valueType = mapType.itemDescriptor();

                    out = mapType.builder();
                } else {
                    out = new PMap.DefaultBuilder<>();
                }

                FieldInfo keyInfo = new FieldInfo(1, keyT);
                FieldInfo itemInfo = new FieldInfo(2, itemT);
                for (int i = 0; i < size; ++i) {
                    Object key = readFieldValue(in, keyInfo, keyType, strict);
                    Object value = readFieldValue(in, itemInfo, valueType, strict);
                    if (key != null && value != null) {
                        out.put(key, value);
                    } else if (strict) {
                        if (key == null) {
                            throw new SerializerException("Null key in map");
                        } else {
                            throw new SerializerException("Null value in map");
                        }
                    }
                }
                return out.build();
            }
            case BinaryType.SET: {
                final byte itemT = in.expectByte();
                final int size = in.expectUInt32();

                PDescriptor entryType = null;
                PSet.Builder<Object> out;
                if (fieldType != null) {
                    @SuppressWarnings("unchecked")
                    PSet<Object> setType = (PSet<Object>) fieldType;
                    entryType = setType.itemDescriptor();
                    out = setType.builder();
                } else {
                    out = new PSet.DefaultBuilder<>();
                }

                FieldInfo itemInfo = new FieldInfo(0, itemT);
                for (int i = 0; i < size; ++i) {
                    Object value = readFieldValue(in, itemInfo, entryType, strict);
                    if (value != null) {
                        out.add(value);
                    } else if (strict) {
                        throw new SerializerException("Null value in set");
                    }
                }

                return out.build();
            }
            case BinaryType.LIST: {
                final byte itemT = in.expectByte();
                final int size = in.expectUInt32();

                PDescriptor entryType = null;
                PList.Builder<Object> out;
                if (fieldType != null) {
                    @SuppressWarnings("unchecked")
                    PList<Object> listType = (PList<Object>) fieldType;
                    entryType = listType.itemDescriptor();
                    out = listType.builder();
                } else {
                    out = new PList.DefaultBuilder<>();
                }

                FieldInfo itemInfo = new FieldInfo(0, itemT);
                for (int i = 0; i < size; ++i) {
                    Object value = readFieldValue(in, itemInfo, entryType, strict);
                    if (value != null) {
                        out.add(value);
                    } else if (strict) {
                        throw new SerializerException("Null value in list");
                    }
                }

                return out.build();
            }
            default:
                throw new SerializerException("unknown data type: " + fieldInfo.getType());
        }
    }

    // --- WRITE METHODS ---

    /**
     * Write message to writer.
     *
     * @param writer The binary writer.
     * @param message The message to write.
     * @param <Message> The message type.
     * @param <Field> The field type.
     * @return The number of bytes written.
     * @throws IOException If write failed.
     */
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    int writeMessage(BigEndianBinaryWriter writer, Message message)
            throws IOException {
        if (message instanceof BinaryWriter) {
            return ((BinaryWriter) message).writeBinary(writer);
        }

        int len = 0;
        if (message instanceof PUnion) {
            if (((PUnion) message).unionFieldIsSet()) {
                PField field = ((PUnion) message).unionField();
                len += writeFieldSpec(writer, forType(field.getDescriptor().getType()), field.getId());
                len += writeFieldValue(writer,
                                       message.get(field.getId()),
                                       field.getDescriptor());
            }
        } else {
            for (PField field : message.descriptor().getFields()) {
                if (message.has(field.getId())) {
                    len += writeFieldSpec(writer, forType(field.getDescriptor().getType()), field.getId());
                    len += writeFieldValue(writer,
                                           message.get(field.getId()),
                                           field.getDescriptor());
                }
            }
        }
        len += writer.writeUInt8(BinaryType.STOP);
        return len;
    }

    private static int writeFieldSpec(BigEndianBinaryWriter out, byte type, int key) throws IOException {
        out.writeByte(type);
        out.writeShort((short) key);
        return 3;
    }

    private static int writeFieldValue(BigEndianBinaryWriter out, Object value, PDescriptor descriptor) throws IOException {
        switch (descriptor.getType()) {
            case VOID:
                return 0;
            case BOOL:
                return out.writeByte(((Boolean) value) ? (byte) 1 : (byte) 0);
            case BYTE:
                return out.writeByte((Byte) value);
            case I16:
                return out.writeShort((Short) value);
            case I32:
                return out.writeInt((Integer) value);
            case I64:
                return out.writeLong((Long) value);
            case DOUBLE:
                return out.writeDouble((Double) value);
            case BINARY: {
                Binary binary = (Binary) value;
                int len = out.writeUInt32(binary.length());
                return len + out.writeBinary(binary);
            }
            case STRING: {
                Binary binary = Binary.wrap(value.toString().getBytes(StandardCharsets.UTF_8));
                int len = out.writeUInt32(binary.length());
                return len + out.writeBinary(binary);
            }
            case ENUM:
                return out.writeInt(((PEnumValue<?>) value).asInteger());
            case MAP: {
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) value;
                PMap<?,?> pMap = (PMap<?, ?>) descriptor;
                int len = out.writeByte(forType(pMap.keyDescriptor().getType()));
                len += out.writeByte(forType(pMap.itemDescriptor().getType()));
                len += out.writeUInt32(map.size());
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    len += writeFieldValue(out, entry.getKey(), pMap.keyDescriptor());
                    len += writeFieldValue(out, entry.getValue(), pMap.itemDescriptor());
                }
                return len;
            }
            case SET:
            case LIST: {
                @SuppressWarnings("unchecked")
                Collection<Object> coll = (Collection<Object>) value;
                PContainer<?> pSet = (PContainer<?>) descriptor;

                int len = out.writeByte(forType(pSet.itemDescriptor().getType()));
                len += out.writeUInt32(coll.size());

                for (Object item : coll) {
                    len += writeFieldValue(out, item, pSet.itemDescriptor());
                }
                return len;
            }
            case MESSAGE: {
                @SuppressWarnings("unchecked")
                int size = writeMessage(out, (PMessage) value);
                return size;
            }
            default:
                throw new SerializerException("Unhandled field type: " + descriptor.getType());
        }
    }
}
