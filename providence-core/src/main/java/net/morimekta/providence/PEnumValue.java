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

import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.util.Numeric;
import net.morimekta.util.Stringable;

import javax.annotation.Nonnull;

/**
 * Base interface for enum values.
 */
public interface PEnumValue<T> extends PValue<T>, Stringable, Numeric {
    /**
     * Method for getting the ID of the value that is a bit more accurate
     * when reading code than {@link #asInteger()}
     *
     * @return The ID value for the enum.
     */
    default int getId() {
        return asInteger();
    }

    /**
     * Method for getting the name of the value that is a bit more accurate
     * when reading code than {@link #asString()}
     *
     * @return The name of the enum value.
     */
    @Nonnull
    default String getName() {
        return asString();
    }

    /**
     * @return The numeric value for the enum.
     * @deprecated since 0.5.0 Use {@link #asInteger()} or {@link #getId()} instead.
     *             Will be removed in a future version, but kept for now as it is
     *             too widely used to be removed immediately.
     */
    @Deprecated
    default int getValue() {
        return asInteger();
    }

    @Nonnull
    @Override
    PEnumDescriptor descriptor();
}
