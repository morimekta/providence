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

import net.morimekta.config.ConfigException;
import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.serializer.pretty.Token;
import net.morimekta.providence.serializer.pretty.Tokenizer;
import net.morimekta.providence.serializer.pretty.TokenizerException;
import net.morimekta.util.Binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static net.morimekta.providence.config.ProvidenceConfig.UNDEFINED;

/**
 * A supplier of a providence message config based on a parent config
 * (supplier) and a map of value overrides. Handy for use with
 * argument parser overrides, system property overrides or similar.
 *
 * <pre>{@code
 *     Supplier<Service> supplier = new OverrideMessageSupplier<>(
 *             baseServiceConfig,
 *             ImmutableMap.of(
 *                 "db.username", "root",
 *                 "jdbc.driver", "com.oracle.jdbc.Driver"
 *             ));
 * }</pre>
 */
public class OverrideMessageSupplier<Message extends PMessage<Message, Field>, Field extends PField>
        implements ReloadableSupplier<Message> {
    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix. Note that reading from properties
     * are <b>never</b> strict.
     *
     * @param parent The parent message to override values of.
     * @param overrides The message override values.
     * @throws ConfigException If message overriding failed
     */
    public OverrideMessageSupplier(Supplier<Message> parent, Properties overrides) {
        this(parent, propertiesMap(overrides), false);
    }

    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix.
     *
     * @param parent The parent message to override values of.
     * @param overrides The message override values.
     * @throws ConfigException If message overriding failed
     */
    public OverrideMessageSupplier(Supplier<Message> parent, Map<String, String> overrides) {
        this(parent, overrides, false);
    }

    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix.
     *
     * @param parent The parent message to override values of.
     * @param overrides The message override values.
     * @throws ConfigException If message overriding failed
     */
    public OverrideMessageSupplier(Supplier<Message> parent, Map<String, String> overrides, boolean strict) {
        this.parent = parent;
        this.overrides = overrides;
        this.strict = strict;
        this.instance = new AtomicReference<>(loadInternal());
    }

    /**
     * Get the message enclosed in the config wrapper.
     *
     * @return The message.
     */
    @Override
    public Message get() {
        return instance.get();
    }

    /**
     * Reload the message into the config.
     */
    @Override
    public void reload() {
        instance.set(loadInternal());
    }

    private Message loadInternal() {
        PMessageBuilder<Message, Field> builder = parent.get()
                                                        .mutate();
        for (Map.Entry<String, String> override : overrides.entrySet()) {
            String[] path = override.getKey()
                                    .split("[.]");

            String fieldName = lastFieldName(path);
            PMessageBuilder containedBuilder = builderForField(builder, path);
            if (containedBuilder == null) {
                continue;
            }
            PField field = containedBuilder.descriptor()
                                           .findFieldByName(fieldName);
            if (field == null) {
                if (strict) {
                    throw new ConfigException("No such field %s in %s [%s]",
                                              fieldName,
                                              containedBuilder.descriptor()
                                                              .getQualifiedName(),
                                              String.join(".", path));
                }
                continue;
            }

            try {
                Tokenizer tokenizer = new Tokenizer(new ByteArrayInputStream(override.getValue()
                                                                                     .getBytes(StandardCharsets.UTF_8)),
                                                    true);
                if (UNDEFINED.equals(override.getValue())) {
                    containedBuilder.clear(field.getKey());
                } else if (field.getType() == PType.STRING) {
                    if (tokenizer.hasNext()) {
                        Token next = tokenizer.next();
                        if (next.isStringLiteral()) {
                            containedBuilder.set(field.getKey(), next.decodeLiteral(strict));
                            if (tokenizer.hasNext()) {
                                throw new ConfigException("Garbage after string value [%s]: '%s'",
                                                          override.getKey(),
                                                          override.getValue());
                            }
                            continue;
                        }
                    }
                    containedBuilder.set(field.getKey(), override.getValue());
                } else {
                    containedBuilder.set(field.getKey(), readFieldValue(tokenizer, tokenizer.expect("value"), field.getDescriptor()));
                    if (tokenizer.hasNext()) {
                        throw new ConfigException("Garbage after %s value [%s]: '%s'",
                                                  field.getType(),
                                                  override.getKey(),
                                                  override.getValue());
                    }
                }
            } catch (IOException e) {
                throw new ConfigException(e.getMessage() + " [" + override.getKey() + "]", e);
            }
        }

        return builder.build();
    }

    private final AtomicReference<Message> instance;
    private final Supplier<Message>        parent;
    private final Map<String,String>       overrides;
    private final boolean                  strict;

    private String lastFieldName(String... path) {
        return path[path.length - 1];
    }

    private PMessageBuilder builderForField(PMessageBuilder builder, String... path) {
        for (int i = 0; i < (path.length - 1); ++i) {
            PMessageDescriptor descriptor = builder.descriptor();
            String fieldName = path[i];
            PField field = descriptor.findFieldByName(fieldName);
            if (field == null) {
                if (strict) {
                    throw new ConfigException("No such field %s in %s [%s]",
                                              fieldName,
                                              descriptor.getQualifiedName(),
                                              String.join(".", path));
                }
                return null;
            }
            if (field.getType() != PType.MESSAGE) {
                throw new ConfigException("'%s' is not a message field in %s [%s]",
                                          fieldName,
                                          descriptor.getQualifiedName(),
                                          String.join(".", path));
            }
            builder = builder.mutator(field.getKey());
        }
        return builder;
    }

    private Object readFieldValue(Tokenizer tokenizer, Token token, PDescriptor descriptor) throws IOException {
        switch (descriptor.getType()) {
            case BOOL: {
                switch (token.asString().toLowerCase()) {
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
                throw new TokenizerException(token, "Invalid boolean value " + token.asString())
                        .setLine(tokenizer.getLine(token.getLineNo()));

            }
            case BYTE: {
                if (token.isInteger()) {
                    long val = token.parseInteger();
                    if (val > Byte.MAX_VALUE || val < Byte.MIN_VALUE) {
                        throw new TokenizerException(token, "Byte value out of bounds: " + token.asString())
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }
                    return (byte) val;
                } else {
                    throw new TokenizerException(token, "Invalid byte value: " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case I16: {
                if (token.isInteger()) {
                    long val = token.parseInteger();
                    if (val > Short.MAX_VALUE || val < Short.MIN_VALUE) {
                        throw new TokenizerException(token, "Short value out of bounds: " + token.asString())
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }
                    return (short) val;
                } else {
                    throw new TokenizerException(token, "Invalid i16 value: " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case I32: {
                if (token.isInteger()) {
                    long val = token.parseInteger();
                    if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
                        throw new TokenizerException(token, "Integer value out of bounds: " + token.asString())
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }
                    return (int) val;
                } else {
                    throw new TokenizerException(token, "Invalid i32 value: " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case I64: {
                if (token.isInteger()) {
                    return token.parseInteger();
                } else {
                    throw new TokenizerException(token, "Invalid i64 value: " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case DOUBLE: {
                try {
                    return token.parseDouble();
                } catch (NumberFormatException nfe) {
                    throw new TokenizerException(token, "Invalid double value: " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case STRING: {
                if (!token.isStringLiteral()) {
                    throw new TokenizerException(token, "Expected string literal, got '%s'", token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
                return token.decodeLiteral(strict);
            }
            case BINARY: {
                switch (token.asString()) {
                    case "b64": {
                        try {
                            tokenizer.expectSymbol("binary content start", Token.kParamsStart);
                            String content = tokenizer.readBinary(Token.kParamsEnd);
                            return Binary.fromBase64(content);
                        } catch (IllegalArgumentException e) {
                            throw new TokenizerException(e, e.getMessage());
                        }
                    }
                    case "hex": {
                        try {
                            tokenizer.expectSymbol("binary content start", Token.kParamsStart);
                            String content = tokenizer.readBinary(Token.kParamsEnd);
                            return Binary.fromHexString(content);
                        } catch (NumberFormatException e) {
                            throw new TokenizerException(e, "Invalid hex value: " + e.getMessage());
                        }
                    }
                    default:
                        throw new TokenizerException(token, "Unrecognized binary format " + token.asString())
                                .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case ENUM: {
                PEnumBuilder b = ((PEnumDescriptor) descriptor).builder();
                b.setByName(token.asString());
                if (strict && !b.valid()) {
                    throw new TokenizerException(token, "No such " + descriptor.getQualifiedName() + " value " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
                return b.build();
            }
            case MESSAGE: {
                // TODO: Parse messages?
                throw new TokenizerException(token, "Message overrides not allowed")
                        .setLine(tokenizer.getLine(token.getLineNo()));
            }
            case MAP: {
                if (!token.isSymbol(Token.kMessageStart)) {
                    throw new TokenizerException(token, "Expected map start, got '%s'", token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
                @SuppressWarnings("unchecked")
                PMap<Object, Object> pMap = (PMap) descriptor;
                PDescriptor kDesc = pMap.keyDescriptor();
                PDescriptor iDesc = pMap.itemDescriptor();

                PMap.Builder<Object, Object> builder = pMap.builder();

                token = tokenizer.expect("list end or value");
                while (!token.isSymbol(Token.kMessageEnd)) {
                    Object key = readFieldValue(tokenizer, token, kDesc);
                    tokenizer.expectSymbol("map kv sep", Token.kKeyValueSep);
                    Object value = readFieldValue(tokenizer, tokenizer.expect("map value"), iDesc);
                    builder.put(key, value);
                    token = tokenizer.expect("map sep, end or value");
                    if (token.isSymbol(Token.kLineSep1)) {
                        token = tokenizer.expect("map end or value");
                    }
                }
                return builder.build();
            }
            case LIST: {
                if (!token.isSymbol(Token.kListStart)) {
                    throw new TokenizerException(token, "Expected list start, got '%s'", token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
                @SuppressWarnings("unchecked")
                PList<Object> pList = (PList) descriptor;
                PDescriptor iDesc = pList.itemDescriptor();

                PList.Builder<Object> builder = pList.builder();

                token = tokenizer.expect("list end or value");
                while (!token.isSymbol(Token.kListEnd)) {
                    builder.add(readFieldValue(tokenizer, token, iDesc));
                    token = tokenizer.expect("list sep, end or value");
                    if (token.isSymbol(Token.kLineSep1)) {
                        token = tokenizer.expect("list end or value");
                    }
                }

                return builder.build();
            }
            case SET: {
                if (!token.isSymbol(Token.kListStart)) {
                    throw new TokenizerException(token, "Expected set start, got '%s'", token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
                @SuppressWarnings("unchecked")
                PSet<Object> pList = (PSet) descriptor;
                PDescriptor iDesc = pList.itemDescriptor();

                PSet.Builder<Object> builder = pList.builder();

                token = tokenizer.expect("set end or value");
                while (!token.isSymbol(Token.kListEnd)) {
                    builder.add(readFieldValue(tokenizer, token, iDesc));
                    token = tokenizer.expect("set sep, end or value");
                    if (token.isSymbol(Token.kLineSep1)) {
                        token = tokenizer.expect("set end or value");
                    }
                }

                return builder.build();
            }
            default: {
                throw new IllegalStateException("Unhandled field type: " + descriptor.getType());
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
