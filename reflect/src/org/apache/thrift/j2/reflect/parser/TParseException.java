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

package org.apache.thrift.j2.reflect.parser;

import java.io.IOException;

import org.apache.thrift.j2.reflect.parser.internal.TToken;
import org.apache.thrift.j2.reflect.parser.internal.TTokenizer;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 24.09.15
 */
public class TParseException extends Exception {
    private final String mLine;
    private final int    mLineNo;
    private final int    mPos;
    private final int    mLen;

    public TParseException(String message, Throwable cause) {
        this(message, null, 0, 0, 0);
        initCause(cause);
    }

    public TParseException(String message) {
        this(message, null, 0, 0, 0);
    }

    public TParseException(String message, String line, int lineNo, int pos, int len) {
        super(message);

        mLine = line;
        mLineNo = lineNo;
        mPos = pos;
        mLen = len;
    }

    public TParseException(String message, TTokenizer tokenizer, TToken token) throws IOException {
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
            return String.format("TParseException(%s,%d:%d,\"%s\")",
                                 getLocalizedMessage(),
                                 getLineNo(),
                                 getPos(),
                                 getLine());
        } else {
            return String.format("TParseException(%s)",
                                 getLocalizedMessage());
        }
    }
}
