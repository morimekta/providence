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

import net.morimekta.providence.mio.MessageReader;
import net.morimekta.test.config.Database;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.UncheckedIOException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for the message config wrapper.
 */
public class MessageSupplierTest {
    MessageReader reader;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        reader = mock(MessageReader.class);
    }

    @Test
    public void testSupplier() throws IOException {
        Database first = Database.builder().build();
        Database second = Database.builder().build();

        when(reader.read(Database.kDescriptor)).thenReturn(first);

        MessageSupplier<Database, Database._Field> supplier = new MessageSupplier<>(Database.kDescriptor, reader);

        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));

        verify(reader).read(Database.kDescriptor);
        verify(reader).close();
        verifyNoMoreInteractions(reader);

        reset(reader);
        when(reader.read(Database.kDescriptor)).thenReturn(second);

        supplier.reload();

        assertThat(supplier.get(), is(sameInstance(second)));
        assertThat(supplier.get(), is(sameInstance(second)));
        assertThat(supplier.get(), is(sameInstance(second)));

        verify(reader).read(Database.kDescriptor);
        verify(reader).close();
        verifyNoMoreInteractions(reader);
    }

    @Test
    public void testSupplierKeepInstanceOnFailedReload() throws IOException {
        Database first = Database.builder().build();

        when(reader.read(Database.kDescriptor)).thenReturn(first);

        MessageSupplier<Database, Database._Field> supplier = new MessageSupplier<>(Database.kDescriptor, reader);

        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));

        verify(reader).read(Database.kDescriptor);
        verify(reader).close();
        verifyNoMoreInteractions(reader);

        reset(reader);
        when(reader.read(Database.kDescriptor)).thenThrow(new IOException("Message"));

        try {
            supplier.reload();
            fail("No exception");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("java.io.IOException: Message"));
            assertThat(e, is(instanceOf(UncheckedIOException.class)));
        }

        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));

        verify(reader).read(Database.kDescriptor);
        verify(reader).close();
        verifyNoMoreInteractions(reader);
    }
}
