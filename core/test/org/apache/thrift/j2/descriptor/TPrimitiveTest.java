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

package org.apache.thrift.j2.descriptor;

import org.apache.thrift.j2.TType;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Stein Eldar Johnsen
 * @since 18.10.15
 */
public class TPrimitiveTest {
    @Test
    public void testGetName() {
        assertEquals("bool", TPrimitive.BOOL.getName());
        assertEquals("byte", TPrimitive.BYTE.getName());
        assertEquals("i16", TPrimitive.I16.getName());
        assertEquals("i32", TPrimitive.I32.getName());
        assertEquals("i64", TPrimitive.I64.getName());
        assertEquals("double", TPrimitive.DOUBLE.getName());
        assertEquals("string", TPrimitive.STRING.getName());
        assertEquals("binary", TPrimitive.BINARY.getName());
    }

    @Test
    public void testGetName_noPackage() {
        assertEquals("bool", TPrimitive.BOOL.getQualifiedName(null));
        assertEquals("byte", TPrimitive.BYTE.getQualifiedName(null));
        assertEquals("i16", TPrimitive.I16.getQualifiedName(null));
        assertEquals("i32", TPrimitive.I32.getQualifiedName(null));
        assertEquals("i64", TPrimitive.I64.getQualifiedName(null));
        assertEquals("double", TPrimitive.DOUBLE.getQualifiedName(null));
        assertEquals("string", TPrimitive.STRING.getQualifiedName(null));
        assertEquals("binary", TPrimitive.BINARY.getQualifiedName(null));
    }

    @Test
    public void testGetPackageName() {
        assertNull(TPrimitive.BOOL.getPackageName());
        assertNull(TPrimitive.BYTE.getPackageName());
        assertNull(TPrimitive.I16.getPackageName());
        assertNull(TPrimitive.I32.getPackageName());
        assertNull(TPrimitive.I64.getPackageName());
        assertNull(TPrimitive.DOUBLE.getPackageName());
        assertNull(TPrimitive.STRING.getPackageName());
        assertNull(TPrimitive.BINARY.getPackageName());
    }

    @Test
    public void testGetTypeGroup() {
        Assert.assertEquals(TType.BOOL, TPrimitive.BOOL.getType());
        Assert.assertEquals(TType.BYTE, TPrimitive.BYTE.getType());
        Assert.assertEquals(TType.I16, TPrimitive.I16.getType());
        Assert.assertEquals(TType.I32, TPrimitive.I32.getType());
        Assert.assertEquals(TType.I64, TPrimitive.I64.getType());
        Assert.assertEquals(TType.DOUBLE, TPrimitive.DOUBLE.getType());
        Assert.assertEquals(TType.STRING, TPrimitive.STRING.getType());
        Assert.assertEquals(TType.BINARY, TPrimitive.BINARY.getType());
    }

    @Test
    public void testEquals() {
        assertEquals(TPrimitive.BOOL, TPrimitive.BOOL.provider().descriptor());
        assertEquals(TPrimitive.BYTE, TPrimitive.BYTE.provider().descriptor());
        assertEquals(TPrimitive.I16, TPrimitive.I16.provider().descriptor());
        assertEquals(TPrimitive.I32, TPrimitive.I32.provider().descriptor());
        assertEquals(TPrimitive.I64, TPrimitive.I64.provider().descriptor());
        assertEquals(TPrimitive.DOUBLE, TPrimitive.DOUBLE.provider().descriptor());
        assertEquals(TPrimitive.STRING, TPrimitive.STRING.provider().descriptor());
        assertEquals(TPrimitive.BINARY, TPrimitive.BINARY.provider().descriptor());
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(TPrimitive.BOOL, TPrimitive.BYTE.provider().descriptor());
        assertNotEquals(TPrimitive.BYTE, TPrimitive.I16.provider().descriptor());
        assertNotEquals(TPrimitive.I16, TPrimitive.I32.provider().descriptor());
        assertNotEquals(TPrimitive.I32, TPrimitive.I64.provider().descriptor());
        assertNotEquals(TPrimitive.I64, TPrimitive.DOUBLE.provider().descriptor());
        assertNotEquals(TPrimitive.DOUBLE, TPrimitive.STRING.provider().descriptor());
        assertNotEquals(TPrimitive.STRING, TPrimitive.BINARY.provider().descriptor());
        assertNotEquals(TPrimitive.BINARY, TPrimitive.BOOL.provider().descriptor());
    }
}
