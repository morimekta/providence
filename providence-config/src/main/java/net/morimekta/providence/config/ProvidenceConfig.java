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
import net.morimekta.providence.PEnumValue;
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
import net.morimekta.providence.util.TypeRegistry;
import net.morimekta.providence.util.pretty.Token;
import net.morimekta.providence.util.pretty.Tokenizer;
import net.morimekta.providence.util.pretty.TokenizerException;
import net.morimekta.util.Binary;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.morimekta.config.util.ConfigUtil.asBoolean;
import static net.morimekta.config.util.ConfigUtil.asDouble;
import static net.morimekta.config.util.ConfigUtil.asInteger;
import static net.morimekta.config.util.ConfigUtil.asLong;
import static net.morimekta.config.util.ConfigUtil.asString;

/**
 * Providence config loader. This loads providence configs.
 */
public class ProvidenceConfig {

    public ProvidenceConfig(TypeRegistry registry) {
        this(registry, ImmutableMap.of());
    }

    public ProvidenceConfig(TypeRegistry registry, Map<String, String> inputParams) {
        this(registry, inputParams, false);
    }

    public ProvidenceConfig(TypeRegistry registry, Map<String, String> inputParams, boolean strict) {
        this.registry = registry;
        this.inputParams = ImmutableMap.copyOf(inputParams);
        this.loaded = new ConcurrentHashMap<>();
        this.parents = new ConcurrentHashMap<>();
        this.reverseDependencies = new HashMap<>();
        this.strict = strict;
    }

    private Set<String> getReverseDeps(String to) {
        return reverseDependencies.computeIfAbsent(to, k -> new HashSet<>());
    }

    /**
     * Load providence config from the given file.
     *
     * @param file The file to load.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return The parsed and merged config.
     * @throws IOException If the file could not be read.
     * @throws TokenizerException If the file could not be parsed.
     */
    public <M extends PMessage<M, F>, F extends PField> M getConfig(File file) throws IOException {
        Supplier<M> supplier = getSupplier(file);
        return supplier.get();
    }

    /**
     * Load providence config from the given file.
     *
     * @param file The file to load.
     * @param descriptor The config type descriptor.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return The parsed and merged config.
     * @throws IOException If the file could not be read.
     * @throws TokenizerException If the file could not be parsed.
     */
    public <M extends PMessage<M, F>, F extends PField> M getConfig(File file, PMessageDescriptor<M,F> descriptor) throws IOException {
        return getSupplier(file, descriptor).get();
    }

    /**
     * Load providence config from the given file.
     *
     * @param file The file to load.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return Supplier for the parsed and merged config.
     * @throws IOException If the file could not be read.
     * @throws TokenizerException If the file could not be parsed.
     */
    public synchronized <M extends PMessage<M, F>, F extends PField> Supplier<M> getSupplier(File file) throws IOException {
        try {
            AtomicReference<M> reference = loadConfigRecursively(resolveFile(null, file.getPath()));
            return reference::get;
        } catch (FileNotFoundException e) {
            throw new TokenizerException(e.getMessage(), e).setFile(file.getName());
        }
    }

    /**
     * Load providence config from the given file, and with a defined parent
     * config. The parent config comes from a supplier, so does not need to
     * be a providence config per se.
     *
     * The loaded config will <b>not</b> be updated when the parent config is
     * updated, as there is no known "upward" dependency. Therefore this should
     * only be used for non-changing config, and for top-level config that does
     * not depend on listening to upstream config changes.
     *
     * @param file The file to load.
     * @param parent The parent message for the config to inherit.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return Supplier for the parsed and merged config.
     * @throws IOException If the file could not be read.
     * @throws TokenizerException If the file could not be parsed.
     */
    @SuppressWarnings("unchecked")
    public synchronized <M extends PMessage<M, F>, F extends PField> Supplier<M> getSupplierWithParent(File file, Supplier<M> parent) throws IOException {
        try {
            File config = resolveFile(null, file.getPath());
            String path = config.getCanonicalFile().getAbsolutePath();

            // It is assumed that if the parent is already set, it is the same.
            parents.computeIfAbsent(path, (name) -> (Supplier) parent);
            AtomicReference<M> reference = loadConfigRecursively(config);
            return reference::get;
        } catch (FileNotFoundException e) {
            throw new TokenizerException(e.getMessage(), e).setFile(file.getName());
        }
    }

