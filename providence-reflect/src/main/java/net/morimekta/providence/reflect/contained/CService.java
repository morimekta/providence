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
package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceProvider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Descriptor for a complete service.
 */
public class CService extends PService implements CAnnotatedDescriptor {
    private final Map<String, String> annotations;
    private String                    documentation;

    public CService(String documentation,
                    String programName,
                    String name,
                    PServiceProvider extendsService,
                    Collection<CServiceMethod> methods,
                    Map<String, String> annotations) {
        super(programName, name, extendsService, methods);
        this.documentation = documentation;
        this.annotations = annotations == null
                           ? Collections.EMPTY_MAP
                           : ImmutableMap.copyOf(annotations);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public Collection<CServiceMethod> getMethods() {
        return (Collection<CServiceMethod>) super.getMethods();
    }

    @Override
    public CServiceMethod getMethod(String name) {
        for (CServiceMethod method : getMethods()) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        if (getExtendsService() != null) {
            return getExtendsService().getMethod(name);
        }
        return null;
    }

    @Override
    public CService getExtendsService() {
        return (CService) super.getExtendsService();
    }

    /**
     * Get all methods including methods declared in extended services.
     *
     * @return The list of service methods.
     */
    public Collection<CServiceMethod> getMethodsIncludingExtended() {
        CService extended = getExtendsService();
        if (extended == null) {
            return getMethods();
        }
        List<CServiceMethod> out = new ArrayList<>();
        out.addAll(extended.getMethodsIncludingExtended());
        out.addAll(getMethods());
        return ImmutableList.copyOf(out);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getAnnotations() {
        return annotations.keySet();
    }

    @Override
    public boolean hasAnnotation(@Nonnull String name) {
        return annotations.containsKey(name);
    }

    @Override
    public String getAnnotationValue(@Nonnull String name) {
        return annotations.get(name);
    }

    @Override
    public String getDocumentation() {
        return documentation;
    }
}
