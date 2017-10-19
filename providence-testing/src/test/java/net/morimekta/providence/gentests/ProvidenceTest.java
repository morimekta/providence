/*
 * Copyright (c) 2016, Stein Eldar Johnsen
 *
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
package net.morimekta.providence.gentests;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.testing.generator.GeneratorWatcher;
import net.morimekta.providence.testing.generator.SimpleGeneratorWatcher;
import net.morimekta.test.providence.testing.AutoIdFields;
import net.morimekta.test.providence.testing.CompactFields;
import net.morimekta.test.providence.testing.Containers;
import net.morimekta.test.providence.testing.DefaultValues;
import net.morimekta.test.providence.testing.EnumNames;
import net.morimekta.test.providence.testing.ExceptionFields;
import net.morimekta.test.providence.testing.OptionalFields;
import net.morimekta.test.providence.testing.RequiredFields;
import net.morimekta.test.providence.testing.UnionFields;
import net.morimekta.test.providence.testing.Value;
import net.morimekta.test.providence.testing.calculator.Calculator;
import net.morimekta.test.providence.testing.math.OtherCalculator;
import net.morimekta.test.providence.testing.service.Request;
import net.morimekta.util.Binary;

import com.google.common.collect.ImmutableSet;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static net.morimekta.providence.util.ProvidenceHelper.debugString;
import static net.morimekta.providence.util.ProvidenceHelper.parseDebugString;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Tests for providence built sources - message main body.
 */
public class ProvidenceTest {
    @Rule
    public SimpleGeneratorWatcher generator = GeneratorWatcher.create();

    @Test
    public void testUnion() {
        UnionFields uf = UnionFields.withCompactValue(new CompactFields("a", 4, null));

        assertThat(uf.unionField(), is(UnionFields._Field.COMPACT_VALUE));
        assertThat(uf.hasCompactValue(), is(true));
        assertThat(uf.getCompactValue(), is(any(CompactFields.class)));
        assertThat(uf.getEnumValue(), is(nullValue()));
        assertThat(uf.getBinaryValue(), is(nullValue()));
        assertThat(uf.hasBooleanValue(), is(false));
        assertThat(uf.hasByteValue(), is(false));
    }

    @Test
    public void testEnumNaming() {
        assertThat(EnumNames.UM4V.asString(), is("UM4V"));
        assertThat(EnumNames.UMP3.asString(), is("UMP3"));
        assertThat(EnumNames.LM4V.asString(), is("lm4v"));
        assertThat(EnumNames.LMP3.asString(), is("lmp3"));
        assertThat(EnumNames.UMPEG4.asString(), is("Umpeg4"));
        assertThat(EnumNames.L_MPEG4.asString(), is("lMPEG4"));
        assertThat(EnumNames.U4L.asString(), is("U4l"));
        assertThat(EnumNames.L4_U.asString(), is("l4U"));
    }

    @Test
    public void testRequiredFields() {
        RequiredFields rf = RequiredFields.builder().build();

        assertThat(rf.hasBooleanValue(), is(true));
        assertThat(rf.isBooleanValue(), is(false));

        assertThat(rf.hasByteValue(), is(true));
        assertThat(rf.getByteValue(), is((byte) 0));

        assertThat(rf.hasShortValue(), is(true));
        assertThat(rf.getShortValue(), is((short) 0));

        assertThat(rf.hasIntegerValue(), is(true));
        assertThat(rf.getIntegerValue(), is(0));

        assertThat(rf.hasLongValue(), is(true));
        assertThat(rf.getLongValue(), is(0L));

        assertThat(rf.hasDoubleValue(), is(true));
        assertThat(rf.getDoubleValue(), is(0.0));

        // null pointers, presence = false.
        assertThat(rf.hasStringValue(), is(true));
        assertThat(rf.getStringValue(), is(""));

        assertThat(rf.hasBinaryValue(), is(true));
        assertThat(rf.getBinaryValue(), is(Binary.empty()));

        assertThat(rf.hasEnumValue(), is(false));
        assertThat(rf.getEnumValue(), is(nullValue()));

        assertThat(rf.hasCompactValue(), is(false));
        assertThat(rf.getCompactValue(), is(nullValue()));
    }

