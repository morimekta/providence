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
import net.morimekta.providence.model.EnumType;
import net.morimekta.providence.model.ThriftField;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Testing for the providence message config builder.
 */
public class MessageConfigBuilderTest {
    @Test
    public void testBuilderConfig() {
        MessageBuilderConfig<Declaration,Declaration._Field> config =
                new MessageBuilderConfig<>(Declaration.kDescriptor);

        config.putString("decl_const.name", "MyName");
        config.putInteger("decl_const.key", 4);

        Declaration first = config.getSnapshot();

        assertNull(config.getPrefix());

        assertNotNull(first.getDeclConst());
        assertEquals("MyName", first.getDeclConst().getName());
        assertEquals(4, first.getDeclConst().getKey());

        config.putString("decl_const.name", "OtherName");

        Declaration second = config.getSnapshot();

        assertEquals("OtherName", config.getString("decl_const.name"));
        assertEquals("OtherName", second.getDeclConst().getName());
        assertEquals("MyName", first.getDeclConst().getName());

        ThriftField cnst = config.getMessage("decl_const");
        assertNotNull(cnst);
        assertEquals(second.getDeclConst(), cnst);

        assertTrue(config.containsKey("decl_const"));
        assertFalse(config.containsKey("decl_enum"));
        assertFalse(config.containsKey("willy"));

        config.putMessage("decl_enum", EnumType.builder()
                                               .setName("MyEnum")
                                               .build());
        assertFalse(config.containsKey("decl_const"));

        Declaration third = config.getSnapshot();

        assertNotNull(third.getDeclEnum());
        assertEquals("MyEnum", third.getDeclEnum().getName());
    }

    @Test
    public void testBuilderConfig_withPrefix() {
        MessageBuilderConfig<Declaration,Declaration._Field> config =
                new MessageBuilderConfig<>("prefix", Declaration.kDescriptor);

        config.putString("prefix.decl_const.name", "MyName");
        config.putInteger("prefix.decl_const.key", 4);

        Declaration first = config.getSnapshot();
        assertEquals("prefix", config.getPrefix());

        assertNotNull(first.getDeclConst());
        assertEquals("MyName", first.getDeclConst().getName());
        assertEquals(4, first.getDeclConst().getKey());

        config.putString("prefix.decl_const.name", "OtherName");

        Declaration second = config.getSnapshot();

        assertEquals("OtherName", config.getString("prefix.decl_const.name"));
        assertEquals("OtherName", second.getDeclConst().getName());
        assertEquals("MyName", first.getDeclConst().getName());

        ThriftField cnst = config.getMessage("prefix.decl_const");
        assertNotNull(cnst);
        assertEquals(second.getDeclConst(), cnst);

        assertTrue(config.containsKey("prefix.decl_const"));
        assertFalse(config.containsKey("prefix.decl_enum"));
        assertFalse(config.containsKey("prefix.willy"));
        assertFalse(config.containsKey("willy"));
        assertTrue(config.containsKey("prefix"));

        config.putMessage("prefix.decl_enum", EnumType.builder()
                                                      .setName("MyEnum")
                                                      .build());
        assertFalse(config.containsKey("prefix.decl_const"));

        Declaration third = config.getSnapshot();

        assertNotNull(third.getDeclEnum());
        assertEquals("MyEnum", third.getDeclEnum().getName());
    }
}
