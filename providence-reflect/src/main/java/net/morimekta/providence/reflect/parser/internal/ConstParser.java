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
package net.morimekta.providence.reflect.parser.internal;

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.serializer.pretty.Token;
import net.morimekta.providence.util.TypeRegistry;
import net.morimekta.util.Binary;
import net.morimekta.util.Strings;
import net.morimekta.util.json.JsonException;
import net.morimekta.util.json.JsonToken;
import net.morimekta.util.json.JsonTokenizer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Parsing thrift constants from string to actual value. This uses a JSON like
 * format with some allowed special references.
 *
 * <ul>
 *   <li> Enums can be 'EnumName.VALUE' or 'program.EnumName.VALUE', no quotes.
 *   <li> Enums values can be used as integer constants.
 *   <li> String literals can be single-quoted.
 *   <li> Lists and map entries have optional separator ',', ';' and none.
 *   <li> Last element of list or map may have an ending list separator.
 *   <li> Map keys may be non-string values like numbers or enum value reference.
 * </ul>
 */
public class ConstParser {
    private static final String NULL = "null";

    private final TypeRegistry registry;
    private final String       programContext;
    private final int          startLineNo;
    private final int          startLinePos;

    public ConstParser(TypeRegistry registry,
                       String programContext,
                       int startLineNo,
                       int startLinePos) {
        this.registry = registry;
        this.programContext = programContext;
        this.startLineNo = startLineNo;
        this.startLinePos = startLinePos;
    }

    /**
     *
     *
     * @param inputStream Input stream to parse.
     * @param type The constant type descriptor.
     * @return The parsed constant value.
     * @throws ParseException If not able to parse the constant.
     */
    public Object parse(InputStream inputStream, PDescriptor type) throws ParseException {
        try {
            ThriftTokenizer tokenizer = new ThriftTokenizer(inputStream);
            return parseTypedValue(tokenizer.expect("const value"), tokenizer, type, true);
        } catch (ParseException e) {
            // The line is probably not representative of the "original".
            if (startLinePos > 0 && e.getLineNo() == 1) {
                e.setLinePos(e.getLinePos() + startLinePos - 1);
                if (startLinePos > 3) {
                    e.setLine(Strings.times(".", startLinePos - 4) +
                              " = " +
                              e.getLine());
                } else {
                    e.setLine(Strings.times(" ", startLinePos - 1) +
                              e.getLine());
                }
            }
            if (startLineNo > 0) {
                e.setLineNo(e.getLineNo() + startLineNo - 1);
            }
            throw e;
        } catch (IOException e) {
            throw new ParseException(e, "Unable to read const data from input: " + e.getMessage());
        }
    }

    /**
     * Parse JSON object as a message.
     *
     * @param tokenizer The JSON tokenizer.
     * @param type      The message type.
     * @param <Message> Message generic type.
     * @param <Field>   Message field type.
     * @return The parsed message.
     */
    private <Message extends PMessage<Message, Field>, Field extends PField>
    Message parseMessage(ThriftTokenizer tokenizer,
                         PMessageDescriptor<Message, Field> type) throws IOException {
        PMessageBuilder<Message, Field> builder = type.builder();

        if (tokenizer.peek("checking for empty").isSymbol(Token.kMessageEnd)) {
            tokenizer.next();
            return builder.build();
        }

        while (true) {
            Token token = tokenizer.expect("message field name",
                                           t -> t.isStringLiteral() ||
                                                t.strEquals(ThriftTokenizer.kBlockCommentStart) ||
                                                t.strEquals(ThriftTokenizer.kLineCommentStart));
            if (token.strEquals(ThriftTokenizer.kLineCommentStart)) {
                int c;
                while ((c = tokenizer.read()) >= 0) {
                    if (c == '\n') break;
                }
                continue;
            } else if (token.strEquals(ThriftTokenizer.kBlockCommentStart)) {
                int c;
                while ((c = tokenizer.read()) >= 0) {
                    if (c == '*') {
                        c = tokenizer.read();
                        if (c == '/') {
                            break;
                        }
                    }
                }
                continue;
            }
            Field field = type.findFieldByName(token.decodeLiteral(true));
            if (field == null) {
                throw tokenizer.failure(token, "Not a valid field name: " + token.decodeLiteral(true));
            }
            tokenizer.expectSymbol("message key-value sep", Token.kKeyValueSep);

            builder.set(field.getId(),
                        parseTypedValue(tokenizer.expect("parsing field value"), tokenizer, field.getDescriptor(), false));

            token = tokenizer.peek("optional line sep or message end");
            if (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2)) {
                tokenizer.next();
                token = tokenizer.peek("optional message end");
            }
            if (token.isSymbol(Token.kMessageEnd)) {
                tokenizer.next();
                break;
            }
        }

