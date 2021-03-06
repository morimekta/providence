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
package net.morimekta.providence.util;

import javax.annotation.Nonnull;

/**
 * Enum containing known "general" thrift annotations. Note that annotation
 * tags are case sensitive, values depends on the implementation details.
 */
public enum ThriftAnnotation {
    NONE(null),

    /**
     * Whether a set-like container are normal (hash-), ordered (linked-hash-)
     * or sorted (tree-). Valid for set and map type fields.
     * <p>
     * <code>container = "ordered"</code>
     * <p>
     * See the {@link ThriftContainer} enum for valid values, and how it is
     * handled.
     */
    CONTAINER("container"),

    /**
     * If the field, message, service or method is not supposed to be used any
     * more. Whatever is in the value part may be used as the 'deprecated'
     * reason.
     * <p>
     * <code>deprecated = "For some reason"</code>
     */
    DEPRECATED("deprecated"),

    /**
     * If a struct can use a compact serialized format. Only valid for struct,
     * not for union or exception.
     * <p>
     * <code>json.compact = ""</code>
     */
    JSON_COMPACT("json.compact"),

    /**
     * Specify which field to use for the exception message. The default is
     * to use the 'message' field, and if neither is set, then a toString
     * variant will be used as the message.
     * <p>
     * <code>message.field = "field_name"</code>
     */
    MESSAGE_FIELD("message.field"),

    /**
     * Add extra interfaces to a struct. Not allowed on unions or exceptions.
     *
     * The interface methods must either match the methods implemented by the
     * struct, <b>or</b> have a default implementation.
     * <p>
     * <code>java.implements = "my.package.MyInterface"</code>
     */
    JAVA_IMPLEMENTS("java.implements"),

    /**
     * Specify exception class to extend for exception structs. The default is
     * to extend {@link java.lang.Exception}, this will override that verbatim.
     * The exception class <b>must</b> be available at compile time.
     * <p>
     * <code>java.exception.class = "my.package.MyException"</code>
     */
    JAVA_EXCEPTION_CLASS("java.exception.class"),

    /**
     * For struct and exception. If true will add a public create constructor
     * with all fields available as parameters. Default is not.
     * <p>
     * <code>java.public.constructor = ""</code>
     */
    JAVA_PUBLIC_CONSTRUCTOR("java.public.constructor"),

    /**
     * Specify an exception class to throw <b>instead</b> of the default
     * declared exceptions. This is only valid for the Service.Iface interface,
     * and not for the Service.Client implementation.
     * <p>
     * Non-declared exceptions, even if extending the declared exception, will
     * be handled as a system failure, and wrapped in an IOException or sent as
     * an application exception.
     * <p>
     * <code>java.service.methods.throws = "my.package.MyException"</code>
     */
    JAVA_SERVICE_METHOD_THROWS("java.service.methods.throws"),

    /**
     * Specify if a file should generate with support with hazelcast portable
     * implementation.
     * <p>
     * The id needs to be a unique int id for each program that is generated with
     * support for hazelcast.
     * </p>
     * <code>hazelcast.factory.id = "1"</code>
     */
    JAVA_HAZELCAST_FACTORY_ID("hazelcast.factory.id"),

    /**
     * Specify if a struct should be generated using hazelcast.portable interface
     * to be compatible with serialization and deserialization of hazelcast portable.
     * <p>
     * The id needs to be a unique int id for each struct in the program.
     * </p>
     * <code>hazelcast.factory.id = "1"</code>
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
