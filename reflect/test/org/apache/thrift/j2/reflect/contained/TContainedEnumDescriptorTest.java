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
public class TContainedEnumDescriptorTest {
    TContainedEnumDescriptor mType;

    @Before
    public void setUp() {
        List<TContainedEnum> values = new LinkedList<>();
        mType = new TContainedEnumDescriptor("My comment",
                                             "package",
                                             "MyEnum");

        values.add(new TContainedEnum(null, 1, "ONE", mType));
        values.add(new TContainedEnum(null, 2, "TWO", mType));
        values.add(new TContainedEnum(null, 3, "THREE", mType));
        values.add(new TContainedEnum("Skipping stuff", 5, "FIVE", mType));
        values.add(new TContainedEnum(null, 6, "SIX", mType));
        values.add(new TContainedEnum("And more", 8, "EIGHT", mType));

        mType.setValues(values);
    }

    @Test
    public void testEnum() {
        Assert.assertEquals(6, mType.getValues().length);

        Assert.assertEquals("ONE", mType.getValues()[0].toString());
        Assert.assertEquals("TWO", mType.getValues()[1].toString());
        Assert.assertEquals("THREE", mType.getValues()[2].toString());
        Assert.assertEquals("FIVE", mType.getValues()[3].toString());
        Assert.assertEquals("SIX", mType.getValues()[4].toString());
        Assert.assertEquals("EIGHT", mType.getValues()[5].toString());

        Assert.assertEquals("ONE", mType.getValues()[0].getName());
        Assert.assertEquals("TWO", mType.getValues()[1].getName());
        Assert.assertEquals("THREE", mType.getValues()[2].getName());
        Assert.assertEquals("FIVE", mType.getValues()[3].getName());
        Assert.assertEquals("SIX", mType.getValues()[4].getName());
        Assert.assertEquals("EIGHT", mType.getValues()[5].getName());

        Assert.assertEquals(1, mType.getValues()[0].getValue());
        Assert.assertEquals(2, mType.getValues()[1].getValue());
        Assert.assertEquals(3, mType.getValues()[2].getValue());
        Assert.assertEquals(5, mType.getValues()[3].getValue());
        Assert.assertEquals(6, mType.getValues()[4].getValue());
        Assert.assertEquals(8, mType.getValues()[5].getValue());

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
