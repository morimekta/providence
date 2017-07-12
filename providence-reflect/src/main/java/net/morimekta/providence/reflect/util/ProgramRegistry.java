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
package net.morimekta.providence.reflect.util;

import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.util.SimpleTypeRegistry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class ProgramRegistry extends SimpleTypeRegistry {
    private final Map<String, CProgram> documents;

    public ProgramRegistry() {
        documents = new LinkedHashMap<>();
    }

    /**
     * Puts the given document into the registry.
     *
     * @param path File path the document was found.
     * @param doc The contained document.
     * @return True if the document was not already there.
     */
    public boolean putDocument(String path, CProgram doc) {
        if (!documents.containsKey(path)) {
            documents.put(path, doc);
            return true;
        }
        return false;
    }

    /**
     * Gets the document for a given file path.
     *
     * @param path The file path.
     * @return The contained document, or null if not found.
     */
    public CProgram getDocument(String path) {
        return documents.get(path);
    }

    public CProgram getDocumentForPackage(String packageContext) {
        for (CProgram document : documents.values()) {
            if (document.getProgramName()
                        .equals(packageContext)) {
                return document;
            }
        }
        return null;
    }
}
