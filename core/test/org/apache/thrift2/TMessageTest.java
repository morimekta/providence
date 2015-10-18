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

package org.apache.thrift2;

import org.apache.test.calculator.Operand;
import org.apache.test.number.Imaginary;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 18.10.15
 */
public class TMessageTest {
    @Test
    public void testToString() {
        Operand value = Operand.builder().setNumber(44).build();

        assertEquals("calculator.Operand{number:44}", value.toString());

        value = Operand.builder()
                     .setImaginary(Imaginary.builder()
                                            .setV(12.9)
                                            .setI(1.0)
                                            .build())
                     .build();

        assertEquals("calculator.Operand{imaginary:{v:12.9,i:1}}", value.toString());
    }

    @Test
    public void testEquals() {
        Operand a = Operand.builder().setNumber(42).build();
        Operand b = Operand.builder().setNumber(42).build();
        Operand c = Operand.builder().setNumber(44).build();

        assertEquals(a, b);
        assertNotEquals(a, c);
    }
}
