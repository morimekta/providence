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

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.config.ProvidenceConfigException;
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

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asBoolean;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asDouble;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asInteger;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asLong;
import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.asString;

/**
 * This parser parses config files. The class in itself should be stateless, so
 * can safely be used in multiple threads safely. This is a utility class created
 * in order to simplify testing.
 */
public class ProvidenceConfigParser {
    /**
     * Simple stage separation. The content *must* come in this order.
     */
    private enum Stage {
        INCLUDES,
        DEFINES,
        MESSAGE
    }

    public ProvidenceConfigParser(TypeRegistry registry, boolean strict) {
        this.registry = registry;
        this.strict = strict;
    }

    @Nonnull
    <M extends PMessage<M, F>, F extends PField>
    Pair<M, Set<String>> parseConfig(@Nonnull File configFile, @Nullable M parent) throws ProvidenceConfigException {
        configFile = absoluteCanonical(configFile);
        Pair<M, Set<String>> result = checkAndParseInternal(configFile, parent);
        if (result == null) {
            throw new IllegalArgumentException("No config: " + configFile.toString());
        }
        return result;
    }

    private File absoluteCanonical(File f) throws ProvidenceConfigException {
        try {
            return f.getAbsoluteFile().getCanonicalFile();
        } catch (IOException e) {
            throw new ProvidenceConfigException(e, "Unable to resolve config file " + f);
        }
    }

    // --- private

    private <M extends PMessage<M, F>, F extends PField>
    Pair<M, Set<String>> checkAndParseInternal(@Nonnull File configFile,
                                               @Nullable M parent,
                                               String... includeStack) throws ProvidenceConfigException {
        try {
            configFile = configFile.getAbsoluteFile()
                                   .getCanonicalFile();

            String filePath = configFile.toString();
            List<String> stackList = new ArrayList<>();
            Collections.addAll(stackList, includeStack);

            if (Arrays.binarySearch(includeStack, filePath) >= 0) {
                stackList.add(filePath);
                throw new ProvidenceConfigException("Circular includes detected: " +
                                                    String.join(" -> ",
                                                                stackList.stream()
                                                                         .map(p -> new File(p).getName())
                                                                         .collect(Collectors.toList())));
            }

            stackList.add(filePath);

            return parseConfigRecursively(configFile, parent, stackList.toArray(new String[stackList.size()]));
        } catch (IOException e) {
            if (e instanceof ProvidenceConfigException) {
                ProvidenceConfigException pce = (ProvidenceConfigException) e;
                if (pce.getFile() == null) {
                    pce.setFile(configFile.getName());
                }
                throw pce;
            }
            if (e instanceof TokenizerException) {
                TokenizerException te = (TokenizerException) e;
                if (te.getFile() == null) {
                    te.setFile(configFile.getName());
                }
                throw new ProvidenceConfigException(te);
            }
            ProvidenceConfigException ex = new ProvidenceConfigException(e, e.getMessage());
            ex.setFile(configFile.getName());
            throw ex;
        }
    }

    @SuppressWarnings("unchecked")
    private <M extends PMessage<M, F>, F extends PField>
    Pair<M, Set<String>> parseConfigRecursively(@Nonnull File file,
                                               M parent,
                                               String[] stack) throws IOException {
        Tokenizer tokenizer;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            // Non-enclosed content, meaning we should read the whole file immediately.
            tokenizer = new Tokenizer(new Utf8StreamReader(in), Tokenizer.DEFAULT_BUFFER_SIZE, true);
        }

