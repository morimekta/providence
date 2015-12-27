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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class TCEnumTest {
    private TCEnumDescriptor mType;
    private TCEnum           mValue3;
    private TCEnum           mValue3_other;
    private TCEnum           mValue5;

    @Before
    public void setUp() {
        List<TCEnum> values = new LinkedList<>();
        mType = new TCEnumDescriptor("My comment",
                                     "package",
                                     "MyEnum");

        values.add(new TCEnum(null, 1, "ONE", mType));
        values.add(new TCEnum(null, 2, "TWO", mType));
        values.add(new TCEnum(null, 3, "THREE", mType));
        values.add(new TCEnum("Skipping stuff", 5, "FIVE", mType));
        values.add(new TCEnum(null, 6, "SIX", mType));
        values.add(new TCEnum("And more", 8, "EIGHT", mType));

        mType.setValues(values);

        mValue3 = mType.factory()
                       .builder()
                       .setByValue(3)
                       .build();
        mValue3_other = mType.factory()
                             .builder()
                             .setByValue(3)
                             .build();
        mValue5 = mType.factory()
                       .builder()
                       .setByValue(5)
                       .build();
    }

    @Test
    public void testGetValue() {
        assertEquals(3, mValue3.getValue());
        assertEquals(3, mValue3_other.getValue());
        assertEquals(5, mValue5.getValue());
    }

    @Test
    public void testGetDescriptor() {
        assertSame(mType, mValue3.descriptor());
    }

    @Test
    public void testEquals() {
        assertTrue(mValue3.equals(mValue3_other));
        assertFalse(mValue3.equals(mValue5));
        assertFalse(mValue3.equals(mType));
    }

    @Test
    public void testToString() {
        assertEquals("THREE", mValue3.toString());
        assertEquals("THREE", mValue3_other.toString());
        assertEquals("FIVE", mValue5.toString());
    }

    @Test
    public void testBuilder() {
        assertFalse(new TCEnum.Builder(mType).isValid());
        assertTrue(new TCEnum.Builder(mType).setByValue(3).isValid());
        assertFalse(new TCEnum.Builder(mType).setByValue(7).isValid());

        TCEnum.Builder threeBuilder = new TCEnum.Builder(mType).setByName("THREE");
        assertTrue(threeBuilder.isValid());
        assertEquals(mValue3, threeBuilder.build());

        TCEnum.Builder fourBuilder = new TCEnum.Builder(mType).setByName("FOUR");
        assertFalse(fourBuilder.isValid());
        assertNull(fourBuilder.build());
    }
}