        return builder.build();
    }

    private Object parseTypedValue(Token token, ThriftTokenizer tokenizer, PDescriptor valueType, boolean allowNull)
            throws IOException {
        switch (valueType.getType()) {
            case BOOL:
                if (token.isIdentifier()) {
                    return Boolean.parseBoolean(token.asString());
                } else if (token.isInteger()) {
                    return token.parseInteger() != 0L;
                }
                throw tokenizer.failure(token, "Not boolean value for bool: " + token.asString());
            case BYTE:
                if (token.isInteger()) {
                    return (byte) token.parseInteger();
                }
                return (byte) findEnumValue(token.asString(), token, tokenizer, "byte");
            case I16:
                if (token.isInteger()) {
                    return (short) token.parseInteger();
                }
                return (short) findEnumValue(token.asString(), token, tokenizer, "i16");
            case I32:
                if (token.isInteger()) {
                    return (int) token.parseInteger();
                }
                return findEnumValue(token.asString(), token, tokenizer, "i32");
            case I64:
                if (token.isInteger()) {
                    return token.parseInteger();
                }
                return (long) findEnumValue(token.asString(), token, tokenizer, "i64");
            case DOUBLE:
                if (token.isInteger() || token.isReal()) {
                    return token.parseDouble();
                }
                throw tokenizer.failure(token, token.asString() + " is not a valid double value.");
            case STRING:
                if (token.isStringLiteral()) {
                    return token.decodeLiteral(true);
                } else if (allowNull && token.asString().equals(NULL)) {
                    return null;
                }
                throw tokenizer.failure(token, "Not a valid string value.");
            case BINARY:
                if (token.isStringLiteral()) {
                    return parseBinary(token.substring(1, -1)
                                            .asString());
                } else if (allowNull && token.asString().equals(NULL)) {
                    return null;
                }
                throw tokenizer.failure(token, "Not a valid binary value.");
            case ENUM: {
                PEnumBuilder<?> eb = ((PEnumDescriptor<?>) valueType).builder();
                String name = token.asString();
                if (name.startsWith(valueType.getName())) {
                    name = name.substring(valueType.getName()
                                                   .length() + 1);
                } else if (name.startsWith(valueType.getQualifiedName())) {
                    name = name.substring(valueType.getQualifiedName()
                                                   .length() + 1);
                }
                Object ev = eb.setByName(name).build();
                if (ev == null) {
                    if (allowNull && token.asString().equals(NULL)) {
                        return null;
                    }
                    throw tokenizer.failure(token, "No such " + valueType.getQualifiedName() + " enum value.");
                }
                return ev;
            }
            case MESSAGE: {
                if (token.isSymbol(Token.kMessageStart)) {
                    return parseMessage(tokenizer, (PMessageDescriptor<?, ?>) valueType);
                } else if (allowNull && token.asString().equals(NULL)) {
                    // messages can be null values in constants.
                    return null;
                }
                throw tokenizer.failure(token, "Not a valid message start.");
            }
            case LIST: {
                PDescriptor itemType = ((PList<?>) valueType).itemDescriptor();
                ArrayList<Object> list = new ArrayList<>();

                if (!token.isSymbol(Token.kListStart)) {
                    throw tokenizer.failure(token, "Expected list start, found " + token.asString());
                }

                if (tokenizer.peek("checking for empty list")
                             .isSymbol(Token.kListEnd)) {
                    tokenizer.next();
                    return list;
                }

                while (true) {
                    token = tokenizer.expect("list item value");
                    if (token.strEquals(ThriftTokenizer.kLineCommentStart)) {
                        int c;
                        while ((c = tokenizer.read()) >= 0) {
                            if (c == '\n') break;
                        }
                        continue;
                    } else if (token.strEquals(ThriftTokenizer.kBlockCommentStart)) {
                        int c;
                        while ((c = tokenizer.read()) >= 0) {
                            if (c == '*') {
                                c = tokenizer.read();
                                if (c == '/') {
                                    break;
                                }
                            }
                        }
                        continue;
                    }
                    list.add(parseTypedValue(token, tokenizer, itemType, false));

                    Token sep = tokenizer.peek("optional item sep");
                    if (sep.isSymbol(Token.kLineSep1) || sep.isSymbol(Token.kLineSep2)) {
                        tokenizer.next();
                        sep = tokenizer.peek("check for set end");
                    }
                    if (sep.isSymbol(Token.kListEnd)) {
                        tokenizer.next();
                        break;
                    }
                }

                return list;
            }
            case SET: {
                PDescriptor itemType = ((PSet<?>) valueType).itemDescriptor();
                HashSet<Object> set = new HashSet<>();

                if (!token.isSymbol(Token.kListStart)) {
                    throw tokenizer.failure(token, "Expected list start, found " + token.asString());
                }

                if (tokenizer.peek("checking for empty list")
                             .isSymbol(Token.kListEnd)) {
                    tokenizer.next();
                    return set;
                }

                while (true) {
                    token = tokenizer.expect("set item value");
                    if (token.strEquals(ThriftTokenizer.kLineCommentStart)) {
                        int c;
                        while ((c = tokenizer.read()) >= 0) {
                            if (c == '\n') break;
                        }
                        continue;
                    } else if (token.strEquals(ThriftTokenizer.kBlockCommentStart)) {
                        int c;
                        while ((c = tokenizer.read()) >= 0) {
                            if (c == '*') {
                                c = tokenizer.read();
                                if (c == '/') {
                                    break;
                                }
                            }
                        }
                        continue;
                    }
                    set.add(parseTypedValue(token, tokenizer, itemType, false));

                    Token sep = tokenizer.peek("optional item sep");
                    if (sep.isSymbol(Token.kLineSep1) || sep.isSymbol(Token.kLineSep2)) {
                        tokenizer.next();
                        sep = tokenizer.peek("check for set end");
                    }
                    if (sep.isSymbol(Token.kListEnd)) {
                        tokenizer.next();
                        break;
                    }
                }
                return set;
            }
            case MAP: {
                PDescriptor itemType = ((PMap<?, ?>) valueType).itemDescriptor();
                PDescriptor keyType = ((PMap<?, ?>) valueType).keyDescriptor();

                HashMap<Object, Object> map = new HashMap<>();

                if (!token.isSymbol(Token.kMessageStart)) {
                    throw tokenizer.failure(token, "Expected map start, found " + token.asString());
                }

                if (tokenizer.peek("checking for empty map")
                             .isSymbol(Token.kMessageEnd)) {
                    tokenizer.next();
                    return map;
                }

                while (true) {
                    token = tokenizer.expect("map key");
                    if (token.strEquals(ThriftTokenizer.kLineCommentStart)) {
                        int c;
                        while ((c = tokenizer.read()) >= 0) {
                            if (c == '\n') break;
                        }
                        continue;
                    } else if (token.strEquals(ThriftTokenizer.kBlockCommentStart)) {
                        int c;
                        while ((c = tokenizer.read()) >= 0) {
                            if (c == '*') {
                                c = tokenizer.read();
                                if (c == '/') {
                                    break;
                                }
                            }
                        }
                        continue;
                    }

                    Object key;
                    if (token.isStringLiteral()) {
                        key = parsePrimitiveKey(token.decodeLiteral(true), token, tokenizer, keyType);
                    } else {
                        if (keyType.getType().equals(PType.STRING) ||
                            keyType.getType().equals(PType.BINARY)) {
                            throw tokenizer.failure(token, "Expected string literal for string key");
                        }
                        key = parsePrimitiveKey(token.asString(), token, tokenizer, keyType);
                    }
                    tokenizer.expectSymbol("map KV separator", Token.kKeyValueSep);
                    map.put(key, parseTypedValue(tokenizer.expect("map value"), tokenizer, itemType, false));

                    Token sep = tokenizer.peek("optional item sep");
                    if (sep.isSymbol(Token.kLineSep1) || sep.isSymbol(Token.kLineSep2)) {
                        tokenizer.next();
                        sep = tokenizer.peek("check for map end");
                    }
                    if (sep.isSymbol(Token.kMessageEnd)) {
                        tokenizer.next();
                        break;
                    }
                }

                return map;
            }
            default:
                throw new IllegalArgumentException("Unhandled item type " + valueType.getQualifiedName());
        }
    }

    private Object parsePrimitiveKey(String key, Token token, ThriftTokenizer tokenizer, PDescriptor keyType)
            throws IOException {
        switch (keyType.getType()) {
            case ENUM:
                PEnumBuilder<?> eb = ((PEnumDescriptor<?>) keyType).builder();
                if (Strings.isInteger(key)) {
                    return eb.setById(Integer.parseInt(key))
                             .build();
                } else {
                    if (key.startsWith(keyType.getProgramName() + "." + keyType.getName() + ".")) {
                        // Check for qualified type prefixed identifier ( e.g. program.EnumName.VALUE ).
                        key = key.substring(keyType.getProgramName().length() + keyType.getName().length() + 2);
                    } else if (key.startsWith(keyType.getName() + ".")) {
                        // Check for type prefixed identifier ( e.g. EnumName.VALUE ).
                        key = key.substring(keyType.getName().length() + 1);
                    }
                    return eb.setByName(key)
                             .build();
                }
            case BOOL:
                return Boolean.parseBoolean(key);
            case BYTE:
                if (Strings.isInteger(key)) {
                    return Byte.parseByte(key);
                } else {
                    return (byte) findEnumValue(key, token, tokenizer, "byte");
                }
            case I16:
                if (Strings.isInteger(key)) {
                    return Short.parseShort(key);
                } else {
                    return (short) findEnumValue(key, token, tokenizer, "i16");
                }
            case I32:
                if (Strings.isInteger(key)) {
                    return Integer.parseInt(key);
                } else {
                    return findEnumValue(key, token, tokenizer, "i32");
                }
            case I64:
                if (Strings.isInteger(key)) {
                    return Long.parseLong(key);
                } else {
                    return (long) findEnumValue(key, token, tokenizer, "i64");
                }
            case DOUBLE: {
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(key.getBytes(US_ASCII));
                    JsonTokenizer tokener = new JsonTokenizer(bais);
                    JsonToken jt = tokener.expect("parsing double value");
                    return jt.doubleValue();
                } catch (IOException | JsonException e) {
                    throw new ParseException(e, "Unable to parse double value");
                }
            }
            case STRING:
                return key;
            case BINARY:
                return parseBinary(key);
            default:
                throw new ParseException("Illegal key type: " + keyType.getType());
        }
    }

    private int findEnumValue(String identifier, Token token, ThriftTokenizer tokenizer, String expectedType)
            throws IOException {
        String[] parts = identifier.split("[.]");
        String typeName;
        String valueName;
        if (parts.length == 3) {
            typeName = parts[0] + "." + parts[1];
            valueName = parts[2];
        } else if (parts.length == 2) {
            typeName = parts[0];
            valueName = parts[1];
        } else {
            throw tokenizer.failure(token, identifier + " is not a valid " + expectedType + " value.");
        }

        @SuppressWarnings("unchecked")
        PDeclaredDescriptor descriptor = registry.getDeclaredType(typeName, programContext);
        if (descriptor != null && descriptor instanceof PEnumDescriptor) {
            PEnumDescriptor desc = (PEnumDescriptor) descriptor;
            PEnumValue value = desc.findByName(valueName);
            if (value != null) {
                return value.asInteger();
            }
        }

        throw tokenizer.failure(token, identifier + " is not a valid " + expectedType + " value.");
    }

    /**
     * Parse a string into binary format using the same rules as above.
     *
     * @param value The string to decode.
     * @return The decoded byte array.
     */
    private Binary parseBinary(String value) {
        return Binary.fromBase64(value);
    }
}
