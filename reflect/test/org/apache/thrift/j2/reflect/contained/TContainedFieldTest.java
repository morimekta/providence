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

import org.apache.thrift.j2.descriptor.TDefaultValueProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 20.09.15
 */
public class TContainedFieldTest {
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
        fieldA = new TContainedField<>("comment", 4, false, "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldB = new TContainedField<>("comment", 4, false, "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldC = new TContainedField<>("tnemmoc", 4, false, "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldD = new TContainedField<>("comment", 6, false, "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldE = new TContainedField<>("comment", 4, true,  "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldF = new TContainedField<>("comment", 4, false, "eman", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldG = new TContainedField<>("comment", 4, false, "name", TPrimitive.I64.provider(), new TDefaultValueProvider<>(4l));
        fieldH = new TContainedField<>("comment", 4, false, "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(6));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("TField{4: i32 name}", fieldA.toString());
        Assert.assertEquals("TField{4: i32 name}", fieldB.toString());
        Assert.assertEquals("TField{4: i32 name}", fieldC.toString());
        Assert.assertEquals("TField{6: i32 name}", fieldD.toString());
        Assert.assertEquals("TField{4: required i32 name}", fieldE.toString());
        Assert.assertEquals("TField{4: i32 eman}", fieldF.toString());
        Assert.assertEquals("TField{4: i64 name}", fieldG.toString());
        Assert.assertEquals("TField{4: i32 name}", fieldH.toString());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(fieldA, fieldB);
        Assert.assertEquals(fieldA, fieldC);
        Assert.assertEquals(fieldB, fieldC);

        Assert.assertNotEquals(fieldA, fieldD);
        Assert.assertNotEquals(fieldA, fieldE);
        Assert.assertNotEquals(fieldA, fieldF);
        Assert.assertNotEquals(fieldA, fieldG);
        Assert.assertNotEquals(fieldA, fieldH);

        Assert.assertNotEquals(fieldB, fieldD);
        Assert.assertNotEquals(fieldB, fieldE);
        Assert.assertNotEquals(fieldB, fieldF);
        Assert.assertNotEquals(fieldB, fieldG);
        Assert.assertNotEquals(fieldB, fieldH);

        Assert.assertNotEquals(fieldC, fieldD);
        Assert.assertNotEquals(fieldC, fieldE);
        Assert.assertNotEquals(fieldC, fieldF);
        Assert.assertNotEquals(fieldC, fieldG);
        Assert.assertNotEquals(fieldC, fieldH);

        Assert.assertNotEquals(fieldD, fieldE);
        Assert.assertNotEquals(fieldD, fieldF);
        Assert.assertNotEquals(fieldD, fieldG);
        Assert.assertNotEquals(fieldD, fieldH);

        Assert.assertNotEquals(fieldE, fieldF);
        Assert.assertNotEquals(fieldE, fieldG);
        Assert.assertNotEquals(fieldE, fieldH);

        Assert.assertNotEquals(fieldF, fieldG);
        Assert.assertNotEquals(fieldF, fieldH);

        Assert.assertNotEquals(fieldG, fieldH);

        fieldC = new TContainedField<>(null, 4, false, "name", TPrimitive.I32.provider(), new TDefaultValueProvider<>(4));
        fieldH = new TContainedField<>("comment", 4, false, "name", TPrimitive.I32.provider(), null);

        Assert.assertEquals(fieldC, fieldA);
        Assert.assertEquals(fieldC, fieldB);

        Assert.assertNotEquals(fieldH, fieldA);
        Assert.assertNotEquals(fieldH, fieldB);
        Assert.assertNotEquals(fieldH, fieldC);
        Assert.assertNotEquals(fieldH, fieldD);
        Assert.assertNotEquals(fieldH, fieldE);
        Assert.assertNotEquals(fieldH, fieldF);
        Assert.assertNotEquals(fieldH, fieldG);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(fieldA.hashCode(), fieldB.hashCode());
        Assert.assertEquals(fieldA.hashCode(), fieldC.hashCode());
        Assert.assertEquals(fieldB.hashCode(), fieldC.hashCode());

        Assert.assertNotEquals(fieldA.hashCode(), fieldD.hashCode());
        Assert.assertNotEquals(fieldA.hashCode(), fieldE.hashCode());
        Assert.assertNotEquals(fieldA.hashCode(), fieldF.hashCode());
        Assert.assertNotEquals(fieldA.hashCode(), fieldG.hashCode());

        Assert.assertNotEquals(fieldB.hashCode(), fieldD.hashCode());
        Assert.assertNotEquals(fieldB.hashCode(), fieldE.hashCode());
        Assert.assertNotEquals(fieldB.hashCode(), fieldF.hashCode());
        Assert.assertNotEquals(fieldB.hashCode(), fieldG.hashCode());

        Assert.assertNotEquals(fieldC.hashCode(), fieldD.hashCode());
        Assert.assertNotEquals(fieldC.hashCode(), fieldE.hashCode());
        Assert.assertNotEquals(fieldC.hashCode(), fieldF.hashCode());
        Assert.assertNotEquals(fieldC.hashCode(), fieldG.hashCode());

        Assert.assertNotEquals(fieldD.hashCode(), fieldE.hashCode());
        Assert.assertNotEquals(fieldD.hashCode(), fieldF.hashCode());
        Assert.assertNotEquals(fieldD.hashCode(), fieldG.hashCode());

        Assert.assertNotEquals(fieldE.hashCode(), fieldF.hashCode());
        Assert.assertNotEquals(fieldE.hashCode(), fieldG.hashCode());

        Assert.assertNotEquals(fieldF.hashCode(), fieldG.hashCode());
    }
}
