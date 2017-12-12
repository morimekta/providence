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
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.serializer.pretty.Token;
import net.morimekta.providence.serializer.pretty.Tokenizer;
import net.morimekta.providence.serializer.pretty.TokenizerException;
import net.morimekta.util.Binary;
import net.morimekta.util.Strings;
import net.morimekta.util.io.CountingOutputStream;
import net.morimekta.util.io.IndentedPrintWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Pretty printer that can print message content for easily reading and
 * debugging. This is a write only format used in stringifying messages.
 */
public class PrettySerializer extends Serializer {
    public final static String MEDIA_TYPE = "text/plain";

    private final static String INDENT   = "  ";
    private final static String SPACE    = " ";
    private final static String NEWLINE  = "\n";
    private final static String LIST_SEP = ",";

    private final String  indent;
    private final String  space;
    private final String  newline;
    private final String  entrySep;
    private final boolean encloseOuter;
    private final boolean strict;
    private final boolean prefixWithQualifiedName;

    public PrettySerializer() {
        this(DEFAULT_STRICT);
    }

    public PrettySerializer(boolean strict) {
        this(INDENT, SPACE, NEWLINE, "", true, strict, false);
    }

    /**
     * Make a PrettySerializer that generates content similar to the PMessage asString methods.
     * The output of this has <b>very little</b> whitespace, so can be pretty difficult to read.
     * It's similar to the {@link #string()} variant, but without the qualified name prefix.
     *
     * @return Compact pretty serializer.
     */
    public PrettySerializer compact() {
        return new PrettySerializer("", "", "", LIST_SEP, true, strict, false);
    }

    /**
     * Make a PrettySerializer that generates content similar to the PMessage toString methods.
     * The output of this has <b>very little</b> whitespace, so can be pretty difficult to read.
     * It prefixes the message with the root message qualified name, as any
     * {@link PMessage}.toString() would expect.
     *
     * @return String pretty serializer.
     */
    public PrettySerializer string() {
        return new PrettySerializer("", "", "", LIST_SEP, true, strict, true);
    }

    /**
     * Make a PrettySerializer that generates content similar to what the ProvidenceConfig
     * reads. It will not make use of  references or anything fancy though.
     *
     * @return Config-like pretty serializer.
     */
    public PrettySerializer config() {
        return new PrettySerializer(indent,
                                    space,
                                    newline,
                                    entrySep,
                                    true,
                                    strict,
                                    true);
    }

    /**
     * Make a PrettySerializer that generates content with minimal diff.
     *
     * @return Debug pretty serializer.
     */
    public PrettySerializer debug() {
        return new PrettySerializer(indent,
                                    space,
                                    newline,
                                    entrySep,
                                    false,
                                    strict,
                                    prefixWithQualifiedName);
    }

    private PrettySerializer(String indent,
                             String space,
                             String newline,
                             String entrySep,
                             boolean encloseOuter,
                             boolean strict,
                             boolean prefixWithQualifiedName) {
        this.indent = indent;
        this.space = space;
        this.newline = newline;
        this.entrySep = entrySep;
        this.encloseOuter = encloseOuter;
        this.strict = strict;
        this.prefixWithQualifiedName = prefixWithQualifiedName;
    }

