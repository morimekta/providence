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
import net.morimekta.test.providence.config.RefConfig1;
import net.morimekta.test.providence.config.RefMerge;
import net.morimekta.test.providence.config.Service;
import net.morimekta.test.providence.config.Value;
import net.morimekta.util.Binary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Supplier;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
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
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private TypeRegistry registry;

    @Before
    public void setUp() throws IOException {
        registry = new TypeRegistry();
        registry.registerRecursively(Service.kDescriptor);
        registry.registerRecursively(Value.kDescriptor);
        registry.registerRecursively(RefMerge.kDescriptor);
    }

    @Test
    public void testParseSimple() throws IOException {
        copyResourceTo("/net/morimekta/providence/config/base_service.cfg", temp.getRoot());
        copyResourceTo("/net/morimekta/providence/config/prod_db.cfg", temp.getRoot());
        copyResourceTo("/net/morimekta/providence/config/stage_db.cfg", temp.getRoot());

        File prod = copyResourceTo("/net/morimekta/providence/config/prod.cfg", temp.getRoot());
        File stage = copyResourceTo("/net/morimekta/providence/config/stage.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, true);
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
                     "  port = 8088\n" +
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

        ProvidenceConfig config = new ProvidenceConfig(registry, true);
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

        ProvidenceConfig config = new ProvidenceConfig(registry, true);
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

        ProvidenceConfig config = new ProvidenceConfig(registry, true);
        Supplier<Database> stage_db = config.getSupplier(f_stage_db, Database.kDescriptor);
        Supplier<Database> stage_nocred = config.getSupplierWithParent(f_stage_nocred, stage_db);

        assertEquals("uri = \"jdbc:h2:localhost:mem\"\n" +
                     "driver = \"org.h2.Driver\"",
                     debugString(stage_nocred.get()));
    }

    @Test
    public void testDefinesEveryType() throws IOException {
        ProvidenceConfig config = new ProvidenceConfig(registry);
        File defs = copyResourceTo("/net/morimekta/providence/config/all_defs.cfg", temp.getRoot());
        RefConfig1 ref = config.getSupplier(defs, RefConfig1.kDescriptor).get();

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
        copyResourceTo("/net/morimekta/providence/config/base_service.cfg", temp.getRoot());
        File stageDb = copyResourceTo("/net/morimekta/providence/config/stage_db.cfg", temp.getRoot());
        File stage = copyResourceTo("/net/morimekta/providence/config/stage.cfg", temp.getRoot());

        ProvidenceConfig config = new ProvidenceConfig(registry, true);

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

        assertThat(config.getSupplier(stage).get(), is(sameInstance(stage_service.get())));

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

        ProvidenceConfig config = new ProvidenceConfig(registry);
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

        ProvidenceConfig config = new ProvidenceConfig(registry, true);
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

        ProvidenceConfig config = new ProvidenceConfig(registry);

        try {
            config.getSupplier(a);
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

        ProvidenceConfig config = new ProvidenceConfig(registry);

        try {
            config.getSupplier(a);
            fail("no exception on circular deps");
        } catch (SerializerException e) {
            assertEquals("Included file \"b.cfg\" not found.", e.getMessage());
        }
    }

    @Test
    public void testInternalReference() throws IOException {
        File a = writeContentTo(
                "config.RefMerge {\n" +
                "  ref1 & first = {\n" +
                "    bool_value & boo = false\n" +
                "    msg_value & db {\n" +
                "      driver = \"Driver\"\n" +
                "    }\n" +
                "  }\n" +
                "  ref1_1 = first {\n" +
                "    i16_value = 12345\n" +
                "    msg_value & db2 = db {\n" +
                "      uri = \"someuri\"\n" +
                "    }\n" +
                "  }\n" +
                "  ref2 {\n" +
                "    bool_value = boo" +
                "    msg_value = db2\n" +
                "  }\n" +
                "}\n", temp.newFile("a.cfg"));

        try {
            ProvidenceConfig config = new ProvidenceConfig(registry);
            RefMerge merged = config.getSupplier(a, RefMerge.kDescriptor)
                                    .get();

            assertThat(debugString(merged), is(
                    "ref1 = {\n" +
                    "  bool_value = false\n" +
                    "  msg_value = {\n" +
                    "    driver = \"Driver\"\n" +
                    "  }\n" +
                    "}\n" +
                    "ref1_1 = {\n" +
                    "  bool_value = false\n" +
                    "  i16_value = 12345\n" +
                    "  msg_value = {\n" +
                    "    uri = \"someuri\"\n" +
                    "    driver = \"Driver\"\n" +
                    "  }\n" +
                    "}\n" +
                    "ref2 = {\n" +
                    "  bool_value = false\n" +
                    "  msg_value = {\n" +
                    "    uri = \"someuri\"\n" +
                    "    driver = \"Driver\"\n" +
                    "  }\n" +
                    "}"));
        } catch (TokenizerException e) {
            System.err.println(e.asString());
            throw e;
        }
    }

    @Test
    public void testInternalReferenceFails() throws IOException {
        writeContentTo("config.Database {}\n", temp.newFile("db.cfg"));

        assertReferenceFails("include \"db.cfg\" as db\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & db {\n" +
                             "  }\n" +
                             "}\n",
                             "Trying to reassign include alias 'db' to reference.");
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & first {}\n" +
                             "  ref1_1 & first {}\n" +
                             "}\n",
                             "Trying to reassign reference 'first', original at line 3");
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & def {\n" +
                             "  }\n" +
                             "}\n",
                             "Trying to assign reference id 'def', which is reserved.");
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & first {\n" +
                             "    msg_value & first {}\n" +
                             "  }\n" +
                             "}\n",
                             "Trying to reassign reference 'first' while calculating it's value, original at line 3");
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & first {\n" +
                             "    msg_value = first\n" +
                             "  }\n" +
                             "}\n",
                             "Trying to reference 'first' while it's being defined, original at line 3");
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & first {\n" +
                             "    msg_value = second\n" +
                             "  }\n" +
                             "}\n",
                             "No such reference 'second'");

        // --- with unknown / consumed values
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & first {\n" +
                             "    unk & first = {\n" +
                             "    }\n" +
                             "  }\n" +
                             "}\n",
                             "Trying to reassign reference 'first' while calculating it's value, original at line 3");
        assertReferenceFails("\n" +
                             "config.RefMerge {\n" +
                             "  ref1 & first {\n" +
                             "    unk = {\n" +
                             "      val & first = \"str\"\n" +
                             "    }\n" +
                             "  }\n" +
                             "}\n",
                             "Trying to reassign reference 'first' while calculating it's value, original at line 3");
    }

    private void assertReferenceFails(String cfg, String message) throws IOException {
        try {
            File a = writeContentTo(cfg, temp.newFile());
            ProvidenceConfig config = new ProvidenceConfig(registry);
            config.getSupplier(a, RefMerge.kDescriptor).get();
            fail("No exception on fail: " + message);
        } catch (TokenizerException e) {
            assertThat(e.getMessage(), is(message));
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

        ProvidenceConfig config = new ProvidenceConfig(registry);

        assertEquals(f1_1.getCanonicalPath(), config.resolveFile(null, temp.getRoot() + "/test/test.cfg").getAbsolutePath());
        assertEquals(f1_2.getCanonicalPath(), config.resolveFile(f1_1, "same.cfg").getAbsolutePath());
        assertEquals(f2_1.getCanonicalPath(), config.resolveFile(f1_1, "../other/other.cfg").getAbsolutePath());
        assertEquals(f2_2.getCanonicalPath(), config.resolveFile(f1_1, "../third.cfg").getAbsolutePath());

        assertFileNotResolved(f1_1, "../", "../ is a directory, expected file");
        assertFileNotResolved(f1_1, "../fourth.cfg", "Included file ../fourth.cfg not found");
        assertFileNotResolved(f1_1, "fourth.cfg", "Included file fourth.cfg not found");
        assertFileNotResolved(f1_1, "/fourth.cfg", "Absolute path includes not allowed: /fourth.cfg");
        assertFileNotResolved(f1_1, "other/fourth.cfg", "Included file other/fourth.cfg not found");
        assertFileNotResolved(f1_1, "../other", "../other is a directory, expected file");
        assertFileNotResolved(f1_1, "other", "Included file other not found");

        assertFileNotResolved(null, "../", "../ is a directory, expected file");
        assertFileNotResolved(null, "../fourth.cfg", "File ../fourth.cfg not found");
        assertFileNotResolved(null, "fourth.cfg", "File fourth.cfg not found");
        assertFileNotResolved(null, "/fourth.cfg", "File /fourth.cfg not found");
        assertFileNotResolved(null, "other/fourth.cfg", "File other/fourth.cfg not found");
        assertFileNotResolved(null, "../other", "File ../other not found");
        assertFileNotResolved(null, "other", "File other not found");
    }

    private void assertFileNotResolved(File ref, String file, String message) throws IOException {
        ProvidenceConfig config = new ProvidenceConfig(registry);

        try {
            config.resolveFile(ref, file);
            fail("no exception on unresolved file");
        } catch (FileNotFoundException e) {
            assertEquals(message, e.getMessage());
        }
    }

    @Test
    public void testParseFailure() throws IOException {
        writeContentTo("config.Database {}", temp.newFile("a.cfg"));
        writeContentTo("config.Database {}", temp.newFile("b.cfg"));

        assertParseFailure("No message in config: test.cfg",
                           "");
        assertParseFailure("Error in test.cfg on line 1, pos 11:\n" +
                           "    Invalid termination of number: '1f'\n" +
                           "def { n = 1f }\n" +
                           "-----------^",
                           "def { n = 1f }");
        assertParseFailure("Error in test.cfg on line 3, pos 0:\n" +
                           "    Defines already complete or passed.\n" +
                           "def { y = \"baa\"}\n" +
                           "^",
                           "include \"a.cfg\" as a\n" +
                           "def { n = 1 }\n" +
                           "def { y = \"baa\"}\n");
        assertParseFailure("Error in test.cfg on line 2, pos 0:\n" +
                           "    Expected the token 'as', but got 'config.Database'\n" +
                           "config.Database { driver = \"baa\"}\n" +
                           "^",
                           "include \"a.cfg\"\n" +
                           "config.Database { driver = \"baa\"}\n");
        assertParseFailure("Error in test.cfg on line 1, pos 16:\n" +
                           "    Expected token 'as' after included file \"a.cfg\".\n" +
                           "include \"a.cfg\" ass db\n" +
                           "----------------^",
                           "include \"a.cfg\" ass db\n" +
                           "config.Database { driver = \"baa\"}\n");
        assertParseFailure("Error in test.cfg on line 2, pos 0:\n" +
                           "    Expected Include alias, but got 'config.Database'\n" +
                           "config.Database { driver = \"baa\"}\n" +
                           "^",
                           "include \"a.cfg\" as\n" +
                           "config.Database { driver = \"baa\"}\n");
        assertParseFailure("Error in test.cfg on line 1, pos 19:\n" +
                           "    Alias \"def\" is a reserved word.\n" +
                           "include \"a.cfg\" as def\n" +
                           "-------------------^",
                           "include \"a.cfg\" as def\n" +
                           "config.Database { driver = \"baa\"}\n");
        assertParseFailure("Error in test.cfg on line 2, pos 19:\n" +
                           "    Alias \"a\" is already used.\n" +
                           "include \"a.cfg\" as a\n" +
                           "-------------------^",
                           "include \"a.cfg\" as a\n" +
                           "include \"a.cfg\" as a\n" +
                           "config.Database { driver = \"baa\"}\n");
        assertParseFailure("Error in test.cfg on line 1, pos 11:\n" +
                           "    Unexpected line break in literal\n" +
                           "def { s = \"\n" +
                           "-----------^",
                           "def { s = \"\n\"}");
        assertParseFailure("Error in test.cfg on line 1, pos 11:\n" +
                           "    Unescaped non-printable char in literal: '\\t'\n" +
                           "def { s = \"\t\"}\n" +
                           "-----------^",
                           "def { s = \"\t\"}");
        assertParseFailure("Error in test.cfg on line 1, pos 11:\n" +
                           "    Unexpected end of stream in literal\n" +
                           "def { s = \"a\n" +
                           "-----------^",
                           "def { s = \"a");
        assertParseFailure("Error in test.cfg on line 1, pos 6:\n" +
                           "    Reference name '1' is not valid.\n" +
                           "def { 1 = \"boo\" }\n" +
                           "------^",
                           "def { 1 = \"boo\" }");
        assertParseFailure("Error in test.cfg on line 1, pos 0:\n" +
                           "    Unexpected token '44'. Expected include, defines or message type\n" +
                           "44\n" +
                           "^",
                           "44");
        assertParseFailure("Error in test.cfg on line 1, pos 0:\n" +
                           "    Unexpected token 'boo'. Expected include, defines or message type\n" +
                           "boo {\n" +
                           "^",
                           "boo {\n" +
                           "}\n");

        // Parsing that only fails in strict mode.
        assertParseFailure("Error in test.cfg on line 1, pos 10:\n" +
                           "    Unknown enum identifier: boo.En\n" +
                           "def { s = boo.En.VAL }\n" +
                           "----------^",
                           "def { s = boo.En.VAL }", true);
    }


    private void assertParseFailure(String message,
                                    String pretty) throws IOException {
        assertParseFailure(message, pretty, false);
    }

    private void assertParseFailure(String message,
                                    String pretty,
                                    boolean strict) throws IOException {
        File a = temp.newFile("test.cfg");
        writeContentTo(pretty, a);

        ProvidenceConfig config = new ProvidenceConfig(registry, strict);

        try {
            config.getSupplier(a);
            fail("no exception");
        } catch (ConfigException e) {
            assertThat(e.getMessage(), is(message));
        } catch (SerializerException e) {
            assertThat(e.asString().replaceAll("\\r", ""), is(message));
        }
        a.delete();
    }
}
