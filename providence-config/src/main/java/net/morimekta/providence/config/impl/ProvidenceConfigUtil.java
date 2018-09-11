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

import net.morimekta.providence.PEnumValue;
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
import net.morimekta.providence.serializer.pretty.Token;
import net.morimekta.providence.serializer.pretty.Tokenizer;
import net.morimekta.providence.serializer.pretty.TokenizerException;
import net.morimekta.util.Binary;
import net.morimekta.util.Numeric;
import net.morimekta.util.Stringable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import static net.morimekta.util.FileUtil.readCanonicalPath;

/**
 * Utilities for helping with providence config handling.
 */
public class ProvidenceConfigUtil {
    /**
     * Simple stage separation. The content *must* come in this order.
     */
    enum Stage {
        INCLUDES,
        DEFINES,
        MESSAGE
    }

    static final String IDENTIFIER_SEP = ".";
    static final char DEFINE_REFERENCE = '&';

    static final        String FALSE     = "false";
    static final        String TRUE      = "true";
    static final        String DEF       = "def";
    public static final String UNDEFINED = "undefined";
    static final        String INCLUDE   = "include";
    static final        String AS        = "as";

    static final Set<String> RESERVED_WORDS = ImmutableSet.of(
            TRUE,
            FALSE,
            UNDEFINED,
            DEF,
            AS,
            INCLUDE
    );

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
    static Object asType(PDescriptor descriptor, Object o) throws ProvidenceConfigException {
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
                if (o instanceof PEnumValue) {
                    PEnumValue verified = ((PEnumDescriptor) descriptor).findById(((PEnumValue) o).asInteger());
                    if (o.equals(verified)) {
                        return verified;
                    }
                } else if (o instanceof Number) {
                    return ((PEnumDescriptor) descriptor).findById(((Number) o).intValue());
                } else if (o instanceof Numeric) {
                    return ((PEnumDescriptor) descriptor).findById(((Numeric) o).asInteger());
                } else if (o instanceof CharSequence) {
                    return ((PEnumDescriptor) descriptor).findByName(o.toString());
                }
                throw new ProvidenceConfigException("Unable to cast " + o.getClass().getSimpleName() + " to enum " + descriptor.getQualifiedName());
            case MESSAGE:
                if (o instanceof PMessage) {
                    // Assume the correct message.
                    PMessage message = (PMessage) o;
                    if (descriptor.equals(message.descriptor())) {
                        return o;
                    }
                    throw new ProvidenceConfigException("Message type mismatch: " + message.descriptor().getQualifiedName() +
                                                        " is not compatible with " + descriptor.getQualifiedName());
                } else {
                    throw new ProvidenceConfigException(o.getClass().getSimpleName() + " is not compatible with message " +
                                                        descriptor.getQualifiedName());
                }
            case STRING:
                return asString(o);
            case BINARY:
                if (o instanceof Binary) {
                    return o;
                } else {
                    throw new ProvidenceConfigException(o.getClass()
                                                         .getSimpleName() + " is not compatible with binary");
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
            Collection<T> out = new ArrayList<>();
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
            boolean sorted = value instanceof TreeMap ||
                             value instanceof ImmutableSortedMap;
            Map<K,V> out = sorted ? new TreeMap<>() : new LinkedHashMap<>();
            for (Map.Entry item : ((Map<?,?>) value).entrySet()) {
                out.put((K) asType(keyType, item.getKey()),
                        (V) asType(itemType, item.getValue()));
            }
            return out;
        }
        throw new ProvidenceConfigException(
                "Unable to convert " + value.getClass().getSimpleName() + " to a collection");
    }

