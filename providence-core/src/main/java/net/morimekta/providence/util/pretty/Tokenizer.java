/*
 * Copyright (c) 2016, Stein Eldar Johnsen
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
package net.morimekta.providence.util.pretty;

import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.Binary;
import net.morimekta.util.Slice;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple tokenizer for the pretty serializer that strips away comments based
 * on the "#" (shell) comment character. Each comment lasts until the next
 * newline.
 */
public class Tokenizer extends InputStream {
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
            while ((r = in.read()) >= 0) {
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
                        } else if (r == '\\') {
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
                    } else if (stack == 0) {
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
        if (r == '_' || (r >= 'a' && r <= 'z') || (r >= 'A' && r <= 'Z')) {
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
                    if ((lastByte >= '0' && lastByte <= '9') || (lastByte >= 'a' && lastByte <= 'f') ||
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
        if (lastByte < 0 || lastByte == ' ' || lastByte == '\t' || lastByte == '\n' || lastByte == '\r' ||
            lastByte == Token.kKeyValueSep || lastByte == Token.kMessageEnd || lastByte == Token.kListEnd ||
            lastByte == Token.kLineSep1 || lastByte == Token.kLineSep2 || lastByte == Token.kShellComment) {
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

            if (r == '_' || (r >= '0' && r <= '9') || (r >= 'a' && r <= 'z') || (r >= 'A' && r <= 'Z')) {
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

        if (r == -1 || r == ' ' || r == '\t' || r == '\n' || r == '\r' || r == Token.kKeyValueSep ||
            r == Token.kMessageEnd || r == Token.kListEnd || r == Token.kLineSep1 || r == Token.kLineSep2 ||
            r == Token.kShellComment || Token.kSymbols.indexOf(r) >= 0) {
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
