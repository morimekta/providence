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

import net.morimekta.providence.*;
import net.morimekta.providence.descriptor.*;
import net.morimekta.providence.serializer.PSerializeException;
import net.morimekta.providence.serializer.PSerializer;
import net.morimekta.util.io.CountingOutputStream;
import net.morimekta.util.Binary;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
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
import java.util.*;

/**
 * @author Stein Eldar Johnsen
 * @since 23.09.15
 */
class TProtocolSerializer extends PSerializer {
    private final TProtocolFactory mProtocolFactory;

    public TProtocolSerializer(TProtocolFactory protocolFactory) {
        mProtocolFactory = protocolFactory;
    }

    @Override
    public int serialize(OutputStream output, PMessage<?> message)
            throws IOException, PSerializeException {
        CountingOutputStream wrapper = new CountingOutputStream(output);
        TTransport transport = new TIOStreamTransport(wrapper);
        try {
            TProtocol protocol = mProtocolFactory.getProtocol(transport);
            write(message, protocol);
            transport.flush();
            wrapper.flush();
            return wrapper.getByteCount();
        } catch (TException e) {
            throw new PSerializeException(e, e.getMessage());
        }
    }

    @Override
    public <T> int serialize(OutputStream output, PDescriptor<T> descriptor, T value)
            throws IOException, PSerializeException {
        CountingOutputStream wrapper = new CountingOutputStream(output);
        TTransport transport = new TIOStreamTransport(wrapper);

        try {
            TProtocol protocol = mProtocolFactory.getProtocol(transport);
            switch (descriptor.getType()) {
                case MESSAGE:
                    write((PMessage<?>) value, protocol);
                    break;
                default:
                    protocol.writeStructBegin(new TStruct("msg"));
                    protocol.writeFieldBegin(new TField("", getFieldType(descriptor), (short) 0));
                    writeTypedValue(value, descriptor, protocol);
                    protocol.writeFieldEnd();
                    protocol.writeFieldStop();
                    protocol.writeStructEnd();
                    break;
            }
            transport.flush();
            wrapper.flush();
            return wrapper.getByteCount();
        } catch (TException e) {
            throw new PSerializeException(e, e.getMessage());
        }
    }

    @Override
    public <T> T deserialize(InputStream input, PDescriptor<T> definition)
            throws IOException, PSerializeException {
        T ret;
        try {
            TTransport transport = new TIOStreamTransport(input);
            TProtocol protocol = mProtocolFactory.getProtocol(transport);

            ret = read(protocol, definition);
        } catch (TTransportException e) {
            throw new PSerializeException(e, "Unable to serialize into transport protocol");
        } catch (TException e) {
            throw new PSerializeException(e, "Transport exception in protocol");
        }

        return ret;
    }

    protected <T> T read(TProtocol protocol, PDescriptor<T> descriptor)
            throws TException, PSerializeException {
        switch (descriptor.getType()) {
            case MESSAGE:
                T ret = cast((Object) readMessage(protocol, (PStructDescriptor<?,?>) descriptor));
                return ret;
            default:
                protocol.readStructBegin();
                TField field = protocol.readFieldBegin();
                if (field == null) {
                    throw new PSerializeException("Unexpected end of fields.");
                }
                ret = readTypedValue(field.type, descriptor, protocol);
                protocol.readFieldEnd();
                protocol.readFieldBegin();  // ignored.
                protocol.readStructEnd();
                return ret;
        }
    }

    protected void write(PMessage<?> message, TProtocol protocol)
            throws TException, PSerializeException {
        PStructDescriptor<?,?> type = message.descriptor();

        protocol.writeStructBegin(new TStruct(message.descriptor().getQualifiedName(null)));

        for (PField<?> field : type.getFields()) {
            if (!message.has(field.getKey())) continue;

            protocol.writeFieldBegin(new TField(
                    field.getName(), getFieldType(field.getDescriptor()), (short) field.getKey()));

            writeTypedValue(message.get(field.getKey()), field.getDescriptor(), protocol);

            protocol.writeFieldEnd();
        }

        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }

