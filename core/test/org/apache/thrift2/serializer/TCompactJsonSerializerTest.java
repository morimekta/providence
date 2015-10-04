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
import java.nio.charset.StandardCharsets;

import org.apache.calculator.Operand;
import org.apache.calculator.Operation;
import org.apache.calculator.Operator;
import org.apache.number.Imaginary;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TCompactJsonSerializerTest {
    private Operation mOperation;
    private String    mCompactIdJson;
    private String    mCompactNamedJson;

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
        mCompactIdJson = "{" +
                         "    \"1\":4," +
                         "    \"2\":[" +
                         "        {\"1\":{" +
                         "            \"1\":2," +
                         "            \"2\":[" +
                         "                {\"2\":1234}," +
                         "                {\"2\":4.321}" +
                         "            ]" +
                         "        }}," +
                         "        {\"3\":{" +
                         "            \"1\":1.7," +
                         "            \"2\":-2" +
                         "        }}" +
                         "    ]" +
                         "}";
        mCompactNamedJson = "{" +
                            "    \"operator\":\"MULTIPLY\"," +
                            "    \"operands\":[" +
                            "        {\"operation\":{" +
                            "            \"operator\":\"ADD\"," +
                            "            \"operands\":[" +
                            "                {\"number\":1234}," +
                            "                {\"number\":4.321}" +
                            "            ]" +
                            "        }}," +
                            "        {\"imaginary\":{" +
                            "            \"v\":1.7," +
                            "            \"i\":-2" +
                            "        }}" +
                            "    ]" +
                            "}";
    }

    @Test
    public void testSerialize_compactIdJson() throws TSerializeException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TCompactJsonSerializer serializer = new TCompactJsonSerializer();

        String expectedOutput = mCompactIdJson.replaceAll(" ", "");
        int expectedLength = serializer.serialize(baos, mOperation);
        assertEquals(expectedOutput, new String(baos.toByteArray(), StandardCharsets.UTF_8));
        assertEquals(expectedLength, baos.toByteArray().length);
    }

    @Test
    public void testSerialize_compactNamedJson() throws TSerializeException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TCompactJsonSerializer serializer = new TCompactJsonSerializer(TCompactJsonSerializer.IdType.NAME);

        String expectedOutput = mCompactNamedJson.replaceAll(" ", "");
        int expectedLength = serializer.serialize(baos, mOperation);
        assertEquals(expectedOutput, new String(baos.toByteArray(), StandardCharsets.UTF_8));
        assertEquals(expectedLength, baos.toByteArray().length);
    }

    @Test
    public void testDeserialize_compactIdJson() throws TSerializeException {
        ByteArrayInputStream bais = new ByteArrayInputStream(mCompactIdJson.getBytes());
        // Deserializing can deserialize both formats regardless of serializer
        // type.
        TCompactJsonSerializer serializer = new TCompactJsonSerializer(TCompactJsonSerializer.IdType.NAME);
        Operation operation = serializer.deserialize(bais, Operation.DESCRIPTOR);

        assertEquals(mOperation, operation);
    }

    @Test
    public void testDeserialize_compactNamedJson() throws TSerializeException {
        ByteArrayInputStream bais = new ByteArrayInputStream(mCompactNamedJson.getBytes());
        // Deserializing can deserialize both formats regardless of serializer
        // type.
        TCompactJsonSerializer serializer = new TCompactJsonSerializer();
        Operation operation = serializer.deserialize(bais, Operation.DESCRIPTOR);

        assertEquals(mOperation, operation);
    }
}
