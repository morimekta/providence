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

package net.morimekta.providence.descriptor;

import net.morimekta.test.providence.core.calculator.Operand;
import net.morimekta.test.providence.core.calculator.Operation;
import net.morimekta.test.providence.core.number.Imaginary;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 10.09.15.
 */
public class PStructDescriptorTest {
    PStructDescriptor<?, ?> valueType;

    @Before
    public void setUp() {
        valueType = Imaginary.kDescriptor;
    }

    @Test
    public void testToString() {
        // Even though it's a union, it inherits from PStructDescriptor.
        assertEquals("calculator.Operand", Operand.kDescriptor.toString());
        assertEquals("number.Imaginary", Imaginary.kDescriptor.toString());
        assertEquals("calculator.Operation", Operation.kDescriptor.toString());
    }

    @Test
    public void testEquals() {
        assertEquals(Imaginary.kDescriptor,
                     Imaginary.provider()
                              .descriptor());
        assertEquals(Operation.kDescriptor,
                     Operation.provider()
                              .descriptor());

        assertNotEquals(Operation.kDescriptor, Imaginary.kDescriptor);
    }
}
