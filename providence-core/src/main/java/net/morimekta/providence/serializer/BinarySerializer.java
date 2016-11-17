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

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
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

    /**
     * Construct a serializer instance.
     *
     * @param readStrict If the serializer should fail on bad reading.
     */
    public BinarySerializer(boolean readStrict) {
        this(readStrict, true);
    }

    /**
     * Construct a serializer instance.
     *
     * @param readStrict If the serializer should fail on reading mismatched data.
     * @param versioned If the serializer should use the versioned service call format.
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
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(OutputStream os, Message message) throws IOException {
        BinaryWriter writer = new BigEndianBinaryWriter(os);
        return writeMessage(writer, message);
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(OutputStream os, PServiceCall<Message, Field> call)
            throws IOException {
        BinaryWriter out = new BigEndianBinaryWriter(os);
        byte[] method = call.getMethod().getBytes(UTF_8);

        int len = method.length;
        if (versioned) {
            len += out.writeInt(VERSION_1 | (byte) call.getType().getValue());
            len += out.writeInt(method.length);
            out.write(method);
        } else {
            len += out.writeInt(method.length);
            out.write(method);
            len += out.writeByte((byte) call.getType().getValue());
        }
        len += out.writeInt(call.getSequence());
        len += writeMessage(out, call.getMessage());
        return len;
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    Message deserialize(InputStream input, PStructDescriptor<Message, Field> descriptor)
            throws IOException {
        BinaryReader reader = new BigEndianBinaryReader(input);
        return readMessage(reader, descriptor, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Message extends PMessage<Message, Field>, Field extends PField>
    PServiceCall<Message, Field> deserialize(InputStream is, PService service)
            throws IOException {
        BinaryReader in = new BigEndianBinaryReader(is);
        String methodName = null;
        int sequence = 0;
        PServiceCallType type = null;
        try {
            int methodNameLen = in.expectInt();
            int typeKey;
            // Accept both "strict" read mode and non-strict.
            // versioned
            if (methodNameLen < 0) {
                int version = methodNameLen & VERSION_MASK;
                if (version == VERSION_1) {
                    typeKey = methodNameLen & 0xFF;
                    methodNameLen = in.expectInt();
                    methodName = new String(in.expectBytes(methodNameLen), UTF_8);
                } else {
                    throw new SerializerException("Bad protocol version: %08x", version >>> 16)
                            .setExceptionType(PApplicationExceptionType.INVALID_PROTOCOL);
                }
            } else {
                if (readStrict && versioned) {
                    throw new SerializerException("Missing protocol version")
                            .setExceptionType(PApplicationExceptionType.INVALID_PROTOCOL);
                }

                methodName = new String(in.expectBytes(methodNameLen), UTF_8);
                typeKey = in.expectByte();
            }
            sequence = in.expectInt();

            type = PServiceCallType.forValue(typeKey);
            PServiceMethod method = service.getMethod(methodName);
            if (type == null) {
                throw new SerializerException("Invalid call type " + typeKey)
                        .setExceptionType(PApplicationExceptionType.INVALID_MESSAGE_TYPE);
            } else if (type == PServiceCallType.EXCEPTION) {
                PApplicationException ex = readMessage(in, PApplicationException.kDescriptor, false);
                return (PServiceCall<Message, Field>) new PServiceCall<>(methodName, type, sequence, ex);
            } else if (method == null) {
                throw new SerializerException("No such method " + methodName + " on " + service.getQualifiedName(null))
                        .setExceptionType(PApplicationExceptionType.UNKNOWN_METHOD);
            }

            @SuppressWarnings("unchecked")
            PStructDescriptor<Message, Field> descriptor = isRequestCallType(type) ? method.getRequestType() : method.getResponseType();

            Message message = readMessage(in, descriptor, false);

            return new PServiceCall<>(methodName, type, sequence, message);
        } catch (SerializerException se) {
            throw new SerializerException(se)
                    .setMethodName(methodName)
                    .setCallType(type)
                    .setSequenceNo(sequence);
        } catch (IOException e) {
            throw new SerializerException(e, e.getMessage())
                    .setMethodName(methodName)
                    .setCallType(type)
                    .setSequenceNo(sequence);
        }
    }

    private <Message extends PMessage<Message, Field>, Field extends PField>
    int writeMessage(BinaryWriter writer, Message message)
            throws IOException {
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
            for (PField field : message.descriptor().getFields()) {
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

    private <Message extends PMessage<Message, Field>, Field extends PField>
    Message readMessage(BinaryReader input,
                        PStructDescriptor<Message, Field> descriptor,
                        boolean nullable) throws IOException {
        FieldInfo fieldInfo = readFieldInfo(input);
        if (nullable && fieldInfo == null) {
            return null;
        }
        PMessageBuilder<Message, Field> builder = descriptor.builder();
        while (fieldInfo != null) {
            PField field = descriptor.getField(fieldInfo.getId());
            if (field != null) {
                Object value = readFieldValue(input, fieldInfo, field.getDescriptor());
                builder.set(field.getKey(), value);
            } else {
                if (readStrict) {
                    throw new SerializerException(
                            "Unknown field " + fieldInfo.getId() + " for type " + descriptor.getQualifiedName(null));
                }
                readFieldValue(input, fieldInfo, null);
            }

            fieldInfo = readFieldInfo(input);
        }

        if (readStrict) {
            try {
                builder.validate();
            } catch (IllegalStateException e) {
                throw new SerializerException(e, e.getMessage());
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
    private void consumeMessage(BinaryReader in) throws IOException {
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
    private FieldInfo readFieldInfo(BinaryReader in) throws IOException {
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
     * @return The field value, or null if no type.
     *
     * @throws IOException If unable to read from stream or invalid field type.
     */
    private Object readFieldValue(BinaryReader in, FieldInfo fieldInfo, PDescriptor type)
            throws IOException {
        if (type == null) {
            if (readStrict) {
                throw new SerializerException("Reading unknown field in strict mode.");
            }
        } else if (type.getType().id != fieldInfo.getType()) {
            if (readStrict) {
                throw new SerializerException("Mismatching field type in strict mode.");
            } else {
                // consume the content.
                readFieldValue(in, fieldInfo, null);
                // return 'null', which should clear the field value.
                return null;
            }
        }

        switch (PType.findById(fieldInfo.getType())) {
            case VOID:
                return Boolean.TRUE;
            case BOOL:
                return in.expectByte() != 0;
            case BYTE:
                return in.expectByte();
            case I16:
                return in.expectShort();
            case ENUM:
            case I32:
                int val = in.expectInt();
                if (type != null && type instanceof PEnumDescriptor) {
                    @SuppressWarnings("unchecked")
                    PEnumBuilder builder = ((PEnumDescriptor<?>)type).builder();
                    builder.setByValue(val);
                    return builder.build();
                } else {
                    return val;
                }
            case I64:
                return in.expectLong();
            case DOUBLE:
                return in.expectDouble();
            case STRING:
            case BINARY:
                int len = in.expectUInt32();
                byte[] data = in.expectBytes(len);
                if (type != null && type.getType() == PType.STRING) {
                    return new String(data, StandardCharsets.UTF_8);
                } else {
                    return Binary.wrap(data);
                }
            case MESSAGE: {
                if (type == null) {
                    consumeMessage(in);
                    return null;
                }
                return readMessage(in, (PStructDescriptor<?,?>) type, false);
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

                    @SuppressWarnings("unchecked")
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
                return out.build();
            }
            case SET: {
                final byte itemT = in.expectByte();
                final int size = in.expectUInt32();

                PDescriptor entryType = null;
                PSet.Builder<Object> out;
                if (type != null) {
                    @SuppressWarnings("unchecked")
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

                return out.build();
            }
            case LIST: {
                final byte itemT = in.expectByte();
                final int size = in.expectUInt32();

                PDescriptor entryType = null;
                PList.Builder<Object> out;
                if (type != null) {
                    @SuppressWarnings("unchecked")
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

                return out.build();
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
    private int writeFieldValue(BinaryWriter out, Object value, PDescriptor descriptor) throws IOException {
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
                throw new SerializerException("Unhandled field type: " + descriptor.getType());
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
