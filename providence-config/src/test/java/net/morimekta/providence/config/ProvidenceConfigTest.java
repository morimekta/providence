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
import net.morimekta.test.config.Service;
import net.morimekta.test.config.Value;
import net.morimekta.util.io.IOUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    public void testParseSimple() throws IOException, SerializerException {
        writeConfig("/net/morimekta/providence/config/base_service.cfg");
        writeConfig("/net/morimekta/providence/config/prod_db.cfg");
        writeConfig("/net/morimekta/providence/config/stage_db.cfg");

        File prod = writeConfig("/net/morimekta/providence/config/prod.cfg");
        File stage = writeConfig("/net/morimekta/providence/config/stage.cfg");

        Map<String,String> params = new HashMap<>();
        params.put("admin_port", "14256");

        ProvidenceConfig config = new ProvidenceConfig(registry, params);
        Service stage_service = config.load(stage);
        Service prod_service = config.load(prod);

        assertEquals("stage", stage_service.getName());
        assertEquals("prod", prod_service.getName());

        assertNotNull(prod_service.getAdmin());
        assertNull(stage_service.getAdmin());

        assertEquals((short) 8080, prod_service.getHttp().getPort());
        assertEquals((short) 14256, prod_service.getAdmin().getPort());
        assertEquals((short) 8080, stage_service.getHttp().getPort());
    }

    @Test
    public void testParams() throws IOException, SerializerException {
        File a = temp.newFile("a.cfg");
        File b = temp.newFile("b.cfg");

        writeConfig(a,
                    "params {\n" +
                    "  a_number = 4321\n" +
                    "  a_real = 43.21\n" +
                    "  a_text = \"string\"\n" +
                    "  a_enum = config.Value.SECOND\n" +
                    "  a_bin = b64(dGVzdAo=)\n" +
                    "}\n" + "config.Database {}\n");
        writeConfig(b,
                    "include \"a.cfg\" as a\n" +
                    "\n" +
                    "params {\n" +
                    "  b_number = 4321\n" +
                    "  b_real = 43.21\n" +
                    "  b_text = \"string\"\n" +
                    "  b_enum = config.Value.SECOND\n" +
                    "  b_bin = hex(0123456789abcdef)\n" +
                    "}\n" +
                    "config.Database {}\n");

        ProvidenceConfig config = new ProvidenceConfig(registry, new HashMap<>());

        List<ProvidenceConfig.Param> params = config.params(b);

        StringBuilder builder = new StringBuilder();
        params.stream()
              .map(ProvidenceConfig.Param::toString)
              .sorted()
              .forEachOrdered(s -> {
                  builder.append(s);
                  builder.append('\n');
              });

        assertEquals(
                "a_bin = [dGVzdAo] (a.cfg)\n" +
                "a_enum = config.Value.SECOND (a.cfg)\n" +
                "a_number = 4321 (a.cfg)\n" +
                "a_real = 43.21 (a.cfg)\n" +
                "a_text = \"string\" (a.cfg)\n" +
                "b_bin = [ASNFZ4mrze8] (b.cfg)\n" +
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

        writeConfig(a,
                    "include \"b.cfg\" as a\n" +
                    "config.Database {}\n");
        writeConfig(b,
                    "include \"c.cfg\" as a\n" +
                    "config.Database {}\n");
        writeConfig(c,
                    "include \"a.cfg\" as a\n" +
                    "config.Database {}\n");

        ProvidenceConfig config = new ProvidenceConfig(registry, new HashMap<>());

        try {
            config.load(a);
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
        writeConfig(a,
                    "include \"b.cfg\" as a\n" +
                    "config.Database {}\n");

        ProvidenceConfig config = new ProvidenceConfig(registry, new HashMap<>());

        try {
            config.load(a);
            fail("no exception on circular deps");
        } catch (SerializerException e) {
            assertEquals("Included file \"b.cfg\" not found", e.getMessage());
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
        assertParseFailure("unknown identifier",
                           "Error in test.cfg on line 1, pos 13:\n" +
                           "    Invalid param value boo\n" +
                           "params { s = boo }\n" +
                           "-------------^",
                           "params { s = boo }");
    }

    private void assertParseFailure(String reason,
                                    String message,
                                    String pretty) throws IOException {
        File a = temp.newFile("test.cfg");
        writeConfig(a, pretty);

        ProvidenceConfig config = new ProvidenceConfig(registry, new HashMap<>());

        try {
            config.load(a);
            fail("no exception on " + reason);
        } catch (ConfigException e) {
            assertEquals("Wrong exception message on " + reason,
                         message, e.getMessage());
        } catch (SerializerException e) {
            assertEquals("Wrong exception message on " + reason,
                         message, e.toString());
        }
        a.delete();
    }

    private File writeConfig(String resource) throws IOException {
        File file = temp.newFile(new File(resource).getName());

        try (OutputStream out = new FileOutputStream(file);
             InputStream in = ProvidenceConfigTest.class.getResourceAsStream(resource)) {
            IOUtils.copy(in, out);
        }

        return file;
    }

    private File writeConfig(File file, String content) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(UTF_8));
        try (OutputStream out = new FileOutputStream(file)) {
            IOUtils.copy(in, out);
        }

        return file;
    }

    private File writeConfig(String resource, File folder) throws IOException {
        File file = new File(folder, new File(resource).getName());

        try (OutputStream out = new FileOutputStream(file);
             InputStream in = ProvidenceConfigTest.class.getResourceAsStream(resource)) {
            IOUtils.copy(in, out);
        }

        return file;
    }

}
