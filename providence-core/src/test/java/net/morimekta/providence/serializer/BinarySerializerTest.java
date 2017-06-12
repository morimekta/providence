/*
 * Copyright 2017 Providence Authors
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

import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.binary.BinaryType;
import net.morimekta.providence.test_internal.Containers;
import net.morimekta.providence.test_internal.OptionalFields;
import net.morimekta.providence.test_internal.RequiredFields;
import net.morimekta.providence.util.ProvidenceHelper;
import net.morimekta.providence.serializer.pretty.TokenizerException;
import net.morimekta.test.providence.core.calculator.CalculateException;
import net.morimekta.util.Binary;
import net.morimekta.util.io.BigEndianBinaryWriter;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.morimekta.providence.util_internal.EqualToMessage.equalToMessage;
import static net.morimekta.providence.util_internal.TestUtils.decode;
import static net.morimekta.providence.util_internal.TestUtils.encode;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BinarySerializerTest {
    private static ArrayList<Containers> containers;
    private JsonSerializer json;
    private BinarySerializer lenient;
    private BinarySerializer strict;
    private BinarySerializer unversioned;
    private ByteArrayOutputStream tmp;
    private BigEndianBinaryWriter writer;
    private Random random;

    @Before
    public void setUp() throws IOException {
        synchronized (SerializerTest.class) {
            // Since these are immutable, we don't need to read for each test.
            if (containers == null) {
                containers = ProvidenceHelper.arrayListFromJsonResource("/compat/compact.json", Containers.kDescriptor);
            }
        }

        json = new JsonSerializer();
        lenient = new BinarySerializer(false);
        strict = new BinarySerializer(true);
        unversioned = new BinarySerializer(false, false);

        tmp = new ByteArrayOutputStream();
        writer = new BigEndianBinaryWriter(tmp);
        random = new Random();
    }

    @Test
    public void testProperties() {
        assertThat(lenient.binaryProtocol(), is(true));
        assertThat(strict.mimeType(), is(BinarySerializer.MIME_TYPE));
    }

    @Test
    public void testNonPrecompiled_lenient() throws IOException {
        Serializer serializer = new BinarySerializer();

        // Just a sanity check.
        assertTrue(containers.size() == 10);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais;
        int size;

        // complex message, one at a time.
        for (int i = 0; i < 10; ++i) {
            baos.reset();

            Containers expected = containers.get(i);
            size = serializer.serialize(baos, expected);
            assertEquals(baos.size(), size);

            bais = new ByteArrayInputStream(baos.toByteArray());
            Containers actual;
            try {
                actual = serializer.deserialize(bais, Containers.kDescriptor);
            } catch (TokenizerException e) {
                System.err.println(new String(baos.toByteArray(), UTF_8));
                System.err.println(e.asString());
                fail("oops");
                return;
            }

            assertEquals(actual, expected);
        }

        // complex message in stream.
        {
            baos.reset();
            size = 0;
            for (int i = 0; i < 10; ++i) {
                size += serializer.serialize(baos, containers.get(i));
            }

            assertEquals(baos.size(), size);

            bais = new ByteArrayInputStream(baos.toByteArray());

            for (int i = 0; i < 10; ++i) {
                Containers expected = containers.get(i);
                Containers actual = serializer.deserialize(bais, Containers.kDescriptor);

                assertEquals(actual, expected);
            }

            assertEquals(0, bais.available());
        }
    }

    @Test
    public void testService() throws IOException, CalculateException {
        PServiceCall call = decode("[\"calculate\", \"call\", 44, {\"1\": {\"1\": 2}}]".getBytes(UTF_8), json);

        PServiceCall result = decode(encode(call, unversioned), lenient);

        assertThat(result, is(call));

        result = decode(encode(call, strict), strict);

        assertThat(result, is(call));
    }

    @Test
    public void testService_randomdata() throws IOException {
        for (int i = 0; i < 100; ++i) {
            tmp.reset();
            for (int b = 0; b < 100; ++b) {
                writer.writeLong(random.nextLong());
            }

            try {
                // Note: This will *not* throw an exception about every blue moon...
                decode(tmp.toByteArray(), strict);
                fail("No exception on 800 random bytes");
            } catch (SerializerException ignore) {
            }
        }
    }

    @Test
    public void testService_fail_length() throws IOException {
        writer.writeInt(0x80010001);
        writer.writeInt(-1);
        writer.writeLong(-1);
        try {
            decode(tmp.toByteArray(), strict);
        } catch (SerializerException e) {
            assertThat(e.getExceptionType(), is(PApplicationExceptionType.PROTOCOL_ERROR));
            assertThat(e.getMessage(), is("Exceptionally short method name of -1 bytes"));
        }
    }

    @Test
    public void testService_fail_noName() throws IOException {
        writer.writeInt(0x80010001);
        writer.writeInt(8);
        writer.writeShort((short) 4);
        try {
            decode(tmp.toByteArray(), strict);
        } catch (SerializerException e) {
            assertThat(e.getExceptionType(), is(PApplicationExceptionType.PROTOCOL_ERROR));
            assertThat(e.getMessage(), is("Not enough data available on stream: 2 < 8"));
        }
    }

    @Test
    public void testService_fail_longName() throws IOException {
        writer.writeInt(0x80010001);
        writer.writeInt(5733567);
        writer.writeShort((short) 4);
        try {
            decode(tmp.toByteArray(), strict);
        } catch (SerializerException e) {
            assertThat(e.getExceptionType(), is(PApplicationExceptionType.PROTOCOL_ERROR));
            assertThat(e.getMessage(), is("Exceptionally long method name of 5733567 bytes"));
        }
    }

    @Test
    public void testService_fail_noSeq() throws IOException {
        writer.writeInt(0x80010017);
        writer.writeInt(4);
        writer.writeBinary(Binary.wrap("abcd".getBytes(UTF_8)));

        try {
            decode(tmp.toByteArray(), strict);
        } catch (SerializerException e) {
            assertThat(e.getExceptionType(), is(PApplicationExceptionType.PROTOCOL_ERROR));
            assertThat(e.getMessage(), is("Missing byte 1 to expected int"));
        }
    }

    @Test
    public void testService_fail_wrongType() throws IOException {
        writer.writeInt(0x80010017);
        writer.writeInt(4);
        writer.writeBinary(Binary.wrap("abcd".getBytes(UTF_8)));
        writer.writeInt(44);
        try {
            decode(tmp.toByteArray(), strict);
        } catch (SerializerException e) {
            assertThat(e.getExceptionType(), is(PApplicationExceptionType.INVALID_MESSAGE_TYPE));
            assertThat(e.getMessage(), is("Invalid call type 23"));
        }
    }

    @Test
    public void testService_fail_unversionedStrict() throws IOException {
        writer.writeInt(4);
        writer.writeBinary(Binary.wrap("abcd".getBytes(UTF_8)));

        try {
            decode(tmp.toByteArray(), strict);
        } catch (SerializerException e) {
            assertThat(e.getExceptionType(), is(PApplicationExceptionType.INVALID_PROTOCOL));
            assertThat(e.getMessage(), is("Missing protocol version"));
        }
    }

    @Test
    public void testService_fail_unversionedNameLen() throws IOException {
        writer.writeInt(15678);
        writer.writeBinary(Binary.wrap("abcd".getBytes(UTF_8)));

        try {
            decode(tmp.toByteArray(), lenient);
        } catch (SerializerException e) {
            assertThat(e.getExceptionType(), is(PApplicationExceptionType.PROTOCOL_ERROR));
            assertThat(e.getMessage(), is("Exceptionally long method name of 15678 bytes"));
        }
    }

    @Test
    public void testService_fail_unknownMethod() throws IOException {
        writer.writeInt(0x80010001);
        writer.writeInt(4);
        writer.writeBinary(Binary.wrap("abcd".getBytes(UTF_8)));
        writer.writeInt(44);

        try {
            decode(tmp.toByteArray(), lenient);
        } catch (SerializerException e) {
            assertThat(e.getExceptionType(), is(PApplicationExceptionType.UNKNOWN_METHOD));
            assertThat(e.getMessage(), is("No such method abcd on calculator.Calculator"));
        }
    }

    @Test
    public void testReadOK() throws IOException {
        assertOK(bytes(BinaryType.STOP), OptionalFields.builder().build(), strict);
        assertOK(bytes(BinaryType.STRUCT,
                       (short) 44,
                       BinaryType.I32,
                       (short) 44,
                       44,
                       BinaryType.VOID,
                       (short) 11,
                       BinaryType.STOP,
                       BinaryType.STOP),
                 OptionalFields.builder().build(),
                 strict);
    }

    @Test
    public void testReadFailures() throws IOException {
        assertFailure(bytes(),
                      "Missing expected byte",
                      OptionalFields.kDescriptor, lenient);
        assertFailure(bytes(BinaryType.I16),
                      "Missing byte 1 to expected short",
                      OptionalFields.kDescriptor, lenient);
        assertFailure(bytes(BinaryType.I16, (short) 4),
                      "Wrong field type for id=4: expected i32(8), got i16(6)",
                      OptionalFields.kDescriptor, lenient);
        assertFailure(bytes(BinaryType.I32, (short) 4, 4),
                      "Missing expected byte",
                      OptionalFields.kDescriptor, lenient);
        assertFailure(bytes(BinaryType.STOP),
                      "Missing required fields booleanValue,byteValue,shortValue,integerValue,longValue,doubleValue,stringValue,binaryValue,enumValue,compactValue in message providence.RequiredFields",
                      RequiredFields.kDescriptor, strict);
    }

    private byte[] bytes(Object... values) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BigEndianBinaryWriter writer = new BigEndianBinaryWriter(baos);
        for (Object o : values) {
            if (o instanceof Byte) {
                writer.writeByte((Byte) o);
            } else if (o instanceof Short) {
                writer.writeShort((Short) o);
            } else if (o instanceof Integer) {
                writer.writeInt((Integer) o);
            } else if (o instanceof Long) {
                writer.writeLong((Long) o);
            } else if (o instanceof Double) {
                writer.writeDouble((Double) o);
            } else {
                throw new IllegalArgumentException("");
            }
        }
        return baos.toByteArray();
    }

    private <M extends PMessage<M,F>, F extends PField>
    void assertOK(byte[] data, M message, Serializer serializer) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        assertThat(serializer.deserialize(in, message.descriptor()),
                   is(equalToMessage(message)));
    }

    private void assertFailure(byte[] data,
                               String message,
                               PMessageDescriptor<?,?> descriptor,
                               Serializer serializer) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        try {
            serializer.deserialize(in, descriptor);
            fail("no exception: " + message);
        } catch (IOException e) {
            if (!e.getMessage().equals(message)) {
                e.printStackTrace();
            }
            assertThat(e.getMessage(), is(message));
        }
    }
}
