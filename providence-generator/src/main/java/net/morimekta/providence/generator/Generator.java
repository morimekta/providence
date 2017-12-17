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
package net.morimekta.providence.generator;

import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.util.ProgramRegistry;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 */
public abstract class Generator {
    private final FileManager fileManager;

    public Generator(FileManager manager) {
        fileManager = manager;
    }

    /**
     * @return The local file manager.
     */
    protected final FileManager getFileManager() {
        return fileManager;
    }

    /**
     * Each compiler must implement this method.
     *
     * @param registry The typed and scoped registry for the program.
     * @throws IOException If a file could not be written.
     * @throws GeneratorException If some part of the file code could not be generated (invalid content).
     */
    public abstract void generate(ProgramTypeRegistry registry) throws IOException, GeneratorException;

    /**
     * Generate anything that is dependent on the global scope, or not
     * directly connected to a single program.
     *
     * @param registry The global program registry.
     * @param inputFiles List of files that are generated for.
     * @throws IOException If writing files failed.
     * @throws GeneratorException If bad generation.
     */
    public void generateGlobal(ProgramRegistry registry,
                               Collection<File> inputFiles) throws IOException, GeneratorException {}
}
