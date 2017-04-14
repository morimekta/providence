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
package net.morimekta.providence.config;

import net.morimekta.config.ConfigException;
import net.morimekta.providence.testing.util.MessageGenerator;
import net.morimekta.test.providence.config.Credentials;
import net.morimekta.test.providence.config.Database;
import net.morimekta.test.providence.config.RefConfig1;
import net.morimekta.test.providence.config.Value;
import net.morimekta.util.Binary;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for the message config wrapper.
 */
public class OverrideMessageSupplierTest {
    private AtomicReference<Database> base = new AtomicReference<>();

    @Rule
    public MessageGenerator generator = new MessageGenerator();

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        base.set(Database.builder()
                         .setUri("http://hostname:9057/path")
                         .setDriver("driver")
                         .setCredentials(new Credentials("username", "complicated password that no one guesses"))
                         .build());
    }

    @Test
    public void testSupplier() throws IOException {
        OverrideMessageSupplier<Database, Database._Field> supplier = new OverrideMessageSupplier<>(
                base::get, ImmutableMap.of(
                        "credentials.password", "password",
                        "uri", "undefined"),
                true);

        Database instance = supplier.get();

        assertThat(supplier.get(), is(sameInstance(instance)));
        assertThat(instance.getUri(), is(nullValue()));  // it was reset.
        assertThat(instance.getDriver(), is("driver"));
        assertThat(instance.getCredentials(), is(not(nullValue())));
        assertThat(instance.getCredentials().getUsername(), is("username"));
        assertThat(instance.getCredentials().getPassword(), is("password"));  // it was updated.

        base.set(Database.builder()
                         .setUri("http://hostname:9057/path")
                         .setDriver("otherDriver")
                         .setCredentials(new Credentials("username", "complicated password that no one guesses"))
                         .build());
        // updating the base does not update the result.
        assertThat(supplier.get(), is(sameInstance(instance)));

        supplier.reload();

        instance = supplier.get();

        assertThat(supplier.get(), is(sameInstance(instance)));
        assertThat(instance.getUri(), is(nullValue()));  // it was reset.
        assertThat(instance.getDriver(), is("otherDriver"));
        assertThat(instance.getCredentials(), is(not(nullValue())));
        assertThat(instance.getCredentials().getUsername(), is("username"));
        assertThat(instance.getCredentials().getPassword(), is("password"));  // it was updated.
    }

    @Test
    public void testFaliures() throws IOException {
        try {
            new OverrideMessageSupplier<>(
                    base::get, ImmutableMap.of(
                            "pass.password", "password",
                            "uri", "undefined"),
                    true);
            fail("No exception when strict");
        } catch (ConfigException e) {
            assertThat(e.getMessage(), is("No such field pass in config.Database [pass.password]"));
        }

        try {
            new OverrideMessageSupplier<>(
                    base::get, ImmutableMap.of(
                    "credentials.pass", "password",
                    "uri", "undefined"),
                    true);
            fail("No exception when strict");
        } catch (ConfigException e) {
            assertThat(e.getMessage(), is("No such field pass in config.Credentials [credentials.pass]"));
        }
    }

    @Test
    public void testFaliures_noStrict() throws IOException {
        // no exception. See testFailures()
        new OverrideMessageSupplier<>(
                base::get, ImmutableMap.of(
                "pass.password", "password",
                "uri", "undefined"));
        new OverrideMessageSupplier<>(
                base::get, ImmutableMap.of(
                "credentials.pass", "password",
                "uri", "undefined"));

        try {
            new OverrideMessageSupplier<>(
                    base::get, ImmutableMap.of(
                    "credentials.password", "\"password\" and some"));
            fail("No exception");
        } catch (ConfigException e) {
            assertThat(e.getMessage(),
                       is("Garbage after string value [credentials.password]: '\"password\" and some'"));
        }

        try {
            Properties properties = new Properties();
            properties.setProperty("value", "FIRST SECOND");
            new OverrideMessageSupplier<>(base::get, properties);
            fail("No exception");
        } catch (ConfigException e) {
            assertThat(e.getMessage(),
                       is("Garbage after enum value [value]: 'FIRST SECOND'"));
        }
    }

    @Test
    public void testOverrideEveryType() throws IOException {
        AtomicReference<RefConfig1> ref = new AtomicReference<>();
        ref.set(generator.generate(RefConfig1.kDescriptor)
                         .mutate()
                         .clearMsgValue()
                         .clearMapValue()
                         .build());

        Supplier<RefConfig1> supplier = new OverrideMessageSupplier<>(
                ref::get,
                ImmutableMap.<String,String>builder()
                            .put("bool_value", "true")
                            .put("byte_value", "123")
                            .put("i16_value", "12345")
                            .put("i32_value", "1234567890")
                            .put("i64_value", "12345678901234567")
                            .put("double_value", "1234567.1234567")
                            .put("enum_value", "SECOND")
                            .put("bin_value", "hex(01020304)")
                            .put("str_value", "\"This is a string\"")
                            .put("list_value", "[\"first\",\"second\"]")
                            .put("set_value", "[1234, 4321]")
                            .put("simple_map", "{12345678:SECOND}")
                            .build(),
                true);

        // Make sure every field is overridden.
        assertThat(supplier.get(), is(equalToMessage(
                RefConfig1.builder()
                          .setBoolValue(true)
                          .setByteValue((byte) 123)
                          .setI16Value((short) 12345)
                          .setI32Value(1234567890)
                          .setI64Value(12345678901234567L)
                          .setDoubleValue(1234567.1234567)
                          .setEnumValue(Value.SECOND)
                          .setBinValue(Binary.fromHexString("01020304"))
                          .setStrValue("This is a string")
                          .setListValue(ImmutableList.of("first", "second"))
                          .setSetValue(ImmutableSet.of((short) 1234, (short) 4321))
                          .setSimpleMap(ImmutableMap.of(12345678, Value.SECOND))
                          .build())));
    }
}
