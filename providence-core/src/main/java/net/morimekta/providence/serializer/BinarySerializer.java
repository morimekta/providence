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
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.PType;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.util.Binary;
import net.morimekta.util.io.BigEndianBinaryReader;
import net.morimekta.util.io.BigEndianBinaryWriter;
import net.morimekta.util.io.BinaryReader;
import net.morimekta.util.io.BinaryWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Compact binary serializer. This usesd a format that is as close the the default
 * thrift binary protocol as possible.
 * <p>
 * See data definition file <code>docs/serializer-binary.md</code> for format
 * spec.
 */
public class BinarySerializer extends Serializer {
    public static final String MIME_TYPE = "application/vnd.apache.thrift.binary";

    private static final int VERSION_MASK = 0xffff0000;
    private static final int VERSION_1    = 0x80010000;

    private final boolean readStrict;
    private final boolean versioned;

    /**
     * Construct a serializer instance.
     */
    public BinarySerializer() {
        this(true);
    }

    public BinarySerializer(boolean readStrict) {
        this(readStrict, false);
    }

    /**
     * Construct a serializer instance.
     *
     * @param readStrict If the serializer should fail on reading mismatched data.
     */
    public BinarySerializer(boolean readStrict, boolean versioned) {
        this.readStrict = readStrict;
        this.versioned = versioned;
    }

    @Override
    public boolean binaryProtocol() {
        return true;
    }

    @Override
    public String mimeType() {
        return MIME_TYPE;
    }

    @Override
    public <T extends PMessage<T>> int serialize(OutputStream os, T message) throws IOException, SerializerException {
        BinaryWriter writer = new BigEndianBinaryWriter(os);
        return writeMessage(writer, message);
    }

    @Override
    public <T extends PMessage<T>> int serialize(OutputStream os, PServiceCall<T> call)
            throws IOException, SerializerException {
        BinaryWriter out = new BigEndianBinaryWriter(os);
        byte[] method = call.getMethod().getBytes(UTF_8);

        int len = method.length;
        if (versioned) {
            len += out.writeInt(VERSION_1 | (byte) call.getType().key);
            len += out.writeInt(method.length);
            out.write(method);
        } else {
            len += out.writeInt(method.length);
            out.write(method);
            len += out.writeByte((byte) call.getType().key);
        }
        len += out.writeInt(call.getSequence());
        len += writeMessage(out, call.getMessage());
        return len;
    }

    @Override
    public <T extends PMessage<T>, TF extends PField> T deserialize(InputStream input, PStructDescriptor<T, TF> descriptor) throws
                                                                                                                            SerializerException, IOException {
        BinaryReader reader = new BigEndianBinaryReader(input);
        return readMessage(reader, descriptor, true);
    }

    @Override
    public <T extends PMessage<T>> PServiceCall<T> deserialize(InputStream is, PService service)
            throws IOException, SerializerException {
        BinaryReader in = new BigEndianBinaryReader(is);

        int methodNameLen = in.expectInt();
        int typeKey;
        String methodName;
        PServiceMethod method;
        int sequence;
        // Accept both "strict" read mode and non-strict.
        // versioned
        if (methodNameLen < 0) {
            int version = methodNameLen & VERSION_MASK;
            if (version == VERSION_1) {
                typeKey = methodNameLen & 0xFF;
                methodNameLen = in.expectInt();
                methodName = new String(in.expectBytes(methodNameLen), UTF_8);
                method = service.getMethod(methodName);
                sequence = in.expectInt();
            } else {
                throw new SerializerException("Bad protocol version: %08x", version >>> 16);
            }
        } else {
            if (readStrict) {
                throw new SerializerException("Missing protocol version");
            }

            methodName = new String(in.expectBytes(methodNameLen), UTF_8);
            method = service.getMethod(methodName);
            if (method == null) {
                throw new SerializerException("No such method " + methodName + " on " + service.getQualifiedName(null));
            }
            typeKey = in.expectByte();
            sequence = in.expectInt();
        }

        PServiceCallType type = PServiceCallType.findByKey(typeKey);
        if (type == null) {
            throw new SerializerException("Invalid call type " + typeKey);
        }

        @SuppressWarnings("unchecked")
        PStructDescriptor<T,?> descriptor = type.request ? method.getRequestType() : method.getResponseType();

        T message = readMessage(in, descriptor, false);

        return new PServiceCall<>(methodName, type, sequence, message);
    }

    private <T extends PMessage<T>> int writeMessage(BinaryWriter writer, T message) throws IOException,
                                                                                            SerializerException {
        int len = 0;
        if (message instanceof PUnion) {
            PField field = ((PUnion) message).unionField();
            if (field != null) {
                len += writeFieldSpec(writer, field.getDescriptor().getType().id, field.getKey());
                len += writeFieldValue(writer,
                                       message.get(field.getKey()),
                                       field.getDescriptor());
            }
        } else {
            for (PField field : message.descriptor()
                                          .getFields()) {
                if (message.has(field.getKey())) {
                    len += writeFieldSpec(writer, field.getDescriptor().getType().id, field.getKey());
                    len += writeFieldValue(writer,
                                           message.get(field.getKey()),
                                           field.getDescriptor());
                }
            }
        }
        len += writer.writeUInt8(PType.STOP.id);
        return len;
    }

