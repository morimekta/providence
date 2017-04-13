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

import net.morimekta.test.providence.core.calculator.Operand;
import net.morimekta.test.providence.core.calculator.Operation;
import net.morimekta.test.providence.core.calculator.Operator;
import net.morimekta.test.providence.core.number.Imaginary;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 18.10.15
 */
public class PrettySerializerTest {
    private Operation mOperation;
    private String    mFormatted;

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

        mFormatted = "operator = MULTIPLY\n" +
                     "operands = [\n" +
                     "  {\n" +
                     "    operation = {\n" +
                     "      operator = ADD\n" +
                     "      operands = [\n" +
                     "        {\n" +
                     "          number = 1234\n" +
                     "        },\n" +
                     "        {\n" +
                     "          number = 4.321\n" +
                     "        }\n" +
                     "      ]\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    imaginary = {\n" +
                     "      v = 1.7\n" +
                     "      i = -2\n" +
                     "    }\n" +
                     "  }\n" +
                     "]";
    }

    @Test
    public void testFormat() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrettySerializer serializer = new PrettySerializer("  ", " ", "\n", "", false);
        serializer.serialize(baos, mOperation);
        assertEquals(mFormatted, new String(baos.toByteArray(), UTF_8));
    }

    @Test
    public void testParse() throws IOException {
        PrettySerializer serializer = new PrettySerializer("  ", " ", "\n", "", false);

        Operation actual = serializer.deserialize(getClass().getResourceAsStream("/json/calculator/pretty.cfg"), Operation.kDescriptor);

        assertEquals(mOperation, actual);
    }
}
