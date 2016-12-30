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

/**
 * Value type constants. The ID matches the type ID used in the binary
 * protocol, which is the thrift default.
 *
 * TODO: It might be preferable to decouple the type ID from the types
 * themselves.
 */
public enum PType {
    STOP(0, "stop"),
    VOID(1, "void"),
    BOOL(2, "bool"),
    BYTE(3, "byte"),
    I16(6, "i16"),
    I32(8, "i32"),
    I64(10, "i64"),
    DOUBLE(4, "double"),
    STRING(11, "string"),
    // encodes as string.
    BINARY(11, "binary"),
    // encodes as i32
    ENUM(8, "enum"),
    // Called 'struct' in apache thrift.
    MESSAGE(12, "message"),
    MAP(13, "map"),
    SET(14, "set"),
    LIST(15, "list"),;

    // Thrift serialized type ID number.
    public final byte   id;
    public final String name;

    PType(int typeId, String name) {
        this.id = (byte) typeId;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static PType findById(byte id) {
        switch (id) {
            case 0:
                return STOP;
            case 1:
                return VOID;
            case 2:
                return BOOL;
            case 3:
                return BYTE;
            case 4:
                return DOUBLE;
            // case 5:
            case 6:
                return I16;
            // case 7:
            case 8:
                // ENUM is same as I32.
                return I32;
            // case 9:
            case 10:
                return I64;
            case 11:
                // BINARY is same as STRING.
                return STRING;
            case 12:
                return MESSAGE;
            case 13:
                return MAP;
            case 14:
                return SET;
            case 15:
                return LIST;
            default:
                return STOP;
        }
    }

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
}
