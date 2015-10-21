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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 20.09.15
 */
public class TFieldTest {
    TField<?> fieldA;
    TField<?> fieldB;
    TField<?> fieldC;
    TField<?> fieldD;
    TField<?> fieldE;
    TField<?> fieldF;
    TField<?> fieldG;
    TField<?> fieldH;

    @Before
    public void setUp() {
        fieldA = new TFieldInfo<>("comment", 4, false, "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldB = new TFieldInfo<>("comment", 4, false, "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldC = new TFieldInfo<>("tnemmoc", 4, false, "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldD = new TFieldInfo<>("comment", 6, false, "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldE = new TFieldInfo<>("comment", 4, true,  "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldF = new TFieldInfo<>("comment", 4, false, "eman", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldG = new TFieldInfo<>("comment", 4, false, "name", TPrimitive.I64.provider(), new TDefaultValueProvider<>(4l));
        fieldH = new TFieldInfo<>("comment", 4, false, "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(6));
    }

    @Test
    public void testToString() {
        assertEquals("TField{4: i32 name}", fieldA.toString());
        assertEquals("TField{4: i32 name}", fieldB.toString());
        assertEquals("TField{4: i32 name}", fieldC.toString());
        assertEquals("TField{6: i32 name}", fieldD.toString());
        assertEquals("TField{4: required i32 name}", fieldE.toString());
        assertEquals("TField{4: i32 eman}", fieldF.toString());
        assertEquals("TField{4: i64 name}", fieldG.toString());
        assertEquals("TField{4: i32 name}", fieldH.toString());
    }

    @Test
    public void testEquals() {
        assertEquals(fieldA, fieldB);
        assertEquals(fieldA, fieldC);
        assertEquals(fieldB, fieldC);

        assertNotEquals(fieldA, fieldD);
        assertNotEquals(fieldA, fieldE);
        assertNotEquals(fieldA, fieldF);
        assertNotEquals(fieldA, fieldG);
        assertNotEquals(fieldA, fieldH);

        assertNotEquals(fieldB, fieldD);
        assertNotEquals(fieldB, fieldE);
        assertNotEquals(fieldB, fieldF);
        assertNotEquals(fieldB, fieldG);
        assertNotEquals(fieldB, fieldH);

        assertNotEquals(fieldC, fieldD);
        assertNotEquals(fieldC, fieldE);
        assertNotEquals(fieldC, fieldF);
        assertNotEquals(fieldC, fieldG);
        assertNotEquals(fieldC, fieldH);

        assertNotEquals(fieldD, fieldE);
        assertNotEquals(fieldD, fieldF);
        assertNotEquals(fieldD, fieldG);
        assertNotEquals(fieldD, fieldH);

        assertNotEquals(fieldE, fieldF);
        assertNotEquals(fieldE, fieldG);
        assertNotEquals(fieldE, fieldH);

        assertNotEquals(fieldF, fieldG);
        assertNotEquals(fieldF, fieldH);

        assertNotEquals(fieldG, fieldH);

        fieldC = new TFieldInfo<>(null, 4, false, "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldH = new TFieldInfo<>("comment", 4, false, "name", TPrimitive.I32.provider(), null);

        assertEquals(fieldC, fieldA);
        assertEquals(fieldC, fieldB);

        assertNotEquals(fieldH, fieldA);
        assertNotEquals(fieldH, fieldB);
        assertNotEquals(fieldH, fieldC);
        assertNotEquals(fieldH, fieldD);
        assertNotEquals(fieldH, fieldE);
        assertNotEquals(fieldH, fieldF);
        assertNotEquals(fieldH, fieldG);
    }

    @Test
    public void testHashCode() {
        assertEquals(fieldA.hashCode(), fieldB.hashCode());
        assertEquals(fieldA.hashCode(), fieldC.hashCode());
        assertEquals(fieldB.hashCode(), fieldC.hashCode());

        assertNotEquals(fieldA.hashCode(), fieldD.hashCode());
        assertNotEquals(fieldA.hashCode(), fieldE.hashCode());
        assertNotEquals(fieldA.hashCode(), fieldF.hashCode());
        assertNotEquals(fieldA.hashCode(), fieldG.hashCode());

        assertNotEquals(fieldB.hashCode(), fieldD.hashCode());
        assertNotEquals(fieldB.hashCode(), fieldE.hashCode());
        assertNotEquals(fieldB.hashCode(), fieldF.hashCode());
        assertNotEquals(fieldB.hashCode(), fieldG.hashCode());

        assertNotEquals(fieldC.hashCode(), fieldD.hashCode());
        assertNotEquals(fieldC.hashCode(), fieldE.hashCode());
        assertNotEquals(fieldC.hashCode(), fieldF.hashCode());
        assertNotEquals(fieldC.hashCode(), fieldG.hashCode());

        assertNotEquals(fieldD.hashCode(), fieldE.hashCode());
        assertNotEquals(fieldD.hashCode(), fieldF.hashCode());
        assertNotEquals(fieldD.hashCode(), fieldG.hashCode());

        assertNotEquals(fieldE.hashCode(), fieldF.hashCode());
        assertNotEquals(fieldE.hashCode(), fieldG.hashCode());

        assertNotEquals(fieldF.hashCode(), fieldG.hashCode());
    }

    @Test
    public void testCompareTo() {
        assertEquals(0, fieldA.compareTo(fieldB));
        assertEquals(0, fieldD.compareTo(fieldD));

        assertEquals(0, fieldA.compareTo(fieldB));
        assertEquals(0, fieldA.compareTo(fieldC));
        assertEquals(-1, fieldA.compareTo(fieldD));
        assertEquals(0, fieldA.compareTo(fieldE));
        assertEquals(0, fieldA.compareTo(fieldF));
        assertEquals(0, fieldA.compareTo(fieldG));
        assertEquals(0, fieldA.compareTo(fieldH));

        assertEquals(1, fieldD.compareTo(fieldH));
        assertEquals(1, fieldD.compareTo(fieldG));
        assertEquals(1, fieldD.compareTo(fieldF));
        assertEquals(1, fieldD.compareTo(fieldE));
        assertEquals(1, fieldD.compareTo(fieldC));
        assertEquals(1, fieldD.compareTo(fieldB));
        assertEquals(1, fieldD.compareTo(fieldA));
    }
}
