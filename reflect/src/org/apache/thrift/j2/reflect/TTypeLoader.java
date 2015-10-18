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

package org.apache.thrift.j2.reflect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.thrift.j2.model.ThriftDocument;
import org.apache.thrift.j2.reflect.contained.TContainedDocument;
import org.apache.thrift.j2.reflect.parser.TParseException;
import org.apache.thrift.j2.reflect.parser.TParser;
import org.apache.thrift.j2.reflect.util.TDocumentConverter;
import org.apache.thrift.j2.reflect.util.TTypeRegistry;
import org.apache.thrift.j2.serializer.TSerializeException;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 07.09.15
 */
public class TTypeLoader {
    private final TTypeRegistry mRegistry;

    private final TDocumentConverter              mConverter;
    private final TParser                         mParser;
    private final Map<String, ThriftDocument>     mLoadedDocuments;
    private final Map<String, TContainedDocument> mParsedDocuments;
    private final Collection<File>                mIncludes;

    /**
     * Construct a type loader for file types matches with the given parser.
     *
     * @param includes List of files with include path roots. For includes search these in order.
     * @param parser The thrift file parser.
     */
    public TTypeLoader(Collection<File> includes,
                       TParser parser) throws IOException {
        this(includes,
             parser,
             new TTypeRegistry());
    }

    /**
     * Intermediate constructor.
     *
     * @param includes List of files with include path roots. For includes search these in order.
     * @param parser The thrift file parser.
     * @param registry Type registry to keep parsed types in.
     * @throws IOException
     */
    private TTypeLoader(Collection<File> includes,
                        TParser parser,
                        TTypeRegistry registry) {
        this(includes,
             parser,
             registry,
             new TDocumentConverter(registry));
    }

    /**
     * Constructor with injected functionality.
     *
     * @param includes List of files with include path roots. For includes search these in order.
     * @param parser The thrift file parser.
     * @param registry The type registry.
     * @param converter The document converter
     * @throws IOException
     */
    protected TTypeLoader(Collection<File> includes,
                          TParser parser,
                          TTypeRegistry registry,
                          TDocumentConverter converter) {
        mIncludes = includes;
        mParser = parser;
        mRegistry = registry;
        mConverter = converter;

        mLoadedDocuments = new LinkedHashMap<>();
        mParsedDocuments = new LinkedHashMap<>();
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
     * @param file
     * @throws IOException
     */
    public TContainedDocument load(File file) throws IOException, TSerializeException, TParseException {
        file = file.getCanonicalFile();
        if (!file.exists()) {
            throw new IllegalArgumentException("No such file " + file.getCanonicalPath());
        }
        if (file.isDirectory()) {
            throw new IllegalArgumentException("Unable to load thrift definition from directory: " +
                                               file.getCanonicalPath());
        }

        TContainedDocument cdoc = mRegistry.getDocument(file.getCanonicalPath());
        if (cdoc != null) {
            return cdoc;
        }

        InputStream in = new FileInputStream(file);
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

    public TTypeRegistry getRegistry() {
        return mRegistry;
    }
}
