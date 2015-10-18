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

package org.apache.thrift2.descriptor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by morimekta on 10.09.15.
 */
public class TStructDescriptorTest {
    TStructDescriptor<?> valueType;

    @Before
    public void setUp() {
        valueType = Operand.DESCRIPTOR;
    }

    @Test
    public void testToString() {
        // Even though it's a union, it inherits from TStructDescriptor.
        assertEquals("calculator.Operand", Operand.DESCRIPTOR.toString());
        assertEquals("number.Imaginary", Imaginary.DESCRIPTOR.toString());
        assertEquals("calculator.Operation", Operation.DESCRIPTOR.toString());
    }

    @Test
    public void testEquals() {
        assertEquals(Imaginary.DESCRIPTOR, Imaginary.provider().descriptor());
        assertEquals(Operation.DESCRIPTOR, Operation.provider().descriptor());

        assertNotEquals(Operation.DESCRIPTOR, Imaginary.DESCRIPTOR);
    }
}
