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

package net.morimekta.providence.reflect.util;

import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceProvider;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.parser.ThriftDocumentParser;
import net.morimekta.providence.util.TypeRegistry;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class DocumentRegistry extends TypeRegistry {
    private final Map<String, CDocument>              documents;
    private final Map<String, PService>               services;

    public DocumentRegistry() {
        services = new LinkedHashMap<>();
        documents = new LinkedHashMap<>();
    }

    /**
     * Puts the given document into the registry.
     *
     * @param path File path the document was found.
     * @param doc The contained document.
     * @return True if the document was not already there.
     */
    public boolean putDocument(String path, CDocument doc) {
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
    public CDocument getDocument(String path) {
        return documents.get(path);
    }

    public CDocument getDocumentForPackage(String packageContext) {
        for (CDocument document : documents.values()) {
            if (document.getPackageName()
                        .equals(packageContext)) {
                return document;
            }
        }
        return null;
    }

    /**
     * Given a type name and a package context, fetches the type provider for the given type.
     *
     * @param name        Name of type, without any spaces.
     * @param context     The package context of the type.
     * @param annotations Annotations on the type.
     * @return The type provider.
     */
    @SuppressWarnings("unchecked")
    public PDescriptorProvider getProvider(String name, final String context, Map<String, String> annotations) {
        name = finalTypename(name, context);

        // Prepend package context to name
        PPrimitive primitive = PPrimitive.findByName(name);
        if (primitive != null) {
            return primitive.provider();
        }

        if (annotations == null) {
            annotations = Collections.EMPTY_MAP;
        }

        // Collection types are a bit complex, so handle it here.
        if (name.startsWith("map<") && name.endsWith(">")) {
            String[] parts = name.substring(4, name.length() - 1)
                                     .split(",", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException(name + " is not a valid map descriptor, wrong number of types.");
            }
            String keyType = parts[0];
            String valueType = parts[1];
            switch (ThriftCollection.forName(annotations.get(ThriftAnnotation.COLLECTION.id))) {
                case SORTED:
                    return PMap.sortedProvider(getProvider(keyType, context, null),
                                               getProvider(valueType, context, null));
                case ORDERED:
                    return PMap.orderedProvider(getProvider(keyType, context, null),
                                                getProvider(valueType, context, null));
                case DEFAULT:
                    return PMap.provider(getProvider(keyType, context, null),
                                         getProvider(valueType, context, null));
            }
        }
        if (name.startsWith("set<") && name.endsWith(">")) {
            String itemType = name.substring(4, name.length() - 1);
            switch (ThriftCollection.forName(annotations.get(ThriftAnnotation.COLLECTION.id))) {
                case SORTED:
                    return PSet.sortedProvider(getProvider(itemType, context, null));
                case ORDERED:
                    return PSet.orderedProvider(getProvider(itemType, context, null));
                case DEFAULT:
                    return PSet.provider(getProvider(itemType, context, null));
            }
        }
        if (name.startsWith("list<") && name.endsWith(">")) {
            String itemType = name.substring(5, name.length() - 1);
            return PList.provider(getProvider(itemType, context, null));
        }

        if (!ThriftDocumentParser.VALID_IDENTIFIER.matcher(name).matches()) {
            throw new IllegalArgumentException(name + " is not a valid declared type identifier.");
        }

        final String finalName = name;

        // Otherwise it's a declared type.
        return () -> (PDescriptor) getDeclaredType(finalName, context);
    }

    public PServiceProvider getServiceProvider(String serviceName, final String packageContext) {
        return () -> getService(serviceName, packageContext);
    }
}
