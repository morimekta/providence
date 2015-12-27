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

package org.apache.thrift.j2.util;

import org.apache.thrift.j2.TBinary;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 18.10.15
 */
public class TTypeUtilsTest {
    String  mString;
    String  mString_ne;
    String  mString_eq;
    Integer mInteger;
    Integer mInteger_ne;
    Integer mInteger_eq;
    Double  mDouble;
    Double  mDouble_eq;
    Double  mDouble_ne;
    TBinary mArray;
    TBinary mArray_eq;
    TBinary mArray_ne_content;
    TBinary mArray_ne_length;
    String  mArray_string;

    @Before
    public void setUp() {
        mString = "1";
        mString_ne = "2";
        mString_eq = "1";
        mInteger = 1;
        mInteger_ne = 2;
        mInteger_eq = 1;
        mDouble = 1.0;
        mDouble_ne = 1.2;
        mDouble_eq = 1.0;

        mArray = TBinary.wrap(new byte[] { '1', '2', '3' });
        mArray_eq = TBinary.wrap(new byte[] { '1', '2', '3' });
        mArray_ne_content = TBinary.wrap(new byte[] { '1', '2', '4' });
        mArray_ne_length = TBinary.wrap(new byte[] { '1', '2', '3', '4' });
        // Same content, but different type than the byte[].
        mArray_string = "123";
    }

    @Test
    public void testEquals_simple_eq() {
        assertTrue(TTypeUtils.equals(mString, mString_eq));
        assertTrue(TTypeUtils.equals(mInteger, mInteger_eq));
        assertTrue(TTypeUtils.equals(mDouble, mDouble_eq));
    }

    @Test
    public void testEquals_simple_ne() {
        assertFalse(TTypeUtils.equals(mString, mString_ne));
        assertFalse(TTypeUtils.equals(mInteger, mInteger_ne));
        assertFalse(TTypeUtils.equals(mDouble, mDouble_ne));
    }

    @Test
    public void testEquals_simple_typesafe() {
        // They are all numerically '1', but of different types.
        assertFalse(TTypeUtils.equals(mString, mInteger));
        assertFalse(TTypeUtils.equals(mString, mDouble));
        assertFalse(TTypeUtils.equals(mInteger, mDouble));
    }

    @Test
    public void testEquals_simple_array() {
        assertTrue(TTypeUtils.equals(mArray, mArray_eq));

        assertFalse(TTypeUtils.equals(mArray, mArray_ne_length));
        assertFalse(TTypeUtils.equals(mArray, mArray_ne_content));
        assertFalse(TTypeUtils.equals(mArray_ne_length, mArray));
        assertFalse(TTypeUtils.equals(mArray_ne_content, mArray));
        assertFalse(TTypeUtils.equals(mArray, mArray_string));
        assertFalse(TTypeUtils.equals(mArray_string, mArray));
    }

    @Test
    public void testEquals_compositeMap_equals() {
        // Order sensitive
        Map<Integer, Object> composite1 = new LinkedHashMap<>();
        Map<Integer, Object> composite2 = new HashMap<>();

        composite1.put(1, "1");
        composite1.put(2, 2);
        composite1.put(3, (short) 3);

        composite2.put(3, (short) 3);
        composite2.put(2, 2);
        composite2.put(1, "1");

        assertTrue(TTypeUtils.equals(composite1, composite2));
        assertTrue(TTypeUtils.equals(composite2, composite1));

        Map<Integer, Object> composite3 = new TreeMap<>(composite2);

        assertTrue(TTypeUtils.equals(composite1, composite3));
        assertTrue(TTypeUtils.equals(composite3, composite1));

        composite2.put(4, 2);

        assertFalse(TTypeUtils.equals(composite1, composite2));
        assertFalse(TTypeUtils.equals(composite2, composite1));
    }

    @Test
    public void testEquals_compositeList_equals() {
        List<Object> composite1 = new LinkedList<>();
        List<Object> composite2 = new ArrayList<>();

        composite1.add("1");
        composite1.add(2);
        composite1.add((short) 3);

        composite2.add("1");
        composite2.add(2);
        composite2.add((short) 3);

        assertTrue(TTypeUtils.equals(composite1, composite2));
        assertTrue(TTypeUtils.equals(composite2, composite1));

        composite2.remove(2);

        // Different content (same plus extra).
        assertFalse(TTypeUtils.equals(composite1, composite2));
        assertFalse(TTypeUtils.equals(composite2, composite1));

        composite2.add(0, (short) 3);

        // Same content, but different order.
        assertFalse(TTypeUtils.equals(composite1, composite2));
        assertFalse(TTypeUtils.equals(composite2, composite1));
    }

    @Test
    public void testEquals_compositeSet_equals() {
        Set<Object> composite1 = new TreeSet<>();
        Set<Object> composite2 = new LinkedHashSet<>();

        composite1.add("1");
        composite1.add("2");
        composite1.add("3");

        composite2.add("3");
        composite2.add("2");
        composite2.add("1");

        assertTrue(TTypeUtils.equals(composite1, composite2));
        assertTrue(TTypeUtils.equals(composite2, composite1));

        composite2.remove("3");

        // Different content (same plus extra).
        assertFalse(TTypeUtils.equals(composite1, composite2));
        assertFalse(TTypeUtils.equals(composite2, composite1));

        composite2.add("3");

        // Same content, but different order.
        assertTrue(TTypeUtils.equals(composite1, composite2));
        assertTrue(TTypeUtils.equals(composite2, composite1));
    }

    @Test
    public void testIsInteger() {
        assertTrue(TStringUtils.isInteger("0"));
        assertTrue(TStringUtils.isInteger("5"));
        assertTrue(TStringUtils.isInteger("12345"));
        assertTrue(TStringUtils.isInteger("1234567890"));
        assertTrue(TStringUtils.isInteger("987654321098765")); // long

        assertFalse(TStringUtils.isInteger("0.0")); // float / double
        assertFalse(TStringUtils.isInteger("0.5"));
        assertFalse(TStringUtils.isInteger("0.55555555555"));
        assertFalse(TStringUtils.isInteger("1234567890.0"));
        // scientific notation
        assertFalse(TStringUtils.isInteger("1.23456789E-8"));
        assertFalse(TStringUtils.isInteger("1.23456789E12"));
        assertFalse(TStringUtils.isInteger("1.4f"));
        assertFalse(TStringUtils.isInteger("1..23456789E12"));
        assertFalse(TStringUtils.isInteger("1..23456789E-8"));
        assertFalse(TStringUtils.isInteger("0xff")); // hex
        assertFalse(TStringUtils.isInteger("deadbeef")); // non-prefixed hex.
    }
}
