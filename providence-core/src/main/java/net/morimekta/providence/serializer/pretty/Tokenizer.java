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
package net.morimekta.providence.serializer.pretty;

import net.morimekta.util.CharSlice;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UncheckedIOException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Simple tokenizer for the pretty serializer that strips away comments based
 * on the "#" (shell) comment character. Each comment lasts until the next
 * newline.
 */
public class Tokenizer extends Reader {

    @FunctionalInterface
    public interface TokenValidator {
        boolean validate(Token token);
    }

    private final char[] buffer;
    protected     int    readOffset;
    protected     int    lineNo;
    protected     int    linePos;

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
                tmp.write(r);
                if (comment) {
                    if (r == '\n' || r == '\r') {
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
                    } else if (r == '}') {
                        if (--stack <= 0) {
                            break;
                        }
                    } else if (r == '{') {
                        ++stack;
                    }
                }
            }

            // then read until end of the line.
            while ((r = in.read()) >= 0) {
                tmp.write(r);
                if (r == '\n') {
                    break;
                }
            }
        } else {
            IOUtils.copy(in, tmp);
        }

        this.buffer = new String(tmp.toByteArray(), UTF_8).toCharArray();
        this.readOffset = -1;

        this.lineNo = 1;
        this.linePos = 0;
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
            linePos = 0;
        } else {
            ++linePos;
        }
        return ret > 0 ? ret : 0x100 + ret;
    }

    @Override
    public int read(char[] chars, int i, int i1) throws IOException {
        return 0;
    }

    @Override
    public void close() throws IOException {
        // ignore.
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

    /**
     * Expect at a valid token containing anything.
     *
     * @param expected The expectation description.
     * @return The token.
     * @throws IOException If failed to read a token.
     */
    public Token expect(@Nonnull String expected) throws IOException {
        if (!hasNext()) {
            throw failure("Expected %s, got end of file", expected);
        }
        Token next = nextToken;
        nextToken = null;
        return next;
    }

    public Token expect(@Nonnull String expected,
                        @Nonnull TokenValidator validator) throws IOException {
        if (!hasNext()) {
            throw failure("Expected %s, got end of file", expected);
        } else if (validator.validate(nextToken)) {
            Token next = nextToken;
            nextToken = null;
            return next;
        }
        throw failure(nextToken,
                      "Expected %s, but got '%s'",
                      expected,
                      Strings.escape(nextToken.asString()));
    }

    public Token peek(@Nonnull String expected) throws IOException {
        if (!hasNext()) {
            throw failure("Expected %s, got end of file", expected);
        }
        return nextToken;
    }

    public Token peek() throws IOException {
        hasNext();
        return nextToken;
    }

    public char expectSymbol(@Nonnull String expected, char... symbols) throws IOException {
        if (!hasNext()) {
            throw failure("Expected %s, one of ['%s'], got end of file",
                                         expected,
                                         Strings.joinP("', '", symbols));
        } else {
            for (char symbol : symbols) {
                if (nextToken.isSymbol(symbol)) {
                    nextToken = null;
                    return symbol;
                }
            }

            throw failure(nextToken,
                          "Expected %s, one of ['%s'], but found '%s'",
                          expected,
                          Strings.joinP("', '", symbols),
                          Strings.escape(nextToken.asString()));
        }
    }

    public Token expectIdentifier(@Nonnull String message) throws IOException {
        return expect(message, Token::isIdentifier);
    }

    @Nonnull
    public Token expectInteger(String message) throws IOException {
        return expect(message, Token::isInteger);
    }

    @Nonnull
    public Token expectLiteral(String message) throws IOException {
        return expect(message, Token::isStringLiteral);
    }

    public boolean hasNext() throws IOException {
        if (nextToken == null) {
            nextToken = nextInternal();
        }
        return nextToken != null;
    }

    public Token next() throws IOException {
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
        int startLineNo = lineNo;
        boolean escaped = false;
        while (true) {
            int r = read();
            if (r < 0x20 || r == 0x7F) {
                int length = readOffset - startOffset;
                if (r == -1) {
                    throw failure(startLineNo, startLinePos, length,
                                  "Unexpected end of stream in literal");
                } else if (r == '\n' || r == '\r') {
                    throw failure(startLineNo, startLinePos, length - 1,
                                  "Unexpected line break in literal");
                } else {
                    throw failure(startLineNo, startLinePos, length + 1,
                                  "Unescaped non-printable char in literal: '%s'",
                                  escapeChar(r));
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
        return token(startOffset, readOffset - startOffset + 1, startLinePos);
    }

    private Token nextInternal() throws IOException {
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
                    break;
                }
            }
        }

        if (r < 0) {
            return null;
        }

        // Known symbols.
        if (Token.kSymbols.indexOf(r) >= 0) {
            return nextSymbol(r);
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

        throw failure(lineNo, linePos, 1, "Unknown token initiator '%c'", r);
    }

    protected Token nextSymbol(int lastByte) throws TokenizerException {
        return token(readOffset, 1, linePos);
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
        int startLineNo = lineNo;
        int startOffset = readOffset;
        int len = 0;

        if (lastByte == '-') {
            lastByte = read();
            ++len;
            if (lastByte < 0) {
                throw failure(startLineNo, startLinePos, len,
                              "Unexpected end of stream after negative indicator");
            }
            if (!(lastByte == '.' || (lastByte >= '0' && lastByte <= '9'))) {
                throw failure(startLineNo, startLinePos, len,
                              "No decimal after negative indicator");
            }
        } else if (lastByte == '0') {
            lastByte = read();
            ++len;
            if (lastByte == 'x') {
                ++len;
                // hexadecimal.
                while ((lastByte = read()) != -1) {
                    if ((lastByte >= '0' && lastByte <= '9') || (lastByte >= 'a' && lastByte <= 'f') ||
                        (lastByte >= 'A' && lastByte <= 'F')) {
                        ++len;
                        continue;
                    }
                    // we read a char that's *not* part of the
                    break;
                }

                return validateAfterNumber(lastByte, startOffset, startLinePos, len);
            } else if ('0' <= lastByte && lastByte <= '7') {
                ++len;
                // Octals have 0 in front, and then more digits.
                while ((lastByte = read()) != -1) {
                    if ((lastByte < '0' || lastByte > '7')) {
                        ++len;
                        break;
                    }
                }

                return validateAfterNumber(lastByte, startOffset, startLinePos, len);
            }

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

                if (lastByte < '0' || '9' < lastByte) {
                    throw failure(startLineNo, startLinePos, len + 1, "Missing exponent value");
                }

                while (lastByte >= '0' && lastByte <= '9') {
                    ++len;
                    // numbers are terminated by first non-numeric character.
                    lastByte = read();
                    if (lastByte < 0) {
                        break;
                    }
                }
            } else {
                throw failure(startLineNo, startLinePos, len,
                              "Unexpected end of stream after exponent indicator");
            }
        }

        return validateAfterNumber(lastByte, startOffset, startLinePos, len);
    }

    private Token validateAfterNumber(int lastByte, int startOffset, int startLinePos, int len)
            throws TokenizerException {
        // A number must be terminated correctly: End of stream, space, newline
        // or a symbol that may be after a value: ':', ',' ';' '}' ')' ']', '#'.
        if (lastByte < 0 || lastByte == ' ' || lastByte == '\t' || lastByte == '\n' || lastByte == '\r' ||
            lastByte == Token.kKeyValueSep ||
            lastByte == Token.kMessageEnd || lastByte == Token.kListEnd || lastByte == Token.kParamsEnd ||
            lastByte == Token.kLineSep1 || lastByte == Token.kLineSep2 || lastByte == Token.kShellComment) {
            if (Token.kSymbols.indexOf(lastByte) >= 0) {
                unread();
            }
            return token(startOffset, len, startLinePos);
        } else {
            // This is safe since line breaks are valid.
            ++len;
            Token token = token(startOffset, len, startLinePos);
            throw failure(token,
                          "Invalid termination of number: '%s'",
                          Strings.escape(token.asString()));
        }

    }

    private Token nextIdentifier() throws TokenizerException {
        int startOffset = readOffset;
        int startLinePos = linePos;
        int startLineNo = lineNo;

        int len = 1, r;
        boolean dot = false;
        while ((r = read()) != -1) {
            if (r == '.') {
                ++len;
                if (dot) {
                    throw failure(startLineNo, startLinePos, len,
                                  "Identifier with double '.'");
                }
                dot = true;
                continue;
            } else if (dot) {
                if (!(r == '_' ||
                      (r >= 'a' && r <= 'z') ||
                      (r >= 'A' && r <= 'Z'))) {
                    if (r >= '0' && r <= '9') {
                        throw failure(startLineNo, startLinePos, len + 1,
                                     "Identifier part starting with digit '" + ((char) r) + "'");
                    } else {
                        throw failure(startLineNo, startLinePos, len,
                                      "Identifier with trailing '.'");
                    }
                }
                ++len;
                dot = false;
                continue;
            } else if (r == '_' ||
                       (r >= '0' && r <= '9') ||
                       (r >= 'a' && r <= 'z') ||
                       (r >= 'A' && r <= 'Z')) {
                ++len;
                continue;
            }

            unread();
            break;
        }

        if (r == -1 || r == ' ' || r == '\t' || r == '\n' || r == '\r' || Token.kSymbols.indexOf(r) >= 0) {
            return token(startOffset, len, startLinePos);
        } else {
            throw failure(startLineNo, startLinePos, len,
                          "Wrongly terminated identifier: '%s'", escapeChar(r));
        }
    }

    /**
     * Get the full non-delimited line content of the 1-indexed line.
     *
     * @param theLine The nine number.
     * @return The line string content.
     */
    @Nonnull
    public String getLine(final int theLine) {
        if (theLine < 1) {
            throw new IllegalArgumentException(theLine + " is not a valid line number. Must be 1 .. N");
        }
        int originalReadOffset = readOffset;
        int originalLineNo = lineNo;
        int originalLinePos = linePos;

        // reset read position.
        readOffset = -1;
        lineNo = 1;
        linePos = 0;

        try {
            int line = theLine;
            while (--line > 0) {
                int c;
                while ((c = this.read()) >= 0) {
                    if (c == '\n') break;
                }
                if (c < 0) {
                    throw new IOException("No such line " + theLine);
                }
            }
            return IOUtils.readString(this, "\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        } finally {
            readOffset = originalReadOffset;
            lineNo = originalLineNo;
            linePos = originalLinePos;
        }
    }

    /**
     * Read the 'content' of encoded binary. This does not parse the
     * binary, just read out from the buffer the string representing the
     * binary data, as delimited by the requested 'end' char.
     *
     * @param end The char that ends the binary content.
     * @return The string encoded string representation.
     * @throws TokenizerException On illegal content.
     */
    public String readBinary(char end) throws IOException {
        int startOffset = readOffset + 1;
        int startLinePos = linePos;
        int startLineNo = lineNo;

        int r;
        while ((r = read()) != -1) {
            if (r == end) {
                return new CharSlice(buffer, startOffset, readOffset - startOffset).asString();
            } else if (r == ' ' || r == '\t' || r == '\n' || r == '\r') {
                throw failure(startLineNo, startLinePos, linePos - startLinePos + 1, "Illegal char '%s' in binary", escapeChar(r));
            }
        }

        // throw in with the old start.
        throw failure(startLineNo, startLinePos, linePos - startLinePos + 1, "unexpected end of stream in binary");
    }

    @Nonnull
    public TokenizerException failure(Token token, String message, Object... params) {
        return failure(token.getLineNo(),
                       token.getLinePos(),
                       token.length(),
                       message, params);
    }

    @Nonnull
    protected TokenizerException failure(int startLineNo,
                                         int startLinePos,
                                         int length,
                                         String format,
                                         Object... params) {
        return failure(format, params).setLineNo(startLineNo)
                                      .setLinePos(startLinePos)
                                      .setLine(getLine(startLineNo))
                                      .setLength(length);
    }

    @Nonnull
    protected TokenizerException failure(Throwable cause,
                                         int startLineNo,
                                         int startLinePos,
                                         int length,
                                         String message,
                                         Object... params) {
        return failure(startLineNo, startLinePos, length, message, params).initCause(cause);
    }

    @Nonnull
    protected TokenizerException failure(String format, Object ... params) {
        return new TokenizerException(format, params);
    }

    @Nonnull
    protected Token token(int off, int len, int linePos) {
        return token(off, len, lineNo, linePos);
    }

    @Nonnull
    public Token token(int off, int len, int lineNo, int linePos) {
        return new Token(buffer, off, len, lineNo, linePos);
    }

    private static String escapeChar(int c) {
        return Strings.escape(new String(new char[]{(char) c}));
    }
}
