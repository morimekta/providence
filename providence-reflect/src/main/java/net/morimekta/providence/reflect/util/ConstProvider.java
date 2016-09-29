/*
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
    private final DocumentRegistry mRegistry;
    private final String           mTypeName;
    private final String           mPackageContext;
    private final String           mDefaultValue;

    private Object mParsedValue;

    public ConstProvider(DocumentRegistry registry, String typeName, String packageContext, String defaultValue) {
        mRegistry = registry;
        mTypeName = typeName;
        mPackageContext = packageContext;
        mDefaultValue = defaultValue;
        mParsedValue = null;
    }

    @Override
    public Object get() {
        if (mParsedValue == null) {
            ConstParser parser = new ConstParser();
            @SuppressWarnings("unchecked")
            PDescriptor type = mRegistry.getProvider(mTypeName, mPackageContext, Collections.EMPTY_MAP)
                                        .descriptor();
            try (ByteArrayInputStream in = new ByteArrayInputStream(mDefaultValue.getBytes(StandardCharsets.UTF_8))) {
                mParsedValue = parser.parse(in, type);
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }

        return mParsedValue;
    }
}
