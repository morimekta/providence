/*
 * Copyright (c) 2016, Stein Eldar Johnsen
 *
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
package net.morimekta.providence.gentests;

import net.morimekta.providence.util.PrettyPrinter;
import net.morimekta.test.providence.CompactFields;
import net.morimekta.test.providence.Containers;
import net.morimekta.test.providence.DefaultValues;
import net.morimekta.test.providence.OptionalFields;
import net.morimekta.test.providence.RequiredFields;
import net.morimekta.test.providence.UnionFields;
import net.morimekta.test.providence.Value;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for providence built sources - message main body.
 */
public class ProvidenceTest {
    @Test
    public void testUnion() {
        UnionFields uf = UnionFields.withCompactValue(new CompactFields("a", 4, null));

        assertSame(UnionFields._Field.COMPACT_VALUE,
                   uf.unionField());
        assertTrue(uf.hasCompactValue());
        assertNotNull(uf.getCompactValue());
        assertNull(uf.getEnumValue());
        assertNull(uf.getBinaryValue());
        assertFalse(uf.hasBooleanValue());
        assertFalse(uf.hasByteValue());
    }

    @Test
    public void testRequiredFields() {
        RequiredFields rf = RequiredFields.builder().build();

        assertTrue(rf.hasBooleanValue());
        assertFalse(rf.isBooleanValue());

        assertTrue(rf.hasByteValue());
        assertEquals((byte) 0, rf.getByteValue());

        assertTrue(rf.hasShortValue());
        assertEquals((short) 0, rf.getShortValue());

        assertTrue(rf.hasIntegerValue());
        assertEquals(0, rf.getIntegerValue());

        assertTrue(rf.hasLongValue());
        assertEquals(0L, rf.getLongValue());

        assertTrue(rf.hasDoubleValue());
        assertEquals(0.0, rf.getDoubleValue(), 0.0);

        assertFalse(rf.hasStringValue());
        assertNull(rf.getStringValue());

        assertFalse(rf.hasBinaryValue());
        assertNull(rf.getBinaryValue());

        assertFalse(rf.hasEnumValue());
        assertNull(rf.getEnumValue());

        assertFalse(rf.hasCompactValue());
        assertNull(rf.getCompactValue());
    }

    @Test
    public void testOptionalFields() {
        OptionalFields of = OptionalFields.builder().build();

        assertFalse(of.hasBooleanValue());
        assertFalse(of.isBooleanValue());

        assertFalse(of.hasByteValue());
        assertEquals((byte) 0, of.getByteValue());

        assertFalse(of.hasShortValue());
        assertEquals((short) 0, of.getShortValue());

        assertFalse(of.hasIntegerValue());
        assertEquals(0, of.getIntegerValue());

        assertFalse(of.hasLongValue());
        assertEquals(0L, of.getLongValue());

        assertFalse(of.hasDoubleValue());
        assertEquals(0.0, of.getDoubleValue(), 0.0);

        assertFalse(of.hasStringValue());
        assertNull(of.getStringValue());

        assertFalse(of.hasBinaryValue());
        assertNull(of.getBinaryValue());

        assertFalse(of.hasEnumValue());
        assertNull(of.getEnumValue());

        assertFalse(of.hasCompactValue());
        assertNull(of.getCompactValue());
    }

    @Test
    public void testDefaultValues() {
        DefaultValues dv = DefaultValues.builder().build();

        assertFalse(dv.hasBooleanValue());
        assertTrue(dv.isBooleanValue());

        assertFalse(dv.hasByteValue());
        assertEquals((byte) -125, dv.getByteValue());

        assertFalse(dv.hasShortValue());
        assertEquals((short) 13579, dv.getShortValue());

        assertFalse(dv.hasIntegerValue());
        assertEquals(1234567890, dv.getIntegerValue());

        assertFalse(dv.hasLongValue());
        assertEquals(1234567891L, dv.getLongValue());

        assertFalse(dv.hasDoubleValue());
        assertEquals(2.99792458e+8, dv.getDoubleValue(), 0.0);

        assertFalse(dv.hasStringValue());
        assertEquals("test\\twith escapes\\nand\\u00a0ũñı©ôðé.", dv.getStringValue());

        assertFalse(dv.hasBinaryValue());
        assertNull(dv.getBinaryValue());

        assertFalse(dv.hasEnumValue());
        assertEquals(Value.SECOND, dv.getEnumValue());

        assertFalse(dv.hasCompactValue());
        assertNull(dv.getCompactValue());

        // And it serializes as no fields.
        assertEquals("", PrettyPrinter.debugString(dv));
    }

    @Test
    public void testHashCode() {
        OptionalFields of = OptionalFields.builder().build();
        OptionalFields of2 = OptionalFields.builder().build();
        RequiredFields rf = RequiredFields.builder().build();
        UnionFields uf = UnionFields.withCompactValue(new CompactFields("a", 4, null));

        assertNotSame(of, of2);
        assertEquals(of.hashCode(), of2.hashCode());
        assertNotEquals(uf.hashCode(), rf.hashCode());
        assertNotEquals(of.hashCode(), rf.hashCode());
    }

    @Test
    public void testMutable() {
        OptionalFields of = OptionalFields.builder()
                                          .setCompactValue(new CompactFields("a", 4, null))
                                          .build();
        Containers a = Containers.builder()
                                 .setOptionalFields(of)
                                 .build();

        Containers._Builder b = a.mutate();

        assertSame(of, b.build().getOptionalFields());

        b.mutableOptionalFields().setIntegerValue(55);

        Containers c = b.build();

        // Even if the intermediate structure is mutated
        // inner contained structures are not rebuilt.
        assertEquals(55, c.getOptionalFields().getIntegerValue());
        assertSame(of.getCompactValue(), c.getOptionalFields().getCompactValue());
    }
}
