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

import net.morimekta.test.providence.config.Credentials;
import net.morimekta.test.providence.config.Database;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * Tests for the message config wrapper.
 */
public class OverrideMessageSupplierTest {

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
    }

    @Test
    public void testSupplier() throws IOException {
        Database first = Database.builder()
                                 .setUri("http://hostname:9057/path")
                                 .setDriver("driver")
                                 .setCredentials(new Credentials("username", "complicated password that no one guesses"))
                                 .build();

        OverrideMessageSupplier<Database, Database._Field> supplier = new OverrideMessageSupplier<>(
                () -> first, ImmutableMap.of(
                        "credentials.password", "password",
                        "uri", "undefined"
                ),
                true);

        Database instance = supplier.get();

        assertThat(supplier.get(), is(sameInstance(instance)));
        assertThat(instance.getUri(), is(nullValue()));  // it was reset.
        assertThat(instance.getDriver(), is("driver"));
        assertThat(instance.getCredentials(), is(not(nullValue())));
        assertThat(instance.getCredentials().getUsername(), is("username"));
        assertThat(instance.getCredentials().getPassword(), is("password"));  // it was updated.
    }
}
