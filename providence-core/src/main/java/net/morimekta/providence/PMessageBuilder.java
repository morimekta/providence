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
import net.morimekta.providence.descriptor.PStructDescriptor;

import javax.annotation.CheckReturnValue;
import javax.annotation.meta.When;

/**
 * Base class for message builders.
 */
public abstract class PMessageBuilder<T extends PMessage<T, F>, F extends PField> implements PBuilder<T> {
    /**
     * Checks if the current set data is enough to make a valid struct. It
     * will check for all required fields, and if any are missing it will
     * return false.
     *
     * @return True for a valid message.
     */
    public abstract boolean isValid();

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
     * @param key The field key.
     * @param value The field value.
     * @return The message builder.
     */
    @CheckReturnValue(when = When.NEVER)
    public abstract PMessageBuilder<T, F> set(int key, Object value);

    /**
     * Checks if a specific field is set on the builder.
     *
     * @param key The key of the field to check.
     * @return True if the field is set.
     */
    public abstract boolean isSet(int key);

    /**
     * Adds a value to a set or list container.
     *
     * @param key The field key.
     * @param value The field value to add.
     * @return The message builder.
     * @throws IllegalArgumentException if the field is not a list or set.
     */
    @CheckReturnValue(when = When.NEVER)
    public abstract PMessageBuilder<T, F> addTo(int key, Object value);

    /**
     * clear the provided field value.
     *
     * @param key The field key.
     * @return The message builder.
     */
    @CheckReturnValue(when = When.NEVER)
    public abstract PMessageBuilder<T, F> clear(int key);

    /**
     * Merges the provided message into the builder. Contained messages should
     * in turn be merged and not replaced wholesale. Sets are unioned (addAll)
     * and maps will overwrite / replace on a per-key basis (putAll).
     *
     * @param from The message to merge values from.
     */
    @CheckReturnValue(when = When.NEVER)
    public abstract PMessageBuilder<T, F> merge(T from);

    /**
     * Get the builder for the given message contained in this builder. If
     * the sub-builder does not exist, create, either from existing instance
     * or from scratch.
     *
     * @param key The field key.
     * @return The builder.
     */
    public abstract PMessageBuilder mutator(int key);

    /**
     * Get the descriptor for the message being built.
     *
     * @return The struct descriptor.
     */
    public abstract PStructDescriptor<T, F> descriptor();
}
