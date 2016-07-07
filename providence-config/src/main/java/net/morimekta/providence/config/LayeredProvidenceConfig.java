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
import net.morimekta.config.impl.LayeredConfig;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static net.morimekta.providence.config.ProvidenceConfigUtil.asMessage;

/**
 * A layered config that gives access to contained messages, both the top
 * message with {@link #getMessage(String)} or the merged stack of messages
 * with {@link #getMergedMessage(String)}.
 */
public class LayeredProvidenceConfig extends LayeredConfig implements ProvidenceConfig {
    /**
     * Create an instance with an initial set of static configs.
     *
     * @param configs The configs from top to bottom layer.
     */
    public LayeredProvidenceConfig(Config... configs) {
        super(configs);
    }

    /**
     * Create an instance with an initial set of config suppliers.
     *
     * @param configs The config suppliers form top to bottom layer.
     */
    public LayeredProvidenceConfig(Collection<Supplier<Config>> configs) {
        super(configs);
    }

    /**
     * Since the same message may appear in multiple layers, we will merge
     * all the messages that share the same key. The top message will overwrite
     * the content of the messages below.
     * <p>
     * If you want the top message without values from the same message in
     * lower layers use {@link #getMessage(String)} instead.
     *
     * @param key The config key to look up.
     */
    public <Message extends PMessage<Message, Field>, Field extends PField>
    Message getMergedMessage(String key) {
        PMessageBuilder<Message, Field> builder = null;

        List<Supplier<Config>> layers = layers();
        for (int i = layers.size(); i >= 0; --i) {
            Config config = layers.get(i).get();
            if (config.containsKey(key)) {
                Message tmp = asMessage(config.getValue(key));
                if (builder != null) {
                    builder.merge(tmp);
                } else {
                    builder = tmp.mutate();
                }
            }
        }

        if (builder == null) {
            throw new ConfigException("No such message: " + key);
        }
        return builder.build();
    }
}
