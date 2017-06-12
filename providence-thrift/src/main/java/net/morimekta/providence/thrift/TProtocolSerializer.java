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
package net.morimekta.providence.thrift;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.binary.BinaryType;
import net.morimekta.util.Binary;
import net.morimekta.util.io.CountingOutputStream;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TSet;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.morimekta.providence.serializer.binary.BinaryType.asString;
import static net.morimekta.providence.serializer.binary.BinaryType.forType;

/**
 * @author Stein Eldar Johnsen
 * @since 23.09.15
 */
class TProtocolSerializer extends Serializer {
    private final TProtocolFactory protocolFactory;
    private final boolean          readStrict;
    private final boolean          binary;
    private final String           mimeType;

    public TProtocolSerializer(boolean readStrict, TProtocolFactory protocolFactory,
                               boolean binary, String mimeType) {
        this.readStrict = readStrict;
        this.protocolFactory = protocolFactory;
        this.binary = binary;
        this.mimeType = mimeType;
    }

    @Override
    public boolean binaryProtocol() {
        return binary;
    }

    @Nonnull
    @Override
    public String mimeType() {
        return mimeType;
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(@Nonnull OutputStream output, @Nonnull Message message) throws IOException {
        CountingOutputStream wrapper = new CountingOutputStream(output);
        TTransport transport = new TIOStreamTransport(wrapper);
        try {
            TProtocol protocol = protocolFactory.getProtocol(transport);
            writeMessage(message, protocol);
            transport.flush();
            wrapper.flush();
            return wrapper.getByteCount();
        } catch (TException e) {
            throw new SerializerException(e, e.getMessage());
        }
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(@Nonnull OutputStream output, @Nonnull PServiceCall<Message, Field> call)
            throws IOException {
        CountingOutputStream wrapper = new CountingOutputStream(output);
        TTransport transport = new TIOStreamTransport(wrapper);
        try {
            TProtocol protocol = protocolFactory.getProtocol(transport);
            TMessage tm = new TMessage(call.getMethod(), (byte) call.getType().asInteger(), call.getSequence());

            protocol.writeMessageBegin(tm);
            writeMessage(call.getMessage(), protocol);
            protocol.writeMessageEnd();

            transport.flush();
            wrapper.flush();
            return wrapper.getByteCount();
        } catch (TException e) {
            throw new SerializerException(e, e.getMessage());
        }
    }

    @Nonnull
    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField> Message
    deserialize(@Nonnull InputStream input, @Nonnull PMessageDescriptor<Message, Field> descriptor) throws IOException {
        try {
            TTransport transport = new TIOStreamTransport(input);
            TProtocol protocol = protocolFactory.getProtocol(transport);

            return readMessage(protocol, descriptor);
        } catch (TTransportException e) {
            throw new SerializerException(e, "Unable to serialize into transport protocol");
        } catch (TException e) {
            throw new SerializerException(e, "Transport exception in protocol");
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <Message extends PMessage<Message, Field>, Field extends PField>
    PServiceCall<Message, Field> deserialize(@Nonnull InputStream input, @Nonnull PService service)
            throws SerializerException {
        PServiceCallType type = null;
        TMessage tm = null;
        try {
            TTransport transport = new TIOStreamTransport(input);
            TProtocol protocol = protocolFactory.getProtocol(transport);

            tm = protocol.readMessageBegin();

            type = PServiceCallType.findById(tm.type);
            if (type == null) {
                throw new SerializerException("Unknown call type for id " + tm.type);
            } else if (type == PServiceCallType.EXCEPTION) {
                PApplicationException exception = readMessage(protocol, PApplicationException.kDescriptor);
                return new PServiceCall(tm.name, type, tm.seqid, exception);
            }

            PServiceMethod method = service.getMethod(tm.name);
            if (method == null) {
                throw new SerializerException("No such method " + tm.name + " on " + service.getQualifiedName());
            }

            @SuppressWarnings("unchecked")
            PMessageDescriptor<Message,Field> descriptor = isRequestCallType(type) ? method.getRequestType() : method.getResponseType();

            Message message = readMessage(protocol, descriptor);

            protocol.readMessageEnd();

            return new PServiceCall<>(tm.name, type, tm.seqid, message);
        } catch (TTransportException e) {
            throw new SerializerException(e, e.getMessage())
                    .setExceptionType(PApplicationExceptionType.findById(e.getType()))
                    .setCallType(type)
                    .setSequenceNo(tm != null ? tm.seqid : 0)
                    .setMethodName(tm != null ? tm.name : null);
        } catch (TException e) {
            throw new SerializerException(e, e.getMessage())
                    .setExceptionType(PApplicationExceptionType.PROTOCOL_ERROR)
                    .setCallType(type)
                    .setSequenceNo(tm != null ? tm.seqid : 0)
                    .setMethodName(tm != null ? tm.name : null);
        }
    }

    private void writeMessage(PMessage<?,?> message, TProtocol protocol) throws TException, SerializerException {
        PMessageDescriptor<?, ?> type = message.descriptor();

        protocol.writeStructBegin(new TStruct(message.descriptor()
                                                     .getQualifiedName()));

        for (PField field : type.getFields()) {
            if (!message.has(field.getId())) {
                continue;
            }

            protocol.writeFieldBegin(new TField(field.getName(),
                                                forType(field.getDescriptor().getType()),
                                                (short) field.getId()));

            writeTypedValue(message.get(field.getId()), field.getDescriptor(), protocol);

            protocol.writeFieldEnd();
        }

        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }

    private <Message extends PMessage<Message, Field>, Field extends PField>
    Message readMessage(TProtocol protocol, PMessageDescriptor<Message, Field> descriptor)
            throws SerializerException, TException {
        TField f;

        PMessageBuilder<Message, Field> builder = descriptor.builder();
        protocol.readStructBegin();  // ignored.
        while ((f = protocol.readFieldBegin()) != null) {
            if (f.type == BinaryType.STOP) {
                break;
            }

            PField field;
            // f.name is never fulled out, rely on f.id being correct.
            field = descriptor.findFieldById(f.id);
            if (field != null) {
                if (f.type != forType(field.getDescriptor().getType())) {
                    throw new SerializerException("Incompatible serialized type " + asString(f.type) +
                                                  " for field " + field.getName() +
                                                  ", expected " + asString(forType(field.getDescriptor().getType())));
                }

                Object value = readTypedValue(f.type, field.getDescriptor(), protocol, true);
                if (value != null) {
                    builder.set(field.getId(), value);
                }
            } else {
                TProtocolUtil.skip(protocol, f.type);
            }

            protocol.readFieldEnd();
        }
        protocol.readStructEnd();

        if (readStrict) {
            try {
                builder.validate();
            } catch (IllegalStateException e) {
                throw new SerializerException(e, e.getMessage());
            }
        }

        return builder.build();
    }

    private Object readTypedValue(byte tType, PDescriptor type, TProtocol protocol, boolean allowNull)
            throws TException, SerializerException {
        if (tType != forType(type.getType())) {
            throw new SerializerException("Expected type " +
                                          asString(forType(type.getType())) +
                                          " but found " +
                                          asString(tType));
        }
        switch (tType) {
            case BinaryType.BOOL:
                return protocol.readBool();
            case BinaryType.BYTE:
                return protocol.readByte();
            case BinaryType.I16:
                return protocol.readI16();
            case BinaryType.I32:
                if (PType.ENUM == type.getType()) {
                    PEnumDescriptor<?> et = (PEnumDescriptor<?>) type;
                    PEnumBuilder<?> eb = et.builder();
                    int value = protocol.readI32();
                    eb.setById(value);
                    if (!eb.valid() && !allowNull) {
                        throw new SerializerException("Invalid enum value " + value + " for " +
                                                      et.getQualifiedName());
                    }
                    return eb.build();
                } else {
                    return protocol.readI32();
                }
            case BinaryType.I64:
                return protocol.readI64();
            case BinaryType.DOUBLE:
                return protocol.readDouble();
            case BinaryType.STRING:
                if (type == PPrimitive.BINARY) {
                    ByteBuffer buffer = protocol.readBinary();
                    return Binary.wrap(buffer.array());
                }
                return protocol.readString();
            case BinaryType.STRUCT:
                return readMessage(protocol, (PMessageDescriptor<?, ?>) type);
            case BinaryType.LIST:
                TList listInfo = protocol.readListBegin();
                PList<Object> lDesc = (PList<Object>) type;
                PDescriptor liDesc = lDesc.itemDescriptor();

                PList.Builder<Object> list = lDesc.builder();
                for (int i = 0; i < listInfo.size; ++i) {
                    list.add(readTypedValue(listInfo.elemType, liDesc, protocol, false));
                }

                protocol.readListEnd();
                return list.build();
            case BinaryType.SET:
                TSet setInfo = protocol.readSetBegin();
                PSet<Object> sDesc = (PSet<Object>) type;
                PDescriptor siDesc = sDesc.itemDescriptor();

                PSet.Builder<Object> set = sDesc.builder();
                for (int i = 0; i < setInfo.size; ++i) {
                    set.add(readTypedValue(setInfo.elemType, siDesc, protocol, false));
                }

                protocol.readSetEnd();
                return set.build();
            case BinaryType.MAP:
                TMap mapInfo = protocol.readMapBegin();
                PMap<Object, Object> mDesc = (PMap<Object, Object>) type;
                PDescriptor mkDesc = mDesc.keyDescriptor();
                PDescriptor miDesc = mDesc.itemDescriptor();

                PMap.Builder<Object, Object> map = mDesc.builder();
                for (int i = 0; i < mapInfo.size; ++i) {
                    Object key = readTypedValue(mapInfo.keyType, mkDesc, protocol, false);
                    Object val = readTypedValue(mapInfo.valueType, miDesc, protocol, false);
                    map.put(key, val);
                }

                protocol.readMapEnd();
                return map.build();
            default:
                throw new SerializerException("Unsupported protocol field type: " + tType);
        }
    }

    private void writeTypedValue(Object item, PDescriptor type, TProtocol protocol)
            throws TException, SerializerException {
        switch (type.getType()) {
            case BOOL:
                protocol.writeBool((Boolean) item);
                break;
            case BYTE:
                protocol.writeByte((Byte) item);
                break;
            case I16:
                protocol.writeI16((Short) item);
                break;
            case I32:
                protocol.writeI32((Integer) item);
                break;
            case I64:
                protocol.writeI64((Long) item);
                break;
            case DOUBLE:
                protocol.writeDouble((Double) item);
                break;
            case STRING:
                protocol.writeString((String) item);
                break;
            case BINARY:
                protocol.writeBinary(((Binary) item).getByteBuffer());
                break;
            case ENUM:
                PEnumValue<?> value = (PEnumValue<?>) item;
                protocol.writeI32(value.asInteger());
                break;
            case MESSAGE:
                writeMessage((PMessage<?,?>) item, protocol);
                break;
            case LIST:
                PList<?> lType = (PList<?>) type;
                List<?> list = (List<?>) item;
                TList listInfo = new TList(forType(lType.itemDescriptor().getType()), list.size());
                protocol.writeListBegin(listInfo);
                for (Object i : list) {
                    writeTypedValue(i, lType.itemDescriptor(), protocol);
                }
                protocol.writeListEnd();
                break;
            case SET:
                PSet<?> sType = (PSet<?>) type;
                Set<?> set = (Set<?>) item;
                TSet setInfo = new TSet(forType(sType.itemDescriptor().getType()), set.size());
                protocol.writeSetBegin(setInfo);
                for (Object i : set) {
                    writeTypedValue(i, sType.itemDescriptor(), protocol);
                }
                protocol.writeSetEnd();
                break;
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) type;
                Map<?, ?> map = (Map<?, ?>) item;
                protocol.writeMapBegin(new TMap(forType(mType.keyDescriptor().getType()),
                                                forType(mType.itemDescriptor().getType()),
                                                map.size()));

                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    writeTypedValue(entry.getKey(), mType.keyDescriptor(), protocol);
                    writeTypedValue(entry.getValue(), mType.itemDescriptor(), protocol);
                }

                protocol.writeMapEnd();
                break;
            default:
                break;
        }
    }
}
