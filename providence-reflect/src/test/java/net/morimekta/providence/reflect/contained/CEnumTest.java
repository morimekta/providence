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
public class CEnumTest {
    private CEnumDescriptor mType;
    private CEnum           mValue3;
    private CEnum           mValue3_other;
    private CEnum           mValue5;

    @Before
    public void setUp() {
        List<CEnum> values = new LinkedList<>();
        mType = new CEnumDescriptor("My comment", "package", "MyEnum", null);

        values.add(new CEnum(null, 1, "ONE", mType, null));
        values.add(new CEnum(null, 2, "TWO", mType, null));
        values.add(new CEnum(null, 3, "THREE", mType, null));
        values.add(new CEnum("Skipping stuff", 5, "FIVE", mType, null));
        values.add(new CEnum(null, 6, "SIX", mType, null));
        values.add(new CEnum("And more", 8, "EIGHT", mType, null));

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
        assertFalse(new CEnum.Builder(mType).isValid());
        assertTrue(new CEnum.Builder(mType).setByValue(3)
                                           .isValid());
        assertFalse(new CEnum.Builder(mType).setByValue(7)
                                            .isValid());

        CEnum.Builder threeBuilder = new CEnum.Builder(mType).setByName("THREE");
        assertTrue(threeBuilder.isValid());
        assertEquals(mValue3, threeBuilder.build());

        CEnum.Builder fourBuilder = new CEnum.Builder(mType).setByName("FOUR");
        assertFalse(fourBuilder.isValid());
        assertNull(fourBuilder.build());
    }
}