    static void consumeValue(@Nonnull ProvidenceConfigContext context,
                             @Nonnull Tokenizer tokenizer,
                             @Nonnull Token token) throws IOException {
        boolean isMessage = false;

        if (UNDEFINED.equals(token.asString())) {
            // ignore undefined.
            return;
        } else if (token.asString().equals(Token.B64)) {
            tokenizer.expectSymbol("b64 body start", Token.kParamsStart);
            tokenizer.readBinary(Token.kParamsEnd);
        } else if (token.asString().equals(Token.HEX)) {
            tokenizer.expectSymbol("hex body start", Token.kParamsStart);
            tokenizer.readBinary(Token.kParamsEnd);
        } else if (token.isReferenceIdentifier()) {
            if (!tokenizer.peek("message start").isSymbol(Token.kMessageStart)) {
                // just a reference.
                return;
            }
            // reference + message.
            isMessage = true;
            token = tokenizer.expect("start of message");
        }

        if (token.isSymbol(Token.kMessageStart)) {
            // message or map.
            token = tokenizer.expect("map or message first entry");
            if (token.isSymbol(Token.kMessageEnd)) {
                return;
            }

            Token firstSep = tokenizer.peek("First separator");

            if (!isMessage &&
                !firstSep.isSymbol(Token.kFieldValueSep) &&
                !firstSep.isSymbol(Token.kMessageStart) &&
                !firstSep.isSymbol(DEFINE_REFERENCE)) {
                // assume map.
                while (!token.isSymbol(Token.kMessageEnd)) {
                    if (!token.isIdentifier() && token.isReferenceIdentifier()) {
                        throw new TokenizerException(token, "Invalid map key: " + token.asString())
                                .setLine(tokenizer.getLine());
                    }
                    consumeValue(context, tokenizer, token);
                    tokenizer.expectSymbol("key value sep.", Token.kKeyValueSep);
                    consumeValue(context, tokenizer, tokenizer.expect("map value"));

                    // maps do *not* require separator, but allows ',' separator, and separator after last.
                    token = nextNotLineSep(tokenizer, "map key, sep or end");
                }
            } else {
                // assume message.
                while (!token.isSymbol(Token.kMessageEnd)) {
                    if (!token.isIdentifier()) {
                        throw new TokenizerException(token, "Invalid field name: " + token.asString())
                                .setLine(tokenizer.getLine());
                    }
                    token = tokenizer.expect("field value sep");
                    if (token.isSymbol(DEFINE_REFERENCE)) {
                        token = tokenizer.expectIdentifier("reference name");
                        context.setReference(
                                context.initReference(token, tokenizer),
                                null);
                        token = tokenizer.expect("field value sep");
                    }

                    if (token.isSymbol(Token.kMessageStart)) {
                        // direct inheritance of message field.
                        consumeValue(context, tokenizer, token);
                    } else if (token.isSymbol(Token.kFieldValueSep)) {
                        consumeValue(context, tokenizer, tokenizer.expect("field value"));
                    } else {
                        throw new TokenizerException(token, "Unknown field value sep: " + token.asString())
                                .setLine(tokenizer.getLine());
                    }
                    token = nextNotLineSep(tokenizer, "message field or end");
                }
            }
        } else if (token.isSymbol(Token.kListStart)) {
            token = tokenizer.expect("list value or end");
            while (!token.isSymbol(Token.kListEnd)) {
                consumeValue(context, tokenizer, token);
                // lists and sets require list separator (,), and allows trailing separator.
                if (tokenizer.expectSymbol("list separator or end", Token.kLineSep1, Token.kListEnd) == Token.kListEnd) {
                    break;
                }
                token = tokenizer.expect("list value or end");
            }
       }
    }

    static Token nextNotLineSep(Tokenizer tokenizer, String message) throws IOException {
        if (tokenizer.peek().isSymbol(Token.kLineSep1) ||
            tokenizer.peek().isSymbol(Token.kLineSep2)) {
            tokenizer.expect(message);
        }
        return tokenizer.expect(message);
    }

    public static Path canonicalFileLocation(@Nonnull Path file) throws ProvidenceConfigException {
        if (!file.isAbsolute()) {
            file = file.toAbsolutePath();
        }
        if (file.getParent() == null) {
            throw new ProvidenceConfigException("Trying to read root directory");
        }
        try {
            Path dir = readCanonicalPath(file.getParent());
            return dir.resolve(file.getFileName().toString());
        } catch (IOException e) {
            throw new ProvidenceConfigException(e, e.getMessage());
        }
    }

    /**
     * Resolve a file path within the source roots.
     *
     * @param reference A file or directory reference
     * @param path The file reference to resolve.
     * @return The resolved file.
     * @throws FileNotFoundException When the file is not found.
     * @throws IOException When unable to make canonical path.
     */
    static Path resolveFile(Path reference, String path) throws IOException {
        if (reference == null) {
            Path file = canonicalFileLocation(Paths.get(path));
            if (Files.exists(file)) {
                if (Files.isRegularFile(file)) {
                    return file;
                }
                throw new FileNotFoundException(path + " is a directory, expected file");
            }
            throw new FileNotFoundException("File " + path + " not found");
        } else if (path.startsWith("/")) {
            throw new FileNotFoundException("Absolute path includes not allowed: " + path);
        } else {
            // Referenced files are referenced from the real file,
            // not from symlink location, in case of sym-linked files.
            // this way include references are always consistent, but
            // files can be referenced via symlinks if needed.
            reference = readCanonicalPath(reference);
            if (!Files.isDirectory(reference)) {
                reference = reference.getParent();
            }
            Path file = canonicalFileLocation(reference.resolve(path));

            if (Files.exists(file)) {
                if (Files.isRegularFile(file)) {
                    return file;
                }
                throw new FileNotFoundException(path + " is a directory, expected file");
            }
            throw new FileNotFoundException("Included file " + path + " not found");
        }
    }

    private ProvidenceConfigUtil() {}
}
