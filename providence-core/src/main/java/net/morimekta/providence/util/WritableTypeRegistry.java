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
package net.morimekta.providence.util;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PService;

import javax.annotation.Nonnull;

/**
 * Registry for declared types referenced in a specific program context.
 * The registry itself does not have a context per se, as these may
 * reference each other recursively.
 */
public interface WritableTypeRegistry extends TypeRegistry {
    /**
     * Registers a typedef definition.
     *
     * @param identifier The typedef name (the alias) to put.
     * @param program The program context of the typedef.
     * @param target The qualified name that the name represents.
     */
    void registerTypedef(@Nonnull String identifier,
                         @Nonnull String program,
                         @Nonnull String target);

    /**
     * Register a constant value.
     *
     * @param identifier The constant identifier name.
     * @param program The program context for the constant.
     * @param value The parsed value.
     */
    void registerConstant(@Nonnull String identifier,
                          @Nonnull String program,
                          @Nonnull Object value);

    /**
     * Services are not handled as "declared types", so they need to be registered
     * separately.
     *
     * @param service the service to register.
     * @return True if the service was registered and not already known.
     */
    @SuppressWarnings("unchecked")
    boolean register(@Nonnull PService service);

    /**
     * Register the service recursively. E.g. if the service extends a different
     * service, make sure to register that service too.
     *
     * @param service the service to register.
     */
    @SuppressWarnings("unchecked")
    void registerRecursively(@Nonnull PService service);

    /**
     * Register a declared type.
     *
     * @param declaredType The descriptor for the type.
     * @param <T> The declared java type.
     * @return True if the type was registered
     */
    <T> boolean register(PDeclaredDescriptor<T> declaredType);

    /**
     * Register a declared type recursively. If the type is a message, then
     * iterate through the fields and register those types recursively.
     *
     * @param declaredType The descriptor for the type.
     * @param <T> The declared java type.
     */
    <T> void registerRecursively(PDeclaredDescriptor<T> declaredType);
}
