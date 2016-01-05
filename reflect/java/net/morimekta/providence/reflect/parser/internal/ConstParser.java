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
import net.morimekta.providence.descriptor.*;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.util.PBase64Utils;
import net.morimekta.providence.util.PStringUtils;
import net.morimekta.providence.util.json.JsonException;
import net.morimekta.providence.util.json.JsonToken;
import net.morimekta.providence.util.json.JsonTokenizer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

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
            throw new ParseException("Unable to parse JSON: ", e);
        } catch (IOException e) {
            throw new ParseException("Unable to read JSON from input", e);
        }
    }

    /**
     * Parse JSON object as a message.
     *
     * @param tokenizer The JSON tokenizer.
     * @param type The message type.
     * @param <T> Message generic type.
     * @return The parsed message.
     */
    private <T extends PMessage<T>, F extends PField> T parseMessage(JsonTokenizer tokenizer, PStructDescriptor<T,F> type) throws IOException, JsonException {
        PMessageBuilder<T> builder = type.factory().builder();

        JsonToken token = tokenizer.expect("parsing message field id");
        while (!JsonToken.kMapEnd.equals(token.value)) {
            F field = type.getField(token.value);
            if (field == null) {
                throw new JsonException("Not a valid field name: " + token.value);
            }
            tokenizer.expectSymbol("", JsonToken.kKeyValSepChar);

            builder.set(field.getKey(), parseTypedValue(tokenizer.expect("parsing field value."), tokenizer, field.getDescriptor()));

            if (tokenizer.expectSymbol("Message field separator.", JsonToken.kMapEndChar, JsonToken.kListSepChar) == 0) {
                break;
            }
            token = tokenizer.expect("parsing message field id.");
        }

        return builder.build();
    }

    private Object parseTypedValue(JsonToken token, JsonTokenizer tokenizer, PDescriptor valueType) {
        try {
            switch (valueType.getType()) {
                case BOOL:
                    if (token.isBoolean()) {
                        return token.booleanValue();
                    } else if (token.isInteger()) {
                        return token.longValue() != 0;
                    }
                    throw new JsonException("Not boolean value for bool: " + token.value,
                                            tokenizer,
                                            token);
                case BYTE:
                    if (token.isInteger()) {
                        return token.byteValue();
                    }
                    throw new JsonException(token.value + " is not a valid byte value.",
                                            tokenizer,
                                            token);
                case I16:
                    if (token.isInteger()) {
                        return token.shortValue();
                    }
                    throw new JsonException(token.value + " is not a valid short value.",
                                            tokenizer,
                                            token);
                case I32:
                    if (token.isInteger()) {
                        return token.intValue();
                    }
                    throw new JsonException(token.value + " is not a valid int value.",
                                            tokenizer,
                                            token);
                case I64:
                    if (token.isInteger()) {
                        return token.longValue();
                    }
                    throw new JsonException(token.value + " is not a valid long value.",
                                            tokenizer,
                                            token);
                case DOUBLE:
                    if (token.isInteger() || token.isReal()) {
                        return token.doubleValue();
                    }
                    throw new JsonException(token.value + " is not a valid double value.",
                                            tokenizer,
                                            token);
                case STRING:
                    if (token.isLiteral()) {
                        return token.value;
                    }
                    throw new JsonException("Not a valid string value.", tokenizer, token);
                case BINARY:
                    if (token.isLiteral()) {
                        return parseBinary(token.value);
                    }
                    throw new JsonException("Not a valid binary value.", tokenizer, token);
                case ENUM:
                    PEnumBuilder<?> eb = ((PEnumDescriptor<?>) valueType).factory().builder();
                    String name = token.value;
                    if (name.startsWith(valueType.getName())) {
                        name = name.substring(valueType.getName().length() + 1);
                    }
                    Object ev = eb.setByName(name).build();
                    if (ev == null) {
                        throw new JsonException("No such " + valueType.getQualifiedName(null) + " enum value.", tokenizer, token);
                    }
                    return ev;
                case MESSAGE:
                    if (JsonToken.kMapStart.equals(token.value)) {
                        return parseMessage(tokenizer, (PStructDescriptor<?,?>) valueType);
                    }
                    throw new JsonException("Not a valid message start.", tokenizer, token);
                case LIST:
                    PDescriptor itemType = ((PList<?>) valueType).itemDescriptor();
                    LinkedList<Object> list = new LinkedList<>();

                    if (!JsonToken.kListStart.equals(token.value)) {
                        throw new JsonException("Not a valid list start token.", tokenizer, token);
                    }
                    token = tokenizer.expect("parsing list item.");
                    while (!JsonToken.kListEnd.equals(token.value)) {
                        list.add(parseTypedValue(token, tokenizer, itemType));
                        if (tokenizer.expectSymbol("parsing list separator", JsonToken.kListEndChar, JsonToken.kListSepChar) == 0) {
                            break;
                        }
                        token = tokenizer.expect("parsing list item.");
                    }
                    return list;
                case SET:
                    itemType = ((PSet<?>) valueType).itemDescriptor();
                    HashSet<Object> set = new HashSet<>();

                    if (!JsonToken.kListStart.equals(token.value)) {
                        throw new JsonException("Not a valid set list start token.",
                                                tokenizer,
                                                token);
                    }
                    token = tokenizer.expect("parsing set list item.");
                    while (!JsonToken.kListEnd.equals(token.value)) {
                        set.add(parseTypedValue(token, tokenizer, itemType));
                        if (tokenizer.expectSymbol("parsing list separator", JsonToken.kListEndChar, JsonToken.kListSepChar) == 0) {
                            break;
                        }
                        token = tokenizer.expect("parsing set list item.");
                    }
                    return set;
                case MAP:
                    itemType = ((PMap<?, ?>) valueType).itemDescriptor();

                    PDescriptor keyType = ((PMap<?, ?>) valueType).keyDescriptor();
                    HashMap<Object, Object> map = new HashMap<>();

                    if (!JsonToken.kMapStart.equals(token.value)) {
                        throw new JsonException("Not a valid map start token.", tokenizer, token);
                    }
                    token = tokenizer.expect("parsing map key.");
                    while (!JsonToken.kMapEnd.equals(token.value)) {
                        Object key;
                        if (token.isLiteral()) {
                            key = parsePrimitiveKey(token.value, keyType);
                        } else {
                            if (keyType.getType().equals(PType.STRING) || keyType.getType().equals(PType.BINARY)) {
                                throw new JsonException("Expected string literal for string key", tokenizer, token);
                            }
                            key = parsePrimitiveKey(token.value, keyType);
                        }

                        tokenizer.expectSymbol("parsing map (kv)", JsonToken.kKeyValSepChar);
                        map.put(key,
                                parseTypedValue(tokenizer.expect("parsing map value."),
                                                tokenizer,
                                                itemType));
                        if (tokenizer.expectSymbol("parsing list separator", JsonToken.kMapEndChar, JsonToken.kListSepChar) == 0) {
                            break;
                        }
                        token = tokenizer.expect("parsing map key.");
                    }
                    return map;
            }
        } catch (JsonException je) {
            throw new IllegalArgumentException("Unable to parse type value " + token.toString(),
                                               je);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read type value " + token.toString(), e);
        }

        throw new IllegalArgumentException("Unhandled item type " + valueType.getQualifiedName(null));
    }

    private Object parsePrimitiveKey(String key, PDescriptor keyType) throws IOException {
        switch (keyType.getType()) {
            case ENUM:
                PEnumBuilder<?> eb = ((PEnumDescriptor<?>) keyType).factory().builder();
                if (PStringUtils.isInteger(key)) {
                    return eb.setByValue(Integer.parseInt(key)).build();
                } else {
                    // Check for qualified name ( e.g. EnumName.VALUE ).
                    if (key.startsWith(keyType.getName())) {
                        key = key.substring(keyType.getName().length() + 1);
                    }
                    return eb.setByName(key).build();
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
                    ByteArrayInputStream bais = new ByteArrayInputStream(key.getBytes());
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
    private byte[] parseBinary(String value) {
        return PBase64Utils.decode(value);
    }
}
