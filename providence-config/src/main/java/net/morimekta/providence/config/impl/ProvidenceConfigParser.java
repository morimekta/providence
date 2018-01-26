/*
 * Copyright 2017 Providence Authors
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

import com.google.common.collect.ImmutableSet;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.config.ProvidenceConfigException;
import net.morimekta.providence.config.impl.ProvidenceConfigUtil.Stage;
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
import net.morimekta.providence.util.TypeRegistry;
import net.morimekta.util.Binary;
import net.morimekta.util.Pair;
import net.morimekta.util.io.Utf8StreamReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.AS;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.DEF;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.DEFINE_REFERENCE;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.FALSE;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.IDENTIFIER_SEP;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.INCLUDE;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.RESERVED_WORDS;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.TRUE;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.UNDEFINED;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asType;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.canonicalFileLocation;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.consumeValue;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.nextNotLineSep;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.readCanonicalPath;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.resolveFile;

/**
 * This parser parses config files. The class in itself should be stateless, so
 * can safely be used in multiple threads safely. This is a utility class created
 * in order to simplify testing.
 */
public class ProvidenceConfigParser {
    /**
     * Create a providence config parser instance.
     *
     * @param registry The type registry used.
     * @param strict If config should be parsed and handled strictly.
     */
    public ProvidenceConfigParser(TypeRegistry registry, boolean strict) {
        this.registry = registry;
        this.strict = strict;
    }

    /**
     * Parse a providence config into a message.
     *
     * @param configFile The config file to be parsed.
     * @param parent The parent config message.
     * @param <M> The config message type.
     * @param <F> The config field type.
     * @return Pair of parsed config and set of included file paths.
     * @throws ProvidenceConfigException If parsing failed.
     */
    @Nonnull
    <M extends PMessage<M, F>, F extends PField> Pair<M, Set<String>> parseConfig(@Nonnull Path configFile, @Nullable M parent)
            throws ProvidenceConfigException {
        try {
            configFile = canonicalFileLocation(configFile);
        } catch (IOException e) {
            throw new ProvidenceConfigException(e, "Unable to resolve config file " + configFile).setFile(configFile.getFileName()
                                                                                                                    .toString());
        }
        Pair<M, Set<String>> result = checkAndParseInternal(configFile, parent);
        if (result == null) {
            throw new ProvidenceConfigException("No config: " + configFile.toString()).setFile(configFile.getFileName()
                                                                                                         .toString());
        }
        return result;
    }

    // --- private

    private <M extends PMessage<M, F>, F extends PField> Pair<M, Set<String>> checkAndParseInternal(@Nonnull Path configFile,
                                                                                                    @Nullable M parent,
                                                                                                    String... includeStack) throws ProvidenceConfigException {
        try {
            // So we map actual loaded files by the absolute canonical location.
            String canonicalFile = readCanonicalPath(configFile).toString();
            List<String> stackList = new ArrayList<>();
            Collections.addAll(stackList, includeStack);

            if (Arrays.binarySearch(includeStack, canonicalFile) >= 0) {
                stackList.add(canonicalFile);
                throw new ProvidenceConfigException("Circular includes detected: " + String.join(
                        " -> ",
                        stackList.stream()
                                 .map(p -> new File(p).getName())
                                 .collect(Collectors.toList())));
            }

            stackList.add(canonicalFile);

            return parseConfigRecursively(configFile, parent, stackList.toArray(new String[stackList.size()]));
        } catch (IOException e) {
            if (e instanceof ProvidenceConfigException) {
                ProvidenceConfigException pce = (ProvidenceConfigException) e;
                if (pce.getFile() == null) {
                    pce.setFile(configFile.getFileName()
                                          .toString());
                }
                throw pce;
            }
            if (e instanceof TokenizerException) {
                TokenizerException te = (TokenizerException) e;
                if (te.getFile() == null) {
                    te.setFile(configFile.getFileName()
                                         .toString());
                }
                throw new ProvidenceConfigException(te);
            }
            throw new ProvidenceConfigException(e, e.getMessage()).setFile(configFile.getFileName()
                                                                                     .toString());
        }
    }

