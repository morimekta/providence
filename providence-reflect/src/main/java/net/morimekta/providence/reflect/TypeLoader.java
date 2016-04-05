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

package net.morimekta.providence.reflect;

import net.morimekta.providence.model.ThriftDocument;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.Parser;
import net.morimekta.providence.reflect.util.DocumentConverter;
import net.morimekta.providence.reflect.util.TypeRegistry;

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

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class TypeLoader {
    private final TypeRegistry mRegistry;

    private final DocumentConverter           mConverter;
    private final Parser                      mParser;
    private final Map<String, ThriftDocument> mLoadedDocuments;
    private final Collection<File>            mIncludes;

    /**
     * Construct a type loader for file types matches with the given parser.
     *
     * @param includes List of files with include path roots. For includes
     *                 search these in order.
     * @param parser   The thrift file parser.
     */
    public TypeLoader(Collection<File> includes, Parser parser) {
        this(includes, parser, new TypeRegistry());
    }

    /**
     * Intermediate constructor.
     *
     * @param includes List of files with include path roots. For includes
     *                 search these in order.
     * @param parser   The thrift file parser.
     * @param registry Type registry to keep parsed types in.
     */
    private TypeLoader(Collection<File> includes, Parser parser, TypeRegistry registry) {
        this(includes, parser, registry, new DocumentConverter(registry));
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
    protected TypeLoader(Collection<File> includes, Parser parser, TypeRegistry registry, DocumentConverter converter) {
        mIncludes = includes;
        mParser = parser;
        mRegistry = registry;
        mConverter = converter;

        mLoadedDocuments = new LinkedHashMap<>();
    }

    /**
     * @return Set of loaded documents.
     */
    public Collection<ThriftDocument> loadedDocuments() {
        return mLoadedDocuments.values();
    }

    /**
     * Load a thrift definition from file including all it's dependencies.
     *
     * @param file The file to load.
     * @return The loaded contained document.
     * @throws IOException If the file could not be read.
     * @throws ParseException If the file could not be parsed.
     */
    public CDocument load(File file) throws IOException, ParseException {
        file = file.getCanonicalFile();
        if (!file.exists()) {
            throw new IllegalArgumentException("No such file " + file.getCanonicalPath());
        }
        if (file.isDirectory()) {
            throw new IllegalArgumentException(
                    "Unable to load thrift definition from directory: " + file.getCanonicalPath());
        }

        CDocument cdoc = mRegistry.getDocument(file.getCanonicalPath());
        if (cdoc != null) {
            return cdoc;
        }

        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ThriftDocument doc = mParser.parse(in, file.getName());

        LinkedList<File> queue = new LinkedList<>();
        for (String include : doc.getIncludes()) {
            File location = new File(file.getParent(), include);
            if (!location.exists()) {
                for (File inc : mIncludes) {
                    File i = new File(inc, include);
                    if (i.exists()) {
                        location = i.getCanonicalFile();
                        break;
                    }
                }
            }
            if (location.exists()) {
                queue.add(location.getCanonicalFile());
            }
        }

        // Load includes in reverse order, in case of serial dependencies.
        Collections.reverse(queue);

        mLoadedDocuments.put(file.getCanonicalPath(), doc);
        for (File include : queue) {
            if (!mLoadedDocuments.containsKey(include)) {
                load(include);
            }
        }

        // Now everything it depends on is loaded.

        cdoc = mConverter.convert(doc);
        mRegistry.putDocument(file.getCanonicalPath(), cdoc);
        return cdoc;
    }

    /**
     * @return The local registry.
     */
    public TypeRegistry getRegistry() {
        return mRegistry;
    }
}
