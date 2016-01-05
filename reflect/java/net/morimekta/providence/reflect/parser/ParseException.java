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

package net.morimekta.providence.reflect.parser;

import java.io.IOException;

import net.morimekta.providence.reflect.parser.internal.Token;
import net.morimekta.providence.reflect.parser.internal.Tokenizer;

/**
 * @author Stein Eldar Johnsen
 * @since 24.09.15
 */
public class ParseException extends Exception {
    private final String mLine;
    private final int    mLineNo;
    private final int    mPos;
    private final int    mLen;

    public ParseException(String message, Throwable cause) {
        this(message, null, 0, 0, 0);
        initCause(cause);
    }

    public ParseException(String message) {
        this(message, null, 0, 0, 0);
    }

    public ParseException(String message, String line, int lineNo, int pos, int len) {
        super(message);

        mLine = line;
        mLineNo = lineNo;
        mPos = pos;
        mLen = len;
    }

    public ParseException(String message, Tokenizer tokenizer, Token token) throws IOException {
        super(message);

        mLine = tokenizer.getLine(token.getLine());
        mLineNo = token.getLine();
        mPos = token.getPos();
        mLen = token.getLen();
    }

    public String getLine() {
        return mLine;
    }

    public int getLineNo() {
        return mLineNo;
    }

    public int getPos() {
        return mPos;
    }

    public int getLen() {
        return mLen;
    }

    @Override
    public String toString() {
        if (mLine != null) {
            return String.format("ParseException(%s,%d:%d,\"%s\")",
                                 getLocalizedMessage(),
                                 getLineNo(),
                                 getPos(),
                                 getLine());
        } else {
            return String.format("ParseException(%s)",
                                 getLocalizedMessage());
        }
    }
}
