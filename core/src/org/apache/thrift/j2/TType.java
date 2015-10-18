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

package org.apache.thrift.j2;

/**
 * Value types.
 *
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 25.08.15
 */
public enum TType {
    STOP(0, "stop"),
    VOID(1, "void"),
    BOOL(2, "bool"),
    BYTE(3, "byte"),
    DOUBLE(4, "double"),
    // no 5
    I16(6, "i16"),
    // no 7
    I32(8, "i32"),
    // no 9
    I64(10, "i64"),
    STRING(11, "string"),
    BINARY(11, "binary"),  // encodes as string.
    MESSAGE(12, "struct"),
    MAP(13, "map"),
    SET(14, "set"),
    LIST(15, "list"),
    ENUM(8, "enum"),  // encodes as i32
    ;

    // Thrift serialized type ID number.
    public final byte id;
    public final String name;

    TType(int typeId, String name) {
        this.id = (byte) typeId;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static TType findById(byte id) {
        for (TType group : values()) {
            if (id == group.id) {
                return group;
            }
        }
        return STOP;
    }

    public static TType findByName(String name) {
        for (TType group : values()) {
            if (name.equals(group.name)) {
                return group;
            }
        }
        return STOP;
    }
}
