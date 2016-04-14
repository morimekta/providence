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

package net.morimekta.providence.reflect.parser.internal;

import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Stein Eldar Johnsen
 * @since 24.09.15
 */
public class Tokenizer extends InputStream {
    private final byte[] buffer;
    private int readOffset;

    private int lineNo;
    private int linePos;

    private boolean mSlash;

    private       Token             nextToken;

    public Tokenizer(InputStream in) throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        IOUtils.copy(in, tmp);

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

    public Token expect(String message) throws IOException, ParseException {
        if (!hasNext()) {
            throw new ParseException("Unexpected end of file, while %s", message);
        }
        Token next = nextToken;
        nextToken = null;
        return next;
    }

    public Token peek(String message) throws IOException, ParseException {
        if (!hasNext()) {
            throw new ParseException("Unexpected end of file, while %s", message);
        }
        return nextToken;
    }
    public Token peek() throws IOException, ParseException {
        hasNext();
        return nextToken;
    }

    public char expectSymbol(String message, char... symbols) throws IOException, ParseException {
        if (!hasNext()) {
            throw new ParseException("Unexpected end of file, expected one of ['%s'] while %s",
                                     Strings.escape(Strings.join("', '", symbols)),
                                     message);
        } else {
            for (char symbol : symbols) {
                if (nextToken.isSymbol(symbol)) {
                    nextToken = null;
                    return symbol;
                }
            }

            throw new ParseException(this, nextToken,
                                     "Expected one of ['%s'], but found '%s' while %s",
                                     Strings.escape(Strings.join("', '", symbols)),
                                     Strings.escape(nextToken.asString()),
                                     message);
        }
    }

    public Token expectIdentifier(String message) throws IOException, ParseException {
        if (!hasNext()) {
            throw new ParseException("Unexpected end of file, while %s", message);
        } else if (nextToken.isIdentifier()) {
            Token next = nextToken;
            nextToken = null;
            return next;
        } else {
            throw new ParseException(this, nextToken,
                                     "Expected identifier, but found '%s' while %s",
                                     Strings.escape(nextToken.asString()),
                                     message);
        }
    }

    public Token expectQualifiedIdentifier(String message) throws IOException, ParseException {
        if (!hasNext()) {
            throw new ParseException("Unexpected end of file, while %s", message);
        } else if (nextToken.isQualifiedIdentifier()) {
            Token next = nextToken;
            nextToken = null;
            return next;
        } else {
            throw new ParseException(this, nextToken,
                                     "Expected qualified identifier, but found '%s' while %s",
                                     Strings.escape(nextToken.asString()),
                                     message);
        }
    }

    public Token expectStringLiteral(String message) throws IOException, ParseException {
        if (!hasNext()) {
            throw new ParseException("Unexpected end of file, while %s", message);
        } else if (nextToken.isStringLiteral()) {
            Token next = nextToken;
            nextToken = null;
            return next;
        } else {
            throw new ParseException(this, nextToken,
                                     "Expected string literal, but found '%s' while %s",
                                     Strings.escape(nextToken.asString()),
                                     message);
        }
    }

    public Token expectInteger(String message) throws IOException, ParseException {
        if (!hasNext()) {
            throw new ParseException("Unexpected end of file, while %s", message);
        } else if (nextToken.isInteger()) {
            Token next = nextToken;
            nextToken = null;
            return next;
        } else {
            throw new ParseException(this, nextToken,
                                     "Expected integer, but found '%s' while %s",
                                     Strings.escape(nextToken.asString()),
                                     message);
        }
    }

    public boolean hasNext() throws IOException, ParseException {
        if (nextToken == null) {
            nextToken = nextInternal();
        }
        return nextToken != null;
    }

    public Token next() throws IOException, ParseException {
        if (nextToken != null) {
            Token tmp = nextToken;
            nextToken = null;
            return tmp;
        }

        return nextInternal();
    }

