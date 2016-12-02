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

import java.util.Collection;

/**
 * Descriptor for a complete service.
 */
public class PService {
    private final String                               name;
    private final String                               programName;
    private final PServiceProvider                     extendsService;
    private final Collection<? extends PServiceMethod> methods;

    public PService(String programName,
                    String name,
                    PServiceProvider extendsService,
                    Collection<? extends PServiceMethod> methods) {
        this.name = name;
        this.programName = programName;
        this.extendsService = extendsService;
        this.methods = methods;
    }

    public PService(String programName,
             String name,
             PServiceProvider extendsService,
             PServiceMethod[] methods) {
        this.name = name;
        this.programName = programName;
        this.extendsService = extendsService;
        this.methods = ImmutableList.copyOf(methods);
    }

    public String getProgramName() {
        return programName;
    }

    public String getName() {
        return name;
    }

    public String getQualifiedName(String packageContext) {
        if (programName.equals(packageContext)) {
            return name;
        }
        return programName + "." + name;
    }

    public String getQualifiedName() {
        return getQualifiedName(null);
    }

    public PService getExtendsService() {
        if (extendsService != null) {
            return extendsService.getService();
        }
        return null;
    }

    public Collection<? extends PServiceMethod> getMethods() {
        return methods;
    }

    public PServiceMethod getMethod(String name) {
        for (PServiceMethod method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        if (extendsService != null) {
            return extendsService.getService().getMethod(name);
        }
        return null;
    }
}
