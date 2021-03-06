/*
 * Copyright 2015-2016 Providence Authors
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
package net.morimekta.providence.serializer;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.serializer.json.JsonCompactible;
import net.morimekta.providence.serializer.json.JsonCompactibleDescriptor;
import net.morimekta.util.Binary;
import net.morimekta.util.Strings;
import net.morimekta.util.io.CountingOutputStream;
import net.morimekta.util.io.IndentedPrintWriter;
import net.morimekta.util.io.Utf8StreamReader;
import net.morimekta.util.json.JsonException;
import net.morimekta.util.json.JsonToken;
import net.morimekta.util.json.JsonTokenizer;
import net.morimekta.util.json.JsonWriter;
import net.morimekta.util.json.PrettyJsonWriter;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Compact JSON serializer. This uses the most compact type-safe JSON format
 * allowable. There are two optional variants switching the struct field ID
 * between numeric ID and field name.
 * <p>
 * Format is like this:
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
 * <p>
 * This format supports 'compact' struct formatting. A compact struct is
 * formatted as a list with fields in order from 1 to N. E.g.:
 * <pre>
 * ["tag",5,6.45]
 * </pre>
 * is equivalent to:
 * <pre>
 * {"1":"tag","2":5,"3":6.45}
 * </pre>
 */
public class JsonSerializer extends Serializer {
    public static final String MEDIA_TYPE      = "application/vnd.morimekta.providence.json";
    public static final String JSON_MEDIA_TYPE = "application/json";

    public JsonSerializer() {
        this(DEFAULT_STRICT, false, IdType.ID, IdType.ID);
    }

    public JsonSerializer(boolean strict) {
        this(strict, false, IdType.ID, IdType.ID);
    }

    public JsonSerializer pretty() {
        return new JsonSerializer(readStrict, true, IdType.NAME, IdType.NAME);
    }

    public JsonSerializer named() {
        return withNamedEnums().withNamedFields();
    }

    public JsonSerializer withNamedFields() {
        return new JsonSerializer(readStrict, prettyPrint, IdType.NAME, enumValueType);
    }

    public JsonSerializer withNamedEnums() {
        return new JsonSerializer(readStrict, prettyPrint, fieldIdType, IdType.NAME);
    }

    public <T extends PMessage<T, F>, F extends PField> void serialize(@Nonnull PrintWriter output, @Nonnull T message) throws IOException {
        JsonWriter jsonWriter = prettyPrint ? new PrettyJsonWriter(new IndentedPrintWriter(output)) : new JsonWriter(output);
        appendMessage(jsonWriter, message);
        jsonWriter.flush();
    }

    @Override
    public <T extends PMessage<T, F>, F extends PField> int serialize(@Nonnull OutputStream output, @Nonnull T message) throws IOException {
        CountingOutputStream counter = new CountingOutputStream(output);
        JsonWriter jsonWriter = prettyPrint ? new PrettyJsonWriter(counter) : new JsonWriter(counter);
        appendMessage(jsonWriter, message);
        jsonWriter.flush();
        counter.flush();
        return counter.getByteCount();
    }

