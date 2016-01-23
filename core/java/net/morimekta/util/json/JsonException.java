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

package net.morimekta.util.json;

import net.morimekta.util.Strings;

import java.io.IOException;

/**
 * @author Stein Eldar Johnsen
 * @since 19.10.15
 */
public class JsonException extends Exception {
    private final String line;
    private final int    lineNo;
    private final int    linePos;
    private final int    len;

    public JsonException(String message) {
        this(message, null, 0, 0, 0);
    }

    public JsonException(String message, String line, int lineNo, int linePos, int len) {
        super(message);

        this.line = line;
        this.lineNo = lineNo;
        this.linePos = linePos;
        this.len = len;
    }

    public JsonException(String message, JsonTokenizer tokenizer, JsonToken token) throws IOException {
        super(message);

        line = tokenizer.getLine(token.lineNo);
        lineNo = token.lineNo;
        linePos = token.linePos;
        len = token.length();
    }

    public String getLine() {
        return line;
    }

    public int getLineNo() {
        return lineNo;
    }

    public int getLinePos() {
        return linePos;
    }

    public int getLen() {
        return len;
    }

    @Override
    public String toString() {
        if (line != null) {
            return String.format("%s : %d : %d - %d\n# %s\n#%s^",
                                 getLocalizedMessage(),
                                 getLineNo(),
                                 getLinePos(),
                                 getLen(),
                                 getLine(),
                                 Strings.times("-", linePos));
        } else {
            return String.format("JsonException(%s)",
                                 getLocalizedMessage());
        }
    }
}
