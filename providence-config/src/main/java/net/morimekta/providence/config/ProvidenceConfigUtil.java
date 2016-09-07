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

import net.morimekta.config.IncompatibleValueException;
import net.morimekta.config.KeyNotFoundException;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;

import java.util.Set;

/**
 * Utilities for helping with providence config handling.
 */
public class ProvidenceConfigUtil {
    /**
     * Look up a key in the message structure. If the key is not found, return null.
     *
     * @param message The message to look up into.
     * @param key The key to look up.
     * @return The value found or null.
     */
    public static Object getInMessage(PMessage message, String key) {
        PStructDescriptor descriptor = message.descriptor();

        if (key.contains(".")) {
            int idx = key.indexOf(".");
            String name = key.substring(0, idx);
            String sub = key.substring(idx + 1);

            PField field = descriptor.getField(name);
            if (field == null) {
                throw new KeyNotFoundException("Message " + message.descriptor().getQualifiedName(null) + " has no field named " +
                                               name);
            }
            if (field.getDescriptor().getType() != PType.MESSAGE) {
                throw new IncompatibleValueException("Field " + name + " is not of message type in " +
                                                     descriptor.getQualifiedName(null));
            }
            if (!message.has(field.getKey())) {
                throw new KeyNotFoundException("Field " + name + " not a set.");
            }
            return getInMessage((PMessage) message.get(field.getKey()), sub);
        }

        PField field = message.descriptor().getField(key);
        if (field == null) {
            throw new KeyNotFoundException("Message " + message.descriptor().getQualifiedName(null) + " has no field named " +
                                           key);
        }

        return message.get(field.getKey());
    }

    /**
     * Build the key-sets for the given message and prefix.
     * @param prefix The current prefix, or null for no prefix.
     * @param message The message to make key-set pairs for.
     * @param valueKeySet The value key-set (with simple values)
     */
    protected static void buildKeySet(String prefix, PMessage message, Set<String> valueKeySet) {
        for (PField field : message.descriptor().getFields()) {
            if (message.has(field.getKey())) {
                String key = makeKey(prefix, field);

                if (field.getType() == PType.MESSAGE) {
                    buildKeySet(key, (PMessage) message.get(field.getKey()), valueKeySet);
                } else {
                    valueKeySet.add(key);
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
