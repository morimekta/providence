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

/**
 * @author Stein Eldar Johnsen
 * @since 01.10.15
 */
public enum TSymbol {
    LITERAL_QUOTE('"'),
    LITERAL_ESCAPE('\\'),

    LIST_START('['),
    LIST_END(']'),
    LIST_SEPARATOR(','),  // also valid as entry separator...

    MAP_START('{'),
    MAP_END('}'),
    MAP_KEY_ENTRY_SEP(':'),
    MAP_ENTRY_VALUE_SEP('='),

    GENERIC_START('<'),
    GENERIC_END('>'),

    ENTRY_SEPARATOR(';'),  // also valid as list separator...

    SHELL_COMMENT('#'),
    JAVA_COMMENT('/'),

    PARAMS_BEGIN('('),
    PARAMS_END(')'),;

    public final int c;

    TSymbol(char c) {
        this.c = (byte) c;
    }

    public static TSymbol valueOf(int b) {
        for (TSymbol token : values()) {
            if (token.c == b)
                return token;
        }
        return null;
    }

    @Override
    public String toString() {
        return new String(new byte[] { (byte) c });
    }
}
