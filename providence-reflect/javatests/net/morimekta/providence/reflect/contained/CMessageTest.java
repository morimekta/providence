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

import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.reflect.util.TypeRegistry;
import net.morimekta.util.Binary;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class CMessageTest {
    TypeRegistry      mRegistry;
    CStructDescriptor mStructType;
    CUnionDescriptor  mUnionType;

    @Before
    public void setUp() {
        mRegistry = new TypeRegistry();

        List<CField> fields = new LinkedList<>();
        fields.add(new CField<>(null, 1, PRequirement.OPTIONAL, "field_bool", PPrimitive.BOOL.provider(), null));
        fields.add(new CField<>(null, 2, PRequirement.OPTIONAL, "field_8", PPrimitive.BYTE.provider(), null));
        fields.add(new CField<>(null, 65000, PRequirement.OPTIONAL, "field_16", PPrimitive.I16.provider(), null));
        fields.add(new CField<>(null, 4, PRequirement.OPTIONAL, "field_32", PPrimitive.I32.provider(), null));
        fields.add(new CField<>(null, 5, PRequirement.OPTIONAL, "field_64", PPrimitive.I64.provider(), null));
        fields.add(new CField<>(null, 6, PRequirement.OPTIONAL, "field_dbl", PPrimitive.DOUBLE.provider(), null));
        fields.add(new CField<>(null, 9998, PRequirement.OPTIONAL, "field_str", PPrimitive.STRING.provider(), null));
        fields.add(new CField<>(null, 9999, PRequirement.OPTIONAL, "field_bin", PPrimitive.BINARY.provider(), null));
        fields.add(new CField<>(null,
                                32000,
                                PRequirement.OPTIONAL,
                                "field_a",
                                mRegistry.getProvider("TypeA", "test"),
                                null));

        mStructType = new CStructDescriptor(null, "test", "TypeA", fields);
        mUnionType = new CUnionDescriptor(null, "test", "TypeA", fields);
    }

    @Test
    public void testStruct() {
        mRegistry.putDeclaredType(mStructType);

        CStruct inner = mStructType.factory()
                                   .builder()
                                   .set(1, Boolean.TRUE)
                                   .set(2, (byte) 8)
                                   .set(65000, (short) 16)
                                   .set(4, 32)
                                   .set(5, Long.MAX_VALUE)
                                   .set(6, 1234567890.09876)
                                   .set(9998, "string")
                                   .set(9999, Binary.wrap(new byte[]{9, 0, 8, 1, 7, 2, 6, 3, 5, 4}))
                                   .build();
        CStruct outer = mStructType.factory()
                                   .builder()
                                   .set(32000, inner)
                                   .build();

        CStruct struct = (CStruct) outer.get(32000);

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
        assertEquals(Binary.wrap(new byte[]{9, 0, 8, 1, 7, 2, 6, 3, 5, 4}), struct.get(9999));

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
    }
}
