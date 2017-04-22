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
 * Value type enum. Each of the different types are handled differently
 * both in generated code and the various serializers.
 */
public enum PType {
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
}
