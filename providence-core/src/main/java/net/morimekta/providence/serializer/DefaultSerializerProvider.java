/*
 * Copyright 2015-2016 Providence Authors
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
package net.morimekta.providence.serializer;

import static net.morimekta.providence.serializer.Serializer.DEFAULT_STRICT;

/**
 * Default serializer provider for core serializers.
 */
public class DefaultSerializerProvider extends BaseSerializerProvider {

    /**
     * Get the default serializer provider.
     */
    public DefaultSerializerProvider() {
        this(DEFAULT_STRICT);
    }

    /**
     * Get the default serializer provider.
     *
     * @param strict If the serializer should read strictly.
     */
    public DefaultSerializerProvider(boolean strict) {
        this(BinarySerializer.MEDIA_TYPE, strict);
    }

    /**
     * Get provider with the given default media type.
     *
     * @param defaultMediaType The default media-type.
     */
    public DefaultSerializerProvider(String defaultMediaType) {
        this(defaultMediaType, DEFAULT_STRICT);
    }

    /**
     * Get provider with the given default media type and strict mode.
     *
     * @param defaultMediaType The default media-type.
     * @param strict If the serializer should read strictly.
     */
    public DefaultSerializerProvider(String defaultMediaType, boolean strict) {
        super(defaultMediaType);

        register(new BinarySerializer(strict), BinarySerializer.MEDIA_TYPE, BinarySerializer.ALT_MEDIA_TYPE);
        register(new FastBinarySerializer(strict), FastBinarySerializer.MEDIA_TYPE);
        register(new JsonSerializer(strict), JsonSerializer.MEDIA_TYPE, JsonSerializer.JSON_MEDIA_TYPE);
    }
}
