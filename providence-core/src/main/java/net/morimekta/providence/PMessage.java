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
import net.morimekta.util.Stringable;

import javax.annotation.Nonnull;

/**
 * Base class for all messages.
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public interface PMessage<Message extends PMessage<Message, Field>, Field extends PField>
        extends PValue<Message>, Stringable {
    /**
     * @param key The key of the field.
     * @return Whether the field is present.
     */
    boolean has(int key);

    /**
     * @param field The field.
     * @return Whether the field is present.
     */
    default boolean has(@Nonnull Field field) {
        return has(field.getId());
    }

    /**
     * @param key The key of the field.
     * @param <T> The return type.
     * @return The value of the field.
     */
    <T> T get(int key);

    /**
     * @param field The field.
     * @param <T> The return type.
     * @return Whether the field is present.
     */
    default <T> T get(@Nonnull Field field) {
        return get(field.getId());
    }

    /**
     * Get a builder that extends the current object.
     *
     * @return The builder instance.
     */
    @Nonnull
    PMessageBuilder<Message, Field> mutate();

    /**
     * Shorthand for merging two messages.
     *
     * @param other The message to merge over this messages' values.
     * @return The merged message.
     */
    @Nonnull
    default Message mergeWith(Message other) {
        return mutate().merge(other).build();
    }

    /**
     * Pure string representation of content. Does not contain type info.
     *
     * @return String representation.
     */
    @Nonnull
    @Override
    String asString();

    @Nonnull
    @Override
    PMessageDescriptor<Message, Field> descriptor();
}
