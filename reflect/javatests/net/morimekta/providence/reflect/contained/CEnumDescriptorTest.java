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

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class CEnumDescriptorTest {
    CEnumDescriptor mType;

    @Before
    public void setUp() {
        List<CEnum> values = new LinkedList<>();
        mType = new CEnumDescriptor("My comment",
                                     "package",
                                     "MyEnum");

        values.add(new CEnum(null, 1, "ONE", mType));
        values.add(new CEnum(null, 2, "TWO", mType));
        values.add(new CEnum(null, 3, "THREE", mType));
        values.add(new CEnum("Skipping stuff", 5, "FIVE", mType));
        values.add(new CEnum(null, 6, "SIX", mType));
        values.add(new CEnum("And more", 8, "EIGHT", mType));

        mType.setValues(values);
    }

    @Test
    public void testEnum() {
        assertEquals(6, mType.getValues().length);

        assertEquals("ONE", mType.getValues()[0].toString());
        assertEquals("TWO", mType.getValues()[1].toString());
        assertEquals("THREE", mType.getValues()[2].toString());
        assertEquals("FIVE", mType.getValues()[3].toString());
        assertEquals("SIX", mType.getValues()[4].toString());
        assertEquals("EIGHT", mType.getValues()[5].toString());

        assertEquals("ONE", mType.getValues()[0].getName());
        assertEquals("TWO", mType.getValues()[1].getName());
        assertEquals("THREE", mType.getValues()[2].getName());
        assertEquals("FIVE", mType.getValues()[3].getName());
        assertEquals("SIX", mType.getValues()[4].getName());
        assertEquals("EIGHT", mType.getValues()[5].getName());

        assertEquals(1, mType.getValues()[0].getValue());
        assertEquals(2, mType.getValues()[1].getValue());
        assertEquals(3, mType.getValues()[2].getValue());
        assertEquals(5, mType.getValues()[3].getValue());
        assertEquals(6, mType.getValues()[4].getValue());
        assertEquals(8, mType.getValues()[5].getValue());

        Assert.assertEquals("MyEnum", mType.getName());
        Assert.assertEquals("MyEnum", mType.getQualifiedName("package"));
        Assert.assertEquals("package.MyEnum", mType.getQualifiedName("other"));
    }

    @Test
    public void testProvider() {
        assertEquals(CEnum.class,
                     mType.factory()
                          .builder()
                          .setByValue(1)
                          .build()
                          .getClass());
    }
}
