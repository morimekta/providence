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

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.PType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Descriptor base class for all messages.
 */
public abstract class PMessageDescriptor<T extends PMessage<T, F>, F extends PField> extends PDeclaredDescriptor<T> {
    private final Supplier<PMessageBuilder<T, F>> builderSupplier;
    private final boolean                         simple;

    public PMessageDescriptor(String programName,
                              String name,
                              Supplier<PMessageBuilder<T, F>> builderSupplier,
                              boolean simple) {
        super(programName, name);

        this.builderSupplier = builderSupplier;
        this.simple = simple;
    }

    /**
     * @return An unmodifiable list of fields that the struct holds.
     */
    @Nonnull
    public abstract F[] getFields();

    /**
     * @param name Name of field to get.
     * @return The field if present.
     */
    @Nullable
    public abstract F findFieldByName(String name);

    /**
     * @param id The ID of the field to get.
     * @return The field if present.
     */
    @Nullable
    public abstract F findFieldById(int id);

    /**
     * @param name The name of the field to get.
     * @return The field.
     * @throws IllegalArgumentException If not present.
     */
    @Nonnull
    public F fieldForName(String name) {
        F field = findFieldByName(name);
        if (field == null) {
            throw new IllegalArgumentException("No field \"" + name + "\" in " + getQualifiedName());
        }
        return field;
    }

    /**
     * @param id The ID of the field to get.
     * @return The field.
     * @throws IllegalArgumentException If not present.
     */
    @Nonnull
    public F fieldForId(int id) {
        F field = findFieldById(id);
        if (field == null) {
            throw new IllegalArgumentException("No field key " + id + " in " + getQualifiedName());
        }
        return field;
    }

    /**
     * @param name Field name.
     * @return Field or null.
     * @deprecated Use {@link #findFieldByName(String)} instead.
     */
    @Deprecated
    public F getField(String name) {
        return findFieldByName(name);
    }

    /**
     * @param key Field key.
     * @return Field or null.
     * @deprecated Use {@link #findFieldById(int)} instead.
     */
    @Deprecated
    public F getField(int key) {
        return findFieldById(key);
    }

    /**
     * @return The struct variant.
     */
    @Nonnull
    public abstract PMessageVariant getVariant();

    /**
     * @return True if the message is simple. A simple message contains no
     *         containers, and no sub-messages.
     */
    public boolean isSimple() {
        return simple;
    }

    @Nonnull
    @Override
    public PType getType() {
        return PType.MESSAGE;
    }

    @Nonnull
    @Override
    public PMessageBuilder<T, F> builder() {
        return builderSupplier.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o.getClass().equals(getClass()))) {
            return false;
        }
        PMessageDescriptor<?, ?> other = (PMessageDescriptor<?, ?>) o;
        if (!getQualifiedName().equals(other.getQualifiedName()) ||
            !getVariant().equals(other.getVariant()) ||
            getFields().length != other.getFields().length) {
            return false;
        }
        for (PField field : getFields()) {
            if (!field.equals(other.findFieldById(field.getId()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(getClass(),
                                getQualifiedName(),
                                getVariant());
        for (PField field : getFields()) {
            hash *= 28547;
            hash += Objects.hash(hash, field.hashCode());
        }
        return hash;
    }

    /**
     * Get the actual builder builderSupplier instance. For contained structs only.
     * @return The builder builderSupplier.
     */
    protected Supplier<PMessageBuilder<T, F>> getBuilderSupplier() {
        return builderSupplier;
    }
}
