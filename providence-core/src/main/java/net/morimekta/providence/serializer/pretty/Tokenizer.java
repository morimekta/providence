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

import net.morimekta.util.Strings;
import net.morimekta.util.io.LineBufferedReader;
import net.morimekta.util.io.Utf8StreamReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Simple tokenizer for the pretty serializer that strips away comments based
 * on the "#" (shell) comment character. Each comment lasts until the next
 * newline.
 */
public class Tokenizer extends LineBufferedReader {
    @FunctionalInterface
    public interface TokenValidator {
        boolean validate(Token token);
    }

    public static final int     DEFAULT_BUFFER_SIZE = 1 << 11; // 2048 chars --> 4kb

    private Token unreadToken;

    /**
     * Create a JSON tokenizer that reads from the input steam. It will only
     * read as far as requested, and no bytes further. It has no checking of
     * whether the document follows the JSON standard, but will only accept
     * JSON formatted tokens.
     *
     * Note that the content is assumed to be separated with newlines, which
     * means that if multiple JSON contents are read from the same stream, they
     * MUST have a separating newline. A single JSON object may still have
     * newlines in it's stream.
     *
     * @param in Input stream to parse from.
     */
    public Tokenizer(InputStream in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Create a JSON tokenizer that reads from the input steam. It will only
     * read as far as requested, and no bytes further. It has no checking of
     * whether the document follows the JSON standard, but will only accept
     * JSON formatted tokens.
     *
     * Note that the content is assumed to be separated with newlines, which
     * means that if multiple JSON contents are read from the same stream, they
     * MUST have a separating newline. A single JSON object may still have
     * newlines in it's stream.
     *
     * @param in Input stream to parse from.
     * @param bufferSize The size of the char buffer. Default is 2048 chars
     *                   (4096 bytes).
     */
    public Tokenizer(InputStream in, int bufferSize) {
        this(new Utf8StreamReader(in), bufferSize, false);
    }

    /**
     * Create a tokenizer that will read everything from the input stream and
     * handle it as a single multi-line buffer.
     *
     * @param in Reader of content to parse.
     * @param bufferSize The size of the char buffer. Default is 2048 chars
     *                   (4096 bytes).
     * @param preLoadAll Load all content up front. Handy for config and thrift
     *                   program files.
     */
    public Tokenizer(Reader in, int bufferSize, boolean preLoadAll) {
        super(in, bufferSize, preLoadAll);
        // If the line is longer than 16k, it will not be used in error messages.
        this.unreadToken = null;
    }

    /**
     * Expect a new JSON token on the stream.
     *
     * @param expected Message to add to exception if there are no more JSON
     *                tokens on the stream.
     * @return The next token.
     * @throws IOException If unable to read from stream.
     */
    @Nonnull
    public Token expect(@Nonnull String expected) throws IOException {
        if (!hasNext()) {
            throw failure("Expected %s: Got end of file", expected);
        }
        Token tmp = unreadToken;
        unreadToken = null;
        return tmp;
    }

    /**
     * Expect at a valid token containing anything.
     *
     * @param expected The expectation description.
     * @param validator Validator callback.
     * @return The token.
     * @throws IOException If failed to read a token.
     */
    public Token expect(@Nonnull String expected, @Nonnull TokenValidator validator) throws IOException {
        if (!hasNext()) {
            throw failure("Expected %s, got end of file", expected);
        } else if (validator.validate(unreadToken)) {
            Token next = unreadToken;
            unreadToken = null;
            return next;
        }
        throw failure(unreadToken, "Expected %s, but got '%s'", expected, Strings.escape(unreadToken.asString()));
    }

    /**
     * @param expected Message to add to exception if there are no more JSON
     *                 tokens on the stream.
     * @param symbols List of symbol characters to expect.
     * @return The symbol that was encountered.
     * @throws IOException If unable to read from stream.
     */
    public char expectSymbol(@Nonnull String expected, char... symbols) throws IOException {
        if (symbols.length == 0) {
            throw new IllegalArgumentException("No symbols to match.");
        }
        if (!hasNext()) {
            if (symbols.length == 1) {
                throw failure("Expected %s ('%c'), Got end of file", expected, symbols[0]);
            }
            throw failure("Expected %s (one of ['%s']): Got end of file", expected, Strings.joinP("', '", symbols));
        } else {
            for (char symbol : symbols) {
                if (unreadToken.isSymbol(symbol)) {
                    unreadToken = null;
                    return symbol;
                }
            }

            if (symbols.length == 1) {
                throw failure(getLineNo(), getLinePos(), 1, "Expected %s ('%c'): but found '%s'", expected, symbols[0], unreadToken.asString());
            }

            throw failure(getLineNo(), getLinePos(), 1, "Expected %s (one of ['%s']): but found '%s'",
                          expected,
                          Strings.joinP("', '", symbols),
                          unreadToken.asString());
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

    /**
     * Whether there is another token on the stream. This will read up until
     * it finds a JSON token, or until the stream ends.
     *
     * @return True if (and only if) there is at least one more token on the
     *         stream.
     * @throws IOException If unable to read from stream.
     */
    public boolean hasNext() throws IOException {
        if (unreadToken == null) {
            unreadToken = next();
        }
        return unreadToken != null;
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
        int startOffset = bufferOffset;
        int startLinePos = linePos;
        CharArrayWriter baos = new CharArrayWriter();
        while (readNextChar()) {
            if (lastChar == end) {
                lastChar = 0;
                return baos.toString();
            } else if (lastChar == ' ' || lastChar == '\t' || lastChar == '\r' || lastChar == '\n') {
                throw failure(getLineNo(), startLinePos, startOffset, "Illegal char '%s' in binary", Strings.escape((char) lastChar));
            }
            baos.write(lastChar);
        }
        throw failure("Unexpected end of file in binary");
    }

    /**
     * Return the next token or throw an exception. Though it does not consume
     * that token.
     *
     * @return The next token.
     * @throws IOException If unable to read from stream.
     */
    public Token peek() throws IOException {
        hasNext();
        return unreadToken;
    }

    /**
     * Return the next token or throw an exception. Though it does not consume
     * that token.
     *
     * @param message Message to add to exception if there are no more JSON
     *                tokens on the stream.
     * @return The next token.
     * @throws IOException If unable to read from stream.
     */
    @Nonnull
    public Token peek(String message) throws IOException {
        if (!hasNext()) {
            throw failure(lineNo, linePos + 1, 0, "Expected %s: Got end of file", message);
        }
        return unreadToken;
    }

    /**
     * Returns the next token on the stream, or null if there are no more JSON
     * tokens on the stream.
     * @return The next token, or null.
     * @throws IOException If unable to read from stream.
     */
    @Nullable
    public Token next() throws IOException {
        if (unreadToken != null) {
            Token tmp = unreadToken;
            unreadToken = null;
            return tmp;
        }

        while (lastChar >= 0) {
            if (lastChar == 0) {
                if (!readNextChar()) {
                    break;
                }
            }
            if (lastChar == Token.kNewLine ||
                lastChar == Token.kCarriageReturn ||
                lastChar == Token.kSpace ||
                lastChar == Token.kTab) {
                lastChar = 0;
                continue;
            }

            if (lastChar == Token.kShellComment) {
                getRestOfLine();
                continue;
            }

            if (lastChar == Token.kLiteralDoubleQuote || lastChar == Token.kLiteralQuote) {
                return nextStringLiteral((char) lastChar);
            } else if (lastChar == '.' || lastChar == '-' || (lastChar >= '0' && lastChar <= '9')) {
                return nextNumber();
            } else if ('_' == lastChar ||
                       ('a' <= lastChar && lastChar <= 'z') ||
                       ('A' <= lastChar && lastChar <= 'Z')) {
                return nextToken();
            } else if (lastChar < 0x20 || lastChar >= 0x7F) {
                // non-ASCII UTF-8 characters are only allowed inside JSON string literals.
                throw failure(lineNo, linePos, 1, "Unknown token initiator '%s'", Strings.escape((char) lastChar));
            } else {
                return nextSymbol();
            }
        }

        return null;
    }

    // --- INTERNAL ---

    @Nonnull
    protected Token nextSymbol() throws IOException {
        lastChar = 0;
        return new Token(buffer, bufferOffset, 1, lineNo, linePos);
    }

    @Nonnull
    private Token nextToken() throws IOException {
        maybeConsolidateBuffer();

        int startPos = linePos;
        int startOffset = bufferOffset;
        int startLine = lineNo;
        int len = 0;

        int lastLast = 0;
        while (lastChar == '_' || lastChar == '.' ||
               (lastChar >= '0' && lastChar <= '9') ||
               (lastChar >= 'a' && lastChar <= 'z') ||
               (lastChar >= 'A' && lastChar <= 'Z')) {
            if (lastChar == '.' && lastLast == '.') {
                throw failure(lineNo, startPos, bufferOffset - startOffset + 1,
                              "Identifier with double '.'");
            } else if (lastLast == '.' &&
                       ('0' <= lastChar && lastChar <= '9')) {
                throw failure(lineNo, startPos, bufferOffset - startOffset + 1,
                              "Identifier part starting with digit '%c'", lastChar);
            }
            lastLast = lastChar;
            ++len;
            if (!readNextChar()) {
                break;
            }
        }

        if (lastLast == '.') {
            throw failure(lineNo, startPos, bufferOffset - startOffset,
                          "Identifier with trailing '.'");
        }

        return new Token(buffer, startOffset, len, startLine, startPos);
    }

    @Nonnull
    private Token nextNumber() throws IOException {
        maybeConsolidateBuffer();
        // NOTE: This code is pretty messy because it is a full state-engine
        // to ensure that the parsed number follows the JSON number syntax.
        // Alternatives are:
        //
        // dec = -?0
        // dec = -?.0
        // dec = -?0.0
        // sci = (dec)[eE][+-]?[0-9]+
        // hex = 0x[0-9a-fA-F]+
        // oct = 0[0-7]+
        //
        // It is programmed as a state-engine to be very efficient, but
        // correctly detect valid JSON (and what is invalid if not).

        int startPos = linePos;
        int startOffset = bufferOffset;
        // number (any type).
        int len = 0;

        if (lastChar == '-') {
            // only base 10 decimals can be negative.
            ++len;
            if (!readNextChar()) {
                throw failure(lineNo, startPos, bufferOffset - startOffset,
                              "Negative indicator without number");
            }

            if (!(lastChar == '.' || (lastChar >= '0' && lastChar <= '9'))) {
                throw failure(lineNo, startPos, bufferOffset - startOffset,
                              "No decimal after negative indicator");
            }
        } else if (lastChar == '0') {
            if (readNextChar()) {
                ++len;
                if (lastChar == 'x') {
                    ++len;
                    if (!readNextChar()) {
                        throw failure(lineNo, startPos, bufferOffset - startOffset + 1,
                                      "No decimal after hex indicator");
                    }
                    // hexadecimal.
                    do {
                        if (!((lastChar >= '0' && lastChar <= '9') || (lastChar >= 'a' && lastChar <= 'f') ||
                              (lastChar >= 'A' && lastChar <= 'F'))) {
                            // we read a char that's *not* part of the hex number.
                            break;
                        }
                        ++len;
                    } while (readNextChar());

                    return validateAfterNumber(startOffset, startPos, len);
                } else if ('0' <= lastChar && lastChar <= '7') {
                    ++len;
                    // Octals have 0 in front, and then more digits.
                    while (readNextChar()) {
                        if (lastChar < '0' || lastChar > '7') {
                            break;
                        }
                        ++len;
                    }
                    return validateAfterNumber(startOffset, startPos, len);
                }
            } else {
                // just '0'
                return validateAfterNumber(startOffset, startPos, 1);
            }
        }

        // decimal part.
        while (lastChar >= '0' && lastChar <= '9') {
            ++len;
            // numbers are terminated by first non-numeric character.
            if (!readNextChar()) {
                break;
            }
        }
        // fraction part.
        if (lastChar == '.') {
            ++len;
            // numbers are terminated by first non-numeric character.
            if (readNextChar()) {
                while (lastChar >= '0' && lastChar <= '9') {
                    ++len;
                    // numbers are terminated by first non-numeric character.
                    if (!readNextChar()) {
                        break;
                    }
                }
            }
        }
        // exponent part.
        if (lastChar == 'e' || lastChar == 'E') {
            ++len;
            // numbers are terminated by first non-numeric character.
            if (!readNextChar()) {
                String tmp = new String(buffer, startOffset, len);
                throw failure(lineNo, startPos, bufferOffset - startOffset + 1,
                              "Badly terminated number exponent: '%s'", tmp);
            }

            // The exponent can be explicitly prefixed with both '+'
            // and '-'.
            if (lastChar == '-' || lastChar == '+') {
                ++len;
                // numbers are terminated by first non-numeric character.
                if (!readNextChar()) {
                    String tmp = new String(buffer, startOffset, len);
                    throw failure(lineNo, startPos, bufferOffset - startOffset + 1,
                                  "Badly terminated number exponent: '%s'", tmp);
                }
            }

            if (lastChar >= '0' && lastChar <= '9') {
                while (lastChar >= '0' && lastChar <= '9') {
                    ++len;
                    // numbers are terminated by first non-numeric character.
                    if (!readNextChar()) {
                        break;
                    }
                }
            } else if (lastChar > 0) {
                String tmp = new String(buffer, startOffset, len + 1);
                throw failure(lineNo, startPos, bufferOffset - startOffset + 1,
                              "Badly terminated number exponent: '%s'", tmp);
            } else {
                String tmp = new String(buffer, startOffset, len + 1);
                throw failure(lineNo, startPos, bufferOffset - startOffset,
                              "Badly terminated number exponent: '%s'", tmp);
            }
        }

        return validateAfterNumber(startOffset, startPos, len);
    }

    private Token validateAfterNumber(int startOffset, int startLinePos, int len)
            throws TokenizerException {
        // A number must be terminated correctly: End of stream, space or a
        // symbol that may be after a value: ',' '}' ']'.
        if (lastChar == '_' ||
            (lastChar >= 'a' && lastChar <= 'z') ||
            (lastChar >= 'A' && lastChar <= 'Z')) {
            String tmp = new String(buffer, startOffset, len + 1);
            throw failure(lineNo, startLinePos, bufferOffset - startOffset + 1,
                          "Invalid termination of number: '%s'", tmp);
        } else {
            return new Token(buffer, startOffset, len, lineNo, startLinePos);
        }
    }

    @Nonnull
    private Token nextStringLiteral(char quote) throws IOException {
        maybeConsolidateBuffer();

        // string literals may be longer than 128 bytes. We may need to build it.
        StringBuilder consolidatedString = null;

        int startPos = linePos;
        int startOffset = bufferOffset;

        boolean esc = false;
        for (; ; ) {
            if (!preLoaded && bufferOffset >= (bufferLimit - 1)) {
                if (consolidatedString == null) {
                    consolidatedString = new StringBuilder();
                }
                consolidatedString.append(buffer, startOffset, bufferOffset - startOffset + 1);
                startOffset = 0;
            }

            if (!readNextChar()) {
                throw failure(lineNo, startPos, bufferOffset - startOffset + 1,
                              "Unexpected end of stream in string literal");
            }

            if (esc) {
                esc = false;
            } else if (lastChar == Token.kLiteralEscape) {
                esc = true;
            } else if (lastChar == quote) {
                break;
            } else if (lastChar == Token.kNewLine) {
                throw failure(lineNo, startPos, bufferOffset - startOffset,
                              "Unexpected newline in string literal");
            } else if (lastChar < 0x20 || lastChar == 0x7f ||
                       (lastChar > 0x7f && !Strings.isConsolePrintable(lastChar))) {
                throw failure(lineNo, startPos, bufferOffset - startOffset + 1,
                              "Unescaped non-printable char in literal: '%s'", Strings.escape((char) lastChar));
            }
        }

        lastChar = 0;
        if (consolidatedString != null) {
            consolidatedString.append(buffer, 0, bufferOffset + 1);
            String result = consolidatedString.toString();
            return new Token(result.toCharArray(), 0, result.length(), lineNo, startPos);
        } else {
            return new Token(buffer, startOffset, bufferOffset - startOffset + 1, lineNo, startPos);
        }
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
                                      .setLine(getLine())
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
}