    public <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(@Nonnull OutputStream out, @Nonnull Message message) {
        CountingOutputStream cout = new CountingOutputStream(out);
        IndentedPrintWriter builder = new IndentedPrintWriter(cout, indent, newline);
        if (prefixWithQualifiedName) {
            builder.append(message.descriptor().getQualifiedName())
                   .append(space);
        }
        appendMessage(builder, message, encloseOuter || prefixWithQualifiedName);
        builder.flush();
        return cout.getByteCount();
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(@Nonnull OutputStream out, @Nonnull PServiceCall<Message, Field> call)
            throws IOException {
        CountingOutputStream cout = new CountingOutputStream(out);
        IndentedPrintWriter builder = new IndentedPrintWriter(cout, indent, newline);

        builder.format("%d: %s %s(",
                       call.getSequence(),
                       call.getType().asString(),
                       call.getMethod())
               .begin(indent + indent);

        appendMessage(builder, call.getMessage(), true);

        builder.end()
               .append(Token.kParamsEnd)
               .newline()
               .flush();

        return cout.getByteCount();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <Message extends PMessage<Message, Field>, Field extends PField>
    PServiceCall<Message, Field> deserialize(@Nonnull InputStream input, @Nonnull PService service)
            throws IOException {
        String methodName = null;
        int sequence = 0;
        PServiceCallType callType = null;
        try {
            // pretty printed service calls cannot be chained-serialized, so this should be totally safe.
            Tokenizer tokenizer = new Tokenizer(input);

            Token token = tokenizer.expect("Sequence or type");
            if (token.isInteger()) {
                sequence = (int) token.parseInteger();
                tokenizer.expectSymbol("Sequence type sep", Token.kKeyValueSep);
                token = tokenizer.expectIdentifier("Call Type");
            }
            callType = PServiceCallType.findByName(token.asString());
            if (callType == null) {
                throw new TokenizerException(token, "No such call type " + token.asString())
                        .setLine(tokenizer.getLine())
                        .setExceptionType(PApplicationExceptionType.INVALID_MESSAGE_TYPE);
            }

            token = tokenizer.expectIdentifier("method name");
            methodName = token.asString();

            PServiceMethod method = service.getMethod(methodName);
            if (method == null) {
                throw new TokenizerException(token, "no such method " + methodName + " on service " + service.getQualifiedName())
                        .setLine(tokenizer.getLine())
                        .setExceptionType(PApplicationExceptionType.UNKNOWN_METHOD);
            }

            tokenizer.expectSymbol("call params start", Token.kParamsStart);
            tokenizer.expectSymbol("message encloser", Token.kMessageStart);

            Message message;
            switch (callType) {
                case CALL:
                case ONEWAY:
                    message = (Message) readMessage(tokenizer, method.getRequestType(), true);
                    break;
                case REPLY:
                    message = (Message) readMessage(tokenizer, method.getResponseType(), true);
                    break;
                case EXCEPTION:
                    message = (Message) readMessage(tokenizer, PApplicationException.kDescriptor, true);
                    break;
                default:
                    throw new IllegalStateException("Unreachable code reached");
            }

            tokenizer.expectSymbol("Call params closing", Token.kParamsEnd);

            return new PServiceCall<>(methodName, callType, sequence, message);
        } catch (TokenizerException e) {
            throw new TokenizerException(e, null)
                    .setCallType(callType)
                    .setSequenceNo(sequence)
                    .setMethodName(methodName);
        } catch (IOException e) {
            throw new SerializerException(e, e.getMessage())
                    .setCallType(callType)
                    .setSequenceNo(sequence)
                    .setMethodName(methodName);
        }
    }

    @Nonnull
    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    Message deserialize(@Nonnull InputStream input,
                        @Nonnull PMessageDescriptor<Message, Field> descriptor)
            throws IOException {
        Tokenizer tokenizer = new Tokenizer(input);
        if (!tokenizer.hasNext() && !encloseOuter) {
            return descriptor.builder().build();
        }
        Token first = tokenizer.peek("start of message");

        boolean requireEnd = false;
        if (first.isQualifiedIdentifier() &&
            first.asString().equals(descriptor.getQualifiedName())) {
            tokenizer.next();  // skip the name
            tokenizer.expectSymbol("message start", Token.kMessageStart);
            requireEnd = true;
        } else if (first.isSymbol(Token.kMessageStart)) {
            tokenizer.next();
            requireEnd = true;
        }
        return readMessage(tokenizer, descriptor, requireEnd);
    }

    private <Message extends PMessage<Message, Field>, Field extends PField>
    Message readMessage(Tokenizer tokenizer,
                        PMessageDescriptor<Message, Field> descriptor,
                        boolean requireEnd)
            throws IOException {
        PMessageBuilder<Message, Field> builder = descriptor.builder();

        Token token = tokenizer.next();
        for (;;) {
            if (token == null) {
                if (requireEnd) {
                    throw new TokenizerException("Unexpected end of stream");
                }
                break;
            } else if (token.isSymbol(Token.kMessageEnd)) {
                break;
            }

            if (!token.isIdentifier()) {
                throw new TokenizerException(token, "Expected field name, got '%s'",
                                             Strings.escape(token.asString()))
                        .setLine(tokenizer.getLine());
            }

            tokenizer.expectSymbol("field value separator", Token.kFieldValueSep);

            PField field = descriptor.findFieldByName(token.asString());
            if (field == null) {
                consumeValue(tokenizer, tokenizer.expect("field value"));
            } else {
                builder.set(field.getId(), readFieldValue(
                        tokenizer, tokenizer.expect("field value"), field.getDescriptor()));
            }

            if (tokenizer.hasNext()) {
                token = tokenizer.peek("");
                if (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2)) {
                    tokenizer.next();
                }
            }
            token = tokenizer.next();
        }
        return builder.build();
    }

    private Object readFieldValue(Tokenizer tokenizer, Token token, PDescriptor descriptor) throws IOException {
        switch (descriptor.getType()) {
            case VOID: {
                // Even void fields needs a value token...
                // Allow any boolean true value that is an _identifier_. No numbers here.
                switch (token.asString().toLowerCase()) {
                    case "t":
                    case "true":
                    case "y":
                    case "yes":
                        return Boolean.TRUE;
                }
                throw new TokenizerException(token, "Invalid void value " + token.asString())
                        .setLine(tokenizer.getLine());
            }
            case BOOL: {
                switch (token.asString().toLowerCase()) {
                    case "1":
                    case "t":
                    case "true":
                    case "y":
                    case "yes":
                        return Boolean.TRUE;
                    case "0":
                    case "f":
                    case "false":
                    case "n":
                    case "no":
                        return Boolean.FALSE;
                }
                throw new TokenizerException(token, "Invalid boolean value " + token.asString())
                        .setLine(tokenizer.getLine());

            }
            case BYTE: {
                if (token.isInteger()) {
                    long val = token.parseInteger();
                    if (val > Byte.MAX_VALUE || val < Byte.MIN_VALUE) {
                        throw new TokenizerException(token, "Byte value out of bounds: " + token.asString())
                                .setLine(tokenizer.getLine());
                    }
                    return (byte) val;
                } else {
                    throw new TokenizerException(token, "Invalid byte value: " + token.asString())
                            .setLine(tokenizer.getLine());
                }
            }
            case I16: {
                if (token.isInteger()) {
                    long val = token.parseInteger();
                    if (val > Short.MAX_VALUE || val < Short.MIN_VALUE) {
                        throw new TokenizerException(token, "Short value out of bounds: " + token.asString())
                                .setLine(tokenizer.getLine());
                    }
                    return (short) val;
                } else {
                    throw new TokenizerException(token, "Invalid byte value: " + token.asString())
                            .setLine(tokenizer.getLine());
                }
            }
            case I32: {
                if (token.isInteger()) {
                    long val = token.parseInteger();
                    if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
                        throw new TokenizerException(token, "Integer value out of bounds: " + token.asString())
                                .setLine(tokenizer.getLine());
                    }
                    return (int) val;
                } else {
                    throw new TokenizerException(token, "Invalid byte value: " + token.asString())
                            .setLine(tokenizer.getLine());
                }
            }
            case I64: {
                if (token.isInteger()) {
                    return token.parseInteger();
                } else {
                    throw new TokenizerException(token, "Invalid byte value: " + token.asString())
                            .setLine(tokenizer.getLine());
                }
            }
            case DOUBLE: {
                try {
                    return token.parseDouble();
                } catch (NumberFormatException nfe) {
                    throw new TokenizerException(token, "Number format error: " + nfe.getMessage())
                            .setLine(tokenizer.getLine());
                }
            }
            case STRING: {
                if (!token.isStringLiteral()) {
                    throw new TokenizerException(token, "Expected string literal, got '%s'", token.asString())
                            .setLine(tokenizer.getLine());
                }
                return token.decodeLiteral(strict);
            }
            case BINARY: {
                tokenizer.expectSymbol("binary content start", Token.kParamsStart);
                String content = tokenizer.readBinary(Token.kParamsEnd);
                content = content.replaceAll("[\\s\\n=]*", "");
                switch (token.asString()) {
                    case "b64":
                        return Binary.fromBase64(content);
                    case "hex":
                        return Binary.fromHexString(content);
                    default:
                        throw new TokenizerException(token, "Unrecognized binary format " + token.asString())
                                .setLine(tokenizer.getLine());
                }
            }
            case ENUM: {
                PEnumBuilder b = ((PEnumDescriptor) descriptor).builder();
                b.setByName(token.asString());
                if (strict && !b.valid()) {
                    throw new TokenizerException(token, "No such " + descriptor.getQualifiedName() + " value " + token.asString())
                            .setLine(tokenizer.getLine());
                }
                return b.build();
            }
            case MESSAGE: {
                if (!token.isSymbol(Token.kMessageStart)) {
                    throw new TokenizerException(token, "Expected message start, got '%s'", token.asString())
                            .setLine(tokenizer.getLine());
                }
                return readMessage(tokenizer, (PMessageDescriptor<?, ?>) descriptor, true);
            }
            case MAP: {
                if (!token.isSymbol(Token.kMessageStart)) {
                    throw new TokenizerException(token, "Expected map start, got '%s'", token.asString())
                            .setLine(tokenizer.getLine());
                }
                @SuppressWarnings("unchecked")
                PMap<Object, Object> pMap = (PMap) descriptor;
                PDescriptor kDesc = pMap.keyDescriptor();
                PDescriptor iDesc = pMap.itemDescriptor();

                PMap.Builder<Object, Object> builder = pMap.builder();

                token = tokenizer.expect("list end or value");
                while (!token.isSymbol(Token.kMessageEnd)) {
                    Object key = readFieldValue(tokenizer, token, kDesc);
                    tokenizer.expectSymbol("map kv sep", Token.kKeyValueSep);
                    Object value = readFieldValue(tokenizer, tokenizer.expect("map value"), iDesc);
                    builder.put(key, value);
                    token = tokenizer.expect("map sep, end or value");
                    if (token.isSymbol(Token.kLineSep1)) {
                        token = tokenizer.expect("map end or value");
                    }
                }
                return builder.build();
            }
            case LIST: {
                if (!token.isSymbol(Token.kListStart)) {
                    throw new TokenizerException(token, "Expected list start, got '%s'", token.asString())
                            .setLine(tokenizer.getLine());
                }
                @SuppressWarnings("unchecked")
                PList<Object> pList = (PList) descriptor;
                PDescriptor iDesc = pList.itemDescriptor();

                PList.Builder<Object> builder = pList.builder();

                token = tokenizer.expect("list end or value");
                while (!token.isSymbol(Token.kListEnd)) {
                    builder.add(readFieldValue(tokenizer, token, iDesc));
                    token = tokenizer.expect("list sep, end or value");
                    if (token.isSymbol(Token.kLineSep1)) {
                        token = tokenizer.expect("list end or value");
                    }
                }

                return builder.build();
            }
            case SET: {
                if (!token.isSymbol(Token.kListStart)) {
                    throw new TokenizerException(token, "Expected set start, got '%s'", token.asString())
                            .setLine(tokenizer.getLine());
                }
                @SuppressWarnings("unchecked")
                PSet<Object> pList = (PSet) descriptor;
                PDescriptor iDesc = pList.itemDescriptor();

                PSet.Builder<Object> builder = pList.builder();

                token = tokenizer.expect("set end or value");
                while (!token.isSymbol(Token.kListEnd)) {
                    builder.add(readFieldValue(tokenizer, token, iDesc));
                    token = tokenizer.expect("set sep, end or value");
                    if (token.isSymbol(Token.kLineSep1)) {
                        token = tokenizer.expect("set end or value");
                    }
                }

                return builder.build();
            }
            default: {
                throw new IllegalStateException("Unhandled field type: " + descriptor.getType());
            }
        }
    }

