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
package net.morimekta.providence.config.impl;

import net.morimekta.providence.config.ProvidenceConfigException;
import net.morimekta.test.providence.config.Database;
import net.morimekta.util.FileWatcher;
import net.morimekta.util.Pair;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doNothing;
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

    private ProvidenceConfigParser parser;
    private FileWatcher            watcher;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        parser = mock(ProvidenceConfigParser.class);
        watcher = mock(FileWatcher.class);
    }

    @Test
    public void testSupplier() throws IOException {
        Database first = Database.builder()
                                 .setDriver("com.mysql.Driver")
                                 .build();
        Database second = Database.builder()
                                  .setDriver("org.h2.Driver")
                                  .build();

        File file = tmp.newFile().getAbsoluteFile().getCanonicalFile();

        when((Pair) parser.parseConfig(file, null)).thenReturn(Pair.create(first, ImmutableSet.of(file.toString())));
        ArgumentCaptor<FileWatcher.Watcher> watcherCapture = ArgumentCaptor.forClass(FileWatcher.Watcher.class);
        doNothing().when(watcher).weakAddWatcher(watcherCapture.capture());

        ProvidenceConfigSupplier<Database, Database._Field> supplier =
                new ProvidenceConfigSupplier<>(file, null, watcher, parser);

        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));

        verify(parser).parseConfig(file, null);
        verify(watcher).weakAddWatcher(any(FileWatcher.Watcher.class));
        verify(watcher, atLeast(1)).startWatching(any(File.class));
        verifyNoMoreInteractions(watcher, parser);

        reset(parser, watcher);
        when((Pair) parser.parseConfig(file, null)).thenReturn(Pair.create(second, ImmutableSet.of(file.toString())));
        doNothing().when(watcher).weakAddWatcher(watcherCapture.capture());

        watcherCapture.getValue().onFileUpdate(file);

        assertThat(supplier.get(), is(sameInstance(second)));
        assertThat(supplier.get(), is(sameInstance(second)));
        assertThat(supplier.get(), is(sameInstance(second)));

        verify(parser).parseConfig(file, null);
        verify(watcher).startWatching(any(File.class));
        verifyNoMoreInteractions(parser, watcher);
    }

    @Test
    public void testWatchedSupplier() throws IOException {
        Database first = Database.builder()
                                 .setDriver("com.mysql.Driver")
                                 .build();
        Database second = Database.builder()
                                  .setDriver("org.h2.Driver")
                                  .build();

        File file = tmp.newFile().getAbsoluteFile().getCanonicalFile();

        when((Pair) parser.parseConfig(file, null)).thenReturn(Pair.create(first, ImmutableSet.of(file.toString())));
        ArgumentCaptor<FileWatcher.Watcher> watcherCapture = ArgumentCaptor.forClass(FileWatcher.Watcher.class);
        doNothing().when(watcher).weakAddWatcher(watcherCapture.capture());

        ProvidenceConfigSupplier<Database, Database._Field> supplier =
                new ProvidenceConfigSupplier<>(file, null, watcher, parser);

        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));

        verify(parser).parseConfig(file, null);
        verify(watcher).weakAddWatcher(any(FileWatcher.Watcher.class));
        verify(watcher, atMost(4)).startWatching(file);
        verifyNoMoreInteractions(watcher, parser);

        reset(parser, watcher);
        when((Pair) parser.parseConfig(file, null)).thenReturn(Pair.create(second, ImmutableSet.of(file.toString())));
        doNothing().when(watcher).weakAddWatcher(watcherCapture.capture());

        watcherCapture.getValue().onFileUpdate(file);

        assertThat(supplier.get(), is(sameInstance(second)));
        assertThat(supplier.get(), is(sameInstance(second)));
        assertThat(supplier.get(), is(sameInstance(second)));

        verify(parser).parseConfig(file, null);
        verify(watcher).startWatching(file);
        verifyNoMoreInteractions(parser, watcher);
    }

    @Test
    public void testSupplierKeepInstanceOnFailedReload() throws IOException {
        Database first = Database.builder().build();

        File file = tmp.newFile().getAbsoluteFile().getCanonicalFile();

        when((Pair) parser.parseConfig(file, null)).thenReturn(Pair.create(first, ImmutableSet.of(file.toString())));
        ArgumentCaptor<FileWatcher.Watcher> watcherCapture = ArgumentCaptor.forClass(FileWatcher.Watcher.class);
        doNothing().when(watcher).weakAddWatcher(watcherCapture.capture());

        ProvidenceConfigSupplier<Database, Database._Field> supplier =
                new ProvidenceConfigSupplier<>(file, null, watcher, parser);
        assertThat(supplier.get(), is(first));
        verify(watcher).weakAddWatcher(any(FileWatcher.Watcher.class));
        reset(parser, watcher);
        when(parser.parseConfig(file, null)).thenThrow(new ProvidenceConfigException("test"));

        watcherCapture.getValue().onFileUpdate(file);

        verify(parser).parseConfig(file, null);
        verifyNoMoreInteractions(parser);

        assertThat(supplier.get(), is(first));
    }
}
