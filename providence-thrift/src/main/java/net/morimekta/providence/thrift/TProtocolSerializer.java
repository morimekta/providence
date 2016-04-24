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
package net.morimekta.providence.thrift;

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
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.util.Binary;
import net.morimekta.util.io.CountingOutputStream;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TSet;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Override
    public String mimeType() {
        return mimeType;
    }

    @Override
    public <T extends PMessage<T>> int
    serialize(OutputStream output, T message) throws IOException, SerializerException {
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
    public <T extends PMessage<T>> int serialize(OutputStream output, PServiceCall<T> call)
            throws IOException, SerializerException {
        CountingOutputStream wrapper = new CountingOutputStream(output);
        TTransport transport = new TIOStreamTransport(wrapper);
        try {
            TProtocol protocol = protocolFactory.getProtocol(transport);
            TMessage tm = new TMessage(call.getMethod(), (byte) call.getType().key, call.getSequence());

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

    @Override
    public <T extends PMessage<T>, TF extends PField> T
    deserialize(InputStream input, PStructDescriptor<T, TF> descriptor) throws IOException, SerializerException {
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

    @Override
    public <T extends PMessage<T>> PServiceCall<T> deserialize(InputStream input, PService service)
            throws IOException, SerializerException {
        try {
            TTransport transport = new TIOStreamTransport(input);
            TProtocol protocol = protocolFactory.getProtocol(transport);

            TMessage tm = protocol.readMessageBegin();
            PServiceMethod method = service.getMethod(tm.name);
            if (method == null) {
                throw new SerializerException("No such method " + tm.name + " on " + service.getQualifiedName(null));
            }

            PServiceCallType type = PServiceCallType.findByKey(tm.type);
            if (type == null) {
                throw new SerializerException("Unknown call type for id " + tm.type);
            }
            @SuppressWarnings("unchecked")
            PStructDescriptor<T,?> descriptor = type.request ? method.getRequestType() : method.getResponseType();

            T message = readMessage(protocol, descriptor);

            protocol.readMessageEnd();

            return new PServiceCall<>(tm.name, type, tm.seqid, message);
        } catch (TTransportException e) {
            throw new SerializerException(e, "Unable to serialize into transport protocol");
        } catch (TException e) {
            throw new SerializerException(e, "Transport exception in protocol");
        }
    }

    private void writeMessage(PMessage<?> message, TProtocol protocol) throws TException, SerializerException {
        PStructDescriptor<?, ?> type = message.descriptor();

        protocol.writeStructBegin(new TStruct(message.descriptor()
                                                     .getQualifiedName(null)));

        for (PField field : type.getFields()) {
            if (!message.has(field.getKey())) {
                continue;
            }

            protocol.writeFieldBegin(new TField(field.getName(),
                                                getFieldType(field.getDescriptor()),
                                                (short) field.getKey()));

            writeTypedValue(message.get(field.getKey()), field.getDescriptor(), protocol);

            protocol.writeFieldEnd();
        }

        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }

    private <T extends PMessage<T>> T readMessage(TProtocol protocol, PStructDescriptor<T, ?> descriptor)
            throws SerializerException, TException {
        TField f;

        PMessageBuilder<T> builder = descriptor.builder();
        protocol.readStructBegin();  // ignored.
        while ((f = protocol.readFieldBegin()) != null) {
            if (f.type == PType.STOP.id) {
                break;
            }

            PField field;
            if (f.id != 0) {
                field = descriptor.getField(f.id);
                if (field == null) {
                    throw new SerializerException("No such field " + f.id + " in " + descriptor.getQualifiedName(null));
                }
            } else {
                field = descriptor.getField(f.name);
                if (field == null) {
                    throw new SerializerException(
                            "No such field " + f.name + " in " + descriptor.getQualifiedName(null));
                }
            }

            if (f.type != getFieldType(field.getDescriptor())) {
                throw new SerializerException("Incompatible serialized type " + PType.findById(f.type) +
                                              " for field " + field.getName() +
                                              ", expected " + field.getDescriptor()
                                                                   .getType());
            }

            Object value = readTypedValue(f.type, field.getDescriptor(), protocol);
            if (value == null) {
                throw new SerializerException("Illegal null field value");
            }
            builder.set(field.getKey(), value);

            protocol.readFieldEnd();
        }
        protocol.readStructEnd();

        if (!builder.isValid() && readStrict) {
            throw new SerializerException("Read invalid message from protocol");
        }

        return builder.build();
    }

    private <T> T readTypedValue(byte tType, PDescriptor type, TProtocol protocol)
            throws TException, SerializerException {
        switch (tType) {
            case TType.BOOL:
                return cast(protocol.readBool());
            case TType.BYTE:
                return cast(protocol.readByte());
            case TType.I16:
                return cast(protocol.readI16());
            case TType.I32:
                if (PType.ENUM == type.getType()) {
                    PEnumDescriptor<?> et = (PEnumDescriptor<?>) type;
                    PEnumBuilder<?> eb = et.builder();
                    int value = protocol.readI32();
                    eb.setByValue(value);
                    if (!eb.isValid() && readStrict) {
                        throw new SerializerException("Invalid enum value " + value + " for " +
                                                      et.getQualifiedName(null));
                    }
                    return cast(eb.build());
                } else {
                    return cast(protocol.readI32());
                }
            case TType.I64:
                return cast(protocol.readI64());
            case TType.DOUBLE:
                return cast(protocol.readDouble());
            case TType.STRING:
                if (type == PPrimitive.BINARY) {
                    ByteBuffer buffer = protocol.readBinary();
                    return cast(Binary.wrap(buffer.array()));
                }
                return cast(protocol.readString());
            case TType.STRUCT:
                return cast(readMessage(protocol, (PStructDescriptor<?, ?>) type));
            case TType.LIST:
                TList listInfo = protocol.readListBegin();
                PList<Object> lDesc = (PList<Object>) type;
                PDescriptor liDesc = lDesc.itemDescriptor();

                PList.Builder<Object> list = lDesc.builder();
                for (int i = 0; i < listInfo.size; ++i) {
                    list.add(readTypedValue(listInfo.elemType, liDesc, protocol));
                }

                protocol.readListEnd();
                return cast(list.build());
            case TType.SET:
                TSet setInfo = protocol.readSetBegin();
                PSet<Object> sDesc = (PSet<Object>) type;
                PDescriptor siDesc = sDesc.itemDescriptor();

                PSet.Builder<Object> set = sDesc.builder();
                for (int i = 0; i < setInfo.size; ++i) {
                    set.add(cast(readTypedValue(setInfo.elemType, siDesc, protocol)));
                }

                protocol.readSetEnd();
                return cast(set.build());
            case TType.MAP:
                TMap mapInfo = protocol.readMapBegin();
                PMap<Object, Object> mDesc = (PMap<Object, Object>) type;
                PDescriptor mkDesc = mDesc.keyDescriptor();
                PDescriptor miDesc = mDesc.itemDescriptor();

                PMap.Builder<Object, Object> map = mDesc.builder();
                for (int i = 0; i < mapInfo.size; ++i) {
                    Object key = readTypedValue(mapInfo.keyType, mkDesc, protocol);
                    Object val = readTypedValue(mapInfo.valueType, miDesc, protocol);
                    map.put(key, val);
                }

                protocol.readMapEnd();
                return cast(map.build());
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
                protocol.writeI32(value.getValue());
                break;
            case MESSAGE:
                writeMessage((PMessage<?>) item, protocol);
                break;
            case LIST:
                PList<?> lType = (PList<?>) type;
                List<?> list = (List<?>) item;
                TList listInfo = new TList(getFieldType(lType.itemDescriptor()), list.size());
                protocol.writeListBegin(listInfo);
                for (Object i : list) {
                    writeTypedValue(i, lType.itemDescriptor(), protocol);
                }
                protocol.writeListEnd();
                break;
            case SET:
                PSet<?> sType = (PSet<?>) type;
                Set<?> set = (Set<?>) item;
                TSet setInfo = new TSet(getFieldType(sType.itemDescriptor()), set.size());
                protocol.writeSetBegin(setInfo);
                for (Object i : set) {
                    writeTypedValue(i, sType.itemDescriptor(), protocol);
                }
                protocol.writeSetEnd();
                break;
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) type;
                Map<?, ?> map = (Map<?, ?>) item;
                protocol.writeMapBegin(new TMap(getFieldType(mType.keyDescriptor()),
                                                getFieldType(mType.itemDescriptor()),
                                                map.size()));

                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    writeTypedValue(entry.getKey(), mType.keyDescriptor(), protocol);
                    writeTypedValue(entry.getValue(), mType.itemDescriptor(), protocol);
                }

                protocol.writeMapEnd();
                break;
        }
    }

    private byte getFieldType(PDescriptor type) throws SerializerException {
        if (type == null) {
            throw new SerializerException("No type!");
        }
        return type.getType().id;
    }
}
