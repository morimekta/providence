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
package org.apache.thrift.j2.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TList;
import org.apache.thrift.j2.descriptor.TMap;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TSet;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.serializer.TSerializeException;
import org.apache.thrift.j2.serializer.TSerializer;
import org.apache.thrift.j2.util.io.CountingOutputStream;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.descriptor.TDescriptor;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 23.09.15
 */
public class TProtocolSerializer extends TSerializer {
    private final TProtocolFactory mProtocolFactory;

    public TProtocolSerializer() {
        this(new TBinaryProtocol.Factory());
    }

    public TProtocolSerializer(TProtocolFactory protocolFactory) {
        mProtocolFactory = protocolFactory;
    }

    @Override
    public int serialize(OutputStream output, TMessage<?> message)
            throws IOException, TSerializeException {
        if (!message.isValid()) {
            throw new TSerializeException("Message is not valid for serialization.");
        }

        CountingOutputStream wrapper = new CountingOutputStream(output);
        TTransport transport = new TIOStreamTransport(wrapper);
        try {
            TProtocol protocol = mProtocolFactory.getProtocol(transport);
            write(message, protocol);
            transport.flush();
            wrapper.flush();
            return wrapper.getByteCount();
        } catch (TException e) {
            throw new TSerializeException(e, e.getMessage());
        } finally {
            transport.close();  // closes wrapper too.
        }
    }

