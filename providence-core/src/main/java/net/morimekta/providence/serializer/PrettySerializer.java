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
import net.morimekta.util.Binary;
import net.morimekta.util.Slice;
import net.morimekta.util.Strings;
import net.morimekta.util.io.CountingOutputStream;
import net.morimekta.util.io.IOUtils;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

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
    private final boolean encloseOuther;
    private final boolean repeatedListEntries;

    public PrettySerializer() {
        this(INDENT, SPACE, NEWLINE, LIST_SEP, true, false);
    }

    public PrettySerializer(String indent, String space, String newline, String entrySep, boolean encloseOuther, boolean repeatedListEntries) {
        this.indent = indent;
        this.space = space;
        this.newline = newline;
        this.entrySep = entrySep;
        this.encloseOuther = encloseOuther;
        this.repeatedListEntries = repeatedListEntries;
    }

    public <T extends PMessage<T>> int serialize(OutputStream out, T message) {
        CountingOutputStream cout = new CountingOutputStream(out);
        IndentedPrintWriter builder = new IndentedPrintWriter(cout, indent, newline);
        appendMessage(builder, message, encloseOuther);
        builder.flush();
        return cout.getByteCount();
    }

    @Override
    public <T extends PMessage<T>> int serialize(OutputStream out, PServiceCall<T> call)
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
    public <T extends PMessage<T>> PServiceCall<T> deserialize(InputStream input, PService service)
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

        T message;
        switch (callType) {
            case CALL:
            case ONEWAY:
                message = (T) readMessage(tokenizer, method.getRequestType(), true);
                break;
            case REPLY:
                message = (T) readMessage(tokenizer, method.getResponseType(), true);
                break;
            case EXCEPTION:
                message = (T) readMessage(tokenizer, ApplicationException.kDescriptor, true);
                break;
            default:
                throw new IllegalStateException("Unreachable code reached");
        }

        tokenizer.expectSymbol("Call params closing", ')');

        return new PServiceCall<>(methodName, callType, sequence, message);
    }

    @Override
    public <T extends PMessage<T>, TF extends PField> T deserialize(InputStream input,
                                                                    PStructDescriptor<T, TF> descriptor)
            throws IOException, SerializerException {
        Tokenizer tokenizer = new Tokenizer(input, encloseOuther);
        Token first = tokenizer.peek();
        if (first != null && first.isSymbol(Token.kMessageStart)) {
            tokenizer.next();
            return readMessage(tokenizer, descriptor, true);
        } else {
            return readMessage(tokenizer, descriptor, false);
        }
    }

    private <T extends PMessage<T>, TF extends PField> T readMessage(Tokenizer tokenizer, PStructDescriptor<T, TF> descriptor, boolean requireEnd)
            throws IOException, SerializerException {
        PMessageBuilder<T> builder = descriptor.builder();

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

    private void appendMessage(IndentedPrintWriter builder, PMessage<?> message, boolean enclose) {
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
                PMessage<?> message = (PMessage<?>) o;
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

    private static class Token extends Slice {
        // Various symbols.
        public static final char kMessageStart = '{';
        public static final char kMessageEnd   = '}';
        public static final char kKeyValueSep  = ':';

        public static final char kLineSep1 = ',';
        public static final char kLineSep2 = ';';

        // Not really 'symbols'.
        public static final char kLiteralEscape      = '\\';
        public static final char kLiteralQuote       = '\'';
        public static final char kLiteralDoubleQuote = '\"';
        public static final char kListStart  = '[';
        public static final char kListEnd    = ']';
        public static final char kShellComment       = '#';

        public static final String kSymbols = "{}:=()<>,;#[]";

        private static final Pattern RE_IDENTIFIER           = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");
        private static final Pattern RE_INTEGER              = Pattern.compile("-?(0|[1-9][0-9]*|0[0-7]+|0x[0-9a-fA-F]+)");

        private final int lineNo;
        private final int linePos;

        public Token(byte[] fb, int off, int len, int lineNo, int linePos) {
            super(fb, off, len);
            this.lineNo = lineNo;
            this.linePos = linePos;
        }

        public boolean isSymbol(char symbol) {
            return len == 1 && fb[off] == symbol;
        }

        public boolean isStringLiteral() {
            return (length() > 1 &&
                    charAt(0) == '\"' &&
                    charAt(-1) == '\"');
        }

        public boolean isIdentifier() {
            return RE_IDENTIFIER.matcher(asString())
                                .matches();
        }

        public boolean isInteger() {
            return RE_INTEGER.matcher(asString())
                             .matches();
        }

        /**
         * Get the whole slice as a string.
         *
         * @return Slice decoded as UTF_8 string.
         */
        public String decodeLiteral() {
            // This decodes the string from UTF_8 bytes.
            String tmp = substring(1, -1).asString();
            final int l = tmp.length();
            StringBuilder out = new StringBuilder(l);

            boolean esc = false;
            for (int i = 0; i < l; ++i) {
                if (esc) {
                    esc = false;

                    char ch = tmp.charAt(i);
                    switch (ch) {
                        case 'b':
                            out.append('\b');
                            break;
                        case 'f':
                            out.append('\f');
                            break;
                        case 'n':
                            out.append('\n');
                            break;
                        case 'r':
                            out.append('\r');
                            break;
                        case 't':
                            out.append('\t');
                            break;
                        case '\"':
                        case '\'':
                        case '\\':
                            out.append(ch);
                            break;
                        case 'u':
                            if (l < i + 5) {
                                out.append('?');
                            } else {
                                String n = tmp.substring(i + 1, i + 5);
                                try {
                                    int cp = Integer.parseInt(n, 16);
                                    out.append((char) cp);
                                } catch (NumberFormatException e) {
                                    out.append('?');
                                }
                            }
                            i += 4;  // skipping 4 more characters.
                            break;
                        case '0':
                        case '1':
                            if (l < (i + 3)) {
                                out.append('?');
                            } else {
                                String n = tmp.substring(i, i + 2);
                                try {
                                    int cp = Integer.parseInt(n, 8);
                                    out.append((char) cp);
                                } catch (NumberFormatException e) {
                                    out.append('?');
                                }
                            }
                            i += 2;  // skipping 2 more characters.
                            break;
                        default:
                            out.append('?');
                            break;
                    }
                } else if (tmp.charAt(i) == '\\') {
                    esc = true;
                } else {
                    out.append(tmp.charAt(i));
                }
            }
            return out.toString();
        }

        @Override
        public String toString() {
            return String.format("Token('%s',%d:%d-%d)", asString(), lineNo, linePos, linePos + len);
        }
    }

    public static class Tokenizer extends InputStream {
        private final byte[] buffer;
        private       int    readOffset;
        private       int    lineNo;
        private       int    linePos;
        private       Token  nextToken;

        public Tokenizer(InputStream in, boolean enclosedContent) throws IOException {
            ByteArrayOutputStream tmp = new ByteArrayOutputStream();
            if (enclosedContent) {
                int stack = 0;
                char literal = '\0';
                boolean escaped = false;
                boolean comment = false;

                int r;
                while((r = in.read()) >= 0) {
                    if (comment) {
                        if (r == '\n' || r == '\r') {
                            tmp.write(r);
                            comment = false;
                        }
                    } else {
                        if (literal != '\0') {
                            if (escaped) {
                                escaped = false;
                            } else if (r == literal) {
                                literal = '\0';
                                escaped = false;
                            } else if (r == '\\'){
                                escaped = true;
                            }
                        } else if (r == ' ' || r == '\t' || r == '\r' || r == '\n') {
                            // just continue.
                        } else if (r == '\"' || r == '\'') {
                            literal = (char) r;
                        } else if (r == '#') {
                            comment = true;
                            continue;  // do not write char.
                        } else if (r == '}') {
                            if (--stack <= 0) {
                                tmp.write(r);
                                break;
                            }
                        } else if (r == '{') {
                            ++stack;
                        } else if (stack == 0){
                            // This means there is a "meaningful" symbol
                            // before the first { character. This means we are
                            // actually in non-enclosed mode. Setting the max
                            // stack to 1, so that the read will not stop until
                            // end or file, or something invalid has occurred.
                            stack = 1;
                        }

                        tmp.write(r);
                    }
                }
            } else {
                IOUtils.copy(in, tmp);
            }

            this.buffer = tmp.toByteArray();
            this.readOffset = -1;

            this.lineNo = 1;
            this.linePos = -1;
        }

        @Override
        public int read() {
            if (++readOffset >= buffer.length) {
                readOffset = buffer.length;
                return -1;
            }
            int ret = buffer[readOffset];
            if (ret == '\n') {
                ++lineNo;
                linePos = -1;
            } else {
                ++linePos;
            }
            return ret > 0 ? ret : 0x100 + ret;
        }

        /**
         * "Unread" the last read byte. Note that line-pos is not usable
         * immediately after reading, until reading again.
         */
        private void unread() {
            if (readOffset == buffer.length) {
                --readOffset;
                return;
            }

            if (buffer[readOffset--] == '\n') {
                --lineNo;
            } else {
                --linePos;
            }
        }

        public Token expect(String message) throws IOException, SerializerException {
            if (!hasNext()) {
                throw new SerializerException("Unexpected end of file, while %s", message);
            }
            Token next = nextToken;
            nextToken = null;
            return next;
        }

        public Token peek(String message) throws IOException, SerializerException {
            if (!hasNext()) {
                throw new SerializerException("Unexpected end of file, while %s", message);
            }
            return nextToken;
        }

        public Token peek() throws IOException, SerializerException {
            hasNext();
            return nextToken;
        }

        public char expectSymbol(String message, char... symbols) throws IOException, SerializerException {
            if (!hasNext()) {
                throw new SerializerException("Unexpected end of file, expected one of ['%s'] while %s",
                                              Strings.escape(Strings.join("', '", symbols)),
                                              message);
            } else {
                for (char symbol : symbols) {
                    if (nextToken.isSymbol(symbol)) {
                        nextToken = null;
                        return symbol;
                    }
                }

                throw new SerializerException("Expected one of ['%s'], but found '%s' while %s",
                                              Strings.escape(Strings.join("', '", symbols)),
                                              Strings.escape(nextToken.asString()),
                                              message);
            }
        }

        public Token expectIdentifier(String message) throws IOException, SerializerException {
            if (!hasNext()) {
                throw new SerializerException("Unexpected end of file, while %s", message);
            } else if (nextToken.isIdentifier()) {
                Token next = nextToken;
                nextToken = null;
                return next;
            } else {
                throw new SerializerException("Expected identifier, but found '%s' while %s",
                                              Strings.escape(nextToken.asString()),
                                              message);
            }
        }

        public Token expectStringLiteral(String message) throws IOException, SerializerException {
            if (!hasNext()) {
                throw new SerializerException("Unexpected end of file, while %s", message);
            } else if (nextToken.isStringLiteral()) {
                Token next = nextToken;
                nextToken = null;
                return next;
            } else {
                throw new SerializerException("Expected string literal, but found '%s' while %s",
                                              Strings.escape(nextToken.asString()),
                                              message);
            }
        }

        public boolean hasNext() throws IOException, SerializerException {
            if (nextToken == null) {
                nextToken = nextInternal();
            }
            return nextToken != null;
        }

        public Token next() throws IOException, SerializerException {
            if (nextToken != null) {
                Token tmp = nextToken;
                nextToken = null;
                return tmp;
            }

            return nextInternal();
        }

        private Token nextStringLiteral(int startQuote) throws SerializerException {
            int startOffset = readOffset;
            int startLinePos = linePos;
            boolean escaped = false;
            while (true) {
                int r = read();
                if (r < 0x20 || r == 0x7F) {
                    int pos = startOffset - readOffset;
                    if (r == -1) {
                        throw new SerializerException(
                                "Unexpected end of stream in string: line" + lineNo + " pos " + startLinePos + pos);
                    } else {
                        throw new SerializerException(
                                "Invalid string literal char: " + r + " at line " + lineNo + " pos " + startLinePos +
                                pos);
                    }
                }

                if (escaped) {
                    escaped = false;
                } else if (r == Token.kLiteralEscape) {
                    escaped = true;
                } else if (startQuote == r) {
                    break;
                }
                // else just include into string token.
            }
            return new Token(buffer, startOffset, readOffset - startOffset + 1, lineNo, startLinePos);
        }

        private Token nextInternal() throws IOException, SerializerException {
            int startOffset = readOffset;
            int r;
            while ((r = read()) != -1) {
                if (r != ' ' && r != '\t' && r != '\r' && r != '\n') {
                    if (r == Token.kShellComment) {
                        while ((r = read()) != -1) {
                            if (r == '\n' || r == '\r') {
                                break;
                            }
                        }
                    } else {
                        startOffset = readOffset;
                        break;
                    }
                }
            }

            if (r < 0) {
                return null;
            }

            // Known symbols.
            if (Token.kSymbols.indexOf(r) >= 0) {
                return new Token(buffer, startOffset, 1, lineNo, linePos);
            }

            // String literals.
            if (r == Token.kLiteralQuote || r == Token.kLiteralDoubleQuote) {
                return nextStringLiteral(r);
            }

            // Number.
            if (r == '.' || r == '-' || (r >= '0' && r <= '9')) {
                return nextNumber(r);
            }

            // Identifier / qualified identifier / type name token.
            if (r == '_' ||
                (r >= 'a' && r <= 'z') ||
                (r >= 'A' && r <= 'Z')) {
                return nextIdentifier();
            }

            throw new SerializerException(String.format("Unknown token initiator: %c, line %d, pos %d",
                                                        r,
                                                        lineNo,
                                                        linePos));
        }

        private Token nextNumber(int lastByte) throws SerializerException {
            // NOTE: This code is pretty messy because it is a full state-engine
            // to ensure that the parsed number follows the JSON number syntax.
            // Alternatives are:
            //
            // dec = -?0
            // dec = -?.0
            // dec = -?0.0
            // sci = (dec)[eE][+-]?[0-9]+
            // hex = 0x[0-9a-fA-F]+
            //
            // Octal and hexadecimal numbers are not supported.
            //
            // It is programmed as a state-engine to be very efficient, but
            // correctly detect valid JSON (and what is invalid if not).

            int startLinePos = linePos;
            int startOffset = readOffset;
            int len = 0;

            if (lastByte == '-') {
                lastByte = read();
                ++len;
                if (lastByte < 0) {
                    throw new SerializerException("Unexpected end of stream on line " + lineNo);
                }

                if (!(lastByte == '.' || (lastByte >= '0' && lastByte <= '9'))) {
                    throw new SerializerException("No decimal after negative indicator.");
                }
            } else if (lastByte == '0') {
                lastByte = read();
                ++len;
                if (lastByte == 'x') {
                    // hexadecimal.
                    while ((lastByte = read()) != -1) {
                        if ((lastByte >= '0' && lastByte <= '9') ||
                            (lastByte >= 'a' && lastByte <= 'f') ||
                            (lastByte >= 'A' && lastByte <= 'F')) {
                            ++len;
                            continue;
                        }
                        // we read a char that's *not* part of the
                        unread();
                        break;
                    }

                    return new Token(buffer, startOffset, len, lineNo, startLinePos);
                }

                // Octal
                while ((lastByte = read()) != -1) {
                    if ((lastByte >= '0' && lastByte <= '7')) {
                        ++len;
                        continue;
                    }
                    // we read a char that's *not* part of the
                    unread();
                    break;
                }

                return new Token(buffer, startOffset, len, lineNo, startLinePos);
            }

            // decimal part.
            while (lastByte >= '0' && lastByte <= '9') {
                ++len;
                // numbers are terminated by first non-numeric character.
                lastByte = read();
                if (lastByte < 0) {
                    break;
                }
            }
            // fraction part.
            if (lastByte == '.') {
                ++len;
                // numbers are terminated by first non-numeric character.
                lastByte = read();
                if (lastByte >= 0) {
                    while (lastByte >= '0' && lastByte <= '9') {
                        ++len;
                        // numbers are terminated by first non-numeric character.
                        lastByte = read();
                        if (lastByte < 0) {
                            break;
                        }
                    }
                }
            }
            // exponent part.
            if (lastByte == 'e' || lastByte == 'E') {
                ++len;
                // numbers are terminated by first non-numeric character.
                lastByte = read();
                if (lastByte >= 0) {
                    // The exponent can be explicitly prefixed with both '+'
                    // and '-'.
                    if (lastByte == '-' || lastByte == '+') {
                        ++len;
                        // numbers are terminated by first non-numeric character.
                        lastByte = read();
                    }

                    while (lastByte >= '0' && lastByte <= '9') {
                        ++len;
                        // numbers are terminated by first non-numeric character.
                        lastByte = read();
                        if (lastByte < 0) {
                            break;
                        }
                    }
                }
            }
            Token token = new Token(buffer, startOffset, len, lineNo, startLinePos);

            // A number must be terminated correctly: End of stream, space, newline
            // or a symbol that may be after a value: ':', ',' ';' '}' ')'.
            if (lastByte < 0 ||
                lastByte == ' ' ||
                lastByte == '\t' ||
                lastByte == '\n' ||
                lastByte == '\r' ||
                lastByte == Token.kKeyValueSep ||
                lastByte == Token.kMessageEnd ||
                lastByte == Token.kListEnd ||
                lastByte == Token.kLineSep1 ||
                lastByte == Token.kLineSep2 ||
                lastByte == Token.kShellComment) {
                if (Token.kSymbols.indexOf(lastByte) >= 0) {
                    unread();
                }
                return token;
            } else {
                throw new SerializerException("Wrongly terminated number: %c.", (char) lastByte);
            }
        }

        private Token nextIdentifier() throws SerializerException {
            int startOffset = readOffset;
            int startLinePos = linePos;

            int len = 1, r;
            boolean dot = false;
            while ((r = read()) != -1) {
                if (r == '.') {
                    if (dot) {
                        throw new SerializerException("Identifier with double '..' at line %d pos %d",
                                                      lineNo,
                                                      startLinePos);
                    }
                    dot = true;
                    ++len;
                    continue;
                }
                dot = false;

                if (r == '_' ||
                    (r >= '0' && r <= '9') ||
                    (r >= 'a' && r <= 'z') ||
                    (r >= 'A' && r <= 'Z')) {
                    ++len;
                    continue;
                }

                unread();
                break;
            }
            Token token = new Token(buffer, startOffset, len, lineNo, startLinePos);

            if (dot) {
                throw new SerializerException("Identifier trailing with '.' at line %d pos &d", lineNo, startLinePos);
            }

            if (r == -1 ||
                r == ' ' ||
                r == '\t' ||
                r == '\n' ||
                r == '\r' ||
                r == Token.kKeyValueSep ||
                r == Token.kMessageEnd ||
                r == Token.kListEnd ||
                r == Token.kLineSep1 ||
                r == Token.kLineSep2 ||
                r == Token.kShellComment ||
                Token.kSymbols.indexOf(r) >= 0) {
                return token;
            } else {
                throw new SerializerException("Wrongly terminated identifier: %c.", (char) r);
            }
        }

        public String getLine(int line) throws IOException {
            if (line < 1) {
                throw new IllegalArgumentException("Oops!!!");
            }
            // reset read position.
            readOffset = -1;
            lineNo = 1;
            linePos = -1;

            while (--line > 0) {
                if (!IOUtils.skipUntil(this, (byte) '\n')) {
                    throw new IOException("Oops");
                }
            }
            return IOUtils.readString(this, "\n");
        }

        public Binary readBinaryUntil(char end) throws SerializerException {
            int startOffset = readOffset + 1;
            int startLinePos = linePos;

            int r;
            while ((r = read()) != -1) {
                if (r == end) {
                    return Binary.fromBase64(new Slice(buffer, startOffset, readOffset - startOffset).asString());
                } else if (r == ' ' || r == '\n' || r == '\r' || r == '\t') {
                    throw new SerializerException("Illegal char in binary");
                }
            }

            throw new SerializerException("unexpected end of binary data on line " + startLinePos);
        }
    }
}
