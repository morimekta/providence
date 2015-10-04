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

package org.apache.thrift2.reflect.parser.internal;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.thrift2.TEnumBuilder;
import org.apache.thrift2.TMessage;
import org.apache.thrift2.TMessageBuilder;
import org.apache.thrift2.descriptor.TDescriptor;
import org.apache.thrift2.descriptor.TEnumDescriptor;
import org.apache.thrift2.descriptor.TField;
import org.apache.thrift2.descriptor.TList;
import org.apache.thrift2.descriptor.TMap;
import org.apache.thrift2.descriptor.TSet;
import org.apache.thrift2.descriptor.TStructDescriptor;
import org.apache.thrift2.reflect.parser.TParseException;
import org.apache.thrift2.util.TBase64Utils;
import org.apache.thrift2.util.TStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Parsing thrift constants from string to actual value. This uses the JSON
 * format with some allowed special references. So enum values are always
 * expected to be 'EnumName.VALUE' (quotes or no quotes), and map keys are not
 * expected to be string literals.
 *
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 25.08.15
 */
public class TConstParser {
    public TConstParser() {}

    public Object parse(InputStream inputStream, TDescriptor type) throws TParseException {
        try {
            JSONTokener tokenizer = new JSONTokener(new InputStreamReader(inputStream));
            return parseTypedValue(tokenizer.nextValue(), type);
        } catch (JSONException e) {
            throw new TParseException("Unable to parse JSON", e);
        }
    }

    /**
     * Parse JSON object as a message.
     *
     * @param object The object to parse.
     * @param type The message type.
     * @param <T> Message generic type.
     * @return The parsed message.
     */
    private <T extends TMessage<T>> T parseMessage(JSONObject object, TStructDescriptor<T> type) {
        TMessageBuilder<T> builder = type.factory().builder();

        for (TField<?> field : type.getFields()) {
            int id = field.getKey();
            String idStr = String.valueOf(id);
            if (!object.has(idStr)) {
                idStr = field.getName();
                if (!object.has(idStr)) {
                    continue;
                }
            }

            try {
                Object value = parseTypedValue(object.get(idStr),
                                               field.descriptor());
                builder.set(id, value);
            } catch (JSONException e) {
                // ignore.
            }
        }

        return builder.build();
    }

    private Object parseTypedValue(Object item, TDescriptor itemType) {
        if (item == null || item == JSONObject.NULL) {
            return null;
        }

        try {
            switch (itemType.getType()) {
                case BOOL:
                    if (!(item instanceof Boolean)) {
                        throw new IllegalArgumentException("Not boolean value for bool: " +
                                                           item.getClass().getSimpleName());
                    }
                    return item;
                case BYTE:
                    return ((Number) item).byteValue();
                case I16:
                    return ((Number) item).shortValue();
                case I32:
                    return ((Number) item).intValue();
                case I64:
                    return ((Number) item).longValue();
                case DOUBLE:
                    return ((Number) item).doubleValue();
                case STRING:
                    return String.valueOf(item);
                case BINARY:
                    return parseBinary((String) item);
                case ENUM:
                    TEnumBuilder<?> eb = ((TEnumDescriptor<?>) itemType).factory()
                                                                  .builder();
                    String name = item.toString();
                    if (name.startsWith(itemType.getName())) {
                        name = name.substring(itemType.getName().length() + 1);
                    }
                    return eb.setByName(name).build();
                case MESSAGE:
                    return parseMessage((JSONObject) item, (TStructDescriptor<?>) itemType);
                case LIST:
                    TDescriptor type = ((TList<?>) itemType).itemDescriptor();
                    JSONArray array = (JSONArray) item;
                    LinkedList<Object> list = new LinkedList<>();
                    for (int i = 0; i < array.length(); ++i) {
                        list.add(parseTypedValue(array.get(i), type));
                    }
                    return list;
                case SET:
                    type = ((TSet<?>) itemType).itemDescriptor();
                    array = (JSONArray) item;
                    HashSet<Object> set = new HashSet<>();
                    for (int i = 0; i < array.length(); ++i) {
                        set.add(parseTypedValue(array.get(i), type));
                    }
                    return set;
                case MAP:
                    type = ((TMap<?, ?>) itemType).itemDescriptor();

                    TDescriptor keyType = ((TMap<?, ?>) itemType).keyDescriptor();
                    JSONObject object = (JSONObject) item;
                    HashMap<Object, Object> map = new HashMap<>();

                    Iterator<?> keyIterator = object.keys();
                    while (keyIterator.hasNext()) {
                        String k = (String) keyIterator.next();
                        Object v = object.get(k);

                        map.put(parsePrimitiveKey(k, keyType), parseTypedValue(v, type));
                    }
                    return map;
            }
        } catch (JSONException je) {
            throw new IllegalArgumentException("Unable to parse type value " + item.toString(), je);
        }

        throw new IllegalArgumentException("Unhandled item type " + itemType.getQualifiedName(null));
    }

    private Object parsePrimitiveKey(String key, TDescriptor keyType) {
        switch (keyType.getType()) {
            case ENUM:
                TEnumBuilder<?> eb = ((TEnumDescriptor<?>) keyType).factory().builder();
                if (TStringUtils.isInteger(key)) {
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
                    JSONTokener tokener = new JSONTokener(String.format("{\"d\":%s}", key));
                    JSONObject object = new JSONObject(tokener);
                    return object.getDouble("d");
                } catch (JSONException e) {
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
        return TBase64Utils.decode(value);
    }
}
