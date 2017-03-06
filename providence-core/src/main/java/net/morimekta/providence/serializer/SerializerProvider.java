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

/**
 * Provider of serializers based on a string mime-type.
 */
@FunctionalInterface
public interface SerializerProvider {
    /**
     * Get serializer for the given mime-type
     *
     * @param mediaType The media-type to get serializer for.
     * @return The serializer, or null if not found.
     * @throws IllegalArgumentException If no such serializer exists.
     */
    @Nonnull
    Serializer getSerializer(String mediaType);

    /**
     * @return The default serializer.
     */
    @Nonnull
    default Serializer getDefault() {
        return getSerializer(BinarySerializer.MIME_TYPE);
    }
}
