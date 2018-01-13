/*
 * Copyright 2016,2017 Providence Authors
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
package net.morimekta.providence.config.impl;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PType;
import net.morimekta.providence.config.ProvidenceConfigException;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.util.Binary;
import net.morimekta.util.Numeric;
import net.morimekta.util.Stringable;
import net.morimekta.util.Strings;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

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
     * @throws ProvidenceConfigException When unable to get value from message.
     */
    static Object getInMessage(PMessage message, String key) throws ProvidenceConfigException {
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
     * @throws ProvidenceConfigException When unable to get value from message.
     */
    static Object getInMessage(PMessage message, final String key, Object defValue)
            throws ProvidenceConfigException {
        String sub = key;
        String name;

        PMessageDescriptor descriptor = message.descriptor();
        while (sub.contains(".")) {
            int idx = sub.indexOf(".");

            name = sub.substring(0, idx);
            sub = sub.substring(idx + 1);

            PField field = descriptor.findFieldByName(name);
            if (field == null) {
                throw new ProvidenceConfigException("Message " + descriptor.getQualifiedName() + " has no field named " + name);
            }

            PDescriptor fieldDesc = field.getDescriptor();
            if (fieldDesc.getType() != PType.MESSAGE) {
                throw new ProvidenceConfigException("Field '" + name + "' is not of message type in " + descriptor.getQualifiedName());
            }
            descriptor = (PMessageDescriptor) fieldDesc;

            if (message != null) {
                message = (PMessage) message.get(field.getId());
            }
        }

        PField field = descriptor.findFieldByName(sub);
        if (field == null) {
            throw new ProvidenceConfigException("Message " + descriptor.getQualifiedName() + " has no field named " + sub);
        }

        if (message == null || !message.has(field.getId())) {
            return asType(field.getDescriptor(), defValue);
        }

        return message.get(field.getId());
    }

    @SuppressWarnings("unchecked")
    private static Object asType(PDescriptor descriptor, Object o) throws ProvidenceConfigException {
        if (o == null) {
            return null;
        }

        switch (descriptor.getType()) {
            case BOOL:
                return asBoolean(o);
            case BYTE:
                return (byte) asInteger(o, Byte.MIN_VALUE, Byte.MAX_VALUE);
            case I16:
                return (short) asInteger(o, Short.MIN_VALUE, Short.MAX_VALUE);
            case I32:
                return asInteger(o, Integer.MIN_VALUE, Integer.MAX_VALUE);
            case I64:
                return asLong(o);
            case DOUBLE:
                return asDouble(o);
            case ENUM:
                if (o instanceof Number) {
                    return ((PEnumDescriptor) descriptor).findById(((Number) o).intValue());
                } else if (o instanceof Numeric) {
                    return ((PEnumDescriptor) descriptor).findById(((Numeric) o).asInteger());
                } else if (o instanceof CharSequence) {
                    return ((PEnumDescriptor) descriptor).findByName(o.toString());
                } else {
                    throw new ProvidenceConfigException("Unable to cast " + o.getClass().getSimpleName() + " to enum type.");
                }
            case MESSAGE:
                if (o instanceof PMessage) {
                    // Assume the correct message.
                    return o;
                } else {
                    throw new ProvidenceConfigException("Unable to cast " + o.getClass().getSimpleName() + " to message.");
                }
            case STRING:
                return asString(o);
            case BINARY:
                if (o instanceof Binary) {
                    return o;
                } else if (o instanceof CharSequence) {
                    return Binary.fromBase64(o.toString());
                } else {
                    throw new ProvidenceConfigException("Unable to cast " + o.getClass().getSimpleName() + " to binary.");
                }
            case LIST: {
                PList<Object> list = (PList) descriptor;
                return list.builder()
                           .addAll(asCollection(o, list.itemDescriptor()))
                           .build();
            }
            case SET: {
                PSet<Object> set = (PSet) descriptor;
                return set.builder()
                          .addAll(asCollection(o, set.itemDescriptor()))
                          .build();
            }
            case MAP: {
                PMap<Object, Object> map = (PMap) descriptor;
                return map.builder()
                          .putAll(asMap(o, map.keyDescriptor(), map.itemDescriptor()))
                          .build();
            }
            default:
                throw new IllegalStateException("Unhandled field type: " + descriptor.getType());
        }
    }

    /**
     * Convert the value to a boolean.
     *
     * @param value The value instance.
     * @return The boolean value.
     * @throws ProvidenceConfigException When unable to convert value.
     */
    static boolean asBoolean(Object value) throws ProvidenceConfigException {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof Double || value instanceof Float) {
            throw new ProvidenceConfigException("Unable to convert real value to boolean");
        } else if (value instanceof Number) {
            long l = ((Number) value).longValue();
            if (l == 0L) return false;
            if (l == 1L) return true;
            throw new ProvidenceConfigException("Unable to convert number " + l + " to boolean");
        } else if (value instanceof CharSequence) {
            switch (value.toString().toLowerCase()) {
                case "0":
                case "n":
                case "f":
                case "no":
                case "false":
                    return false;
                case "1":
                case "y":
                case "t":
                case "yes":
                case "true":
                    return true;
                default:
                    throw new ProvidenceConfigException(String.format(
                            "Unable to parse the string \"%s\" to boolean",
                            Strings.escape(value.toString())));
            }
        }
        throw new ProvidenceConfigException("Unable to convert " + value.getClass().getSimpleName() + " to a boolean");
    }

    /**
     * Convert the value to an integer.
     *
     * @param value The value instance.
     * @return The integer value.
     * @throws ProvidenceConfigException When unable to convert value.
     */
    static int asInteger(Object value, int min, int max) throws ProvidenceConfigException {
        if (value instanceof Long) {
            return validateInRange("Long", (Long) value, min, max);
        } else if (value instanceof Float || value instanceof Double) {
            long l = ((Number) value).longValue();
            if ((double) l != ((Number) value).doubleValue()) {
                throw new ProvidenceConfigException("Truncating integer decimals from " + value.toString());
            }
            return validateInRange(value.getClass().getSimpleName(), l, min, max);
        } else if (value instanceof Number) {
            return validateInRange(value.getClass().getSimpleName(), ((Number) value).intValue(), min, max);
        } else if (value instanceof Numeric) {
            return validateInRange("Numeric", ((Numeric) value).asInteger(), min, max);
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? 1 : 0;
        } else if (value instanceof CharSequence) {
            try {
                String s = value.toString();
                if (s.startsWith("0x")) {
                    return validateInRange("String", Integer.parseInt(s.substring(2), 16), min, max);
                } else if (s.startsWith("0")) {
                    return validateInRange("String", Integer.parseInt(s, 8), min, max);
                }
                return validateInRange("String", Integer.parseInt(value.toString()), min, max);
            } catch (NumberFormatException nfe) {
                throw new ProvidenceConfigException(
                        "Unable to parse string \"" + Strings.escape(value.toString()) +
                        "\" to an int", nfe);
            }
        } else if (value instanceof Date) {
            // Convert date timestamp to seconds since epoch.
            return validateInRange("Date", (((Date) value).getTime() / 1000), min, max);
        }
        throw new ProvidenceConfigException("Unable to convert " + value.getClass().getSimpleName() + " to an int");
    }

    private static int validateInRange(String type, long l, int min, int max) throws ProvidenceConfigException {
        if (l < min) {
            throw new ProvidenceConfigException(type + " value outsize of bounds: " + l + " < " + min);
        } else if (l > max) {
            throw new ProvidenceConfigException(type + " value outsize of bounds: " + l + " > " + max);
        }
        return (int) l;
    }

    /**
     * Convert the value to a long.
     *
     * @param value The value instance.
     * @return The long value.
     * @throws ProvidenceConfigException When unable to convert value.
     */
    static long asLong(Object value) throws ProvidenceConfigException {
        if (value instanceof Float || value instanceof Double) {
            long l = ((Number) value).longValue();
            if ((double) l != ((Number) value).doubleValue()) {
                throw new ProvidenceConfigException("Truncating long decimals from " + value.toString());
            }
            return l;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof Numeric) {
            return ((Numeric) value).asInteger();
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? 1L : 0L;
        } else if (value instanceof CharSequence) {
            try {
                String s = value.toString();
                if (s.startsWith("0x")) {
                    return Long.parseLong(s.substring(2), 16);
                } else if (s.startsWith("0")) {
                    return Long.parseLong(s, 8);
                }
                return Long.parseLong(s);
            } catch (NumberFormatException nfe) {
                throw new ProvidenceConfigException("Unable to parse string \"" + Strings.escape(value.toString()) +
                                                    "\" to a long", nfe);
            }
        } else if (value instanceof Date) {
            // Return date timestamp in milliseconds.
            return ((Date) value).getTime();
        }
        throw new ProvidenceConfigException("Unable to convert " + value.getClass().getSimpleName() + " to a long");
    }

    /**
     * Convert the value to a double.
     *
     * @param value The value instance.
     * @return The double value.
     * @throws ProvidenceConfigException When unable to convert value.
     */
    static double asDouble(Object value) throws ProvidenceConfigException {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof Numeric) {
            return ((Numeric) value).asInteger();
        } else if (value instanceof CharSequence) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException nfe) {
                throw new ProvidenceConfigException("Unable to parse string \"" + Strings.escape(value.toString()) +
                                                    "\" to a double", nfe);
            }
        }
        throw new ProvidenceConfigException(
                "Unable to convert " + value.getClass().getSimpleName() + " to a double");
    }

    /**
     * Convert the value to a string.
     *
     * @param value The value instance.
     * @return The string value.
     * @throws ProvidenceConfigException When unable to convert value.
     */
    static String asString(Object value) throws ProvidenceConfigException {
        if (value instanceof Collection || value instanceof Map) {
            throw new ProvidenceConfigException(
                    "Unable to convert " + value.getClass().getSimpleName() + " to a string");
        } else if (value instanceof Stringable) {
            return ((Stringable) value).asString();
        } else if (value instanceof Date) {
            Instant instant = ((Date) value).toInstant();
            return DateTimeFormatter.ISO_INSTANT.format(instant);
        }
        return Objects.toString(value);
    }

    /**
     * Convert the value to a collection.
     *
     * @param value The value instance.
     * @param itemType The item type descriptor.
     * @param <T> The item type.
     * @return The collection value.
     * @throws ProvidenceConfigException When unable to convert value.
     */
    @SuppressWarnings("unchecked")
    static <T> Collection<T> asCollection(Object value, PDescriptor itemType) throws ProvidenceConfigException {
        if (value instanceof Collection) {
            List<T> out = new ArrayList<>();
            for (Object item : (Collection) value) {
                out.add((T) asType(itemType, item));
            }
            return out;
        }
        throw new ProvidenceConfigException(
                "Unable to convert " + value.getClass().getSimpleName() + " to a collection");
    }

    /**
     * Convert the value to a collection.
     *
     * @param value The value instance.
     * @param keyType The key type descriptor.
     * @param itemType The value type descriptor.
     * @param <K> The map key type.
     * @param <V> The map value type.
     * @return The map value.
     * @throws ProvidenceConfigException When unable to convert value.
     */
    @SuppressWarnings("unchecked")
    static <K,V> Map<K,V> asMap(Object value, PDescriptor keyType, PDescriptor itemType) throws ProvidenceConfigException {
        if (value instanceof Map) {
            Map<K,V> out = value instanceof TreeMap ? new TreeMap<>() : new LinkedHashMap<>();
            for (Map.Entry item : ((Map<?,?>) value).entrySet()) {
                out.put((K) asType(keyType, item.getKey()),
                        (V) asType(itemType, item.getValue()));
            }
            return out;
        }
        throw new ProvidenceConfigException(
                "Unable to convert " + value.getClass().getSimpleName() + " to a collection");
    }
}
