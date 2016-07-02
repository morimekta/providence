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

import net.morimekta.config.ConfigException;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;

import java.util.Set;

import static net.morimekta.config.util.ConfigUtil.asBoolean;
import static net.morimekta.config.util.ConfigUtil.asCollection;
import static net.morimekta.config.util.ConfigUtil.asDouble;
import static net.morimekta.config.util.ConfigUtil.asInteger;
import static net.morimekta.config.util.ConfigUtil.asLong;
import static net.morimekta.config.util.ConfigUtil.asString;
import static net.morimekta.providence.config.ProvidenceConfigUtil.asMessage;
import static net.morimekta.providence.config.ProvidenceConfigUtil.buildKeySet;
import static net.morimekta.providence.config.ProvidenceConfigUtil.getFromMessage;

/**
 * A config that wraps a providence message builder instance. Because the
 * content may change during the lifetime, using this config as a lookup
 * mechanism is not recommended.
 * <p>
 * Note that the message builder config is <b>NOT</b> thread safe at all.
 */
public class MessageBuilderConfig<Message extends PMessage<Message, Field>, Field extends PField>
        implements ProvidenceConfigBuilder<MessageBuilderConfig<Message, Field>> {

    public MessageBuilderConfig(PStructDescriptor<Message, Field> descriptor) {
        this.builder = descriptor.builder();
    }

    /**
     * Get a snapshot of the config based on the message builder.
     *
     * @return The config snapshot.
     */
    public Message getSnapshot() {
        return builder.build();
    }

    /**
     * Get the message builder backing the config.
     *
     * @return The message builder.
     */
    public PMessageBuilder<Message, Field> getBuilder() {
        return builder;
    }

    @Override
    public Object get(String key) {
        return getFromMessage(getSnapshot(), key);
    }

    @Override
    public boolean containsKey(String key) {
        // This is a slight simplification, where we assume null values don't
        // exist in the message structure. It should be pretty rare though.
        try {
            return getFromMessage(getSnapshot(), key) != null;
        } catch (ConfigException e) {
            // obviously not found.
            return false;
        }
    }

    @Override
    public Set<String> keySet() {
        return buildKeySet(getSnapshot());
    }

    @Override
    public Object put(String key, Object value) {
        String[] parts = key.split("[.]");

        PMessageBuilder current = builder;
        for (int i = 0; i < (parts.length - 1); ++i) {
            String name = parts[i];
            PField field = current.descriptor().getField(name);
            if (field == null) {
                throw new ConfigException("");
            } else if (field.getType() != PType.MESSAGE) {
                throw new ConfigException("");
            }
            current = current.mutator(field.getKey());
        }

        String name = parts[parts.length - 1];
        PField field = current.descriptor().getField(name);
        if (field == null) {
            throw new ConfigException("");
        }

        switch (field.getType()) {
            case BOOL:
                current.set(field.getKey(), asBoolean(value));
                break;
            case BYTE:
                current.set(field.getKey(), (byte) asInteger(value));
                break;
            case I16:
                current.set(field.getKey(), (short) asInteger(value));
                break;
            case I32:
                current.set(field.getKey(), asInteger(value));
                break;
            case I64:
                current.set(field.getKey(), asLong(value));
                break;
            case DOUBLE:
                current.set(field.getKey(), asDouble(value));
                break;
            case STRING:
                current.set(field.getKey(), asString(value));
                break;
            case MESSAGE:
                current.set(field.getKey(), asMessage(value));
                break;
            case LIST:
            case SET:
                current.set(field.getKey(), asCollection(value));
                break;

            // --- not supported ---
            case BINARY:
            // NOTE: Binary types not supported in config.
            // TODO: Po
            case ENUM:
                // TODO: Handle enum values correctly.
                // This needs some extra tooling.
            default:
                throw new ConfigException("Unable to set providence type " + field.getType() +
                                          " from " + value.getClass().getSimpleName());
        }

        // TODO: At some point we may choose to return the replaced value.
        return null;
    }

    private final PMessageBuilder<Message, Field> builder;
}
