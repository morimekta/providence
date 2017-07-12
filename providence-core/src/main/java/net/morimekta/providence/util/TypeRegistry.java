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
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceProvider;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Basic type registry used in managing content.
 */
public interface TypeRegistry {
    /**
     * Get the declared type with the given name and program context.
     *
     * @param typeName       The type name.
     * @param programContext The program context of the type.
     * @param <T>            The described type.
     * @return The declared type descriptor.
     */
    @Nonnull
    <T extends PDeclaredDescriptor<T>> T getDeclaredType(@Nonnull String typeName,
                                                         @Nonnull String programContext);

    /**
     * Get a declared type by its qualified type name.
     *
     * @param typeName The type name.
     * @param <T>      The described type.
     * @return The type descriptor.
     */
    @Nonnull
    default <T extends PDeclaredDescriptor<T>> T getDeclaredType(@Nonnull String typeName) {
        String[] parts = typeName.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Requesting global type name without program name: \"" + typeName + "\"");
        } else if (parts.length > 2) {
            throw new IllegalArgumentException("Invalid identifier: \"" + typeName + "\"");
        }
        return getDeclaredType(parts[1], parts[0]);
    }

    /**
     * Get a service definition from the name and program context.
     *
     * @param serviceName    The service name.
     * @param programContext The program context name to get the service in.
     * @return The service or null if not found.
     */
    @Nonnull
    PService getService(String serviceName, String programContext);

    /**
     * Get a service definition from it's qualified service name.
     *
     * @param serviceName The service name.
     * @return the service or null of not found.
     */
    @Nonnull
    default PService getService(String serviceName) {
        String[] parts = serviceName.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Requesting global service name without package: \"" + serviceName + "\"");
        } else if (parts.length > 2) {
            throw new IllegalArgumentException("Invalid identifier: \"" + serviceName + "\"");
        }
        return getService(parts[1], parts[0]);
    }

    /**
     * Given a type name and a package context, fetches the type provider for
     * the given type.
     *
     * @param typeName       Name of type, without any spaces.
     * @param programContext The program context of the reference.
     * @param annotations    Annotations affecting the type.
     * @return The type provider.
     */
    PDescriptorProvider getProvider(String typeName, final String programContext, Map<String, String> annotations);

    /**
     * Given a service name and program context, fetches the service provider
     * for the given service.
     *
     * @param serviceName    Name of the service.
     * @param programContext The program context of the reference.
     * @return The service provider.
     */
    PServiceProvider getServiceProvider(final String serviceName, final String programContext);
}
