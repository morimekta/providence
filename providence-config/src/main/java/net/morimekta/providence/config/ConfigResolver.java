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
package net.morimekta.providence.config;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

/**
 * A config resolver resolves config file into suppliers for the actual config.
 * Note that if handling of changes to the config is required, that is done
 * by adding listener to the given supplier, see {@link ConfigSupplier}.
 */
@FunctionalInterface
public interface ConfigResolver {
    /**
     * Resolve a config file like on config file includes.
     *
     * @param configFile The file to resolve.
     * @param parentConfig The parent config supplier if any.
     * @param <Message> The message type.
     * @param <Field> The message field type.
     * @return The resolved config.
     * @throws ProvidenceConfigException If parsing of config failed.
     */
    @Nonnull
    <Message extends PMessage<Message, Field>, Field extends PField>
    ConfigSupplier<Message, Field> resolveConfig(@Nonnull File configFile,
                                                 @Nullable ConfigSupplier<Message, Field> parentConfig)
            throws ProvidenceConfigException;

    /**
     * Resolve a config file without parent config like on config file includes.
     *
     * @param configFile The file to resolve.
     * @param <Message> The message type.
     * @param <Field> The message field type.
     * @return The resolved config.
     * @throws ProvidenceConfigException If parsing of config failed.
     */
    @Nonnull
    default <Message extends PMessage<Message, Field>, Field extends PField>
    ConfigSupplier<Message, Field> resolveConfig(@Nonnull File configFile)
            throws ProvidenceConfigException {
        return resolveConfig(configFile, null);
    }
}