    @Override
    public <T extends PMessage<T, F>, F extends PField> int serialize(@Nonnull OutputStream output, @Nonnull
            PServiceCall<T, F> call)
            throws IOException {
        CountingOutputStream counter = new CountingOutputStream(output);
        JsonWriter jsonWriter = prettyPrint ? new PrettyJsonWriter(counter) : new JsonWriter(counter);

        jsonWriter.array().value(call.getMethod());
        if (enumValueType == IdType.ID) {
            jsonWriter.value(call.getType().asInteger());
        } else {
            jsonWriter.valueUnescaped(call.getType().asString().toLowerCase(Locale.US));
        }
        jsonWriter.value(call.getSequence());

        appendMessage(jsonWriter, call.getMessage());

        jsonWriter.endArray().flush();
        counter.flush();
        return counter.getByteCount();

    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PMessage<T, TF>, TF extends PField> T deserialize(
            @Nonnull InputStream input, @Nonnull PMessageDescriptor<T, TF> type) throws IOException {
        return deserialize(new Utf8StreamReader(input), type);
    }

    @SuppressWarnings("unchecked")
    public <T extends PMessage<T, TF>, TF extends PField> T deserialize(
            @Nonnull Reader input, @Nonnull PMessageDescriptor<T, TF> type) throws IOException {
        try {
            JsonTokenizer tokenizer = new JsonTokenizer(input, prettyPrint ? PRETTY_READ_BUFFER_SIZE : DEFAULT_READ_BUFFER_SIZE);
            if (!tokenizer.hasNext()) {
                throw new SerializerException("Empty json body");
            }
            return requireNonNull((T) parseTypedValue(tokenizer.expect("Impossible"), tokenizer, type, false));
        } catch (JsonException e) {
            throw new JsonSerializerException(e);
        }
    }

    @Nonnull
    @Override
    public <T extends PMessage<T, F>, F extends PField> PServiceCall<T, F> deserialize(@Nonnull InputStream input, @Nonnull
            PService service)
            throws IOException {
        JsonTokenizer tokenizer = new JsonTokenizer(input, prettyPrint ? PRETTY_READ_BUFFER_SIZE : DEFAULT_READ_BUFFER_SIZE);
        return parseServiceCall(tokenizer, service);
    }

    @Override
    public boolean binaryProtocol() {
        return false;
    }

    @Nonnull
    @Override
    public String mediaType() {
        // Use "application/json" as media type if named fields are used.
        if (fieldIdType == IdType.NAME) {
            return JSON_MEDIA_TYPE;
        }
        return MEDIA_TYPE;
    }

    // ------------------- PRIVATE ONLY ------------------

    private JsonSerializer(boolean readStrict, boolean prettyPrint, IdType fieldIdType, IdType enumValueType) {
        this.readStrict = readStrict;
        this.prettyPrint = prettyPrint;
        this.fieldIdType = fieldIdType;
        this.enumValueType = enumValueType;
    }

    private enum IdType {
        // print field or enums as numeric IDs and values.
        ID,
        // print field or enums as field name and enum name.
        NAME
    }

    private static final int PRETTY_READ_BUFFER_SIZE  = 1 << 10;  //  1024 chars.
    private static final int DEFAULT_READ_BUFFER_SIZE = 1 << 15;  // 32768 chars --> 64kb

    private final boolean readStrict;
    private final IdType  fieldIdType;
    private final IdType  enumValueType;
    private final boolean prettyPrint;

    @SuppressWarnings("unchecked")
    private <T extends PMessage<T, F>, F extends PField> PServiceCall<T, F> parseServiceCall(JsonTokenizer tokenizer, PService service)
            throws IOException {
        PServiceCallType type = null;
        String methodName = null;
        int sequence = 0;
        try {
            tokenizer.expectSymbol("service call start", JsonToken.kListStart);

            methodName = tokenizer.expectString("method name")
                                  .rawJsonLiteral();

            tokenizer.expectSymbol("entry sep", JsonToken.kListSep);

            JsonToken callTypeToken = tokenizer.expect("call type");
            if (callTypeToken.isInteger()) {
                int typeKey = callTypeToken.byteValue();
                type = PServiceCallType.findById(typeKey);
                if (type == null) {
                    throw new SerializerException("Service call type " + typeKey + " is not valid")
                            .setExceptionType(PApplicationExceptionType.INVALID_MESSAGE_TYPE);
                }
            } else if (callTypeToken.isLiteral()) {
                String typeName = callTypeToken.rawJsonLiteral();
                type = PServiceCallType.findByName(typeName.toUpperCase(Locale.US));
                if (type == null) {
                    throw new SerializerException("Service call type \"" + Strings.escape(typeName) + "\" is not valid")
                            .setExceptionType(PApplicationExceptionType.INVALID_MESSAGE_TYPE);
                }
            } else {
                throw new SerializerException("Invalid service call type token " + callTypeToken.asString())
                        .setExceptionType(PApplicationExceptionType.INVALID_MESSAGE_TYPE);
            }

            tokenizer.expectSymbol("entry sep", JsonToken.kListSep);

            sequence = tokenizer.expectNumber("Service call sequence")
                                .intValue();

            tokenizer.expectSymbol("entry sep", JsonToken.kListSep);

            if (type == PServiceCallType.EXCEPTION) {
                PApplicationException ex = (PApplicationException) parseTypedValue(tokenizer.expect("Message start"),
                                                                                   tokenizer,
                                                                                   PApplicationException.kDescriptor,
                                                                                   false);

                tokenizer.expectSymbol("service call end", JsonToken.kListEnd);

                return (PServiceCall<T, F>) new PServiceCall<>(methodName, type, sequence, ex);
            }

            PServiceMethod method = service.getMethod(methodName);
            if (method == null) {
                throw new SerializerException("No such method " + methodName + " on " + service.getQualifiedName())
                        .setExceptionType(PApplicationExceptionType.UNKNOWN_METHOD);
            }

            @SuppressWarnings("unchecked")
            PMessageDescriptor<T, F> descriptor = isRequestCallType(type) ? method.getRequestType() : method.getResponseType();
            if (descriptor == null) {
                throw new SerializerException("No %s type for %s.%s()",
                                              isRequestCallType(type) ? "request" : "response",
                                              service.getQualifiedName(), methodName)
                        .setExceptionType(PApplicationExceptionType.UNKNOWN_METHOD);
            }
            T message = (T) parseTypedValue(tokenizer.expect("message start"), tokenizer, descriptor, false);

            tokenizer.expectSymbol("service call end", JsonToken.kListEnd);

            return new PServiceCall<>(methodName, type, sequence, message);
        } catch (SerializerException se) {
            throw new SerializerException(se)
                    .setMethodName(methodName)
                    .setCallType(type)
                    .setSequenceNo(sequence);
        } catch (JsonException je) {
            throw new JsonSerializerException(je)
                    .setMethodName(methodName)
                    .setCallType(type)
                    .setSequenceNo(sequence);
        }
    }

    private <T extends PMessage<T, F>, F extends PField> T parseMessage(JsonTokenizer tokenizer, PMessageDescriptor<T, F> type)
            throws JsonException, IOException {
        PMessageBuilder<T, F> builder = type.builder();

        if (tokenizer.peek("message end or key").isSymbol(JsonToken.kMapEnd)) {
            tokenizer.next();
        } else {
            char sep = JsonToken.kMapStart;
            while (sep != JsonToken.kMapEnd) {
                JsonToken token = tokenizer.expectString("field spec");
                String key = token.rawJsonLiteral();
                PField field;
                if (Strings.isInteger(key)) {
                    field = type.findFieldById(Integer.parseInt(key));
                } else {
                    field = type.findFieldByName(key);
                }
                tokenizer.expectSymbol("field KV sep", JsonToken.kKeyValSep);

                if (field != null) {
                    Object value = parseTypedValue(tokenizer.expect("field value"), tokenizer, field.getDescriptor(), true);
                    builder.set(field.getId(), value);
                } else {
                    consume(tokenizer.expect("field value"), tokenizer);
                }

                sep = tokenizer.expectSymbol("message end or sep", JsonToken.kMapEnd, JsonToken.kListSep);
            }
        }

        if (readStrict) {
            try {
                builder.validate();
            } catch (IllegalStateException e) {
                throw new SerializerException(e, e.getMessage());
            }
        }

        return builder.build();
    }

    private <T extends PMessage<T, F>, F extends PField> T parseCompactMessage(JsonTokenizer tokenizer, PMessageDescriptor<T, F> type)
            throws IOException, JsonException {
        PMessageBuilder<T, F> builder = type.builder();
        // compact message are not allowed to be empty.

        int i = 0;
        char sep = JsonToken.kListStart;
        while (sep != JsonToken.kListEnd) {
            PField field = type.findFieldById(++i);

            if (field != null) {
                Object value = parseTypedValue(tokenizer.expect("field value"), tokenizer, field.getDescriptor(), true);
                builder.set(i, value);
            } else {
                consume(tokenizer.expect("compact field value"), tokenizer);
            }

            sep = tokenizer.expectSymbol("compact entry sep", JsonToken.kListEnd, JsonToken.kListSep);
        }

        if (readStrict) {
            try {
                builder.validate();
            } catch (IllegalStateException e) {
                throw new SerializerException(e, e.getMessage());
            }
        }

        return builder.build();
    }

    private void consume(JsonToken token, JsonTokenizer tokenizer) throws IOException, JsonException {
        if (token.isSymbol()) {
            if (token.isSymbol(JsonToken.kListStart)) {
                if (tokenizer.peek("lists end or value").isSymbol(JsonToken.kListEnd)) {
                    tokenizer.next();
                } else {
                    char sep = JsonToken.kListStart;
                    while (sep != JsonToken.kListEnd) {
                        consume(tokenizer.expect("list item"), tokenizer);
                        sep = tokenizer.expectSymbol("list sep", JsonToken.kListEnd, JsonToken.kListSep);
                    }
                }
            } else if (token.isSymbol(JsonToken.kMapStart)) {
                if (tokenizer.peek("map end or key").isSymbol(JsonToken.kMapEnd)) {
                    tokenizer.next();
                } else {
                    char sep = JsonToken.kMapStart;
                    while (sep != JsonToken.kMapEnd) {
                        tokenizer.expectString("map key");
                        tokenizer.expectSymbol("map KV sep", JsonToken.kKeyValSep);
                        consume(tokenizer.expect("entry value"), tokenizer);
                        sep = tokenizer.expectSymbol("map end or sep", JsonToken.kMapEnd, JsonToken.kListSep);
                    }
                }
            }
        }
        // Otherwise it is a simple value. No need to consume.
    }

    private Object parseTypedValue(JsonToken token, JsonTokenizer tokenizer, PDescriptor t, boolean allowNull)
            throws IOException, JsonException {
        if (token.isNull()) {
            if (!allowNull) {
                throw new SerializerException("Null value as body.");
            }
            return null;
        }

        switch (t.getType()) {
            case VOID: {
                if (token.isBoolean()) {
                    return token.booleanValue() ? Boolean.TRUE : null;
                }
                throw new SerializerException("Not a void token value: '" + token.asString() + "'");
            }
            case BOOL:
                if (token.isBoolean()) {
                    return token.booleanValue();
                }
                throw new SerializerException("No boolean value for token: '" + token.asString() + "'");
            case BYTE:
                if (token.isInteger()) {
                    return token.byteValue();
                }
                throw new SerializerException("Not a valid byte value: '" + token.asString() + "'");
            case I16:
                if (token.isInteger()) {
                    return token.shortValue();
                }
                throw new SerializerException("Not a valid short value: '" + token.asString() + "'");
            case I32:
                if (token.isInteger()) {
                    return token.intValue();
                }
                throw new SerializerException("Not a valid int value: '" + token.asString() + "'");
            case I64:
                if (token.isInteger()) {
                    return token.longValue();
                }
                throw new SerializerException("Not a valid long value: '" + token.asString() + "'");
            case DOUBLE:
                if (token.isNumber()) {
                    return token.doubleValue();
                }
                throw new SerializerException("Not a valid double value: '" + token.asString() + "'");
            case STRING:
                if (token.isLiteral()) {
                    return token.decodeJsonLiteral();
                }
                throw new SerializerException("Not a valid string value: '" + token.asString() + "'");
            case BINARY:
                if (token.isLiteral()) {
                    try {
                        return Binary.fromBase64(token.rawJsonLiteral());
                    } catch (IllegalArgumentException e) {
                        throw new SerializerException(e, "Unable to parse Base64 data: " + token.asString());
                    }
                }
                throw new SerializerException("Not a valid binary value: " + token.asString());
            case ENUM:
                PEnumBuilder<?> eb = ((PEnumDescriptor<?>) t).builder();
                if (token.isInteger()) {
                    eb.setById(token.intValue());
                } else if (token.isLiteral()) {
                    eb.setByName(token.rawJsonLiteral());
                } else {
                    throw new SerializerException(token.asString() + " is not a enum value type");
                }
                if (!(allowNull || eb.valid())) {
                    throw new SerializerException(token.asString() + " is not a known enum value for " + t.getQualifiedName());
                }
                return eb.build();
            case MESSAGE: {
                PMessageDescriptor<?, ?> st = (PMessageDescriptor<?, ?>) t;
                if (token.isSymbol(JsonToken.kMapStart)) {
                    return parseMessage(tokenizer, st);
                } else if (token.isSymbol(JsonToken.kListStart)) {
                    if (isCompactible(st)) {
                        return parseCompactMessage(tokenizer, st);
                    } else {
                        throw new SerializerException(
                                st.getName() + " is not compatible for compact struct notation.");
                    }
                }
                throw new SerializerException("expected message start, found: '%s'", token.asString());
            }
            case MAP: {
                @SuppressWarnings("unchecked")
                PMap<Object, Object> mapType = (PMap<Object, Object>) t;
                PDescriptor itemType = mapType.itemDescriptor();
                PDescriptor keyType = mapType.keyDescriptor();
                if (!token.isSymbol(JsonToken.kMapStart)) {
                    throw new SerializerException("Invalid start of map '" + token.asString() + "'");
                }
                PMap.Builder<Object, Object> map = mapType.builder();

                if (tokenizer.peek("map end or value").isSymbol(JsonToken.kMapEnd)) {
                    tokenizer.next();
                } else {
                    char sep = JsonToken.kMapStart;
                    while (sep != JsonToken.kMapEnd) {
                        Object key = parseMapKey(tokenizer.expectString("map key")
                                                          .decodeJsonLiteral(), keyType);
                        tokenizer.expectSymbol("map K/V sep", JsonToken.kKeyValSep);
                        Object value = parseTypedValue(tokenizer.expect("map value"), tokenizer, itemType, false);
                        if (key != null && value != null) {
                            // In lenient mode, just drop the entire entry if the
                            // key could not be parsed. Should only be the case
                            // for unknown enum values.
                            // -- parseMapKey checked for strictRead mode.
                            map.put(key, value);
                        }
                        sep = tokenizer.expectSymbol("map end or sep", JsonToken.kMapEnd, JsonToken.kListSep);
                    }
                }
                return map.build();
            }
            case SET: {
                PDescriptor itemType = ((PSet<?>) t).itemDescriptor();
                if (!token.isSymbol(JsonToken.kListStart)) {
                    throw new SerializerException("Invalid start of set '" + token.asString() + "'");
                }
                @SuppressWarnings("unchecked")
                PSet.Builder<Object> set = ((PSet<Object>) t).builder();

                if (tokenizer.peek("set end or value").isSymbol(JsonToken.kListEnd)) {
                    tokenizer.next();
                } else {
                    char sep = JsonToken.kListStart;
                    while (sep != JsonToken.kListEnd) {
                        Object val = parseTypedValue(tokenizer.expect("set value"), tokenizer, itemType, !readStrict);
                        if (val != null) {
                            // In lenient mode, just drop the entire entry if the
                            // key could not be parsed. Should only be the case
                            // for unknown enum values.
                            set.add(val);
                        }
                        sep = tokenizer.expectSymbol("set end or sep", JsonToken.kListSep, JsonToken.kListEnd);
                    }
                }
                return set.build();
            }
            case LIST: {
                PDescriptor itemType = ((PList<?>) t).itemDescriptor();
                if (!token.isSymbol(JsonToken.kListStart)) {
                    throw new SerializerException("Invalid start of list '" + token.asString() + "'");
                }
                @SuppressWarnings("unchecked")
                PList.Builder<Object> list = ((PList<Object>) t).builder();
                if (tokenizer.peek("list end or value").isSymbol(JsonToken.kListEnd)) {
                    tokenizer.next();
                } else {
                    char sep = JsonToken.kListStart;
                    while (sep != JsonToken.kListEnd) {
                        list.add(parseTypedValue(tokenizer.expect("list value"), tokenizer, itemType, false));
                        sep = tokenizer.expectSymbol("list end or sep", JsonToken.kListSep, JsonToken.kListEnd);
                    }
                }
                return list.build();
            }
        }

        throw new SerializerException("Unhandled item type " + t.getQualifiedName());
    }

    private boolean isCompactible(PMessageDescriptor descriptor) {
        return descriptor instanceof JsonCompactibleDescriptor &&
               ((JsonCompactibleDescriptor) descriptor).isJsonCompactible();
    }

    private boolean isCompact(PMessage message) {
        return message instanceof JsonCompactible && ((JsonCompactible) message).jsonCompact();
    }

    private Object parseMapKey(String key, PDescriptor keyType) throws SerializerException {
        try {
            switch (keyType.getType()) {
                case BOOL:
                    if (key.equalsIgnoreCase("true")) {
                        return Boolean.TRUE;
                    } else if (key.equalsIgnoreCase("false")) {
                        return Boolean.FALSE;
                    }
                    throw new SerializerException("Invalid boolean value: \"" + Strings.escape(key) + "\"");
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
                        JsonTokenizer tokenizer = new JsonTokenizer(new ByteArrayInputStream(key.getBytes(
                                StandardCharsets.US_ASCII)));
                        JsonToken token = tokenizer.next();
                        if (!token.isNumber()) {
                            throw new SerializerException("Unable to parse double from key \"" + key + "\"");
                        } else if (tokenizer.hasNext()) {
                            throw new SerializerException("Garbage after double: \"" + key + "\"");
                        }
                        return token.doubleValue();
                    } catch (SerializerException e) {
                        throw e;
                    } catch (JsonException | IOException e) {
                        throw new SerializerException(e, "Unable to parse double from key \"" + key + "\"");
                    }
                case STRING:
                    return key;
                case BINARY:
                    try {
                        return Binary.fromBase64(key);
                    } catch (IllegalArgumentException e) {
                        throw new SerializerException(e, "Unable to parse Base64 data");
                    }
                case ENUM:
                    PEnumBuilder<?> eb = ((PEnumDescriptor<?>) keyType).builder();
                    if (Strings.isInteger(key)) {
                        eb.setById(Integer.parseInt(key));
                    } else {
                        eb.setByName(key);
                    }
                    if (readStrict && !eb.valid()) {
                        throw new SerializerException("\"%s\" is not a known enum value for %s",
                                                      Strings.escape(key), keyType.getQualifiedName());
                    }
                    return eb.build();
                case MESSAGE:
                    PMessageDescriptor<?, ?> st = (PMessageDescriptor<?, ?>) keyType;
                    if (!st.isSimple()) {
                        throw new SerializerException("Only simple structs can be used as map key. %s is not.",
                                                      st.getQualifiedName());
                    }
                    ByteArrayInputStream input = new ByteArrayInputStream(key.getBytes(StandardCharsets.UTF_8));
                    try {
                        JsonTokenizer tokenizer = new JsonTokenizer(input);
                        if (JsonToken.kMapStart ==
                            tokenizer.expectSymbol("message start", JsonToken.kMapStart, JsonToken.kListStart)) {
                            return parseMessage(tokenizer, st);
                        } else {
                            return parseCompactMessage(tokenizer, st);
                        }
                    } catch (JsonException | IOException e) {
                        throw new SerializerException(e, "Error parsing message key: " + e.getMessage());
                    }
                default:
                    throw new SerializerException("Illegal key type: %s", keyType.getType());
            }
        } catch (NumberFormatException nfe) {
            throw new SerializerException(nfe, "Unable to parse numeric value %s", key);
        }
    }

