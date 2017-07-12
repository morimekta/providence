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
package net.morimekta.providence.reflect;

import net.morimekta.providence.model.ProgramType;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.ProgramParser;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.providence.reflect.util.ProgramConverter;
import net.morimekta.providence.reflect.util.ProgramRegistry;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import static net.morimekta.providence.reflect.util.ReflectionUtils.programNameFromPath;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class TypeLoader {
    private final ProgramRegistry programRegistry;

    private final ProgramConverter         converter;
    private final ProgramParser            parser;
    private final Map<String, ProgramType> loadedDocuments;
    private final Collection<File>         includes;

    /**
     * Construct a type loader for file types matches with the given parser.
     *
     * @param includes List of files with include path roots. For includes
     *                 search these in order.
     */
    public TypeLoader(Collection<File> includes) {
        this(includes, new ThriftProgramParser());
    }

    /**
     * Construct a type loader for file types matches with the given parser.
     *
     * @param includes List of files with include path roots. For includes
     *                 search these in order.
     * @param parser   The thrift file parser.
     */
    public TypeLoader(Collection<File> includes, ProgramParser parser) {
        this(includes, parser, new ProgramRegistry());
    }

    /**
     * Intermediate constructor.
     *
     * @param includes List of files with include path roots. For includes
     *                 search these in order.
     * @param parser   The thrift file parser.
     * @param registry Type registry to keep parsed types in.
     */
    private TypeLoader(Collection<File> includes, ProgramParser parser, ProgramRegistry registry) {
        this(includes, parser, registry, new ProgramConverter(registry));
    }

    /**
     * Constructor with injected functionality.
     *
     * @param includes  List of files with include path roots. For includes
     *                  search these in order.
     * @param parser    The thrift file parser.
     * @param registry  The type registry.
     * @param converter The document converter
     */
    protected TypeLoader(Collection<File> includes, ProgramParser parser, ProgramRegistry registry, ProgramConverter converter) {
        this.includes = includes;
        this.parser = parser;
        this.programRegistry = registry;
        this.converter = converter;
        this.loadedDocuments = new LinkedHashMap<>();
    }

    /**
     * @return Set of loaded documents.
     */
    public Collection<ProgramType> loadedPrograms() {
        return loadedDocuments.values();
    }

    /**
     * Load a thrift definition from file including all it's dependencies.
     *
     * @param file The file to load.
     * @return The loaded contained document.
     * @throws IOException If the file could not be read or parsed.
     */
    public ProgramTypeRegistry load(File file) throws IOException {
        file = file.getCanonicalFile();
        if (!file.exists()) {
            throw new IllegalArgumentException("No such file " + file.getCanonicalPath());
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException(
                    "Unable to load thrift program: " + file.getCanonicalPath() + " is not a file.");
        }
        file = file.getAbsoluteFile();
        String path = file.getPath();

        ProgramTypeRegistry registry = this.programRegistry.registryForPath(path);
        if (programRegistry.containsProgramPath(path)) {
            return registry;
        }

        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ProgramType doc = parser.parse(in, file, includes);

        LinkedList<File> queue = new LinkedList<>();
        if (doc.hasIncludes()) {
            for (String include : doc.getIncludes()) {
                File location = new File(file.getParent(), include).getCanonicalFile();
                if (!location.exists()) {
                    if (include.startsWith(".") || include.startsWith(File.separator)) {
                        throw new ParseException("No such file \"" + include + "\" to include from " + file.getName());
                    }

                    for (File inc : includes) {
                        File i = new File(inc, include);
                        if (i.exists()) {
                            location = i.getCanonicalFile();
                            break;
                        }
                    }
                }

                if (location.exists() && !queue.contains(location)) {
                    queue.add(location.getAbsoluteFile());
                }
            }
        }

        // Load includes in reverse order, in case of serial dependencies.
        Collections.reverse(queue);

        loadedDocuments.put(path, doc);
        for (File include : queue) {
            registry.registerInclude(programNameFromPath(include.getPath()), load(include));
        }

        // Now everything it depends on is loaded.

        CProgram program = converter.convert(path, doc);
        programRegistry.putProgram(path, program);
        return registry;
    }

    /**
     * @return The local registry.
     */
    public ProgramRegistry getProgramRegistry() {
        return programRegistry;
    }
}
