/*
 * Copyright 2015 Providence Authors
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
package net.morimekta.providence;

import javax.annotation.Nonnull;

/**
 * Value type constants. The ID matches the type ID used in the binary
 * protocol, which is the thrift default.
 *
 * TODO: It might be preferable to decouple the type ID from the types
 * themselves.
 */
public enum PType {
    STOP("stop"),
    VOID("void"),
    BOOL("bool"),
    BYTE("byte"),
    I16("i16"),
    I32("i32"),
    I64("i64"),
    DOUBLE("double"),
    STRING("string"),
    BINARY("binary"),
    ENUM("enum"),
    // Called 'struct' in apache thrift.
    MESSAGE("message"),
    MAP("map"),
    SET("set"),
    LIST("list"),
    ;

    private final String name;

    PType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Find the best matching type for a type name.
     *
     * @param name The name of the type.
     * @return The type enum value.
     */
    @Nonnull
    public static PType findByName(String name) {
        switch (name) {
            case "stop":
                return STOP;
            case "void":
                return VOID;
            case "bool":
                return BOOL;
            case "byte":
                return BYTE;
            case "double":
                return DOUBLE;
            case "i8":
                return BYTE;
            case "i16":
                return I16;
            case "i32":
                return I32;
            case "i64":
                return I64;
            case "binary":
                return BINARY;
            case "string":
                return STRING;
            case "struct":
                return MESSAGE;
            case "union":
                return MESSAGE;
            case "exception":
                return MESSAGE;
            case "map":
                return MAP;
            case "set":
                return SET;
            case "list":
                return LIST;
            case "enum":
                return ENUM;
            default:
                return STOP;
        }
    }

    /**
     * Get the type name for a given type ID, or just the ID if now known.
     *
     * @param id The type ID.
     * @return The type name (best effort).
     */
    @Nonnull
    public static String nameForId(byte id) {
        switch (id) {
            case 0:
                return "stop";
            case 1:
                return "void";
            case 2:
                return "bool";
            case 3:
                return "byte";
            case 4:
                return "double";
            // case 5:
            case 6:
                return "i16";
            // case 7:
            case 8:
                // ENUM is same as I32.
                return "i32";
            // case 9:
            case 10:
                return "i64";
            case 11:
                // BINARY is same as STRING.
                return "string";
            case 12:
                return "message";
            case 13:
                return "map";
            case 14:
                return "set";
            case 15:
                return "list";
            default:
                return String.valueOf(id);
        }
    }
}