    /**
     * Load providence config from the given file, and with a defined parent
     * config. The parent config must come from an already loaded config file.
     *
     * @param configFile The file to load.
     * @param parentFile The parent config file.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return Supplier for the parsed and merged config.
     * @throws IOException If the file could not be read.
     * @throws TokenizerException If the file could not be parsed.
     */
    @SuppressWarnings("unchecked")
    public synchronized <M extends PMessage<M, F>, F extends PField> Supplier<M> getSupplierWithParent(File configFile, File parentFile) throws IOException {
        return getSupplierWithParent(configFile, parentFile, null);
    }

    /**
     * Load providence config from the given file, and with a defined parent
     * config. The parent config must come from an already loaded config file.
     *
     * @param configFile The file to load.
     * @param parentFile The parent config file.
     * @param descriptor The config type descriptor.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return Supplier for the parsed and merged config.
     * @throws IOException If the file could not be read.
     * @throws TokenizerException If the file could not be parsed.
     */
    @SuppressWarnings("unchecked")
    public synchronized <M extends PMessage<M, F>, F extends PField> Supplier<M> getSupplierWithParent(File configFile, File parentFile, PMessageDescriptor<M, F> descriptor) throws IOException {
        try {
            String configPath = resolveFile(null, configFile.getPath()).getCanonicalFile().getAbsolutePath();
            String parentPath = resolveFile(null, parentFile.getPath()).getCanonicalFile().getAbsolutePath();

            AtomicReference<M> parent = (AtomicReference) loaded.get(parentPath);
            if (parent == null) {
                throw new TokenizerException("Parent file " + parentFile.getName() + " is not loaded.")
                        .setFile(configFile.getName());
            }
            if (descriptor != null && !parent.get().descriptor().equals(descriptor)) {
                throw new TokenizerException(
                        String.format(
                                Locale.ENGLISH,
                                "Incompatible message type: Expected %s, got %s",
                                descriptor.getQualifiedName(),
                                parent.get().descriptor().getQualifiedName()))
                        .setFile(configFile.getPath());
            }

            Supplier<M> supplier = getSupplierWithParent(configFile, parent::get);

            if (descriptor != null && !supplier.get().descriptor().equals(descriptor)) {
                throw new TokenizerException(
                        String.format(
                                Locale.ENGLISH,
                                "Incompatible message type: Expected %s, got %s",
                                descriptor.getQualifiedName(),
                                supplier.get().descriptor().getQualifiedName()))
                        .setFile(configFile.getPath());
            }

            getReverseDeps(parentPath).add(configPath);

            return supplier;
        } catch (FileNotFoundException e) {
            throw new TokenizerException(e.getMessage(), e).setFile(configFile.getName());
        }
    }
    /**
     * Load providence config from the given file.
     *
     * @param file The file to load.
     * @param <M> The message type.
     * @param <F> The message field type.
     * @return Supplier for the parsed and merged config.
     * @throws IOException If the file could not be read.
     * @throws TokenizerException If the file could not be parsed.
     */
    public <M extends PMessage<M, F>, F extends PField> Supplier<M> getSupplier(File file, PMessageDescriptor<M,F> descriptor) throws IOException {
        try {
            Supplier<M> supplier = getSupplier(file);
            if (descriptor != null && !supplier.get().descriptor().equals(descriptor)) {
                throw new TokenizerException(
                        String.format(
                                Locale.ENGLISH,
                                "Incompatible message type: Expected %s, got %s",
                                descriptor.getQualifiedName(),
                                supplier.get().descriptor().getQualifiedName()))
                        .setFile(file.getPath());
            }

            return supplier;
        } catch (FileNotFoundException e) {
            throw new TokenizerException(e.getMessage()).setFile(file.getName());
        }
    }

    /**
     * Generate a list of params from the  providence config, and it's included subconfigs.
     *
     * @param file The file to load.
     * @return The list of params from the loaded configs.
     * @throws IOException If the file could not be read.
     * @throws TokenizerException If the file could not be parsed.
     */
    public List<ProvidenceConfigParam> params(File file) throws IOException {
        return loadParamsRecursively(resolveFile(null, file.toString()));
    }

