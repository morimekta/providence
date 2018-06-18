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

import net.morimekta.providence.config.ConfigListener;
import net.morimekta.providence.config.ProvidenceConfigException;
import net.morimekta.providence.config.util.TestConfigSupplier;
import net.morimekta.providence.util.SimpleTypeRegistry;
import net.morimekta.test.providence.config.Database;
import net.morimekta.test.providence.config.Service;
import net.morimekta.testing.time.FakeClock;
import net.morimekta.util.FileWatcher;
import net.morimekta.util.Pair;

import com.google.common.collect.ImmutableSet;
import org.awaitility.Duration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static net.morimekta.testing.ResourceUtils.writeContentTo;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for the message config wrapper.
 */
public class ProvidenceConfigSupplierTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private ProvidenceConfigParser parser;
    private FileWatcher            watcher;
    private FakeClock              clock;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        clock = new FakeClock();
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

        when((Pair) parser.parseConfig(file.toPath(), null)).thenReturn(Pair.create(first, ImmutableSet.of(file.toString())));
        ArgumentCaptor<FileWatcher.Listener> watcherCapture = ArgumentCaptor.forClass(FileWatcher.Listener.class);
        doNothing().when(watcher).weakAddWatcher(eq(file.toPath()), watcherCapture.capture());

        ProvidenceConfigSupplier<Database, Database._Field> supplier =
                new ProvidenceConfigSupplier<>(file, null, watcher, parser, clock);

        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));

        verify(parser).parseConfig(file.toPath(), null);
        verify(watcher, atLeast(1)).weakAddWatcher(eq(file.toPath()), any(FileWatcher.Listener.class));
        verifyNoMoreInteractions(watcher, parser);

        reset(parser, watcher);
        when((Pair) parser.parseConfig(file.toPath(), null)).thenReturn(Pair.create(second, ImmutableSet.of(file.toString())));
        doNothing().when(watcher).weakAddWatcher(eq(file.toPath()), watcherCapture.capture());

        watcherCapture.getValue().onPathUpdate(file.toPath());

        assertThat(supplier.get(), is(sameInstance(second)));
        assertThat(supplier.get(), is(sameInstance(second)));
        assertThat(supplier.get(), is(sameInstance(second)));

        verify(parser).parseConfig(file.toPath(), null);
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

        when((Pair) parser.parseConfig(file.toPath(), null)).thenReturn(Pair.create(first, ImmutableSet.of(file.toString())));
        ArgumentCaptor<FileWatcher.Listener> watcherCapture = ArgumentCaptor.forClass(FileWatcher.Listener.class);
        doNothing().when(watcher).weakAddWatcher(eq(file.toPath()), watcherCapture.capture());

        ProvidenceConfigSupplier<Database, Database._Field> supplier =
                new ProvidenceConfigSupplier<>(file, null, watcher, parser, clock);

        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));
        assertThat(supplier.get(), is(sameInstance(first)));

        verify(parser).parseConfig(file.toPath(), null);
        verify(watcher, atMost(5)).weakAddWatcher(eq(file.toPath()), any(FileWatcher.Listener.class));
        verifyNoMoreInteractions(watcher, parser);

        reset(parser, watcher);
        when((Pair) parser.parseConfig(file.toPath(), null)).thenReturn(Pair.create(second, ImmutableSet.of(file.toString())));
        doNothing().when(watcher).weakAddWatcher(eq(file.toPath()), watcherCapture.capture());

        watcherCapture.getValue().onPathUpdate(file.toPath());

        assertThat(supplier.get(), is(sameInstance(second)));
        assertThat(supplier.get(), is(sameInstance(second)));
        assertThat(supplier.get(), is(sameInstance(second)));

        verify(parser).parseConfig(file.toPath(), null);
        verifyNoMoreInteractions(parser, watcher);
    }

    @Test
    public void testSupplierKeepInstanceOnFailedReload() throws IOException {
        Database first = Database.builder().build();

        File file = tmp.newFile().getAbsoluteFile().getCanonicalFile();

        when((Pair) parser.parseConfig(file.toPath(), null)).thenReturn(Pair.create(first, ImmutableSet.of(file.toString())));
        ArgumentCaptor<FileWatcher.Listener> watcherCapture = ArgumentCaptor.forClass(FileWatcher.Listener.class);
        doNothing().when(watcher).weakAddWatcher(eq(file.toPath()), watcherCapture.capture());

        ProvidenceConfigSupplier<Database, Database._Field> supplier =
                new ProvidenceConfigSupplier<>(file, null, watcher, parser, clock);
        assertThat(supplier.get(), is(first));
        verify(watcher).weakAddWatcher(eq(file.toPath()), any(FileWatcher.Listener.class));
        reset(parser, watcher);
        when(parser.parseConfig(file.toPath(), null)).thenThrow(new ProvidenceConfigException("test"));

        watcherCapture.getValue().onPathUpdate(file.toPath());

        verify(parser).parseConfig(file.toPath(), null);
        verifyNoMoreInteractions(parser);

        assertThat(supplier.get(), is(first));
    }

    @Test
    public void testReferencedFilesOnUpdatesFromCanonicalFiles() throws IOException {
        SimpleTypeRegistry registry = new SimpleTypeRegistry();
        registry.registerRecursively(Service.kDescriptor);
        watcher = new FileWatcher();
        parser = new ProvidenceConfigParser(registry, true);

        File service = new File(tmp.getRoot(), "service.config").getAbsoluteFile();
        File include = new File(tmp.getRoot(), "include.config").getAbsoluteFile();

        File v1 = tmp.newFolder("..dist", "v1").getAbsoluteFile();
        File service1 = new File(v1, "service.config").getAbsoluteFile();
        File include1 = new File(v1, "include.config").getAbsoluteFile();
        File v2 = tmp.newFolder("..dist", "v2").getAbsoluteFile();
        File service2 = new File(v2, "service.config").getAbsoluteFile();
        File include2 = new File(v2, "include.config").getAbsoluteFile();

        // write v1 config
        writeContentTo("config.ServicePort {\n" +
                       "  port = 8080\n" +
                       "}\n", include1);
        writeContentTo("include \"include.config\" as inc\n" +
                       "config.Service {\n" +
                       "  name = \"test\"\n" +
                       "  http = inc\n" +
                       "}\n", service1);
        // write v2 config
        writeContentTo("config.ServicePort {\n" +
                       "  port = 80\n" +
                       "}\n", include2);
        writeContentTo("include \"include.config\" as inc\n" +
                       "config.Service {\n" +
                       "  name = \"other\"\n" +
                       "  http = inc\n" +
                       "}\n", service2);

        Files.createSymbolicLink(service.toPath(), service1.toPath());
        Files.createSymbolicLink(include.toPath(), include1.toPath());

        ProvidenceConfigSupplier<Service,Service._Field> config = new ProvidenceConfigSupplier<>(service, null, watcher, parser, clock);
        Service serviceConfig = config.get();
        AtomicReference<Service> serviceRef = new AtomicReference<>(serviceConfig);

        assertThat(serviceConfig.getName(), is("test"));
        assertThat(serviceConfig.getHttp().getPort(), is((short) 8080));

        AtomicBoolean updated = new AtomicBoolean();
        ConfigListener<Service,Service._Field> listener = cf -> {
                serviceRef.set(cf);
                updated.set(true);
        };
        config.addListener(listener);

        File tmpFile = new File(tmp.getRoot(), "..tmp");
        Files.createSymbolicLink(tmpFile.toPath(), service2.toPath());
        Files.move(tmpFile.toPath(), service.toPath(), StandardCopyOption.REPLACE_EXISTING);

        waitAtMost(Duration.ONE_SECOND).untilTrue(updated);

        serviceConfig = serviceRef.get();

        assertThat(serviceConfig.getName(), is("other"));
        assertThat(serviceConfig.getHttp().getPort(), is((short) 80));
    }

    @Test
    public void testParentConfigUpdatesPropagaing() throws IOException {
        TestConfigSupplier<Database, Database._Field> parent = new TestConfigSupplier<>(Database.builder()
                                                                                                .setUri("foo")
                                                                                                .build());
        File bar = writeContentTo("config.Database { driver = \"bar\" }", tmp.newFile("bar.config"));
        SimpleTypeRegistry registry = new SimpleTypeRegistry();
        registry.registerRecursively(Database.kDescriptor);
        parser = new ProvidenceConfigParser(registry, false);
        ProvidenceConfigSupplier<Database,Database._Field> config = new ProvidenceConfigSupplier<>(bar, parent, watcher, parser, clock);

        assertThat(config.get(), is(Database.builder()
                                            .setUri("foo")
                                            .setDriver("bar")
                                            .build()));

        ConfigListener<Database,Database._Field> listener = mock(ConfigListener.class);
        config.addListener(listener);

        parent.testUpdate(Database.builder()
                                  .setUri("fish")
                                  .build());

        verify(listener).onConfigChange(Database.builder()
                                                .setUri("fish")
                                                .setDriver("bar")
                                                .build());
        reset(listener);

        config.removeListener(listener);

        parent.testUpdate(Database.builder()
                                  .build());

        verifyZeroInteractions(listener);
    }
}
