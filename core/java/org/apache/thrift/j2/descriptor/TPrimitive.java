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

package org.apache.thrift.j2.descriptor;

import org.apache.thrift.j2.TBinary;
import org.apache.thrift.j2.TType;

import java.util.Objects;

/**
 * Descriptors for primitive types.
 *
 * These are the basic types used in the thrift structure except containers.
 */
public class TPrimitive<T> implements TDescriptor<T> {
    public static final TPrimitive<Void>    VOID   = new TPrimitive<>(TType.VOID, null);
    public static final TPrimitive<Boolean> BOOL   = new TPrimitive<>(TType.BOOL, false);
    public static final TPrimitive<Byte>    BYTE   = new TPrimitive<>(TType.BYTE, (byte) 0);
    public static final TPrimitive<Short>   I16    = new TPrimitive<>(TType.I16, (short) 0);
    public static final TPrimitive<Integer> I32    = new TPrimitive<>(TType.I32, 0);
    public static final TPrimitive<Long>    I64    = new TPrimitive<>(TType.I64, (long) 0);
    public static final TPrimitive<Double>  DOUBLE = new TPrimitive<>(TType.DOUBLE, 0.0);
    public static final TPrimitive<String>  STRING = new TPrimitive<>(TType.STRING, null);
    public static final TPrimitive<TBinary> BINARY = new TPrimitive<>(TType.BINARY, null);

    private final TPrimitiveProvider<T> mProvider;
    private final TType                 mType;
    private final T                     mDefault;

    private TPrimitive(TType type, T defValue) {
        mType = type;
        mProvider = new TPrimitiveProvider<>(this);
        mDefault = defValue;
    }

    public TPrimitiveProvider<T> provider() {
        return mProvider;
    }

    @Override
    public String getPackageName() {
        return null;
    }

    @Override
    public String getName() {
        return mType.name;
    }

    @Override
    public String getQualifiedName(String packageName) {
        return mType.name;
    }

    @Override
    public String toString() {
        return mType.name;
    }

    @Override
    public TType getType() {
        return mType;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mType, mDefault);
    }

    public T getDefaultValue() {
        return mDefault;
    }

    public static TPrimitive<?> findByName(String name) {
        switch (name) {
            case "void": return VOID;
            case "bool": return BOOL;
            case "byte": return BYTE;
            case "i16": return I16;
            case "i32": return I32;
            case "i64": return I64;
            case "double": return DOUBLE;
            case "string": return STRING;
            case "binary": return BINARY;
        }
        return null;
    }
}
