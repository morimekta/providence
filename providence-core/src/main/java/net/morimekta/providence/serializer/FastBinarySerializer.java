/*
 * Copyright 2016 Providence Authors
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
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.util.Binary;
import net.morimekta.util.io.LittleEndianBinaryReader;
import net.morimekta.util.io.LittleEndianBinaryWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Compact binary serializer. This uses the most compact binary format
 * allowable.
 * <p>
 * See data definition file <code>docs/fast-binary.md</code> for format spec.
 */
public class FastBinarySerializer extends Serializer {
    public static final String MIME_TYPE = "application/vnd.morimekta.providence.binary";

    protected final boolean readStrict;

    /**
     * Construct a serializer instance.
     */
    public FastBinarySerializer() {
        this(DEFAULT_STRICT);
    }

    /**
     * Construct a serializer instance.
     *
     * @param readStrict If serializer should fail on unknown input data.
     */
    public FastBinarySerializer(boolean readStrict) {
        this.readStrict = readStrict;
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(OutputStream os, Message message) throws IOException {
        LittleEndianBinaryWriter out = new LittleEndianBinaryWriter(os);
        return writeMessage(out, message);
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(OutputStream os, PServiceCall<Message, Field> call)
            throws IOException {
        LittleEndianBinaryWriter out = new LittleEndianBinaryWriter(os);
        byte[] method = call.getMethod().getBytes(UTF_8);
        int len = out.writeVarint(method.length << 3 | call.getType().getValue());
        len += method.length;
        out.write(method);
        len += out.writeInt(call.getSequence());
        len += writeMessage(out, call.getMessage());
        return len;
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    Message deserialize(InputStream is, PMessageDescriptor<Message, Field> descriptor)
            throws IOException {
        LittleEndianBinaryReader in = new LittleEndianBinaryReader(is);
        return readMessage(in, descriptor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Message extends PMessage<Message, Field>, Field extends PField>
    PServiceCall<Message, Field> deserialize(InputStream is, PService service)
            throws SerializerException {
        String methodName = null;
        int sequence = 0;
        PServiceCallType type = null;
        try {
            LittleEndianBinaryReader in = new LittleEndianBinaryReader(is);
            // Max method name length: 255 chars.
            int tag = in.readIntVarint();
            int len = tag >>> 3;
            int typeKey = tag & 0x07;

            methodName = new String(in.expectBytes(len), UTF_8);
            sequence = in.readIntVarint();
            type = PServiceCallType.forValue(typeKey);

            if (type == null) {
                throw new SerializerException("Invalid call type " + typeKey)
                        .setExceptionType(PApplicationExceptionType.INVALID_MESSAGE_TYPE);
            } else if (type == PServiceCallType.EXCEPTION) {
                PApplicationException ex = readMessage(in, PApplicationException.kDescriptor);
                return (PServiceCall<Message, Field>) new PServiceCall<>(methodName, type, sequence, ex);
            }

            PServiceMethod method = service.getMethod(methodName);
            if (method == null) {
                throw new SerializerException("No such method %s on %s",
                                              methodName,
                                              service.getQualifiedName())
                        .setExceptionType(PApplicationExceptionType.UNKNOWN_METHOD);
            }

            @SuppressWarnings("unchecked")
            PMessageDescriptor<Message, Field> descriptor = isRequestCallType(type) ? method.getRequestType() : method.getResponseType();

            Message message = readMessage(in, descriptor);
            return new PServiceCall<>(methodName, type, sequence, message);
        } catch (SerializerException e) {
            throw new SerializerException(e)
                    .setCallType(type)
                    .setMethodName(methodName)
                    .setSequenceNo(sequence);
        } catch (IOException e) {
            throw new SerializerException(e, e.getMessage())
                    .setCallType(type)
                    .setMethodName(methodName)
                    .setSequenceNo(sequence);
        }
    }

    @Override
    public boolean binaryProtocol() {
        return true;
    }

    @Override
    public String mimeType() {
        return MIME_TYPE;
    }

    // --- MESSAGE ---

    private <Message extends PMessage<Message, Field>, Field extends PField>
    int writeMessage(LittleEndianBinaryWriter out, Message message)
            throws IOException {
        int len = 0;
        if (message instanceof PUnion) {
            PField field = ((PUnion) message).unionField();
            if (field != null) {
                len += writeFieldValue(out, field.getKey(), field.getDescriptor(), message.get(field.getKey()));
            }
        } else {
            for (PField field : message.descriptor()
                                          .getFields()) {
                if (message.has(field.getKey())) {
                    len += writeFieldValue(out, field.getKey(), field.getDescriptor(), message.get(field.getKey()));
                }
            }
        }
        // write STOP field.
        return len + out.writeVarint(STOP);
    }

    private <Message extends PMessage<Message, Field>, Field extends PField>
    Message readMessage(LittleEndianBinaryReader in, PMessageDescriptor<Message, Field> descriptor)
            throws IOException {
        PMessageBuilder<Message, Field> builder = descriptor.builder();
        int tag;
        while ((tag = in.readIntVarint()) > STOP) {
            int id = tag >>> 3;
            int type = tag & 0x07;
            Field field = descriptor.getField(id);
            if (field != null) {
                Object value = readFieldValue(in, type, field.getDescriptor());
                builder.set(field.getKey(), value);
            } else {
                if (readStrict) {
                    throw new SerializerException(
                            "Unknown field ID %d in type %s", id, descriptor.getQualifiedName());
                }
                readFieldValue(in, tag, null);
            }
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

    // --- FIELD VALUE ---

    @SuppressWarnings("unchecked")
    private int writeFieldValue(LittleEndianBinaryWriter out, int key, PDescriptor descriptor, Object value)
            throws IOException {
        switch (descriptor.getType()) {
            case VOID: {
                return out.writeVarint(key << 3 | TRUE);
            }
            case BOOL: {
                return out.writeVarint(key << 3 | ((Boolean) value ? TRUE : NONE));
            }
            case BYTE: {
                int len = out.writeVarint(key << 3 | VARINT);
                return len + out.writeZigzag((byte) value);
            }
            case I16: {
                int len = out.writeVarint(key << 3 | VARINT);
                return len + out.writeZigzag((short) value);
            }
            case I32: {
                int len = out.writeVarint(key << 3 | VARINT);
                return len + out.writeZigzag((int) value);
            }
            case I64: {
                int len = out.writeVarint(key << 3 | VARINT);
                return len + out.writeZigzag((long) value);
            }
            case DOUBLE: {
                int len = out.writeVarint(key << 3 | FIXED_64);
                return len + out.writeDouble((Double) value);
            }
            case STRING: {
                byte[] bytes = ((String) value).getBytes(StandardCharsets.UTF_8);
                int len = out.writeVarint(key << 3 | BINARY);
                len += out.writeVarint(bytes.length);
                out.write(bytes);
                return len + bytes.length;
            }
            case BINARY: {
                Binary bytes = (Binary) value;
                int len = out.writeVarint(key << 3 | BINARY);
                len += out.writeVarint(bytes.length());
                bytes.write(out);
                return len + bytes.length();
            }
            case ENUM: {
                int len = out.writeVarint(key << 3 | VARINT);
                return len + out.writeZigzag(((PEnumValue) value).getValue());
            }
            case MESSAGE: {
                int len = out.writeVarint(key << 3 | MESSAGE);
                return len + writeMessage(out, (PMessage) value);
            }
            case MAP:
            case SET:
            case LIST: {
                int len = out.writeVarint(key << 3 | COLLECTION);
                return len + writeContainerEntry(out, COLLECTION, descriptor, value);
            }
            default:
                throw new Error("Unreachable code reached");
        }
    }


    @SuppressWarnings("unchecked")
    private int writeContainerEntry(LittleEndianBinaryWriter out, int typeid, PDescriptor descriptor, Object value)
            throws IOException {
        switch (typeid) {
            case VARINT: {
                if (value instanceof Boolean) {
                    return out.writeVarint(((Boolean) value ? 1 : 0));
                } else if (value instanceof Number) {
                    return out.writeZigzag(((Number) value).longValue());
                } else if (value instanceof PEnumValue) {
                    return out.writeZigzag(((PEnumValue) value).getValue());
                } else {
                    throw new SerializerException("");
                }
            }
            case FIXED_64: {
                return out.writeDouble((Double) value);
            }
            case BINARY: {
                if (value instanceof CharSequence) {
                    byte[] bytes = ((String) value).getBytes(StandardCharsets.UTF_8);
                    int len = out.writeVarint(bytes.length);
                    out.write(bytes);
                    return len + bytes.length;
                } else if (value instanceof Binary) {
                    Binary bytes = (Binary) value;
                    int len = out.writeVarint(bytes.length());
                    bytes.write(out);
                    return len + bytes.length();
                } else {
                    throw new SerializerException("");
                }
            }
            case MESSAGE: {
                return writeMessage(out, (PMessage) value);
            }
            case COLLECTION: {
                if (value instanceof Map) {
                    Map<Object, Object> map = (Map<Object, Object>) value;
                    PMap<?, ?> desc = (PMap<?, ?>) descriptor;

                    int ktype = itemType(desc.keyDescriptor());
                    int vtype = itemType(desc.itemDescriptor());

                    int len = out.writeVarint(map.size() * 2);
                    len += out.writeVarint(ktype << 3 | vtype);
                    for (Map.Entry<Object, Object> entry : map.entrySet()) {
                        len += writeContainerEntry(out, ktype, desc.keyDescriptor(), entry.getKey());
                        len += writeContainerEntry(out, vtype, desc.itemDescriptor(), entry.getValue());
                    }
                    return len;
                } else if (value instanceof Collection){
                    Collection<Object> coll = (Collection<Object>) value;
                    PContainer<?> desc = (PContainer<?>) descriptor;
                    int vtype = itemType(desc.itemDescriptor());

                    int len = out.writeVarint(coll.size());
                    len    += out.writeVarint(vtype);
                    for (Object item : coll) {
                        len += writeContainerEntry(out, vtype, desc.itemDescriptor(), item);
                    }
                    return len;
                } else {
                    throw new SerializerException("");
                }
            }
            default:
                throw new SerializerException("");
        }
    }

    @SuppressWarnings("unchecked")
    private Object readFieldValue(LittleEndianBinaryReader in, int type, PDescriptor descriptor)
            throws IOException {
        switch (type) {
            case NONE:
                return Boolean.FALSE;
            case TRUE:
                return Boolean.TRUE;
            case VARINT: {
                if (descriptor == null) {
                    if (readStrict) {
                        throw new SerializerException("");
                    }
                    in.readLongVarint();
                    return null;
                }
                switch (descriptor.getType()) {
                    case BOOL:
                        return in.readIntVarint() != 0;
                    case BYTE:
                        return (byte) in.readIntZigzag();
                    case I16:
                        return (short) in.readIntZigzag();
                    case I32:
                        return in.readIntZigzag();
                    case I64:
                        return in.readLongZigzag();
                    case ENUM: {
                        PEnumBuilder<?> builder = ((PEnumDescriptor<?>) descriptor).builder();
                        builder.setByValue(in.readIntZigzag());
                        return builder.build();
                    }
                    default: {
                        throw new SerializerException("");
                    }
                }
            }
            case FIXED_64:
                return in.expectDouble();
            case BINARY: {
                int len = in.readIntVarint();
                byte[] data = in.expectBytes(len);
                if (descriptor != null) {
                    switch (descriptor.getType()) {
                        case STRING:
                            return new String(data, StandardCharsets.UTF_8);
                        case BINARY:
                            return Binary.wrap(data);
                        default:
                            throw new SerializerException("");
                    }
                } else {
                    if (readStrict) {
                        throw new SerializerException("");
                    }
                    return null;
                }
            }
            case MESSAGE:
                return readMessage(in, (PMessageDescriptor<?, ?>) descriptor);
            case COLLECTION:
                if (descriptor == null) {
                    if (readStrict) {
                        throw new SerializerException("");
                    }
                    final int len = in.readIntVarint();
                    final int tag = in.readIntVarint();
                    final int vtype = tag & 0x07;
                    final int ktype = tag > 0x07 ? tag >>> 3 : vtype;
                    for (int i = 0; i < len; ++i) {
                        if (i % 2 == 0) {
                            readFieldValue(in, ktype, null);
                        } else {
                            readFieldValue(in, vtype, null);
                        }
                    }
                    return null;
                } else if (descriptor.getType() == PType.MAP) {
                    PMap<Object, Object> ct = (PMap<Object, Object>) descriptor;
                    PDescriptor kt = ct.keyDescriptor();
                    PDescriptor vt = ct.itemDescriptor();

                    PMap.Builder<Object, Object> out = ct.builder();
                    final int len = in.readIntVarint();
                    final int tag = in.readIntVarint();
                    final int vtype = tag & 0x07;
                    final int ktype = tag > 0x07 ? tag >>> 3 : vtype;
                    for (int i = 0; i < len; ++i, ++i) {
                        Object key = readFieldValue(in, ktype, kt);
                        Object value = readFieldValue(in, vtype, vt);
                        out.put(key, value);
                    }
                    return out.build();
                } else if (descriptor.getType() == PType.LIST) {
                    PList<Object> ct = (PList<Object>) descriptor;
                    PDescriptor it = ct.itemDescriptor();
                    PList.Builder<Object> out = ct.builder();
                    final int len = in.readIntVarint();
                    final int vtype = in.readIntVarint() & 0x07;
                    for (int i = 0; i < len; ++i) {
                        out.add(readFieldValue(in, vtype, it));
                    }
                    return out.build();
                } else if (descriptor.getType() == PType.SET) {
                    PSet<Object> ct = (PSet<Object>) descriptor;
                    PDescriptor it = ct.itemDescriptor();
                    PSet.Builder<Object> out = ct.builder();
                    final int len = in.readIntVarint();
                    final int vtype = in.readIntVarint() & 0x07;
                    for (int i = 0; i < len; ++i) {
                        out.add(readFieldValue(in, vtype, it));
                    }
                    return out.build();
                } else {
                    throw new SerializerException("Type " + descriptor.getType() +
                                                  " not compatible with collection data.");
                }
            default:
                throw new Error("Unreachable code reached");
        }
    }

    private static int itemType(PDescriptor descriptor) {
        switch (descriptor.getType()) {
            case BOOL:
            case BYTE:
            case I16:
            case I32:
            case I64:
            case ENUM:
                return VARINT;
            case DOUBLE:
                return FIXED_64;
            case BINARY:
            case STRING:
                return BINARY;
            case MESSAGE:
                return MESSAGE;
            case SET:
            case LIST:
            case MAP:
                return COLLECTION;
            default:
                throw new Error("Unreachable code reached");
        }
    }

    private static final int STOP       = 0x00;
    private static final int NONE       = 0x01;  // 0, false, empty.
    private static final int TRUE       = 0x02;  // 1, true.
    private static final int VARINT     = 0x03;  // -> zigzag encoded base-128 number (byte, i16, i32, i64).
    private static final int FIXED_64   = 0x04;  // -> double
    private static final int BINARY     = 0x05;  // -> varint len + binary data.
    private static final int MESSAGE    = 0x06;  // -> messages, terminated with field-ID 0.
    private static final int COLLECTION = 0x07;  // -> varint len + N * (tag + field).
}