    /**
     * Trigger reloading of the given file, and run recursively <i>up</i> through dependencies.
     *
     * @param file The file that may need to be reloaded.
     */
    public void reload(File file) throws IOException {
        String canonicalPath = file.getCanonicalFile()
                                   .getAbsolutePath();

        AtomicReference<PMessage> reference = loaded.get(canonicalPath);
        if (reference == null) {
            return;
        }
        Set<String> dependencies = new TreeSet<>(getReverseDeps(canonicalPath));

        try {
            @SuppressWarnings("unchecked")
            PMessage reloaded = parseConfigRecursively(file, new String[]{canonicalPath});
            if (reference.get()
                         .equals(reloaded)) {
                return;
            }

            reference.set(reloaded);

            for (String dep : dependencies) {
                reload(new File(dep));
            }
        } catch (IOException e) {
            // Reinstate the old value if we failed to reload it. Also
            // reinstate the old value if any of the dependent files failed
            // to load. The reason they failed could easily be that this
            // file was no longer compatible.
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * Resolve a file path within the source roots.
     *
     * @param ref A file or directory reference
     * @param path The file reference to resolve.
     * @return The resolved file.
     * @throws FileNotFoundException When the file is not found.
     * @throws IOException When unable to make canonical path.
     */
    @VisibleForTesting
    File resolveFile(File ref, String path) throws IOException {
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

    private List<ProvidenceConfigParam> loadParamsRecursively(File file, String... stack)
            throws IOException {
        try {
            File canonicalFile = file.getCanonicalFile()
                                     .getAbsoluteFile();
            String filePath = canonicalFile.toString();

            List<String> stackList = new LinkedList<>();
            Collections.addAll(stackList, stack);
            if (stackList.contains(filePath)) {
                stackList.add(filePath);
                throw new TokenizerException("Circular includes detected: " +
                                             String.join(" -> ", stackList.stream()
                                                                          .map(p -> new File(p).getName())
                                                                          .collect(Collectors.toList())));
            }

            stackList.add(filePath);

            Tokenizer tokenizer;
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(canonicalFile))) {
                // Non-enclosed content, meaning we should read the whole file immediately.
                tokenizer = new Tokenizer(in, false);
            }

            List<ProvidenceConfigParam> result = new LinkedList<>();

            Stage stage = Stage.PARAMS;

            Token token = tokenizer.peek();
            while (token != null) {
                tokenizer.next();

                if (token.isQualifiedIdentifier()) {
                    // if a.b (type identifier) --> MESSAGE
                    return result;
                } else if (token.isIdentifier()) {
                    if (PARAMS.equals(token.asString())) {
                        // if params && PARAMS --> PARAMS
                        if (stage != Stage.PARAMS) {
                            throw new TokenizerException(token, "Params already defined.").setLine(tokenizer.getLine(
                                    token.getLineNo()));
                        }
                        stage = Stage.INCLUDES;
                        parseParams(tokenizer).entrySet()
                                              .forEach(e -> result.add(new ProvidenceConfigParam(e.getKey(), e.getValue(), canonicalFile)));
                    } else if (INCLUDE.equals(token.asString())) {
                        // if include && stage == INCLUDES --> INCLUDES
                        token = tokenizer.expect("file to be included");
                        File includedFile;
                        try {
                            includedFile = resolveFile(file, token.decodeLiteral());
                            result.addAll(loadParamsRecursively(includedFile, (String[]) stackList.toArray(new String[stackList.size()])));
                        } catch (FileNotFoundException e) {
                            throw new TokenizerException(token, "Included file " + token.asString() + " not found")
                                    .setLine(tokenizer.getLine(token.getLineNo()));
                        }

                        if (!AS.equals(tokenizer.expectIdentifier("the token 'as'")
                                                .asString())) {
                            throw new TokenizerException(token, "Missing alias for included file " + includedFile).setLine(
                                    tokenizer.getLine(token.getLineNo()));
                        }
                        tokenizer.expectIdentifier("Include alias")
                                 .asString();
                    } else {
                        throw new TokenizerException(token,
                                                     "Unexpected token " + token.asString() +
                                                     "expected include, params or message type").setLine(tokenizer.getLine(
                                token.getLineNo()));
                    }
                } else {
                    throw new TokenizerException(token,
                                                 "Unexpected token " + token.asString() +
                                                 "expected include, params or message type").setLine(tokenizer.getLine(
                            token.getLineNo()));
                }

                token = tokenizer.peek();
            }

            throw new TokenizerException("No message in config: " + filePath);
        } catch (TokenizerException e) {
            throw new TokenizerException(e, file);
        }
    }

    @SuppressWarnings("unchecked") @Nonnull
    private <M extends PMessage<M, F>, F extends PField> AtomicReference<M> loadConfigRecursively(File file, String... stack)
            throws IOException {
        M result;

        try {
            file = file.getCanonicalFile()
                       .getAbsoluteFile();
            String filePath = file.toString();

            List<String> stackList = new LinkedList<>();
            Collections.addAll(stackList, stack);
            if (stackList.contains(filePath)) {
                stackList.add(filePath);
                throw new TokenizerException("Circular includes detected: " + String.join(" -> ",
                                                                                          stackList.stream()
                                                                                                   .map(p -> new File(p).getName())
                                                                                                   .collect(Collectors.toList())));
            }

            if (loaded.containsKey(filePath)) {
                if (stack.length > 0) {
                    getReverseDeps(filePath).add(stack[stack.length - 1]);
                }

                return (AtomicReference) loaded.get(filePath);
            }

            stackList.add(filePath);

            result = parseConfigRecursively(file, stackList.toArray(new String[stackList.size()]));
            if (result == null) {
                return new AtomicReference<>();
            }

            stackList.add(filePath);
            if (stack.length > 0) {
                getReverseDeps(filePath).add(stack[stack.length - 1]);
            }

            AtomicReference ref = loaded.get(filePath);
            if (ref == null) {
                ref = new AtomicReference(result);
                loaded.put(filePath, ref);
            } else {
                ref.set(result);
            }

            return ref;
        } catch (TokenizerException e) {
            throw new TokenizerException(e, file);
        }
    }

    @SuppressWarnings("unchecked")
    private <M extends PMessage<M, F>, F extends PField> M parseConfigRecursively(File file,
                                                                                  String[] stack) throws IOException {
        Tokenizer tokenizer;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            // Non-enclosed content, meaning we should read the whole file immediately.
            tokenizer = new Tokenizer(in, false);
        }

        ProvidenceConfigContext context = new ProvidenceConfigContext();
        Map<String, Object> params = new HashMap<>();

        Stage stage = Stage.PARAMS;
        M result = null;

        Token token = tokenizer.peek();
        while (token != null) {
            tokenizer.next();

            if (stage == Stage.MESSAGE) {
                throw new TokenizerException(token,
                                             "Unexpected token " + token.asString() + ", expected end of file.").setLine(tokenizer.getLine(token.getLineNo()));
            }

            if (token.isQualifiedIdentifier()) {
                // if a.b (type identifier) --> MESSAGE
                stage = Stage.MESSAGE;
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
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }
                    return null;
                }
                context.params.putAll(mkParams(params));
                result = parseConfigMessage(tokenizer, context, descriptor.builder(), file);
            } else if (token.isIdentifier()) {
                if (PARAMS.equals(token.asString())) {
                    // if params && stage == PARAMS --> PARAMS
                    if (stage != Stage.PARAMS) {
                        throw new TokenizerException(token,
                                                     "Params already defined, or passed; must be at head of file").setLine(
                                tokenizer.getLine(token.getLineNo()));
                    }
                    stage = Stage.INCLUDES;
                    params = parseParams(tokenizer);
                } else if (INCLUDE.equals(token.asString())) {
                    // if include --> INCLUDES
                    stage = Stage.INCLUDES;
                    token = tokenizer.expectStringLiteral("file to be included");
                    File includedFile;
                    PMessage included;
                    try {
                        includedFile = resolveFile(file, token.decodeLiteral());
                        included = loadConfigRecursively(includedFile, stack).get();
                    } catch (FileNotFoundException e) {
                        throw new TokenizerException(token, "Included file " + token.asString() + " not found")
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }
                    if (!AS.equals(tokenizer.expectIdentifier("the token 'as'")
                                            .asString())) {
                        throw new TokenizerException(token, "Missing alias for included file " + includedFile).setLine(
                                tokenizer.getLine(token.getLineNo()));
                    }
                    String alias = tokenizer.expectIdentifier("Include alias")
                                            .asString();
                    if (RESERVED_WORDS.contains(alias)) {
                        throw new TokenizerException(token, "Alias \"" + alias + "\" is a reserved word").setLine(
                                tokenizer.getLine(token.getLineNo()));
                    }
                    if (context.includes.containsKey(alias)) {
                        throw new TokenizerException(token, "Alias \"" + alias + "\" is already used").setLine(
                                tokenizer.getLine(token.getLineNo()));
                    }
                    context.includes.put(alias, included);
                } else {
                    throw new TokenizerException(token,
                                                 "Unexpected token " + token.asString() +
                                                 "expected include, params or message type").setLine(tokenizer.getLine(
                            token.getLineNo()));
                }
            } else {
                throw new TokenizerException(token,
                                             "Unexpected token " + token.asString() +
                                             "expected include, params or message type").setLine(tokenizer.getLine(
                        token.getLineNo()));
            }

