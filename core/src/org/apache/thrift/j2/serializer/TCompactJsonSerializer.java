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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.descriptor.TContainer;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TList;
import org.apache.thrift.j2.descriptor.TMap;
import org.apache.thrift.j2.descriptor.TSet;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.util.TBase64Utils;
import org.apache.thrift.j2.util.io.CountingOutputStream;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.util.TStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONWriter;

/**
 * Compact JSON serializer. This uses the most compact type-safe JSON format
 * allowable. There are two optional variants switching the struct field ID
 * between numeric ID and field name.
 * <p/>
 * There is also the strict mode. If strict is OFF:
 *   - Unknown enum values will be ignored (as field missing).
 *   - Unknown fields will be ignored.
 *   - Struct validity will be ignored.
 * If strict more is ON:
 *   - Unknown enum values will fail the deserialization.
 *   - Unknown fields will fail the deserialization.
 *   - Struct validity will fail both serialization and deserialization.
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
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 25.08.15
 */
public class TCompactJsonSerializer
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

    public TCompactJsonSerializer() {
        this(false, IdType.ID, IdType.ID);
    }

    public TCompactJsonSerializer(boolean strict) {
        this(strict, IdType.ID, IdType.ID);
    }

    public TCompactJsonSerializer(IdType idType) {
        this(false, idType, idType);
    }

    public TCompactJsonSerializer(boolean strict, IdType idType) {
        this(strict, idType, idType);
    }

    public TCompactJsonSerializer(IdType idType, IdType enumType) {
        this(false, idType, enumType);
    }

    public TCompactJsonSerializer(boolean strict, IdType idType, IdType enumType) {
        mStrict = strict;
        mIdType = idType;
        mEnumType = enumType;
    }

    @Override
    public int serialize(OutputStream output, TMessage<?> message) throws TSerializeException {
        CountingOutputStream counter = new CountingOutputStream(output);
        OutputStreamWriter writer = new OutputStreamWriter(counter);
        JSONWriter jsonWriter = new JSONWriter(writer);
        try {
            appendMessage(jsonWriter, message);
            writer.flush();
            counter.flush();
            return counter.getByteCount();
        } catch (IOException e) {
            throw new TSerializeException(e, "Unable to write to stream");
        } catch (JSONException e) {
            throw new TSerializeException(e, "Unable to serialize JSON");
        }
    }

    @Override
    public <T> int serialize(OutputStream output, TDescriptor<T> descriptor, T value)
            throws IOException, TSerializeException {
        CountingOutputStream counter = new CountingOutputStream(output);
        OutputStreamWriter writer = new OutputStreamWriter(counter);
        JSONWriter jsonWriter = new JSONWriter(writer);
        try {
            appendTypedValue(jsonWriter, descriptor, value);
            writer.flush();
            counter.flush();
            return counter.getByteCount();
        } catch (IOException e) {
            throw new TSerializeException(e, "Unable to write to stream");
        } catch (JSONException e) {
            throw new TSerializeException(e, "Unable to serialize JSON");
        }
    }

    @Override
    public <T> T deserialize(InputStream input, TDescriptor<T> type)
            throws TSerializeException {
        try {
            JSONTokener tokenizer = new JSONTokener(new InputStreamReader(input));
            return parseTypedValue(tokenizer.nextValue(), type);
        } catch (JSONException e) {
            throw new TSerializeException(e, "Unable to parse JSON");
        }
    }

    /**
     * Parse JSON object as a message.
     *
     * @param object The object to parse.
     * @param type The message type.
     * @param <T> Message generic type.
     * @return The parsed message.
     * @throws JSONException 
     */
    protected <T extends TMessage<T>> T parseMessage(JSONObject object, TStructDescriptor<T> type) throws TSerializeException, JSONException {
        TMessageBuilder<T> builder = type.factory().builder();

        Iterator<?> keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            TField<?> field;
            if (TStringUtils.isInteger(key)) {
                field = type.getField(Integer.parseInt(key));
            } else {
                field = type.getField(key);
            }

            if (field != null) {
                Object value = parseTypedValue(object.get(key),
                                               field.descriptor());
                builder.set(field.getKey(), value);
            } else if (mStrict) {
                throw new TSerializeException("Unknown field " + key + " for type " + type.getQualifiedName(null));
            }
        }

        if (mStrict && !builder.isValid()) {
            throw new TSerializeException("Type " + type.getName() + " not properly populated");
        }

        return builder.build();
    }

    /**
     * Parse JSON object as a message.
     *
     * @param object The object to parse.
     * @param type The message type.
     * @param <T> Message generic type.
     * @return The parsed message.
     * @throws JSONException
     */
    protected <T extends TMessage<T>> T parseCompactMessage(JSONArray object, TStructDescriptor<T> type) throws TSerializeException, JSONException {
        TMessageBuilder<T> builder = type.factory().builder();

        for (int i = 0; i < object.length(); ++i) {
            TField<?> field = type.getField(i + 1);

            if (field != null) {
                Object value = parseTypedValue(object.get(i),
                                               field.descriptor());
                builder.set(i + 1, value);
            } else if (mStrict) {
                throw new TSerializeException("Compact Field ID " + (i + 1) + " outside field spectrum for type " +
                                              type.getQualifiedName(null));
            }
        }

        if (mStrict && !builder.isValid()) {
            throw new TSerializeException("Type " + type.getName() + " not properly populated");
        }

        return builder.build();
    }

    protected <T> T parseTypedValue(Object item, TDescriptor<T> t) throws TSerializeException {
        if (item == null || item == JSONObject.NULL) {
            return null;
        }

        try {
            switch (t.getType()) {
                case BOOL:
                    if (item instanceof Number) {
                        return cast(((Number) item).longValue() != 0);
                    }
                    if (item instanceof Boolean) {
                        return cast(item);
                    }
                    throw new TSerializeException("Not boolean value for bool: " +
                                                  item.getClass().getSimpleName());
                case BYTE:
                    return cast(((Number) item).byteValue());
                case I16:
                    return cast(((Number) item).shortValue());
                case I32:
                    return cast(((Number) item).intValue());
                case I64:
                    return cast(((Number) item).longValue());
                case DOUBLE:
                    return cast(((Number) item).doubleValue());
                case STRING:
                    return cast(String.valueOf(item));
                case BINARY:
                    return cast(parseBinary((String) item));
                case ENUM:
                    TEnumBuilder<?> eb = ((TEnumDescriptor<?>) t).factory()
                                                                  .builder();
                    if (item instanceof Number) {
                        eb.setByValue(((Number) item).intValue());
                    } else if (item instanceof String) {
                        eb.setByName((String) item);
                    } else {
                        throw new TSerializeException(item.toString() + " is not a enum value type");
                    }
                    if (mStrict && !eb.isValid()) {
                        throw new TSerializeException(item.toString() + " is not a enum value");
                    }
                    return cast(eb.build());
                case MESSAGE:
                    TStructDescriptor<?> st = (TStructDescriptor<?>) t;
                    if (item instanceof JSONObject) {
                        return cast((Object) parseMessage((JSONObject) item, st));
                    } else if (item instanceof JSONArray) {
                        if (st.isCompactible()) {
                            return cast((Object) parseCompactMessage((JSONArray) item, st));
                        } else {
                            throw new TSerializeException(st.getName() + " is not compactable for array notation.");
                        }
                    } else {
                        throw new TSerializeException(item.getClass().getSimpleName() + " not parsable message format.");
                    }
                case LIST:
                    TDescriptor type = ((TList<?>) t).itemDescriptor();
                    JSONArray array = (JSONArray) item;
                    LinkedList<Object> list = new LinkedList<>();
                    for (int i = 0; i < array.length(); ++i) {
                        list.add(parseTypedValue(array.get(i), type));
                    }
                    return cast(list);
                case SET:
                    type = ((TSet<?>) t).itemDescriptor();
                    array = (JSONArray) item;
                    LinkedHashSet<Object> set = new LinkedHashSet<>();
                    for (int i = 0; i < array.length(); ++i) {
                        set.add(parseTypedValue(array.get(i), type));
                    }
                    return cast(set);
                case MAP:
                    type = ((TMap<?, ?>) t).itemDescriptor();

                    TDescriptor keyType = ((TMap<?, ?>) t).keyDescriptor();
                    JSONObject object = (JSONObject) item;
                    LinkedHashMap<Object, Object> map = new LinkedHashMap<>();

                    Iterator<?> keyIterator = object.keys();
                    while (keyIterator.hasNext()) {
                        String k = (String) keyIterator.next();
                        Object v = object.get(k);

                        map.put(parsePrimitiveKey(k, keyType), parseTypedValue(v, type));
                    }
                    return cast(map);
            }
        } catch (JSONException je) {
            throw new TSerializeException(je, "Unable to parse type value " + item.toString());
        } catch (ClassCastException ce) {
            throw new TSerializeException(ce, "Serialized type " + item.getClass().getSimpleName() +
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
                        throw new TSerializeException(key + " is not a valid enum value for " + keyType.getQualifiedName(
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
                        JSONTokener tokener = new JSONTokener(key);
                        Object number = tokener.nextValue();
                        if (!(number instanceof Number)) {
                            throw new TSerializeException(key + " is not a number");
                        }
                        return ((Number) number).doubleValue();
                    } catch (JSONException e) {
                        throw new TSerializeException(e, "Unable to parse double from key \"" + key + "\"");
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

    protected void appendMessage(JSONWriter writer, TMessage<?> message) throws TSerializeException, JSONException {
        TStructDescriptor<?> type = message.descriptor();
        if (message.compact()) {
            writer.array();
            for (TField<?> field : type.getFields()) {
                if (message.has(field.getKey())) {
                    appendTypedValue(writer, field.descriptor(), message.get(field.getKey()));
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
                    appendTypedValue(writer, field.descriptor(), value);
                }
            }
            writer.endObject();
        }
    }

    protected void appendTypedValue(JSONWriter writer, TDescriptor type, Object value)
            throws TSerializeException, JSONException {
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
     *
     * @param writer
     * @param primitive
     * @return
     * @throws JSONException
     */
    protected void appendPrimitiveKey(JSONWriter writer, Object primitive) throws JSONException, TSerializeException {
        writer.key(getPrimitiveKey(primitive));
    }

    /**
     *
     * @param primitive
     * @return
     * @throws JSONException 
     */
    protected String getPrimitiveKey(Object primitive) throws TSerializeException, JSONException {
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
            StringWriter out = new StringWriter();
            JSONWriter doubleWriter = new JSONWriter(out);
            doubleWriter.value(((Number) primitive).doubleValue());
            return out.toString();
        } else if (primitive instanceof String) {
            return (String) primitive;
        } else if (primitive instanceof byte[]) {
            return formatBinary((byte[]) primitive);
        } else {
            throw new TSerializeException("illegal simple type class " + primitive.getClass().getSimpleName());
        }
    }

    /**
     * Append a primitive value to json struct.
     *
     * @param writer    The JSON writer.
     * @param primitive The primitive instance.
     * @throws JSONException
     */
    protected void appendPrimitive(JSONWriter writer, Object primitive) throws JSONException, TSerializeException {
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
            throw new TSerializeException("illegal primitive type class " + primitive.getClass().getSimpleName());
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
