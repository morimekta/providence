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
package net.morimekta.providence.config.core;

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.config.utils.ProvidenceConfigException;
import net.morimekta.providence.config.utils.UncheckedProvidenceConfigException;
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

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import static net.morimekta.providence.config.ProvidenceConfigParser.UNDEFINED;

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
        extends ConfigSupplier<Message, Field> {
    // Make sure the listener cannot be GC'd as long as this instance
    // survives.
    private final ConfigListener<Message, Field> listener;

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
        synchronized (this) {
            listener = updated -> {
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

            try {
                Tokenizer tokenizer = new Tokenizer(new ByteArrayInputStream(override.getValue()
                                                                                     .getBytes(StandardCharsets.UTF_8)),
                                                    true);
                if (UNDEFINED.equals(override.getValue())) {
                    containedBuilder.clear(field.getId());
                } else if (field.getType() == PType.STRING) {
                    if (tokenizer.hasNext()) {
                        Token next = tokenizer.next();
                        if (next.isStringLiteral()) {
                            containedBuilder.set(field.getId(), next.decodeLiteral(strict));
                            if (tokenizer.hasNext()) {
                                throw new ProvidenceConfigException("Garbage after string value [%s]: '%s'",
                                                                    override.getKey(),
                                                                    override.getValue());
                            }
                            continue;
                        }
                    }
                    containedBuilder.set(field.getId(), override.getValue());
                } else {
                    containedBuilder.set(field.getId(),
                                         readFieldValue(tokenizer,
                                                        tokenizer.expect("value"),
                                                        field.getDescriptor(),
                                                        strict));
                    if (tokenizer.hasNext()) {
                        throw new ProvidenceConfigException("Garbage after %s value [%s]: '%s'",
                                                            field.getType(),
                                                            override.getKey(),
                                                            override.getValue());
                    }
                }
            } catch (ProvidenceConfigException e) {
                throw e;
            } catch (IOException e) {
                throw new ProvidenceConfigException(e.getMessage() + " [" + override.getKey() + "]", e);
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

    private static Object readFieldValue(Tokenizer tokenizer, Token token, PDescriptor descriptor, boolean strict) throws IOException {
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
                    Object key = readFieldValue(tokenizer, token, kDesc, strict);
                    tokenizer.expectSymbol("map kv sep", Token.kKeyValueSep);
                    Object value = readFieldValue(tokenizer, tokenizer.expect("map value"), iDesc, strict);
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
                    builder.add(readFieldValue(tokenizer, token, iDesc, strict));
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
                    builder.add(readFieldValue(tokenizer, token, iDesc, strict));
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
