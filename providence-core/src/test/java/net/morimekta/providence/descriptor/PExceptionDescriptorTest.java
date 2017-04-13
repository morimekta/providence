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

import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PMessageVariant;
import net.morimekta.test.providence.core.calculator.CalculateException;
import net.morimekta.test.providence.core.calculator.Operation;
import net.morimekta.test.providence.core.number.Imaginary;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Stein Eldar Johnsen
 * @since 10.09.15.
 */
public class PExceptionDescriptorTest {
    PExceptionDescriptor<?, ?> type;

    @Before
    public void setUp() {
        type = CalculateException.kDescriptor;
    }

    @Test
    public void testCoreOverrides() {
        // Even though it's an exception, it inherits from PMessageDescriptor.
        assertThat(type.toString(), is(equalTo("calculator.CalculateException")));
        assertThat(type.hashCode(), is(not(0)));

        assertThat(type.equals(new Object()), is(false));
        assertThat(type.equals(CalculateException.kDescriptor), is(true));
        assertThat(type.equals((PMessageDescriptor) Operation.kDescriptor), is(false));
    }

    @Test
    public void testOverrides() {
        assertThat(type.getVariant(), is(PMessageVariant.EXCEPTION));
        assertThat(type.builder(), is(instanceOf(PMessageBuilder.class)));
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