            token = tokenizer.peek();
        }

        if (result == null) {
            throw new TokenizerException("No message in config: " + file.getName());
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String,Object> parseParams(Tokenizer tokenizer) throws IOException {
        Map<String,Object> out = new HashMap<>();

        tokenizer.expectSymbol("params start", Token.kMessageStart);
        Token token = tokenizer.expect("param or end");
        while (!token.isSymbol(Token.kMessageEnd)) {
            if (!token.isIdentifier()) {
                throw new TokenizerException(token, "Param name " + token.asString() + " not valid")
                        .setLine(tokenizer.getLine(token.getLineNo()));
            }
            String name = token.asString();
            tokenizer.expectSymbol("param value sep", Token.kFieldValueSep);
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
            } else if (Token.B64.equals(token.asString())) {
                tokenizer.expectSymbol("binary data enclosing start", Token.kMethodStart);
                out.put(name, Binary.fromBase64(tokenizer.readUntil(Token.kMethodEnd, false, false)));
            } else if (Token.HEX.equals(token.asString())) {
                tokenizer.expectSymbol("binary data enclosing start", Token.kMethodStart);
                out.put(name, Binary.fromHexString(tokenizer.readUntil(Token.kMethodEnd, false, false)));
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
                    PEnumValue val = ed.getValueByName(id.substring(l + 1));
                    if (val == null && strict) {
                        throw new TokenizerException(token, "Unknown %s value: %s", id.substring(0, l), id.substring(l + 1))
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }
                    // Note that unknown enum value results in null. Therefore we don't catch null values here.
                    out.put(name, val);
                } catch (IllegalArgumentException e) {
                    // No such declared type.
                    if (strict) {
                        throw new TokenizerException(token, "Unknown enum identifier: %s", id.substring(0, l))
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }
                } catch (ClassCastException e) {
                    // Not an enum.
                    throw new TokenizerException(token, "Identifier " + id + " does not reference an enum, from " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            } else {
                throw new TokenizerException(token, "Invalid param value " + token.asString())
                        .setLine(tokenizer.getLine(token.getLineNo()));
            }

            token = tokenizer.expect("next param or end");
        }

        return out;
    }

    @SuppressWarnings("unchecked")
    private <M extends PMessage<M, F>, F extends PField> M parseConfigMessage(Tokenizer tokenizer,
                                                                              ProvidenceConfigContext context,
                                                                              PMessageBuilder<M, F> builder,
                                                                              File file)
            throws IOException {
        String path = file.getCanonicalFile().getAbsolutePath();
        if (tokenizer.expectSymbol("extension marker", Token.kKeyValueSep, Token.kMessageStart) == Token.kKeyValueSep) {
            Token token = tokenizer.expect("extension object");

            if (parents.containsKey(path)) {
                throw new TokenizerException(token, "Config in '" + file.getName() + "' has both defined parent and inherits from")
                        .setLine(tokenizer.getLine(token.getLineNo()))
                        .setFile(file.getName());
            }

            if (token.isReferenceIdentifier()) {
                try {
                    builder.merge(resolve(context, token, tokenizer, builder.descriptor()));
                } catch (ClassCastException e) {
                    throw new TokenizerException(token, "Config type mismatch, expected  ")
                            .setLine(tokenizer.getLine(token.getLineNo()));
                } catch (KeyNotFoundException e) {
                    throw new TokenizerException(token, e.getMessage())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
                tokenizer.expectSymbol("object begin", Token.kMessageStart);
            } else {
                throw new TokenizerException(token, "Unexpected token " + token.asString() + ", expected reference identifier")
                        .setLine(tokenizer.getLine(token.getLineNo()));
            }
        } else if (parents.containsKey(path)) {
            builder.merge((M) parents.get(path).get());
        }

        return parseMessage(tokenizer, context, builder);
    }

    private void consumeValue(ProvidenceConfigContext context, Tokenizer tokenizer, Token token) throws IOException {
        if (UNDEFINED.equals(token.asString())) {
            // ignore undefined.
            return;
        } else if (token.isReferenceIdentifier()) {
            if (!tokenizer.peek().isSymbol(Token.kMessageStart)) {
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
                                .setLine(tokenizer.getLine(token.getLineNo()));
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
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }
                    if (tokenizer.peek().isSymbol(kDefineReference)) {
                        tokenizer.next();
                        context.setReference(
                                context.initReference(tokenizer.expectIdentifier("reference name"), tokenizer),
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
            tokenizer.expectSymbol("hex body start", Token.kMethodStart);
            tokenizer.readUntil(Token.kMethodEnd, false, false);
        } else if (!(token.isReal() ||  // number (double)
                     token.isInteger() ||  // number (int)
                     token.isStringLiteral() ||  // string literal
                     token.isIdentifier())) {  // enum value reference.
            throw new TokenizerException(token, "Unknown value token '%s'", token.asString())
                    .setLine(tokenizer.getLine(token.getLineNo()));
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
                        .setLine(tokenizer.getLine(token.getLineNo()));
            }

            F field = descriptor.getField(token.asString());
            if (field == null) {
                if (strict) {
                    throw new TokenizerException("No such field " + token.asString() + " in " + descriptor.getQualifiedName())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                } else {
                    token = tokenizer.expect("field value sep, message start or reference start");
                    if (token.isSymbol(kDefineReference)) {
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
                                .setLine(tokenizer.getLine(token.getLineNo()));
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
                        kDefineReference);
                if (symbol == kDefineReference) {
                    reference = context.initReference(tokenizer.expectIdentifier("reference name"), tokenizer);
                    symbol = tokenizer.expectSymbol("Message assigner or start after " + reference, Token.kFieldValueSep, Token.kMessageStart);
                }

                PMessageBuilder bld;
                if (symbol == Token.kFieldValueSep) {
                    token = tokenizer.expect("reference or message start");
                    if (UNDEFINED.equals(token.asString())) {
                        // unset.
                        builder.clear(field.getKey());
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
                                            .setLine(tokenizer.getLine(token.getLineNo()));
                                } else if (strict) {
                                    throw new TokenizerException(token, "Unknown reference %s", token.asString())
                                            .setLine(tokenizer.getLine(token.getLineNo()));
                                }
                            }
                        } catch (KeyNotFoundException e) {
                            throw new TokenizerException(token, "Unknown inherited reference '%s'", token.asString())
                                    .setLine(tokenizer.getLine(token.getLineNo()));
                        }

                        token = tokenizer.expect("after message reference");
                        // if the following symbol is *not* message start,
                        // we assume a new field or end of current message.
                        if (!token.isSymbol(Token.kMessageStart)) {
                            builder.set(field.getKey(), context.setReference(reference, bld.build()));
                            continue;
                        }
                    } else if (!token.isSymbol(Token.kMessageStart)) {
                        throw new TokenizerException(token,
                                                     "Unexpected token " + token.asString() +
                                                     ", expected message start").setLine(tokenizer.getLine(token.getLineNo()));
                    }
                } else {
                    // extend in-line.
                    bld = builder.mutator(field.getKey());
                }
                builder.set(field.getKey(), context.setReference(reference, parseMessage(tokenizer, context, bld)));
            } else if (field.getType() == PType.MAP) {
                // maps can be extended the same way as
                token = tokenizer.expect("field sep or value start");
                Map baseValue = new HashMap();
                String reference = null;
                if (token.isSymbol(kDefineReference)) {
                    reference = context.initReference(tokenizer.expectIdentifier("reference name"), tokenizer);
                    token = tokenizer.expect("field sep or value start");
                }

                if (token.isSymbol(Token.kFieldValueSep)) {
                    token = tokenizer.expect("field id or start");
                    if (UNDEFINED.equals(token.asString())) {
                        builder.clear(field.getKey());
                        context.setReference(reference, null);

                        token = tokenizer.expect("message end or field");
                        continue;
                    } else if (token.isReferenceIdentifier()) {
                        try {
                            baseValue = resolve(context, token, tokenizer, field.getDescriptor());
                        } catch (KeyNotFoundException e) {
                            throw new TokenizerException(token, e.getMessage())
                                    .setLine(tokenizer.getLine(token.getLineNo()));
                        }

                        token = tokenizer.expect("map start or next field");
                        if (!token.isSymbol(Token.kMessageStart)) {
                            builder.set(field.getKey(), context.setReference(reference, baseValue));
                            continue;
                        } else if (baseValue == null) {
                            baseValue = new HashMap();
                        }
                    }
                } else {
                    baseValue.putAll((Map) builder.build().get(field.getKey()));
                }

                if (!token.isSymbol(Token.kMessageStart)) {
                    throw new TokenizerException(token, "Expected map start, but got '%s'", token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
                Map map =  parseMapValue(tokenizer, context, (PMap) field.getDescriptor(), baseValue);
                builder.set(field.getKey(), context.setReference(reference, map));
            } else {
                String reference = null;
                // Simple fields *must* have the '=' separation, may have '&' reference.
                if (tokenizer.expectSymbol("field value sep", Token.kFieldValueSep, kDefineReference) ==
                    kDefineReference) {
                    reference = context.initReference(tokenizer.expectIdentifier("reference name"), tokenizer);
                    tokenizer.expectSymbol("field value sep", Token.kFieldValueSep);
                }
                token = tokenizer.expect("field value");
                if (UNDEFINED.equals(token.asString())) {
                    builder.clear(field.getKey());
                    context.setReference(reference, null);
                } else {
                    Object value = parseFieldValue(token, tokenizer, context, field.getDescriptor());
                    builder.set(field.getKey(), context.setReference(reference, value));
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
                        return next.decodeLiteral();
                    }
                    break;
                case BINARY:
                    if (Token.B64.equals(next.asString())) {
                        tokenizer.expectSymbol("binary data enclosing start", Token.kMethodStart);
                        return Binary.fromBase64(tokenizer.readUntil(Token.kMethodEnd, false, false));
                    } else if (Token.HEX.equals(next.asString())) {
                        tokenizer.expectSymbol("binary data enclosing start", Token.kMethodStart);
                        return Binary.fromHexString(tokenizer.readUntil(Token.kMethodEnd, false, false));
                    } else if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    }
                    break;
                case ENUM: {
                    PEnumDescriptor ed = (PEnumDescriptor) descriptor;
                    PEnumValue value;
                    if (next.isInteger()) {
                        value = ed.getValueById((int) next.parseInteger());
                    } else if (next.isIdentifier()) {
                        value = ed.getValueByName(next.asString());
                    } else if (next.isReferenceIdentifier()) {
                        value = resolve(context, next, tokenizer, descriptor);
                    } else {
                        break;
                    }
                    if (value == null && strict) {
                        throw new TokenizerException(next, "No such enum value %s for %s.",
                                                     next.asString(),
                                                     ed.getQualifiedName())
                                .setLine(tokenizer.getLine(next.getLineNo()));
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
                                    .setLine(tokenizer.getLine(next.getLineNo()));
                        }
                        return resolved;
                    } else if (next.isSymbol(Token.kMessageStart)) {
                        return parseMapValue(tokenizer, context, (PMap) descriptor, new HashMap());
                    }
                    break;
                }
                case SET: {
                    if (next.isReferenceIdentifier()) {
                        return resolve(context, next, tokenizer, descriptor);
                    } else if (next.isSymbol(Token.kListStart)) {
                        @SuppressWarnings("unchecked")
                        PSet<Object> ct = (PSet) descriptor;
                        HashSet<Object> value = new HashSet<>();

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
                    throw new TokenizerException(next, descriptor.getType() + " not supported!").setLine(tokenizer.getLine(
                            next.getLineNo()));
                }
            }
        } catch (KeyNotFoundException e) {
            throw new TokenizerException(next, e.getMessage())
                    .setLine(tokenizer.getLine(next.getLineNo()));
        }

        throw new TokenizerException(next, "Unhandled value \"%s\" for type %s",
                                     next.asString(),
                                     descriptor.getType())
                .setLine(tokenizer.getLine(next.getLineNo()));
    }

    private Token nextNotLineSep(Tokenizer tokenizer, String message) throws IOException {
        if (tokenizer.peek().isSymbol(Token.kLineSep1) ||
            tokenizer.peek().isSymbol(Token.kLineSep2)) {
            tokenizer.expect(message);
        }
        return tokenizer.expect(message);
    }

    private Map<String, Object> mkParams(Map<String,Object> declared) {
        ImmutableMap.Builder<String,Object> builder = ImmutableMap.builder();
        for (Map.Entry<String,Object> entry : declared.entrySet()) {
            Object orig = entry.getValue();
            String key = entry.getKey();
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
                    PEnumValue verified = ((PEnumDescriptor) descriptor).getValueById(((PEnumValue) value).getValue());
                    if (value.equals(verified)) {
                        return (V) value;
                    }
                } else if (value instanceof Number) {
                    return (V) ((PEnumDescriptor) descriptor).getValueById(((Number) value).intValue());
                } else if (value instanceof CharSequence) {
                    return (V) ((PEnumDescriptor) descriptor).getValueByName(value.toString());
                }
                throw new IncompatibleValueException(value.getClass().getSimpleName() + " is not compatible with " + descriptor.getQualifiedName());
            case STRING:
                return (V) asString(value);
            case BINARY:
                if (value instanceof Binary) {
                    return (V) value;
                } else if (value instanceof CharSequence) {
                    return (V) Binary.fromBase64(value.toString());
                }
                throw new IncompatibleValueException(value.getClass().getSimpleName() + " is not compatible with binary");
            case MAP:
                if (value instanceof Map) {
                    return (V) value;
                }
                throw new IncompatibleValueException(value.getClass().getSimpleName() + " is not compatible with map");
            case SET:
            case LIST:
                if (value instanceof Collection) {
                    return (V) value;
                }
                throw new IncompatibleValueException(value.getClass().getSimpleName() + " is not compatible with " + descriptor.getType());
            case MESSAGE:
                if (value instanceof PMessage) {
                    if (descriptor.equals(((PMessage) value).descriptor())) {
                        return (V) value;
                    }
                }
                throw new IncompatibleValueException(value.getClass().getSimpleName() + " is not compatible with " + descriptor.getQualifiedName());
            default:
                throw new IllegalArgumentException("Type " + descriptor.getType() + " is not handled by config.");
        }
    }

    private Object resolveAny(ProvidenceConfigContext context, Token token, Tokenizer tokenizer)
            throws TokenizerException {
        String key = token.asString();

        if (key.contains(IDENTIFIER_SEP)) {
            int idx = key.indexOf(IDENTIFIER_SEP);
            String name = key.substring(0, idx);
            String sub = key.substring(idx + 1);
            if (PARAMS.equals(name)) {
                if (!context.params.containsKey(sub)) {
                    throw new KeyNotFoundException("Name " + sub + " not in params (\"" + key + "\")");
                }
                return context.params.get(sub);
            } else if (context.includes.containsKey(name)) {
                PMessage include = context.includes.get(name);
                if (include == null) {
                    if (strict) {
                        throw new KeyNotFoundException("Included file with alias %s not parsed", name);
                    }
                    return null;
                }
                return ProvidenceConfigUtil.getInMessage(include, sub, null);
            }
            throw new KeyNotFoundException("Reference name " + key + " not declared");
        } else if (context.includes.containsKey(key)) {
            return context.includes.get(key);
        } else {
            return context.getReference(token, tokenizer);
        }
    }

    private static final String IDENTIFIER_SEP = ".";

    private static final String FALSE     = "false";
    private static final String TRUE      = "true";
    private static final String PARAMS    = "params";
            static final String UNDEFINED = "undefined";
    private static final String INCLUDE   = "include";
    private static final String AS        = "as";

    static final Set<String> RESERVED_WORDS = ImmutableSet.of(
            TRUE,
            FALSE,
            UNDEFINED,
            PARAMS,
            AS,
            INCLUDE
    );

    private static final char kDefineReference = '&';

    /**
     * Full path to resolved instance.
     */
    private final Map<String, AtomicReference<PMessage>> loaded;

    /**
     * Some configs have defined 'parents'. The parent must be of the
     * same type as the config, and will be the base of the config
     * as it is parsed. It can reference any type of providence message
     * source.
     * <p>
     * The configs that has defined parents can not have explicit parents
     * (with the <code>type : parent { ... }</code> syntax.
     */
    private final Map<String, Supplier<PMessage>> parents;

    /**
     * Type registry for looking up the base config types.
     */
    private final TypeRegistry             registry;

    /**
     * Map of input params used to override the
     */
    private final Map<String, String>      inputParams;
    private final Map<String, Set<String>> reverseDependencies;
    private final boolean                  strict;

    // simple stage separation. The content *must* come in this order.
    private enum Stage {
        PARAMS,
        INCLUDES,
        MESSAGE
    }
}
