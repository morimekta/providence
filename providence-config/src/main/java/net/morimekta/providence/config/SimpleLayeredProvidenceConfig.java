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

import net.morimekta.config.Config;
import net.morimekta.config.ConfigException;
import net.morimekta.config.impl.SimpleLayeredConfig;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static net.morimekta.providence.config.ProvidenceConfigUtil.asMessage;

/**
 * A layered config that gives access to contained messages, both the top
 * message with {@link #getMessage(String)} or the merged stack of messages
 * with {@link #getMergedMessage(String, PStructDescriptor)}.
 */
public class SimpleLayeredProvidenceConfig extends SimpleLayeredConfig implements LayeredProvidenceConfig {
    /**
     * Create an instance with an initial set of static configs.
     *
     * @param configs The configs from top to bottom layer.
     */
    public SimpleLayeredProvidenceConfig(Config... configs) {
        super(configs);
    }

    /**
     * Create an instance with an initial set of config suppliers.
     *
     * @param configs The config suppliers form top to bottom layer.
     */
    public SimpleLayeredProvidenceConfig(Collection<Supplier<Config>> configs) {
        super(configs);
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    Message getMergedMessage(String key, PStructDescriptor<Message, Field> descriptor) {
        PMessageBuilder<Message, Field> builder = null;

        List<Supplier<Config>> layers = layers();
        for (int i = layers.size(); i >= 0; --i) {
            Config config = layers.get(i).get();
            if (config.containsKey(key)) {
                Message tmp = asMessage(config.getValue(key), descriptor);
                if (builder == null) {
                    builder = descriptor.builder();
                }
                builder.merge(tmp);
            }
        }

        if (builder == null) {
            throw new ConfigException("No message at key " + key);
        }
        return builder.build();
    }
}
