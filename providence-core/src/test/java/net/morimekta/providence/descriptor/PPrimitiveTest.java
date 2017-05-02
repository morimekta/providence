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

package net.morimekta.providence.descriptor;

import net.morimekta.providence.PType;
import net.morimekta.util.Binary;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Stein Eldar Johnsen
 * @since 18.10.15
 */
public class PPrimitiveTest {
    @Test
    public void testToString() {
        assertEquals("bool", PPrimitive.BOOL.toString());
        assertEquals("byte", PPrimitive.BYTE.toString());
        assertEquals("i16", PPrimitive.I16.toString());
        assertEquals("i32", PPrimitive.I32.toString());
        assertEquals("i64", PPrimitive.I64.toString());
        assertEquals("double", PPrimitive.DOUBLE.toString());
        assertEquals("string", PPrimitive.STRING.toString());
        assertEquals("binary", PPrimitive.BINARY.toString());
    }

    @Test
    public void testDefaultValue() {
        assertEquals(false, PPrimitive.BOOL.getDefaultValue());
        assertEquals((byte) 0, PPrimitive.BYTE.getDefaultValue());
        assertEquals((short) 0, PPrimitive.I16.getDefaultValue());
        assertEquals(0, PPrimitive.I32.getDefaultValue());
        assertEquals(0L, PPrimitive.I64.getDefaultValue());
        assertEquals(0.0d, PPrimitive.DOUBLE.getDefaultValue());
        assertEquals("", PPrimitive.STRING.getDefaultValue());
        assertEquals(Binary.empty(), PPrimitive.BINARY.getDefaultValue());
    }

    @Test
    public void testGetName() {
        assertEquals("bool", PPrimitive.BOOL.getName());
        assertEquals("byte", PPrimitive.BYTE.getName());
        assertEquals("i16", PPrimitive.I16.getName());
        assertEquals("i32", PPrimitive.I32.getName());
        assertEquals("i64", PPrimitive.I64.getName());
        assertEquals("double", PPrimitive.DOUBLE.getName());
        assertEquals("string", PPrimitive.STRING.getName());
        assertEquals("binary", PPrimitive.BINARY.getName());
    }

    @Test
    public void testGetName_noPackage() {
        assertEquals("bool", PPrimitive.BOOL.getQualifiedName());
        assertEquals("byte", PPrimitive.BYTE.getQualifiedName());
        assertEquals("i16", PPrimitive.I16.getQualifiedName());
        assertEquals("i32", PPrimitive.I32.getQualifiedName());
        assertEquals("i64", PPrimitive.I64.getQualifiedName());
        assertEquals("double", PPrimitive.DOUBLE.getQualifiedName());
        assertEquals("string", PPrimitive.STRING.getQualifiedName());
        assertEquals("binary", PPrimitive.BINARY.getQualifiedName());
    }

    @Test
    public void testGetPackageName() {
        assertThat(PPrimitive.BOOL.getProgramName(), nullValue());
        assertThat(PPrimitive.BYTE.getProgramName(), nullValue());
        assertThat(PPrimitive.I16.getProgramName(), nullValue());
        assertThat(PPrimitive.I32.getProgramName(), nullValue());
        assertThat(PPrimitive.I64.getProgramName(), nullValue());
        assertThat(PPrimitive.DOUBLE.getProgramName(), nullValue());
        assertThat(PPrimitive.STRING.getProgramName(), nullValue());
        assertThat(PPrimitive.BINARY.getProgramName(), nullValue());
    }

    @Test
    public void testGetTypeGroup() {
        assertEquals(PType.BOOL, PPrimitive.BOOL.getType());
        assertEquals(PType.BYTE, PPrimitive.BYTE.getType());
        assertEquals(PType.I16, PPrimitive.I16.getType());
        assertEquals(PType.I32, PPrimitive.I32.getType());
        assertEquals(PType.I64, PPrimitive.I64.getType());
        assertEquals(PType.DOUBLE, PPrimitive.DOUBLE.getType());
        assertEquals(PType.STRING, PPrimitive.STRING.getType());
        assertEquals(PType.BINARY, PPrimitive.BINARY.getType());
    }

    @Test
    public void testEquals() {
        assertEquals(PPrimitive.BOOL,
                     PPrimitive.BOOL.provider()
                                    .descriptor());
        assertEquals(PPrimitive.BYTE,
                     PPrimitive.BYTE.provider()
                                    .descriptor());
        assertEquals(PPrimitive.I16,
                     PPrimitive.I16.provider()
                                   .descriptor());
        assertEquals(PPrimitive.I32,
                     PPrimitive.I32.provider()
                                   .descriptor());
        assertEquals(PPrimitive.I64,
                     PPrimitive.I64.provider()
                                   .descriptor());
        assertEquals(PPrimitive.DOUBLE,
                     PPrimitive.DOUBLE.provider()
                                      .descriptor());
        assertEquals(PPrimitive.STRING,
                     PPrimitive.STRING.provider()
                                      .descriptor());
        assertEquals(PPrimitive.BINARY,
                     PPrimitive.BINARY.provider()
                                      .descriptor());
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(PPrimitive.BOOL,
                        PPrimitive.BYTE.provider()
                                       .descriptor());
        assertNotEquals(PPrimitive.BYTE,
                        PPrimitive.I16.provider()
                                      .descriptor());
        assertNotEquals(PPrimitive.I16,
                        PPrimitive.I32.provider()
                                      .descriptor());
        assertNotEquals(PPrimitive.I32,
                        PPrimitive.I64.provider()
                                      .descriptor());
        assertNotEquals(PPrimitive.I64,
                        PPrimitive.DOUBLE.provider()
                                         .descriptor());
        assertNotEquals(PPrimitive.DOUBLE,
                        PPrimitive.STRING.provider()
                                         .descriptor());
        assertNotEquals(PPrimitive.STRING,
                        PPrimitive.BINARY.provider()
                                         .descriptor());
        assertNotEquals(PPrimitive.BINARY,
                        PPrimitive.BOOL.provider()
                                       .descriptor());
    }

    @Test
    public void testFindByName() {
        assertThat(PPrimitive.findByName("void"), is(PPrimitive.VOID));
        assertThat(PPrimitive.findByName("bool"), is(PPrimitive.BOOL));
        assertThat(PPrimitive.findByName("byte"), is(PPrimitive.BYTE));
        assertThat(PPrimitive.findByName("i8"), is(PPrimitive.BYTE));
        assertThat(PPrimitive.findByName("i16"), is(PPrimitive.I16));
        assertThat(PPrimitive.findByName("i32"), is(PPrimitive.I32));
        assertThat(PPrimitive.findByName("i64"), is(PPrimitive.I64));
        assertThat(PPrimitive.findByName("double"), is(PPrimitive.DOUBLE));
        assertThat(PPrimitive.findByName("string"), is(PPrimitive.STRING));
        assertThat(PPrimitive.findByName("binary"), is(PPrimitive.BINARY));
        assertThat(PPrimitive.findByName("goo"), is(nullValue()));
    }
}
