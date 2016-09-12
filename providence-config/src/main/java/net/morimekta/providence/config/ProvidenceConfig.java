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

import net.morimekta.config.ConfigException;
import net.morimekta.config.IncompatibleValueException;
import net.morimekta.config.KeyNotFoundException;
import net.morimekta.config.util.ConfigUtil;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.util.TypeRegistry;
import net.morimekta.providence.util.pretty.Token;
import net.morimekta.providence.util.pretty.Tokenizer;

import com.google.common.collect.ImmutableMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.morimekta.config.util.ConfigUtil.asBoolean;
import static net.morimekta.config.util.ConfigUtil.asDouble;
import static net.morimekta.config.util.ConfigUtil.asInteger;
import static net.morimekta.config.util.ConfigUtil.asLong;
import static net.morimekta.config.util.ConfigUtil.asString;
import static net.morimekta.providence.config.ProvidenceConfigUtil.getInMessage;

/**
 * Providence config loader. This loads providence configs.
 */
public class ProvidenceConfig {
    private static final String IDENTIFIER_SEP = ".";

    private static final String FALSE     = "false";
    private static final String TRUE      = "true";
    private static final String PARAMS    = "params";
    private static final String UNDEFINED = "undefined";
    private static final String INCLUDE   = "include";
    private static final String AS        = "as";

    private final TypeRegistry registry;
    private final Map<String, String>   inputParams;
    // Full file path to resolved instance.
    private final Map<String, PMessage> loaded;

    // simple stage separation. The content *must* come in this order.
    private enum Stage {
        INCLUDES,
        PARAMS,
        MESSAGE
    }

    public ProvidenceConfig(TypeRegistry registry, Map<String, String> inputParams) {
        this.registry = registry;
        this.inputParams = ImmutableMap.copyOf(inputParams);
        this.loaded = new HashMap<>();
    }

