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

package net.morimekta.providence.reflect.contained;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class CEnumValueTest {
    private CEnumDescriptor type;
    private CEnumValue      value3;
    private CEnumValue      value3_other;
    private CEnumValue      value5;

    @Before
    public void setUp() {
        List<CEnumValue> values = new LinkedList<>();
        type = new CEnumDescriptor("My comment", "package", "MyEnum", null);

        values.add(new CEnumValue(null, 1, "ONE", type, null));
        values.add(new CEnumValue(null, 2, "TWO", type, null));
        values.add(new CEnumValue(null, 3, "THREE", type, null));
        values.add(new CEnumValue("Skipping stuff", 5, "FIVE", type, null));
        values.add(new CEnumValue(null, 6, "SIX", type, null));
        values.add(new CEnumValue("And more", 8, "EIGHT", type, null));

        type.setValues(values);

        value3 = type.builder()
                     .setByValue(3)
                     .build();
        value3_other = type.builder()
                           .setByValue(3)
                           .build();
        value5 = type.builder()
                     .setByValue(5)
                     .build();
    }

    @Test
    public void testGetValue() {
        assertEquals(3, value3.getValue());
        assertEquals(3, value3_other.getValue());
        assertEquals(5, value5.getValue());
    }

    @Test
    public void testGetDescriptor() {
        assertSame(type, value3.descriptor());
    }

    @Test
    public void testEquals() {
        assertTrue(value3.equals(value3_other));
        assertFalse(value3.equals(value5));
        assertFalse(value3.equals(type));
    }

    @Test
    public void testToString() {
        assertEquals("THREE", value3.toString());
        assertEquals("THREE", value3_other.toString());
        assertEquals("FIVE", value5.toString());
    }

    @Test
    public void testBuilder() {
        assertFalse(new CEnumValue.Builder(type).valid());
        assertTrue(new CEnumValue.Builder(type).setByValue(3)
                                               .valid());
        assertFalse(new CEnumValue.Builder(type).setByValue(7)
                                                .valid());

        CEnumValue.Builder threeBuilder = new CEnumValue.Builder(type).setByName("THREE");
        assertTrue(threeBuilder.valid());
        assertEquals(value3, threeBuilder.build());

        CEnumValue.Builder fourBuilder = new CEnumValue.Builder(type).setByName("FOUR");
        assertFalse(fourBuilder.valid());
        assertNull(fourBuilder.build());
    }
}
