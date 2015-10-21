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

import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.reflect.util.TTypeRegistry;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by morimekta on 05.09.15.
 */
public class TContainedMessageTest {
    TTypeRegistry              mRegistry;
    TContainedStructDescriptor mStructType;
    TContainedUnionDescriptor  mUnionType;

    @Before
    public void setUp() {
        mRegistry = new TTypeRegistry();

        List<TField<?>> fields = new LinkedList<>();
        fields.add(new TContainedField<>(null, 1, false, "field_bool", TPrimitive.BOOL.provider(), null));
        fields.add(new TContainedField<>(null, 2, false, "field_8", TPrimitive.BYTE.provider(), null));
        fields.add(new TContainedField<>(null, 65000, false, "field_16", TPrimitive.I16.provider(), null));
        fields.add(new TContainedField<>(null, 4, false, "field_32", TPrimitive.I32.provider(), null));
        fields.add(new TContainedField<>(null, 5, false, "field_64", TPrimitive.I64.provider(), null));
        fields.add(new TContainedField<>(null, 6, false, "field_dbl", TPrimitive.DOUBLE.provider(), null));
        fields.add(new TContainedField<>(null, 9998, false, "field_str", TPrimitive.STRING.provider(), null));
        fields.add(new TContainedField<>(null, 9999, false, "field_bin", TPrimitive.BINARY.provider(), null));
        fields.add(new TContainedField<>(null, 32000, false, "field_a", mRegistry.getProvider("TypeA", "test"), null));

        mStructType = new TContainedStructDescriptor(null, "test", "TypeA", fields);
        mUnionType = new TContainedUnionDescriptor(null, "test", "TypeA", fields);
    }

    @Test
    public void testStruct() {
        mRegistry.putDeclaredType(mStructType);

        TContainedStruct inner = mStructType.factory()
                                            .builder()
                                            .set(1, Boolean.TRUE)
                                            .set(2, (byte) 8)
                                            .set(65000, (short) 16)
                                            .set(4, 32)
                                            .set(5, Long.MAX_VALUE)
                                            .set(6, 1234567890.09876)
                                            .set(9998, "string")
                                            .set(9999, new byte[] { 9, 0, 8, 1, 7, 2, 6, 3, 5, 4 })
                                            .build();
        TContainedStruct outer = mStructType.factory()
                                            .builder()
                                            .set(32000, inner)
                                            .build();

        TContainedStruct struct = (TContainedStruct) outer.get(32000);

        assertTrue(struct.has(1));
        assertTrue(struct.has(2));
        assertTrue(struct.has(65000));
        assertTrue(struct.has(4));
        assertTrue(struct.has(5));
        assertTrue(struct.has(6));
        assertTrue(struct.has(9998));
        assertTrue(struct.has(9999));
        assertFalse(struct.has(32000));

        assertFalse(struct.has(1234));

        assertEquals(true, struct.get(1));
        assertEquals((byte) 8, struct.get(2));
        assertEquals((short) 16, struct.get(65000));
        assertEquals(32, struct.get(4));
        assertEquals(Long.MAX_VALUE, struct.get(5));
        assertEquals(1234567890.09876, struct.get(6));
        assertEquals("string", struct.get(9998));
        assertArrayEquals(new byte[] { 9, 0, 8, 1, 7, 2, 6, 3, 5, 4 },
                          (byte[]) struct.get(9999));

        assertNull(struct.get(1234));

        assertSame(struct, inner);
        assertFalse(outer.has(1));
        assertFalse(outer.has(2));
        assertFalse(outer.has(65000));
        assertFalse(outer.has(4));
        assertFalse(outer.has(5));
        assertFalse(outer.has(6));
        assertFalse(outer.has(9998));
        assertFalse(outer.has(9999));
        assertTrue(outer.has(32000));

        assertFalse(outer.has(1234));

        assertEquals(false, outer.get(1));
        assertEquals((byte) 0, outer.get(2));
        assertEquals((short) 0, outer.get(65000));
        assertEquals(0, outer.get(4));
        assertEquals((long) 0, outer.get(5));
        assertEquals(0.0d, outer.get(6));
        assertNull(outer.get(9998));
        assertNull(outer.get(9999));
        assertNotNull(outer.get(32000));

        assertNull(outer.get(1234));

        assertNotEquals(struct.toString(), outer.toString());
        assertEquals(struct.toString(), inner.toString());

        assertNotEquals(struct, outer);
        assertEquals(struct, inner);
    }

    @Test
    public void testUnion() {
        // ...
    }

    @Test
    public void testEquals() {
        // ...
    }}