    public <M extends PMessage<M, F>, F extends PField> M load(File file) {
        try {
            return loadRecursively(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (SerializerException e) {
            throw new ConfigException(e, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private <M extends PMessage<M, F>, F extends PField> M loadRecursively(File file, String... stack)
            throws IOException, SerializerException {
        file = file.getCanonicalFile().getAbsoluteFile();
        String filePath = file.toString();

        List<String> stackList = new LinkedList<>();
        Collections.addAll(stackList, stack);
        if (stackList.contains(filePath)) {
            stackList.add(filePath);

            Path pwd = Paths.get(new File(IDENTIFIER_SEP).getAbsoluteFile().getCanonicalPath());

            throw new SerializerException("Circular includes detected: " +
                    String.join(" -> ", stackList.stream()
                                                 .map(p -> Paths.get(p).relativize(pwd).toString())
                                                 .collect(Collectors.toList())));
        }
        if (loaded.containsKey(filePath)) {
            return (M) loaded.get(filePath);
        }

        stackList.add(filePath);

        Tokenizer tokenizer;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            // Non-enclosed content, meaning we should read the whole file immediately.
            tokenizer = new Tokenizer(in, false);
        }

        Map<String, Object> params = new HashMap<>();
        Map<String, PMessage> includes = new HashMap<>();
        M result = null;

        Stage stage = Stage.INCLUDES;

        Token token = tokenizer.peek();
        while (token != null) {
            tokenizer.next();

            if (stage == Stage.MESSAGE) {
                throw new SerializerException("Unexpected token " + token.asString() + ", expected end of file.");
            }

            if (token.isQualifiedIdentifier()) {
                // if a.b (type identifier)        --> MESSAGE
                stage = Stage.MESSAGE;
                PStructDescriptor<M, F> descriptor = (PStructDescriptor) registry.getDeclaredType(token.asString());
                result = parseConfigMessage(tokenizer, includes, mkParams(params), descriptor.builder());
            } else if (token.isIdentifier()) {
                if (PARAMS.equals(token.asString())) {
                    // if params && stage != MESSAGE   --> PARAMS
                    if (stage == Stage.PARAMS) {
                        throw new IllegalArgumentException("Params already defined.");
                    }
                    stage = Stage.PARAMS;
                    params = parseParams(tokenizer);
                } else if (INCLUDE.equals(token.asString())) {
                    // if include && stage == INCLUDES --> INCLUDES
                    if (stage != Stage.INCLUDES) {
                        throw new SerializerException("Unexpected include after params or message: " + token.asString());
                    }
                    token = tokenizer.expectStringLiteral("file to be included");
                    File includedFile = new File(file.getParentFile(), token.decodeLiteral());
                    PMessage included = loadRecursively(includedFile, (String[]) stackList.toArray(new String[stackList.size()]));
                    if (!AS.equals(tokenizer.expectIdentifier("the token 'as'").asString())) {
                        throw new SerializerException("Missing alias for included file " + includedFile);
                    }
                    String alias = tokenizer.expectIdentifier("Include alias").asString();
                    if (includes.containsKey(alias)) {
                        throw new SerializerException("Alias \"" + alias + "\" is already used");
                    } else if (PARAMS.equals(alias) || INCLUDE.equals(alias) || AS.equals(alias)) {
                        throw new SerializerException("Alias \"" + alias + "\" is reserved word.");
                    }
                    includes.put(alias, included);
                } else {
                    throw new SerializerException("Unexpected token " + token.asString() + "expected include, params or message type");
                }
            } else {
                throw new SerializerException("Unexpected token " + token.asString() + "expected include, params or message type");
            }

            token = tokenizer.peek();
        }

        if (result == null) {
            throw new ConfigException("No message in config: " + filePath);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String,Object> parseParams(Tokenizer tokenizer) throws IOException, SerializerException {
        Map<String,Object> out = new HashMap<>();

        tokenizer.expectSymbol("params start", '{');
        Token token = tokenizer.expect("param or end");
        while (!token.isSymbol('}')) {
            if (!token.isIdentifier()) {
                throw new SerializerException("Param name " + token.asString() + " not valid");
            }
            String name = token.asString();
            tokenizer.expectSymbol("param value sep", '=');
            token = tokenizer.expect("param value");

            if (token.isReal()) {
                out.put(name, Double.parseDouble(token.asString()));
            } else if (token.isInteger()) {
                out.put(name, Long.parseLong(token.asString()));
            } else if (token.isStringLiteral()) {
                out.put(name, token.decodeLiteral());
            } else if (TRUE.equalsIgnoreCase(token.asString())) {
                out.put(name, true);
            } else if (FALSE.equalsIgnoreCase(token.asString())) {
                out.put(name, false);
            } else if (token.isDoubleQualifiedIdentifier()) {
                // this may be an enum reference, must be
                // - package.EnumType.IDENTIFIER

                String id = token.asString();
                int l = id.lastIndexOf('.');
                try {
                    PEnumDescriptor ed = (PEnumDescriptor) (Object) registry.getDeclaredType(id.substring(0, l));
                    out.put(name, ed.getValueByName(id.substring(l + 1)));
                } catch (ClassCastException e) {
                    throw new SerializerException("Identifier " + id + " does not reference an enum, from " + token.asString());
                }
            } else {
                throw new IllegalArgumentException("Invalid value " + token.asString());
            }

            token = tokenizer.expect("next param or end");
        }

        return out;
    }

    private <M extends PMessage<M, F>, F extends PField> M parseConfigMessage(Tokenizer tokenizer,
                                                                              Map<String, PMessage> includes,
                                                                              Map<String, Object> params,
                                                                              PMessageBuilder<M, F> builder)
            throws IOException, SerializerException {
        PStructDescriptor<M, F> descriptor = builder.descriptor();

        if (tokenizer.expectSymbol("extension marker", ':', '{') == ':') {
            Token token = tokenizer.expect("extension object");
            if (token.isReferenceIdentifier()) {
                builder = descriptor.builder();
                builder.merge(resolve(includes, params, token.asString()));
                tokenizer.expectSymbol("object begin", '{');
            } else {
                throw new IllegalArgumentException("Unexpected token " + token.asString() + ", expected message begin");
            }
        }

        return parseMessage(tokenizer, includes, params, builder);
    }

    @SuppressWarnings("unchecked")
    private <M extends PMessage<M, F>, F extends PField> M parseMessage(Tokenizer tokenizer,
                                                                        Map<String, PMessage> includes,
                                                                        Map<String, Object> params,
                                                                        PMessageBuilder<M, F> builder)
            throws IOException, SerializerException {
        PStructDescriptor<M, F> descriptor = builder.descriptor();

        Token token = tokenizer.expect("object end or field");
        while (!token.isSymbol('}')) {
            if (!token.isIdentifier()) {
                throw new SerializerException("Invalid field name: " + token.asString());
            }

            F field = descriptor.getField(token.asString());
            if (field == null) {
                throw new SerializerException("No such field " + token.asString() + " in " + descriptor.getQualifiedName(null));
            }

            if (field.getType() == PType.MESSAGE) {
                // go recursive with optional
                char symbol = tokenizer.expectSymbol("Message assigner or start", '=', '{');
                PMessageBuilder bld;
                if (symbol == '=') {
                    token = tokenizer.expect("reference or message start");
                    if (UNDEFINED.equals(token.asString())) {
                        // unset.
                        builder.clear(field.getKey());

                        // special casing this, as we don't want to duplicate the parse line below.
                        token = tokenizer.expect("message end or field");
                        continue;
                    }
                    // overwrite with new.
                    bld = ((PStructDescriptor) field.getDescriptor()).builder();
                    if (token.isReferenceIdentifier()) {
                        // Inherit from reference.
                        bld.merge(resolve(includes, params, token.asString()));

                        token = tokenizer.expect("after message reference");
                        // if the following symbol is *not* message start,
                        // we assume a new field or end of current message.
                        if (!token.isSymbol('{')) {
                            continue;
                        }
                    } else if (!token.isSymbol('{')) {
                        throw new IllegalArgumentException("Unexpected token " + token.asString() + ", expected message start.");
                    }
                } else {
                    // extend in-line.
                    bld = builder.mutator(field.getKey());
                }

                builder.set(field.getKey(), parseMessage(tokenizer, includes, params, bld));
            } else {
                // Non-message fields *must* have the '=' separation.
                tokenizer.expectSymbol("field value sep", '=');
                token = tokenizer.peek("field value (lookahead)");
                if (UNDEFINED.equals(token.asString())) {
                    tokenizer.next();
                    builder.clear(field.getKey());
                } else {
                    builder.set(field.getKey(), parseFieldValue(tokenizer, includes, params, field.getDescriptor()));
                }
            }

            token = tokenizer.expect("message end or field");
        }

        return builder.build();
    }

    private Object parseFieldValue(Tokenizer tokenizer,
                                   Map<String, PMessage> includes,
                                   Map<String, Object> params,
                                   PDescriptor descriptor) throws IOException, SerializerException {
        Token next = tokenizer.expect("Field value");

        switch (descriptor.getType()) {
            case BOOL:
                if (TRUE.equals(next.asString())) {
                    return true;
                } else if (FALSE.equals(next.asString())) {
                    return false;
                } else if (next.isReferenceIdentifier()) {
                    return asBoolean(resolve(includes, params, next.asString()));
                }
                break;
            case BYTE:
                if (next.isReferenceIdentifier()) {
                    return (byte) asInteger(resolve(includes, params, next.asString()));
                } else if (next.isInteger()) {
                    return (byte) next.parseInteger();
                }
                break;
            case I16:
                if (next.isReferenceIdentifier()) {
                    return (short) asInteger(resolve(includes, params, next.asString()));
                } else if (next.isInteger()) {
                    return (short) next.parseInteger();
                }
                break;
            case I32:
                if (next.isReferenceIdentifier()) {
                    return asInteger(resolve(includes, params, next.asString()));
                } else if (next.isInteger()) {
                    return (int) next.parseInteger();
                }
                break;
            case I64:
                if (next.isReferenceIdentifier()) {
                    return asLong(resolve(includes, params, next.asString()));
                } else if (next.isInteger()) {
                    return next.parseInteger();
                }
                break;
            case DOUBLE:
                if (next.isReferenceIdentifier()) {
                    return asDouble(resolve(includes, params, next.asString()));
                } else if (next.isInteger() || next.isReal()) {
                    return next.parseDouble();
                }
                break;
            case STRING:
                if (next.isReferenceIdentifier()) {
                    return asString(resolve(includes, params, next.asString()));
                } else if (next.isStringLiteral()) {
                    return next.decodeLiteral();
                }
                break;
            case BINARY:
                // TODO: Implement binary handling.
                throw new IllegalArgumentException("Not implemented !!!!!!!");
            case ENUM: {
                PEnumDescriptor ed = (PEnumDescriptor) descriptor;
                if (next.isInteger()) {
                    return ed.getValueById((int) next.parseInteger());
                } else if (next.isIdentifier()) {
                    // Check for VALUE.
                    PEnumValue value = ed.getValueByName(next.asString());
                    if (value != null) {
                        return value;
                    } else {
                        return resolve(includes, params, next.asString());
                    }
                } else if (next.isReferenceIdentifier()) {
                    return resolve(includes, params, next.asString());
                }
                break;
            }
            case MESSAGE:
                if (next.isReferenceIdentifier()) {
                    return resolve(includes, params, next.asString());
                } else if (next.isSymbol('{')) {
                    return parseMessage(tokenizer, includes, params, ((PStructDescriptor) descriptor).builder());
                }
                break;
            case LIST:
            case SET:
            case MAP:
                // TODO: Implement container handling.
                throw new IllegalArgumentException("Not implemented !!!!!!!");
        }

        throw new IllegalArgumentException(String.format("Unhandled value \"%s\" for type %s",
                                                         next.asString(),
                                                         descriptor.getType()));
    }

    private Map<String, Object> mkParams(Map<String,Object> declared) {
        ImmutableMap.Builder<String,Object> builder = ImmutableMap.builder();
        for (String key : declared.keySet()) {
            Object orig = declared.get(key);
            if (this.inputParams.containsKey(key)) {
                if (orig instanceof CharSequence) {
                    builder.put(key, this.inputParams.get(key));
                } else if (orig instanceof Double) {
                    builder.put(key, ConfigUtil.asDouble(this.inputParams.get(key)));
                } else if (orig instanceof Long) {
                    builder.put(key, ConfigUtil.asLong(this.inputParams.get(key)));
                } else if (orig instanceof Integer) {
                    builder.put(key, asInteger(this.inputParams.get(key)));
                } else if (orig instanceof Boolean){
                    builder.put(key, ConfigUtil.asBoolean(this.inputParams.get(key)));
                }
            } else {
                builder.put(key, orig);
            }
        }

        return builder.build();
    }

    /**
     * Resolve a value reference.
     *
     * @param includes A name to config include map.
     * @param params A resolved params map.
     * @param key The key to look for.
     * @return The value at the given key, or exception if not found.
     */
    @SuppressWarnings("unchecked")
    private <V> V resolve(Map<String, PMessage> includes,
                          Map<String, Object> params,
                          String key) {
        try {
            if (key.contains(IDENTIFIER_SEP)) {
                int idx = key.indexOf(IDENTIFIER_SEP);
                String name = key.substring(0, idx);
                String sub = key.substring(idx + 1);
                if (PARAMS.equals(name)) {
                    if (!params.containsKey(sub)) {
                        throw new IllegalArgumentException("Name " + sub + " not in params (\"" + key + "\")");
                    }
                    return (V) params.get(sub);
                } else if (includes.containsKey(name)) {
                    return (V) getInMessage(includes.get(name), sub);
                }
                throw new IllegalArgumentException("Name " + name + " not available in config: " + key);
            } else if (includes.containsKey(key)) {
                return (V) includes.get(key);
            } else if (params.containsKey(key)) {
                return (V) params.get(key);
            }
            throw new IllegalArgumentException("Name " + key + " not available in config.");
        } catch (KeyNotFoundException e) {
            throw new KeyNotFoundException(e.getMessage() + ": " + key);
        } catch (IncompatibleValueException e) {
            throw new IncompatibleValueException(e.getMessage() + ": " + key);
        }
    }
}
