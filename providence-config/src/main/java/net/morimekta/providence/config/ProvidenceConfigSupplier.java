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
package net.morimekta.providence.config;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.serializer.SerializerException;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

/**
 * A reloadable supplier of a providence config based on a config file.
 *
 * <pre>{@code
 *     ProvidenceConfigSupplier supplier = new ProvidenceConfigSupplier(configFile, configLoader);
 * }</pre>
 */
public class ProvidenceConfigSupplier<Message extends PMessage<Message, Field>, Field extends PField>
        implements ReloadableSupplier<Message> {
    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix.
     *
     * @param configFile The file containing the config.
     * @param configLoader The providence config loader.
     * @throws IOException If message read failed.
     * @throws SerializerException If message deserialization failed.
     */
    public ProvidenceConfigSupplier(File configFile, ProvidenceConfig configLoader)
            throws IOException {
        this.configFile = configFile;
        this.configLoader = configLoader;
        this.instance = configLoader.getSupplier(configFile);
    }

    /**
     * Get the message enclosed in the config wrapper.
     *
     * @return The message.
     */
    @Override
    public Message get() {
        return instance.get();
    }

    /**
     * Reload the message into the config.
     */
    @Override
    public void reload() {
        try {
            configLoader.reload(configFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    private final Supplier<Message> instance;
    private final File              configFile;
    private final ProvidenceConfig  configLoader;
}