        ProvidenceConfigContext context = new ProvidenceConfigContext();
        Set<String> includedFilePaths = new TreeSet<>();
        includedFilePaths.add(file.toString());

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
                    throw new TokenizerException(token, "Include added after defines or message. Only one def block allowed.")
                            .setLine(tokenizer.getLine());
                }
                token = tokenizer.expectLiteral("file to be included");
                String includedFilePath = token.decodeLiteral(strict);
                PMessage included;
                File includedFile;
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
                    throw new TokenizerException(token, "Expected token 'as' after included file \"%s\".", includedFilePath)
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
                        throw new TokenizerException(token, "Unknown declared type: %s", token.asString()).setLine(
                                tokenizer.getLine());
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
            throw new TokenizerException("No message in config: " + file.getName());
        }

        return Pair.create(result, includedFilePaths);
    }

    @SuppressWarnings("unchecked")
    private void parseDefinitions(ProvidenceConfigContext context, Tokenizer tokenizer) throws IOException {
        Token token = tokenizer.expect("defines group start or identifier");
        if (token.isIdentifier()) {
            String name = context.initReference(token, tokenizer);
            tokenizer.expectSymbol("def value sep", Token.kFieldValueSep);
            context.setReference(name, parseDefinitionValue(context, tokenizer));
        } else if (token.isSymbol(Token.kMessageStart)) {
            token = tokenizer.expect("define or end");
            while (!token.isSymbol(Token.kMessageEnd)) {
                if (!token.isIdentifier()) {
                    throw new TokenizerException(token, "Token '%s' is not valid reference name.", token.asString()).setLine(
                            tokenizer.getLine());
                }
                String name = context.initReference(token, tokenizer);
                tokenizer.expectSymbol("def value sep", Token.kFieldValueSep);
                context.setReference(name, parseDefinitionValue(context, tokenizer));
                token = tokenizer.expect("next define or end");
            }
        } else {
            throw new TokenizerException(token, "Unexpected token after def: '%s'", token.asString()).setLine(
                    tokenizer.getLine());
        }
    }

    @SuppressWarnings("unchecked")
    private Object parseDefinitionValue(ProvidenceConfigContext context, Tokenizer tokenizer) throws IOException {
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
                    throw new TokenizerException(token, "Unknown declared type: %s", token.asString()).setLine(
                            tokenizer.getLine());
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
                                                                              File file)
            throws IOException {
        if (tokenizer.expectSymbol("extension marker", Token.kKeyValueSep, Token.kMessageStart) == Token.kKeyValueSep) {
            Token token = tokenizer.expect("extension object");

            if (parent != null) {
                throw new TokenizerException(token, "Config in '" + file.getName() + "' has both defined parent and inherits from")
                        .setLine(tokenizer.getLine())
                        .setFile(file.getName());
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
                throw new TokenizerException(token, "Unexpected token " + token.asString() + ", expected reference identifier")
                        .setLine(tokenizer.getLine());
            }
        } else if (parent != null) {
            builder.merge(parent);
        }

        return parseMessage(tokenizer, context, builder);
    }

    private void consumeValue(ProvidenceConfigContext context, Tokenizer tokenizer, Token token) throws IOException {
        if (UNDEFINED.equals(token.asString())) {
            // ignore undefined.
            return;
        } else if (token.isReferenceIdentifier()) {
            if (!tokenizer.peek("message start").isSymbol(Token.kMessageStart)) {
                // just a reference.
                return;
            }
            // reference + message.
            token = tokenizer.next();
        }

        if (token.isSymbol(Token.kMessageStart)) {
            // message or map.
            token = tokenizer.expect("map or message first entry");

            if (!token.isSymbol(Token.kMessageEnd) && !token.isIdentifier()) {
                // assume map.
                while (!token.isSymbol(Token.kMessageEnd)) {
                    if (token.isIdentifier() || token.isReferenceIdentifier()) {
                        throw new TokenizerException(token, "Invalid map key: " + token.asString())
                                .setLine(tokenizer.getLine());
                    }
                    consumeValue(context, tokenizer, token);
                    tokenizer.expectSymbol("key value sep.", Token.kKeyValueSep);
                    consumeValue(context, tokenizer, tokenizer.expect("map value"));

                    // maps do *not* require separator, but allows ',' separator, and separator after last.
                    token = tokenizer.expect("map key, end or sep");
                    if (token.isSymbol(Token.kLineSep1)) {
                        token = tokenizer.expect("map key or end");
                    }
                }
            } else {
                // assume message.
                while (!token.isSymbol(Token.kMessageEnd)) {
                    if (!token.isIdentifier()) {
                        throw new TokenizerException(token, "Invalid field name: " + token.asString())
                                .setLine(tokenizer.getLine());
                    }
                    if (tokenizer.peek().isSymbol(DEFINE_REFERENCE)) {
                        tokenizer.next();
                        context.setReference(
                                context.initReference(
                                        tokenizer.expectIdentifier("reference name"), tokenizer),
                                null);
                    }

                    if (tokenizer.peek().isSymbol(Token.kMessageStart)) {
                        // direct inheritance of message field.
                        consumeValue(context, tokenizer, tokenizer.next());
                    } else {
                        tokenizer.expectSymbol("field value sep.", Token.kFieldValueSep);
                        consumeValue(context, tokenizer, tokenizer.next());
                    }
                    token = nextNotLineSep(tokenizer, "message field or end");
                }
            }
        } else if (token.isSymbol(Token.kListStart)) {
            token = tokenizer.next();
            while (!token.isSymbol(Token.kListEnd)) {
                consumeValue(context, tokenizer, token);
                // lists and sets require list separator (,), and allows trailing separator.
                if (tokenizer.expectSymbol("list separator or end", Token.kLineSep1, Token.kListEnd) == Token.kListEnd) {
                    break;
                }
                token = tokenizer.expect("list value or end");
            }
        } else if (token.asString().equals(Token.HEX)) {
            tokenizer.expectSymbol("hex body start", Token.kParamsStart);
            tokenizer.readBinary(Token.kParamsEnd);
        } else if (!(token.isReal() ||  // number (double)
                     token.isInteger() ||  // number (int)
                     token.isStringLiteral() ||  // string literal
                     token.isIdentifier())) {  // enum value reference.
            throw new TokenizerException(token, "Unknown value token '%s'", token.asString())
                    .setLine(tokenizer.getLine());
        }
    }

    @SuppressWarnings("unchecked")
    private <M extends PMessage<M, F>, F extends PField> M parseMessage(Tokenizer tokenizer,
                                                                        ProvidenceConfigContext context,
                                                                        PMessageBuilder<M, F> builder)
            throws IOException {
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
                char symbol = tokenizer.expectSymbol(
                        "Message assigner or start",
                        Token.kFieldValueSep,
                        Token.kMessageStart,
                        DEFINE_REFERENCE);
                if (symbol == DEFINE_REFERENCE) {
                    reference = context.initReference(tokenizer.expectIdentifier("reference name"), tokenizer);
                    symbol = tokenizer.expectSymbol("Message assigner or start after " + reference, Token.kFieldValueSep, Token.kMessageStart);
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
                    reference = context.initReference(tokenizer.expectIdentifier("reference name"), tokenizer);
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
                    baseValue.putAll((Map) builder.build().get(field.getId()));
                }

                if (!token.isSymbol(Token.kMessageStart)) {
                    throw new TokenizerException(token, "Expected map start, but got '%s'", token.asString())
                            .setLine(tokenizer.getLine());
                }
                Map map =  parseMapValue(tokenizer, context, (PMap) field.getDescriptor(), baseValue);
                builder.set(field.getId(), context.setReference(reference, map));
            } else {
                String reference = null;
                // Simple fields *must* have the '=' separation, may have '&' reference.
                if (tokenizer.expectSymbol("field value sep", Token.kFieldValueSep, DEFINE_REFERENCE) ==
                    DEFINE_REFERENCE) {
                    reference = context.initReference(tokenizer.expectIdentifier("reference name"), tokenizer);
                    tokenizer.expectSymbol("field value sep", Token.kFieldValueSep);
                }
                token = tokenizer.expect("field value");
                if (UNDEFINED.equals(token.asString())) {
                    builder.clear(field.getId());
                    context.setReference(reference, null);
                } else {
                    Object value = parseFieldValue(token, tokenizer, context, field.getDescriptor());
                    builder.set(field.getId(), context.setReference(reference, value));
                }
            }

            token = nextNotLineSep(tokenizer, "field or message end");
        }

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private Map parseMapValue(Tokenizer tokenizer,
                              ProvidenceConfigContext context,
                              PMap descriptor,
                              Map builder) throws IOException {
        Token next = tokenizer.expect("map key or end");
        while (!next.isSymbol(Token.kMessageEnd)) {
            Object key = parseFieldValue(next, tokenizer, context, descriptor.keyDescriptor());
            tokenizer.expectSymbol("map key value sep", Token.kKeyValueSep);
            next = tokenizer.expect("map value");
            if (UNDEFINED.equals(next.asString())) {
                builder.remove(key);
            } else {
                Object value = parseFieldValue(next, tokenizer, context, descriptor.itemDescriptor());
                builder.put(key, value);
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
    private Object parseFieldValue(Token next,
                                   Tokenizer tokenizer,
                                   ProvidenceConfigContext context,
                                   PDescriptor descriptor) throws IOException {
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
                    if (next.isInteger()) {
                        value = ed.findById((int) next.parseInteger());
                    } else if (next.isIdentifier()) {
                        value = ed.findByName(next.asString());
                        if (value == null && context.containsReference(next.asString())) {
                            value = resolve(context, next, tokenizer, ed);
                        }
                    } else if (next.isReferenceIdentifier()) {
                        value = resolve(context, next, tokenizer, descriptor);
                    } else {
                        break;
                    }
                    if (value == null && strict) {
                        throw new TokenizerException(next, "No such enum value %s for %s.",
                                                     next.asString(),
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
                            throw new TokenizerException(next, "Reference %s is not a map field ", next.asString())
                                    .setLine(tokenizer.getLine());
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
                            value.add(parseFieldValue(next, tokenizer, context, ct.itemDescriptor()));
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
                            builder.add(parseFieldValue(next, tokenizer, context, ct.itemDescriptor()));
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
            throw new TokenizerException(next, e.getMessage())
                    .setLine(tokenizer.getLine());
        }

        throw new TokenizerException(next, "Unhandled value \"%s\" for type %s",
                                     next.asString(),
                                     descriptor.getType())
                .setLine(tokenizer.getLine());
    }

    private Token nextNotLineSep(Tokenizer tokenizer, String message) throws IOException {
        if (tokenizer.peek().isSymbol(Token.kLineSep1) ||
            tokenizer.peek().isSymbol(Token.kLineSep2)) {
            tokenizer.expect(message);
        }
        return tokenizer.expect(message);
    }

    @Nonnull
    private <V> V resolveRequired(ProvidenceConfigContext context,
                                  Token token,
                                  Tokenizer tokenizer,
                                  PDescriptor descriptor) throws TokenizerException {
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
    private <V> V resolve(ProvidenceConfigContext context,
                          Token token,
                          Tokenizer tokenizer,
                          PDescriptor descriptor) throws TokenizerException {
        Object value = resolveAny(context, token, tokenizer);
        if (value == null) {
            return null;
        }
        switch (descriptor.getType()) {
            case BOOL:
                return (V) (Object) asBoolean(value);
            case BYTE:
                return (V) (Object) (byte) asInteger(value);
            case I16:
                return (V) (Object) (short) asInteger(value);
            case I32:
                return (V) (Object) asInteger(value);
            case I64:
                return (V) (Object) asLong(value);
            case DOUBLE:
                return (V) (Object) asDouble(value);
            case ENUM:
                if (value instanceof PEnumValue) {
                    PEnumValue verified = ((PEnumDescriptor) descriptor).findById(((PEnumValue) value).asInteger());
                    if (value.equals(verified)) {
                        return (V) value;
                    }
                } else if (value instanceof Number) {
                    return (V) ((PEnumDescriptor) descriptor).findById(((Number) value).intValue());
                } else if (value instanceof CharSequence) {
                    return (V) ((PEnumDescriptor) descriptor).findByName(value.toString());
                }
                throw new ProvidenceConfigException(value.getClass().getSimpleName() + " is not compatible with " + descriptor.getQualifiedName());
            case STRING:
                return (V) asString(value);
            case BINARY:
                if (value instanceof Binary) {
                    return (V) value;
                } else if (value instanceof CharSequence) {
                    return (V) Binary.fromBase64(value.toString());
                }
                throw new ProvidenceConfigException(value.getClass().getSimpleName() + " is not compatible with binary");
            case MAP:
                if (value instanceof Map) {
                    return (V) value;
                }
                throw new ProvidenceConfigException(value.getClass().getSimpleName() + " is not compatible with map");
            case SET:
            case LIST:
                if (value instanceof Collection) {
                    return (V) value;
                }
                throw new ProvidenceConfigException(value.getClass().getSimpleName() + " is not compatible with " + descriptor.getType());
            case MESSAGE:
                if (value instanceof PMessage) {
                    if (descriptor.equals(((PMessage) value).descriptor())) {
                        return (V) value;
                    }
                }
                throw new ProvidenceConfigException(value.getClass().getSimpleName() + " is not compatible with " + descriptor.getQualifiedName());
            default:
                throw new IllegalArgumentException("Type " + descriptor.getType() + " is not handled by config.");
        }
    }

    private Object resolveAny(ProvidenceConfigContext context, Token token, Tokenizer tokenizer)
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
                throw new TokenizerException(token, e.getMessage())
                        .setLine(tokenizer.getLine())
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

    // --- static

    private static final String IDENTIFIER_SEP = ".";
    private static final char DEFINE_REFERENCE = '&';

    private static final String FALSE     = "false";
    private static final String TRUE      = "true";
    private static final String DEF       = "def";
    public static final  String UNDEFINED = "undefined";
    private static final String INCLUDE   = "include";
    private static final String AS        = "as";

    static final Set<String> RESERVED_WORDS = ImmutableSet.of(
            TRUE,
            FALSE,
            UNDEFINED,
            DEF,
            AS,
            INCLUDE
    );

    /**
     * Resolve a file path within the source roots.
     *
     * @param ref A file or directory reference
     * @param path The file reference to resolve.
     * @return The resolved file.
     * @throws FileNotFoundException When the file is not found.
     * @throws IOException When unable to make canonical path.
     */
    protected static File resolveFile(File ref, String path) throws IOException {
        if (ref == null) {
            // relative to PWD from initial load file path.
            File tmp = new File(path).getCanonicalFile().getAbsoluteFile();
            if (tmp.exists()) {
                if (tmp.isFile()) {
                    return tmp;
                }
                throw new FileNotFoundException(path + " is a directory, expected file");
            }
            throw new FileNotFoundException("File " + path + " not found");
        } else if (path.startsWith("/")) {
            throw new FileNotFoundException("Absolute path includes not allowed: " + path);
        } else {
            // relative to reference file. Parent directory lookup (..) allowed.

            if (!ref.isDirectory()) {
                ref = ref.getParentFile();
            }
            File tmp = new File(ref, path).getCanonicalFile()
                                          .getAbsoluteFile();
            if (tmp.exists()) {
                if (tmp.isFile()) {
                    return tmp;
                }
                throw new FileNotFoundException(path + " is a directory, expected file");
            }
            throw new FileNotFoundException("Included file " + path + " not found");
        }
    }
}
