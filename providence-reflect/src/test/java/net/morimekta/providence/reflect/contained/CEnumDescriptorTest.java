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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class CEnumDescriptorTest {
    CEnumDescriptor type;

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
    }

    @Test
    public void testEnum() {
        assertEquals(6, type.getValues().length);

        assertEquals("ONE", type.getValues()[0].toString());
        assertEquals("TWO", type.getValues()[1].toString());
        assertEquals("THREE", type.getValues()[2].toString());
        assertEquals("FIVE", type.getValues()[3].toString());
        assertEquals("SIX", type.getValues()[4].toString());
        assertEquals("EIGHT", type.getValues()[5].toString());

        assertEquals("ONE", type.getValues()[0].getName());
        assertEquals("TWO", type.getValues()[1].getName());
        assertEquals("THREE", type.getValues()[2].getName());
        assertEquals("FIVE", type.getValues()[3].getName());
        assertEquals("SIX", type.getValues()[4].getName());
        assertEquals("EIGHT", type.getValues()[5].getName());

        assertEquals(1, type.getValues()[0].getValue());
        assertEquals(2, type.getValues()[1].getValue());
        assertEquals(3, type.getValues()[2].getValue());
        assertEquals(5, type.getValues()[3].getValue());
        assertEquals(6, type.getValues()[4].getValue());
        assertEquals(8, type.getValues()[5].getValue());

        Assert.assertEquals("MyEnum", type.getName());
        Assert.assertEquals("MyEnum", type.getQualifiedName("package"));
        Assert.assertEquals("package.MyEnum", type.getQualifiedName("other"));
    }

    @Test
    public void testProvider() {
        assertEquals(CEnumValue.class,
                     type.builder()
                         .setByValue(1)
                         .build()
                         .getClass());
    }
}
