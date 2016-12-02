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

import net.morimekta.providence.descriptor.PDeclaredDescriptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contained document. It contains everything that is parsed out of a single
 * thrift file.
 */
@SuppressWarnings("unused")
public class CProgram {
    private final String                       comment;
    private final String                       programName;
    private final Set<String>                  includedPrograms;
    private final List<String>                 includedFiles;
    private final Map<String, String>          namespaces;
    private final Map<String, String>          typedefs;
    private final List<PDeclaredDescriptor<?>> declaredTypes;
    private final List<CService>               services;
    private final List<CConst>                 constants;

    public CProgram(String comment,
                    String programName,
                    Map<String, String> namespaces,
                    Collection<String> includedPrograms,
                    Collection<String> includedFies,
                    Map<String, String> typedefs,
                    Collection<PDeclaredDescriptor<?>> declaredTypes,
                    Collection<CService> services,
                    Collection<CConst> constants) {
        this.comment = comment;
        this.programName = programName;
        this.namespaces = ImmutableMap.copyOf(namespaces);
        this.includedPrograms = ImmutableSet.copyOf(includedPrograms);
        this.includedFiles = ImmutableList.copyOf(includedFies);
        this.typedefs = ImmutableMap.copyOf(typedefs);
        this.declaredTypes = ImmutableList.copyOf(declaredTypes);
        this.services = ImmutableList.copyOf(services);
        this.constants = ImmutableList.copyOf(constants);
    }

    public String getComment() {
        return comment;
    }

    public String getProgramName() {
        return programName;
    }

    public Set<String> getIncludedPrograms() {
        return includedPrograms;
    }

    public List<String> getIncludedFiles() {
        return includedFiles;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public Map<String, String> getTypedefs() {
        return typedefs;
    }

    public List<PDeclaredDescriptor<?>> getDeclaredTypes() {
        return declaredTypes;
    }

    public List<CService> getServices() {
        return services;
    }

    public List<CConst> getConstants() {
        return constants;
    }

    // --- Extra methods.

    public String getNamespaceForLanguage(String language) {
        return namespaces.get(language);
    }

}
