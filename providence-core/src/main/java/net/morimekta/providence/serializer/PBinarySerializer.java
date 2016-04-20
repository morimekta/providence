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
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.util.Binary;
import net.morimekta.util.io.BinaryReader;
import net.morimekta.util.io.BinaryWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Compact binary serializer. This usesd a format that is as close the the default
 * thrift binary protocol as possible.
 * <p>
 * See data definition file <code>docs/serializer-binary.md</code> for format
 * spec.
 */
public class PBinarySerializer extends PSerializer {
    private final boolean readStrict;

    /**
     * Construct a serializer instance.
     */
    public PBinarySerializer() {
        this(true);
    }

    /**
     * Construct a serializer instance.
     *
     * @param strict If the serializer should fail on reading mismatched data.
     */
    public PBinarySerializer(boolean strict) {
        readStrict = strict;
    }

    @Override
    public int serialize(OutputStream output, PMessage<?> message) throws IOException, PSerializeException {
        BinaryWriter writer = new BinaryWriter(output);
        int len = 0;
        if (message instanceof PUnion) {
            PField field = ((PUnion) message).unionField();
            if (field != null) {
                len += writer.writeUInt16((short) field.getKey());
                len += writeFieldValue(writer, message.get(field.getKey()));
            }
        } else {
            for (PField<?> field : message.descriptor()
                                          .getFields()) {
                if (message.has(field.getKey())) {
                    len += writer.writeUInt16((short) field.getKey());
                    len += writeFieldValue(writer, message.get(field.getKey()));
                }
            }
        }
        // write 2 null-bytes (field ID 0).
        len += writer.writeUInt16(0);
        return len;
    }

