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

import net.morimekta.test.providence.CompactFields;
import net.morimekta.test.providence.OptionalFields;
import net.morimekta.test.providence.RequiredFields;
import net.morimekta.test.providence.UnionFields;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for providence built sources - message main body.
 */
public class ProvidenceTest {
    private OptionalFields optionals;
    private RequiredFields requireds;
    private UnionFields union;

    @Before
    public void setUp() {
        optionals = OptionalFields.builder()
                                  .build();
        requireds = RequiredFields.builder()
                                  .build();
        union = UnionFields.withCompactValue(new CompactFields("a", 4, null));
    }

    @Test
    public void testUnion() {
        assertSame(UnionFields._Field.COMPACT_VALUE,
                   union.unionField());
        assertTrue(union.hasCompactValue());
        assertNotNull(union.getCompactValue());
        assertNull(union.getEnumValue());
        assertNull(union.getBinaryValue());
        assertFalse(union.hasBooleanValue());
        assertFalse(union.hasByteValue());
    }

    @Test
    public void testRequiredFields() {
        assertTrue(requireds.hasBooleanValue());
    }

    @Test
    public void testOptionalFields() {
        assertFalse(optionals.hasBooleanValue());
    }

    @Test
    public void testHashCode() {
        assertNotEquals(union.hashCode(), requireds.hashCode());
        assertNotEquals(optionals.hashCode(), requireds.hashCode());
    }
}
