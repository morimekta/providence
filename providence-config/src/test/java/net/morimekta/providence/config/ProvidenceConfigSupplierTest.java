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

import net.morimekta.test.providence.config.Database;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for the message config wrapper.
 */
public class ProvidenceConfigSupplierTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private ProvidenceConfig config;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        config = mock(ProvidenceConfig.class);
    }

    @Test
    public void testSupplier() throws IOException {
        Database first = Database.builder().build();
        Database second = Database.builder().build();

        File file = tmp.newFile();

        AtomicReference<Database> reference = new AtomicReference<>(first);

        when((Supplier) config.getSupplier(file)).thenReturn(reference::get);

        ProvidenceConfigSupplier<Database, Database._Field> supplier =
                new ProvidenceConfigSupplier<>(file, config);

        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));

        verify(config).getSupplier(file);
        verifyNoMoreInteractions(config);

        reset(config);
        doAnswer((mock) -> {
            reference.set(second);
            return null;
        }).when(config).reload(file);

        supplier.reload();

        assertThat(supplier.get(), is(sameInstance(second)));
        assertThat(supplier.get(), is(sameInstance(second)));
        assertThat(supplier.get(), is(sameInstance(second)));

        verify(config).reload(file);
        verifyNoMoreInteractions(config);
    }

    @Test
    public void testSupplierKeepInstanceOnFailedReload() throws IOException {
        Database first = Database.builder().build();

        File file = tmp.newFile();

        AtomicReference<Database> reference = new AtomicReference<>(first);

        when((Supplier) config.getSupplier(file)).thenReturn(reference::get);

        ProvidenceConfigSupplier<Database, Database._Field> supplier =
                new ProvidenceConfigSupplier<>(file, config);

        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));

        verify(config).getSupplier(file);
        verifyNoMoreInteractions(config);

        reset(config);
        doThrow(new IOException("Message")).when(config).reload(file);

        try {
            supplier.reload();
            fail("No exception");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Message"));
            assertThat(e, is(instanceOf(UncheckedIOException.class)));
        }

        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));

        verify(config).reload(file);
        verifyNoMoreInteractions(config);
    }
}
