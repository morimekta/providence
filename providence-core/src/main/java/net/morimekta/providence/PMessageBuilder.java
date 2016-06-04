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

package net.morimekta.providence;

/**
 * Base class for message builders.
 */
public abstract class PMessageBuilder<T extends PMessage<T>> implements PBuilder<T> {
    /**
     * Checks if the current set data is enough to make a valid struct. It
     * will check for all required fields, and if any are missing it will
     * return false.
     *
     * @return True for a valid message.
     */
    public abstract boolean isValid();

    /**
     * Set the provided field value.
     *
     * @param key The field key.
     * @param value The field value.
     * @return The message builder.
     */
    public abstract PMessageBuilder<T> set(int key, Object value);

    /**
     * Adds a value to a set or list container.
     *
     * @param key The field key.
     * @param value The field value to add.
     * @return The message builder.
     * @throws IllegalArgumentException if the field is not a list or set.
     */
    public abstract PMessageBuilder<T> addTo(int key, Object value);

    /**
     * clear the provided field value.
     *
     * @param key The field key.
     * @return The message builder.
     */
    public abstract PMessageBuilder<T> clear(int key);

    /**
     * Merges the provided message into the builder. Contained messages should
     * in turn be merged and not replaced wholesale. Sets are unioned (addAll)
     * and maps will overwrite / replace on a per-key basis (putAll).
     *
     * @param from The message to merge values from.
     */
    public abstract PMessageBuilder<T> merge(T from);
}
