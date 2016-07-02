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

import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.model.ThriftField;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the message config wrapper.
 */
public class MessageConfigTest {
    @Test
    public void testConfig() {
        Declaration declaration = Declaration.withDeclConst(
                ThriftField.builder()
                           .setName("Name")
                           .setKey(44)
                           .build());

        MessageConfig<Declaration,Declaration._Field> config = new MessageConfig<>(declaration);

        assertSame(declaration, config.getMessage());

        assertTrue(config.containsKey("decl_const"));
        assertTrue(config.containsKey("decl_const.name"));

        assertEquals("Name", config.getString("decl_const.name"));
        assertEquals(44, config.getInteger("decl_const.key"));
    }
}
