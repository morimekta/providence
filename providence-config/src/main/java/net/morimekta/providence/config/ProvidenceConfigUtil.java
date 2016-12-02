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

import net.morimekta.config.IncompatibleValueException;
import net.morimekta.config.KeyNotFoundException;
import net.morimekta.config.util.ConfigUtil;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.util.Binary;
import net.morimekta.util.Numeric;

import java.util.Map;
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
        return getInMessage(message, key, null);
    }

    /**
     * Look up a key in the message structure. If the key is not found, return
     * the default value. Note that the default value will be converted to the
     * type of the declared field, not returned verbatim.
     *
     * @param message The message to look up into.
     * @param key The key to look up.
     * @param defValue The default value.
     * @return The value found or the default.
     */
    public static Object getInMessage(PMessage message, String key, Object defValue) {
        PStructDescriptor descriptor = message.descriptor();

        if (key.contains(".")) {
            int idx = key.indexOf(".");
            String name = key.substring(0, idx);
            String sub = key.substring(idx + 1);

            PField field = descriptor.getField(name);
            if (field == null) {
                throw new KeyNotFoundException("Message " + message.descriptor().getQualifiedName() + " has no field named " +
                                               name);
            }
            PDescriptor fieldDesc = field.getDescriptor();
            if (fieldDesc.getType() != PType.MESSAGE) {
                throw new IncompatibleValueException("Field " + name + " is not of message type in " +
                                                     descriptor.getQualifiedName());
            }

            if (!message.has(field.getKey())) {
                while (sub.contains(".")) {
                    if (fieldDesc.getType() != PType.MESSAGE) {
                        throw new IncompatibleValueException("Field " + name + " is not of message type in " +
                                                             descriptor.getQualifiedName());
                    }

                    idx = sub.indexOf(".");
                    name = sub.substring(0, idx);
                    sub = sub.substring(idx + 1);

                    field = ((PStructDescriptor) fieldDesc).getField(name);
                    fieldDesc = field.getDescriptor();
                }
                return asFieldType(field, defValue);
            }
            return getInMessage((PMessage) message.get(field.getKey()), sub, defValue);
        }

        PField field = message.descriptor().getField(key);
        if (field == null) {
            throw new KeyNotFoundException("Message " + message.descriptor().getQualifiedName() + " has no field named " +
                                           key);
        }

        if (!message.has(field.getKey())) {
            return asFieldType(field, defValue);
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

    private static Object asFieldType(PField field, Object o) {
        if (o == null) {
            return field.getDefaultValue();
        }

        switch (field.getType()) {
            case VOID:
                return null;  // value is irrelevant.
            case BOOL:
                return ConfigUtil.asBoolean(o);
            case BYTE:
                return (byte) ConfigUtil.asInteger(o);
            case I16:
                return (short) ConfigUtil.asInteger(o);
            case I32:
                return ConfigUtil.asInteger(o);
            case I64:
                return ConfigUtil.asLong(o);
            case DOUBLE:
                return ConfigUtil.asDouble(o);
            case ENUM:
                if (o instanceof Number) {
                    return ((PEnumDescriptor) field.getDescriptor()).getValueById(((Number) o).intValue());
                } else if (o instanceof Numeric) {
                    return ((PEnumDescriptor) field.getDescriptor()).getValueById(((Numeric) o).asInteger());
                } else if (o instanceof CharSequence) {
                    return ((PEnumDescriptor) field.getDescriptor()).getValueByName(o.toString());
                } else {
                    throw new IncompatibleValueException("Unable to cast " + o.getClass().getSimpleName() + " to enum type.");
                }
            case MESSAGE:
                if (o instanceof PMessage) {
                    // Assume the correct message.
                    return o;
                } else {
                    throw new IncompatibleValueException("Unable to cast " + o.getClass().getSimpleName() + " to message.");
                }
            case STRING:
                return ConfigUtil.asString(o);
            case BINARY:
                if (o instanceof Binary) {
                    return o;
                } else if (o instanceof CharSequence) {
                    return Binary.fromBase64(o.toString());
                } else {
                    throw new IncompatibleValueException("Unable to cast " + o.getClass().getSimpleName() + " to binary.");
                }
            case LIST:
                return ((PList<Object>) field.getDescriptor()).builder().addAll(ConfigUtil.asCollection(o)).build();
            case SET:
                return ((PSet<Object>) field.getDescriptor()).builder().addAll(ConfigUtil.asCollection(o)).build();
            case MAP:
                if (o instanceof Map) {
                    return o;
                } else {
                    throw new IncompatibleValueException("Unable to cast " + o.getClass().getSimpleName() + " to map.");
                }
            default:
                throw new IllegalStateException("Unhandled field type: " + field.getType());
        }
    }
}
