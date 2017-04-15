/*
 * Copyright 2016 Providence Authors
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

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Descriptor for a complete service.
 */
public abstract class PService {
    private final String                               name;
    private final String                               programName;
    private final PServiceProvider                     extendsService;
    private final Collection<? extends PServiceMethod> methods;

    public PService(@Nonnull String programName,
                    @Nonnull String name,
                    @Nullable PServiceProvider extendsService,
                    @Nonnull Collection<? extends PServiceMethod> methods) {
        this.name = name;
        this.programName = programName;
        this.extendsService = extendsService;
        this.methods = methods;
    }

    public PService(@Nonnull String programName,
                    @Nonnull String name,
                    @Nullable PServiceProvider extendsService,
                    @Nonnull PServiceMethod[] methods) {
        this(programName, name, extendsService, ImmutableList.copyOf(methods));
    }

    /**
     * @return Get the program name for the service.
     */
    @Nonnull
    public String getProgramName() {
        return programName;
    }

    /**
     * @return Get the name of the service.
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * @param programContext The relative program context.
     * @return Get the qualified name or relative name given the current program context.
     */
    @Nonnull
    public String getQualifiedName(String programContext) {
        if (programName.equals(programContext)) {
            return name;
        }
        return programName + "." + name;
    }

    /**
     * @return Get the qualified name for the service.
     */
    @Nonnull
    public String getQualifiedName() {
        return getQualifiedName(null);
    }

    /**
     * Get the service that this service extends.
     *
     * @return Extended service or null if none.
     */
    @Nullable
    public PService getExtendsService() {
        if (extendsService != null) {
            return extendsService.getService();
        }
        return null;
    }

    /**
     * Get the collection of methods for the service, including all inherited
     * services.
     *
     * @return The collection of methods.
     */
    @Nonnull
    public Collection<? extends PServiceMethod> getMethods() {
        return methods;
    }

    /**
     * Get the method definition for the given method name.
     *
     * @param name The service method name.
     * @return The service method.
     */
    @Nullable
    public abstract PServiceMethod getMethod(String name);
}