    private <T extends PMessage<T>> T readMessage(BinaryReader input,
                                                  PStructDescriptor<T, ?> descriptor,
                                                  boolean nullable) throws SerializerException, IOException {
        FieldInfo fieldInfo = readFieldInfo(input);
        if (nullable && fieldInfo == null) {
            return null;
        }
        PMessageBuilder<T> builder = descriptor.builder();
        while (fieldInfo != null) {
            PField field = descriptor.getField(fieldInfo.getId());
            if (field != null) {
                Object value = readFieldValue(input, fieldInfo, field.getDescriptor());
                builder.set(field.getKey(), value);
            } else {
                if (readStrict) {
                    throw new SerializerException(
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
    private void consumeMessage(BinaryReader in) throws IOException, SerializerException {
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
    private FieldInfo readFieldInfo(BinaryReader in) throws IOException, SerializerException {
        byte type = in.expectByte();
        if (type == PType.STOP.id) {
            return null;
        }
        return new FieldInfo(in.expectUInt16(), type);
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
    private <T> T readFieldValue(BinaryReader in, FieldInfo fieldInfo, PDescriptor type)
            throws IOException, SerializerException {
        if (type.getType().id != fieldInfo.getType()) {
            if (readStrict) {
                throw new SerializerException("");
            }
        }
        switch (PType.findById(fieldInfo.getType())) {
            case BOOL:
                return cast(in.expectByte() != 0);
            case BYTE:
                return cast(in.expectByte());
            case I16:
                return cast(in.expectShort());
            case ENUM:
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
                return cast(readMessage(in, (PStructDescriptor<?,?>) type, false));
            }
            case MAP: {
                final byte keyT = in.expectByte();
                final byte itemT = in.expectByte();
                final int size = in.expectUInt32();

                PDescriptor keyType = null;
                PDescriptor valueType = null;
                PMap.Builder<Object, Object> out;
                if (type != null) {
                    if (!type.getType()
                             .equals(PType.MAP)) {
                        throw new SerializerException("Invalid type for map encoding: " + type);
                    }

                    PMap<Object, Object> mapType = (PMap<Object, Object>) type;
                    keyType = mapType.keyDescriptor();
                    valueType = mapType.itemDescriptor();

                    out = mapType.builder();
                } else {
                    out = new PMap.ImmutableMapBuilder<>();
                }

                FieldInfo keyInfo = new FieldInfo(1, keyT);
                FieldInfo itemInfo = new FieldInfo(2, itemT);
                for (int i = 0; i < size; ++i) {
                    Object key = readFieldValue(in, keyInfo, keyType);
                    Object value = readFieldValue(in, itemInfo, valueType);
                    if (key != null && value != null) {
                        out.put(key, value);
                    } else if (readStrict) {
                        throw new SerializerException("Null key or value in map.");
                    }
                }
                return cast(out.build());
            }
            case SET: {
                final byte itemT = in.expectByte();
                final int size = in.expectUInt32();

                PDescriptor entryType = null;
                PSet.Builder<Object> out;
                if (type != null) {
                    PSet<Object> setType = (PSet<Object>) type;
                    entryType = setType.itemDescriptor();
                    out = setType.builder();
                } else {
                    out = new PSet.ImmutableSetBuilder<>();
                }

                FieldInfo itemInfo = new FieldInfo(0, itemT);
                for (int i = 0; i < size; ++i) {
                    Object key = readFieldValue(in, itemInfo, entryType);
                    if (key != null) {
                        out.add(key);
                    } else if (readStrict) {
                        throw new SerializerException("Null value in set.");
                    }
                }

                return cast(out.build());
            }
            case LIST: {
                final byte itemT = in.expectByte();
                final int size = in.expectUInt32();

                PDescriptor entryType = null;
                PList.Builder<Object> out;
                if (type != null) {
                    PList<Object> listType = (PList<Object>) type;
                    entryType = listType.itemDescriptor();
                    out = listType.builder();
                } else {
                    out = new PList.ImmutableListBuilder<>();
                }

                FieldInfo itemInfo = new FieldInfo(0, itemT);
                for (int i = 0; i < size; ++i) {
                    Object key = readFieldValue(in, itemInfo, entryType);
                    if (key != null) {
                        out.add(key);
                    } else if (readStrict) {
                        throw new SerializerException("Null value in list.");
                    }
                }

                return cast(out.build());
            }
            default:
                throw new SerializerException("unknown data type: " + fieldInfo.getType());
        }
    }

    // --- WRITE METHODS ---

    private int writeFieldSpec(BinaryWriter out, byte type, int key) throws IOException {
        out.writeByte(type);
        out.writeUInt16(key);
        return 3;
    }

    /**
     * Write a field value to stream.
     *
     * @param out   The stream to write to.
     * @param value The value to write.
     * @return The number of bytes written.
     */
    private int writeFieldValue(BinaryWriter out, Object value, PDescriptor descriptor) throws IOException,
                                                                                               SerializerException {
        switch (descriptor.getType()) {
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
                return out.writeInt(((PEnumValue<?>) value).getValue());
            case MAP: {
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) value;
                PMap<?,?> pMap = (PMap<?, ?>) descriptor;
                int len = out.writeByte(pMap.keyDescriptor().getType().id);
                len += out.writeByte(pMap.itemDescriptor().getType().id);
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

                int len = out.writeByte(pSet.itemDescriptor().getType().id);
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
                throw new SerializerException("");
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
            return String.format("field(%d: %s)", id, PType.findById(type));
        }

        public int getId() {
            return id;
        }

        public byte getType() {
            return type;
        }

    }
}
