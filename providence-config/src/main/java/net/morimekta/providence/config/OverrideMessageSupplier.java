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
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.util.pretty.Token;
import net.morimekta.providence.util.pretty.Tokenizer;
import net.morimekta.providence.util.pretty.TokenizerException;
import net.morimekta.util.Binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static net.morimekta.providence.config.ProvidenceConfig.UNDEFINED;

/**
 * A supplier of a providence message config based on a message reader. For reading a simple
 * message from a readable file, use the FileMessageReader with the PrettySerializer:
 *
 * NOTE: The message reader will be closed after every read, so only message readers that
 * can be reset that way can be supported, e.g. {@link net.morimekta.providence.mio.FileMessageReader}.
 *
 * <code>
 *     MessageSupplier supplier = new MessageSupplier(
 *             MyConfig.kDescriptor,
 *             new FileMessageReader(configFile, new PrettySerializer()));
 * </code>
 */
public class OverrideMessageSupplier<Message extends PMessage<Message, Field>, Field extends PField>
        implements ReloadableSupplier<Message> {

    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix.
     *
     * @param parent The parent message to override values of.
     * @param overrides The message override values.
     * @throws IOException If message read failed.
     * @throws SerializerException If message deserialization failed.
     */
    public OverrideMessageSupplier(Supplier<Message> parent,
                                   Map<String,String> overrides) throws IOException {
        this(parent, overrides, false);
    }

    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix.
     *
     * @param parent The parent message to override values of.
     * @param overrides The message override values.
     * @throws IOException If message read failed.
     * @throws SerializerException If message deserialization failed.
     */
    public OverrideMessageSupplier(Supplier<Message> parent,
                                   Map<String,String> overrides,
                                   boolean strict) throws IOException {
        this.parent    = parent;
        this.overrides = overrides;
        this.strict    = strict;
        this.instance  = new AtomicReference<>(loadInternal());
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
        try {
            instance.set(loadInternal());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Message loadInternal() throws IOException {
        PMessageBuilder<Message, Field> builder = parent.get()
                                                        .mutate();
        for (Map.Entry<String, String> override : overrides.entrySet()) {
            String[] path = override.getKey()
                                    .split("[.]");
            Tokenizer tokenizer = new Tokenizer(new ByteArrayInputStream(
                    override.getValue().getBytes(StandardCharsets.UTF_8)), true);

            String fieldName = lastFieldName(path);
            PMessageBuilder containedBuilder = builderForField(builder, path);
            if (containedBuilder == null) {
                continue;
            }
            PField field = containedBuilder.descriptor().getField(fieldName);
            if (field == null) {
                if (strict) {
                    throw new TokenizerException("No such field %s in %s [%s]",
                                                 fieldName,
                                                 containedBuilder.descriptor().getQualifiedName(),
                                                 String.join(".", path));
                }
                continue;
            }
            if (UNDEFINED.equals(override.getValue())) {
                containedBuilder.clear(field.getKey());
            } else if (field.getType() == PType.STRING) {
                if (tokenizer.hasNext()) {
                    Token next = tokenizer.next();
                    if (next.isStringLiteral()) {
                        containedBuilder.set(field.getKey(), next.decodeLiteral());
                        if (tokenizer.hasNext()) {
                            throw new TokenizerException(tokenizer.next(),
                                                         "Garbage after string value")
                                    .setLine(override.getValue());
                        }
                        continue;
                    }
                }
                containedBuilder.set(field.getKey(), override.getValue());
            } else {
                containedBuilder.set(field.getKey(), readFieldValue(tokenizer, tokenizer.expect("value"), field.getDescriptor()));
                if (tokenizer.hasNext()) {
                    throw new TokenizerException(tokenizer.next(),
                                                 "Garbage after %s value",
                                                 field.getDescriptor().getQualifiedName() )
                            .setLine(override.getValue());
                }
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
            PField field = descriptor.getField(fieldName);
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
                throw new ConfigException("Trying to look up field %s in non-message type %s [%s]",
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
            case VOID: {
                // Even void fields needs a value token...
                // Allow any boolean true value that is an _identifier_. No numbers here.
                switch (token.asString().toLowerCase()) {
                    case "t":
                    case "true":
                    case "y":
                    case "yes":
                        return Boolean.TRUE;
                }
                throw new TokenizerException(token, "Invalid void value " + token.asString())
                        .setLine(tokenizer.getLine(token.getLineNo()));
            }
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
                    throw new TokenizerException(token, "Invalid byte value: " + token.asString())
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
                    throw new TokenizerException(token, "Invalid byte value: " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case I64: {
                if (token.isInteger()) {
                    return token.parseInteger();
                } else {
                    throw new TokenizerException(token, "Invalid byte value: " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case DOUBLE: {
                try {
                    return token.parseDouble();
                } catch (NumberFormatException nfe) {
                    throw new TokenizerException(token, "Number format error: " + nfe.getMessage())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case STRING: {
                if (!token.isStringLiteral()) {
                    throw new TokenizerException(token, "Expected string literal, got '%s'", token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
                return token.decodeLiteral();
            }
            case BINARY: {
                tokenizer.expectSymbol("binary content start", Token.kMethodStart);
                String content = tokenizer.readUntil(Token.kMethodEnd, false, false);
                switch (token.asString()) {
                    case "b64":
                        return Binary.fromBase64(content);
                    case "hex":
                        return Binary.fromHexString(content);
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
                throw new TokenizerException(token, "Message overrides not allowed").setLine(tokenizer.getLine(token.getLineNo()));
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
}
