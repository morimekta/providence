/*
 * Copyright (c) 2016, Stein Eldar Johnsen
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
package net.morimekta.providence.config;

import net.morimekta.config.ConfigBuilder;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

/**
 * Interface for config builder for providence.
 */
public interface ProvidenceConfigBuilder<Builder extends ProvidenceConfigBuilder<Builder>>
        extends ProvidenceConfig, ConfigBuilder<Builder> {
    /**
     * Get value as a providence message.
     *
     * @param key The key to look for.
     * @param message The message to put.
     * @param <Message> The message type.
     * @param <Field> The message field type.
     * @return The message.
     */
    @SuppressWarnings("unchecked")
    default <Message extends PMessage<Message, Field>, Field extends PField>
    Builder putMessage(String key, Message message) {
        put(key, message);
        return (Builder) this;
    }
}
