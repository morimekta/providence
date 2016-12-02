/*
 * Copyright 2016 Providence Authors
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
package net.morimekta.providence.reflect.util;

import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PValueProvider;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.internal.ConstParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class ConstProvider implements PValueProvider<Object> {
    private final ProgramRegistry registry;
    private final String          typeName;
    private final String          programContext;
    private final String          defaultValue;

    private Object parsedValue;

    public ConstProvider(ProgramRegistry registry, String typeName, String programContext, String defaultValue) {
        this.registry = registry;
        this.typeName = typeName;
        this.programContext = programContext;
        this.defaultValue = defaultValue;
        this.parsedValue = null;
    }

    @Override
    public Object get() {
        if (parsedValue == null) {
            ConstParser parser = new ConstParser(registry, programContext);
            @SuppressWarnings("unchecked")
            PDescriptor type = registry.getProvider(typeName, programContext, Collections.EMPTY_MAP)
                                       .descriptor();
            try (ByteArrayInputStream in = new ByteArrayInputStream(defaultValue.getBytes(StandardCharsets.UTF_8))) {
                parsedValue = parser.parse(in, type);
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }

        return parsedValue;
    }
}