    @Test
    public void testOptionalFields() {
        OptionalFields of = OptionalFields.builder().build();

        assertThat(of.hasBooleanValue(), is(false));
        assertThat(of.isBooleanValue(), is(false));

        assertThat(of.hasByteValue(), is(false));
        assertThat(of.getByteValue(), is((byte) 0));

        assertThat(of.hasShortValue(), is(false));
        assertThat(of.getShortValue(), is((short) 0));

        assertThat(of.hasIntegerValue(), is(false));
        assertThat(of.getIntegerValue(), is(0));

        assertThat(of.hasLongValue(), is(false));
        assertThat(of.getLongValue(), is(0L));

        assertThat(of.hasDoubleValue(), is(false));
        assertThat(of.getDoubleValue(), is(0.0));

        assertThat(of.hasStringValue(), is(false));
        assertThat(of.getStringValue(), is(nullValue()));

        assertThat(of.hasBinaryValue(), is(false));
        assertThat(of.getBinaryValue(), is(nullValue()));

        assertThat(of.hasEnumValue(), is(false));
        assertThat(of.getEnumValue(), is(nullValue()));

        assertThat(of.hasCompactValue(), is(false));
        assertThat(of.getCompactValue(), is(nullValue()));
    }

    @Test
    public void testDefaultValues() {
        DefaultValues dv = DefaultValues.builder().build();

        assertThat(dv.hasBooleanValue(), is(false));
        assertThat(dv.isBooleanValue(), is(true));

        assertThat(dv.hasByteValue(), is(false));
        assertThat(dv.getByteValue(), is((byte) -125));

        assertThat(dv.hasShortValue(), is(false));
        assertThat(dv.getShortValue(), is((short) 13579));

        assertThat(dv.hasIntegerValue(), is(false));
        assertThat(dv.getIntegerValue(), is(1234567890));

        assertThat(dv.hasLongValue(), is(false));
        assertThat(dv.getLongValue(), is(1234567891L));

        assertThat(dv.hasDoubleValue(), is(false));
        assertThat(dv.getDoubleValue(), is(2.99792458e+8));

        assertThat(dv.hasStringValue(), is(false));
        assertThat(dv.getStringValue(), is(equalTo("test\\twith escapes\\nand\\u00a0ũñı©ôðé.")));

        assertThat(dv.hasBinaryValue(), is(false));
        assertThat(dv.getBinaryValue(), is(nullValue())); // No default on binary.

        assertThat(dv.hasEnumValue(), is(false));
        assertThat(dv.getEnumValue(), is(Value.SECOND));

        assertThat(dv.hasCompactValue(), is(false));
        assertThat(dv.getCompactValue(), is(nullValue()));

        // And it serializes as no fields.
        assertThat(debugString(dv), is(equalTo("")));
    }

    @Test
    public void testHashCode() {
        OptionalFields of = OptionalFields.builder().build();
        OptionalFields of2 = OptionalFields.builder().build();
        RequiredFields rf = RequiredFields.builder().build();
        UnionFields uf = UnionFields.withCompactValue(new CompactFields("a", 4, null));

        assertThat(of, not(sameInstance(of2)));
        assertThat(of.hashCode(), is(equalTo(of2.hashCode())));
        assertThat(of.hashCode(), not(equalTo(rf.hashCode())));
        assertThat(uf.hashCode(), not(equalTo(rf.hashCode())));
    }

    @Test
    public void testAutoIdFields() throws IOException {
        AutoIdFields af = AutoIdFields.builder()
                                      .setBooleanValue(false)
                                      .setByteValue((byte) 4)
                                      .setShortValue((short) 6)
                                      .setIntegerValue(8)
                                      .setLongValue(10L)
                                      .setDoubleValue(12.12)
                                      .setStringValue("a string")
                                      .setBinaryValue(Binary.fromHexString("0123456789abcdef"))
                                      .setCompactValue(new CompactFields("name", 14, "label"))
                                      .setEnumValue(Value.SIXTEENTH)
                                      .build();
        BinarySerializer serializer = new BinarySerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializer.serialize(baos, af);
        AutoIdFields af2 = serializer.deserialize(new ByteArrayInputStream(baos.toByteArray()), AutoIdFields.kDescriptor);

        assertThat(af2, is(equalToMessage(af)));
        // An interesting side effect from auto ID fields, is that the numeric
        // order is reverse that of declared order. Always.
        assertEquals("compactValue = {\n" +
                     "  name = \"name\"\n" +
                     "  id = 14\n" +
                     "  label = \"label\"\n" +
                     "}\n" +
                     "enumValue = SIXTEENTH\n" +
                     "binaryValue = b64(ASNFZ4mrze8)\n" +
                     "stringValue = \"a string\"\n" +
                     "doubleValue = 12.12\n" +
                     "longValue = 10\n" +
                     "integerValue = 8\n" +
                     "shortValue = 6\n" +
                     "byteValue = 4\n" +
                     "booleanValue = false", debugString(af2));
    }

