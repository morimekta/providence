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

package org.apache.thrift2.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.test.calculator.Operand;
import org.apache.test.calculator.Operation;
import org.apache.test.calculator.Operator;
import org.apache.test.number.Imaginary;
import org.apache.thrift2.util.TStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 18.10.15
 */
public class TCompactBinarySerializerTest {
    private Operation mOperation;

    @Before
    public void setUp() {
        mOperation = Operation.builder()
                              .setOperator(Operator.MULTIPLY)
                              .addToOperands(Operand.builder()
                                                    .setOperation(Operation.builder()
                                                                           .setOperator(Operator.ADD)
                                                                           .addToOperands(Operand.builder()
                                                                                                 .setNumber(1234)
                                                                                                 .build())
                                                                           .addToOperands(Operand.builder()
                                                                                                 .setNumber(4.321)
                                                                                                 .build())
                                                                           .build())
                                                    .build())
                              .addToOperands(Operand.builder()
                                                    .setImaginary(Imaginary.builder()
                                                                           .setV(1.7)
                                                                           .setI(-2.0)
                                                                           .build())
                                                    .build())
                              .build();
    }

    @Test
    public void testSerialize() throws IOException, TSerializeException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        TCompactBinarySerializer serializer = new TCompactBinarySerializer();

        int length = serializer.serialize(baos, mOperation);

        assertEquals(84, length);

        byte[] out = baos.toByteArray();
        assertEquals(length, out.length);

        ByteArrayInputStream bais = new ByteArrayInputStream(out);
        Operation operation = serializer.deserialize(bais, Operation.DESCRIPTOR);

        assertEquals(mOperation, operation);
    }

    @Test
    public void testWriteDouble() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        TCompactBinarySerializer serializer = new TCompactBinarySerializer();

        assertEquals(9, serializer.writeDouble(baos, 1234567890.0));
        assertEquals("30000080b48065d241", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(9, serializer.writeDouble(baos, 1.2345678900));
        assertEquals("301bde8342cac0f33f", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(9, serializer.writeDouble(baos, -1234567890.0));
        assertEquals("30000080b48065d2c1", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(9, serializer.writeDouble(baos, -1.2345678900));
        assertEquals("301bde8342cac0f3bf", TStringUtils.toHexString(baos.toByteArray()));
    }

    @Test
    public void testReadDouble() throws IOException, TSerializeException {
        TCompactBinarySerializer serializer = new TCompactBinarySerializer();
        ByteArrayInputStream bais;

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("000080b48065d241"));
        assertEquals(1234567890.0d, serializer.readDouble(bais), 0.0);

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("1bde8342cac0f33f"));
        assertEquals(1.2345678900d, serializer.readDouble(bais), 0.0);

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("000080b48065d2c1"));
        assertEquals(-1234567890.0d, serializer.readDouble(bais), 0.0);

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("1bde8342cac0f3bf"));
        assertEquals(-1.2345678900d, serializer.readDouble(bais), 0.0);
    }

    @Test
    public void testWriteUnsigned() throws IOException {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        TCompactBinarySerializer serializer = new TCompactBinarySerializer();

        assertEquals(1, serializer.writeUnsigned(baos, 1, 1));
        assertEquals("01", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(1, serializer.writeUnsigned(baos, 255, 1));
        assertEquals("ff", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(8, serializer.writeUnsigned(baos, Long.MAX_VALUE, 8));
        assertEquals("ffffffffffffff7f", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(8, serializer.writeUnsigned(baos, Long.MIN_VALUE, 8));
        assertEquals("0000000000000080", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(8, serializer.writeUnsigned(baos, -1, 8));
        assertEquals("ffffffffffffffff", TStringUtils.toHexString(baos.toByteArray()));
    }

    @Test
    public void testReadUnsigned() throws IOException, TSerializeException {
        TCompactBinarySerializer serializer = new TCompactBinarySerializer();
        ByteArrayInputStream bais;

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("01"));
        assertEquals(1, serializer.readUnsigned(bais, 1));

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("ff"));
        assertEquals(255, serializer.readUnsigned(bais, 1));

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("0100000000000000"));
        assertEquals(1, serializer.readUnsigned(bais, 8));

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("ffffffffffffffff"));
        assertEquals(-1, serializer.readUnsigned(bais, 8));

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("ffffffffffffff7f"));
        assertEquals(Long.MAX_VALUE, serializer.readUnsigned(bais, 8));

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("0000000000000080"));
        assertEquals(Long.MIN_VALUE, serializer.readUnsigned(bais, 8));
    }

    @Test
    public void testWriteSigned() throws IOException, TSerializeException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        TCompactBinarySerializer serializer = new TCompactBinarySerializer();

        assertEquals(1, serializer.writeSigned(baos, 1, 1));
        assertEquals("01", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(1, serializer.writeSigned(baos, -1, 1));
        assertEquals("81", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(8, serializer.writeSigned(baos, 1, 8));
        assertEquals("0100000000000000", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(8, serializer.writeSigned(baos, -1, 8));
        assertEquals("0100000000000080", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(8, serializer.writeSigned(baos, Long.MAX_VALUE, 8));
        assertEquals("ffffffffffffff7f", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(8, serializer.writeSigned(baos, Long.MIN_VALUE + 1, 8));
        assertEquals("ffffffffffffffff", TStringUtils.toHexString(baos.toByteArray()));

        baos.reset();

        assertEquals(8, serializer.writeSigned(baos, Long.MIN_VALUE, 8));
        assertEquals("0000000000000080", TStringUtils.toHexString(baos.toByteArray()));
    }

    @Test
    public void testReadSigned() throws IOException, TSerializeException {
        TCompactBinarySerializer serializer = new TCompactBinarySerializer();
        ByteArrayInputStream bais;

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("01"));
        assertEquals(1, serializer.readSigned(bais, 1));

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("81"));
        assertEquals(-1, serializer.readSigned(bais, 1));

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("0100000000000000"));
        assertEquals(1, serializer.readSigned(bais, 8));

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("0100000000000080"));
        assertEquals(-1, serializer.readSigned(bais, 8));

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("ffffffffffffff7f"));
        assertEquals(Long.MAX_VALUE, serializer.readSigned(bais, 8));

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("ffffffffffffffff"));
        assertEquals(Long.MIN_VALUE  + 1, serializer.readSigned(bais, 8));

        bais = new ByteArrayInputStream(TStringUtils.fromHexString("0000000000000080"));
        assertEquals(Long.MIN_VALUE, serializer.readSigned(bais, 8));
    }

}
