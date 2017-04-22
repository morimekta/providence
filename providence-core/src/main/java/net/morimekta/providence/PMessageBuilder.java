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
package net.morimekta.providence;

import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Base class for message builders.
 */
public abstract class PMessageBuilder<T extends PMessage<T, F>, F extends PField>
        implements PBuilder<T> {
    /**
     * Checks if the current set data is enough to make a valid struct. It
     * will check for all required fields, and if any are missing it will
     * return false.
     *
     * @return True for a valid message.
     */
    public abstract boolean valid();

    /**
     * Checks if the current set data is enough to make a valid struct. It
     * will check for all required fields, and if any are missing it will
     * throw an {@link IllegalStateException} with an appropriate error
     * message.
     *
     * @throws IllegalStateException When the builder will not generate a
     *         valid message model object.
     */
    public abstract void validate() throws IllegalStateException;

    /**
     * Set the provided field value.
     *
     * @param key The key of the field to set.
     * @param value The value to be set.
     * @return The message builder.
     */
    @Nonnull
    public abstract PMessageBuilder<T, F> set(int key, Object value);

    /**
     * Set the provided field value.
     *
     * @param field The field to set.
     * @param value The value to be set.
     * @return True if the field is set.
     */
    @Nonnull
    public PMessageBuilder<T, F> set(@Nonnull F field, Object value) {
        return set(field.getKey(), value);
    }

    /**
     * Checks if a specific field is set on the builder.
     *
     * @param key The key of the field to check.
     * @return True if the field is set.
     */
    public abstract boolean isSet(int key);

    /**
     * Checks if a specific field is set on the builder.
     *
     * @param field The field to check.
     * @return True if the field is set.
     */
    public boolean isSet(@Nonnull F field) {
        return isSet(field.getKey());
    }

    /**
     * Get a Collection of F with fields set on the builder.
     *
     * Unusual naming because it avoids conflict with generated methods.
     *
     * @return Collection of F
     */
    @Nonnull
    public Collection<F> collectSetFields() {
           return Arrays.stream(descriptor().getFields())
                        .filter(this::isSet)
                        .collect(Collectors.toList());
    }

    /**
     * Checks if a specific field is modified on the builder.
     *
     * @param key The key of the field to check.
     * @return True if the field is modified.
     */
    public abstract boolean isModified(int key);

    /**
     * Checks if a specific field is modified on the builder.
     *
     * @param field The field to check.
     * @return True if the field is modified.
     */
    public boolean isModified(@Nonnull F field) {
        return isModified(field.getKey());
    }

    /**
     * Get a Collection of F with fields Modified since creation of the builder.
     *
     * @return Collection of F
     */
    @Nonnull
    public Collection<F> modifiedFields() {
           return Arrays.stream(descriptor().getFields())
                        .filter(this::isModified)
                        .collect(Collectors.toList());
     }

    /**
     * Adds a value to a set or list container.
     *
     * @param key The key of the container field to add too.
     * @param value The value to add.
     * @return The message builder.
     * @throws IllegalArgumentException if the field is not a list or set.
     */
    @Nonnull
    public abstract PMessageBuilder<T, F> addTo(int key, Object value);

    /**
     * Checks if a specific field is set on the builder.
     *
     * @param field The container field to add too.
     * @param value The value to add.
     * @return True if the field is set.
     */
    @Nonnull
    public PMessageBuilder<T, F> addTo(@Nonnull F field, Object value) {
        return addTo(field.getKey(), value);
    }

    /**
     * Clear the provided field value.
     *
     * @param key The key of the field to clear.
     * @return The message builder.
     */
    @Nonnull
    public abstract PMessageBuilder<T, F> clear(int key);


    /**
     * Clear the provided field value.
     *
     * @param field The field to clear.
     * @return The message builder.
     */
    @Nonnull
    public PMessageBuilder<T, F> clear(@Nonnull F field) {
        return clear(field.getKey());
    }


    /**
     * Merges the provided message into the builder. Contained messages should
     * in turn be merged and not replaced wholesale. Sets are unioned (addAll)
     * and maps will overwrite / replace on a per-key basis (putAll).
     *
     * @param from The message to merge values from.
     * @return The message builder.
     */
    @Nonnull
    public abstract PMessageBuilder<T, F> merge(T from);

    /**
     * Get the builder for the given message contained in this builder. If
     * the sub-builder does not exist, create, either from existing instance
     * or from scratch.
     *
     * @param key The field key.
     * @return The field builder.
     * @throws IllegalArgumentException if field is not a message field.
     */
    @Nonnull
    public abstract PMessageBuilder mutator(int key);

    /**
     * Get the builder for the given message contained in this builder. If
     * the sub-builder does not exist, create, either from existing instance
     * or from scratch.
     *
     * @param field The field to mutate.
     * @return The field builder.
     * @throws IllegalArgumentException if field is not a message field.
     */
    @Nonnull
    public PMessageBuilder mutator(@Nonnull F field) {
        return mutator(field.getKey());
    }

    /**
     * Get the descriptor for the message being built.
     *
     * @return The struct descriptor.
     */
    @Nonnull
    public abstract PMessageDescriptor<T, F> descriptor();
}
