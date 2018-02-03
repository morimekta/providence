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

import net.morimekta.providence.serializer.BaseSerializerProvider;
import net.morimekta.providence.serializer.BinarySerializer;

/**
 * Serializer provider that only holds serializers that is also supported by
 * apache thrift.
 */
public class ThriftOnlySerializerProvider extends BaseSerializerProvider {
    /**
     * Get the thrift-only serializer provider.
     */
    public ThriftOnlySerializerProvider() {
        this(false);
    }

    /**
     * Get the thrift-only serializer provider.
     *
     * @param strict If the serializer should read strictly.
     */
    public ThriftOnlySerializerProvider(boolean strict) {
        this(BinarySerializer.MEDIA_TYPE, strict);
    }

    /**
     * Get the thrift-only serializer provider.
     *
     * @param defaultMediaType The default media type.
     */
    public ThriftOnlySerializerProvider(String defaultMediaType) {
        this(defaultMediaType, false);
    }

    /**
     * Get the thrift-only serializer provider.
     *
     * @param defaultMediaType The default media type.
     * @param strict If the serializer should read strictly.
     */
    public ThriftOnlySerializerProvider(String defaultMediaType, boolean strict) {
        super(defaultMediaType);

        // The BinarySerializer is identical to the TBinaryProtocolSerializer,
        // except that it is "native providence".
        register(new BinarySerializer(strict), BinarySerializer.MEDIA_TYPE, TBinaryProtocolSerializer.ALT_MEDIA_TYPE);
        register(new TCompactProtocolSerializer(strict), TCompactProtocolSerializer.MEDIA_TYPE);
        register(new TJsonProtocolSerializer(strict), TJsonProtocolSerializer.MEDIA_TYPE);
        register(new TTupleProtocolSerializer(strict), TTupleProtocolSerializer.MEDIA_TYPE);
        // Even though it's a write-only protocol.
        register(new TSimpleJsonProtocolSerializer(), TSimpleJsonProtocolSerializer.MEDIA_TYPE);
    }
}
