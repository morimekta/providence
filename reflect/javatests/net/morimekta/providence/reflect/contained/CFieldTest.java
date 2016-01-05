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

import net.morimekta.providence.descriptor.PDefaultValueProvider;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PRequirement;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 20.09.15
 */
public class CFieldTest {
    PField<?> field;
    PField<?> fieldSame;
    PField<?> fieldComment;
    PField<?> fieldKey;
    PField<?> fieldRequired;
    PField<?> fieldOptional;
    PField<?> fieldName;
    PField<?> fieldType;
    PField<?> fieldNoDefault;
    PField<?> fieldDefault;

    @Before
    public void setUp() {
        field = new CField<>("comment", 4, PRequirement.DEFAULT, "name", PPrimitive.I32.provider(), new PDefaultValueProvider<>(4));
        fieldSame = new CField<>("comment", 4, PRequirement.DEFAULT, "name", PPrimitive.I32.provider(), new PDefaultValueProvider<>(4));
        fieldComment = new CField<>("tnemmoc", 4, PRequirement.DEFAULT, "name", PPrimitive.I32.provider(), new PDefaultValueProvider<>(4));
        fieldKey = new CField<>("comment", 6, PRequirement.DEFAULT, "name", PPrimitive.I32.provider(), new PDefaultValueProvider<>(4));
        fieldRequired = new CField<>("comment", 4, PRequirement.REQUIRED, "name", PPrimitive.I32.provider(), new PDefaultValueProvider<>(4));
        fieldOptional = new CField<>("comment", 4, PRequirement.OPTIONAL, "name", PPrimitive.I32.provider(), new PDefaultValueProvider<>(4));
        fieldName = new CField<>("comment", 4, PRequirement.DEFAULT, "eman", PPrimitive.I32.provider(), new PDefaultValueProvider<>(4));
        fieldType = new CField<>("comment", 4, PRequirement.DEFAULT, "name", PPrimitive.I64.provider(), new PDefaultValueProvider<>(4L));
        fieldNoDefault = new CField<>("comment", 4, PRequirement.DEFAULT, "name", PPrimitive.I64.provider(), null);
        fieldDefault = new CField<>("comment", 4, PRequirement.DEFAULT, "name", PPrimitive.I32.provider(), new PDefaultValueProvider<>(6));
    }

    @Test
    public void testToString() {
        assertEquals("PField{4: i32 name}", field.toString());
        assertEquals("PField{4: i32 name}", fieldSame.toString());
        assertEquals("PField{4: i32 name}", fieldComment.toString());
        assertEquals("PField{6: i32 name}", fieldKey.toString());
        assertEquals("PField{4: required i32 name}", fieldRequired.toString());
        assertEquals("PField{4: i32 eman}", fieldName.toString());
        assertEquals("PField{4: i64 name}", fieldType.toString());
        assertEquals("PField{4: i32 name}", fieldDefault.toString());
        assertEquals("PField{4: optional i32 name}", fieldOptional.toString());
    }

    @Test
    public void testEquals() {
        assertEquals(field, fieldSame);
        assertEquals(field, fieldComment);
        assertEquals(fieldSame, fieldComment);

        assertNotEquals(field, fieldKey);
        assertNotEquals(field, fieldRequired);
        assertNotEquals(field, fieldName);
        assertNotEquals(field, fieldType);
        assertNotEquals(field, fieldDefault);
        assertNotEquals(field, fieldNoDefault);

        assertNotEquals(fieldSame, fieldKey);
        assertNotEquals(fieldSame, fieldRequired);
        assertNotEquals(fieldSame, fieldName);
        assertNotEquals(fieldSame, fieldType);
        assertNotEquals(fieldSame, fieldDefault);
        assertNotEquals(fieldSame, fieldNoDefault);

        assertNotEquals(fieldComment, fieldKey);
        assertNotEquals(fieldComment, fieldRequired);
        assertNotEquals(fieldComment, fieldName);
        assertNotEquals(fieldComment, fieldType);
        assertNotEquals(fieldComment, fieldDefault);
        assertNotEquals(fieldComment, fieldNoDefault);

        assertNotEquals(fieldKey, fieldRequired);
        assertNotEquals(fieldKey, fieldName);
        assertNotEquals(fieldKey, fieldType);
        assertNotEquals(fieldKey, fieldDefault);
        assertNotEquals(fieldKey, fieldNoDefault);

        assertNotEquals(fieldRequired, fieldName);
        assertNotEquals(fieldRequired, fieldType);
        assertNotEquals(fieldRequired, fieldDefault);
        assertNotEquals(fieldRequired, fieldNoDefault);

        assertNotEquals(fieldName, fieldType);
        assertNotEquals(fieldName, fieldDefault);
        assertNotEquals(fieldName, fieldNoDefault);

        assertNotEquals(fieldType, fieldDefault);
        assertNotEquals(fieldType, fieldNoDefault);

        assertNotEquals(fieldDefault, fieldNoDefault);

        // Null comment.
        fieldComment = new CField<>(null, 4, PRequirement.DEFAULT, "name", PPrimitive.I32.provider(), new PDefaultValueProvider<>(4));

        assertEquals(fieldComment, field);
        assertEquals(fieldComment, fieldSame);

    }

    @Test
    public void testHashCode() {
        assertEquals(field.hashCode(), fieldSame.hashCode());
        assertEquals(field.hashCode(), fieldComment.hashCode());
        assertEquals(fieldSame.hashCode(), fieldComment.hashCode());

        assertNotEquals(field.hashCode(), fieldKey.hashCode());
        assertNotEquals(field.hashCode(), fieldRequired.hashCode());
        assertNotEquals(field.hashCode(), fieldName.hashCode());
        assertNotEquals(field.hashCode(), fieldDefault.hashCode());

        assertNotEquals(fieldSame.hashCode(), fieldKey.hashCode());
        assertNotEquals(fieldSame.hashCode(), fieldRequired.hashCode());
        assertNotEquals(fieldSame.hashCode(), fieldName.hashCode());
        assertNotEquals(fieldSame.hashCode(), fieldDefault.hashCode());

        assertNotEquals(fieldComment.hashCode(), fieldKey.hashCode());
        assertNotEquals(fieldComment.hashCode(), fieldRequired.hashCode());
        assertNotEquals(fieldComment.hashCode(), fieldName.hashCode());
        assertNotEquals(fieldComment.hashCode(), fieldDefault.hashCode());

        assertNotEquals(fieldKey.hashCode(), fieldRequired.hashCode());
        assertNotEquals(fieldKey.hashCode(), fieldName.hashCode());
        assertNotEquals(fieldKey.hashCode(), fieldDefault.hashCode());

        assertNotEquals(fieldRequired.hashCode(), fieldName.hashCode());
        assertNotEquals(fieldRequired.hashCode(), fieldDefault.hashCode());

        assertNotEquals(fieldName.hashCode(), fieldDefault.hashCode());
    }
}
