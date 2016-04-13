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

import net.morimekta.providence.reflect.parser.internal.Token;
import net.morimekta.providence.reflect.parser.internal.Tokenizer;

import java.io.IOException;

/**
 * @author Stein Eldar Johnsen
 * @since 24.09.15
 */
public class ParseException extends Exception {
    private final String line;
    private final Token token;

    public ParseException(Throwable cause, String message, Object... params) {
        super(String.format(message, params), cause);

        line = null;
        token = null;
    }

    public ParseException(String message, Object... params) {
        super(String.format(message, params));

        line = null;
        token = null;
    }

    public ParseException(String line, Token token, String message, Object... params) {
        super(String.format(message, params));

        this.line = line;
        this.token = token;
    }

    public ParseException(Tokenizer tokenizer, Token token, String message, Object... params) throws IOException {
        super(String.format(message, params));

        this.line = tokenizer.getLine(token.getLineNo());
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public String getLine() {
        return line;
    }

    @Override
    public String toString() {
        if (line != null && token != null) {
            return String.format("ParseException(%s,%d:%d,\"%s\")",
                                 getLocalizedMessage(),
                                 token.getLineNo(),
                                 token.getLinePos(),
                                 line);
        } else {
            return String.format("ParseException(%s)", getLocalizedMessage());
        }
    }
}