    @SuppressWarnings("unchecked")
    <M extends PMessage<M, F>, F extends PField> Pair<M, Set<String>> parseConfigRecursively(@Nonnull Path file,
                                                                                             M parent,
                                                                                             String[] stack)
            throws IOException {
        Tokenizer tokenizer;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file.toFile()))) {
            // Non-enclosed content, meaning we should read the whole file immediately.
            tokenizer = new Tokenizer(new Utf8StreamReader(in), Tokenizer.DEFAULT_BUFFER_SIZE, true);
        }

        ProvidenceConfigContext context = new ProvidenceConfigContext();
        Set<String> includedFilePaths = new TreeSet<>();
        includedFilePaths.add(canonicalFileLocation(file).toString());

        Stage lastStage = Stage.INCLUDES;
        M result = null;

        Token token = tokenizer.peek();
        while (token != null) {
            tokenizer.next();

            if (lastStage == Stage.MESSAGE) {
                throw new TokenizerException(token, "Unexpected token '" + token.asString() + "', expected end of file.")
                        .setLine(tokenizer.getLine());
            } else if (INCLUDE.equals(token.asString())) {
                // if include && stage == INCLUDES --> INCLUDES
                if (lastStage != Stage.INCLUDES) {
                    throw new TokenizerException(token,
                                                 "Include added after defines or message. Only one def block allowed.")
                            .setLine(tokenizer.getLine());
                }
                token = tokenizer.expectLiteral("file to be included");
                String includedFilePath = token.decodeLiteral(strict);
                PMessage included;
                Path includedFile;
                try {
                    includedFile = resolveFile(file, includedFilePath);
                    Pair<PMessage, Set<String>> tmp = checkAndParseInternal(includedFile, null, stack);
                    if (tmp != null) {
                        includedFilePaths.add(includedFile.toString());
                        includedFilePaths.addAll(tmp.second);
                        included = tmp.first;
                    } else {
                        included = null;
                    }
                } catch (FileNotFoundException e) {
                    throw new TokenizerException(token, "Included file \"%s\" not found.", includedFilePath)
                            .setLine(tokenizer.getLine());
                }
                token = tokenizer.expectIdentifier("the token 'as'");
                if (!AS.equals(token.asString())) {
                    throw new TokenizerException(token,
                                                 "Expected token 'as' after included file \"%s\".",
                                                 includedFilePath)
                            .setLine(tokenizer.getLine());
                }
                token = tokenizer.expectIdentifier("Include alias");
                String alias = token.asString();
                if (RESERVED_WORDS.contains(alias)) {
                    throw new TokenizerException(token, "Alias \"%s\" is a reserved word.", alias)
                            .setLine(tokenizer.getLine());
                }
                if (context.containsReference(alias)) {
                    throw new TokenizerException(token, "Alias \"%s\" is already used.", alias)
                            .setLine(tokenizer.getLine());
                }
                context.setInclude(alias, included);
            } else if (DEF.equals(token.asString())) {
                // if params && stage == DEF --> DEF
                lastStage = Stage.DEFINES;
                parseDefinitions(context, tokenizer);
            } else if (token.isQualifiedIdentifier()) {
                // if a.b (type identifier) --> MESSAGE
                lastStage = Stage.MESSAGE;
                PMessageDescriptor<M, F> descriptor;
                try {
                    descriptor = (PMessageDescriptor) registry.getDeclaredType(token.asString());
                } catch (IllegalArgumentException e) {
                    // Unknown declared type. Fail if:
                    // - strict mode, all files must be of known types.
                    // - top of the stack. This is the config requested by the user. It should fail
                    //   even in non-strict mode.
                    if (strict || stack.length == 1) {
                        throw new TokenizerException(token, "Unknown declared type: %s", token.asString())
                                .setLine(tokenizer.getLine());
                    }
                    return null;
                }
                result = parseConfigMessage(tokenizer, context, descriptor.builder(), parent, file);
            } else {
                throw new TokenizerException(token,
                                             "Unexpected token '" + token.asString() +
                                             "'. Expected include, defines or message type")
                        .setLine(tokenizer.getLine());
            }

            token = tokenizer.peek();
        }

        if (result == null) {
            throw new TokenizerException("No message in config: " + file.getFileName().toString());
        }

        return Pair.create(result, includedFilePaths);
    }

    @SuppressWarnings("unchecked")
    void parseDefinitions(ProvidenceConfigContext context, Tokenizer tokenizer) throws IOException {
        Token token = tokenizer.expect("defines group start or identifier");
        if (token.isIdentifier()) {
            String name = context.initReference(token, tokenizer);
            tokenizer.expectSymbol("def value sep", Token.kFieldValueSep);
            context.setReference(name, parseDefinitionValue(context, tokenizer));
        } else if (token.isSymbol(Token.kMessageStart)) {
            token = tokenizer.expect("define or end");
            while (!token.isSymbol(Token.kMessageEnd)) {
                if (!token.isIdentifier()) {
                    throw new TokenizerException(token, "Token '%s' is not valid reference name.", token.asString())
                            .setLine(tokenizer.getLine());
                }
                String name = context.initReference(token, tokenizer);
                tokenizer.expectSymbol("def value sep", Token.kFieldValueSep);
                context.setReference(name, parseDefinitionValue(context, tokenizer));
                token = tokenizer.expect("next define or end");
            }
        } else {
            throw new TokenizerException(token, "Unexpected token after def: '%s'", token.asString())
                    .setLine(tokenizer.getLine());
        }
    }

    @SuppressWarnings("unchecked")
    Object parseDefinitionValue(ProvidenceConfigContext context, Tokenizer tokenizer) throws IOException {
        Token token = tokenizer.expect("Start of def value");

        if (token.isReal()) {
            return Double.parseDouble(token.asString());
        } else if (token.isInteger()) {
            return Long.parseLong(token.asString());
        } else if (token.isStringLiteral()) {
            return token.decodeLiteral(strict);
        } else if (TRUE.equalsIgnoreCase(token.asString())) {
            return Boolean.TRUE;
        } else if (FALSE.equalsIgnoreCase(token.asString())) {
            return Boolean.FALSE;
        } else if (Token.B64.equals(token.asString())) {
            tokenizer.expectSymbol("binary data enclosing start", Token.kParamsStart);
            return Binary.fromBase64(tokenizer.readBinary(Token.kParamsEnd));
        } else if (Token.HEX.equals(token.asString())) {
            tokenizer.expectSymbol("binary data enclosing start", Token.kParamsStart);
            return Binary.fromHexString(tokenizer.readBinary(Token.kParamsEnd));
        } else if (token.isDoubleQualifiedIdentifier()) {
            // this may be an enum reference, must be
            // - package.EnumType.IDENTIFIER

            String id = token.asString();
            int l = id.lastIndexOf(Token.kIdentifierSep);
            try {
                // These extra casts needs to be there, otherwise we'd get this error:
                // incompatible types: inference variable T has incompatible upper bounds
                // net.morimekta.providence.descriptor.PDeclaredDescriptor<net.morimekta.providence.descriptor.PEnumDescriptor>,
                // net.morimekta.providence.descriptor.PEnumDescriptor
                // TODO: Figure out a way to fix the generic cast.
                PEnumDescriptor ed = (PEnumDescriptor) (Object) registry.getDeclaredType(id.substring(0, l));
                PEnumValue val = ed.findByName(id.substring(l + 1));
                if (val == null && strict) {
                    throw new TokenizerException(token, "Unknown %s value: %s", id.substring(0, l), id.substring(l + 1))
                            .setLine(tokenizer.getLine());
                }
                // Note that unknown enum value results in null. Therefore we don't catch null values here.
                return val;
            } catch (IllegalArgumentException e) {
                // No such declared type.
                if (strict) {
                    throw new TokenizerException(token, "Unknown enum identifier: %s", id.substring(0, l))
                            .setLine(tokenizer.getLine());
                }
                consumeValue(context, tokenizer, token);
            } catch (ClassCastException e) {
                // Not an enum.
                throw new TokenizerException(token, "Identifier " + id + " does not reference an enum, from " + token.asString())

                        .setLine(tokenizer.getLine());
            }
        } else if (token.isQualifiedIdentifier()) {
            // Message type.
            PMessageDescriptor descriptor;
            try {
                // These extra casts needs to be there, otherwise we'd get this error:
                // incompatible types: inference variable T has incompatible upper bounds
                // net.morimekta.providence.descriptor.PDeclaredDescriptor<net.morimekta.providence.descriptor.PEnumDescriptor>,
                // net.morimekta.providence.descriptor.PEnumDescriptor
                // TODO: Figure out a way to fix the generic cast.
                descriptor = (PMessageDescriptor) (Object) registry.getDeclaredType(token.asString());
            } catch (IllegalArgumentException e) {
                // Unknown declared type. Fail if:
                // - strict mode: all types must be known.
                if (strict) {
                    throw new TokenizerException(token, "Unknown declared type: %s", token.asString())
                            .setLine(tokenizer.getLine());
                }
                consumeValue(context, tokenizer, token);
                return null;
            }
            PMessageBuilder builder = descriptor.builder();
            if (tokenizer.expectSymbol("message start or inherits", '{', ':') == ':') {
                token = tokenizer.expect("inherits reference");
                PMessage inheritsFrom = resolve(context, token, tokenizer, descriptor);
                if (inheritsFrom == null) {
                    throw new TokenizerException(token, "Inheriting from null reference: %s", token.asString())
                            .setLine(tokenizer.getLine());
                }

                builder.merge(inheritsFrom);
                tokenizer.expectSymbol("message start", '{');
            }

            return parseMessage(tokenizer, context, builder);
        } else {
            throw new TokenizerException(token, "Invalid define value " + token.asString())
                    .setLine(tokenizer.getLine());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <M extends PMessage<M, F>, F extends PField> M parseConfigMessage(Tokenizer tokenizer,
                                                                              ProvidenceConfigContext context,
                                                                              PMessageBuilder<M, F> builder,
                                                                              M parent,
                                                                              Path file) throws IOException {
        if (tokenizer.expectSymbol("extension marker", Token.kKeyValueSep, Token.kMessageStart) == Token.kKeyValueSep) {
            Token token = tokenizer.expect("extension object");

            if (parent != null) {
                throw new TokenizerException(token, "Config in '" + file.getFileName().toString() + "' has both defined parent and inherits from")
                        .setLine(tokenizer.getLine())
                        .setFile(file.getFileName().toString());
            }

            if (token.isReferenceIdentifier()) {
                try {
                    builder.merge(resolveRequired(context, token, tokenizer, builder.descriptor()));
                } catch (ClassCastException e) {
                    throw new TokenizerException(token, "Config type mismatch, expected  ")
                            .setLine(tokenizer.getLine());
                } catch (ProvidenceConfigException e) {
                    throw new TokenizerException(token, e.getMessage())
                            .setLine(tokenizer.getLine());
                }
                tokenizer.expectSymbol("object begin", Token.kMessageStart);
            } else {
                throw new TokenizerException(token,
                                             "Unexpected token " + token.asString() + ", expected reference identifier").setLine(tokenizer.getLine());
            }
        } else if (parent != null) {
            if (!builder.descriptor().equals(parent.descriptor())) {
                throw new ProvidenceConfigException("Loaded config type %s does not match parent %s",
                                                    parent.descriptor().getQualifiedName(),
                                                    builder.descriptor().getQualifiedName());
            }
            builder.merge(parent);
        }

        return parseMessage(tokenizer, context, builder);
    }

    @SuppressWarnings("unchecked")
    <M extends PMessage<M, F>, F extends PField>
    M parseMessage(@Nonnull Tokenizer tokenizer,
                   @Nonnull ProvidenceConfigContext context,
                   @Nonnull PMessageBuilder<M, F> builder) throws IOException {
        PMessageDescriptor<M, F> descriptor = builder.descriptor();

        Token token = tokenizer.expect("object end or field");
        while (!token.isSymbol(Token.kMessageEnd)) {
            if (!token.isIdentifier()) {
                throw new TokenizerException(token, "Invalid field name: " + token.asString())
                        .setLine(tokenizer.getLine());
            }

            F field = descriptor.findFieldByName(token.asString());
            if (field == null) {
                if (strict) {
                    throw new TokenizerException("No such field " + token.asString() + " in " + descriptor.getQualifiedName())
                            .setLine(tokenizer.getLine());
                } else {
                    token = tokenizer.expect("field value sep, message start or reference start");
                    if (token.isSymbol(DEFINE_REFERENCE)) {
                        context.setReference(
                                context.initReference(tokenizer.expectIdentifier("reference name"), tokenizer),
                                null);
                        // Ignore reference.
                        token = tokenizer.expect("field value sep or message start");
                    }
                    if (token.isSymbol(Token.kFieldValueSep)) {
                        token = tokenizer.expect("value declaration");
                    } else if (!token.isSymbol(Token.kMessageStart)) {
                        throw new TokenizerException(token, "Expected field-value separator or inherited message")
                                .setLine(tokenizer.getLine());
                    }
                    // Non-strict will just consume unknown fields, this way
                    // we can be forward-compatible when reading config.
                    consumeValue(context, tokenizer, token);
                    token = nextNotLineSep(tokenizer, "field or message end");
                    continue;
                }
            }

            if (field.getType() == PType.MESSAGE) {
                // go recursive with optional
                String reference = null;
                char symbol = tokenizer.expectSymbol("Message assigner or start",
                                                     Token.kFieldValueSep,
                                                     Token.kMessageStart,
                                                     DEFINE_REFERENCE);
                if (symbol == DEFINE_REFERENCE) {
                    Token ref = tokenizer.expectIdentifier("reference name");
                    if (strict) {
                        throw tokenizer.failure(ref, "Reusable objects are not allowed in strict mode.");
                    }
                    reference = context.initReference(ref, tokenizer);
                    symbol = tokenizer.expectSymbol("Message assigner or start after " + reference,
                                                    Token.kFieldValueSep,
                                                    Token.kMessageStart);
                }

                PMessageBuilder bld;
                if (symbol == Token.kFieldValueSep) {
                    token = tokenizer.expect("reference or message start");
                    if (UNDEFINED.equals(token.asString())) {
                        // unset.
                        builder.clear(field.getId());
                        context.setReference(reference, null);

                        // special casing this, as we don't want to duplicate the parse line below.
                        token = nextNotLineSep(tokenizer, "field or message end");
                        continue;
                    }
                    // overwrite with new.
                    bld = ((PMessageDescriptor) field.getDescriptor()).builder();
                    if (token.isReferenceIdentifier()) {
                        // Inherit from reference.
                        try {
                            PMessage ref = resolve(context, token, tokenizer, field.getDescriptor());
                            if (ref != null) {
                                bld.merge(ref);
                            } else {
                                if (tokenizer.peek().isSymbol(Token.kMessageStart)) {
                                    throw new TokenizerException(token, "Inherit from unknown reference %s", token.asString())
                                            .setLine(tokenizer.getLine());
                                } else if (strict) {
                                    throw new TokenizerException(token, "Unknown reference %s", token.asString())
                                            .setLine(tokenizer.getLine());
                                }
                            }
                        } catch (ProvidenceConfigException e) {
                            throw new TokenizerException(token, "Unknown inherited reference '%s'", token.asString())
                                    .setLine(tokenizer.getLine());
                        }

                        token = tokenizer.expect("after message reference");
                        // if the following symbol is *not* message start,
                        // we assume a new field or end of current message.
                        if (!token.isSymbol(Token.kMessageStart)) {
                            builder.set(field.getId(), context.setReference(reference, bld.build()));
                            continue;
                        }
                    } else if (!token.isSymbol(Token.kMessageStart)) {
                        throw new TokenizerException(token,
                                                     "Unexpected token " + token.asString() +
                                                     ", expected message start").setLine(tokenizer.getLine());
                    }
                } else {
                    // extend in-line.
                    bld = builder.mutator(field.getId());
                }
                builder.set(field.getId(), context.setReference(reference, parseMessage(tokenizer, context, bld)));
            } else if (field.getType() == PType.MAP) {
                // maps can be extended the same way as
                token = tokenizer.expect("field sep or value start");
                Map baseValue = new LinkedHashMap<>();
                String reference = null;
                if (token.isSymbol(DEFINE_REFERENCE)) {
                    Token ref = tokenizer.expectIdentifier("reference name");
                    if (strict) {
                        throw tokenizer.failure(ref, "Reusable objects are not allowed in strict mode.");
                    }
                    reference = context.initReference(ref, tokenizer);
                    token = tokenizer.expect("field sep or value start");
                }

                if (token.isSymbol(Token.kFieldValueSep)) {
                    token = tokenizer.expect("field id or start");
                    if (UNDEFINED.equals(token.asString())) {
                        builder.clear(field.getId());
                        context.setReference(reference, null);

                        token = tokenizer.expect("message end or field");
                        continue;
                    } else if (token.isReferenceIdentifier()) {
                        try {
                            baseValue = resolve(context, token, tokenizer, field.getDescriptor());
                        } catch (ProvidenceConfigException e) {
                            throw new TokenizerException(token, e.getMessage())
                                    .setLine(tokenizer.getLine());
                        }

                        token = tokenizer.expect("map start or next field");
                        if (!token.isSymbol(Token.kMessageStart)) {
                            builder.set(field.getId(), context.setReference(reference, baseValue));
                            continue;
                        } else if (baseValue == null) {
                            baseValue = new LinkedHashMap<>();
                        }
                    }
                } else {
                    baseValue.putAll(builder.build().get(field.getId()));
                }

                if (!token.isSymbol(Token.kMessageStart)) {
                    throw new TokenizerException(token, "Expected map start, but got '%s'", token.asString())
                            .setLine(tokenizer.getLine());
                }
                Map map = parseMapValue(tokenizer, context, (PMap) field.getDescriptor(), baseValue);
                builder.set(field.getId(), context.setReference(reference, map));
            } else {
                String reference = null;
                // Simple fields *must* have the '=' separation, may have '&' reference.
                if (tokenizer.expectSymbol("field value sep", Token.kFieldValueSep, DEFINE_REFERENCE) ==
                    DEFINE_REFERENCE) {
                    Token ref = tokenizer.expectIdentifier("reference name");
                    if (strict) {
                        throw tokenizer.failure(ref, "Reusable objects are not allowed in strict mode.");
                    }
                    reference = context.initReference(ref, tokenizer);
                    tokenizer.expectSymbol("field value sep", Token.kFieldValueSep);
                }
                token = tokenizer.expect("field value");
                if (UNDEFINED.equals(token.asString())) {
                    builder.clear(field.getId());
                    context.setReference(reference, null);
                } else {
                    Object value = parseFieldValue(token, tokenizer, context, field.getDescriptor(), strict);
                    builder.set(field.getId(), context.setReference(reference, value));
                }
            }

            token = nextNotLineSep(tokenizer, "field or message end");
        }

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    Map parseMapValue(Tokenizer tokenizer,
                             ProvidenceConfigContext context,
                             PMap descriptor,
                             Map builder) throws IOException {
        Token next = tokenizer.expect("map key or end");
        while (!next.isSymbol(Token.kMessageEnd)) {
            Object key = parseFieldValue(next, tokenizer, context, descriptor.keyDescriptor(), true);
            tokenizer.expectSymbol("map key value sep", Token.kKeyValueSep);
            next = tokenizer.expect("map value");
            if (UNDEFINED.equals(next.asString())) {
                builder.remove(key);
            } else {
                Object value;
                if (context.containsReference(next.asString())) {
                    value = context.getReference(next.asString(), next, tokenizer);
                } else {
                    value = parseFieldValue(next, tokenizer, context, descriptor.itemDescriptor(), strict);
                }

                if (value != null) {
                    builder.put(key, value);
                }
            }
            // maps do *not* require separator, but allows ',' separator, and separator after last.
            next = tokenizer.expect("map key, end or sep");
            if (next.isSymbol(Token.kLineSep1)) {
                next = tokenizer.expect("map key or end");
            }
        }

        return descriptor.builder().putAll(builder).build();
    }

    @SuppressWarnings("unchecked")
    Object parseFieldValue(Token next,
                           Tokenizer tokenizer,
                           ProvidenceConfigContext context,
                           PDescriptor descriptor,
                           boolean requireEnumValue) throws IOException {
        try {
            switch (descriptor.getType()) {
                case BOOL:
                    if (TRUE.equals(next.asString())) {
                        return true;
                    } else if (FALSE.equals(next.asString())) {
                        return false;
                    } else if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    }
                    break;
                case BYTE:
                    if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    } else if (next.isInteger()) {
                        return (byte) next.parseInteger();
                    }
                    break;
                case I16:
                    if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    } else if (next.isInteger()) {
                        return (short) next.parseInteger();
                    }
                    break;
                case I32:
                    if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    } else if (next.isInteger()) {
                        return (int) next.parseInteger();
                    }
                    break;
                case I64:
                    if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    } else if (next.isInteger()) {
                        return next.parseInteger();
                    }
                    break;
                case DOUBLE:
                    if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    } else if (next.isInteger() || next.isReal()) {
                        return next.parseDouble();
                    }
                    break;
                case STRING:
                    if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    } else if (next.isStringLiteral()) {
                        return next.decodeLiteral(strict);
                    }
                    break;
                case BINARY:
                    if (Token.B64.equals(next.asString())) {
                        tokenizer.expectSymbol("binary data enclosing start", Token.kParamsStart);
                        return Binary.fromBase64(tokenizer.readBinary(Token.kParamsEnd));
                    } else if (Token.HEX.equals(next.asString())) {
                        tokenizer.expectSymbol("binary data enclosing start", Token.kParamsStart);
                        return Binary.fromHexString(tokenizer.readBinary(Token.kParamsEnd));
                    } else if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    }
                    break;
                case ENUM: {
                    PEnumDescriptor ed = (PEnumDescriptor) descriptor;
                    PEnumValue value;
                    String name = next.asString();
                    if (next.isInteger()) {
                        value = ed.findById((int) next.parseInteger());
                    } else if (next.isIdentifier()) {
                        value = ed.findByName(name);
                        if (value == null && context.containsReference(name)) {
                            value = resolve(context, next, tokenizer, ed);
                        }
                    } else if (next.isReferenceIdentifier()) {
                        value = resolve(context, next, tokenizer, descriptor);
                    } else {
                        break;
                    }
                    if (value == null && (strict || requireEnumValue)) {
                        PEnumValue option = null;
                        if (next.isIdentifier()) {
                            for (PEnumValue o : ed.getValues()) {
                                if (o.getName().equalsIgnoreCase(name)) {
                                    option = o;
                                    break;
                                }
                            }
                        }
                        if (option != null) {
                            throw new TokenizerException(next, "No such enum value '%s' for %s, did you mean '%s'?",
                                                         name,
                                                         ed.getQualifiedName(),
                                                         option.getName())
                                    .setLine(tokenizer.getLine());
                        }

                        throw new TokenizerException(next, "No such enum value '%s' for %s.",
                                                     name,
                                                     ed.getQualifiedName())
                                .setLine(tokenizer.getLine());
                    }
                    return value;
                }
                case MESSAGE:
                    if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    } else if (next.isSymbol(Token.kMessageStart)) {
                        return parseMessage(tokenizer, context, ((PMessageDescriptor) descriptor).builder());
                    }
                    break;
                case MAP: {
                    if (next.isReferenceIdentifier()) {
                        Map resolved;
                        try {
                            // Make sure the reference is to a map.
                            resolved = resolve(context, next, tokenizer, descriptor);
                        } catch (ClassCastException e) {
                            throw new TokenizerException(next, "Reference %s is not a map field ", next.asString()).setLine(tokenizer.getLine());
                        }
                        return resolved;
                    } else if (next.isSymbol(Token.kMessageStart)) {
                        return parseMapValue(tokenizer, context, (PMap) descriptor, new LinkedHashMap());
                    }
                    break;
                }
                case SET: {
                    if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    } else if (next.isSymbol(Token.kListStart)) {
                        @SuppressWarnings("unchecked")
                        PSet<Object> ct = (PSet) descriptor;
                        Set<Object> value = new LinkedHashSet<>();

                        next = tokenizer.expect("set value or end");
                        while (!next.isSymbol(Token.kListEnd)) {
                            Object item = parseFieldValue(next, tokenizer, context, ct.itemDescriptor(), strict);
                            if (item != null) {
                                value.add(item);
                            }
                            // sets require separator, and allows separator after last.
                            if (tokenizer.expectSymbol("set separator or end", Token.kLineSep1, Token.kListEnd) == Token.kListEnd) {
                                break;
                            }
                            next = tokenizer.expect("set value or end");
                        }

                        return ct.builder()
                                 .addAll(value)
                                 .build();
                    }
                    break;
                }
                case LIST: {
                    if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    } else if (next.isSymbol(Token.kListStart)) {
                        @SuppressWarnings("unchecked")
                        PList<Object> ct = (PList) descriptor;
                        PList.Builder<Object> builder = ct.builder();

                        next = tokenizer.expect("list value or end");
                        while (!next.isSymbol(Token.kListEnd)) {
                            Object item = parseFieldValue(next, tokenizer, context, ct.itemDescriptor(), strict);
                            if (item != null) {
                                builder.add(item);
                            }
                            // lists require separator, and allows separator after last.
                            if (tokenizer.expectSymbol("list separator or end", Token.kLineSep1, Token.kListEnd) == Token.kListEnd) {
                                break;
                            }
                            next = tokenizer.expect("list value or end");
                        }

                        return builder.build();
                    }
                    break;
                }
                default: {
                    throw new TokenizerException(next, descriptor.getType() + " not supported!").setLine(tokenizer.getLine());
                }
            }
        } catch (ProvidenceConfigException e) {
            throw new TokenizerException(next, e.getMessage()).setLine(tokenizer.getLine());
        }

        throw new TokenizerException(next, "Unhandled value \"%s\" for type %s",
                                     next.asString(),
                                     descriptor.getType()).setLine(tokenizer.getLine());
    }

    @Nonnull
    private static <V> V resolveRequired(ProvidenceConfigContext context, Token token, Tokenizer tokenizer, PDescriptor descriptor) throws TokenizerException {
        V result = resolve(context, token, tokenizer, descriptor);
        if (result == null) {
            throw new TokenizerException("Nu");
        }
        return result;
    }

    /**
     * Resolve a value reference.
     *
     * @param context The parsing context.
     * @param token The ID token to look for.
     * @param tokenizer The tokenizer.
     * @param descriptor The item descriptor.
     * @return The value at the given key, or exception if not found.
     */
    @SuppressWarnings("unchecked")
    static <V> V resolve(ProvidenceConfigContext context, Token token, Tokenizer tokenizer, PDescriptor descriptor) throws TokenizerException {
        Object value = resolveAny(context, token, tokenizer);
        if (value == null) {
            return null;
        }
        return (V) asType(descriptor, value);
    }

    private static Object resolveAny(ProvidenceConfigContext context, Token token, Tokenizer tokenizer)
            throws TokenizerException {
        String key = token.asString();

        String name = key;
        String subKey = null;

        if (key.contains(IDENTIFIER_SEP)) {
            int idx = key.indexOf(IDENTIFIER_SEP);
            name = key.substring(0, idx);
            subKey = key.substring(idx + 1);
        }

        Object value = context.getReference(name, token, tokenizer);
        if (subKey != null) {
            if (!(value instanceof PMessage)) {
                throw new TokenizerException(token, "Reference name " + key + " not declared");
            }
            try {
                return ProvidenceConfigUtil.getInMessage((PMessage) value, subKey, null);
            } catch (ProvidenceConfigException e) {
                throw new TokenizerException(token, e.getMessage()).setLine(tokenizer.getLine())
                                                                   .initCause(e);
            }
        }
        return value;
    }

    /**
     * Type registry for looking up the base config types.
     */
    private final TypeRegistry registry;

    /**
     * If config should be parsed strictly.
     */
    private final boolean strict;
}
