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

package net.morimekta.providence;

import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.util.ProvidenceHelper;
import net.morimekta.test.calculator.Operand;
import net.morimekta.test.calculator.Operation;
import net.morimekta.test.calculator.Operator;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 18.10.15
 */
public class PMessageTest {
    private static Operation operation;

    @Before
    public void setUp() throws IOException, SerializerException {
        synchronized (PMessageTest.class) {
            if (operation == null) {
                operation = ProvidenceHelper.fromJsonResource("/json/calculator/compact.json", Operation.kDescriptor);
            }
        }
    }

    @Test
    public void testToString() throws IOException, SerializerException {
        assertEquals("calculator.Operand{imaginary:{v:1.7,i:-2}}",
                     operation.getOperands()
                              .get(1)
                              .toString());
        assertEquals("calculator.Operand{number:4.321}",
                     operation.getOperands()
                              .get(0)
                              .getOperation()
                              .getOperands()
                              .get(1)
                              .toString());
        assertEquals("calculator.Operation{operator:ADD,operands:[{number:1234},{number:4.321}]}",
                     operation.getOperands()
                              .get(0)
                              .getOperation()
                              .toString());
        assertEquals("calculator.Operation{" +
                     "operator:MULTIPLY,operands:[" +
                     "{operation:{operator:ADD,operands:[{number:1234},{number:4.321}]}}," +
                     "{imaginary:{v:1.7,i:-2}}" +
                     "]" +
                     "}", operation.toString());
    }

    @Test
    public void testMerge() {
        assertEquals(operation,
                     Operation.builder().merge(operation).build());

        assertEquals("calculator.Operation{" +
                     "operator:SUBTRACT,operands:[" +
                     "{operation:{operator:ADD,operands:[{number:1234},{number:4.321}]}}," +
                     "{imaginary:{v:1.7,i:-2}}" +
                     "]" +
                     "}",
                     operation.mergeWith(Operation.builder().setOperator(Operator.SUBTRACT).build()).toString());
    }

    @Test
    public void testEquals() {
        Operand a = Operand.builder()
                           .setNumber(42)
                           .build();
        Operand b = Operand.builder()
                           .setNumber(42)
                           .build();
        Operand c = Operand.builder()
                           .setNumber(44)
                           .build();

        assertEquals(a, b);
        assertNotEquals(a, c);
    }
}
