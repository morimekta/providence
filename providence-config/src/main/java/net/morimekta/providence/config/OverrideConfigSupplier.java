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
package net.morimekta.providence.config;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.config.impl.UpdatingConfigSupplier;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.util.Binary;
import net.morimekta.util.Strings;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.UNDEFINED;

/**
 * A supplier of a providence message config based on a parent config
 * (supplier) and a map of value overrides. Handy for use with
 * argument parsers overrides, system property overrides or similar.
 *
 * <pre>{@code
 *     Supplier<Service> supplier = new OverrideConfigSupplier<>(
 *             baseServiceConfig,
 *             ImmutableMap.of(
 *                 "db.username", "root",
 *                 "jdbc.driver", "com.oracle.jdbc.Driver"
 *             ));
 * }</pre>
 */
public class OverrideConfigSupplier<Message extends PMessage<Message, Field>, Field extends PField>
        extends UpdatingConfigSupplier<Message, Field> {
    // Make sure the listener cannot be GC'd as long as this instance
    // survives.
    private final ConfigListener<Message, Field> listener;
    private final ConfigSupplier<Message, Field> parent;
    private final Map<String, String> overrides;

    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix. Note that reading from properties
     * are <b>never</b> strict.
     *
     * @param parent The parent message to override values of.
     * @param overrides The message override values.
     * @throws ProvidenceConfigException If message overriding failed
     */
    public OverrideConfigSupplier(@Nonnull ConfigSupplier<Message,Field> parent,
                                  @Nonnull Properties overrides)
            throws ProvidenceConfigException {
        this(parent, propertiesMap(overrides), false);
    }

    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix.
     *
     * @param parent The parent message to override values of.
     * @param overrides The message override values.
     * @throws ProvidenceConfigException If message overriding failed
     */
    public OverrideConfigSupplier(@Nonnull ConfigSupplier<Message,Field> parent,
                                  @Nonnull Map<String, String> overrides)
            throws ProvidenceConfigException {
        this(parent, overrides, false);
    }

    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix.
     *
     * @param parent The parent message to override values of.
     * @param overrides The message override values.
     * @param strict If config should be read strictly.
     * @throws ProvidenceConfigException If message overriding failed
     */
    public OverrideConfigSupplier(@Nonnull ConfigSupplier<Message,Field> parent,
                                  @Nonnull Map<String, String> overrides,
                                  boolean strict)
            throws ProvidenceConfigException {
        this(Clock.systemUTC(), parent, overrides, strict);
    }
    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix.
     *
     * @param clock Clock used to time the updates.
     * @param parent The parent message to override values of.
     * @param overrides The message override values.
     * @param strict If config should be read strictly.
     * @throws ProvidenceConfigException If message overriding failed
     */
    public OverrideConfigSupplier(@Nonnull Clock clock,
                                  @Nonnull ConfigSupplier<Message,Field> parent,
                                  @Nonnull Map<String, String> overrides,
                                  boolean strict)
            throws ProvidenceConfigException {
        super(clock);
        synchronized (this) {
            this.parent = parent;
            this.overrides = overrides;
            this.listener = updated -> {
                try {
                    set(buildOverrideConfig(updated, overrides, strict));
                } catch (ProvidenceConfigException e) {
                    throw new UncheckedProvidenceConfigException(e);
                }
            };
            parent.addListener(listener);
            set(buildOverrideConfig(parent.get(), overrides, strict));
        }
    }

    @Override
    public String toString() {
        return String.format("OverrideConfig{[%s], parent=%s}", Strings.join(", ", overrides.keySet()), parent.getName());
    }

    @Override
    public String getName() {
        return String.format("OverrideConfig{[%s]}", Strings.join(", ", overrides.keySet()));
    }

    private static <Message extends PMessage<Message, Field>, Field extends PField>
    Message buildOverrideConfig(Message parent,
                                Map<String,String> overrides,
                                boolean strict) throws ProvidenceConfigException {
        PMessageBuilder<Message, Field> builder = parent.mutate();
        for (Map.Entry<String, String> override : overrides.entrySet()) {
            String[] path = override.getKey()
                                    .split("[.]");

            String fieldName = lastFieldName(path);
            PMessageBuilder containedBuilder = builderForField(strict, builder, path);
            if (containedBuilder == null) {
                continue;
            }
            PField field = containedBuilder.descriptor()
                                           .findFieldByName(fieldName);
            if (field == null) {
                if (strict) {
                    throw new ProvidenceConfigException("No such field %s in %s [%s]",
                                              fieldName,
                                              containedBuilder.descriptor()
                                                              .getQualifiedName(),
                                              String.join(".", path));
                }
                continue;
            }

            if (UNDEFINED.equals(override.getValue())) {
                containedBuilder.clear(field.getId());
            } else {
                containedBuilder.set(field.getId(), readFieldValue(override.getKey(), override.getValue(), field.getDescriptor()));
            }
        }

        return builder.build();
    }

    private static String lastFieldName(String... path) {
        return path[path.length - 1];
    }

    private static PMessageBuilder builderForField(boolean strict, PMessageBuilder builder, String... path) throws ProvidenceConfigException {
        for (int i = 0; i < (path.length - 1); ++i) {
            PMessageDescriptor descriptor = builder.descriptor();
            String fieldName = path[i];
            PField field = descriptor.findFieldByName(fieldName);
            if (field == null) {
                if (strict) {
                    throw new ProvidenceConfigException("No such field %s in %s [%s]",
                                              fieldName,
                                              descriptor.getQualifiedName(),
                                              String.join(".", path));
                }
                return null;
            }
            if (field.getType() != PType.MESSAGE) {
                throw new ProvidenceConfigException("'%s' is not a message field in %s [%s]",
                                          fieldName,
                                          descriptor.getQualifiedName(),
                                          String.join(".", path));
            }
            builder = builder.mutator(field.getId());
        }
        return builder;
    }

    private static Object readFieldValue(String key, String value, PDescriptor descriptor) throws ProvidenceConfigException {
        switch (descriptor.getType()) {
            case BOOL: {
                switch (value.toLowerCase()) {
                    case "1":
                    case "t":
                    case "true":
                    case "y":
                    case "yes":
                        return Boolean.TRUE;
                    case "0":
                    case "f":
                    case "false":
                    case "n":
                    case "no":
                        return Boolean.FALSE;
                }
                throw new ProvidenceConfigException("Invalid bool value " + value + " [" + key + "]");
            }
            case BYTE: {
                try {
                    if (value.startsWith("0x")) {
                        return Byte.parseByte(value.substring(2), 16);
                    } else if (value.startsWith("0")) {
                        return Byte.parseByte(value.substring(1), 8);
                    }
                    return Byte.parseByte(value);
                } catch (NumberFormatException e) {
                    throw new ProvidenceConfigException(e, "Invalid byte value " + value + " [" + key + "]");
                }
            }
            case I16: {
                try {
                    if (value.startsWith("0x")) {
                        return Short.parseShort(value.substring(2), 16);
                    } else if (value.startsWith("0")) {
                        return Short.parseShort(value.substring(1), 8);
                    }
                    return Short.parseShort(value);
                } catch (NumberFormatException e) {
                    throw new ProvidenceConfigException(e, "Invalid i16 value " + value + " [" + key + "]");
                }
            }
            case I32: {
                try {
                    if (value.startsWith("0x")) {
                        return Integer.parseInt(value.substring(2), 16);
                    } else if (value.startsWith("0")) {
                        return Integer.parseInt(value.substring(1), 8);
                    }
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new ProvidenceConfigException(e, "Invalid i32 value " + value + " [" + key + "]");
                }
            }
            case I64: {
                try {
                    if (value.startsWith("0x")) {
                        return Long.parseLong(value.substring(2), 16);
                    } else if (value.startsWith("0")) {
                        return Long.parseLong(value.substring(1), 8);
                    }
                    return Long.parseLong(value);
                } catch (NumberFormatException e) {
                    throw new ProvidenceConfigException(e, "Invalid i64 value " + value + " [" + key + "]");
                }
            }
            case DOUBLE: {
                try {
                   return Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    throw new ProvidenceConfigException(e, "Invalid double value " + value + " [" + key + "]");
                }
            }
            case STRING: {
                return value;
            }
            case BINARY: {
                try {
                    if (value.startsWith("hex(") && value.endsWith(")")) {
                        return Binary.fromHexString(value.substring(4, value.length() - 1));
                    } else if (value.startsWith("b64(") && value.endsWith(")")) {
                        return Binary.fromBase64(value.substring(4, value.length() - 1));
                    }
                    throw new ProvidenceConfigException("Missing binary format " + value + " [" + key + "]");
               } catch (IllegalArgumentException e) {
                    throw new ProvidenceConfigException(e, "Invalid " + value.substring(0, 3) +
                                                           " binary value " + value + " [" + key + "]");
                }
            }
            case ENUM: {
                PEnumDescriptor ed = (PEnumDescriptor) descriptor;
                try {
                    if (Strings.isInteger(value)) {
                        return ed.valueForId(Integer.parseInt(value));
                    } else {
                        return ed.valueForName(value);
                    }
                } catch (IllegalArgumentException e) {
                    throw new ProvidenceConfigException("No " + ed.getQualifiedName() + " value for '" + value + "' [" + key + "]");
                }
            }
            default: {
                throw new ProvidenceConfigException("Overrides not allowed on " + descriptor.getType() + " fields [" + key + "]");
            }
        }
    }

    private static Map<String,String> propertiesMap(Properties properties) {
        Map<String,String> overrides = new TreeMap<>();
        for (String key : properties.stringPropertyNames()) {
            overrides.put(key, properties.getProperty(key));
        }
        return overrides;
    }
}