    private void appendMessage(JsonWriter writer, PMessage<?,?> message) throws SerializerException {
        PMessageDescriptor<?, ?> type = message.descriptor();
        if (message instanceof PUnion) {
            writer.object();
            if (((PUnion) message).unionFieldIsSet()) {
                PField field = ((PUnion) message).unionField();
                Object value = message.get(field.getId());
                if (IdType.ID.equals(fieldIdType)) {
                    writer.key(field.getId());
                } else {
                    writer.keyUnescaped(field.getName());
                }
                appendTypedValue(writer, field.getDescriptor(), value);
            }
            writer.endObject();
        } else {
            if (isCompact(message)) {
                writer.array();
                for (PField field : type.getFields()) {
                    if (message.has(field.getId())) {
                        appendTypedValue(writer, field.getDescriptor(), message.get(field.getId()));
                    } else {
                        break;
                    }
                }
                writer.endArray();
            } else {
                writer.object();
                for (PField field : type.getFields()) {
                    if (message.has(field.getId())) {
                        Object value = message.get(field.getId());
                        if (IdType.ID.equals(fieldIdType)) {
                            writer.key(field.getId());
                        } else {
                            writer.keyUnescaped(field.getName());
                        }
                        appendTypedValue(writer, field.getDescriptor(), value);
                    }
                }
                writer.endObject();
            }
        }
    }

