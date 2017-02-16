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
import net.morimekta.providence.util.pretty.Token;
import net.morimekta.providence.util.pretty.Tokenizer;
import net.morimekta.providence.util.pretty.TokenizerException;
import net.morimekta.util.Binary;
import net.morimekta.util.Strings;
import net.morimekta.util.io.CountingOutputStream;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;

/**
 * Pretty printer that can print message content for easily reading and
 * debugging. This is a write only format used in stringifying messages.
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class PrettySerializer extends Serializer {
    public final static String MIME_TYPE = "text/plain";

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

    public PrettySerializer() {
        this(true, DEFAULT_STRICT);
    }

    public PrettySerializer(boolean encloseOuter, boolean strict) {
        this(INDENT, SPACE, NEWLINE, LIST_SEP, encloseOuter, strict);
    }

    public PrettySerializer(String indent,
                            String space,
                            String newline,
                            String entrySep,
                            boolean encloseOuter) {
        this(indent, space, newline, entrySep, encloseOuter, false);
    }

    public PrettySerializer(String indent,
                            String space,
                            String newline,
                            String entrySep,
                            boolean encloseOuter,
                            boolean strict) {
        this.indent = indent;
        this.space = space;
        this.newline = newline;
        this.entrySep = entrySep;
        this.encloseOuter = encloseOuter;
        this.strict = strict;
    }

    public <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(OutputStream out, Message message) {
        CountingOutputStream cout = new CountingOutputStream(out);
        IndentedPrintWriter builder = new IndentedPrintWriter(cout, indent, newline);
        appendMessage(builder, message, encloseOuter);
        builder.flush();
        return cout.getByteCount();
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(OutputStream out, PServiceCall<Message, Field> call)
            throws IOException {
        CountingOutputStream cout = new CountingOutputStream(out);
        IndentedPrintWriter builder = new IndentedPrintWriter(cout, indent, newline);

        builder.format("%d: %s %s(",
                       call.getSequence(),
                       call.getType().getName(),
                       call.getMethod())
               .begin(indent + indent);

        appendMessage(builder, call.getMessage(), true);

        builder.end()
               .append(Token.kMethodEnd)
               .newline()
               .flush();

        return cout.getByteCount();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Message extends PMessage<Message, Field>, Field extends PField>
    PServiceCall<Message, Field> deserialize(InputStream input, PService service)
            throws IOException {
        String methodName = null;
        int sequence = 0;
        PServiceCallType callType = null;
        try {
            // pretty printed service calls cannot be chained-serialized, so this should be totally safe.
            Tokenizer tokenizer = new Tokenizer(input, false);

            Token token = tokenizer.expect("Sequence or type");
            if (token.isInteger()) {
                sequence = (int) token.parseInteger();
                tokenizer.expectSymbol("Sequence type sep", Token.kKeyValueSep);
                token = tokenizer.expectIdentifier("Call Type");
            }
            callType = PServiceCallType.forName(token.asString());
            if (callType == null) {
                throw new TokenizerException(token, "No such call type " + token.asString())
                        .setLine(tokenizer.getLine(token.getLineNo()))
                        .setExceptionType(PApplicationExceptionType.INVALID_MESSAGE_TYPE);
            }

            token = tokenizer.expectIdentifier("method name");
            methodName = token.asString();

            PServiceMethod method = service.getMethod(methodName);
            if (method == null) {
                throw new TokenizerException(token, "no such method " + methodName + " on service " + service.getQualifiedName())
                        .setLine(tokenizer.getLine(token.getLineNo()))
                        .setExceptionType(PApplicationExceptionType.UNKNOWN_METHOD);
            }

            tokenizer.expectSymbol("call params start", Token.kMethodStart);
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

            tokenizer.expectSymbol("Call params closing", Token.kMethodEnd);

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

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    Message deserialize(InputStream input,
                        PMessageDescriptor<Message, Field> descriptor)
            throws IOException {
        Tokenizer tokenizer = new Tokenizer(input, encloseOuter);
        Token first = tokenizer.peek();
        if (first != null && first.isSymbol(Token.kMessageStart)) {
            tokenizer.next();
            return readMessage(tokenizer, descriptor, true);
        } else {
            return readMessage(tokenizer, descriptor, false);
        }
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
                        .setLine(tokenizer.getLine(token.getLineNo()));
            }

            tokenizer.expectSymbol("field value separator", Token.kFieldValueSep);

            PField field = descriptor.getField(token.asString());
            if (field == null) {
                if (strict) {
                    throw new TokenizerException(token, "No such field %s on %s", token.asString(), descriptor.getQualifiedName()).setLine(tokenizer.getLine(token.getLineNo()));
                }
                consumeValue(tokenizer, tokenizer.expect("field value"));
            } else {
                builder.set(field.getKey(), readFieldValue(
                        tokenizer, tokenizer.expect("field value"), field.getDescriptor()));
            }

            token = tokenizer.peek();
            if (token != null && (token.isSymbol(Token.kLineSep1) || token.isSymbol(Token.kLineSep2))) {
                tokenizer.next();
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
                        .setLine(tokenizer.getLine(token.getLineNo()));
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
                        .setLine(tokenizer.getLine(token.getLineNo()));

            }
            case BYTE: {
                if (token.isInteger()) {
                    long val = token.parseInteger();
                    if (val > Byte.MAX_VALUE || val < Byte.MIN_VALUE) {
                        throw new TokenizerException(token, "Byte value out of bounds: " + token.asString())
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }
                    return (byte) val;
                } else {
                    throw new TokenizerException(token, "Invalid byte value: " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case I16: {
                if (token.isInteger()) {
                    long val = token.parseInteger();
                    if (val > Short.MAX_VALUE || val < Short.MIN_VALUE) {
                        throw new TokenizerException(token, "Short value out of bounds: " + token.asString())
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }
                    return (short) val;
                } else {
                    throw new TokenizerException(token, "Invalid byte value: " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case I32: {
                if (token.isInteger()) {
                    long val = token.parseInteger();
                    if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
                        throw new TokenizerException(token, "Integer value out of bounds: " + token.asString())
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }
                    return (int) val;
                } else {
                    throw new TokenizerException(token, "Invalid byte value: " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case I64: {
                if (token.isInteger()) {
                    return token.parseInteger();
                } else {
                    throw new TokenizerException(token, "Invalid byte value: " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case DOUBLE: {
                try {
                    return token.parseDouble();
                } catch (NumberFormatException nfe) {
                    throw new TokenizerException(token, "Number format error: " + nfe.getMessage())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case STRING: {
                if (!token.isStringLiteral()) {
                    throw new TokenizerException(token, "Expected string literal, got '%s'", token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
                return token.decodeLiteral();
            }
            case BINARY: {
                tokenizer.expectSymbol("binary content start", Token.kMethodStart);
                String content = tokenizer.readUntil(Token.kMethodEnd, false, false);
                switch (token.asString()) {
                    case "b64":
                        return Binary.fromBase64(content);
                    case "hex":
                        return Binary.fromHexString(content);
                    default:
                        throw new TokenizerException(token, "Unrecognized binary format " + token.asString())
                                .setLine(tokenizer.getLine(token.getLineNo()));
                }
            }
            case ENUM: {
                PEnumBuilder b = ((PEnumDescriptor) descriptor).builder();
                b.setByName(token.asString());
                if (strict && !b.valid()) {
                    throw new TokenizerException(token, "No such " + descriptor.getQualifiedName() + " value " + token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
                return b.build();
            }
            case MESSAGE: {
                if (!token.isSymbol(Token.kMessageStart)) {
                    throw new TokenizerException(token, "Expected message start, got '%s'", token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
                }
                return readMessage(tokenizer, (PMessageDescriptor<?, ?>) descriptor, true);
            }
            case MAP: {
                if (!token.isSymbol(Token.kMessageStart)) {
                    throw new TokenizerException(token, "Expected map start, got '%s'", token.asString())
                            .setLine(tokenizer.getLine(token.getLineNo()));
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
                            .setLine(tokenizer.getLine(token.getLineNo()));
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
                            .setLine(tokenizer.getLine(token.getLineNo()));
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

    @Override
    public String mimeType() {
        return MIME_TYPE;
    }

    private void appendMessage(IndentedPrintWriter builder, PMessage<?,?> message, boolean enclose) {
        PMessageDescriptor<?, ?> type = message.descriptor();

        if (enclose) {
            builder.append(Token.kMessageStart)
                   .begin();
        }

        if (message instanceof PUnion) {
            PField field = ((PUnion) message).unionField();
            if (field != null) {
                Object o = message.get(field.getKey());

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
                if (message.has(field.getKey())) {
                    if (first) {
                        first = false;
                        if (enclose) {
                            builder.appendln();
                        }
                    } else {
                        builder.appendln();
                    }
                    Object o = message.get(field.getKey());

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
                  .append(Token.kMethodStart)
                  .append(b.toBase64())
                  .append(Token.kMethodEnd);
        } else if (o instanceof Boolean) {
            writer.print(((Boolean) o).booleanValue());
        } else if (o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Long) {
            writer.print(Strings.escape(o.toString()));
        } else if (o instanceof Double) {
            Double d = (Double) o;
            if (d == ((double) d.longValue())) {
                // actually an integer or long value.
                writer.print(d.longValue());
            } else if (d > ((10 << 9) - 1) || (1 / d) > (10 << 6)) {
                // Scientific notation should be used, this enforces a decimal
                // length that is not too overwhelming.
                writer.print((new DecimalFormat("0.#########E0")).format(d.doubleValue()));
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

            if (!token.isSymbol(Token.kMessageEnd) && !token.isIdentifier()) {
                // assume map.
                while (!token.isSymbol(Token.kMessageEnd)) {
                    if (token.isIdentifier() || token.isReferenceIdentifier()) {
                        throw new TokenizerException(token, "Invalid map key: " + token.asString())
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }
                    consumeValue(tokenizer, token);
                    tokenizer.expectSymbol("key value sep.", Token.kKeyValueSep);
                    consumeValue(tokenizer, tokenizer.expect("map value"));

                    // maps do *not* require separator, but allows ',' separator, and separator after last.
                    token = tokenizer.expect("map key, end or sep");
                    if (token.isSymbol(Token.kLineSep1)) {
                        token = tokenizer.expect("map key or end");
                    }
                }
            } else {
                // assume message.
                while (!token.isSymbol(Token.kMessageEnd)) {
                    if (!token.isIdentifier()) {
                        throw new TokenizerException(token, "Invalid field name: " + token.asString())
                                .setLine(tokenizer.getLine(token.getLineNo()));
                    }

                    tokenizer.expectSymbol("field value sep.", Token.kFieldValueSep);
                    consumeValue(tokenizer, tokenizer.next());
                    token = nextNotLineSep(tokenizer, "message field or end");
                }
            }
        } else if (token.isSymbol(Token.kListStart)) {
            token = tokenizer.next();
            while (!token.isSymbol(Token.kListEnd)) {
                consumeValue(tokenizer, token);
                // lists and sets require list separator (,), and allows trailing separator.
                if (tokenizer.expectSymbol("list separator or end", Token.kLineSep1, Token.kListEnd) == Token.kListEnd) {
                    break;
                }
                token = tokenizer.expect("list value or end");
            }
        } else if (token.asString().equals(Token.HEX)) {
            tokenizer.expectSymbol("hex body start", Token.kMethodStart);
            tokenizer.readUntil(Token.kMethodEnd, false, false);
        } else if (!(token.isReal() ||  // number (double)
                     token.isInteger() ||  // number (int)
                     token.isStringLiteral() ||  // string literal
                     token.isIdentifier())) {  // enum value reference.
            throw new TokenizerException(token, "Unknown value token '%s'", token.asString())
                    .setLine(tokenizer.getLine(token.getLineNo()));
        }
    }

    private Token nextNotLineSep(Tokenizer tokenizer, String message) throws IOException {
        if (tokenizer.peek().isSymbol(Token.kLineSep1) ||
            tokenizer.peek().isSymbol(Token.kLineSep2)) {
            tokenizer.expect(message);
        }
        return tokenizer.expect(message);
    }
}
