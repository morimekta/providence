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

import net.morimekta.util.Slice;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

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

    public Token expect(String message) throws IOException, TokenizerException {
        if (!hasNext()) {
            throw new TokenizerException("Expected %s, got end of file", message);
        }
        Token next = nextToken;
        nextToken = null;
        return next;
    }

    public Token peek(String message) throws IOException, TokenizerException {
        if (!hasNext()) {
            throw new TokenizerException("Expected %s, got end of file", message);
        }
        return nextToken;
    }

    public Token peek() throws IOException, TokenizerException {
        hasNext();
        return nextToken;
    }

    public char expectSymbol(String message, char... symbols) throws IOException, TokenizerException {
        if (!hasNext()) {
            throw new TokenizerException("Expected %s, got end of file", message);
        } else {
            for (char symbol : symbols) {
                if (nextToken.isSymbol(symbol)) {
                    nextToken = null;
                    return symbol;
                }
            }

            throw new TokenizerException(nextToken,
                                         "Expected %s, but found '%s'",
                                         message,
                                         Strings.escape(nextToken.asString()))
                    .setLine(getLine(nextToken.getLineNo()));
        }
    }

    public Token expectIdentifier(String message) throws IOException, TokenizerException {
        if (!hasNext()) {
            throw new TokenizerException("Expected %s, got end of file", message);
        } else if (nextToken.isIdentifier()) {
            Token next = nextToken;
            nextToken = null;
            return next;
        } else {
            throw new TokenizerException(nextToken,
                                         "Expected %s, but got '%s'",
                                         message,
                                         Strings.escape(nextToken.asString()))
                    .setLine(getLine(nextToken.getLineNo()));
        }
    }

    public Token expectStringLiteral(String message) throws IOException, TokenizerException {
        if (!hasNext()) {
            throw new TokenizerException("Expected %s, got end of file", message);
        } else if (nextToken.isStringLiteral()) {
            Token next = nextToken;
            nextToken = null;
            return next;
        } else {
            throw new TokenizerException(nextToken,
                                         "Expected %s, but got '%s'",
                                         message,
                                         Strings.escape(nextToken.asString()))
                    .setLine(getLine(nextToken.getLineNo()));
        }
    }

    public boolean hasNext() throws IOException, TokenizerException {
        if (nextToken == null) {
            nextToken = nextInternal();
        }
        return nextToken != null;
    }

    public Token next() throws IOException, TokenizerException {
        if (nextToken != null) {
            Token tmp = nextToken;
            nextToken = null;
            return tmp;
        }

        return nextInternal();
    }

    private Token nextStringLiteral(int startQuote) throws TokenizerException {
        int startOffset = readOffset;
        int startLinePos = linePos;
        boolean escaped = false;
        while (true) {
            int r = read();
            if (r < 0x20 || r == 0x7F) {
                int pos = startOffset - readOffset;
                if (r == -1) {
                    throw new TokenizerException("Unexpected end of stream in literal.")
                            .setLineNo(lineNo)
                            .setLinePos(startLinePos + pos)
                            .setLine(getLine(lineNo));
                } else {
                    throw new TokenizerException("Invalid string literal char: %d", r)
                            .setLineNo(lineNo)
                            .setLinePos(startLinePos + pos)
                            .setLine(getLine(nextToken.getLineNo()));
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

    private Token nextInternal() throws IOException, TokenizerException {
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

        throw new TokenizerException("Unknown token initiator '%c'", r)
                .setLineNo(lineNo)
                .setLinePos(linePos)
                .setLine(getLine(lineNo));
    }

    private Token nextNumber(int lastByte) throws TokenizerException {
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
            if (lastByte < 0) {
                throw new TokenizerException("Unexpected end of stream after negative indicator")
                        .setLineNo(lineNo)
                        .setLinePos(startLinePos + len)
                        .setLine(getLine(lineNo));
            }
            ++len;
            if (!(lastByte == '.' || (lastByte >= '0' && lastByte <= '9'))) {
                throw new TokenizerException("No decimal after negative indicator")
                        .setLineNo(lineNo)
                        .setLinePos(startLinePos + len)
                        .setLine(getLine(lineNo));
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
            throw new TokenizerException("Invalid termination of number: '%c'", escapeChar(lastByte))
                    .setLineNo(lineNo)
                    .setLinePos(startLinePos + len)
                    .setLine(getLine(lineNo));
        }
    }

    private Token nextIdentifier() throws TokenizerException {
        int startOffset = readOffset;
        int startLinePos = linePos;

        int len = 1, r;
        boolean dot = false;
        while ((r = read()) != -1) {
            if (r == '.') {
                if (dot) {
                    throw new TokenizerException("Identifier with double '.'")
                            .setLineNo(lineNo)
                            .setLinePos(startLinePos + len)
                            .setLine(getLine(lineNo));
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
            throw new TokenizerException("Identifier trailing with '.'")
                    .setLineNo(lineNo)
                    .setLinePos(linePos)
                    .setLine(getLine(lineNo));
        }

        if (r == -1 || r == ' ' || r == '\t' || r == '\n' || r == '\r' || Token.kSymbols.indexOf(r) >= 0) {
            return token;
        } else {
            throw new TokenizerException("Wrongly terminated identifier: %s", escapeChar(r))
                    .setLineNo(lineNo)
                    .setLinePos(linePos)
                    .setLine(getLine(lineNo));
        }
    }

    public String getLine(final int theLine) {
        if (theLine < 1) {
            throw new IllegalStateException(theLine + " is not a valid line number. Must be 1 .. N");
        }
        // reset read position.
        readOffset = -1;
        lineNo = 1;
        linePos = -1;

        try {
            int line = theLine;
            while (--line > 0) {
                if (!IOUtils.skipUntil(this, (byte) '\n')) {
                    throw new IOException("No such line " + theLine);
                }
            }
            return IOUtils.readString(this, "\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String readUntil(char end, boolean allowSpaces, boolean allowNewlines) throws TokenizerException {
        int startOffset = readOffset + 1;
        int startLinePos = linePos;
        int startLineNo = lineNo;

        int r;
        while ((r = read()) != -1) {
            if (r == end) {
                return new Slice(buffer, startOffset, readOffset - startOffset).asString();
            } else if (r == ' ' || r == '\t') {
                if (!allowSpaces) {
                    throw new TokenizerException("Illegal char '%s' in binary", escapeChar(r))
                            .setLineNo(lineNo)
                            .setLinePos(linePos)
                            .setLine(getLine(lineNo));
                }
            } else if (r == '\n' || r == '\r') {
                if (!allowNewlines) {
                    throw new TokenizerException("Illegal char '%s' in binary", escapeChar(r))
                            .setLineNo(lineNo)
                            .setLinePos(linePos)
                            .setLine(getLine(lineNo));
                }
            }
        }

        // throw with the old
        throw new TokenizerException("unexpected end of stream in binary")
                .setLineNo(startLineNo)
                .setLinePos(startLinePos);
    }

    private static String escapeChar(int c) {
        return Strings.escape(new String(new char[]{(char) c}));
    }
}