    @Override
    public boolean binaryProtocol() {
        return false;
    }

    @Nonnull
    @Override
    public String mediaType() {
        return MEDIA_TYPE;
    }

    private void appendMessage(IndentedPrintWriter builder, PMessage<?,?> message, boolean enclose) {
        PMessageDescriptor<?, ?> type = message.descriptor();

        if (enclose) {
            builder.append(Token.kMessageStart)
                   .begin();
        }

        if (message instanceof PUnion) {
            if (((PUnion) message).unionFieldIsSet()) {
                PField field = ((PUnion) message).unionField();
                Object o = message.get(field.getId());

                if (enclose) {
                    builder.appendln();
                }

                builder.append(field.getName())
                       .append(space)
                       .append(Token.kFieldValueSep)
                       .append(space);
                appendTypedValue(builder, field.getDescriptor(), o);
            }
        } else {
            boolean first = true;
            for (PField field : type.getFields()) {
                if (message.has(field.getId())) {
                    if (first) {
                        first = false;
                        if (enclose) {
                            builder.appendln();
                        }
                    } else {
                        builder.append(entrySep);
                        builder.appendln();
                    }
                    Object o = message.get(field.getId());

                    builder.append(field.getName())
                           .append(space)
                           .append(Token.kFieldValueSep)
                           .append(space);
                    appendTypedValue(builder, field.getDescriptor(), o);
                }
            }
        }

        if (enclose) {
            builder.end()
                   .appendln(Token.kMessageEnd);
        }
    }

