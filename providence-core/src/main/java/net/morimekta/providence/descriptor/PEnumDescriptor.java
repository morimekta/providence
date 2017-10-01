/*
 * Copyright 2015-2016 Providence Authors
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

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The definition of a thrift enum.
 */
public abstract class PEnumDescriptor<T extends PEnumValue<T>> extends PDeclaredDescriptor<T> {
    // According to doc it's 1, but the current c++ compiler makes it 0...
    public static final int DEFAULT_FIRST_VALUE = 0;

    private final Supplier<PEnumBuilder<T>> builderSupplier;

    public PEnumDescriptor(String packageName, String name, Supplier<PEnumBuilder<T>> provider) {
        super(packageName, name);
        builderSupplier = provider;
    }

    @Nonnull
    @Override
    public PType getType() {
        return PType.ENUM;
    }

    /**
     * @return The array of enum instances.
     */
    @Nonnull
    public abstract T[] getValues();

    /**
     * @param id Value to look up enum from.
     * @return Enum if found, null otherwise.
     */
    @Nullable
    public abstract T findById(int id);

    /**
     * @param name Name to look up enum from.
     * @return Enum if found, null otherwise.
     */
    @Nullable
    public abstract T findByName(String name);

    /**
     * @param id Value to look up enum from.
     * @return The enum value.
     * @throws IllegalArgumentException If value not found.
     */
    @Nonnull
    public T valueForId(int id) {
        T value = findById(id);
        if (value == null) {
            throw new IllegalArgumentException("No " + getQualifiedName() + " for id " + id);
        }
        return value;
    }

    /**
     * @param name Name to look up enum from.
     * @return The enum value.
     * @throws IllegalArgumentException If value not found.
     */
    @Nonnull
    public T valueForName(String name) {
        T value = findByName(name);
        if (value == null) {
            throw new IllegalArgumentException("No " + getQualifiedName() + " for name \"" + name + "\"");
        }
        return value;
    }

    @Nonnull
    @Override
    public PEnumBuilder<T> builder() {
        return builderSupplier.get();
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o.getClass().equals(getClass()))) {
            return false;
        }
        PEnumDescriptor<?> other = (PEnumDescriptor<?>) o;
        if (!getQualifiedName().equals(other.getQualifiedName()) ||
            getValues().length != other.getValues().length) {
            return false;
        }
        for (PEnumValue<?> value : getValues()) {
            PEnumValue<?> ovI = other.findById(value.asInteger());
            if (ovI != null && !value.asString().equals(ovI.asString())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(PEnumDescriptor.class, getQualifiedName());
    }

    protected Supplier<PEnumBuilder<T>> getBuilderSupplier() {
        return builderSupplier;
    }
}
