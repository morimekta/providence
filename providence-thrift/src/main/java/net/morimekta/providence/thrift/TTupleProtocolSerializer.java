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
import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.Binary;
import net.morimekta.util.io.CountingOutputStream;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TProtocol serializer specialized for Tuple protocol, just because thrift
 * decided that this protocol should be written in a different way than other
 * protocols.
 */
public class TTupleProtocolSerializer extends Serializer {
    public static final String MIME_TYPE = "application/vnd.apache.thrift.tuple";

    private final boolean          strict;
    private final TProtocolFactory protocolFactory;

    public TTupleProtocolSerializer() {
        this(DEFAULT_STRICT);
    }

    public TTupleProtocolSerializer(boolean strict) {
        this.strict = strict;
        this.protocolFactory = new TTupleProtocol.Factory();
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField> int
    serialize(@Nonnull OutputStream output, @Nonnull Message message) throws IOException {
        CountingOutputStream wrapper = new CountingOutputStream(output);
        TTransport transport = new TIOStreamTransport(wrapper);
        try {
            TTupleProtocol protocol = (TTupleProtocol) protocolFactory.getProtocol(transport);
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
            TTupleProtocol protocol = (TTupleProtocol) protocolFactory.getProtocol(transport);
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
            TTupleProtocol protocol = (TTupleProtocol) protocolFactory.getProtocol(transport);

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
        TMessage tm = null;
        PServiceCallType type = null;
        try {
            TTransport transport = new TIOStreamTransport(input);
            TTupleProtocol protocol = (TTupleProtocol) protocolFactory.getProtocol(transport);

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

            PMessageDescriptor<Message,Field> descriptor = isRequestCallType(type) ? method.getRequestType() : method.getResponseType();

            Message message = readMessage(protocol, descriptor);

            protocol.readMessageEnd();

            return new PServiceCall<>(tm.name, type, tm.seqid, message);
        } catch (TTransportException e) {
            throw new SerializerException(e, "Unable to serialize into transport protocol")
                    .setExceptionType(PApplicationExceptionType.findById(e.getType()))
                    .setCallType(type)
                    .setMethodName(tm != null ? tm.name : "")
                    .setSequenceNo(tm != null ? tm.seqid : 0);
        } catch (TException e) {
            throw new SerializerException(e, "Transport exception in protocol")
                    .setExceptionType(PApplicationExceptionType.PROTOCOL_ERROR)
                    .setCallType(type)
                    .setMethodName(tm != null ? tm.name : "")
                    .setSequenceNo(tm != null ? tm.seqid : 0);
        }
    }

    @Override
    public boolean binaryProtocol() {
        return true;
    }

    @Nonnull
    @Override
    public String mimeType() {
        return MIME_TYPE;
    }

    private void writeMessage(PMessage<?,?> message, TTupleProtocol protocol) throws TException, SerializerException {
        PMessageDescriptor<?, ?> descriptor = message.descriptor();
        if (descriptor.getVariant() == PMessageVariant.UNION) {
            PField fld = ((PUnion<?,?>) message).unionField();
            if (fld != null) {
                protocol.writeI16((short) fld.getId());
                writeTypedValue(message.get(fld.getId()), fld.getDescriptor(), protocol);
            } else {
                throw new SerializerException("Unable to write " + descriptor.getQualifiedName() + " without set union field.");
            }
        } else {
            PField[] fields = descriptor.getFields();
            Arrays.sort(fields, Comparator.comparingInt(PField::getId));
            int numOptionals = countOptionals(fields);
            BitSet optionals = new BitSet();
            if (numOptionals > 0) {
                int optionalPos = 0;
                for (PField fld : fields) {
                    if (fld.getRequirement() != PRequirement.REQUIRED) {
                        if (message.has(fld.getId())) {
                            optionals.set(optionalPos);
                        }
                        ++optionalPos;
                    }
                }
            }

            boolean shouldWriteOptionals = true;
            int optionalPos = 0;

            for (PField fld : fields) {
                if (fld.getRequirement() == PRequirement.REQUIRED) {
                    writeTypedValue(message.get(fld.getId()), fld.getDescriptor(), protocol);
                } else {
                    // Write the optionals bitset at the position of the first
                    // non-required field.
                    if (shouldWriteOptionals) {
                        protocol.writeBitSet(optionals, numOptionals);
                        shouldWriteOptionals = false;
                    }
                    if (optionals.get(optionalPos)) {
                        writeTypedValue(message.get(fld.getId()), fld.getDescriptor(), protocol);
                    }
                    ++optionalPos;
                }
            }
        }
    }

    private int countOptionals(PField[] fields) {
        int numOptionals = 0;
        for (PField fld : fields) {
            if (fld.getRequirement() != PRequirement.REQUIRED) {
                ++numOptionals;
            }
        }
        return numOptionals;
    }

    private <Message extends PMessage<Message, Field>, Field extends PField>
    Message readMessage(TTupleProtocol protocol, PMessageDescriptor<Message, Field> descriptor)
            throws SerializerException, TException {
        PMessageBuilder<Message, Field> builder = descriptor.builder();

        if (descriptor.getVariant() == PMessageVariant.UNION) {
            int fieldId = protocol.readI16();
            PField fld = descriptor.findFieldById(fieldId);
            if (fld != null) {
                builder.set(fld.getId(), readTypedValue(fld.getDescriptor(), protocol));
            } else {
                throw new SerializerException("Unable to read unknown union field " + fieldId + " in " + descriptor.getQualifiedName());
            }
        } else {
            PField[] fields = descriptor.getFields();
            int numOptionals = countOptionals(fields);

            BitSet optionals = null;
            int optionalPos = 0;
            for (PField fld : fields) {
                if (fld.getRequirement() == PRequirement.REQUIRED) {
                    builder.set(fld.getId(), readTypedValue(fld.getDescriptor(), protocol));
                } else {
                    if (optionals == null) {
                        optionals = protocol.readBitSet(numOptionals);
                    }
                    if (optionals.get(optionalPos)) {
                        builder.set(fld.getId(), readTypedValue(fld.getDescriptor(), protocol));
                    }
                    ++optionalPos;
                }
            }
        }

        if (strict) {
            try {
                builder.validate();
            } catch (IllegalStateException e) {
                throw new SerializerException(e, e.getMessage());
            }
        }

        return builder.build();
    }

    private Object readTypedValue(PDescriptor type, TTupleProtocol protocol)
            throws TException, SerializerException {
        switch (type.getType()) {
            case BOOL:
                return protocol.readBool();
            case BYTE:
                return protocol.readByte();
            case I16:
                return protocol.readI16();
            case I32:
                return protocol.readI32();
            case I64:
                return protocol.readI64();
            case DOUBLE:
                return protocol.readDouble();
            case BINARY: {
                ByteBuffer buffer = protocol.readBinary();
                return Binary.wrap(buffer.array());
            }
            case STRING:
                return protocol.readString();
            case ENUM: {
                PEnumDescriptor<?> et = (PEnumDescriptor<?>) type;
                PEnumBuilder<?> eb = et.builder();
                final int value = protocol.readI32();
                eb.setById(value);
                if (strict && !eb.valid()) {
                    throw new SerializerException("Invalid enum value " + value + " for " +
                                                  et.getQualifiedName());
                }
                return eb.build();
            }
            case MESSAGE:
                return readMessage(protocol, (PMessageDescriptor<?, ?>) type);
            case LIST: {
                int lSize = protocol.readI32();
                @SuppressWarnings("unchecked")
                PList<Object> lDesc = (PList<Object>) type;
                PDescriptor liDesc = lDesc.itemDescriptor();

                PList.Builder<Object> list = lDesc.builder();
                for (int i = 0; i < lSize; ++i) {
                    list.add(readTypedValue(liDesc, protocol));
                }

                return list.build();
            }
            case SET: {
                int sSize = protocol.readI32();
                @SuppressWarnings("unchecked")
                PSet<Object> sDesc = (PSet<Object>) type;
                PDescriptor siDesc = sDesc.itemDescriptor();

                PSet.Builder<Object> set = sDesc.builder();
                for (int i = 0; i < sSize; ++i) {
                    set.add(readTypedValue(siDesc, protocol));
                }

                return set.build();
            }
            case MAP: {
                int mSize = protocol.readI32();
                @SuppressWarnings("unchecked")
                PMap<Object, Object> mDesc = (PMap<Object, Object>) type;
                PDescriptor mkDesc = mDesc.keyDescriptor();
                PDescriptor miDesc = mDesc.itemDescriptor();

                PMap.Builder<Object, Object> map = mDesc.builder();
                for (int i = 0; i < mSize; ++i) {
                    Object key = readTypedValue(mkDesc, protocol);
                    Object val = readTypedValue(miDesc, protocol);
                    map.put(key, val);
                }

                protocol.readMapEnd();
                return map.build();
            }
            default:
                throw new SerializerException("Unsupported protocol field type: " + type.getType());
        }
    }

    private void writeTypedValue(Object item, PDescriptor type, TTupleProtocol protocol)
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
                protocol.writeI32(list.size());
                for (Object i : list) {
                    writeTypedValue(i, lType.itemDescriptor(), protocol);
                }
                break;
            case SET:
                PSet<?> sType = (PSet<?>) type;
                Set<?> set = (Set<?>) item;
                protocol.writeI32(set.size());
                for (Object i : set) {
                    writeTypedValue(i, sType.itemDescriptor(), protocol);
                }
                break;
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) type;
                Map<?, ?> map = (Map<?, ?>) item;
                protocol.writeI32(map.size());

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
