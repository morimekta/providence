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

package org.apache.thrift.j2.serializer;

import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.descriptor.TContainer;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TList;
import org.apache.thrift.j2.descriptor.TMap;
import org.apache.thrift.j2.descriptor.TSet;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.util.TBase64Utils;
import org.apache.thrift.j2.util.TStringUtils;
import org.apache.thrift.j2.util.io.CountingOutputStream;
import org.apache.thrift.j2.util.json.JsonException;
import org.apache.thrift.j2.util.json.JsonToken;
import org.apache.thrift.j2.util.json.JsonTokenizer;
import org.apache.thrift.j2.util.json.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * Compact JSON serializer. This uses the most isCompact type-safe JSON format
 * allowable. There are two optional variants switching the struct field ID
 * between numeric ID and field name.
 * <p/>
 * There is also the strict mode. If strict is OFF:
 * - Unknown enum values will be ignored (as field missing).
 * - Unknown fields will be ignored.
 * - Struct validity will be ignored.
 * If strict more is ON:
 * - Unknown enum values will fail the deserialization.
 * - Unknown fields will fail the deserialization.
 * - Struct validity will fail both serialization and deserialization.
 * <p/>
 * Format is like this:
 * <p/>
 * <pre>
 * {
 *     "id":value,
 *     "structId":{ ... },
 *     "listId":[value1,value2],
 *     "mapId":{"id1":value1,"id2":value2}
 * }
 * </pre>
 * But without formatting spaces. The formatted JSON can be read normally.
 * Binary fields are base64 encoded.
 *
 * This format supports 'isCompact' struct formatting. A isCompact struct is
 * formatted as a list with fields in order from 1 to N. E.g.:
 *
 * <pre>
 * ["tag",5,6.45]
 * </pre>
 *
 * is equivalent to:
 *
 * <pre>
 * {"1":"tag","2":5,"3":6.45}
 * </pre>
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class TJsonSerializer
        extends TSerializer {
    public enum IdType {
        // print field and enums as numeric IDs and values.
        ID,
        // print field and enums as field name and enum name.
        NAME
    }

    private final boolean mStrict;
    private final IdType mIdType;
    private final IdType mEnumType;

    public TJsonSerializer() {
        this(false, IdType.ID, IdType.ID);
    }

    public TJsonSerializer(boolean strict) {
        this(strict, IdType.ID, IdType.ID);
    }

    public TJsonSerializer(IdType idType) {
        this(false, idType, idType);
    }

    public TJsonSerializer(boolean strict, IdType idType) {
        this(strict, idType, idType);
    }

    public TJsonSerializer(IdType idType, IdType enumType) {
        this(false, idType, enumType);
    }

    public TJsonSerializer(boolean strict, IdType idType, IdType enumType) {
        mStrict = strict;
        mIdType = idType;
        mEnumType = enumType;
    }

    @Override
    public int serialize(OutputStream output, TMessage<?> message) throws TSerializeException {
        CountingOutputStream counter = new CountingOutputStream(output);
        JsonWriter jsonWriter = new JsonWriter(counter);
        try {
            appendMessage(jsonWriter, message);
            jsonWriter.flush();
            counter.flush();
            return counter.getByteCount();
        } catch (JsonException e) {
            throw new TSerializeException(e, "Unable to serialize JSON");
        } catch (IOException e) {
            throw new TSerializeException(e, "Unable to write to stream");
        }
    }

    @Override
    public <T> int serialize(OutputStream output, TDescriptor<T> descriptor, T value)
            throws IOException, TSerializeException {
        CountingOutputStream counter = new CountingOutputStream(output);
        JsonWriter jsonWriter = new JsonWriter(counter);
        try {
            appendTypedValue(jsonWriter, descriptor, value);
            jsonWriter.flush();
            counter.flush();
            return counter.getByteCount();
        } catch (JsonException e) {
            throw new TSerializeException(e, "Unable to serialize JSON");
        } catch (IOException e) {
            throw new TSerializeException(e, "Unable to write to stream");
        }
    }

    @Override
    public <T> T deserialize(InputStream input, TDescriptor<T> type)
            throws TSerializeException {
        try {
            JsonTokenizer tokenizer = new JsonTokenizer(input);
            if (!tokenizer.hasNext()) {
                return null;
            }
            return parseTypedValue(tokenizer.expect("Missing value."), tokenizer, type);
        } catch (JsonException e) {
            throw new TSerializeException(e, "Unable to parse JSON");
        } catch (IOException e) {
            throw new TSerializeException(e, "Unable to read stream");
        }
    }

    /**
     * Parse JSON object as a message.
     *
     * @param tokenizer The object to parse.
     * @param type    The message type.
     * @param <T>     Message generic type.
     * @return The parsed message.
     * @throws JsonException
     */
    protected <T extends TMessage<T>> T parseMessage(JsonTokenizer tokenizer,
                                                     TStructDescriptor<T> type)
            throws TSerializeException, JsonException, IOException {
        TMessageBuilder<T> builder = type.factory().builder();

        JsonToken token = tokenizer.expect("Map key.");
        while (!JsonToken.CH.MAP_END.equals(token.getSymbol())) {
            if (!token.isLiteral()) {
                throw new JsonException("Key " + token.getToken() + " is not literal.",
                                        tokenizer,
                                        token);
            }
            String key = token.literalValue();
            TField<?> field;
            if (TStringUtils.isInteger(key)) {
                field = type.getField(Integer.parseInt(key));
            } else {
                field = type.getField(key);
            }
            tokenizer.expectSymbol(JsonToken.CH.MAP_KV_SEP, "parsing message field key sep.");

            if (field != null) {
                Object value = parseTypedValue(tokenizer.expect("parsing map value."),
                                               tokenizer,
                                               field.getDescriptor());
                builder.set(field.getKey(), value);
            } else if (mStrict) {
                throw new TSerializeException(
                        "Unknown field " + key + " for type " + type.getQualifiedName(null));
            } else {
                consume(tokenizer.expect("consuming unknown message value"), tokenizer);
            }

            token = tokenizer.expect("message entry separator");
            if (JsonToken.CH.MAP_END.equals(token.getSymbol())) {
                break;
            } else if (!JsonToken.CH.LIST_SEP.equals(token.getSymbol())) {
                throw new JsonException("Expected list separator, got " + token, tokenizer, token);
            }
            token = tokenizer.expect("Map key.");
        }

        if (mStrict && !builder.isValid()) {
            throw new TSerializeException("Type " + type.getName() + " not properly populated");
        }

        return builder.build();
    }

    /**
     * Parse JSON object as a message.
     *
     * @param tokenizer The object to parse.
     * @param type    The message type.
     * @param <T>     Message generic type.
     * @return The parsed message.
     */
    protected <T extends TMessage<T>> T parseCompactMessage(JsonTokenizer tokenizer, TStructDescriptor<T> type) throws TSerializeException, IOException, JsonException {
        TMessageBuilder<T> builder = type.factory().builder();

        int i = 0;
        JsonToken token = tokenizer.expect("List item.");
        while (!JsonToken.CH.LIST_END.equals(token.getSymbol())) {
            TField<?> field = type.getField(++i);

            if (field != null) {
                Object value = parseTypedValue(token,
                                               tokenizer,
                                               field.getDescriptor());
                builder.set(field.getKey(), value);
            } else if (mStrict) {
                throw new TSerializeException(
                        "Compact Field ID " + (i) + " outside field spectrum for type " +
                                type.getQualifiedName(null));
            } else {
                consume(token, tokenizer);
            }

            token = tokenizer.expect("List sep or end.");
            if (JsonToken.CH.LIST_END.equals(token.getSymbol())) {
                break;
            } else if (!JsonToken.CH.LIST_SEP.equals(token.getSymbol())) {
                throw new TSerializeException("ARGH!");
            }
            token = tokenizer.expect("List item");
        }

        if (mStrict && !builder.isValid()) {
            throw new TSerializeException("Type " + type.getName() + " not properly populated");
        }

        return builder.build();
    }

    private void consume(JsonToken token, JsonTokenizer tokenizer) throws IOException, JsonException {
        if (token.isSymbol()) {
            switch (token.getSymbol()) {
                case LIST_START:
                    token = tokenizer.expect("consuming list item.");
                    while (!JsonToken.CH.LIST_END.equals(token.getSymbol())) {
                        consume(token, tokenizer);
                        token = tokenizer.expect("consuming list (sep)");
                        if (JsonToken.CH.LIST_END.equals(token.getSymbol())) {
                            break;
                        } else if (!JsonToken.CH.LIST_SEP.equals(token.getSymbol())) {
                            throw new JsonException("Unexpected symbol " + token + ". " +
                                                            "Expected list separator or end.",
                                                    tokenizer,
                                                    token);
                        }
                        token = tokenizer.expect("consuming list item.");
                    }
                    break;
                case MAP_START:
                    token = tokenizer.expect("consuming map key.");
                    while (!JsonToken.CH.MAP_END.equals(token.getSymbol())) {
                        if (!token.isLiteral()) {
                            throw new JsonException("Unexpected map key format " + token,
                                                    tokenizer,
                                                    token);
                        }
                        tokenizer.expectSymbol(JsonToken.CH.MAP_KV_SEP, "consuming map (kv)");
                        consume(tokenizer.expect("consuming map value"), tokenizer);
                        token = tokenizer.expect("consuming map (sep)");
                        if (JsonToken.CH.MAP_END.equals(token.getSymbol())) {
                            break;
                        } else if (!JsonToken.CH.LIST_SEP.equals(token.getSymbol())) {
                            throw new JsonException("Unexpected symbol " + token + ". " +
                                                            "Expected list separator or map end.",
                                                    tokenizer,
                                                    token);
                        }
                        token = tokenizer.expect("consuming map key.");
                    }
                    break;
            }
        }
        // Otherwise it is a simple value. No need to consume.
    }

    protected <T> T parseTypedValue(JsonToken token, JsonTokenizer tokenizer, TDescriptor<T> t)
            throws IOException, TSerializeException {
        if (token.isNull()) {
            return null;
        }

        try {
            switch (t.getType()) {
                case BOOL:
                    if (token.isBoolean()) {
                        return cast(token.booleanValue());
                    } else if (token.isInteger()) {
                        return cast(token.intValue() != 0);
                    }
                    throw new TSerializeException("Not boolean value for token: " +
                                                          token.getToken());
                case BYTE:
                    if (token.isInteger()) {
                        return cast(token.byteValue());
                    }
                    throw new TSerializeException("Not a valid byte value: " + token.getToken());
                case I16:
                    if (token.isInteger()) {
                        return cast(token.shortValue());
                    }
                    throw new TSerializeException("Not a valid short value: " + token.getToken());
                case I32:
                    if (token.isInteger()) {
                        return cast(token.intValue());
                    }
                    throw new TSerializeException("Not a valid int value: " + token.getToken());
                case I64:
                    if (token.isInteger()) {
                        return cast(token.longValue());
                    }
                    throw new TSerializeException("Not a valid long value: " + token.getToken());
                case DOUBLE:
                    if (token.isInteger() || token.isReal()) {
                        return cast(token.doubleValue());
                    }
                    throw new TSerializeException("Not a valid double value: " + token.getToken());
                case STRING:
                    if (token.isLiteral()) {
                        return cast(token.literalValue());
                    }
                    throw new TSerializeException("Not a valid string value: " + token.getToken());
                case BINARY:
                    if (token.isLiteral()) {
                        return cast(parseBinary(token.literalValue()));
                    }
                    throw new TSerializeException("Not a valid binary value: " + token.getToken());
                case ENUM:
                    TEnumBuilder<?> eb = ((TEnumDescriptor<?>) t).factory()
                                                                 .builder();
                    if (token.isInteger()) {
                        eb.setByValue(token.intValue());
                    } else if (token.isLiteral()) {
                        eb.setByName(token.literalValue());
                    } else {
                        throw new TSerializeException(token.toString() + " is not a enum value type");
                    }
                    if (mStrict && !eb.isValid()) {
                        throw new TSerializeException(token.toString() + " is not a enum value");
                    }
                    return cast(eb.build());
                case MESSAGE:
                    TStructDescriptor<?> st = (TStructDescriptor<?>) t;
                    if (token.isSymbol()) {
                        if (token.getSymbol().equals(JsonToken.CH.MAP_START)) {
                            return cast((Object) parseMessage(tokenizer, st));
                        } else if (token.getSymbol().equals(JsonToken.CH.LIST_START)) {
                            if (st.isCompactible()) {
                                return cast((Object) parseCompactMessage(tokenizer, st));
                            } else {
                                throw new TSerializeException(st.getName() + " is not compatible for compact struct notation.");
                            }
                        }
                    }
                    throw new TSerializeException(token + " not parsable message start.");
                case LIST:
                    TDescriptor type = ((TList<?>) t).itemDescriptor();
                    if (!token.isSymbol() || !token.getSymbol().equals(JsonToken.CH.LIST_START)) {
                        throw new TSerializeException("Incompatible start of list " + token);
                    }
                    LinkedList<Object> list = new LinkedList<>();
                    token = tokenizer.expect("List item.");
                    while (!JsonToken.CH.LIST_END.equals(token.getSymbol())) {
                        list.add(parseTypedValue(token, tokenizer, type));
                        token = tokenizer.expect("expected end of list or separator");
                        if (JsonToken.CH.LIST_END.equals(token.getSymbol())) {
                            break;
                        } else if (!JsonToken.CH.LIST_SEP.equals(token.getSymbol())) {
                            throw new TSerializeException("ASDASDASD");
                        }
                        token = tokenizer.expect("List item");
                    }
                    return cast(list);
                case SET:
                    type = ((TSet<?>) t).itemDescriptor();
                    LinkedHashSet<Object> set = new LinkedHashSet<>();
                    token = tokenizer.expect("Unexpected end of list");
                    while (!JsonToken.CH.LIST_END.equals(token.getSymbol())) {
                        set.add(parseTypedValue(tokenizer.expect("Expected list item."),
                                                tokenizer,
                                                type));
                        token = tokenizer.expect("expected end of list or separator");
                        if (JsonToken.CH.LIST_END.equals(token.getSymbol())) {
                            break;
                        } else if (!JsonToken.CH.LIST_SEP.equals(token.getSymbol())) {
                            throw new TSerializeException("ASDASDASD");
                        }
                        token = tokenizer.expect("List item");
                    }
                    return cast(set);
                case MAP:
                    type = ((TMap<?, ?>) t).itemDescriptor();

                    TDescriptor keyType = ((TMap<?, ?>) t).keyDescriptor();
                    LinkedHashMap<Object, Object> map = new LinkedHashMap<>();

                    token = tokenizer.expect("Unexpected end of map");
                    while (!JsonToken.CH.MAP_END.equals(token.getSymbol())) {
                        if (!token.isLiteral()) {
                            throw new JsonException("Unexpected map key format " + token + ", must be string.",
                                                    tokenizer, token);
                        }
                        tokenizer.expectSymbol(JsonToken.CH.MAP_KV_SEP, "");

                        map.put(parsePrimitiveKey(token.literalValue(), keyType),
                                parseTypedValue(tokenizer.expect(""), tokenizer, type));
                        token = tokenizer.expect("parsing map content (sep).");
                        if (JsonToken.CH.MAP_END.equals(token.getSymbol())) {
                            break;
                        } else if (!JsonToken.CH.LIST_SEP.equals(token.getSymbol())) {
                            throw new JsonException("Expected end of object or separator.");
                        }
                        token = tokenizer.expect("parsing map key.");
                    }
                    return cast(map);
            }
        } catch (JsonException je) {
            throw new TSerializeException(je, "Unable to parse type value " + token.toString());
        } catch (ClassCastException ce) {
            throw new TSerializeException(ce,
                                          "Serialized type " + token.getClass().getSimpleName() +
                                                  " not compatible with " + t.getQualifiedName(null));
        }

        throw new TSerializeException("Unhandled item type " + t.getQualifiedName(null));
    }

    protected Object parsePrimitiveKey(String key, TDescriptor keyType) throws TSerializeException {
        try {
            switch (keyType.getType()) {
                case ENUM:
                    TEnumBuilder<?> eb = ((TEnumDescriptor<?>) keyType).factory().builder();
                    if (TStringUtils.isInteger(key)) {
                        eb.setByValue(Integer.parseInt(key));
                    } else {
                        eb.setByName(key);
                    }
                    if (mStrict && !eb.isValid()) {
                        throw new TSerializeException(key + " is not a valid enum value for " + keyType
                                .getQualifiedName(
                                        null));
                    }
                    return eb.build();
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
                        JsonTokenizer tokenizer = new JsonTokenizer(new ByteArrayInputStream(key.getBytes()));
                        JsonToken token = tokenizer.next();
                        if (!token.isReal() && !token.isInteger()) {
                            throw new TSerializeException(key + " is not a number");
                        }
                        return token.doubleValue();
                    } catch (JsonException e) {
                        throw new TSerializeException(e,
                                                      "Unable to parse double from key \"" + key + "\"");
                    } catch (IOException e) {
                        throw new TSerializeException(e,
                                                      "Unable to parse double from key \"" + key + " (IO)\"");
                    }
                case STRING:
                    return key;
                case BINARY:
                    return parseBinary(key);
                default:
                    throw new TSerializeException("Illegal key type: " + keyType.getType());
            }
        } catch (NumberFormatException nfe) {
            throw new TSerializeException(nfe, "Unable to parse numeric value " + key);
        }
    }

    protected void appendMessage(JsonWriter writer, TMessage<?> message) throws TSerializeException, JsonException {
        TStructDescriptor<?> type = message.descriptor();
        if (message.isCompact()) {
            writer.array();
            for (TField<?> field : type.getFields()) {
                if (message.has(field.getKey())) {
                    appendTypedValue(writer, field.getDescriptor(), message.get(field.getKey()));
                } else {
                    break;
                }
            }
            writer.endArray();
        } else {
            writer.object();
            for (TField<?> field : type.getFields()) {
                if (message.has(field.getKey())) {
                    Object value = message.get(field.getKey());
                    if (IdType.ID.equals(mIdType)) {
                        String key = String.valueOf(field.getKey());
                        writer.key(key);
                        key.length();
                    } else {
                        writer.key(field.getName());
                        field.getName().length();
                    }
                    appendTypedValue(writer, field.getDescriptor(), value);
                }
            }
            writer.endObject();
        }
    }

    protected void appendTypedValue(JsonWriter writer, TDescriptor type, Object value)
            throws TSerializeException, JsonException {
        switch (type.getType()) {
            case LIST:
            case SET:
                writer.array();

                TContainer<?, ?> containerType = (TContainer<?, ?>) type;

                Collection<?> collection = (Collection<?>) value;

                for (Object i : collection) {
                    appendTypedValue(writer, containerType.itemDescriptor(), i);
                }

                writer.endArray();
                break;
            case MAP:
                writer.object();

                TMap<?, ?> mapType = (TMap<?, ?>) type;

                Map<?, ?> map = (Map<?, ?>) value;

                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    appendPrimitiveKey(writer, entry.getKey());
                    appendTypedValue(writer, mapType.itemDescriptor(), entry.getValue());
                }

                writer.endObject();
                break;
            case MESSAGE:
                TMessage<?> message = (TMessage<?>) value;
                appendMessage(writer, message);
                break;
            default:
                appendPrimitive(writer, value);
                break;
        }
    }

    /**
     * @param writer
     * @param primitive
     * @return
     */
    protected void appendPrimitiveKey(JsonWriter writer, Object primitive) throws JsonException, TSerializeException {
        writer.key(getPrimitiveKey(primitive));
    }

    /**
     * @param primitive
     * @return
     */
    protected String getPrimitiveKey(Object primitive) throws TSerializeException, JsonException {
        if (primitive instanceof TEnumValue) {
            if (IdType.ID.equals(mIdType)) {
                return String.valueOf(((TEnumValue<?>) primitive).getValue());
            } else {
                return primitive.toString();
            }
        } else if (primitive instanceof Boolean ||
                primitive instanceof Byte ||
                primitive instanceof Short ||
                primitive instanceof Integer ||
                primitive instanceof Long) {
            return primitive.toString();
        } else if (primitive instanceof Double) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JsonWriter doubleWriter = new JsonWriter(out);
            doubleWriter.value(primitive);
            doubleWriter.flush();
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } else if (primitive instanceof String) {
            return (String) primitive;
        } else if (primitive instanceof byte[]) {
            return formatBinary((byte[]) primitive);
        } else {
            throw new TSerializeException("illegal simple type class " + primitive.getClass()
                                                                                  .getSimpleName());
        }
    }

    /**
     * Append a primitive value to json struct.
     *
     * @param writer    The JSON writer.
     * @param primitive The primitive instance.
     */
    protected void appendPrimitive(JsonWriter writer, Object primitive) throws JsonException, TSerializeException {
        if (primitive instanceof TEnumValue) {
            if (IdType.ID.equals(mEnumType)) {
                writer.value(((TEnumValue<?>) primitive).getValue());
            } else {
                writer.value(primitive.toString());
            }
        } else if (primitive instanceof Boolean) {
            writer.value(((Boolean) primitive).booleanValue());
        } else if (primitive instanceof Byte || primitive instanceof Short || primitive instanceof Integer ||
                primitive instanceof Long) {
            writer.value(((Number) primitive).longValue());
        } else if (primitive instanceof Double) {
            writer.value(((Number) primitive).doubleValue());
        } else if (primitive instanceof String) {
            writer.value(primitive);
        } else if (primitive instanceof byte[]) {
            writer.value(formatBinary((byte[]) primitive));
        } else {
            throw new TSerializeException("illegal primitive type class " + primitive.getClass()
                                                                                     .getSimpleName());
        }
    }

    /**
     * Formats binary data into string for putting into JSON format.
     *
     * @param binary The byte array to format.
     * @return The encoded string.
     */
    protected String formatBinary(byte[] binary) {
        return TBase64Utils.encode(binary);
    }

    /**
     * Parse a string into binary format using the same rules as above.
     *
     * @param value The string to decode.
     * @return The decoded byte array.
     */
    protected byte[] parseBinary(String value) {
        return TBase64Utils.decode(value);
    }
}
