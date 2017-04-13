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
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.util.TypeRegistry;
import net.morimekta.providence.util.pretty.TokenizerException;
import net.morimekta.test.providence.config.Database;
import net.morimekta.test.providence.config.Service;
import net.morimekta.test.providence.config.Value;

import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import static net.morimekta.providence.util.PrettyPrinter.debugString;
import static net.morimekta.testing.ResourceUtils.copyResourceTo;
import static net.morimekta.testing.ResourceUtils.getResourceAsString;
import static net.morimekta.testing.ResourceUtils.writeContentTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for the providence config parser.
 */
public class ProvidenceConfigTest {
    private TemporaryFolder temp;
    private TypeRegistry registry;

    @Before
    public void setUp() throws IOException {
        temp = new TemporaryFolder();
        temp.create();

        registry = new TypeRegistry();
        registry.registerRecursively(Service.kDescriptor);
        registry.registerRecursively(Value.kDescriptor);
    }

    @After
    public void tearDown() {
        temp.delete();
    }

    @Test
    public void testParseSimple() throws IOException {
        copyResourceTo("/net/morimekta/providence/config/base_service.cfg", temp.getRoot());
        copyResourceTo("/net/morimekta/providence/config/prod_db.cfg", temp.getRoot());
        copyResourceTo("/net/morimekta/providence/config/stage_db.cfg", temp.getRoot());

        File prod = copyResourceTo("/net/morimekta/providence/config/prod.cfg", temp.getRoot());
        File stage = copyResourceTo("/net/morimekta/providence/config/stage.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, ImmutableMap.of(
                "admin_port", "14256"
        ), true);
        Service stage_service = config.getConfig(stage, Service.kDescriptor);
        Service prod_service = config.getConfig(prod);

