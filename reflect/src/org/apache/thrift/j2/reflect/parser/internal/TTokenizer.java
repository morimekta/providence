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

package org.apache.thrift.j2.reflect.parser.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.thrift.j2.reflect.parser.TParseException;
import org.apache.thrift.j2.util.TStringUtils;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 24.09.15
 */
public class TTokenizer {
    private final static int MARK_LIMIT = 1024;

    private final BufferedInputStream mIn;

    private int mLine;
    private int mPos;

    private int mLastByte;

    private boolean mLiteral;
    private boolean mLiteralExcaped;
    private boolean mSlash;

    private final ArrayList<String> mLines;
    private       StringBuilder     mLineBuilder;
    private       TToken            mNextToken;

    public TTokenizer(InputStream in) throws IOException {
        mIn = new BufferedInputStream(in);

        mLine = 1;
        mPos = 0;

        mLastByte = 0;
        mLiteral = false;
        mLiteralExcaped = false;

        mLineBuilder = new StringBuilder();
        mLines = new ArrayList<>();
    }

    public TToken expect(String message) throws IOException, TParseException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file while " + message);
        }
        return next();
    }

    public void expectSymbol(TSymbol symbol, String message) throws IOException, TParseException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file, expected " + symbol + " while " + message);
        } else if (mNextToken.isSymbol() && mNextToken.getSymbol().equals(symbol)) {
            mNextToken = null;
        } else {
            throw newParseException("Expected " + symbol + " but found " + mNextToken + " while " + message);
        }
    }

    public TToken expectIdentifier(String message) throws IOException, TParseException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file, expected identifier while " + message);
        } else if (mNextToken.isIdentifier()) {
            return next();
        } else {
            throw newParseException("Expected identifier but found " + mNextToken + " while " + message);
        }
    }

    public TToken expectQualifiedIdentifier(String message) throws IOException, TParseException {
        if (!hasNext()) {
            throw newParseException("Unexpected end of file, expected identifier while " + message);
        } else if (mNextToken.isQualifiedIdentifier()) {
            return next();
        } else {
            throw newParseException("Expected qualified identifier but found " + mNextToken + " while " + message);
        }
    }

    public boolean hasNext() throws IOException, TParseException {
        if (mNextToken == null) {
            mNextToken = next();
        }
        return mNextToken != null;
    }

    public TToken next() throws IOException, TParseException {
        if (mNextToken != null) {
            TToken tmp = mNextToken;
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

                    if (mLiteralExcaped) {
                        mLiteralExcaped = false;
                    } else if (b == '\n') {
                        throw newParseException("Literal newline in string literal");
                    } else if (b == TSymbol.LITERAL_ESCAPE.c) {
                        mLiteralExcaped = true;
                    } else if (b == TSymbol.LITERAL_QUOTE.c) {
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

                TSymbol ct = TSymbol.valueOf((byte) b);
                if (ct != null) {
                    TToken token = mkToken(builder, startPos);
                    if (token != null) {
                        return token;
                    } else {
                        mLastByte = 0;  // consumed 'this'.
                        if (b == TSymbol.LITERAL_QUOTE.c) {
                            mLiteral = true;
                            mLiteralExcaped = false;
                            builder.append((char) b);
                            continue;
                        } else if (b == TSymbol.JAVA_COMMENT.c) {
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

                    TToken token = mkToken(builder, startPos);
                    mIn.mark(MARK_LIMIT);
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

    public void unshift(TToken token) {
        mNextToken = token;
    }

    public String getLine(int line) throws IOException {
        if (line < 1) throw new IllegalArgumentException("Oops!!!");
        if (mLines.size() >= line) {
            return mLines.get(line - 1);
        } else {
            // Next line...
            int pos = mPos;
            mIn.reset();
            String l = TStringUtils.readString(mIn, "\n");
            mIn.reset();
            mIn.skip(pos);
            return l;
        }
    }

    public String readUntil(String terminator) throws IOException {
        return TStringUtils.readString(mIn, terminator);
    }

    private TParseException newParseException(String s) throws IOException {
        return new TParseException(s, getLine(mLine), mLine, mPos, 0);
    }

    private TToken mkToken(TSymbol ct, int pos) {
        return new TToken(ct.toString(), mLine, pos, 1);
    }

    private TToken mkToken(StringBuilder builder, int startPos) {
        if (builder.length() > 0) {
            return new TToken(builder.toString(),
                              mLine,
                              startPos,
                              mPos - startPos - 1);
        }
        return null;
    }
}
