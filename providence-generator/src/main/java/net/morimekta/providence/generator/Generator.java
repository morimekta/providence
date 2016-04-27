/*
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
import net.morimekta.providence.reflect.contained.CDocument;

import java.io.IOException;

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
     * @param document The document to generate files from.
     * @throws IOException If a file could not be written.
     * @throws GeneratorException If some part of the file code could not be generated (invalid content).
     */
    public abstract void generate(CDocument document) throws IOException, GeneratorException;
}
