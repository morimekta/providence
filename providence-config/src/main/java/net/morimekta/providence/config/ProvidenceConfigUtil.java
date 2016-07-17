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
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;

import java.util.HashSet;
import java.util.Set;

/**
 * Utilities for providence config handling.
 */
public class ProvidenceConfigUtil {
    /**
     * Convert the value to a message, or fail if that is impossible. This
     * is essentially just a checked cast.
     *
     * @param value The instance to convert.
     * @param <Message> The message type.
     * @param <Field> The message field type.
     * @return The message instance.
     */
    @SuppressWarnings("unchecked")
    public static <Message extends PMessage<Message, Field>, Field extends PField> Message asMessage(Object value) {
        if (!(value instanceof PMessage)) {
            throw new ConfigException("Cannot convert " + value.getClass().getSimpleName() + " to providence message.");
        }
        return (Message) value;
    }

    /**
     * Convert the value to a message, or fail if that is impossible. This
     * is essentially just a checked cast.
     *
     * @param descriptor The message descriptor to convert to.
     * @param value The instance to convert.
     * @param <Message> The message type.
     * @param <Field> The message field type.
     * @return The message instance.
     */
    @SuppressWarnings("unchecked")
    public static <Message extends PMessage<Message, Field>, Field extends PField> Message asMessage(
            Object value,
            PStructDescriptor<Message, Field> descriptor) {
        if (!(value instanceof PMessage)) {
            throw new ConfigException("Cannot convert " + value.getClass().getSimpleName() + " to providence message.");
        }
        PMessage message = (PMessage) value;
        if (message.descriptor().equals(descriptor)) {
            return (Message) value;
        }
        throw new ConfigException("Message " + message.descriptor().getQualifiedName(null) + " is not instance of " + descriptor.getQualifiedName(null));
    }

    /**
     * Build a key set from a message.
     * @param message The message to make keyset from.
     * @return The key-set.
     */
    public static Set<String> buildKeySet(PMessage<?,?> message) {
        return buildKeySet(null, message);
    }

    /**
     * Build a key set from a message.
     * @param prefix The key prefix.
     * @param message The message to make keyset from.
     * @return The key-set.
     */
    public static Set<String> buildKeySet(String prefix, PMessage<?,?> message) {
        HashSet<String> keys = new HashSet<>();

        buildKeySet(prefix, message, keys);

        return keys;
    }

    /**
     * Look up a key in the message structure. If the key is not found, return null.
     *
     * @param message The message to look up into.
     * @param key The key to look up.
     * @return The value found or null.
     */
    public static Object getFromMessage(PMessage message, String key) {
        if (key.contains(".")) {
            String[] parts = key.split("[.]", 2);
            PField field = message.descriptor().getField(parts[0]);
            if (field == null) {
                throw new ConfigException("Message " + message.descriptor()
                                                              .getQualifiedName(null) + " has no field named " +
                                          parts[0]);
            }
            Object value = message.get(field.getKey());
            if (value == null) {
                return null;
            }

            if (value instanceof PMessage) {
                return getFromMessage((PMessage) value, parts[1]);
            }
            throw new ConfigException("Unable to fetch sub-keys from " + key + ", " + value.getClass().getSimpleName() + " is not a message");
        }

        PField field = message.descriptor().getField(key);
        if (field == null) {
            throw new ConfigException("Message " + message.descriptor()
                                                          .getQualifiedName(null) + " has no field named " +
                                      key);
        }

        return message.get(field.getKey());
    }


    private static void buildKeySet(String prefix, PMessage message, Set<String> into) {
        for (PField field : message.descriptor().getFields()) {
            if (message.has(field.getKey())) {
                String key = makeKey(prefix, field);
                into.add(key);
                if (field.getType() == PType.MESSAGE) {
                    buildKeySet(key, (PMessage) message.get(field.getKey()), into);
                }
            }
        }
    }

    private static String makeKey(String prefix, PField field) {
        if (prefix == null) {
            return field.getName();
        }
        return prefix + "." + field.getName();
    }
}
