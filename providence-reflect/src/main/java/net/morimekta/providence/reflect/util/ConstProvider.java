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
import net.morimekta.providence.reflect.parser.internal.ConstParser;
import net.morimekta.providence.util.TypeRegistry;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * A value provider for thrift constants.
 */
public class ConstProvider implements PValueProvider<Object> {
    private final TypeRegistry registry;
    private final String       typeName;
    private final String       programContext;
    private final String       constantString;
    private final int          startLineNo;
    private final int          startLinePos;

    private Object parsedValue;

    public ConstProvider(@Nonnull TypeRegistry registry,
                         @Nonnull String typeName,
                         @Nonnull String programContext,
                         @Nonnull String constantString,
                         int startLineNo,
                         int startLinePos) {
        this.registry = registry;
        this.typeName = typeName;
        this.programContext = programContext;
        this.constantString = constantString;
        this.parsedValue = null;

        this.startLineNo = startLineNo;
        this.startLinePos = startLinePos;
    }

    @Override
    public Object get() {
        if (parsedValue == null) {
            ConstParser parser = new ConstParser(registry,
                                                 programContext,
                                                 startLineNo,
                                                 startLinePos);
            @SuppressWarnings("unchecked")
            PDescriptor type = registry.getProvider(typeName, programContext, Collections.EMPTY_MAP)
                                       .descriptor();
            try (ByteArrayInputStream in = new ByteArrayInputStream(constantString.getBytes(StandardCharsets.UTF_8))) {
                parsedValue = parser.parse(in, type);
            } catch (IOException e) {
                throw new UncheckedIOException(e.getMessage(), e);
            }
        }

        return parsedValue;
    }
}
