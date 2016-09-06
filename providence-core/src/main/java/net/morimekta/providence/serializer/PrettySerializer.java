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
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.PType;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.util.pretty.Token;
import net.morimekta.providence.util.pretty.Tokenizer;
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
    private final boolean repeatedListEntries;

    public PrettySerializer() {
        this(INDENT, SPACE, NEWLINE, LIST_SEP, true, false);
    }

    public PrettySerializer(String indent,
                            String space,
                            String newline,
                            String entrySep,
                            boolean encloseOuter,
                            boolean repeatedListEntries) {
        this.indent = indent;
        this.space = space;
        this.newline = newline;
        this.entrySep = entrySep;
        this.encloseOuter = encloseOuter;
        this.repeatedListEntries = repeatedListEntries;
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
            throws IOException, SerializerException {
        CountingOutputStream cout = new CountingOutputStream(out);
        IndentedPrintWriter builder = new IndentedPrintWriter(cout, indent, newline);

        builder.format("%d: %s %s",
                       call.getSequence(),
                       call.getType().toString(),
                       call.getMethod())
               .begin(indent + indent);

        appendMessage(builder, call.getMessage(), true);

        builder.end()
               .newline()
               .flush();

        return cout.getByteCount();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Message extends PMessage<Message, Field>, Field extends PField>
    PServiceCall<Message, Field> deserialize(InputStream input, PService service)
            throws SerializerException, IOException {
        // pretty printed service calls cannot be chained-serialized, so this should be totally safe.
        Tokenizer tokenizer = new Tokenizer(input, false);

        Token token = tokenizer.expect("Sequence or type");
        int sequence = 0;
        if (token.isInteger()) {
            sequence = (int) token.parseInteger();
            tokenizer.expectSymbol("Sequence type sep", Token.kKeyValueSep);
            token = tokenizer.expectIdentifier("Call Type");
        }
        PServiceCallType callType = PServiceCallType.findByName(token.asString());
        if (callType == null) {
            throw new SerializerException("No such call type " + token.asString());
        }

        String methodName = tokenizer.expectIdentifier("Method name").asString();

        PServiceMethod method = service.getMethod(methodName);
        if (method == null) {
            throw new SerializerException("No such method " + methodName + " on service " + service.getQualifiedName(null));
        }

        tokenizer.expectSymbol("Call params start", '(');
        tokenizer.expectSymbol("Message encloser", '{');

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
                message = (Message) readMessage(tokenizer, ApplicationException.kDescriptor, true);
                break;
            default:
                throw new IllegalStateException("Unreachable code reached");
        }

        tokenizer.expectSymbol("Call params closing", ')');

        return new PServiceCall<>(methodName, callType, sequence, message);
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    Message deserialize(InputStream input,
                        PStructDescriptor<Message, Field> descriptor)
            throws IOException, SerializerException {
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
                        PStructDescriptor<Message, Field> descriptor,
                        boolean requireEnd)
            throws IOException, SerializerException {
        PMessageBuilder<Message, Field> builder = descriptor.builder();

        for (;;) {
            Token t = tokenizer.next();
            if (requireEnd ? t != null && t.isSymbol(Token.kMessageEnd) : t == null) {
                break;
            }
            if (t == null) {
                throw new SerializerException("Unexpected end of stream");
            }
            if (!t.isIdentifier()) {
                throw new SerializerException("");
            }
            PField field = descriptor.getField(t.asString());
            if (field == null) {
                throw new SerializerException("No such field on " + descriptor.getQualifiedName(null) + ": " + t.asString());
            }

            tokenizer.expectSymbol("field value separator", Token.kKeyValueSep);

            if (field.getType() == PType.LIST) {
                // special handling for lists, repeated fields.
                t = tokenizer.peek("list field value");
                if (t.isSymbol(Token.kListStart)) {
                    // Handle as a list.
                    builder.set(field.getKey(), readFieldValue(tokenizer, field.getDescriptor()));
                } else {
                    // Handle as a repeated field.
                    builder.addTo(field.getKey(), readFieldValue(tokenizer, ((PList) field.getDescriptor()).itemDescriptor()));
                }
            } else {
                builder.set(field.getKey(), readFieldValue(tokenizer, field.getDescriptor()));
            }
            t = tokenizer.peek();
            if (t != null && (t.isSymbol(Token.kLineSep1) || t.isSymbol(Token.kLineSep2))) {
                tokenizer.next();
            }
        }
        return builder.build();
    }

    private Object readFieldValue(Tokenizer tokenizer, PDescriptor descriptor) throws IOException, SerializerException {
        switch (descriptor.getType()) {
            case BOOL: {
                Token t = tokenizer.expect("boolean value");
                switch (t.asString().toLowerCase()) {
                    case "1":
                    case "t":
                    case "true":
                    case "y":
                    case "yes":
                        return true;
                    case "0":
                    case "f":
                    case "false":
                    case "n":
                    case "no":
                        return false;
                }
                throw new SerializerException("Invalid boolean value " + t.asString());

            }
            case BYTE: {
                Token t = tokenizer.expect("byte value");
                if (t.isInteger()) {
                    long val = t.parseInteger();
                    if (val > Byte.MAX_VALUE || val < Byte.MIN_VALUE) {
                        throw new SerializerException("Byte value out of bounds: " + t.asString());
                    }
                    return (byte) val;
                } else {
                    throw new SerializerException("Invalid byte value: " + t.asString());
                }
            }
            case I16: {
                Token t = tokenizer.expect("byte value");
                if (t.isInteger()) {
                    long val = t.parseInteger();
                    if (val > Short.MAX_VALUE || val < Short.MIN_VALUE) {
                        throw new SerializerException("Short value out of bounds: " + t.asString());
                    }
                    return (short) val;
                } else {
                    throw new SerializerException("Invalid byte value: " + t.asString());
                }
            }
            case I32: {
                Token t = tokenizer.expect("byte value");
                if (t.isInteger()) {
                    long val = t.parseInteger();
                    if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
                        throw new SerializerException("Integer value out of bounds: " + t.asString());
                    }
                    return (int) val;
                } else {
                    throw new SerializerException("Invalid byte value: " + t.asString());
                }
            }
            case I64: {
                Token t = tokenizer.expect("byte value");
                if (t.isInteger()) {
                    return t.parseInteger();
                } else {
                    throw new SerializerException("Invalid byte value: " + t.asString());
                }
            }
            case DOUBLE: {
                Token t = tokenizer.expect("byte value");
                try {
                    return t.parseDouble();
                } catch (NumberFormatException nfe) {
                    throw new SerializerException(nfe, nfe.getMessage());
                }
            }
            case STRING: {
                Token t = tokenizer.expectStringLiteral("string value");
                return t.decodeLiteral();
            }
            case BINARY: {
                tokenizer.expectSymbol("binary value", Token.kListStart);
                return tokenizer.readBinaryUntil(Token.kListEnd);
            }
            case ENUM: {
                Token t = tokenizer.expectIdentifier("enum value");
                PEnumBuilder b = ((PEnumDescriptor) descriptor).builder();
                b.setByName(t.asString());
                if (!b.isValid()) {
                    throw new SerializerException("No such " + descriptor.getQualifiedName(null) + " value " + t.asString());
                }
                return b.build();
            }
            case MESSAGE: {
                tokenizer.expectSymbol("message start", Token.kMessageStart);
                return readMessage(tokenizer, (PStructDescriptor<?, ?>) descriptor, true);
            }
            case MAP: {
                @SuppressWarnings("unchecked")
                PMap<Object, Object> pMap = (PMap) descriptor;
                PDescriptor kDesc = pMap.keyDescriptor();
                PDescriptor iDesc = pMap.itemDescriptor();

                PMap.Builder<Object, Object> builder = pMap.builder();

                tokenizer.expectSymbol("map start", Token.kMessageStart);
                if (tokenizer.peek("map end or value").isSymbol(Token.kMessageEnd)) {
                    tokenizer.next();
                } else {
                    while (true) {
                        Object key = readFieldValue(tokenizer, kDesc);
                        tokenizer.expectSymbol("mep kv sep", Token.kKeyValueSep);
                        Object value = readFieldValue(tokenizer, iDesc);

                        builder.put(key, value);

                        Token t = tokenizer.peek("map sep, end or value");
                        if (t.isSymbol(Token.kLineSep1) || t.isSymbol(Token.kLineSep2)) {
                            tokenizer.next();
                        } else if (t.isSymbol(Token.kMessageEnd)) {
                            tokenizer.next();
                            break;
                        }
                    }
                }

                return builder.build();
            }
            case LIST: {
                @SuppressWarnings("unchecked")
                PList<Object> pList = (PList) descriptor;
                PDescriptor iDesc = pList.itemDescriptor();

                PList.Builder<Object> builder = pList.builder();

                tokenizer.expectSymbol("list start", Token.kListStart);
                if (tokenizer.peek("empty list").isSymbol(Token.kListEnd)) {
                    tokenizer.next();
                } else {
                    while (true) {
                        Object value = readFieldValue(tokenizer, iDesc);

                        builder.add(value);

                        Token t = tokenizer.peek("list sep, end or value");
                        if (t.isSymbol(Token.kLineSep1) || t.isSymbol(Token.kLineSep2)) {
                            tokenizer.next();
                        } else if (t.isSymbol(Token.kListEnd)) {
                            tokenizer.next();
                            break;
                        }
                    }
                }

                return builder.build();
            }
            case SET: {
                @SuppressWarnings("unchecked")
                PSet<Object> pList = (PSet) descriptor;
                PDescriptor iDesc = pList.itemDescriptor();

                PSet.Builder<Object> builder = pList.builder();

                tokenizer.expectSymbol("set start", Token.kListStart);
                if (tokenizer.peek("empty set").isSymbol(Token.kListEnd)) {
                    tokenizer.next();
                } else {
                    while (true) {
                        Object value = readFieldValue(tokenizer, iDesc);

                        builder.add(value);

                        Token t = tokenizer.peek("set sep, end or value");
                        if (t.isSymbol(Token.kLineSep1) || t.isSymbol(Token.kLineSep2)) {
                            tokenizer.next();
                        } else if (t.isSymbol(Token.kListEnd)) {
                            tokenizer.next();
                            break;
                        }
                    }
                }

                return builder.build();

            }
        }
        return null;
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
        PStructDescriptor<?, ?> type = message.descriptor();

        if (enclose) {
            builder.append("{")
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
                       .append(":")
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
                        builder.append(entrySep)
                               .appendln();
                    }
                    Object o = message.get(field.getKey());
                    if (field.getType() == PType.LIST && repeatedListEntries) {
                        @SuppressWarnings("unchecked")
                        PList<Object> list = (PList<Object>) field.getDescriptor();
                        @SuppressWarnings("unchecked")
                        Collection<Object> coll = (Collection<Object>) o;

                        boolean firstItem = true;
                        for (Object v : coll) {
                            if (firstItem) {
                                firstItem = false;
                            } else {
                                builder.appendln();
                            }

                            builder.append(field.getName())
                                   .append(":")
                                   .append(space);
                            appendTypedValue(builder, list.itemDescriptor(), v);
                        }
                    } else {
                        builder.append(field.getName())
                               .append(":")
                               .append(space);
                        appendTypedValue(builder, field.getDescriptor(), o);
                    }
                }
            }
        }

        if (enclose) {
            builder.end()
                   .appendln("}");
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
                    writer.append("[");

                    boolean first = true;
                    for (Object i : collection) {
                        if (first) {
                            first = false;
                        } else {
                            // Lists are always comma-delimited
                            writer.append(',')
                                  .append(space);
                        }
                        appendTypedValue(writer, containerType.itemDescriptor(), i);
                    }
                    writer.append("]");
                } else {
                    writer.append("[")
                          .begin();

                    boolean first = true;
                    for (Object i : collection) {
                        if (first) {
                            first = false;
                        } else {
                            // Lists are always comma-delimited
                            writer.append(',');
                        }
                        writer.appendln();
                        appendTypedValue(writer, containerType.itemDescriptor(), i);
                    }

                    writer.end()
                          .appendln("]");
                }
                break;
            }
            case MAP: {
                PMap<?, ?> mapType = (PMap<?, ?>) descriptor;

                Map<?, ?> map = (Map<?, ?>) o;

                writer.append("{")
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
                    writer.append(":")
                          .append(space);
                    appendTypedValue(writer, mapType.itemDescriptor(), entry.getValue());
                }

                writer.end()
                      .appendln("}");
                break;
            }
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
            writer.print('\"');
            writer.print(Strings.escape((CharSequence) o));
            writer.print('\"');
        } else if (o instanceof Binary) {
            Binary b = (Binary) o;
            writer.append('[')
                  .append(b.toBase64())
                  .append(']');
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
                // Scientific notation should be used.
                writer.print((new DecimalFormat("0.#########E0")).format(d.doubleValue()));
            } else {
                writer.print(d.doubleValue());
            }
        } else {
            throw new IllegalArgumentException("Unknown primitive type class " + o.getClass()
                                                                                  .getSimpleName());
        }
    }
}
