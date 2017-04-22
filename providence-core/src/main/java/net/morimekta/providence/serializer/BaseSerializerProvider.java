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

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Default serializer provider for core serializers.
 */
public abstract class BaseSerializerProvider implements SerializerProvider {
    private final Map<String, Serializer> serializerMap;
    private final String defaultContentType;

    /**
     * Get provider with the given default content type.
     *
     * @param defaultContentType The default mime-type.
     */
    public BaseSerializerProvider(String defaultContentType) {
        this.defaultContentType = defaultContentType;
        this.serializerMap = new HashMap<>();
    }

    @Override
    @Nonnull
    public Serializer getSerializer(String mediaType) {
        Serializer serializer = serializerMap.get(mediaType);
        if (serializer == null) {
            throw new IllegalArgumentException("No such serializer for media type " + mediaType);
        }
        return serializer;
    }

    @Override
    @Nonnull
    public Serializer getDefault() {
        return getSerializer(defaultContentType);
    }

    /**
     * Register the serializer with a given set of mime-types.
     * @param serializer The serializer to register.
     * @param mediaTypes The media types to register it for.
     */
    protected void register(Serializer serializer, String... mediaTypes) {
        for (String mimeType : mediaTypes) {
            this.serializerMap.put(mimeType, serializer);
        }
    }
}
