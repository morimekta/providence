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

package org.apache.thrift.j2.reflect.contained;

import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.junit.Assert;
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
        Assert.assertEquals(6, mType.getValues().size());

        Assert.assertEquals("EnumValue{ONE,1}", mType.getValues().get(0).toString());
        Assert.assertEquals("EnumValue{TWO,2}", mType.getValues().get(1).toString());
        Assert.assertEquals("EnumValue{THREE,3}", mType.getValues().get(2).toString());
        Assert.assertEquals("EnumValue{FIVE,5}", mType.getValues().get(3).toString());
        Assert.assertEquals("EnumValue{SIX,6}", mType.getValues().get(4).toString());
        Assert.assertEquals("EnumValue{EIGHT,8}", mType.getValues().get(5).toString());

        Assert.assertEquals("ONE", mType.getValues().get(0).getName());
        Assert.assertEquals("TWO", mType.getValues().get(1).getName());
        Assert.assertEquals("THREE", mType.getValues().get(2).getName());
        Assert.assertEquals("FIVE", mType.getValues().get(3).getName());
        Assert.assertEquals("SIX", mType.getValues().get(4).getName());
        Assert.assertEquals("EIGHT", mType.getValues().get(5).getName());

        Assert.assertEquals(1, mType.getValues().get(0).getValue());
        Assert.assertEquals(2, mType.getValues().get(1).getValue());
        Assert.assertEquals(3, mType.getValues().get(2).getValue());
        Assert.assertEquals(5, mType.getValues().get(3).getValue());
        Assert.assertEquals(6, mType.getValues().get(4).getValue());
        Assert.assertEquals(8, mType.getValues().get(5).getValue());

        Assert.assertEquals("MyEnum", mType.getName());
        Assert.assertEquals("MyEnum", mType.getQualifiedName("package"));
        Assert.assertEquals("package.MyEnum", mType.getQualifiedName("other"));
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
