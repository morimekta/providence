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

import com.google.common.net.MediaType;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Default serializer provider for core serializers.
 */
public abstract class BaseSerializerProvider implements SerializerProvider {
    private static final String ANY_MEDIA_TYPE = MediaType.ANY_TYPE.toString();

    private final Map<String, Serializer> serializerMap;
    private final String                  defaultMediaType;

    /**
     * Get provider with the given default media type.
     *
     * @param defaultMediaType The default media-type.
     */
    public BaseSerializerProvider(String defaultMediaType) {
        this.defaultMediaType = defaultMediaType;
        this.serializerMap = new HashMap<>();
    }

    @Override
    @Nonnull
    public Serializer getSerializer(String mediaType) {
        mediaType = MediaType.parse(mediaType)
                             .withoutParameters()
                             .toString();

        Serializer serializer;
        if (ANY_MEDIA_TYPE.equals(mediaType)) {
            serializer = serializerMap.get(defaultMediaType);
        } else {
            serializer = serializerMap.get(mediaType);
        }

        if (serializer == null) {
            throw new IllegalArgumentException("No serializer for media type '" + mediaType + "'");
        }
        return serializer;
    }

    @Override
    @Nonnull
    public Serializer getDefault() {
        return getSerializer(defaultMediaType);
    }

    /**
     * Register the serializer with a given set of media types.
     *
     * @param serializer The serializer to register.
     * @param mediaTypes The media types to register it for.
     */
    protected void register(Serializer serializer, String... mediaTypes) {
        for (String mediaType : mediaTypes) {
            this.serializerMap.put(mediaType, serializer);
        }
    }
}
