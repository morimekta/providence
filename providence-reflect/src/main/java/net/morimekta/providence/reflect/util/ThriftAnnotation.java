/*
 * Copyright 2016 Providence Authors
 *
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
package net.morimekta.providence.reflect.util;

/**
 * Enum containing known "general" thrift annotations.
 */
public enum ThriftAnnotation {
    NONE(null),
    /**
     * Whether a set-like container are normal (hash-), ordered (linked-hash-)
     * or sorted (tree-). Valid for set and map type fields.
     *
     * container = "ORDERED"
     */
    CONTAINER("container"),

    /**
     * If the field, message, service or method is not supposed to be used any
     * more. Whatever is in the value part may be used as the 'deprecated'
     * reason.
     */
    DEPRECATED("deprecated"),

    /**
     * If a struct can use a compact serialized format. Only valid for struct,
     * not for union and exception.
     *
     * compact = ""
     */
    COMPACT("compact"),

    /**
     * Add extra interfaces to
     */
    JAVA_IMPLEMENTS("java.implements"),

    JAVA_EXCEPTION_CLASS("java.exception.class"),
    ;

    public final String tag;

    ThriftAnnotation(String tag) {
        this.tag = tag;
    }

    public static ThriftAnnotation forTag(String tag) {
        switch (tag) {
            case "collection": return CONTAINER;
            case "compact": return COMPACT;
        }
        return NONE;
    }
}
