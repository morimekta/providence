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

import javax.annotation.Nonnull;

/**
 * Enum containing known "general" thrift annotations.
 */
public enum ThriftAnnotation {
    NONE(null),
    /**
     * Whether a set-like container are normal (hash-), ordered (linked-hash-)
     * or sorted (tree-). Valid for set and map type fields.
     * <p>
     * container = "ORDERED"
     */
    CONTAINER("container"),

    /**
     * If the field, message, service or method is not supposed to be used any
     * more. Whatever is in the value part may be used as the 'deprecated'
     * reason.
     * <p>
     * deprecated = "For some reason"
     */
    DEPRECATED("deprecated"),

    /**
     * If a struct can use a compact serialized format. Only valid for struct,
     * not for union and exception.
     * <p>
     * json.compact = ""
     */
    JSON_COMPACT("json.compact"),

    /**
     * Add extra interfaces to a struct. Not allowed on unions or exceptions.
     *
     * The interface methods must either match the methods implemented by the
     * struct, <b>or</b> have a default implementation.
     * <p>
     * java.implements = "my.package.MyInterface"
     */
    JAVA_IMPLEMENTS("java.implements"),

    /**
     * Specify exception class to extend for exception structs. The default is
     * to extend {@link java.lang.Exception}, this will override that verbatim.
     * The exception class <b>must</b> be available at compile time.
     * <p>
     * java.exception.class = "my.package.MyException"
     */
    JAVA_EXCEPTION_CLASS("java.exception.class"),

    /**
     * Specify an exception class to throw <b>instead</b> of the default
     * declared exceptions. This is only valid for the Service.Iface interface,
     * and not for the Service.Client implementation.
     * <p>
     * Non-declared exceptions, even if extending the declared exception, will
     * be handled as a system failure, and wrapped in an IOException or sent as
     * an application exception.
     * <p>
     * java.service.methods.throws = "my.package.MyException"
     */
    JAVA_SERVICE_METHOD_THROWS("java.service.methods.throws"),
    /**
     * Specify if a file should generate with support with hazelcast portable
     * implementaiton.
     * <p>
     * The id needs to be a unique int id for each file that is generated with
     * support for hazelcast.
     * </p>
     *
     * hazelcast.factory.id = "1"
     */
    JAVA_HAZELCAST_FACTORY_ID("hazelcast.factory.id"),
    /**
     * Specify if a stuct should be generated using hazelcast.portable interface
     * to be compatible with serialization and deserialization of hazelcast portable.
     * <p>
     * The id needs to be a unique int id for each struct in the class.
     * </p>
     * hazelcast.factory.id = "1"
     */
    JAVA_HAZELCAST_CLASS_ID("hazelcast.class.id"),
    ;

    public final String tag;

    ThriftAnnotation(String tag) {
        this.tag = tag;
    }

    @Nonnull
    public static ThriftAnnotation forTag(@Nonnull String tag) {
        for (ThriftAnnotation annotation : values()) {
            if (tag.equals(annotation.tag)) {
                return annotation;
            }
        }
        return NONE;
    }
}
