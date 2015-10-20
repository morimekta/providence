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

import org.apache.thrift.j2.reflect.parser.TParseException;
import org.apache.thrift.j2.util.json.JsonException;
import org.apache.thrift.j2.util.json.JsonToken;
import org.apache.thrift.j2.util.json.JsonTokenizer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 24.09.15
 */
public class TToken {
    public static final Pattern RE_IDENTIFIER = Pattern.compile(
            "[_a-zA-Z][_a-zA-Z0-9]*");
    public static final Pattern RE_QUALIFIED_IDENTIFIER = Pattern.compile(
            "([_a-zA-Z][_a-zA-Z0-9]*[.])*[_a-zA-Z][_a-zA-Z0-9]*");
    public static final Pattern RE_INTEGER = Pattern.compile(
            "[0-9]+");

    private final String mToken;
    private final int mLine;
    private final int mPos;
    private final int mLen;

    public boolean startsLineComment() {
        return mToken.equals(Character.toString((char) TSymbol.SHELL_COMMENT.c)) ||
               mToken.equals(TKeyword.JAVA_LINE_COMMENT_START.keyword);
    }

    public boolean startsBlockComment() {
        return mToken.equals(TKeyword.BLOCK_COMMENT_START.keyword);
    }

    public TToken(String token, int line, int pos, int len) {
        mToken = token;
        mLine = line;
        mPos = pos;
        mLen = len;
    }

    public String getToken() {
        return mToken;
    }

    public int getLine() {
        return mLine;
    }

    public int getPos() {
        return mPos;
    }

    public int getLen() {
        return mLen;
    }

    public boolean isSymbol() {
        return mLen == 1 && TSymbol.valueOf(mToken.charAt(0)) != null;
    }

    public boolean isLiteral() {
        return (mToken.length() > 1 &&
                mToken.charAt(0) == '\"' &&
                mToken.charAt(mToken.length() - 1) == '\"');
    }

    public boolean isIdentifier() {
        if (!RE_IDENTIFIER.matcher(mToken).matches()) return false;
        return true;
    }

    public boolean isQualifiedIdentifier() {
        if (!RE_QUALIFIED_IDENTIFIER.matcher(mToken).matches()) return false;
        return true;
    }

    public boolean isInteger() {
        return RE_INTEGER.matcher(mToken).matches();
    }

    public TSymbol getSymbol() {
        return TSymbol.valueOf(mToken.charAt(0));
    }

    public String getLiteral() throws TParseException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(mToken.getBytes(StandardCharsets.UTF_8));
            JsonTokenizer tokenizer = new JsonTokenizer(bais);
            JsonToken token = tokenizer.expect("parsing string literal.");
            return token.literalValue();
        } catch (JsonException e) {
            throw new TParseException("Unable to parse string literal: " + mToken);
        } catch (IOException e) {
            throw new TParseException("Unable to read string literal: " + mToken);
        }
    }

    public int getInteger() {
        return Integer.parseInt(mToken);
    }

    @Override
    public String toString() {
        return String.format("Token('%s',%d:%d-%d)",
                             mToken, mLine, mPos, mLen);
    }
}
