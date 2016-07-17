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

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;

/**
 * Interface for layered providence configs.
 */
public interface LayeredProvidenceConfig extends ProvidenceConfig {
    /**
     * Since the same message may appear in multiple layers, we will merge
     * all the messages that share the same key. The top message will overwrite
     * the content of the messages below.
     * <p>
     * If you want the top message without values from the same message in
     * lower layers use {@link #getMessage(String)} instead.
     *
     * @param key The config key to look up.
     * @param <Message> The message type.
     * @param <Field> The message field type.
     * @return The merged message.
     */
    <Message extends PMessage<Message, Field>, Field extends PField>
    Message getMergedMessage(String key, PStructDescriptor<Message, Field> descriptor);
}
