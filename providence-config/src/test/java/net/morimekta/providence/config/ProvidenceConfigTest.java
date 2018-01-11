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

import net.morimekta.providence.util.SimpleTypeRegistry;
import net.morimekta.test.providence.config.Database;
import net.morimekta.test.providence.config.RefConfig1;
import net.morimekta.test.providence.config.RefMerge;
import net.morimekta.test.providence.config.Service;
import net.morimekta.test.providence.config.Value;
import net.morimekta.util.Binary;
import net.morimekta.util.FileWatcher;

import org.awaitility.Duration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static net.morimekta.providence.util.ProvidenceHelper.debugString;
import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static net.morimekta.testing.ResourceUtils.getResourceAsString;
import static net.morimekta.testing.ResourceUtils.writeContentTo;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for the providence config parsers.
 */
public class ProvidenceConfigTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private SimpleTypeRegistry registry;
    private FileWatcher watcher;

    @Before
    public void setUp() throws IOException {
        registry = new SimpleTypeRegistry();
        registry.registerRecursively(Service.kDescriptor);
        registry.registerRecursively(Value.kDescriptor);
        registry.registerRecursively(RefMerge.kDescriptor);

        watcher = new FileWatcher();
    }

    @Test
    public void testResolveConfig_simple() throws IOException {
        copyResourceTo("/net/morimekta/providence/config/files/base_service.cfg", temp.getRoot());
        copyResourceTo("/net/morimekta/providence/config/files/prod_db.cfg", temp.getRoot());
        copyResourceTo("/net/morimekta/providence/config/files/stage_db.cfg", temp.getRoot());

        File prod = copyResourceTo("/net/morimekta/providence/config/files/prod.cfg", temp.getRoot());
        File stage = copyResourceTo("/net/morimekta/providence/config/files/stage.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, null, true);
        Service stage_service = config.getConfig(stage);
        Service prod_service = config.getConfig(prod);

        assertEquals("{\n" +
                     "  name = \"prod\"\n" +
                     "  http = {\n" +
                     "    port = 8080\n" +
                     "    context = \"/app\"\n" +
                     "    signature_keys = {\n" +
                     "      \"app1\": b64(VGVzdCBPYXV0aCBLZXkK)\n" +
                     "    }\n" +
                     "    signature_override_keys = [\n" +
                     "      \"not_really_app_1\"\n" +
                     "    ]\n" +
                     "  }\n" +
                     "  admin = {\n" +
                     "    port = 8088\n" +
                     "    oauth_token_key = b64(VGVzdCBPYXV0aCBLZXkK)\n" +
                     "  }\n" +
                     "  db = {\n" +
                     "    uri = \"jdbc:mysql:db01:1364/my_db\"\n" +
                     "    driver = \"org.mysql.Driver\"\n" +
                     "    credentials = {\n" +
                     "      username = \"dbuser\"\n" +
                     "      password = \"DbP4s5w0rD\"\n" +
                     "    }\n" +
                     "  }\n" +
                     "}",
                     debugString(prod_service));
        assertEquals("{\n" +
                     "  name = \"stage\"\n" +
                     "  http = {\n" +
                     "    port = 8080\n" +
                     "    context = \"/app\"\n" +
                     "    signature_keys = {\n" +
                     "      \"app1\": b64(VGVzdCBPYXV0aCBLZXkK)\n" +
                     "    }\n" +
                     "    signature_override_keys = [\n" +
                     "      \"not_really_app_1\"\n" +
                     "    ]\n" +
                     "  }\n" +
                     "  db = {\n" +
                     "    uri = \"jdbc:h2:localhost:mem\"\n" +
                     "    driver = \"org.h2.Driver\"\n" +
                     "    credentials = {\n" +
                     "      username = \"myuser\"\n" +
                     "      password = \"MyP4s5w0rd\"\n" +
                     "    }\n" +
                     "  }\n" +
                     "}",
                     debugString(stage_service));
    }

    @Test
    public void testResolveConfig_withParent() throws IOException {
        File f_stage_db = copyResourceTo("/net/morimekta/providence/config/files/stage_db.cfg", temp.getRoot());
        File f_stage_nocred = copyResourceTo("/net/morimekta/providence/config/files/stage_nocred.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, null, true);
        ConfigSupplier<Database,Database._Field> stage_db = config.resolveConfig(f_stage_db);
        ConfigSupplier<Database,Database._Field> stage_nocred = config.resolveConfig(f_stage_nocred, stage_db);

        assertEquals("{\n" +
                     "  uri = \"jdbc:h2:localhost:mem\"\n" +
                     "  driver = \"org.h2.Driver\"\n" +
                     "}",
                     debugString(stage_nocred.get()));
    }

    @Test
    public void testResolveConfig_withParent_badType() {
        try {
            File first = copyResourceTo("/net/morimekta/providence/config/files/base_service.cfg", temp.getRoot());
            File second = copyResourceTo("/net/morimekta/providence/config/files/stage_db.cfg", temp.getRoot());

            ProvidenceConfig config = new ProvidenceConfig(registry, null, true);
            ConfigSupplier<Service,Service._Field> firstConfig = config.resolveConfig(first);
            config.resolveConfig(second, firstConfig);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Loaded config type config.Service does not match parent config.Database"));
            assertThat(e.getFile(), is("stage_db.cfg"));
            assertThat(e.asString(),
                       is("Error in stage_db.cfg: Loaded config type config.Service does not match parent config.Database"));
        }
    }

    @Test
    public void testGetConfig() throws IOException {
        File f_stage_db = copyResourceTo("/net/morimekta/providence/config/files/stage_db.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, null, true);
        Database stage_db = config.getConfig(f_stage_db);

        assertEquals("{\n" +
                     "  uri = \"jdbc:h2:localhost:mem\"\n" +
                     "  driver = \"org.h2.Driver\"\n" +
                     "  credentials = {\n" +
                     "    username = \"myuser\"\n" +
                     "    password = \"MyP4s5w0rd\"\n" +
                     "  }\n" +
                     "}",
                     debugString(stage_db));
    }

    @Test
    public void testGetConfig_withParent() throws IOException {
        File f_stage_db = copyResourceTo("/net/morimekta/providence/config/files/stage_db.cfg", temp.getRoot());
        File f_stage_nocred = copyResourceTo("/net/morimekta/providence/config/files/stage_nocred.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, null, true);
        Database stage_db = config.getConfig(f_stage_db);
        Database stage_nocred = config.getConfig(f_stage_nocred, stage_db);

        assertEquals("{\n" +
                     "  uri = \"jdbc:h2:localhost:mem\"\n" +
                     "  driver = \"org.h2.Driver\"\n" +
                     "}",
                     debugString(stage_nocred));
    }

    @Test
    public void testGetConfig_withParent_badType() {
        try {
            File first = copyResourceTo("/net/morimekta/providence/config/files/base_service.cfg", temp.getRoot());
            File second = copyResourceTo("/net/morimekta/providence/config/files/stage_db.cfg", temp.getRoot());

            ProvidenceConfig config = new ProvidenceConfig(registry, null, true);
            Service firstConfig = config.getConfig(first);
            config.getConfig(second, firstConfig);
            fail("no exception");
        } catch (ProvidenceConfigException e) {
            assertThat(e.getMessage(), is("Loaded config type config.Service does not match parent config.Database"));
            assertThat(e.getFile(), is("stage_db.cfg"));
            assertThat(e.asString(),
                       is("Error in stage_db.cfg: Loaded config type config.Service does not match parent config.Database"));
        }
    }

    @Test
    public void testDefinesEveryType() throws IOException {
        ProvidenceConfig config = new ProvidenceConfig(registry);
        File defs = copyResourceTo("/net/morimekta/providence/config/files/all_defs.cfg", temp.getRoot());
        RefConfig1 ref = config.getConfig(defs);

        // Make sure every field is overridden.
        Assert.assertThat(ref, is(equalToMessage(
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
                          .setMsgValue(Database.builder()
                                               .setDriver("Driver")
                                               .build())
                          .build())));
    }

    @Test
    public void testReload() throws IOException {
        copyResourceTo("/net/morimekta/providence/config/files/base_service.cfg", temp.getRoot());
        File stageDb = copyResourceTo("/net/morimekta/providence/config/files/stage_db.cfg", temp.getRoot());
        File stage = copyResourceTo("/net/morimekta/providence/config/files/stage.cfg", temp.getRoot());

        FileWatcher watcher = new FileWatcher();

        ProvidenceConfig config = new ProvidenceConfig(registry, watcher, true);

        Supplier<Service> stage_service = config.resolveConfig(stage);

        assertEquals("{\n" +
                     "  name = \"stage\"\n" +
                     "  http = {\n" +
                     "    port = 8080\n" +
                     "    context = \"/app\"\n" +
                     "    signature_keys = {\n" +
                     "      \"app1\": b64(VGVzdCBPYXV0aCBLZXkK)\n" +
                     "    }\n" +
                     "    signature_override_keys = [\n" +
                     "      \"not_really_app_1\"\n" +
                     "    ]\n" +
                     "  }\n" +
                     "  db = {\n" +
                     "    uri = \"jdbc:h2:localhost:mem\"\n" +
                     "    driver = \"org.h2.Driver\"\n" +
                     "    credentials = {\n" +
                     "      username = \"myuser\"\n" +
                     "      password = \"MyP4s5w0rd\"\n" +
                     "    }\n" +
                     "  }\n" +
                     "}",
                     debugString(stage_service.get()));

        AtomicBoolean watcherCalled = new AtomicBoolean(false);
        watcher.weakAddWatcher(file -> watcherCalled.set(true));

        File tmp = temp.newFile();
        writeContentTo(getResourceAsString("/net/morimekta/providence/config/files/stage_db2.cfg"), tmp);
        Files.move(tmp.toPath(), stageDb.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);

        if (FileSystems.getDefault().newWatchService().getClass().getName().equals("sun.nio.fs.PollingWatchService")) {
            // HACK: PollingWatchService uses file modification time to trigger events.  Artificially advancing the
            // modification time seems to successfully trigger events in this case.
            stageDb.setLastModified(System.currentTimeMillis() + 120*1000);
        }

        await().atMost(Duration.TEN_SECONDS).untilTrue(watcherCalled);

        assertThat(config.resolveConfig(stage), is(sameInstance(stage_service)));
        assertThat(stage_service.get(), is(sameInstance(stage_service.get())));

        assertEquals("{\n" +
                     "  name = \"stage\"\n" +
                     "  http = {\n" +
                     "    port = 8080\n" +
                     "    context = \"/app\"\n" +
                     "    signature_keys = {\n" +
                     "      \"app1\": b64(VGVzdCBPYXV0aCBLZXkK)\n" +
                     "    }\n" +
                     "    signature_override_keys = [\n" +
                     "      \"not_really_app_1\"\n" +
                     "    ]\n" +
                     "  }\n" +
                     "  db = {\n" +
                     "    uri = \"jdbc:h2:localhost:mem\"\n" +
                     "    driver = \"org.h2.Driver\"\n" +
                     "    credentials = {\n" +
                     "      username = \"myuser\"\n" +
                     "      password = \"O7h3rP4ssw0rd\"\n" +
                     "    }\n" +
                     "  }\n" +
                     "}",
                     debugString(stage_service.get()));
    }

    @Test
    public void testName() throws ProvidenceConfigException {
        File f_stage_db = copyResourceTo("/net/morimekta/providence/config/files/stage_db.cfg", temp.getRoot());
        File f_stage_nocred = copyResourceTo("/net/morimekta/providence/config/files/stage_nocred.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, null, true);

        ConfigSupplier<Service,Service._Field> stage_db = config.resolveConfig(f_stage_db);
        ConfigSupplier<Service,Service._Field> stage_nocred = config.resolveConfig(f_stage_nocred, stage_db);

        Assert.assertThat(stage_db.getName(), is("ProvidenceConfig{stage_db.cfg}"));
        Assert.assertThat(stage_db.toString(), is("ProvidenceConfig{stage_db.cfg}"));

        Assert.assertThat(stage_nocred.getName(), is("ProvidenceConfig{stage_nocred.cfg}"));
        Assert.assertThat(stage_nocred.toString(), is("ProvidenceConfig{stage_nocred.cfg, parent=ProvidenceConfig{stage_db.cfg}}"));
    }
}