    @Test
    public void testMutable() {
        OptionalFields of = OptionalFields.builder()
                                          .setCompactValue(new CompactFields("a", 4, null))
                                          .build();
        Containers a = Containers.builder()
                                 .setOptionalFields(of)
                                 .build();

        Containers._Builder b = a.mutate();

        assertThat(b.build().getOptionalFields(), is(sameInstance(of)));

        b.mutableOptionalFields().setIntegerValue(55);

        Containers c = b.build();

        // Even if the intermediate structure is mutated
        // inner contained structures are not rebuilt.
        assertThat(c.getOptionalFields().getIntegerValue(), is(55));
        assertThat(c.getOptionalFields().getCompactValue(), is(sameInstance(of.getCompactValue())));
    }

    @Test
    public void testMutableContainer() {
        Containers._Builder containers = Containers.builder()
                                          .setByteList(new ArrayList<>())
                                          .setShortSet(ImmutableSet.of(
                                                  (short) 1,
                                                  (short) -1,
                                                  (short) 2,
                                                  (short) -2))
                                          .setIntegerMap(new HashMap<>())
                                          .addToLongList(1, 2, 3, 4, 5)
                                          .addToEnumSet(Value.EIGHTEENTH, Value.THIRD)
                                          .putInDoubleMap(12, 44)
                                          .putInDoubleMap(44, 12);

        // Not set before, is created.
        assertThat(containers.mutableBinarySet(), is(notNullValue()));
        // Changes to returned collection is reflected in built instance.
        containers.mutableShortSet().remove((short) -1);
        containers.mutableShortSet().add((short) 3);

        assertThat(containers.build(), is(equalToMessage(parseDebugString(
                "byteList = []\n" +
                "longList = [1, 2, 3, 4, 5]\n" +
                "shortSet = [1, 2, -2, 3]\n" +
                "binarySet = [\n" +
                "]\n" +
                "integerMap = {\n" +
                "}\n" +
                "doubleMap = {\n" +
                "  44: 12\n" +
                "  12: 44\n" +
                "}\n" +
                "enumSet = [\n" +
                "  EIGHTEENTH,\n" +
                "  THIRD\n" +
                "]", Containers.kDescriptor))));
    }

    @Test
    public void testOther() {
        // The files containing these two classes have the same
        // program context. That used to cause one of them to
        // use the java package from the other.
        // Both of these are in a 'calculator.thrift' file.
        assertThat(OtherCalculator.class.getPackage().getName(),
                   is("net.morimekta.test.providence.testing.math"));
        assertThat(Calculator.class.getPackage().getName(),
                   is("net.morimekta.test.providence.testing.calculator"));

        // Actually just a compile time check that the "request" field in the "service.Request" struct is
        // a "service2.Request", not a "service.Request".
        Request.builder()
               .setRequest(net.morimekta.test.providence.testing.service2.Request.builder().build())
               .build();
    }

    @Test
    public void testSerializable() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        PApplicationException originalCause = new PApplicationException("test",
                                                                        PApplicationExceptionType.BAD_SEQUENCE_ID);
        ExceptionFields original = generator.generate(ExceptionFields.kDescriptor);
        original.initCause(originalCause);

        oos.writeObject(original);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);

        ExceptionFields actual = (ExceptionFields) in.readObject();

        assertThat(actual, is(equalToMessage(original)));
        assertThat(actual.getCause(), is(originalCause));
        assertThat(actual.getStackTrace(), is(original.getStackTrace()));
    }
}
