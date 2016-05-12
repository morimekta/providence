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

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceProvider;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.reflect.contained.CDocument;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class TypeRegistry {
    private final Map<String, PDeclaredDescriptor<?>> declaredTypes;
    private final Map<String, String>                 typedefs;
    private final Map<String, CDocument>              documents;
    private final Map<String, PService>               services;

    public TypeRegistry() {
        declaredTypes = new LinkedHashMap<>();
        typedefs = new LinkedHashMap<>();
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
     * Get the declared type with the name and package context.
     *
     * @param name           Name of type, without any spaces.
     * @param packageContext The package context of the type.
     * @param <T>            The described type.
     * @return The type provider.
     */
    @SuppressWarnings("unchecked")
    public <T extends PDeclaredDescriptor<T>> T getDescriptor(String name, String packageContext) {
        String declaredTypeName = name;
        if (!name.contains(".") && packageContext != null) {
            declaredTypeName = packageContext + "." + name;
        } else {
            int dot = declaredTypeName.indexOf('.');
            packageContext = declaredTypeName.substring(0, dot);
            name = declaredTypeName.substring(dot + 1);
        }

        if (declaredTypes.containsKey(declaredTypeName)) {
            return (T) declaredTypes.get(declaredTypeName);
        }

        throw new IllegalArgumentException("No such type \"" + name + "\" in package \"" + packageContext + "\"");
    }

    /**
     * Put a declared type into the registry.
     *
     * @param declaredType The type to register.
     * @param <T> The descriptor object type.
     */
    public <T> void putDeclaredType(PDeclaredDescriptor<T> declaredType) {
        String declaredTypeName = declaredType.getQualifiedName(null);
        if (declaredTypes.containsKey(declaredTypeName)) {
            throw new IllegalStateException("Type " + declaredTypeName + " already exists");
        }
        declaredTypes.put(declaredTypeName, declaredType);
    }

    /**
     * Registers a typedef definition.
     *
     * @param typeName The typedef name to put.
     * @param identifier The qualified name that the name represents.
     */
    public void putTypedef(String typeName, String identifier) {
        if (identifier == null || typeName == null) {
            throw new IllegalArgumentException("NOOO!");
        }
        typedefs.put(identifier, typeName);
    }

    /**
     * Given a type name and a package context, fetches the type provider for the given type.
     *
     * @param typeName       Name of type, without any spaces.
     * @param packageContext The package context of the type.
     * @param annotations    Annotations on the type.
     * @return The type provider.
     */
    @SuppressWarnings("unchecked")
    public PDescriptorProvider getProvider(String typeName, final String packageContext, Map<String, String> annotations) {
        while (typedefs.containsKey(typeName)) {
            typeName = typedefs.get(typeName);
        }

        // Prepend package context to name
        PPrimitive primitive = PPrimitive.findByName(typeName);
        if (primitive != null) {
            return primitive.provider();
        }

        // Collection types are a bit complex, so handle it here.
        if (typeName.startsWith("map<") && typeName.endsWith(">")) {
            String[] parts = typeName.substring(4, typeName.length() - 1)
                                     .split(",", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException(typeName + " is not a valid map descriptor, wrong number of types.");
            }
            String keyType = parts[0];
            String valueType = parts[1];
            switch (ThriftCollection.forName(ThriftAnnotation.COLLECTION.id)) {
                case SORTED:
                    return PMap.sortedProvider(getProvider(keyType, packageContext, Collections.EMPTY_MAP),
                                               getProvider(valueType, packageContext, Collections.EMPTY_MAP));
                case ORDERED:
                    return PMap.orderedProvider(getProvider(keyType, packageContext, Collections.EMPTY_MAP),
                                                getProvider(valueType, packageContext, Collections.EMPTY_MAP));
                case DEFAULT:
                    return PMap.provider(getProvider(keyType, packageContext, Collections.EMPTY_MAP),
                                         getProvider(valueType, packageContext, Collections.EMPTY_MAP));
            }
        }
        if (typeName.startsWith("set<") && typeName.endsWith(">")) {
            String itemType = typeName.substring(4, typeName.length() - 1);
            switch (ThriftCollection.forName(ThriftAnnotation.COLLECTION.id)) {
                case SORTED:
                    return PSet.sortedProvider(getProvider(itemType, packageContext, Collections.EMPTY_MAP));
                case ORDERED:
                    return PSet.orderedProvider(getProvider(itemType, packageContext, Collections.EMPTY_MAP));
                case DEFAULT:
                    return PSet.provider(getProvider(itemType, packageContext, Collections.EMPTY_MAP));
            }
        }
        if (typeName.startsWith("list<") && typeName.endsWith(">")) {
            String itemType = typeName.substring(5, typeName.length() - 1);
            return PList.provider(getProvider(itemType, packageContext, Collections.EMPTY_MAP));
        }

        final String name = typeName;

        // Otherwise it's a declared type.
        return () -> (PDescriptor) getDescriptor(name, packageContext);
    }

    public void putService(PService service) {
        if (service == null) {
            throw new IllegalArgumentException("NOOOO!");
        }
        services.put(service.getQualifiedName(null), service);
    }

    public PServiceProvider getServiceProvider(String serviceName, final String packageContext) {
        if (!serviceName.contains(".")) {
            serviceName = packageContext + "." + serviceName;
        }

        final String finalServiceName = serviceName;
        return () -> services.get(finalServiceName);
    }
}