    private void appendTypedValue(IndentedPrintWriter writer, PDescriptor descriptor, Object o) {
        switch (descriptor.getType()) {
            case LIST:
            case SET: {
                PContainer<?> containerType = (PContainer<?>) descriptor;
                PDescriptor itemType = containerType.itemDescriptor();
                Collection<?> collection = (Collection<?>) o;

                PPrimitive primitive = PPrimitive.findByName(itemType.getName());
                if (primitive != null &&
                    primitive != PPrimitive.STRING &&
                    primitive != PPrimitive.BINARY &&
                    collection.size() <= 10) {
                    // special case if we have simple primitives (numbers and bools) in a "short" list,
                    // print in one single line.
                    writer.append(Token.kListStart);

                    boolean first = true;
                    for (Object i : collection) {
                        if (first) {
                            first = false;
                        } else {
                            // Lists are always comma-delimited
                            writer.append(Token.kLineSep1)
                                  .append(space);
                        }
                        appendTypedValue(writer, containerType.itemDescriptor(), i);
                    }
                    writer.append(Token.kListEnd);
                } else {
                    writer.append(Token.kListStart)
                          .begin();

                    boolean first = true;
                    for (Object i : collection) {
                        if (first) {
                            first = false;
                        } else {
                            // Lists are always comma-delimited
                            writer.append(Token.kLineSep1);
                        }
                        writer.appendln();
                        appendTypedValue(writer, containerType.itemDescriptor(), i);
                    }

                    writer.end()
                          .appendln(Token.kListEnd);
                }
                break;
            }
            case MAP: {
                PMap<?, ?> mapType = (PMap<?, ?>) descriptor;

                Map<?, ?> map = (Map<?, ?>) o;

                writer.append(Token.kMessageStart)
                      .begin();

                boolean first = true;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(entrySep);
                    }
                    writer.appendln();
                    appendTypedValue(writer, mapType.keyDescriptor(), entry.getKey());
                    writer.append(Token.kKeyValueSep)
                          .append(space);
                    appendTypedValue(writer, mapType.itemDescriptor(), entry.getValue());
                }

                writer.end()
                      .appendln(Token.kMessageEnd);
                break;
            }
            case VOID:
                writer.print(true);
                break;
            case MESSAGE:
                PMessage<?,?> message = (PMessage<?, ?>) o;
                appendMessage(writer, message, true);
                break;
            default:
                appendPrimitive(writer, o);
                break;
        }
    }

    private void appendPrimitive(IndentedPrintWriter writer, Object o) {
        if (o instanceof PEnumValue) {
            writer.print(((PEnumValue) o).asString());
        } else if (o instanceof CharSequence) {
            writer.print(Token.kLiteralDoubleQuote);
            writer.print(Strings.escape((CharSequence) o));
            writer.print(Token.kLiteralDoubleQuote);
        } else if (o instanceof Binary) {
            Binary b = (Binary) o;
            writer.append(Token.B64)
                  .append(Token.kParamsStart)
                  .append(b.toBase64())
                  .append(Token.kParamsEnd);
        } else if (o instanceof Boolean) {
            writer.print(((Boolean) o).booleanValue());
        } else if (o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Long) {
            writer.print(o.toString());
        } else if (o instanceof Double) {
            Double d = (Double) o;
            if (d.equals(((double) d.longValue()))) {
                // actually an integer or long value.
                writer.print(d.longValue());
            } else {
                writer.print(d.doubleValue());
            }
        } else {
            throw new IllegalArgumentException("Unknown primitive type class " + o.getClass()
                                                                                  .getSimpleName());
        }
    }

    private void consumeValue(Tokenizer tokenizer, Token token) throws IOException {
        if (token.isSymbol(Token.kMessageStart)) {
            // message or map.
            token = tokenizer.expect("map or message first entry");
            // ignore empty map or message.
            if (!token.isSymbol(Token.kMessageEnd)) {
                // key = value: message
                // key : value: map

                // potential message.
                boolean idKey = token.isIdentifier();
                consumeValue(tokenizer, token);
                if (tokenizer.expectSymbol("map", ':', '=') == Token.kFieldValueSep) {
                    // message!
                    if (!idKey) {
                        // TODO: fail!
                    }
                    consumeValue(tokenizer, tokenizer.expect("message field value"));
                    token = nextNotLineSep(tokenizer);
                    while (!token.isSymbol(Token.kMessageEnd)) {
                        if (!token.isIdentifier()) {
                            // TODO: fail.
                        }
                        tokenizer.expectSymbol("message field value sep", Token.kFieldValueSep);
                        consumeValue(tokenizer, tokenizer.expect("message field value"));
                        token = nextNotLineSep(tokenizer);
                    }
                } else {
                    // map!
                    consumeValue(tokenizer, tokenizer.expect("map entry value"));
                    token = nextNotLineSep(tokenizer);
                    while (!token.isSymbol(Token.kMessageEnd)) {
                        consumeValue(tokenizer, token);
                        tokenizer.expectSymbol("message field value sep", Token.kKeyValueSep);
                        consumeValue(tokenizer, tokenizer.expect("message field value"));
                        token = nextNotLineSep(tokenizer);
                    }
                }

                if (!token.isSymbol(Token.kFieldValueSep)) {
                    // assume map.
                    while (!token.isSymbol(Token.kMessageEnd)) {
                        consumeValue(tokenizer, token);
                        tokenizer.expectSymbol("key value sep.", Token.kKeyValueSep);
                        consumeValue(tokenizer, tokenizer.expect("map value"));

                        // maps do *not* require separator, but allows ',' separator, and separator after last.
                        token = nextNotLineSep(tokenizer);
                    }
                } else {
                    // assume message.
                    while (!token.isSymbol(Token.kMessageEnd)) {
                        if (!token.isIdentifier()) {
                            throw new TokenizerException(token, "Invalid field name: " + token.asString())
                                    .setLine(tokenizer.getLine());
                        }

                        tokenizer.expectSymbol("field value sep.", Token.kFieldValueSep);
                        consumeValue(tokenizer, tokenizer.next());
                        token = nextNotLineSep(tokenizer);
                    }
                }
            }
        } else if (token.isSymbol(Token.kListStart)) {
            token = tokenizer.expect("");
            while (!token.isSymbol(Token.kListEnd)) {
                consumeValue(tokenizer, token);
                // lists and sets require list separator (,), and allows trailing separator.
                if (tokenizer.expectSymbol("list separator or end", Token.kLineSep1, Token.kListEnd) == Token.kListEnd) {
                    break;
                }
                token = tokenizer.expect("list value or end");
            }
        } else if (token.strEquals(Token.HEX) ||
                   token.strEquals(Token.B64)) {
            tokenizer.expectSymbol("hex body start", Token.kParamsStart);
            tokenizer.readBinary(Token.kParamsEnd);
        } else if (!(token.isReal() ||  // number (double)
                     token.isInteger() ||  // number (int)
                     token.isStringLiteral() ||  // string literal
                     token.isIdentifier())) {  // enum value reference.
            throw new TokenizerException(token, "Unknown value token '%s'", token.asString())
                    .setLine(tokenizer.getLine());
        }
    }

    private Token nextNotLineSep(Tokenizer tokenizer) throws IOException {
        if (tokenizer.peek("").isSymbol(Token.kLineSep1) ||
            tokenizer.peek("").isSymbol(Token.kLineSep2)) {
            tokenizer.expect("message field or end");
        }
        return tokenizer.expect("message field or end");
    }
}