    @Override
    public <T> int serialize(OutputStream output, TDescriptor<T> descriptor, T value)
            throws IOException, TSerializeException {
        CountingOutputStream wrapper = new CountingOutputStream(output);
        TTransport transport = new TIOStreamTransport(wrapper);

        try {
            TProtocol protocol = mProtocolFactory.getProtocol(transport);
            switch (descriptor.getType()) {
                case MESSAGE:
                    write((TMessage<?>) value, protocol);
                    break;
                default:
                    protocol.writeStructBegin(new TStruct("msg"));
                    protocol.writeFieldBegin(new org.apache.thrift.protocol.TField("", getFieldType(descriptor), (short) 0));
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
            throw new TSerializeException(e, e.getMessage());
        } finally {
            transport.close();
        }
    }

    @Override
    public <T> T deserialize(InputStream input, TDescriptor<T> definition)
            throws IOException, TSerializeException {
        T ret;
        try {
            TTransport transport = new TIOStreamTransport(input);
            TProtocol protocol = mProtocolFactory.getProtocol(transport);

            ret = read(protocol, definition);

            transport.close();
        } catch (TTransportException e) {
            throw new TSerializeException(e, "Unable to serialize into transport protocol");
        } catch (TException e) {
            throw new TSerializeException(e, "Transport exception in protocol");
        }

        return ret;
    }

    protected <T> T read(TProtocol protocol, TDescriptor<T> descriptor)
            throws TException, TSerializeException {
        switch (descriptor.getType()) {
            case MESSAGE:
                T ret = cast((Object) readMessage(protocol, (TStructDescriptor<?>) descriptor));
                return ret;
            default:
                protocol.readStructBegin();
                org.apache.thrift.protocol.TField field = protocol.readFieldBegin();
                if (field == null) {
                    throw new TSerializeException("Unexpected end of fields.");
                }
                ret = readTypedValue(field.type, descriptor, protocol);
                protocol.readFieldEnd();
                protocol.readFieldBegin();  // ignored.
                protocol.readStructEnd();
                return ret;
        }
    }

    protected void write(TMessage<?> message, TProtocol protocol)
            throws TException, TSerializeException {
        TStructDescriptor<?> type = message.descriptor();

        protocol.writeStructBegin(new TStruct(message.descriptor().getQualifiedName(null)));

        for (TField<?> field : type.getFields()) {
            if (!message.has(field.getKey())) continue;

            protocol.writeFieldBegin(new org.apache.thrift.protocol.TField(
                    field.getName(), getFieldType(field.descriptor()), (short) field.getKey()));

            writeTypedValue(message.get(field.getKey()), field.descriptor(), protocol);

            protocol.writeFieldEnd();
        }

        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }

    protected <T extends TMessage<T>> T readMessage(TProtocol protocol, TStructDescriptor<T> descriptor)
            throws TSerializeException, TException {
        org.apache.thrift.protocol.TField f;

        TMessageBuilder<T> builder = descriptor.factory().builder();

        TStruct struct = protocol.readStructBegin();  // ignored.
        System.err.println(struct.name);

        while ((f = protocol.readFieldBegin()) != null) {
            TType type = TType.findById(f.type);
            if (type.equals(TType.STOP)) {
                System.err.println(f.toString());
                System.err.println("STOP");
                break;
            }

            System.err.println(f.toString());

            TField<?> field;
            if (f.id != 0) {
                field = descriptor.getField(f.id);
                if (field == null) {
                    throw new TSerializeException("No such field " + f.id + " in " + descriptor.getQualifiedName(null));
                }
            } else {
                field = descriptor.getField(f.name);
                if (field == null) {
                    throw new TSerializeException("No such field " + f.name + " in " + descriptor.getQualifiedName(null));
                }
            }

            if (f.type != getFieldType(field.descriptor())) {
                throw new TSerializeException("Incompatible serialized type " + type +
                                              " for field " + field.getName() +
                                              ", expected " + field.descriptor().getType());
            }

            Object value = readTypedValue(f.type, field.descriptor(), protocol);
            if (value == null) {
                throw new TSerializeException("Illegal null field value");
            }
            builder.set(field.getKey(), value);

            protocol.readFieldEnd();
        }

        protocol.readStructEnd();

        if (!builder.isValid()) {
            throw new TSerializeException("Read invalid message from protocol");
        }

        return builder.build();    }

    protected <T> T readTypedValue(byte tType, TDescriptor<T> type, TProtocol protocol) throws TException, TSerializeException {
        switch (TType.findById(tType)) {
            case BOOL:
                return cast(protocol.readBool());
            case BYTE:
                return cast(protocol.readByte());
            case I16:
                return cast(protocol.readI16());
            case I32:
                if (type.getType().equals(TType.ENUM)) {
                    TEnumDescriptor<?> et = (TEnumDescriptor<?>) type;
                    TEnumBuilder<?> eb = et.factory().builder();
                    int value = protocol.readI32();
                    eb.setByValue(value);
                    if (!eb.isValid()) {
                        throw new TSerializeException("Invalid enum value " + value + " for " +
                                                      et.getQualifiedName(null));
                    }
                    return cast(eb.build());
                } else {
                    return cast(protocol.readI32());
                }
            case I64:
                return cast(protocol.readI64());
            case DOUBLE:
                return cast(protocol.readDouble());
            case STRING:
                if (type.equals(TPrimitive.BINARY)) {
                    ByteBuffer buffer = protocol.readBinary();
                    return cast(buffer.array());
                }
                return cast(protocol.readString());
            case MESSAGE:
                return cast((Object) readMessage(protocol, (TStructDescriptor<?>) type));
            case ENUM:
                TEnumDescriptor<?> ed = (TEnumDescriptor<?>) type;
                TEnumBuilder<?> eb = ed.factory().builder();
                int value = protocol.readI32();
                eb.setByValue(value);
                if (!eb.isValid()) {
                    throw new TSerializeException("Invalid enum value " + value + " for " +
                                                  ed.getQualifiedName(null));
                }
                return cast(eb.build());
            case LIST:
                org.apache.thrift.protocol.TList listInfo = protocol.readListBegin();
                TList<?> lDesc = (TList<?>) type;
                TDescriptor liDesc = lDesc.itemDescriptor();

                List<Object> list = new LinkedList<>();
                for (int i = 0; i < listInfo.size; ++i) {
                    list.add(readTypedValue(listInfo.elemType, liDesc, protocol));
                }

                protocol.readListEnd();
                return cast(list);
            case SET:
                org.apache.thrift.protocol.TSet setInfo = protocol.readSetBegin();
                TSet<?> sDesc = (TSet<?>) type;
                TDescriptor siDesc = sDesc.itemDescriptor();

                List<Object> set = new LinkedList<>();
                for (int i = 0; i < setInfo.size; ++i) {
                    set.add(readTypedValue(setInfo.elemType, siDesc, protocol));
                }

                protocol.readSetEnd();
                return cast(set);
            case MAP:
                org.apache.thrift.protocol.TMap mapInfo = protocol.readMapBegin();
                TMap<?,?> mDesc = (TMap<?,?>) type;
                TDescriptor mkDesc = mDesc.keyDescriptor();
                TDescriptor miDesc = mDesc.itemDescriptor();

                Map<Object,Object> map = new LinkedHashMap<>();
                for (int i = 0; i < mapInfo.size; ++i) {
                    Object key = readTypedValue(mapInfo.keyType, mkDesc, protocol);
                    Object val = readTypedValue(mapInfo.valueType, miDesc, protocol);
                    map.put(key, val);
                }

                protocol.readMapEnd();
                return cast(map);
            default:
                throw new TSerializeException("Unsupported protocol field type: " + tType);
        }
    }

    protected void writeTypedValue(Object item, TDescriptor type, TProtocol protocol)
            throws TException, TSerializeException {
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
                ByteBuffer buffer = ByteBuffer.wrap((byte[]) item);
                protocol.writeBinary(buffer);
                break;
            case ENUM:
                TEnumValue<?> value = (TEnumValue<?>) item;
                protocol.writeI32(value.getValue());
                break;
            case MESSAGE:
                write((TMessage<?>) item, protocol);
                break;
            case LIST:
                TList<?> lType = (TList<?>) type;
                List<?> list = (List<?>) item;
                org.apache.thrift.protocol.TList listInfo = new org.apache.thrift.protocol.TList(getFieldType(lType.itemDescriptor()), list.size());
                protocol.writeListBegin(listInfo);
                for (Object i : list) {
                    writeTypedValue(i, lType.itemDescriptor(), protocol);
                }
                protocol.writeListEnd();
                break;
            case SET:
                TSet<?> sType = (TSet<?>) type;
                Set<?> set = (Set<?>) item;
                org.apache.thrift.protocol.TSet setInfo = new org.apache.thrift.protocol.TSet(getFieldType(sType.itemDescriptor()), set.size());
                protocol.writeSetBegin(setInfo);
                for (Object i : set) {
                    writeTypedValue(i, sType.itemDescriptor(), protocol);
                }
                protocol.writeSetEnd();
                break;
            case MAP:
                TMap<?, ?> mType = (TMap<?, ?>) type;
                Map<?, ?> map = (Map<?, ?>) item;
                protocol.writeMapBegin(new org.apache.thrift.protocol.TMap(getFieldType(mType.keyDescriptor()),
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

    protected byte getFieldType(TDescriptor type) throws TSerializeException {
        if (type == null) throw new TSerializeException("No type!");
        return type.getType().id;
    }
}