    private Token nextStringLiteral(int startQuote) throws ParseException {
        int startOffset = readOffset;
        int startLinePos = linePos;
        boolean escaped = false;
        while (true) {
            int r = read();
            if (r < 0x20 || r == 0x7F) {
                int pos = startOffset - readOffset;
                if (r == -1) {
                    throw new ParseException(
                            "Unexpected end of stream in string: line" + lineNo + " pos " + startLinePos + pos);
                } else {
                    throw new ParseException(
                            "Invalid string literal char: " + r + " at line " + lineNo + " pos " + startLinePos + pos);
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

    private Token nextInternal() throws IOException, ParseException {
        int startOffset = readOffset;
        int r;
        while ((r = read()) != -1) {
            if (r != ' ' && r != '\t' && r != '\r' && r != '\n') {
                startOffset = readOffset;
                break;
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

        // Java comment starts (shell comment start is a symbol)
        if (r == Token.kJavaCommentStart) {
            // special case.
            int s = read();
            if (s == -1) {
                throw new ParseException("Unexpected end of stream line " + lineNo + ", pos " + linePos + ".");
            } else if (s == '/' || s == '*') {
                return new Token(buffer, startOffset, 2, lineNo, linePos++);
            } else if (s < 32 || s >= 127) {
                throw new ParseException("Invalid start of comment '\\x%x'. Must be '/*' or '//'", s);
            } else {
                throw new ParseException("Invalid start of comment '/%c'. Must be '/*' or '//'", (char) s);
            }
        }

        // Identifier / qualified identifier / type name token.
        if (r == '_' ||
            (r >= 'a' && r <= 'z') ||
            (r >= 'A' && r <= 'Z')) {
            return nextIdentifier();
        }

        throw new ParseException(String.format("Unknown token initiator: %c, line %d, pos %d", r, lineNo, linePos));
    }

    private Token nextNumber(int lastByte) throws ParseException {
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
                throw new ParseException("Unexpected end of stream on line " + lineNo);
            }

            if (!(lastByte == '.' || (lastByte >= '0' && lastByte <= '9'))) {
                throw new ParseException("No decimal after negative indicator.");
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

        // A number must be terminated correctly: End of stream, space, newline
        // or a symbol that may be after a value: ':', ',' ';' '}' ')'.
        if (lastByte < 0 ||
            lastByte == ' ' ||
            lastByte == '\t' ||
            lastByte == '\n' ||
            lastByte == '\r' ||
            lastByte == Token.kFieldIdSep ||
            lastByte == Token.kMessageEnd ||
            lastByte == Token.kLineSep1 ||
            lastByte == Token.kLineSep2 ||
            lastByte == Token.kJavaCommentStart ||
            lastByte == Token.kShellComment ||
            lastByte == Token.kParamsEnd) {
            if (Token.kSymbols.indexOf(lastByte) >= 0) {
                unread();
            }
            return new Token(buffer, startOffset, len, lineNo, startLinePos);
        } else {
            throw new ParseException("Wrongly terminated number: %c.", (char) lastByte);
        }
    }

    private Token nextIdentifier() throws ParseException {
        int startOffset = readOffset;
        int startLinePos = linePos;

        int len = 1, r;
        boolean dot = false;
        while ((r = read()) != -1) {
            if (r == '.') {
                if (dot) {
                    throw new ParseException("");
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

        if (r == -1 ||
            r == ' ' ||
            r == '\t' ||
            r == '\n' ||
            r == '\r' ||
            r == Token.kJavaCommentStart ||
            Token.kSymbols.indexOf(r) >= 0) {
            return new Token(buffer, startOffset, len, lineNo, startLinePos);
        } else {
            throw new ParseException("Wrongly terminated identifier: %c.", (char) r);
        }
    }

    public void unshift(Token token) {
        nextToken = token;
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
        return Strings.readString(this, "\n");
    }
}
