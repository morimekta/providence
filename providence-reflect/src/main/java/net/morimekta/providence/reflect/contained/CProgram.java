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

import javax.annotation.Nonnull;
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
    private final String                       programFilePath;
    private final String                       documentation;
    private final String                       programName;
    private final Set<String>                  includedPrograms;
    private final List<String>                 includedFiles;
    private final Map<String, String>          namespaces;
    private final Map<String, String>          typedefs;
    private final List<PDeclaredDescriptor<?>> declaredTypes;
    private final List<CService>               services;
    private final List<CConst>                 constants;

    public CProgram(@Nonnull String programFilePath,
                    String documentation,
                    @Nonnull String programName,
                    Map<String, String> namespaces,
                    Collection<String> includedPrograms,
                    Collection<String> includedFiles,
                    Map<String, String> typedefs,
                    Collection<PDeclaredDescriptor<?>> declaredTypes,
                    Collection<CService> services,
                    Collection<CConst> constants) {
        this.programFilePath  = programFilePath;
        this.documentation    = documentation;
        this.programName      = programName;
        this.namespaces       = namespaces       == null ? ImmutableMap.of()  : ImmutableMap.copyOf(namespaces);
        this.includedPrograms = includedPrograms == null ? ImmutableSet.of()  : ImmutableSet.copyOf(includedPrograms);
        this.includedFiles    = includedFiles    == null ? ImmutableList.of() : ImmutableList.copyOf(includedFiles);
        this.typedefs         = typedefs         == null ? ImmutableMap.of()  : ImmutableMap.copyOf(typedefs);
        this.declaredTypes    = declaredTypes    == null ? ImmutableList.of() : ImmutableList.copyOf(declaredTypes);
        this.services         = services         == null ? ImmutableList.of() : ImmutableList.copyOf(services);
        this.constants        = constants        == null ? ImmutableList.of() : ImmutableList.copyOf(constants);
    }

    public String getDocumentation() {
        return documentation;
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

    public String getProgramFilePath() {
        return programFilePath;
    }
}
