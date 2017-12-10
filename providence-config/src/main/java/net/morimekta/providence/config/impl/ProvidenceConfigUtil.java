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
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

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
            return asFieldType(field, defValue);
        }

        return message.get(field.getId());
    }

    @SuppressWarnings("unchecked")
    private static Object asFieldType(PField field, Object o) throws ProvidenceConfigException {
        if (o == null) {
            return field.getDefaultValue();
        }

        switch (field.getType()) {
            case BOOL:
                return asBoolean(o);
            case BYTE:
                return (byte) asInteger(o);
            case I16:
                return (short) asInteger(o);
            case I32:
                return asInteger(o);
            case I64:
                return asLong(o);
            case DOUBLE:
                return asDouble(o);
            case ENUM:
                if (o instanceof Number) {
                    return ((PEnumDescriptor) field.getDescriptor()).findById(((Number) o).intValue());
                } else if (o instanceof Numeric) {
                    return ((PEnumDescriptor) field.getDescriptor()).findById(((Numeric) o).asInteger());
                } else if (o instanceof CharSequence) {
                    return ((PEnumDescriptor) field.getDescriptor()).findByName(o.toString());
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
            case LIST:
                return ((PList<Object>) field.getDescriptor()).builder().addAll(asCollection(o)).build();
            case SET:
                return ((PSet<Object>) field.getDescriptor()).builder().addAll(asCollection(o)).build();
            case MAP:
                if (o instanceof Map) {
                    return ((PMap<Object,Object>) field.getDescriptor()).builder().putAll((Map<Object,Object>) o).build();
                } else {
                    throw new ProvidenceConfigException("Unable to cast " + o.getClass().getSimpleName() + " to map.");
                }
            default:
                throw new IllegalStateException("Unhandled field type: " + field.getType());
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
            throw new ProvidenceConfigException("Unable to convert double value to boolean");
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
    static int asInteger(Object value) throws ProvidenceConfigException {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof Numeric) {
            return ((Numeric) value).asInteger();
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? 1 : 0;
        } else if (value instanceof CharSequence) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException nfe) {
                throw new ProvidenceConfigException(
                        "Unable to parse string \"" + Strings.escape(value.toString()) +
                        "\" to an int", nfe);
            }
        } else if (value instanceof Date) {
            // Convert date timestamp to seconds since epoch.
            return (int) (((Date) value).getTime() / 1000);
        }
        throw new ProvidenceConfigException("Unable to convert " + value.getClass().getSimpleName() + " to an int");
    }

    /**
     * Convert the value to a long.
     *
     * @param value The value instance.
     * @return The long value.
     * @throws ProvidenceConfigException When unable to convert value.
     */
    static long asLong(Object value) throws ProvidenceConfigException {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof Numeric) {
            return ((Numeric) value).asInteger();
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? 1L : 0L;
        } else if (value instanceof CharSequence) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException nfe) {
                throw new ProvidenceConfigException("Unable to parse string \"" + Strings.escape(value.toString()) +
                                                    "\" to a long", nfe);
            }
        } else if (value instanceof Date) {
            // Return date timestamp.
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
     * @param <T> The collection item type.
     * @return The collection value.
     * @throws ProvidenceConfigException When unable to convert value.
     */
    @SuppressWarnings("unchecked")
    static <T> Collection<T> asCollection(Object value) throws ProvidenceConfigException {
        if (value instanceof Collection) {
            return (Collection) value;
        }
        throw new ProvidenceConfigException(
                "Unable to convert " + value.getClass().getSimpleName() + " to a collection");
    }
}