        assertEquals("name = \"prod\"\n" +
                     "http = {\n" +
                     "  port = 8080\n" +
                     "  context = \"/app\"\n" +
                     "  signature_keys = {\n" +
                     "    \"app1\": b64(VGVzdCBPYXV0aCBLZXkK)\n" +
                     "  }\n" +
                     "  signature_override_keys = [\n" +
                     "    \"not_really_app_1\"\n" +
                     "  ]\n" +
                     "}\n" +
                     "admin = {\n" +
                     "  port = 14256\n" +
                     "  oauth_token_key = b64(VGVzdCBPYXV0aCBLZXkK)\n" +
                     "}\n" +
                     "db = {\n" +
                     "  uri = \"jdbc:mysql:db01:1364/my_db\"\n" +
                     "  driver = \"org.mysql.Driver\"\n" +
                     "  credentials = {\n" +
                     "    username = \"dbuser\"\n" +
                     "    password = \"DbP4s5w0rD\"\n" +
                     "  }\n" +
                     "}",
                     debugString(prod_service));
        assertEquals("name = \"stage\"\n" +
                     "http = {\n" +
                     "  port = 8080\n" +
                     "  context = \"/app\"\n" +
                     "  signature_keys = {\n" +
                     "    \"app1\": b64(VGVzdCBPYXV0aCBLZXkK)\n" +
                     "  }\n" +
                     "  signature_override_keys = [\n" +
                     "    \"not_really_app_1\"\n" +
                     "  ]\n" +
                     "}\n" +
                     "db = {\n" +
                     "  uri = \"jdbc:h2:localhost:mem\"\n" +
                     "  driver = \"org.h2.Driver\"\n" +
                     "  credentials = {\n" +
                     "    username = \"myuser\"\n" +
                     "    password = \"MyP4s5w0rd\"\n" +
                     "  }\n" +
                     "}",
                     debugString(stage_service));
    }

    @Test
    public void testParse_withParent() throws IOException {
        File f_stage_db = copyResourceTo("/net/morimekta/providence/config/stage_db.cfg", temp.getRoot());
        File f_stage_nocred = copyResourceTo("/net/morimekta/providence/config/stage_nocred.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, ImmutableMap.of(), true);
        config.getConfig(f_stage_db);
        Supplier<Database> stage_nocred = config.getSupplierWithParent(f_stage_nocred, f_stage_db);

        assertEquals("uri = \"jdbc:h2:localhost:mem\"\n" +
                     "driver = \"org.h2.Driver\"",
                     debugString(stage_nocred.get()));
    }

    @Test
    public void testParse_withParent_descriptor() throws IOException {
        File f_stage_db = copyResourceTo("/net/morimekta/providence/config/stage_db.cfg", temp.getRoot());
        File f_stage_nocred = copyResourceTo("/net/morimekta/providence/config/stage_nocred.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, ImmutableMap.of(), true);
        config.getConfig(f_stage_db);
        Supplier<Database> stage_nocred = config.getSupplierWithParent(f_stage_nocred, f_stage_db, Database.kDescriptor);

        assertEquals("uri = \"jdbc:h2:localhost:mem\"\n" +
                     "driver = \"org.h2.Driver\"",
                     debugString(stage_nocred.get()));
    }

    @Test
    public void testParse_withParent_supplier() throws IOException {
        File f_stage_db = copyResourceTo("/net/morimekta/providence/config/stage_db.cfg", temp.getRoot());
        File f_stage_nocred = copyResourceTo("/net/morimekta/providence/config/stage_nocred.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, ImmutableMap.of(), true);
        Supplier<Database> stage_db = config.getSupplier(f_stage_db, Database.kDescriptor);
        Supplier<Database> stage_nocred = config.getSupplierWithParent(f_stage_nocred, stage_db);

        assertEquals("uri = \"jdbc:h2:localhost:mem\"\n" +
                     "driver = \"org.h2.Driver\"",
                     debugString(stage_nocred.get()));
    }


    @Test
    public void testReload() throws IOException {
        copyResourceTo("/net/morimekta/providence/config/base_service.cfg", temp.getRoot());
        File stageDb = copyResourceTo("/net/morimekta/providence/config/stage_db.cfg", temp.getRoot());
        File stage = copyResourceTo("/net/morimekta/providence/config/stage.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, ImmutableMap.of(), true);

        Supplier<Service> stage_service = config.getSupplier(stage);

        assertEquals("name = \"stage\"\n" +
                     "http = {\n" +
                     "  port = 8080\n" +
                     "  context = \"/app\"\n" +
                     "  signature_keys = {\n" +
                     "    \"app1\": b64(VGVzdCBPYXV0aCBLZXkK)\n" +
                     "  }\n" +
                     "  signature_override_keys = [\n" +
                     "    \"not_really_app_1\"\n" +
                     "  ]\n" +
                     "}\n" +
                     "db = {\n" +
                     "  uri = \"jdbc:h2:localhost:mem\"\n" +
                     "  driver = \"org.h2.Driver\"\n" +
                     "  credentials = {\n" +
                     "    username = \"myuser\"\n" +
                     "    password = \"MyP4s5w0rd\"\n" +
                     "  }\n" +
                     "}",
                     debugString(stage_service.get()));

        stageDb.delete();
        writeContentTo(getResourceAsString("/net/morimekta/providence/config/stage_db2.cfg"), stageDb);

        assertThat((Service) (Object) config.getSupplier(stage).get(), is(sameInstance(stage_service.get())));

        config.reload(stageDb);

        stage_service = config.getSupplier(stage);

        assertEquals("name = \"stage\"\n" +
                     "http = {\n" +
                     "  port = 8080\n" +
                     "  context = \"/app\"\n" +
                     "  signature_keys = {\n" +
                     "    \"app1\": b64(VGVzdCBPYXV0aCBLZXkK)\n" +
                     "  }\n" +
                     "  signature_override_keys = [\n" +
                     "    \"not_really_app_1\"\n" +
                     "  ]\n" +
                     "}\n" +
                     "db = {\n" +
                     "  uri = \"jdbc:h2:localhost:mem\"\n" +
                     "  driver = \"org.h2.Driver\"\n" +
                     "  credentials = {\n" +
                     "    username = \"myuser\"\n" +
                     "    password = \"O7h3rP4ssw0rd\"\n" +
                     "  }\n" +
                     "}",
                     debugString(stage_service.get()));

    }

    @Test
    public void testParseWithUnknown() throws IOException {
        copyResourceTo("/net/morimekta/providence/config/unknown.cfg", temp.getRoot());
        File file = copyResourceTo("/net/morimekta/providence/config/unknown_include.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, ImmutableMap.of());
        Supplier<Database> cfg = config.getSupplier(file);

        // all the unknowns are skipped.
        assertEquals("uri = \"jdbc:h2:localhost:mem\"\n" +
                     "driver = \"org.h2.Driver\"",
                     debugString(cfg.get()));

        file = copyResourceTo("/net/morimekta/providence/config/unknown_field.cfg", temp.getRoot());
        cfg = config.getSupplier(file);
        assertEquals("uri = \"jdbc:h2:localhost:mem\"\n" +
                     "driver = \"org.h2.Driver\"",
                     debugString(cfg.get()));

        file = copyResourceTo("/net/morimekta/providence/config/unknown_enum_value.cfg", temp.getRoot());
        cfg = config.getSupplier(file);
        assertEquals("uri = \"jdbc:h2:localhost:mem\"\n" +
                     "driver = \"org.h2.Driver\"",
                     debugString(cfg.get()));
    }

    @Test
    public void testParseWithUnknown_strict() throws IOException {
        copyResourceTo("/net/morimekta/providence/config/unknown.cfg", temp.getRoot());
        File file = copyResourceTo("/net/morimekta/providence/config/unknown_include.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, ImmutableMap.of(), true);
        try {
            config.getSupplier(file);
            fail("no exception");
        } catch (TokenizerException e) {
            assertEquals("Unknown declared type: unknown.OtherConfig", e.getMessage());
        }

        file = copyResourceTo("/net/morimekta/providence/config/unknown_field.cfg", temp.getRoot());
        try {
            config.getSupplier(file);
            fail("no exception");
        } catch (TokenizerException e) {
            assertEquals("No such field unknown_field in config.Database", e.getMessage());
        }

        file = copyResourceTo("/net/morimekta/providence/config/unknown_enum_value.cfg", temp.getRoot());
        try {
            config.getSupplier(file);
            fail("no exception");
        } catch (TokenizerException e) {
            assertEquals("No such enum value LAST for config.Value.", e.getMessage());
        }
    }

    @Test
    public void testParams() throws IOException {
        File a = temp.newFile("a.cfg");
        File b = temp.newFile("b.cfg");

        writeContentTo("params {\n" +
                       "  a_number = 4321\n" +
                       "  a_real = 43.21\n" +
                       "  a_text = \"string\"\n" +
                       "  a_enum = config.Value.SECOND\n" +
                       "  a_bin = b64(dGVzdAo=)\n" +
                       "}\n" + "config.Database {}\n", a);
        writeContentTo("include \"a.cfg\" as a\n" +
                       "\n" +
                       "params {\n" +
                       "  b_number = 4321\n" +
                       "  b_real = 43.21\n" +
                       "  b_text = \"string\"\n" +
                       "  b_enum = config.Value.SECOND\n" +
                       "  b_bin = hex(0123456789abcdef)\n" +
                       "}\n" +
                       "config.Database {}\n", b);

        ProvidenceConfig config = new ProvidenceConfig(registry, new HashMap<>());

        List<ProvidenceConfigParam> params = config.params(b);

        StringBuilder builder = new StringBuilder();
        params.stream()
              .map(ProvidenceConfigParam::toString)
              .sorted()
              .forEachOrdered(s -> {
                  builder.append(s);
                  builder.append('\n');
              });

        assertEquals(
                "a_bin = b64(dGVzdAo) (a.cfg)\n" +
                "a_enum = config.Value.SECOND (a.cfg)\n" +
                "a_number = 4321 (a.cfg)\n" +
                "a_real = 43.21 (a.cfg)\n" +
                "a_text = \"string\" (a.cfg)\n" +
                "b_bin = b64(ASNFZ4mrze8) (b.cfg)\n" +
                "b_enum = config.Value.SECOND (b.cfg)\n" +
                "b_number = 4321 (b.cfg)\n" +
                "b_real = 43.21 (b.cfg)\n" +
                "b_text = \"string\" (b.cfg)\n" +
                "",
                builder.toString());
    }

    @Test
    public void testCircularIncludes() throws IOException {
        File a = temp.newFile("a.cfg");
        File b = temp.newFile("b.cfg");
        File c = temp.newFile("c.cfg");

        writeContentTo("include \"b.cfg\" as a\n" +
                       "config.Database {}\n", a);
        writeContentTo("include \"c.cfg\" as a\n" +
                       "config.Database {}\n", b);
        writeContentTo("include \"a.cfg\" as a\n" +
                       "config.Database {}\n", c);

        ProvidenceConfig config = new ProvidenceConfig(registry, new HashMap<>());

        try {
            config.getSupplier(a);
            fail("no exception on circular deps");
        } catch (SerializerException e) {
            assertEquals("Circular includes detected: a.cfg -> b.cfg -> c.cfg -> a.cfg", e.getMessage());
        }

        try {
            config.params(a);
            fail("no exception on circular deps");
        } catch (SerializerException e) {
            assertEquals("Circular includes detected: a.cfg -> b.cfg -> c.cfg -> a.cfg", e.getMessage());
        }
    }

    @Test
    public void testIncludeNoSuchFile() throws IOException {
        File a = temp.newFile("a.cfg");
        writeContentTo("include \"b.cfg\" as a\n" +
                       "config.Database {}\n", a);

        ProvidenceConfig config = new ProvidenceConfig(registry, new HashMap<>());

        try {
            config.getSupplier(a);
            fail("no exception on circular deps");
        } catch (SerializerException e) {
            assertEquals("Included file \"b.cfg\" not found", e.getMessage());
        }
    }

    @Test
    public void testResolveFile() throws IOException {
        File test = temp.newFolder("test");
        File other = temp.newFolder("other");

        File f1_1 = new File(test, "test.cfg");
        File f1_2 = new File(test, "same.cfg");
        File f2_1 = new File(other, "other.cfg");
        File f2_2 = temp.newFile("third.cfg");

        writeContentTo("a", f1_1);
        writeContentTo("a", f1_2);
        writeContentTo("a", f2_1);

        ProvidenceConfig config = new ProvidenceConfig(registry,
                                                       ImmutableMap.of());

        assertEquals(f1_1.getCanonicalPath(), config.resolveFile(null, temp.getRoot() + "/test/test.cfg").getAbsolutePath());
        assertEquals(f1_2.getCanonicalPath(), config.resolveFile(f1_1, "same.cfg").getAbsolutePath());
        assertEquals(f2_1.getCanonicalPath(), config.resolveFile(f1_1, "../other/other.cfg").getAbsolutePath());
        assertEquals(f2_2.getCanonicalPath(), config.resolveFile(f1_1, "../third.cfg").getAbsolutePath());

        assertFileNotResolved(f1_1, "../fourth.cfg", "Included file ../fourth.cfg not found");
        assertFileNotResolved(f1_1, "fourth.cfg", "Included file fourth.cfg not found");
        assertFileNotResolved(f1_1, "/fourth.cfg", "Absolute path includes not allowed: /fourth.cfg");
        assertFileNotResolved(f1_1, "other/fourth.cfg", "Included file other/fourth.cfg not found");
        assertFileNotResolved(f1_1, "../other", "../other is a directory, expected file");
        assertFileNotResolved(f1_1, "other", "Included file other not found");

        assertFileNotResolved(null, "../fourth.cfg", "File ../fourth.cfg not found");
        assertFileNotResolved(null, "fourth.cfg", "File fourth.cfg not found");
        assertFileNotResolved(null, "/fourth.cfg", "File /fourth.cfg not found");
        assertFileNotResolved(null, "other/fourth.cfg", "File other/fourth.cfg not found");
        assertFileNotResolved(null, "../other", "File ../other not found");
        assertFileNotResolved(null, "other", "File other not found");
    }

    private void assertFileNotResolved(File ref, String file, String message) throws IOException {
        ProvidenceConfig config = new ProvidenceConfig(registry, ImmutableMap.of());

        try {
            config.resolveFile(ref, file);
            fail("no exception on unresolved file");
        } catch (FileNotFoundException e) {
            assertEquals(message, e.getMessage());
        }
    }

    @Test
    public void testParseFailure() throws IOException {
        assertParseFailure("empty file",
                           "No message in config: test.cfg",
                           "");
        assertParseFailure("bad params number",
                           "Error in test.cfg on line 1, pos 14:\n" +
                           "    Invalid termination of number: '1f'\n" +
                           "params { n = 1f }\n" +
                           "--------------^",
                           "params { n = 1f }");
        assertParseFailure("newline in string",
                           "Error in test.cfg on line 1, pos 14:\n" +
                           "    Unexpected line break in literal\n" +
                           "params { s = \"\n" +
                           "--------------^",
                           "params { s = \"\n\"}");
        assertParseFailure("newline in string",
                           "Error in test.cfg on line 1, pos 14:\n" +
                           "    Unescaped non-printable char in literal: '\\t'\n" +
                           "params { s = \"\t\"}\n" +
                           "--------------^",
                           "params { s = \"\t\"}");
        assertParseFailure("unterminated string",
                           "Error in test.cfg on line 1, pos 14:\n" +
                           "    Unexpected end of stream in literal\n" +
                           "params { s = \"a\n" +
                           "--------------^",
                           "params { s = \"a");

        // Parsing that only fails in strict mode.
        assertParseFailure("unknown identifier",
                           "Error in test.cfg on line 1, pos 13:\n" +
                           "    Unknown enum identifier: boo.En\n" +
                           "params { s = boo.En.VAL }\n" +
                           "-------------^",
                           "params { s = boo.En.VAL }", true);
    }


    private void assertParseFailure(String reason,
                                    String message,
                                    String pretty) throws IOException {
        assertParseFailure(reason, message, pretty, false);
    }

    private void assertParseFailure(String reason,
                                    String message,
                                    String pretty,
                                    boolean strict) throws IOException {
        File a = temp.newFile("test.cfg");
        writeContentTo(pretty, a);

        ProvidenceConfig config = new ProvidenceConfig(registry, ImmutableMap.of(), strict);

        try {
            config.getSupplier(a);
            fail("no exception on " + reason);
        } catch (ConfigException e) {
            assertEquals("Wrong exception message on " + reason, message, e.getMessage());
        } catch (SerializerException e) {
            assertEquals("Wrong exception message on " + reason, message, e.asString().replaceAll("\\r", ""));
        }
        a.delete();
    }
}
