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

import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;

public class CUnionTest {
    private CStructDescriptor struct;
    private CUnionDescriptor  normal;
    private CUnionDescriptor  sorted;
    private CUnionDescriptor  simple;

    @Before
    public void setUp() {
        struct = new CStructDescriptor(null, "test", "TestStruct", ImmutableList.of(
                new CField(null, 1, PRequirement.OPTIONAL, "first", PPrimitive.STRING.provider(), null, null),
                new CField(null, 2, PRequirement.OPTIONAL, "second", PPrimitive.I32.provider(), null, null)
        ), ImmutableMap.of("something", "special"));
        simple = new CUnionDescriptor(null, "test", "TestSimple", ImmutableList.of(
                new CField(null, 1, PRequirement.OPTIONAL, "first",
                           PPrimitive.STRING.provider(), null, null),
                new CField(null, 2, PRequirement.OPTIONAL, "second",
                           PPrimitive.I32.provider(), null, null)
        ), null);
        normal = new CUnionDescriptor("Test", "test", "TestUnion", ImmutableList.of(
                new CField(null, 1, PRequirement.OPTIONAL, "first", PPrimitive.STRING.provider(), null, null),
                new CField(null, 2, PRequirement.OPTIONAL, "second", PPrimitive.I32.provider(), null, null),
                new CField(null, 3, PRequirement.OPTIONAL, "third", () -> struct, null, null),
                new CField(null, 4, PRequirement.OPTIONAL, "fourth", PList.provider(PPrimitive.STRING.provider()), null, null),
                new CField(null, 5, PRequirement.OPTIONAL, "fifth", PSet.provider(PPrimitive.I32.provider()), null, null),
                new CField(null, 6, PRequirement.OPTIONAL, "sixth", PMap.provider(PPrimitive.I32.provider(), PPrimitive.STRING.provider()), null, null)
        ), ImmutableMap.of("something", "special"));
        sorted = new CUnionDescriptor(null, "test", "TestSorted", ImmutableList.of(
                new CField(null, 1, PRequirement.OPTIONAL, "first", PPrimitive.STRING.provider(), null, null),
                new CField(null, 2, PRequirement.OPTIONAL, "second", PPrimitive.I32.provider(), null, null),
                new CField(null, 3, PRequirement.OPTIONAL, "third", () -> struct, null, null),
                new CField(null, 4, PRequirement.OPTIONAL, "fourth", PList.provider(PPrimitive.STRING.provider()), null, null),
                new CField(null, 5, PRequirement.OPTIONAL, "fifth", PSet.provider(PPrimitive.I32.provider()), null, ImmutableMap.of(
                        "container", "sorted")),
                new CField(null, 6, PRequirement.OPTIONAL, "sixth", PMap.provider(PPrimitive.I32.provider(), PPrimitive.STRING.provider()), null, ImmutableMap.of(
                        "container", "sorted"))
        ), null);
    }

    @Test
    public void testUnion() {
        assertThat(simple, is(not(struct)));
        assertThat(simple, is(not(normal)));
        assertThat(normal, is(not(sorted)));

        assertThat(normal.getDocumentation(), is("Test"));
        assertThat(normal.getQualifiedName(), is("test.TestUnion"));

        assertThat(normal.getAnnotations(), hasSize(1));
        assertThat(normal.getAnnotations(), hasItem("something"));
        assertThat(simple.getAnnotations(), is(Collections.EMPTY_SET));
        assertThat(normal.hasAnnotation("something"), is(true));
        assertThat(simple.hasAnnotation("something"), is(false));
        assertThat(normal.getAnnotationValue("something"), is("special"));
        assertThat(simple.getAnnotationValue("something"), is(nullValue()));

        assertThat(simple.findFieldById(1), is(simple.getFields()[0]));
        assertThat(simple.findFieldByName("second"), is(simple.getFields()[1]));

        assertThat(simple.isSimple(), is(true));
        assertThat(normal.isSimple(), is(false));
    }

    @Test
    public void testUnionBuilder_constructor() {
        CUnion.Builder b1 = normal.builder();
        b1.mutator(3).set(1, "first");
        CUnion u11 = b1.build();
        assertThat(u11.unionField().getId(), is(3));

        CUnion.Builder b2 = normal.builder();
        b2.set(3, struct.builder().set(1, "first").build());
        CUnion u12 = b1.build();
        assertThat(u11, is(u12));
        assertThat(u11.hashCode(), is(u12.hashCode()));

        u11 = normal.builder().set(5, ImmutableSet.of(11, 12)).build();
        assertThat(u11.unionField().getId(), is(5));
        assertThat(u11.get(5), is(instanceOf(ImmutableSet.class)));

        u12 = sorted.builder().set(5, ImmutableSortedSet.of(11, 12)).build();
        assertThat(u12.unionField().getId(), is(5));
        assertThat(u12.get(5), is(instanceOf(ImmutableSortedSet.class)));

        u11 = normal.builder().set(6, ImmutableMap.of(11, "eleven")).build();
        assertThat(u11.unionField().getId(), is(6));
        assertThat(u11.get(6), is(instanceOf(ImmutableMap.class)));
        u12 = sorted.builder().set(6, ImmutableSortedMap.of(11, "eleven")).build();
        assertThat(u12.unionField().getId(), is(6));
        assertThat(u12.get(6), is(instanceOf(ImmutableSortedMap.class)));
    }

