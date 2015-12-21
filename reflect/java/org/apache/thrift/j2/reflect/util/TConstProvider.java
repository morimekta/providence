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

package org.apache.thrift.j2.reflect.util;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.reflect.parser.TParseException;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.reflect.parser.internal.TConstParser;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class TConstProvider
        implements TValueProvider<Object> {
    private final TTypeRegistry mRegistry;
    private final String mTypeName;
    private final String mPackageContext;
    private final String mDefaultValue;

    private Object mParsedValue;

    public TConstProvider(TTypeRegistry registry,
                          String typeName,
                          String packageContext,
                          String defaultValue) {
        mRegistry = registry;
        mTypeName = typeName;
        mPackageContext = packageContext;
        mDefaultValue = defaultValue;
        mParsedValue = null;
    }

    @Override
    public Object get() {
        if (mParsedValue == null) {
            TConstParser parser = new TConstParser();
            ByteArrayInputStream in = new ByteArrayInputStream(mDefaultValue.getBytes(StandardCharsets.UTF_8));
            TDescriptor type = mRegistry.getProvider(mTypeName, mPackageContext).descriptor();
            try {
                mParsedValue = parser.parse(in, type);
            } catch (TParseException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return mParsedValue;
    }
}
