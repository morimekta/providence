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

package org.apache.thrift2.reflect.contained;

import org.apache.thrift2.descriptor.TEnumDescriptor;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by morimekta on 05.09.15.
 */
public class TContainedEnumTypeTest {
    TContainedEnumDescriptor mType;

    @Before
    public void setUp() {
        List<TEnumDescriptor.Value> values = new LinkedList<>();
        values.add(new TEnumDescriptor.Value(null, "ONE", 1));
        values.add(new TEnumDescriptor.Value(null, "TWO", 2));
        values.add(new TEnumDescriptor.Value(null, "THREE", 3));
        values.add(new TEnumDescriptor.Value("Skipping stuff", "FIVE", 5));
        values.add(new TEnumDescriptor.Value(null, "SIX", 6));
        values.add(new TEnumDescriptor.Value("And more", "EIGHT", 8));

        mType = new TContainedEnumDescriptor("My comment",
                                             "package",
                                             "MyEnum",
                                             values);
    }

    @Test
    public void testEnum() {
        assertEquals(6, mType.getValues().size());

        assertEquals("EnumValue{ONE,1}", mType.getValues().get(0).toString());
        assertEquals("EnumValue{TWO,2}", mType.getValues().get(1).toString());
        assertEquals("EnumValue{THREE,3}", mType.getValues().get(2).toString());
        assertEquals("EnumValue{FIVE,5}", mType.getValues().get(3).toString());
        assertEquals("EnumValue{SIX,6}", mType.getValues().get(4).toString());
        assertEquals("EnumValue{EIGHT,8}", mType.getValues().get(5).toString());

        assertEquals("ONE", mType.getValues().get(0).getName());
        assertEquals("TWO", mType.getValues().get(1).getName());
        assertEquals("THREE", mType.getValues().get(2).getName());
        assertEquals("FIVE", mType.getValues().get(3).getName());
        assertEquals("SIX", mType.getValues().get(4).getName());
        assertEquals("EIGHT", mType.getValues().get(5).getName());

        assertEquals(1, mType.getValues().get(0).getValue());
        assertEquals(2, mType.getValues().get(1).getValue());
        assertEquals(3, mType.getValues().get(2).getValue());
        assertEquals(5, mType.getValues().get(3).getValue());
        assertEquals(6, mType.getValues().get(4).getValue());
        assertEquals(8, mType.getValues().get(5).getValue());

        assertEquals("MyEnum", mType.getName());
        assertEquals("MyEnum", mType.getQualifiedName("package"));
        assertEquals("package.MyEnum", mType.getQualifiedName("other"));
    }

    @Test
    public void testProvider() {
        assertEquals(TContainedEnum.class,
                     mType.factory()
                          .builder()
                          .setByValue(1)
                          .build()
                          .getClass());
    }
}
