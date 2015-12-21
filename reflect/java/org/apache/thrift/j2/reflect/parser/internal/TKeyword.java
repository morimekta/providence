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
 * Enum with reserved words (tokens) for easy reference.
 *
 * @author Stein Eldar Johnsen
 * @since 21.09.15
 */
public enum TKeyword {
    // -- header
    INCLUDE("include"),
    NAMESPACE("namespace"),

    // -- types
    ENUM("enum"),
    STRUCT("struct"),
    UNION("union"),
    EXCEPTION("exception"),
    SERVICE("service"),
    CONST("const"),
    TYPEDEF("typedef"),

    // -- modifiers
    REQUIRED("required"),
    OPTIONAL("optional"),
    ONEWAY("oneway"),
    THROWS("throws"),
    EXTENDS("extends"),
    VOID("void"),

    // -- types
    BOOL("bool"),
    BYTE("byte"),
    I16("i16"),
    I32("i32"),
    I64("i64"),
    DOUBLE("double"),
    STRING("string"),
    BINARY("binary"),

    // -- containers
    LIST("list"),
    SET("set"),
    MAP("map"),

    // -- deprecated types
    SENUM("senum"),
    SLIST("slist"),

    // -- not-used reserved words

    PUBLIC("public"),
    PROTECTED("protected"),
    PRIVATE("private"),

    JAVA_LINE_COMMENT_START("//"),
    BLOCK_COMMENT_START("/*"),
    BLOCK_COMMENT_END("*/"),
    ;

    public String keyword;

    TKeyword(String keyword) {
        this.keyword = keyword;
    }

    public static TKeyword getByToken(String token) {
        for (TKeyword keyword : values()) {
            if (keyword.keyword.equals(token)) {
                return keyword;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return keyword;
    }
}
