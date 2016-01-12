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
import net.morimekta.providence.util.json.JsonException;
import net.morimekta.providence.util.json.JsonToken;
import net.morimekta.providence.util.json.JsonTokenizer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * @author Stein Eldar Johnsen
 * @since 24.09.15
 */
public class Token {
    public static final Pattern RE_IDENTIFIER = Pattern.compile(
            "[_a-zA-Z][_a-zA-Z0-9]*");
    public static final Pattern RE_QUALIFIED_IDENTIFIER = Pattern.compile(
            "([_a-zA-Z][_a-zA-Z0-9]*[.])*[_a-zA-Z][_a-zA-Z0-9]*");
    public static final Pattern RE_INTEGER = Pattern.compile(
            "-?(0|[1-9][0-9]*|0[0-7]+|0x[0-9a-fA-F]+)");

    private final String mToken;
    private final int mLine;
    private final int mPos;
    private final int mLen;

    public boolean startsLineComment() {
        return mToken.equals(Character.toString((char) Symbol.SHELL_COMMENT.c)) ||
               mToken.equals(Keyword.JAVA_LINE_COMMENT_START.keyword);
    }

    public boolean startsBlockComment() {
        return mToken.equals(Keyword.BLOCK_COMMENT_START.keyword);
    }

    public Token(String token, int line, int pos, int len) {
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
        return mLen == 1 && Symbol.valueOf(mToken.charAt(0)) != null;
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

    public Symbol getSymbol() {
        return Symbol.valueOf(mToken.charAt(0));
    }

    public String literalValue() throws ParseException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(mToken.getBytes(StandardCharsets.UTF_8));
            JsonTokenizer tokenizer = new JsonTokenizer(bais);
            JsonToken token = tokenizer.expect("parsing string literal.");
            return token.decodeJsonLiteral();
        } catch (JsonException e) {
            throw new ParseException("Unable to parse string literal: " + mToken, e);
        } catch (IOException e) {
            throw new ParseException("Unable to read string literal: " + mToken, e);
        }
    }

    public int intValue() {
        if (mToken.startsWith("0x")) {
            return Integer.parseInt(mToken.substring(2), 16);
        } else if (mToken.startsWith("0") && mToken.length() > 1) {
            return Integer.parseInt(mToken.substring(1), 8);
        }
        return Integer.parseInt(mToken);
    }

    @Override
    public String toString() {
        return String.format("Token('%s',%d:%d-%d)",
                             mToken, mLine, mPos, mLen);
    }
}
