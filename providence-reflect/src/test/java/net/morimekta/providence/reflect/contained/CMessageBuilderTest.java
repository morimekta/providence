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

import net.morimekta.providence.model.ProgramType;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.providence.reflect.util.ProgramConverter;
import net.morimekta.providence.reflect.util.ProgramRegistry;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static net.morimekta.providence.util.ProvidenceHelper.debugString;
import static net.morimekta.testing.ExtraMatchers.equalToLines;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class CMessageBuilderTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private ProgramTypeRegistry registry;

    @Before
    public void setUp() throws IOException {
        File file = tmp.newFile("test.thrift").getCanonicalFile().getAbsoluteFile();
        ProgramRegistry tmp = new ProgramRegistry();
        ThriftProgramParser parser = new ThriftProgramParser();
        ProgramConverter converter = new ProgramConverter(tmp);
        ProgramType program = parser.parse(getClass().getResourceAsStream("/parser/tests/test.thrift"),
                                           file, ImmutableList.of());
        tmp.putProgram(file.getPath(), converter.convert(file.getPath(), program));
        registry = tmp.registryForPath(file.getCanonicalFile().getAbsolutePath());
    }

    @Test
    public void testOptionals() {
        CStruct.Builder ba = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.OptionalFields")
                                                                         .builder();
        CStruct.Builder bb = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.OptionalFields")
                                                                         .builder();

        ba.set(1, false);
        ba.set(2, (byte) 55);
        ba.set(42, ImmutableList.of());  // does not exists.
        ba.set(5, null);

        bb.set(2, (byte) 42);
        bb.set(5, 42L);

        assertThat(ba.isSet(1), is(true));
        assertThat(ba.isSet(2), is(true));
        assertThat(ba.isSet(5), is(false));

        CStruct a = ba.build();

        assertThat(a.has(1), is(true));
        assertThat(a.has(2), is(true));
        assertThat(a.has(5), is(false));

        assertThat(a.get(1), is(false));
        assertThat(a.get(2), is((byte) 55));
        assertThat(a.get(5), is(nullValue()));

        ba.merge(bb.build());

        assertThat(ba.isSet(1), is(true));
        assertThat(ba.isSet(2), is(true));
        assertThat(ba.isSet(5), is(true));

        CMessage b = ba.build();

        assertThat(b.has(1), is(true));
        assertThat(b.has(2), is(true));
        assertThat(b.has(5), is(true));

        assertThat(b.get(1), is(false));
        assertThat(b.get(2), is((byte) 42));
        assertThat(b.get(5), is(42L));

    }

    @Test
    public void testContainers() {
        CStruct.Builder of = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.OptionalFields")
                                                                         .builder();
        of.set(1, true);
        of.set(2, (byte) 42);

        CStruct.Builder ba = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.Containers").builder();
        CStruct.Builder bb = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.Containers").builder();

        ba.set(4, ImmutableList.of(1, 2, 3, 4));
        ba.set(14, ImmutableSet.of(4, 3, 2, 1));
        ba.set(24, ImmutableMap.of(1, 4,
                                   2, 3,
                                   3, 2,
                                   4, 1));
        ba.set(53, of.build());

        of.clear(2);
        of.set(4, 42);
        bb.set(4, ImmutableList.of(3, 4, 5, 6));
        bb.set(14, ImmutableSet.of(3, 4, 5, 6));
        bb.set(24, ImmutableMap.of(3, 5,
                                   4, 6,
                                   5, 7,
                                   6, 8));
        bb.set(5, ImmutableList.of(5L, 6L, 7L, 8L));
        bb.set(15, ImmutableSet.of(5L, 6L, 7L, 8L));
        bb.set(25, ImmutableMap.of(5L, 4L,
                                   6L, 3L,
                                   7L, 2L,
                                   8L, 1L));
        bb.set(53, of.build());

        bb.mutator(52).set(1, true);
        CStruct.Builder df = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.DefaultFields")
                                                                         .builder();
        df.set(1, true);
        df.set(2, (byte) 42);
        bb.set(52, df.build());
        bb.mutator(52).set(5, 42L);
        bb.mutator(52).set(5, 52L);

        ba.merge(bb.build());

        CStruct a = ba.build();

        assertThat(debugString(a), is(equalToLines(
                "integerList = [3, 4, 5, 6]\n" +
                "longList = [5, 6, 7, 8]\n" +
                "integerSet = [4, 3, 2, 1, 5, 6]\n" +
                "longSet = [5, 6, 7, 8]\n" +
                "integerMap = {\n" +
                "  1: 4\n" +
                "  2: 3\n" +
                "  3: 5\n" +
                "  4: 6\n" +
                "  5: 7\n" +
                "  6: 8\n" +
                "}\n" +
                "longMap = {\n" +
                "  5: 4\n" +
                "  6: 3\n" +
                "  7: 2\n" +
                "  8: 1\n" +
                "}\n" +
                "defaultFields = {\n" +
                "  booleanValue = true\n" +
                "  byteValue = 42\n" +
                "  shortValue = 0\n" +
                "  integerValue = 0\n" +
                "  longValue = 52\n" +
                "  doubleValue = 0\n" +
                "  stringValue = \"\"\n" +
                "  binaryValue = b64()\n" +
                "}\n" +
                "optionalFields = {\n" +
                "  booleanValue = true\n" +
                "  byteValue = 42\n" +
                "  integerValue = 42\n" +
                "}")));

        CStruct.Builder bc = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.Containers")
                                                                         .builder();

        bc.set(4, ImmutableList.of(123));
        bc.addTo(4, 42);

        bc.addTo(bc.descriptor().fieldForId(5), 42L);

        bc.set(11, ImmutableSet.of(true));
        bc.addTo(11, false);

        bc.addTo(bc.descriptor().fieldForId(15), 42L);
        bb.addTo(123, 42L);  // no effect.

        assertThat(debugString(bc.build()), is(equalToLines(
                "integerList = [123, 42]\n" +
                "longList = [42]\n" +
                "booleanSet = [true, false]\n" +
                "longSet = [42]")));

        assertThat(bc.toString(),
                   is("test.Containers._Builder{values={4=[123, 42], 5=[42], 11=[true, false], 15=[42]}, modified=[4, 5, 11, 15]}"));
    }

    @Test
    public void testValidity() {
        CStruct.Builder ba = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.OptionalFields")
                                                                         .builder();
        CStruct.Builder bb = (CStruct.Builder) (CMessageBuilder) registry.getDeclaredType("test.RequiredFields")
                                                                         .builder();

        assertThat(ba.isModified(1), is(false));
        assertThat(ba.isSet(1), is(false));

        ba.set(1, true);
        ba.set(2, (byte) 42);

        assertThat(ba.isModified(1), is(true));
        assertThat(ba.isSet(1), is(true));

        assertThat(ba.valid(), is(true));
        ba.validate();  // no exception

        bb.set(1, true);
        bb.set(2, (byte) 42);
        bb.set(3, (short) 42);
        bb.set(4, 42);
        bb.set(5, (long) 42);
        bb.set(6, 42.42);
        bb.set(7, "42");

        assertThat(bb.valid(), is(false));
        try {
            bb.validate();
            fail("no exception");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("Missing required fields binaryValue,enumValue,compactValue in message test.RequiredFields"));
        }

        try {
            bb.addTo(2, null);
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Adding null value"));
        }

        try {
            bb.addTo(2, 12L);
            fail("no exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Field byteValue in test.RequiredFields is not a collection: byte"));
        }
    }

}