    private void appendTypedValue(JsonWriter writer, PDescriptor type, Object value)
            throws SerializerException {
        switch (type.getType()) {
            case VOID:
                writer.value(true);
                break;
            case MESSAGE:
                PMessage<?,?> message = (PMessage<?,?>) value;
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

                PContainer<?> containerType = (PContainer<?>) type;
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
    private void appendPrimitiveKey(JsonWriter writer, Object primitive) throws SerializerException {
        if (primitive instanceof PEnumValue) {
            if (IdType.ID.equals(fieldIdType)) {
                writer.key(((PEnumValue<?>) primitive).asInteger());
            } else {
                writer.keyUnescaped(primitive.toString());
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
            PMessage<?,?> message = (PMessage<?,?>) primitive;
            if (!message.descriptor().isSimple()) {
                throw new SerializerException("Only simple messages can be used as map keys. " +
                                              message.descriptor()
                                                     .getQualifiedName() + " is not.");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JsonWriter json = new JsonWriter(baos);
            appendMessage(json, message);
            json.flush();
            writer.key(new String(baos.toByteArray(), StandardCharsets.UTF_8));
        } else {
            throw new SerializerException("illegal simple type class " + primitive.getClass()
                                                                                  .getSimpleName());
        }
    }

    /**
     * Append a primitive value to json struct.
     *
     * @param writer    The JSON writer.
     * @param primitive The primitive instance.
     */
    private void appendPrimitive(JsonWriter writer, Object primitive) throws SerializerException {
        if (primitive instanceof PEnumValue) {
            if (IdType.ID.equals(enumValueType)) {
                writer.value(((PEnumValue<?>) primitive).asInteger());
            } else {
                writer.valueUnescaped(primitive.toString());
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
        } else if (primitive instanceof CharSequence) {
            writer.value((String) primitive);
        } else if (primitive instanceof Binary) {
            writer.value((Binary) primitive);
        } else {
            throw new SerializerException("illegal primitive type class " + primitive.getClass()
                                                                                     .getSimpleName());
        }
    }
}
