/*
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
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.util.Base64;
import net.morimekta.util.Strings;
import net.morimekta.util.json.JsonException;
import net.morimekta.util.json.JsonToken;
import net.morimekta.util.json.JsonTokenizer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Parsing thrift constants from string to actual value. This uses the JSON
 * format with some allowed special references. So enum values are always
 * expected to be 'EnumName.VALUE' (quotes or no quotes), and map keys are not
 * expected to be string literals.
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class ConstParser {
    public ConstParser() {}

    public Object parse(InputStream inputStream, PDescriptor type) throws ParseException {
        try {
            JsonTokenizer tokenizer = new JsonTokenizer(inputStream);
            return parseTypedValue(tokenizer.expect("const value"), tokenizer, type);
        } catch (JsonException e) {
            throw new ParseException(e, "Unable to parse JSON: " + e.getMessage());
        } catch (IOException e) {
            throw new ParseException(e, "Unable to read JSON data from input: " + e.getMessage());
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
    Message parseMessage(JsonTokenizer tokenizer,
                         PStructDescriptor<Message, Field> type)
            throws IOException, JsonException {
        PMessageBuilder<Message, Field> builder = type.builder();

        if (tokenizer.peek("checking for empty").isSymbol(JsonToken.kMapEnd)) {
            tokenizer.next();
            return builder.build();
        }

        char sep = JsonToken.kMapStart;
        while (sep != JsonToken.kMapEnd) {
            JsonToken token = tokenizer.expectString("message field name");
            Field field = type.getField(token.substring(1, -1)
                                             .asString());
            if (field == null) {
                throw new JsonException("Not a valid field name: " + token.substring(1, -1));
            }
            tokenizer.expectSymbol("parsing message key-value sep", JsonToken.kKeyValSep);

            builder.set(field.getKey(),
                        parseTypedValue(tokenizer.expect("parsing field value"), tokenizer, field.getDescriptor()));

            sep = tokenizer.expectSymbol("", JsonToken.kListSep, JsonToken.kMapEnd);
        }

        return builder.build();
    }

    private Object parseTypedValue(JsonToken token, JsonTokenizer tokenizer, PDescriptor valueType)
            throws IOException, JsonException {
        switch (valueType.getType()) {
            case BOOL:
                if (token.isBoolean()) {
                    return token.booleanValue();
                } else if (token.isInteger()) {
                    return token.longValue() != 0;
                }
                throw new JsonException("Not boolean value for bool: " + token.asString(), tokenizer, token);
            case BYTE:
                if (token.isInteger()) {
                    return token.byteValue();
                }
                throw new JsonException(token.asString() + " is not a valid byte value.", tokenizer, token);
            case I16:
                if (token.isInteger()) {
                    return token.shortValue();
                }
                throw new JsonException(token.asString() + " is not a valid short value.", tokenizer, token);
            case I32:
                if (token.isInteger()) {
                    return token.intValue();
                }
                throw new JsonException(token.asString() + " is not a valid int value.", tokenizer, token);
            case I64:
                if (token.isInteger()) {
                    return token.longValue();
                }
                throw new JsonException(token.asString() + " is not a valid long value.", tokenizer, token);
            case DOUBLE:
                if (token.isInteger() || token.isDouble()) {
                    return token.doubleValue();
                }
                throw new JsonException(token.asString() + " is not a valid double value.", tokenizer, token);
            case STRING:
                if (token.isLiteral()) {
                    return token.decodeJsonLiteral();
                }
                throw new JsonException("Not a valid string value.", tokenizer, token);
            case BINARY:
                if (token.isLiteral()) {
                    return parseBinary(token.substring(1, -1)
                                            .asString());
                }
                throw new JsonException("Not a valid binary value.", tokenizer, token);
            case ENUM: {
                PEnumBuilder<?> eb = ((PEnumDescriptor<?>) valueType).builder();
                String name = token.asString();
                if (name.startsWith(valueType.getName())) {
                    name = name.substring(valueType.getName()
                                                   .length() + 1);
                }
                Object ev = eb.setByName(name)
                              .build();
                if (ev == null) {
                    throw new JsonException("No such " + valueType.getQualifiedName(null) + " enum value.",
                                            tokenizer,
                                            token);
                }
                return ev;
            }
            case MESSAGE: {
                if (token.isSymbol(JsonToken.kMapStart)) {
                    return parseMessage(tokenizer, (PStructDescriptor<?, ?>) valueType);
                }
                throw new JsonException("Not a valid message start.", tokenizer, token);
            }
            case LIST: {
                PDescriptor itemType = ((PList<?>) valueType).itemDescriptor();
                LinkedList<Object> list = new LinkedList<>();

                if (tokenizer.peek("checking for empty list")
                             .isSymbol(JsonToken.kListEnd)) {
                    return list;
                }

                char sep = JsonToken.kListStart;
                while (sep != JsonToken.kListEnd) {
                    list.add(parseTypedValue(tokenizer.expect("list item value"), tokenizer, itemType));
                    sep = tokenizer.expectSymbol("parsing list item separator", JsonToken.kListEnd, JsonToken.kListSep);
                }

                return list;
            }
            case SET: {
                PDescriptor itemType = ((PSet<?>) valueType).itemDescriptor();
                HashSet<Object> set = new HashSet<>();

                if (tokenizer.peek("checking for empty list")
                             .isSymbol(JsonToken.kListEnd)) {
                    return set;
                }

                char sep = JsonToken.kListStart;
                while (sep != JsonToken.kListEnd) {
                    set.add(parseTypedValue(tokenizer.expect("set item value"), tokenizer, itemType));
                    sep = tokenizer.expectSymbol("parsing set item separator", JsonToken.kListEnd, JsonToken.kListSep);
                }
                return set;
            }
            case MAP: {
                PDescriptor itemType = ((PMap<?, ?>) valueType).itemDescriptor();
                PDescriptor keyType = ((PMap<?, ?>) valueType).keyDescriptor();

                HashMap<Object, Object> map = new HashMap<>();

                if (tokenizer.peek("checking for empty map")
                             .isSymbol(JsonToken.kMapEnd)) {
                    return map;
                }

                char sep = JsonToken.kMapStart;
                while (sep != JsonToken.kMapEnd) {
                    Object key;
                    if (token.isLiteral()) {
                        key = parsePrimitiveKey(token.decodeJsonLiteral(), keyType);
                    } else {
                        if (keyType.getType().equals(PType.STRING) ||
                            keyType.getType().equals(PType.BINARY)) {
                            throw new JsonException("Expected string literal for string key", tokenizer, token);
                        }
                        key = parsePrimitiveKey(token.asString(), keyType);
                    }
                    tokenizer.expectSymbol("parsing map (kv)", JsonToken.kKeyValSep);
                    map.put(key, parseTypedValue(tokenizer.expect("parsing map value."), tokenizer, itemType));

                    sep = tokenizer.expectSymbol("parsing set item separator", JsonToken.kMapEnd, JsonToken.kListSep);
                }

                return map;
            }
            default:
                throw new IllegalArgumentException("Unhandled item type " + valueType.getQualifiedName(null));
        }
    }

    private Object parsePrimitiveKey(String key, PDescriptor keyType) throws IOException {
        switch (keyType.getType()) {
            case ENUM:
                PEnumBuilder<?> eb = ((PEnumDescriptor<?>) keyType).builder();
                if (Strings.isInteger(key)) {
                    return eb.setByValue(Integer.parseInt(key))
                             .build();
                } else {
                    // Check for qualified name ( e.g. EnumName.VALUE ).
                    if (key.startsWith(keyType.getName())) {
                        key = key.substring(keyType.getName()
                                                   .length() + 1);
                    }
                    return eb.setByName(key)
                             .build();
                }
            case BOOL:
                return Boolean.parseBoolean(key);
            case BYTE:
                return Byte.parseByte(key);
            case I16:
                return Short.parseShort(key);
            case I32:
                return Integer.parseInt(key);
            case I64:
                return Long.parseLong(key);
            case DOUBLE:
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(key.getBytes(US_ASCII));
                    JsonTokenizer tokener = new JsonTokenizer(bais);
                    JsonToken token = tokener.expect("parsing double value");
                    return token.doubleValue();
                } catch (JsonException e) {
                    throw new IllegalArgumentException("Unable to parse double from key \"" +
                                                       key + "\"", e);
                }
            case STRING:
                return key;
            case BINARY:
                return parseBinary(key);
            default:
                throw new IllegalArgumentException("Illegal key type: " + keyType.getType());
        }
    }

    /**
     * Parse a string into binary format using the same rules as above.
     *
     * @param value The string to decode.
     * @return The decoded byte array.
     */
    private byte[] parseBinary(String value) throws IOException {
        return Base64.decode(value);
    }
}
