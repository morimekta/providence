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
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TSimpleJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;

/**
 * Created by morimekta on 5/4/16.
 */
public class ThriftOnlySerializerProvider extends BaseSerializerProvider {
    /**
     * Get the default serializer provider.
     */
    public ThriftOnlySerializerProvider() {
        this(BinarySerializer.MIME_TYPE);
    }

    /**
     * Get provider with the given default content type.
     *
     * @param defaultContentType The default mime-type.
     */
    public ThriftOnlySerializerProvider(String defaultContentType) {
        super(defaultContentType);

        // The BinarySerializer is identical to the TBinaryProtocolSerializer,
        // except that it is "native providence".
        register(new BinarySerializer(), BinarySerializer.MIME_TYPE, TBinaryProtocolSerializer.ALT_MIME_TYPE);
        register(new TCompactProtocolSerializer(), TCompactProtocolSerializer.MIME_TYPE);
        register(new TJsonProtocolSerializer(), TJsonProtocolSerializer.MIME_TYPE);
        register(new TTupleProtocolSerializer(), TTupleProtocolSerializer.MIME_TYPE);
        // Even though it's a write-only protocol.
        register(new TSimpleJsonProtocolSerializer(), TSimpleJsonProtocolSerializer.MIME_TYPE);
    }
}