    protected <T extends PMessage<T>> T readMessage(TProtocol protocol, PStructDescriptor<T,?> descriptor)
            throws PSerializeException, TException {
        TField f;

        PMessageBuilder<T> builder = descriptor.factory().builder();
        protocol.readStructBegin();  // ignored.
        while ((f = protocol.readFieldBegin()) != null) {
            if (f.type == PType.STOP.id) {
                break;
            }

            PField<?> field;
            if (f.id != 0) {
                field = descriptor.getField(f.id);
                if (field == null) {
                    throw new PSerializeException("No such field " + f.id + " in " + descriptor.getQualifiedName(null));
                }
            } else {
                field = descriptor.getField(f.name);
                if (field == null) {
                    throw new PSerializeException(
                            "No such field " + f.name + " in " + descriptor.getQualifiedName(null));
                }
            }

            if (f.type != getFieldType(field.getDescriptor())) {
                throw new PSerializeException("Incompatible serialized type " + PType.findById(f.type) +
                                              " for field " + field.getName() +
                                              ", expected " + field.getDescriptor().getType());
            }

            Object value = readTypedValue(f.type, field.getDescriptor(), protocol);
            if (value == null) {
                throw new PSerializeException("Illegal null field value");
            }
            builder.set(field.getKey(), value);

            protocol.readFieldEnd();
        }
        protocol.readStructEnd();

        if (!builder.isValid()) {
            throw new PSerializeException("Read invalid message from protocol");
        }

        return builder.build();
    }

    protected <T> T readTypedValue(byte tType, PDescriptor<T> type, TProtocol protocol) throws TException, PSerializeException {
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
                    PEnumBuilder<?> eb = et.factory().builder();
                    int value = protocol.readI32();
                    eb.setByValue(value);
                    if (!eb.isValid()) {
                        throw new PSerializeException("Invalid enum value " + value + " for " +
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
                return cast((Object) readMessage(protocol, (PStructDescriptor<?,?>) type));
            case TType.LIST:
                TList listInfo = protocol.readListBegin();
                PList<?> lDesc = (PList<?>) type;
                PDescriptor liDesc = lDesc.itemDescriptor();

                List<Object> list = new LinkedList<>();
                for (int i = 0; i < listInfo.size; ++i) {
                    list.add(readTypedValue(listInfo.elemType, liDesc, protocol));
                }

                protocol.readListEnd();
                return cast(list);
            case TType.SET:
                TSet setInfo = protocol.readSetBegin();
                PSet<?> sDesc = (PSet<?>) type;
                PDescriptor siDesc = sDesc.itemDescriptor();

                Set<Object> set = new LinkedHashSet<>();
                for (int i = 0; i < setInfo.size; ++i) {
                    set.add(readTypedValue(setInfo.elemType, siDesc, protocol));
                }

                protocol.readSetEnd();
                return cast(set);
            case TType.MAP:
                TMap mapInfo = protocol.readMapBegin();
                PMap<?,?> mDesc = (PMap<?,?>) type;
                PDescriptor mkDesc = mDesc.keyDescriptor();
                PDescriptor miDesc = mDesc.itemDescriptor();

                Map<Object,Object> map = new LinkedHashMap<>();
                for (int i = 0; i < mapInfo.size; ++i) {
                    Object key = readTypedValue(mapInfo.keyType, mkDesc, protocol);
                    Object val = readTypedValue(mapInfo.valueType, miDesc, protocol);
                    map.put(key, val);
                }

                protocol.readMapEnd();
                return cast(map);
            default:
                throw new PSerializeException("Unsupported protocol field type: " + tType);
        }
    }

    protected void writeTypedValue(Object item, PDescriptor type, TProtocol protocol)
            throws TException, PSerializeException {
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
                write((PMessage<?>) item, protocol);
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

    protected byte getFieldType(PDescriptor type) throws PSerializeException {
        if (type == null) throw new PSerializeException("No type!");
        return type.getType().id;
    }
}