    @Override
    public <T> T deserialize(InputStream input, PDescriptor<T> descriptor) throws PSerializeException, IOException {
        BinaryReader reader = new BinaryReader(input);
        // Assume it consists of a single field.
        if (PType.MESSAGE == descriptor.getType()) {
            return cast(readMessage(reader, (PStructDescriptor<?, ?>) descriptor, true));
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
        PMessageBuilder<T> builder = descriptor.builder();
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
                if (readStrict) {
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
    private void consumeMessage(BinaryReader in) throws IOException, PSerializeException {
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
    private FieldInfo readFieldInfo(BinaryReader in) throws IOException, PSerializeException {
        int id = in.readUInt16();
        if (id == 0) {
            return null;
        }
        return new FieldInfo(id, in.expectByte());
    }

    private FieldInfo readEntryFieldInfo(BinaryReader in, int fieldId) throws IOException, PSerializeException {
        return new FieldInfo(fieldId, in.expectByte());
    }

    /**
     * Read a field value from stream.
     *
     * @param in        The stream to consume.
     * @param fieldInfo The field info about the content.
     * @param type      The type to generate content for.
     * @param <T>       The field item type.
     * @return The field value, or null if no type.
     *
     * @throws IOException If unable to read from stream or invalid field type.
     */
    private <T> T readFieldValue(BinaryReader in, FieldInfo fieldInfo, PDescriptor<T> type)
            throws IOException, PSerializeException {
        switch (PType.findById(fieldInfo.getType())) {
            case BOOL:
                return cast(in.expectByte() != 0);
            case BYTE:
                return cast(in.expectByte());
            case I16:
                return cast(in.expectShort());
            case I32:
                int val = in.expectInt();
                if (type instanceof PEnumDescriptor) {
                    @SuppressWarnings("unchecked")
                    PEnumBuilder<T> builder = (PEnumBuilder<T>) ((PEnumDescriptor<?>)type).builder();
                    builder.setByValue(val);
                    return cast(builder.build());
                } else {
                    return cast(val);
                }
            case I64:
                return cast(in.expectLong());
            case DOUBLE:
                return cast(in.expectDouble());
            case STRING:
            case BINARY:
                int len = in.expectUInt32();
                byte[] data = in.expectBytes(len);
                if (type.getType() == PType.STRING) {
                    return cast(new String(data, StandardCharsets.UTF_8));
                } else {
                    return cast(Binary.wrap(data));
                }
            case MESSAGE: {
                if (type == null) {
                    consumeMessage(in);
                    return null;
                }
                return cast(readMessage(in, (PStructDescriptor<?,?>) type, readStrict));
            }
            case MAP: {
                PDescriptor keyType = null;
                PDescriptor valueType = null;
                PMap.Builder<Object, Object> out;
                if (type != null) {
                    if (!type.getType()
                             .equals(PType.MAP)) {
                        throw new PSerializeException("Invalid type for map encoding: " + type);
                    }

                    PMap<Object, Object> mapType = (PMap<Object, Object>) type;
                    keyType = mapType.keyDescriptor();
                    valueType = mapType.itemDescriptor();

                    out = mapType.builder();
                } else {
                    out = new PMap.ImmutableMapBuilder<>();
                }

                final int size = in.expectUInt32();

                FieldInfo entryInfo;
                for (int i = 0; i < size; ++i) {
                    entryInfo = readEntryFieldInfo(in, fieldInfo.getId());
                    Object key = readFieldValue(in, entryInfo, keyType);
                    entryInfo = readEntryFieldInfo(in, fieldInfo.getId());
                    Object value = readFieldValue(in, entryInfo, valueType);
                    if (key != null && value != null) {
                        out.put(key, value);
                    } else if (readStrict) {
                        throw new PSerializeException("Null key or value in map.");
                    }
                }
                return cast(out.build());
            }
            case SET: {
                PDescriptor entryType = null;
                PSet.Builder<Object> out;
                if (type != null) {
                    PSet<Object> setType = (PSet<Object>) type;
                    entryType = setType.itemDescriptor();
                    out = setType.builder();
                } else {
                    out = new PSet.ImmutableSetBuilder<>();
                }

                final int size = in.expectUInt32();

                FieldInfo entryInfo;
                for (int i = 0; i < size; ++i) {
                    entryInfo = readEntryFieldInfo(in, fieldInfo.getId());
                    Object key = readFieldValue(in, entryInfo, entryType);
                    if (key != null) {
                        out.add(key);
                    } else if (readStrict) {
                        throw new PSerializeException("Null value in set.");
                    }
                }

                return cast(out.build());
            }
            case LIST: {
                PDescriptor entryType = null;
                PList.Builder<Object> out;
                if (type != null) {
                    PList<Object> setType = (PList<Object>) type;
                    entryType = setType.itemDescriptor();
                    out = setType.builder();
                } else {
                    out = new PList.ImmutableListBuilder<>();
                }

                final int size = in.expectUInt32();

                FieldInfo entryInfo;
                for (int i = 0; i < size; ++i) {
                    entryInfo = readEntryFieldInfo(in, fieldInfo.getId());
                    Object key = readFieldValue(in, entryInfo, entryType);
                    if (key != null) {
                        out.add(key);
                    } else if (readStrict) {
                        throw new PSerializeException("Null value in list.");
                    }
                }

                return cast(out.build());
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
    private int writeFieldValue(BinaryWriter out, Object value) throws IOException, PSerializeException {
        if (value instanceof Boolean) {
            out.writeByte(PType.BOOL.id);
            return 1 + out.writeByte(((Boolean) value) ? (byte) 1 : (byte) 0);
        } else if (value instanceof Byte) {
            out.writeByte(PType.BYTE.id);
            return 1 + out.writeByte((Byte) value);
        } else if (value instanceof Short) {
            out.writeByte(PType.I16.id);
            return 1 + out.writeShort((Short) value);
        } else if (value instanceof Integer) {
            out.writeByte(PType.I32.id);
            return 1 + out.writeInt((Integer) value);
        } else if (value instanceof Long) {
            out.writeByte(PType.I64.id);
            return 1 + out.writeLong((Long) value);
        } else if (value instanceof Double) {
            out.writeByte(PType.DOUBLE.id);
            return 1 + out.writeDouble((Double) value);
        } else if (value instanceof Binary) {
            out.writeByte(PType.BINARY.id);
            Binary binary = (Binary) value;
            int lenBytes = out.writeUInt32(binary.length());
            return 1 + lenBytes + out.writeBinary(binary);
        } else if (value instanceof CharSequence) {
            out.writeByte(PType.STRING.id);
            Binary binary = Binary.wrap(value.toString().getBytes(StandardCharsets.UTF_8));
            int lenBytes = out.writeUInt32(binary.length());
            return 1 + lenBytes + out.writeBinary(binary);
        } else if (value instanceof PEnumValue) {
            out.writeByte(PType.I32.id);
            return 1 + out.writeInt(((PEnumValue<?>) value).getValue());
        } else if (value instanceof Map) {
            out.writeByte(PType.MAP.id);
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) value;
            int len = out.writeUInt32(map.size());
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                len += writeFieldValue(out, entry.getKey());
                len += writeFieldValue(out, entry.getValue());
            }
            return 1 + len;
        } else if (value instanceof Collection) {
            if (value instanceof Set) {
                out.writeByte(PType.SET.id);
            } else {
                out.writeByte(PType.LIST.id);
            }
            @SuppressWarnings("unchecked")
            Collection<Object> coll = (Collection<Object>) value;
            int len = out.writeUInt32(coll.size());
            for (Object item : coll) {
                len += writeFieldValue(out, item);
            }
            return 1 + len;
        } else if (value instanceof PMessage) {
            out.writeByte(PType.MESSAGE.id);
            return 1 + serialize(out, (PMessage<?>) value);
        } else {
            throw new PSerializeException("");
        }
    }

    /**
     * Field info data holder with convenience methods.
     */
    private static class FieldInfo {
        private final int id;
        private final byte type;

        private FieldInfo(int id, byte type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public String toString() {
            return String.format("field(%d: %1x)", id, type);
        }

        public int getId() {
            return id;
        }

        public byte getType() {
            return type;
        }

    }
}
