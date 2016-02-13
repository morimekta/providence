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

package net.morimekta.providence.serializer;

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.util.Binary;
import net.morimekta.util.Strings;
import net.morimekta.util.io.CountingOutputStream;
import net.morimekta.util.json.JsonException;
import net.morimekta.util.json.JsonToken;
import net.morimekta.util.json.JsonTokenizer;
import net.morimekta.util.json.JsonWriter;
import net.morimekta.util.json.PrettyJsonWriter;

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
 * Compact JSON serializer. This uses the most compact type-safe JSON format
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
 * <p/>
 * This format supports 'compact' struct formatting. A compact struct is
 * formatted as a list with fields in order from 1 to N. E.g.:
 * <pre>
 * ["tag",5,6.45]
 * </pre>
 * is equivalent to:
 * <pre>
 * {"1":"tag","2":5,"3":6.45}
 * </pre>
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class PJsonSerializer extends PSerializer {
    public static final byte[] STREAM_INITIATOR = {'{'};
    public static final byte[] ENTRY_SEP        = {'\n'};

    public enum IdType {
        // print field and enums as numeric IDs and values.
        ID,
        // print field and enums as field name and enum name.
        NAME
    }

    private final boolean readStrict;
    private final IdType  idType;
    private final IdType  enumType;
    private final boolean pretty;

    public PJsonSerializer() {
        this(true, IdType.ID, IdType.ID, false);
    }

    public PJsonSerializer(boolean strict) {
        this(strict, IdType.ID, IdType.ID, false);
    }

    public PJsonSerializer(IdType idType) {
        this(true, idType, idType, false);
    }

    public PJsonSerializer(boolean readStrict, IdType idType) {
        this(readStrict, idType, idType, false);
    }

    public PJsonSerializer(IdType idType, IdType enumType) {
        this(true, idType, enumType, false);
    }

    public PJsonSerializer(boolean readStrict, IdType idType, IdType enumType, boolean pretty) {
        this.readStrict = readStrict;
        this.idType = idType;
        this.enumType = enumType;
        this.pretty = pretty;
    }

    @Override
    public byte[] streamInitiator() {
        return STREAM_INITIATOR;
    }

    @Override
    public int serialize(OutputStream output, PMessage<?> message) throws PSerializeException {
        CountingOutputStream counter = new CountingOutputStream(output);
        JsonWriter jsonWriter = pretty ? new PrettyJsonWriter(counter) : new JsonWriter(counter);
        try {
            appendMessage(jsonWriter, message);
            jsonWriter.flush();
            counter.flush();
            return counter.getByteCount();
        } catch (JsonException e) {
            throw new PSerializeException(e, "Unable to serialize JSON");
        } catch (IOException e) {
            throw new PSerializeException(e, "Unable to writeBinary to stream");
        }
    }

    @Override
    public <T> int serialize(OutputStream output, PDescriptor<T> descriptor, T value)
            throws IOException, PSerializeException {
        CountingOutputStream counter = new CountingOutputStream(output);
        JsonWriter jsonWriter = new JsonWriter(counter);
        try {
            appendTypedValue(jsonWriter, descriptor, value);
            jsonWriter.flush();
            counter.flush();
            return counter.getByteCount();
        } catch (JsonException e) {
            throw new PSerializeException(e, "Unable to serialize JSON");
        } catch (IOException e) {
            throw new PSerializeException(e, "Unable to writeBinary to stream");
        }
    }

    @Override
    public <T> T deserialize(InputStream input, PDescriptor<T> type) throws PSerializeException {
        try {
            JsonTokenizer tokenizer = new JsonTokenizer(input);
            if (!tokenizer.hasNext()) {
                return null;
            }
            return parseTypedValue(tokenizer.next(), tokenizer, type);
        } catch (JsonException e) {
            throw new PSerializeException(e, "Unable to parse JSON");
        } catch (IOException e) {
            throw new PSerializeException(e, "Unable to read stream");
        }
    }

    /**
     * Parse JSON object as a message.
     *
     * @param tokenizer The object to parse.
     * @param type      The message type.
     * @param <T>       Message generic type.
     * @return The parsed message.
     */
    protected <T extends PMessage<T>> T parseMessage(JsonTokenizer tokenizer, PStructDescriptor<T, ?> type)
            throws PSerializeException, JsonException, IOException {
        PMessageBuilder<T> builder = type.factory()
                                         .builder();

        JsonToken token = tokenizer.expect("message key");
        while (!token.isSymbol(JsonToken.kMapEndChar)) {
            if (!token.isLiteral()) {
                throw new JsonException("" + token + " is not a literal.", tokenizer, token);
            }
            String key = token.substring(1, -1)
                              .asString();
            PField<?> field;
            if (Strings.isInteger(key)) {
                field = type.getField(Integer.parseInt(key));
            } else {
                field = type.getField(key);
            }
            tokenizer.expectSymbol("message field key sep", JsonToken.kKeyValSepChar);

            if (field != null) {
                Object value = parseTypedValue(tokenizer.expect("map value"), tokenizer, field.getDescriptor());
                builder.set(field.getKey(), value);
            } else if (readStrict) {
                throw new PSerializeException("Unknown field " + key + " for type " + type.getQualifiedName(null));
            } else {
                consume(tokenizer.expect("consuming unknown message value"), tokenizer);
            }

            if (tokenizer.expectSymbol("message entry separator", JsonToken.kMapEndChar, JsonToken.kListSepChar) == 0) {
                break;
            }
            token = tokenizer.expect("message entry key.");
        }

        if (readStrict && !builder.isValid()) {
            throw new PSerializeException("Type " + type.getName() + " not properly populated");
        }

        return builder.build();
    }

    /**
     * Parse JSON object as a message.
     *
     * @param tokenizer The object to parse.
     * @param type      The message type.
     * @param <T>       Message generic type.
     * @return The parsed message.
     */
    protected <T extends PMessage<T>> T parseCompactMessage(JsonTokenizer tokenizer, PStructDescriptor<T, ?> type)
            throws PSerializeException, IOException, JsonException {
        PMessageBuilder<T> builder = type.factory()
                                         .builder();

        int i = 0;
        JsonToken token = tokenizer.expect("list item");
        while (!token.isSymbol(JsonToken.kListEndChar)) {
            PField<?> field = type.getField(++i);

            if (field != null) {
                Object value = parseTypedValue(token, tokenizer, field.getDescriptor());
                builder.set(i, value);
            } else if (readStrict) {
                throw new PSerializeException("Compact Field ID " + (i) + " outside field spectrum for type " +
                                              type.getQualifiedName(null));
            } else {
                consume(token, tokenizer);
            }

            if (tokenizer.expectSymbol("consuming list (sep)", JsonToken.kListEndChar, JsonToken.kListSepChar) == 0) {
                break;
            }
            token = tokenizer.expect("consuming list item");
        }

        if (readStrict && !builder.isValid()) {
            throw new PSerializeException("Type " + type.getName() + " not properly populated");
        }

        return builder.build();
    }

    private void consume(JsonToken token, JsonTokenizer tokenizer) throws IOException, JsonException {
        if (token.isSymbol()) {
            if (token.charAt(0) == JsonToken.kListStartChar) {
                token = tokenizer.expect("consuming list item.");
                while (JsonToken.kListEndChar != token.charAt(0)) {
                    consume(token, tokenizer);
                    if (0 == tokenizer.expectSymbol("consuming list (sep)",
                                                    JsonToken.kListEndChar,
                                                    JsonToken.kListSepChar)) {
                        break;
                    }
                    token = tokenizer.expect("consuming list item.");
                }
            } else if (token.charAt(0) == JsonToken.kMapStartChar) {
                token = tokenizer.expect("consuming map key.");
                while (JsonToken.kMapEndChar != token.charAt(0)) {
                    if (!token.isLiteral()) {
                        throw new JsonException("Unexpected map key format " + token, tokenizer, token);
                    }
                    tokenizer.expectSymbol("consuming map (kv)", JsonToken.kKeyValSepChar);
                    consume(tokenizer.expect("consuming map value"), tokenizer);
                    if (0 ==
                        tokenizer.expectSymbol("consuming map (sep)", JsonToken.kMapEndChar, JsonToken.kListSepChar)) {
                        break;
                    }
                    token = tokenizer.expect("consuming map key.");
                }
            }
        }
        // Otherwise it is a simple value. No need to consume.
    }

    protected <T> T parseTypedValue(JsonToken token, JsonTokenizer tokenizer, PDescriptor<T> t)
            throws IOException, PSerializeException {
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
                    throw new PSerializeException("Not boolean value for token: " + token.asString());
                case BYTE:
                    if (token.isInteger()) {
                        return cast(token.byteValue());
                    }
                    throw new PSerializeException("Not a valid byte value: " + token.asString());
                case I16:
                    if (token.isInteger()) {
                        return cast(token.shortValue());
                    }
                    throw new PSerializeException("Not a valid short value: " + token.asString());
                case I32:
                    if (token.isInteger()) {
                        return cast(token.intValue());
                    }
                    throw new PSerializeException("Not a valid int value: " + token.asString());
                case I64:
                    if (token.isInteger()) {
                        return cast(token.longValue());
                    }
                    throw new PSerializeException("Not a valid long value: " + token.asString());
                case DOUBLE:
                    if (token.isReal()) {
                        return cast(token.doubleValue());
                    }
                    throw new PSerializeException("Not a valid double value: " + token.asString());
                case STRING:
                    if (token.isLiteral()) {
                        return cast(token.decodeJsonLiteral());
                    }
                    throw new PSerializeException("Not a valid string value: " + token.asString());
                case BINARY:
                    if (token.isLiteral()) {
                        return cast(Binary.fromBase64(token.substring(1, -1)
                                                           .asString()));
                    }
                    throw new PSerializeException("Not a valid binary value: " + token.asString());
                case ENUM:
                    PEnumBuilder<?> eb = ((PEnumDescriptor<?>) t).factory()
                                                                 .builder();
                    if (token.isInteger()) {
                        eb.setByValue(token.intValue());
                    } else if (token.isLiteral()) {
                        eb.setByName(token.substring(1, -1)
                                          .asString());
                    } else {
                        throw new PSerializeException(token.toString() + " is not a enum value type");
                    }
                    if (readStrict && !eb.isValid()) {
                        throw new PSerializeException(token.toString() + " is not a enum value");
                    }
                    return cast(eb.build());
                case MESSAGE: {
                    PStructDescriptor<?, ?> st = (PStructDescriptor<?, ?>) t;
                    if (token.isSymbol(JsonToken.kMapStartChar)) {
                        return cast((Object) parseMessage(tokenizer, st));
                    } else if (token.isSymbol(JsonToken.kListStartChar)) {
                        if (st.isCompactible()) {
                            return cast((Object) parseCompactMessage(tokenizer, st));
                        } else {
                            throw new PSerializeException(
                                    st.getName() + " is not compatible for compact struct notation.");
                        }
                    }
                    throw new PSerializeException(token + " not parsable message start.");
                }
                case MAP: {
                    PDescriptor<?> itemType = ((PMap<?, ?>) t).itemDescriptor();
                    PDescriptor<?> keyType = ((PMap<?, ?>) t).keyDescriptor();
                    if (!token.isSymbol(JsonToken.kMapStartChar)) {
                        throw new PSerializeException("Incompatible start of map " + token);
                    }

                    LinkedHashMap<Object, Object> map = new LinkedHashMap<>();

                    token = tokenizer.expect("Unexpected end of map");
                    while (!token.isSymbol(JsonToken.kMapEndChar)) {
                        if (!token.isLiteral()) {
                            throw new JsonException("Unexpected map key format " + token + ", must be string.",
                                                    tokenizer,
                                                    token);
                        }
                        tokenizer.expectSymbol("Map key-val separator", ':');

                        map.put(parseMapKey(token.decodeJsonLiteral(), keyType),
                                parseTypedValue(tokenizer.expect("Map value."), tokenizer, itemType));
                        if (tokenizer.expectSymbol("parsing map content (sep).",
                                                   JsonToken.kMapEndChar,
                                                   JsonToken.kListSepChar) == 0) {
                            break;
                        }
                        token = tokenizer.expect("parsing map key.");
                    }
                    return cast(map);
                }
                case SET: {
                    PDescriptor<?> itemType = ((PSet<?>) t).itemDescriptor();
                    if (!token.isSymbol(JsonToken.kListStartChar)) {
                        throw new PSerializeException("Incompatible start of list " + token);
                    }
                    LinkedHashSet<Object> set = new LinkedHashSet<>();
                    token = tokenizer.expect("List item.");
                    while (!token.isSymbol(JsonToken.kListEndChar)) {
                        set.add(parseTypedValue(token, tokenizer, itemType));

                        if (tokenizer.expectSymbol("expected end of list or separator",
                                                   JsonToken.kListEndChar,
                                                   JsonToken.kListSepChar) == 0) {
                            break;
                        }
                        token = tokenizer.expect("List item.");
                    }
                    return cast(set);
                }
                case LIST: {
                    PDescriptor itemType = ((PList<?>) t).itemDescriptor();
                    if (!token.isSymbol(JsonToken.kListStartChar)) {
                        throw new PSerializeException("Incompatible start of list " + token);
                    }
                    LinkedList<Object> list = new LinkedList<>();
                    token = tokenizer.expect("List item.");
                    while (!token.isSymbol(JsonToken.kListEndChar)) {
                        list.add(parseTypedValue(token, tokenizer, itemType));

                        if (tokenizer.expectSymbol("expected end of list or separator",
                                                   JsonToken.kListEndChar,
                                                   JsonToken.kListSepChar) == 0) {
                            break;
                        }
                        token = tokenizer.expect("List item.");
                    }
                    return cast(list);
                }
            }
        } catch (JsonException je) {
            throw new PSerializeException(je, "Unable to parse type value.");
        } catch (ClassCastException ce) {
            throw new PSerializeException(ce, "Serialized type  not compatible with " + t.getQualifiedName(null));
        }

        throw new PSerializeException("Unhandled item type " + t.getQualifiedName(null));
    }

    protected Object parseMapKey(String key, PDescriptor keyType) throws PSerializeException {
        try {
            switch (keyType.getType()) {
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
                            throw new PSerializeException(key + " is not a number");
                        }
                        return token.doubleValue();
                    } catch (JsonException e) {
                        throw new PSerializeException(e, "Unable to parse double from key \"" + key + "\"");
                    } catch (IOException e) {
                        throw new PSerializeException(e, "Unable to parse double from key \"" + key + "\" (IO)");
                    }
                case STRING:
                    return key;
                case BINARY:
                    try {
                        return Binary.fromBase64(key);
                    } catch (IOException e) {
                        throw new PSerializeException(e, "Unable to parse Base64 data.");
                    }
                case ENUM:
                    PEnumBuilder<?> eb = ((PEnumDescriptor<?>) keyType).factory()
                                                                       .builder();
                    if (Strings.isInteger(key)) {
                        eb.setByValue(Integer.parseInt(key));
                    } else {
                        eb.setByName(key);
                    }
                    if (readStrict && !eb.isValid()) {
                        throw new PSerializeException(
                                key + " is not a valid enum value for " + keyType.getQualifiedName(null));
                    }
                    return eb.build();
                case MESSAGE:
                    PStructDescriptor<?, ?> st = (PStructDescriptor<?, ?>) keyType;
                    if (!st.isSimple()) {
                        throw new PSerializeException("Only simple structs can be used as map key. " +
                                                      st.getQualifiedName(null) + " is not.");
                    }
                    ByteArrayInputStream input = new ByteArrayInputStream(key.getBytes(StandardCharsets.UTF_8));
                    try {
                        JsonTokenizer tokenizer = new JsonTokenizer(input);
                        tokenizer.expectSymbol("Message start", JsonToken.kMapStartChar);
                        return cast(parseMessage(tokenizer, st));
                    } catch (IOException e) {
                        throw new PSerializeException(e, "Unable to tokenize map key: " + key);
                    } catch (JsonException e) {
                        throw new PSerializeException(e, "Unable to parse map key: " + key);
                    }
                default:
                    throw new PSerializeException("Illegal key type: " + keyType.getType());
            }
        } catch (NumberFormatException nfe) {
            throw new PSerializeException(nfe, "Unable to parse numeric value " + key);
        }
    }

    protected void appendMessage(JsonWriter writer, PMessage<?> message) throws PSerializeException, JsonException {
        PStructDescriptor<?, ?> type = message.descriptor();
        if (message instanceof PUnion) {
            writer.object();
            PField field = ((PUnion) message).unionField();
            if (field != null) {
                Object value = message.get(field.getKey());
                if (IdType.ID.equals(idType)) {
                    writer.key(field.getKey());
                } else {
                    writer.key(field.getName());
                }
                appendTypedValue(writer, field.getDescriptor(), value);
            }
            writer.endObject();
        } else {
            if (message.isCompact()) {
                writer.array();
                for (PField<?> field : type.getFields()) {
                    if (message.has(field.getKey())) {
                        appendTypedValue(writer, field.getDescriptor(), message.get(field.getKey()));
                    } else {
                        break;
                    }
                }
                writer.endArray();
            } else {
                writer.object();
                for (PField<?> field : type.getFields()) {
                    if (message.has(field.getKey())) {
                        Object value = message.get(field.getKey());
                        if (IdType.ID.equals(idType)) {
                            writer.key(field.getKey());
                        } else {
                            writer.key(field.getName());
                        }
                        appendTypedValue(writer, field.getDescriptor(), value);
                    }
                }
                writer.endObject();
            }
        }
    }

    protected void appendTypedValue(JsonWriter writer, PDescriptor type, Object value)
            throws PSerializeException, JsonException {
        switch (type.getType()) {
            case MESSAGE:
                PMessage<?> message = (PMessage<?>) value;
                appendMessage(writer, message);
                break;
            case MAP:
                writer.object();

                PMap<?, ?> mapType = (PMap<?, ?>) type;

                Map<?, ?> map = (Map<?, ?>) value;

                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    appendPrimitiveKey(writer, entry.getKey());
                    appendTypedValue(writer, mapType.itemDescriptor(), entry.getValue());
                }

                writer.endObject();
                break;
            case SET:
            case LIST:
                writer.array();

                PContainer<?, ?> containerType = (PContainer<?, ?>) type;

                Collection<?> collection = (Collection<?>) value;

                for (Object i : collection) {
                    appendTypedValue(writer, containerType.itemDescriptor(), i);
                }

                writer.endArray();
                break;
            default:
                appendPrimitive(writer, value);
                break;
        }
    }

    /**
     * @param writer    The writer to add primitive key to.
     * @param primitive Primitive object to get map key value of.
     */
    protected void appendPrimitiveKey(JsonWriter writer, Object primitive) throws JsonException, PSerializeException {
        if (primitive instanceof PEnumValue) {
            if (IdType.ID.equals(idType)) {
                writer.key(((PEnumValue<?>) primitive).getValue());
            } else {
                writer.key(primitive.toString());
            }
        } else if (primitive instanceof Boolean) {
            writer.key(((Boolean) primitive));
        } else if (primitive instanceof Byte) {
            writer.key(((Byte) primitive));
        } else if (primitive instanceof Short) {
            writer.key(((Short) primitive));
        } else if (primitive instanceof Integer) {
            writer.key(((Integer) primitive));
        } else if (primitive instanceof Long) {
            writer.key(((Long) primitive));
        } else if (primitive instanceof Double) {
            writer.key(((Double) primitive));
        } else if (primitive instanceof String) {
            writer.key((String) primitive);
        } else if (primitive instanceof Binary) {
            writer.key((Binary) primitive);
        } else if (primitive instanceof PMessage) {
            PMessage<?> message = (PMessage<?>) primitive;
            if (!message.isSimple()) {
                throw new PSerializeException("Only simple messages can be used as map keys. " +
                                              message.descriptor()
                                                     .getQualifiedName(null) + " is not.");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JsonWriter json = new JsonWriter(baos);
            appendMessage(json, message);
            json.flush();
            writer.key(new String(baos.toByteArray(), StandardCharsets.UTF_8));
        } else {
            throw new PSerializeException("illegal simple type class " + primitive.getClass()
                                                                                  .getSimpleName());
        }
    }

    /**
     * Append a primitive value to json struct.
     *
     * @param writer    The JSON writer.
     * @param primitive The primitive instance.
     */
    protected void appendPrimitive(JsonWriter writer, Object primitive) throws JsonException, PSerializeException {
        if (primitive instanceof PEnumValue) {
            if (IdType.ID.equals(enumType)) {
                writer.value(((PEnumValue<?>) primitive).getValue());
            } else {
                writer.value(primitive.toString());
            }
        } else if (primitive instanceof Boolean) {
            writer.value(((Boolean) primitive));
        } else if (primitive instanceof Byte) {
            writer.value(((Byte) primitive));
        } else if (primitive instanceof Short) {
            writer.value(((Short) primitive));
        } else if (primitive instanceof Integer) {
            writer.value(((Integer) primitive));
        } else if (primitive instanceof Long) {
            writer.value(((Long) primitive));
        } else if (primitive instanceof Double) {
            writer.value(((Double) primitive));
        } else if (primitive instanceof String) {
            writer.value((String) primitive);
        } else if (primitive instanceof Binary) {
            writer.value((Binary) primitive);
        } else {
            throw new PSerializeException("illegal primitive type class " + primitive.getClass()
                                                                                     .getSimpleName());
        }
    }
}
