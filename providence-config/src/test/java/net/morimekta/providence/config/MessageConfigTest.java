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

import net.morimekta.providence.mio.FileMessageReader;
import net.morimekta.providence.mio.FileMessageWriter;
import net.morimekta.providence.model.ConstType;
import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.SerializerException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the message config wrapper.
 */
public class MessageConfigTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testConfig() {
        Declaration declaration = Declaration.withDeclConst(
                ConstType.builder()
                         .setName("Name")
                         .setType("i32")
                         .setValue("44")
                         .build());

        MessageConfig<Declaration,Declaration._Field> config = new MessageConfig<>(declaration);

        assertSame(declaration, config.getMessage());

        assertNull(config.getPrefix());

        assertTrue(config.containsKey("decl_const.name"));
        assertFalse(config.containsKey("decl_const"));

        assertEquals("Name", config.getString("decl_const.name"));
        assertEquals(44, config.getInteger("decl_const.value"));
    }

    @Test
    public void testConfigWithPrefix() {
        Declaration declaration = Declaration.withDeclConst(
                ConstType.builder()
                           .setName("Name")
                           .setValue("44")
                           .setType("i32")
                           .build());

        MessageConfig<Declaration,Declaration._Field> config = new MessageConfig<>("prefix", declaration);

        assertSame(declaration, config.getMessage());
        assertEquals("prefix", config.getPrefix());

        assertFalse(config.containsKey("prefix"));
        assertFalse(config.containsKey("prefix.decl_const"));

        assertFalse(config.containsKey("decl_const"));
        assertFalse(config.containsKey("prefix.decl_const"));
        assertTrue(config.containsKey("prefix.decl_const.name"));

        assertEquals("Name", config.getString("prefix.decl_const.name"));
        assertEquals(44, config.getInteger("prefix.decl_const.value"));
    }

    @Test
    public void testSupplier() throws IOException, SerializerException {
        File file = temp.newFile();

        Declaration declaration = Declaration.withDeclConst(ConstType.builder()
                                                                     .setName("Name")
                                                                     .setType("i32")
                                                                     .setValue("44")
                                                                     .build());
        FileMessageWriter writer = new FileMessageWriter(file, new JsonSerializer());
        writer.write(declaration);
        writer.close();

        // -- supplier test

        MessageConfigSupplier<Declaration, Declaration._Field> supplier = new MessageConfigSupplier<>("decl",
                                                                                                      Declaration.kDescriptor,
                                                                                                      new FileMessageReader(
                                                                                                              file,
                                                                                                              new JsonSerializer()));

        assertEquals("decl", supplier.getPrefix());

        MessageConfig<Declaration, Declaration._Field> config = supplier.get();

        assertEquals("decl", config.getPrefix());
        assertEquals("Name", config.getString("decl.decl_const.name"));

        declaration = Declaration.withDeclConst(ConstType.builder()
                                                         .setName("Other")
                                                         .setType("double")
                                                         .setValue("33")
                                                         .build());
        writer.write(declaration);
        writer.close();

        supplier.reload();
        config = supplier.get();

        assertEquals("Other", config.getString("decl.decl_const.name"));
    }
}
