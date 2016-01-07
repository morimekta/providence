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
import net.morimekta.providence.util.PStringUtils;
import net.morimekta.providence.util.io.Utf8StreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Stein Eldar Johnsen
 * @since 24.09.15
 */
public class Tokenizer {
    private final Utf8StreamReader mIn;

    private int mLine;
    private int mPos;

    private int mLastByte;

    private boolean mLiteral;
    private boolean mLiteralExcaped;
    private boolean mSlash;

    private final ArrayList<String> mLines;
    private       StringBuilder     mLineBuilder;
    private Token mNextToken;

    public Tokenizer(InputStream in) throws IOException {
        mIn = new Utf8StreamReader(in);

        mLine = 1;
        mPos = 0;

        mLastByte = 0;
        mLiteral = false;
        mLiteralExcaped = false;

        mLineBuilder = new StringBuilder();
        mLines = new ArrayList<>();
    }

    public Token expect(String message) throws IOException, ParseException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file while " + message);
        }
        return next();
    }

    public void expectSymbol(Symbol symbol, String message) throws IOException, ParseException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file, expected " + symbol + " while " + message);
        } else if (mNextToken.isSymbol() && mNextToken.getSymbol().equals(symbol)) {
            mNextToken = null;
        } else {
            throw newParseException("Expected " + symbol + " but found " + mNextToken + " while " + message);
        }
    }

    public Token expectIdentifier(String message) throws IOException, ParseException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file, expected identifier while " + message);
        } else if (mNextToken.isIdentifier()) {
            return next();
        } else {
            throw newParseException("Expected identifier but found " + mNextToken + " while " + message);
        }
    }

    public Token expectQualifiedIdentifier(String message) throws IOException, ParseException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file, expected identifier while " + message);
        } else if (mNextToken.isQualifiedIdentifier()) {
            return next();
        } else {
            throw newParseException("Expected qualified identifier but found " + mNextToken + " while " + message);
        }
    }

    public boolean hasNext() throws IOException, ParseException {
        if (mNextToken == null) {
            mNextToken = next();
        }
        return mNextToken != null;
    }

    public Token next() throws IOException, ParseException {
        if (mNextToken != null) {
            Token tmp = mNextToken;
            mNextToken = null;
            return tmp;
        }

        int startPos = mPos;
        StringBuilder builder = new StringBuilder();

        while (mLastByte >= 0) {
            int b = mLastByte;
            mLastByte = 0;
            if (b == 0) {
                b = mLastByte = mIn.read();
                ++mPos;
            }
            if (b > 0) {
                if (b != '\n') {
                    mLineBuilder.append((char) b);
                }

                if (mLiteral) {
                    mLastByte = 0;

                    builder.append((char) b);

                    if (b == '\n') {
                        throw newParseException("Literal newline in string literal");
                    } else if (mLiteralExcaped) {
                        mLiteralExcaped = false;
                    } else if (b == Symbol.LITERAL_ESCAPE.c) {
                        mLiteralExcaped = true;
                    } else if (b == Symbol.LITERAL_QUOTE.c) {
                        mLiteral = false;
                        return mkToken(builder, startPos);
                    }
                    continue;
                }
                if (mSlash) {
                    mSlash = false;
                    mLastByte = 0;
                    if (b == '/' || b == '*') {
                        builder.append((char) b);
                        return mkToken(builder, startPos);
                    } else if (b < 32 || b >= 127){
                        throw newParseException(String.format(
                                "Invalid start of comment '\\x%x'. Must be '/*' or '//'",
                                b));
                    } else {
                        throw newParseException(String.format(
                                "Invalid start of comment '/%c'. Must be '/*' or '//'",
                                (char) b));
                    }
                }

                Symbol ct = Symbol.valueOf((byte) b);
                if (ct != null) {
                    Token token = mkToken(builder, startPos);
                    if (token != null) {
                        return token;
                    } else {
                        mLastByte = 0;  // consumed 'this'.
                        if (b == Symbol.LITERAL_QUOTE.c) {
                            mLiteral = true;
                            mLiteralExcaped = false;
                            builder.append((char) b);
                            continue;
                        } else if (b == Symbol.JAVA_COMMENT.c) {
                            mSlash = true;
                            builder.append((char) b);
                            continue;
                        }

                        return mkToken(ct, mPos);
                    }
                } else if (b == ' ' || b == '\t' || b == '\r') {
                    mLastByte = 0;
                    if (builder.length() > 0) {
                        return mkToken(builder, startPos);
                    }
                } else if (b == '\n') {
                    mLastByte = 0;
                    mLines.add(mLineBuilder.toString());
                    mLineBuilder = new StringBuilder();

                    Token token = mkToken(builder, startPos);
                    ++mLine;
                    mPos = 0;
                    if (token != null) {
                        return token;
                    }
                } else {
                    builder.append((char) b);
                }
            } else {
                return mkToken(builder, startPos);
            }

            mLastByte = 0;
        }

        return null;
    }

    public void unshift(Token token) {
        mNextToken = token;
    }

    public String getLine(int line) throws IOException {
        if (line < 1) throw new IllegalArgumentException("Oops!!!");
        if (mLines.size() >= line) {
            return mLines.get(line - 1);
        } else {
            // Next line...
            mLineBuilder.append(PStringUtils.readString(mIn, '\n'));
            return mLineBuilder.toString();
        }
    }

    public String readUntil(char terminator) throws IOException {
        return PStringUtils.readString(mIn, terminator);
    }

    public String readUntil(String terminator) throws IOException {
        return PStringUtils.readString(mIn, terminator);
    }

    private ParseException newParseException(String s) throws IOException {
        return new ParseException(s, getLine(mLine), mLine, mPos, 0);
    }

    private Token mkToken(Symbol ct, int pos) {
        return new Token(ct.toString(), mLine, pos, 1);
    }

    private Token mkToken(StringBuilder builder, int startPos) {
        if (builder.length() > 0) {
            return new Token(builder.toString(),
                              mLine,
                              startPos,
                              mPos - startPos - 1);
        }
        return null;
    }
}
