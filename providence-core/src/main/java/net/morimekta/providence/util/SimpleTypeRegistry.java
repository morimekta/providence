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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for declared types referenced in a flat space program context.
 * The registry itself does not have a context per se, as these may
 * reference each other recursively.
 */
public class SimpleTypeRegistry extends BaseTypeRegistry {
    private final Map<String, PDeclaredDescriptor<?>> declaredTypes;
    private final Map<String, PService>               services;
    private final Set<String>                         knownPrograms;

    public SimpleTypeRegistry() {
        this.declaredTypes = new LinkedHashMap<>();
        this.services      = new HashMap<>();
        this.knownPrograms = new HashSet<>();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PDeclaredDescriptor<T>> T getDeclaredType(@Nonnull String typeName,
                                                                @Nonnull String programContext) {
        String finalName = finalTypename(typeName, programContext);
        if (declaredTypes.containsKey(finalName)) {
            return (T) declaredTypes.get(finalName);
        }

        String program = finalName.replaceAll("\\..*", "");
        String name = finalName.replaceAll(".*\\.", "");

        if (knownPrograms.contains(program)) {
            throw new IllegalArgumentException(
                    "No such type \"" + name + "\" in program \"" + program + "\"");
        } else {
            throw new IllegalArgumentException(
                    "No such program \"" + program + "\" known for type \"" + typeName + "\"");
        }
    }

    @Nonnull
    @Override
    public PService getService(String serviceName, String programContext) {
        String finalName = qualifiedNameFromIdAndContext(serviceName, programContext);
        if (services.containsKey(finalName)) {
            return services.get(finalName);
        }

        String program = finalName.replaceAll("\\..*", "");
        String name = finalName.replaceAll(".*\\.", "");

        if (knownPrograms.contains(program)) {
            throw new IllegalArgumentException("No such service \"" + name + "\" in program \"" + program + "\"");
        } else {
            throw new IllegalArgumentException("No such program \"" + program + "\" exists for service \"" + serviceName + "\"");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean register(@Nonnull PService service) {
        // Services cannot be aliased with typedefs, so no need to resolve the
        // qualified or final name.
        String declaredTypeName = service.getQualifiedName();

        if (!services.containsKey(declaredTypeName)) {
            services.put(declaredTypeName, service);
            knownPrograms.add(service.getProgramName());
            return true;
        }
        return false;
    }

    @Override
    public <T> boolean register(PDeclaredDescriptor<T> declaredType) {
        String declaredTypeName = declaredType.getQualifiedName();
        if (declaredTypes.containsKey(declaredTypeName)) {
            return false;
        }

        declaredTypes.put(declaredTypeName, declaredType);
        knownPrograms.add(declaredType.getProgramName());
        return true;
    }
}