    @Test
    public void testUnionBuilder_commons() {
        CUnion.Builder b1 = normal.builder();
        assertThat(b1.isModified(1), is(false));

        b1.mutator(3).set(1, "first");
        CUnion u1 = b1.build();

        // TODO: fix.
        // assertThat(b1.isModified(1), is(true));

        CUnion u2 = u1.mutate()
                      .set(4, ImmutableList.of("1234"))
                      .build();
        CUnion u3 = u1.mutate().set(1, "not first").build();
        CUnion u4 = u3.mutate().set(1, "first").build();

        assertThat(u1.compareTo(u2), is(1));
        assertThat(u2.compareTo(u1), is(-1));
        assertThat(u1.compareTo(u1.mutate().build()), is(0));

        assertThat(u1.toString(), is("test.TestUnion{third={first=\"first\"}}"));
        assertThat(u1, is(u1));
        assertThat(u1, is(not(u2)));
        assertThat(u1.equals(null), is(false));
        assertThat(u4, is(not(u3)));

        assertThat(u1.hashCode(), is(not(u2.hashCode())));
        assertThat(u1.hashCode(), is(u1.hashCode()));

        assertThat(u1.compareTo(u2), is(1));
        assertThat(u2.compareTo(u1), is(-1));
        assertThat(u1.compareTo(u1.mutate().build()), is(0));
    }

    @Test
    public void testUnionBuilder_failures() {
        try {
            normal.builder().mutator(12);
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("No such field ID 12"));
        }
        try {
            normal.builder().mutator(1);
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Not a message field ID 1: first"));
        }
        try {
            normal.builder().validate();
            fail("no exception");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("No union field set in test.TestUnion"));
        }
        try {
            normal.builder().addTo(1, "boo");
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Unable to accept addTo on non-list field first"));
        }
        try {
            normal.builder().addTo(4, null);
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Adding null item to collection fourth"));
        }
    }

    @Test
    public void testUnionBuilder_mutator() {
        CUnion.Builder builder = normal.builder();
        builder.set(3, struct.builder().build());

        builder.mutator(3).set(1, "first");  //
        builder.mutator(3).set(2, "second");

        assertThat(builder.build().asString(), is("{third={first=\"first\",second=\"second\"}}"));
    }

    @Test
    public void testUnionBuilder_merge() {
        CUnion.Builder a = normal.builder();
        CUnion.Builder b = normal.builder();

        a.set(1, "first");
        b.merge(a.build());
        assertThat(b.build().toString(), is("test.TestUnion{first=\"first\"}"));

        a.set(1, "second");
        b.merge(a.build());
        assertThat(b.build().toString(), is("test.TestUnion{first=\"second\"}"));

        a.set(3, struct.builder().set(1, "first").build());
        b.set(3, struct.builder().build());
        b.merge(a.build());
        b.validate();
        assertThat(b.build().toString(), is("test.TestUnion{third={first=\"first\"}}"));

        a.set(3, struct.builder().set(2, "second").build());
        b.merge(a.build());
        assertThat(b.build().toString(), is("test.TestUnion{third={first=\"first\",second=\"second\"}}"));

        a.set(5, ImmutableSet.of(1, 2));
        b.set(5, ImmutableSet.of());
        b.merge(a.build());
        assertThat(b.build().toString(), is("test.TestUnion{fifth=[1,2]}"));

        a.set(6, ImmutableMap.of(1, "first"));
        b.set(6, ImmutableMap.of());
        b.merge(a.build());
        assertThat(b.build().toString(), is("test.TestUnion{sixth={1:\"first\"}}"));
    }

    @Test
    public void testUnionBuilder_set() {
        CUnion.Builder b = normal.builder();

        b.set(55, "foo");
        assertThat(b.valid(), is(false));
        assertThat(b.build().unionFieldIsSet(), is(false));

        b.set(1, "string");
        assertThat(b.valid(),is(true));
        assertThat(b.build().unionField(), is(notNullValue()));

        b.set(2, null);
        assertThat(b.valid(),is(true));
        assertThat(b.build().unionField(), is(notNullValue()));

        b.set(1, null);
        assertThat(b.valid(), is(false));
        assertThat(b.build().unionFieldIsSet(), is(false));
    }

    @Test
    public void testUnionBuilder_addTo() {
        CUnion.Builder b = sorted.builder();

        b.addTo(55, "boo");
        assertThat(b.valid(), is(false));
        assertThat(b.build().unionFieldIsSet(), is(false));

        b.addTo(4, 2);
        b.addTo(4, 1);
        assertThat(b.valid(), is(true));
        assertThat(b.build().toString(), is("test.TestSorted{fourth=[2,1]}"));

        b.addTo(5, 2);
        b.addTo(5, 1);
        assertThat(b.valid(), is(true));
        assertThat(b.build().toString(), is("test.TestSorted{fifth=[1,2]}"));
    }
}
