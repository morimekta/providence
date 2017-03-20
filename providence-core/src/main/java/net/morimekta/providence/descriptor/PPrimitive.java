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
package net.morimekta.providence.descriptor;

import net.morimekta.providence.PType;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Descriptors for primitive types.
 * <p>
 * These are the basic types used in the thrift structure except containers.
 */
public class PPrimitive implements PDescriptor {
    public static final PPrimitive VOID   = new PPrimitive(PType.VOID, null);
    public static final PPrimitive BOOL   = new PPrimitive(PType.BOOL, false);
    public static final PPrimitive BYTE   = new PPrimitive(PType.BYTE, (byte) 0);
    public static final PPrimitive I16    = new PPrimitive(PType.I16, (short) 0);
    public static final PPrimitive I32    = new PPrimitive(PType.I32, 0);
    public static final PPrimitive I64    = new PPrimitive(PType.I64, (long) 0);
    public static final PPrimitive DOUBLE = new PPrimitive(PType.DOUBLE, 0.0);
    public static final PPrimitive STRING = new PPrimitive(PType.STRING, null);
    public static final PPrimitive BINARY = new PPrimitive(PType.BINARY, null);

    private final PPrimitiveProvider mProvider;
    private final PType              mType;
    private final Object             mDefault;

    private PPrimitive(PType type, Object defValue) {
        mType = type;
        mProvider = new PPrimitiveProvider(this);
        mDefault = defValue;
    }

    /**
     * Get the descriptor provider for the primitive.
     *
     * @return The descriptor provider.
     */
    @Nonnull
    public PPrimitiveProvider provider() {
        return mProvider;
    }

    @Override
    public String getProgramName() {
        return null;
    }

    @Nonnull
    @Override
    public String getName() {
        return mType.name;
    }

    @Nonnull
    @Override
    public String getQualifiedName(String programContext) {
        return mType.name;
    }

    @Override
    public String toString() {
        return mType.name;
    }

    @Nonnull
    @Override
    public PType getType() {
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

    /**
     * @return The default value for the primitive.
     */
    public Object getDefaultValue() {
        return mDefault;
    }

    /**
     * Find primitive by name.
     * @param name The name of the primitive.
     * @return The primitive descriptor.
     */
    public static PPrimitive findByName(String name) {
        switch (name) {
            case "void":
                return VOID;
            case "bool":
                return BOOL;
            case "byte":
            case "i8":
                return BYTE;
            case "i16":
                return I16;
            case "i32":
                return I32;
            case "i64":
                return I64;
            case "double":
                return DOUBLE;
            case "string":
                return STRING;
            case "binary":
                return BINARY;
        }
        return null;
    }
}
