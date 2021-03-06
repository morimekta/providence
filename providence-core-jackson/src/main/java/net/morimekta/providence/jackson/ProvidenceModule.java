/*
 * Copyright 2017 Providence Authors
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
package net.morimekta.providence.jackson;

import net.morimekta.util.Binary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ProvidenceModule extends SimpleModule {
    public ProvidenceModule() {
        super("providence-core-jackson");

        addSerializer(Binary.class, new BinarySerializer());
        addDeserializer(Binary.class, new BinaryDeserializer());

        addKeySerializer(Binary.class, new BinaryKeySerializer());
        addKeyDeserializer(Binary.class, new BinaryKeyDeserializer());
    }

    public static void register(ObjectMapper mapper) {
        mapper.registerModule(new ProvidenceModule());
    }
}
