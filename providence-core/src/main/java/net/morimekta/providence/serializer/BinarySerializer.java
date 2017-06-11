/*
 * Copyright 2015-2016 Providence Authors
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
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.util.io.BigEndianBinaryReader;
import net.morimekta.util.io.BigEndianBinaryWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.morimekta.providence.serializer.rw.BinaryFormatUtils.readMessage;
import static net.morimekta.providence.serializer.rw.BinaryFormatUtils.writeMessage;

/**
 * Compact binary serializer. This usesd a format that is as close the the default
 * thrift binary protocol as possible.
 * <p>
 * See data definition file <code>docs/serializer-binary.md</code> for format
 * spec.
 */
public class BinarySerializer extends Serializer {
    public static final String MIME_TYPE = "application/vnd.apache.thrift.binary";
    public static final String ALT_MIME_TYPE = "application/x-thrift";

    private static final int VERSION_MASK = 0xffff0000;
    private static final int VERSION_1    = 0x80010000;
    // 255 byte (ASCII char) length for a method name is exceptionally long.
    private static final int MAX_METHOD_NAME_LEN = 255;

    private final boolean strict;
    private final boolean versioned;

    /**
     * Construct a serializer instance.
     */
    public BinarySerializer() {
        this(DEFAULT_STRICT);
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
     * Construct a serializer instance. The 'versioned' param is equivalent to
     * to the TBinaryProtocol strict flag.
     *
     * @param readStrict If the serializer should fail on reading mismatched data.
     * @param versioned If the serializer should use the versioned service call format.
     */
    public BinarySerializer(boolean readStrict, boolean versioned) {
        this.strict = readStrict;
        this.versioned = versioned;
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

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(@Nonnull OutputStream os, @Nonnull Message message) throws IOException {
        BigEndianBinaryWriter writer = new BigEndianBinaryWriter(os);
        return writeMessage(writer, message);
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(@Nonnull OutputStream os, @Nonnull PServiceCall<Message, Field> call)
            throws IOException {
        BigEndianBinaryWriter out = new BigEndianBinaryWriter(os);
        byte[] method = call.getMethod().getBytes(UTF_8);

        int len = method.length;
        if (versioned) {
            len += out.writeInt(VERSION_1 | call.getType().asInteger());
            len += out.writeInt(method.length);
            out.write(method);
        } else {
            len += out.writeInt(method.length);
            out.write(method);
            len += out.writeByte((byte) call.getType().asInteger());
        }
        len += out.writeInt(call.getSequence());
        len += writeMessage(out, call.getMessage());
        return len;
    }

    @Nonnull
    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    Message deserialize(@Nonnull InputStream input, @Nonnull PMessageDescriptor<Message, Field> descriptor)
            throws IOException {
        BigEndianBinaryReader reader = new BigEndianBinaryReader(input);
        return readMessage(reader, descriptor, strict);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <Message extends PMessage<Message, Field>, Field extends PField>
    PServiceCall<Message, Field> deserialize(@Nonnull InputStream is, @Nonnull PService service)
            throws IOException {
        BigEndianBinaryReader in = new BigEndianBinaryReader(is);
        String methodName = null;
        int sequence = 0;
        PServiceCallType type = null;
        try {
            int methodNameLen = in.expectInt();
            int typeKey;
            // Accept both "strict" read mode and non-strict.
            // versioned
            if (methodNameLen <= 0) {
                int version = methodNameLen & VERSION_MASK;
                if (version == VERSION_1) {
                    typeKey = methodNameLen & 0xFF;
                    methodNameLen = in.expectInt();
                    if (methodNameLen > MAX_METHOD_NAME_LEN) {
                        throw new SerializerException("Exceptionally long method name of %s bytes", methodNameLen)
                                .setExceptionType(PApplicationExceptionType.PROTOCOL_ERROR);
                    } if (methodNameLen < 1) {
                        throw new SerializerException("Exceptionally short method name of %s bytes", methodNameLen)
                                .setExceptionType(PApplicationExceptionType.PROTOCOL_ERROR);
                    }
                    methodName = new String(in.expectBytes(methodNameLen), UTF_8);
                } else {
                    throw new SerializerException("Bad protocol version: %08x", version >>> 16)
                            .setExceptionType(PApplicationExceptionType.INVALID_PROTOCOL);
                }
            } else {
                if (strict && versioned) {
                    throw new SerializerException("Missing protocol version")
                            .setExceptionType(PApplicationExceptionType.INVALID_PROTOCOL);
                }

                if (methodNameLen > MAX_METHOD_NAME_LEN) {
                    throw new SerializerException("Exceptionally long method name of %s bytes", methodNameLen)
                            .setExceptionType(PApplicationExceptionType.PROTOCOL_ERROR);
                }
                methodName = new String(in.expectBytes(methodNameLen), UTF_8);
                typeKey = in.expectByte();
            }
            sequence = in.expectInt();

            type = PServiceCallType.findById(typeKey);
            PServiceMethod method = service.getMethod(methodName);
            if (type == null) {
                throw new SerializerException("Invalid call type " + typeKey)
                        .setExceptionType(PApplicationExceptionType.INVALID_MESSAGE_TYPE);
            } else if (type == PServiceCallType.EXCEPTION) {
                PApplicationException ex = readMessage(in, PApplicationException.kDescriptor, strict);
                return (PServiceCall<Message, Field>) new PServiceCall<>(methodName, type, sequence, ex);
            } else if (method == null) {
                throw new SerializerException("No such method " + methodName + " on " + service.getQualifiedName())
                        .setExceptionType(PApplicationExceptionType.UNKNOWN_METHOD);
            }

            @SuppressWarnings("unchecked")
            PMessageDescriptor<Message, Field> descriptor = isRequestCallType(type) ? method.getRequestType() : method.getResponseType();

            Message message = readMessage(in, descriptor, strict);

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
}
