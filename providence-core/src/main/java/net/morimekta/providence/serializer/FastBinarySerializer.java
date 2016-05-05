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
        this(false);
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
    public <T extends PMessage<T>> int serialize(OutputStream os, T message) throws IOException, SerializerException {
        BinaryWriter out = new BinaryWriter(os);
        return writeMessage(out, message);
    }

    @Override
    public <T extends PMessage<T>> int serialize(OutputStream os, PServiceCall<T> call)
            throws IOException, SerializerException {
        BinaryWriter out = new BinaryWriter(os);
        byte[] method = call.getMethod().getBytes(UTF_8);
        int len = out.writeVarint(method.length << 3 | call.getType().key);
        len += method.length;
        out.write(method);
        len += out.writeInt(call.getSequence());
        len += writeMessage(out, call.getMessage());
        return len;
    }

    @Override
    public <T extends PMessage<T>, TF extends PField> T deserialize(InputStream is, PStructDescriptor<T, TF> descriptor) throws
                                                                                                                         SerializerException, IOException {
        BinaryReader in = new BinaryReader(is);
        return readMessage(in, descriptor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PMessage<T>> PServiceCall<T> deserialize(InputStream is, PService service)
            throws SerializerException {
        String methodName = null;
        int sequence = 0;
        PServiceCallType type = null;
        try {
            BinaryReader in = new BinaryReader(is);
            // Max method name length: 255 chars.
            int tag = in.readIntVarint();
            int len = tag >>> 3;
            int typeKey = tag & 0x07;

            methodName = new String(in.expectBytes(len), UTF_8);
            sequence = in.readIntVarint();
            type = PServiceCallType.findByKey(typeKey);

            if (type == null) {
                throw new SerializerException("Invalid call type " + typeKey)
                        .setExceptionType(ApplicationExceptionType.INVALID_MESSAGE_TYPE);
            } else if (type == PServiceCallType.EXCEPTION) {
                ApplicationException ex = readMessage(in, ApplicationException.kDescriptor);
                return (PServiceCall<T>) new PServiceCall<>(methodName, type, sequence, ex);
            }

            PServiceMethod method = service.getMethod(methodName);
            if (method == null) {
                throw new SerializerException("No such method %s on %s",
                                              methodName,
                                              service.getQualifiedName(null))
                        .setExceptionType(ApplicationExceptionType.UNKNOWN_METHOD);
            }

            @SuppressWarnings("unchecked")
            PStructDescriptor<T, ?> descriptor = type.request ? method.getRequestType() : method.getResponseType();

            T message = readMessage(in, descriptor);
            return new PServiceCall<>(methodName, type, sequence, message);
        } catch (IOException e) {
            throw new SerializerException(e, e.getMessage())
                    .setExceptionType(ApplicationExceptionType.PROTOCOL_ERROR)
                    .setCallType(type)
                    .setMethodName(methodName)
                    .setSequenceNo(sequence);
        } catch (SerializerException e) {
            throw new SerializerException(e, e.getMessage())
                    .setExceptionType(e.getExceptionType())
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

    private <T extends PMessage<T>> int writeMessage(BinaryWriter out, T message) throws IOException,
                                                                                         SerializerException {
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

    private <T extends PMessage<T>, TF extends PField> T readMessage(BinaryReader in, PStructDescriptor<T, TF> descriptor)
            throws SerializerException, IOException {
        PMessageBuilder<T> builder = descriptor.builder();
        int tag;
        while ((tag = in.readIntVarint()) > STOP) {
            int id = tag >>> 3;
            int type = tag & 0x07;
            TF field = descriptor.getField(id);
            if (field != null) {
                Object value = readFieldValue(in, type, field.getDescriptor());
                builder.set(field.getKey(), value);
            } else {
                if (readStrict) {
                    throw new SerializerException(
                            "Unknown field " + id + " for type" + descriptor.getQualifiedName(null));
                }
                readFieldValue(in, tag, null);
            }
        }
        return builder.build();
    }

    // --- FIELD VALUE ---

    @SuppressWarnings("unchecked")
    private int writeFieldValue(BinaryWriter out, int key, PDescriptor descriptor, Object value)
            throws IOException, SerializerException {
        switch (descriptor.getType()) {
            case VOID: {
                return out.writeVarint(key << 3 | NONE);
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
            case MAP: {
                int len = out.writeVarint(key << 3 | COLLECTION);

                Map<Object, Object> map = (Map<Object, Object>) value;
                PMap<?, ?> desc = (PMap<?, ?>) descriptor;

                len += out.writeVarint(map.size() * 2);
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    len += writeFieldValue(out, 1, desc.keyDescriptor(), entry.getKey());
                    len += writeFieldValue(out, 2, desc.itemDescriptor(), entry.getValue());
                }
                return len;
            }
            case SET:
            case LIST: {
                int len = out.writeVarint(key << 3 | COLLECTION);

                Collection<Object> coll = (Collection<Object>) value;
                PContainer<?> desc = (PContainer<?>) descriptor;

                len += out.writeVarint(coll.size());
                for (Object item : coll) {
                    len += writeFieldValue(out, 0, desc.itemDescriptor(), item);
                }
                return len;
            }
            default:
                throw new SerializerException("");
        }
    }

    @SuppressWarnings("unchecked")
    private  <T> T readFieldValue(BinaryReader in, int type, PDescriptor descriptor)
            throws IOException, SerializerException {
        switch (type) {
            case NONE:
                return cast(false);
            case TRUE:
                return cast(true);
            case VARINT: {
                if (descriptor == null) {
                    if (readStrict) {
                        throw new SerializerException("");
                    }
                    in.readLongVarint();
                    return null;
                }
                switch (descriptor.getType()) {
                    case BYTE:
                        return cast((byte) in.readIntZigzag());
                    case I16:
                        return cast((short) in.readIntZigzag());
                    case I32:
                        return cast(in.readIntZigzag());
                    case I64:
                        return cast(in.readLongZigzag());
                    case ENUM: {
                        PEnumBuilder<?> builder = ((PEnumDescriptor<?>) descriptor).builder();
                        builder.setByValue(in.readIntZigzag());
                        return cast(builder.build());
                    }
                    default: {
                        throw new SerializerException("");
                    }
                }
            }
            case FIXED_64:
                return cast(in.expectDouble());
            case BINARY: {
                int len = in.readIntVarint();
                byte[] data = in.expectBytes(len);
                if (descriptor != null) {
                    switch (descriptor.getType()) {
                        case STRING:
                            return cast(new String(data, StandardCharsets.UTF_8));
                        case BINARY:
                            return cast(Binary.wrap(data));
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
                return cast(readMessage(in, (PStructDescriptor<?, ?>) descriptor));
            case COLLECTION:
                if (descriptor == null) {
                    if (readStrict) {
                        throw new SerializerException("");
                    }
                    final int len = in.readIntVarint();
                    for (int i = 0; i < len; ++i) {
                        readFieldValue(in, in.readIntVarint() & 0x07, null);
                    }
                    return null;
                } else if (descriptor.getType() == PType.MAP) {
                    PMap<Object, Object> ct = (PMap<Object, Object>) descriptor;
                    PDescriptor kt = ct.keyDescriptor();
                    PDescriptor vt = ct.itemDescriptor();

                    PMap.Builder<Object, Object> out = ct.builder();
                    final int len = in.readIntVarint();
                    for (int i = 0; i < len; ++i, ++i) {
                        Object key = readFieldValue(in, in.readIntVarint() & 0x07, kt);
                        Object value = readFieldValue(in, in.readIntVarint() & 0x07, vt);
                        out.put(key, value);
                    }
                    return cast(out.build());
                } else if (descriptor.getType() == PType.LIST) {
                    PList<Object> ct = (PList<Object>) descriptor;
                    PDescriptor it = ct.itemDescriptor();
                    PList.Builder<Object> out = ct.builder();
                    final int len = in.readIntVarint();
                    for (int i = 0; i < len; ++i) {
                        int tag = in.readIntVarint();
                        out.add(readFieldValue(in, tag & 0x07, it));
                    }
                    return cast(out.build());
                } else if (descriptor.getType() == PType.SET) {
                    PSet<Object> ct = (PSet<Object>) descriptor;
                    PDescriptor it = ct.itemDescriptor();
                    PSet.Builder<Object> out = ct.builder();
                    final int len = in.readIntVarint();
                    for (int i = 0; i < len; ++i) {
                        int tag = in.readIntVarint();
                        out.add(readFieldValue(in, tag & 0x07, it));
                    }
                    return cast(out.build());
                } else {
                    throw new SerializerException("Type " + descriptor.getType() +
                                                  " not compatible with collection data.");
                }
        }

        throw new SerializerException("No handling for type " + type);
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
