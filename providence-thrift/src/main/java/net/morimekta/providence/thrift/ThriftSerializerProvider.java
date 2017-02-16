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
package net.morimekta.providence.thrift;

import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.DefaultSerializerProvider;

import static net.morimekta.providence.serializer.Serializer.DEFAULT_STRICT;

/**
 * Default serializer provider with added serializers for the thrift protocols
 * not covered by pure serializer.
 */
public class ThriftSerializerProvider extends DefaultSerializerProvider {
    /**
     * Get the thrift serializer provider.
     */
    public ThriftSerializerProvider() {
        this(DEFAULT_STRICT);
    }

    /**
     * Get the thrift serializer provider.
     *
     * @param strict If the serializer should read strictly.
     */
    public ThriftSerializerProvider(boolean strict) {
        this(BinarySerializer.MIME_TYPE, strict);
    }

    /**
     * Get the thrift serializer provider.
     *
     * @param defaultContentType The default mime-type.
     */
    public ThriftSerializerProvider(String defaultContentType) {
        this(defaultContentType, false);
    }

    /**
     * Get the thrift serializer provider.
     *
     * @param defaultContentType The default mime-type.
     * @param strict If the serializer should read strictly.
     */
    public ThriftSerializerProvider(String defaultContentType, boolean strict) {
        super(defaultContentType, strict);

        // Just add the thrift-only serializers.
        register(new TJsonProtocolSerializer(), TJsonProtocolSerializer.MIME_TYPE);
        register(new TCompactProtocolSerializer(), TCompactProtocolSerializer.MIME_TYPE);
        register(new TTupleProtocolSerializer(), TTupleProtocolSerializer.MIME_TYPE);
    }
}
