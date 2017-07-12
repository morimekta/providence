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

import net.morimekta.providence.util.ThriftAnnotation;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class CEnumDescriptorTest {
    CEnumDescriptor type;

    @Before
    public void setUp() {
        List<CEnumValue> values = new LinkedList<>();
        type = new CEnumDescriptor("My comment", "package", "MyEnum", ImmutableMap.of(
                ThriftAnnotation.DEPRECATED.tag, ""
        ));

        values.add(new CEnumValue(null, 1, "ONE", type, null));
        values.add(new CEnumValue(null, 2, "TWO", type, null));
        values.add(new CEnumValue(null, 3, "THREE", type, null));
        values.add(new CEnumValue("Skipping stuff", 5, "FIVE", type, null));
        values.add(new CEnumValue(null, 6, "SIX", type, null));
        values.add(new CEnumValue("And more", 8, "EIGHT", type, null));

        type.setValues(values);
    }

    @Test
    public void testEnum() {
        assertThat(type.getDocumentation(), is("My comment"));
        assertThat(type.getAnnotations(), hasSize(1));
        assertThat(type.getAnnotations(), hasItem("deprecated"));
        assertThat(type.hasAnnotation("boo"), is(false));
        assertThat(type.getAnnotationValue("boo"), is(nullValue()));

        assertThat(type.hasAnnotation(ThriftAnnotation.CONTAINER), is(false));
        assertThat(type.getAnnotationValue(ThriftAnnotation.CONTAINER), is(nullValue()));

        assertThat(type.hasAnnotation(ThriftAnnotation.DEPRECATED), is(true));
        assertThat(type.getAnnotationValue(ThriftAnnotation.DEPRECATED), is(""));

        assertEquals(6, type.getValues().length);

        assertEquals("ONE", type.getValues()[0].toString());
        assertEquals("TWO", type.getValues()[1].toString());
        assertEquals("THREE", type.getValues()[2].toString());
        assertEquals("FIVE", type.getValues()[3].toString());
        assertEquals("SIX", type.getValues()[4].toString());
        assertEquals("EIGHT", type.getValues()[5].toString());

        assertEquals("ONE", type.getValues()[0].asString());
        assertEquals("TWO", type.getValues()[1].asString());
        assertEquals("THREE", type.getValues()[2].asString());
        assertEquals("FIVE", type.getValues()[3].asString());
        assertEquals("SIX", type.getValues()[4].asString());
        assertEquals("EIGHT", type.getValues()[5].asString());

        assertEquals(1, type.getValues()[0].asInteger());
        assertEquals(2, type.getValues()[1].asInteger());
        assertEquals(3, type.getValues()[2].asInteger());
        assertEquals(5, type.getValues()[3].asInteger());
        assertEquals(6, type.getValues()[4].asInteger());
        assertEquals(8, type.getValues()[5].asInteger());

        Assert.assertEquals("MyEnum", type.getName());
        Assert.assertEquals("MyEnum", type.getQualifiedName("package"));
        Assert.assertEquals("package.MyEnum", type.getQualifiedName("other"));
    }

    @Test
    public void testProvider() {
        assertEquals(CEnumValue.class,
                     type.builder()
                         .setById(1)
                         .build()
                         .getClass());
    }
}
