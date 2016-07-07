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

import com.google.common.collect.ImmutableSet;

import java.util.Set;

import static net.morimekta.providence.config.ProvidenceConfigUtil.buildKeySet;
import static net.morimekta.providence.config.ProvidenceConfigUtil.getFromMessage;

/**
 * A config that wraps a providence message instance.
 */
public class MessageConfig<Message extends PMessage<Message, Field>, Field extends PField>
        implements ProvidenceConfig {
    public MessageConfig(Message instance) {
        this(null, instance);
    }

    public MessageConfig(String prefix, Message instance) {
        this.prefix = prefix;
        this.instance = instance;
        Set<String> keySet = buildKeySet(prefix, instance);
        if (prefix != null) {
            keySet.add(prefix);
        }
        this.instanceKeySet = ImmutableSet.copyOf(keySet);
    }

    /**
     * Get the key prefix used in the confix wrapper. All keys in the message
     * structure is prefixed by this value.
     *
     * @return The key prefix.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Get the message enclosed in the config wrapper.
     *
     * @return The message.
     */
    public Message getMessage() {
        return instance;
    }

    @Override
    public Object get(String key) {
        if (key.equals(prefix)) {
            return getMessage();
        }
        if (containsKey(key)) {
            return getFromMessage(instance, cutPrefix(key));
        }
        return null;
    }

    @Override
    public boolean containsKey(String key) {
        return instanceKeySet.contains(key);
    }

    @Override
    public Set<String> keySet() {
        return instanceKeySet;
    }

    private String cutPrefix(String key) {
        if (prefix != null && key.startsWith(prefix + ".")) {
            return key.substring(prefix.length() + 1);
        }
        return key;
    }

    private final String      prefix;
    private final Message     instance;
    private final Set<String> instanceKeySet;
}
