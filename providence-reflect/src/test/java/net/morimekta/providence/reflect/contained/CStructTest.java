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
import net.morimekta.providence.model.MessageType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class CStructTest {
    @Test
    public void testCStruct() {
        CStructDescriptor descriptor = new CStructDescriptor(
                null, "test", "Test",
                ImmutableList.of(
                        new CField(null, 1, PRequirement.OPTIONAL, "first", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 2, PRequirement.OPTIONAL, "second", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 3, PRequirement.OPTIONAL, "third", PPrimitive.STRING.provider(), null, null)
                ),
                null);
        CStructDescriptor d2 = new CStructDescriptor(
                null, "test", "Test2",
                ImmutableList.of(
                        new CField(null, 1, PRequirement.OPTIONAL, "first", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 2, PRequirement.OPTIONAL, "second", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 3, PRequirement.OPTIONAL, "third", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 4, PRequirement.OPTIONAL, "msg", () -> descriptor, null, null)
                ),
                ImmutableMap.of("nothing", "special"));
        assertThat(descriptor.isJsonCompactible(), is(false));

        CStruct.Builder builder = new CStruct.Builder(descriptor);
        builder.set(1, "first");
        builder.set(2, "second");
        assertThat(builder.valid(), is(true));
        CStruct struct = builder.build();

        builder = new CStruct.Builder(descriptor);
        builder.set(2, "second");
        builder.set(3, "third");
        assertThat(builder.valid(), is(true));
        CStruct struct2 = builder.build();

        assertThat(struct, is(not(struct2)));
        assertThat(struct, is(not(MessageType.builder().build())));
        assertThat(struct.equals(getNull()), is(false));
        assertThat(struct.compareTo(d2.builder().build()), is(-1));
        assertThat(struct.compareTo(struct2), is(1));

        CStruct m1 = d2.builder()
                       .set(4, descriptor.builder().build())
                       .build();
        CStruct m2 = d2.builder()
                       .set(4, descriptor.builder()
                                         .set(1, "boo")
                                         .build())
                       .build();

        assertThat(m1, is(m1));
        assertThat(m1, is(not(m2)));
        assertThat(m1.equals(m2), is(false));
        assertThat(m1.equals(struct), is(false));
        assertThat(m1.compareTo(m2), is(-1));

        assertThat(m1.hashCode(), is(not(m2.hashCode())));
        assertThat(m1.hashCode(), is(not(struct.hashCode())));

        // descriptors.

        assertThat(d2.getDocumentation(), is(nullValue()));
        assertThat(d2.findFieldByName("msg"), is(d2.getFields()[3]));
        assertThat(d2.findFieldByName("boo"), is(nullValue()));
        assertThat(d2.findFieldById(4), is(d2.getFields()[3]));
        assertThat(d2.findFieldById(55), is(nullValue()));

        assertThat(descriptor.getAnnotations(), is(Collections.EMPTY_SET));
        assertThat(d2.getAnnotations(), hasSize(1));
        assertThat(d2.getAnnotations(), hasItem("nothing"));
        assertThat(descriptor.hasAnnotation("nothing"), is(false));
        assertThat(d2.hasAnnotation("nothing"), is(true));
        assertThat(descriptor.getAnnotationValue("nothing"), is(nullValue()));
        assertThat(d2.getAnnotationValue("nothing"), is("special"));

        assertThat(descriptor.isSimple(), is(true));
        assertThat(d2.isSimple(), is(false));
    }

    private CStruct getNull() {
        return null;
    }

    @Test
    public void testJsonCompact() {
        CStructDescriptor descriptor = new CStructDescriptor(
                null, "test", "Test",
                ImmutableList.of(
                        new CField(null, 1, PRequirement.OPTIONAL, "first", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 2, PRequirement.OPTIONAL, "second", PPrimitive.STRING.provider(), null, null)
                ),
                ImmutableMap.of("json.compact", ""));
        assertThat(descriptor.isJsonCompactible(), is(true));

        CStruct.Builder builder = new CStruct.Builder(descriptor);
        builder.set(1, "first");
        builder.set(2, "second");
        assertThat(builder.valid(), is(true));
        CStruct struct = builder.build();
        assertThat(struct.jsonCompact(), is(true));
    }

    @Test
    public void testJsonCompact_notBecauseFieldValue() {
        CStructDescriptor descriptor = new CStructDescriptor(
                null, "test", "Test",
                ImmutableList.of(
                        new CField(null, 1, PRequirement.OPTIONAL, "first", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 2, PRequirement.OPTIONAL, "second", PPrimitive.STRING.provider(), null, null)
                ),
                ImmutableMap.of("json.compact", ""));
        assertThat(descriptor.isJsonCompactible(), is(true));

        CStruct.Builder builder = new CStruct.Builder(descriptor);
        builder.set(2, "second");
        assertThat(builder.valid(), is(true));
        CStruct struct = builder.build();

        assertThat(struct.jsonCompact(), is(false));
    }

    @Test
    public void testJsonCompact_notBecauseMissingAnnotation() {
        CStructDescriptor descriptor = new CStructDescriptor(
                null, "test", "Test",
                ImmutableList.of(
                        new CField(null, 1, PRequirement.OPTIONAL, "first", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 2, PRequirement.OPTIONAL, "second", PPrimitive.STRING.provider(), null, null)
                ),
                ImmutableMap.of());
        assertThat(descriptor.isJsonCompactible(), is(false));

        CStruct.Builder builder = new CStruct.Builder(descriptor);
        builder.set(1, "first");
        builder.set(2, "second");
        assertThat(builder.valid(), is(true));
        CStruct struct = builder.build();
        assertThat(struct.jsonCompact(), is(false));
    }

    @Test
    public void testJsonCompact_notBecauseFieldRequirement() {
        CStructDescriptor descriptor = new CStructDescriptor(
                null, "test", "Test",
                ImmutableList.of(
                        new CField(null, 1, PRequirement.OPTIONAL, "first", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 2, PRequirement.REQUIRED, "second", PPrimitive.STRING.provider(), null, null)
                ),
                ImmutableMap.of("json.compact", ""));
        assertThat(descriptor.isJsonCompactible(), is(false));
    }


    @Test
    public void testJsonCompact_notBecauseTooManyFields() {
        CStructDescriptor descriptor = new CStructDescriptor(
                null, "test", "Test",
                ImmutableList.of(
                        new CField(null, 1, PRequirement.OPTIONAL, "first", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 2, PRequirement.OPTIONAL, "second", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 3, PRequirement.OPTIONAL, "third", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 4, PRequirement.OPTIONAL, "fourth", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 5, PRequirement.OPTIONAL, "fifth", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 6, PRequirement.OPTIONAL, "sixth", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 7, PRequirement.OPTIONAL, "seventh", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 8, PRequirement.OPTIONAL, "eighth", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 9, PRequirement.OPTIONAL, "ninth", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 10, PRequirement.OPTIONAL, "tenth", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 11, PRequirement.OPTIONAL, "eleventh", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 12, PRequirement.OPTIONAL, "twelfth", PPrimitive.STRING.provider(), null, null)
                ),
                ImmutableMap.of("json.compact", ""));
        assertThat(descriptor.isJsonCompactible(), is(false));
    }

    @Test
    public void testJsonCompact_notBecauseIrregularFields() {
        CStructDescriptor descriptor = new CStructDescriptor(
                null, "test", "Test",
                ImmutableList.of(
                        new CField(null, 1, PRequirement.OPTIONAL, "first", PPrimitive.STRING.provider(), null, null),
                        new CField(null, 3, PRequirement.OPTIONAL, "third", PPrimitive.STRING.provider(), null, null)
                ),
                ImmutableMap.of("json.compact", ""));
        assertThat(descriptor.isJsonCompactible(), is(false));
    }
}
