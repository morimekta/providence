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

package org.apache.thrift.j2.reflect.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.thrift.j2.descriptor.TDeclaredDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TList;
import org.apache.thrift.j2.descriptor.TMap;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TSet;
import org.apache.thrift.j2.reflect.contained.TContainedDocument;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class TTypeRegistry {
    private final Map<String, TDeclaredDescriptor<?>> mDeclaredTypes;
    private final Map<String, String>                 mTypedefs;
    private final Map<String, TContainedDocument>     mDocuments;

    public TTypeRegistry() {
        mDeclaredTypes = new LinkedHashMap<>();
        mTypedefs = new LinkedHashMap<>();
        mDocuments = new LinkedHashMap<>();
    }

    public boolean putDocument(String path, TContainedDocument doc) {
        if (!mDocuments.containsKey(path)) {
            mDocuments.put(path, doc);
            return true;
        }
        return false;
    }

    public TContainedDocument getDocument(String path) {
        return mDocuments.get(path);
    }

    public TContainedDocument getDocumentForPackage(String packageContext) {
        for (TContainedDocument document : mDocuments.values()) {
            if (document.getPackageName().equals(packageContext)) {
                return document;
            }
        }
        return null;
    }

    /**
     * Get the declared type with the name and package context.
     *
     * @param name Name of type, without any spaces.
     * @param packageContext The package context of the type.
     * @return The type provider.
     */
    @SuppressWarnings("unchecked")
    public <T extends TDeclaredDescriptor<T>> T getDescriptor(String name, String packageContext) {
        String declaredTypeName = name;
        if (!name.contains(".") && packageContext != null) {
            declaredTypeName = packageContext + "." + name;
        }
        if (mDeclaredTypes.containsKey(declaredTypeName)) {
            return (T) mDeclaredTypes.get(declaredTypeName);
        }

        throw new IllegalArgumentException("No such type \"" + name + "\" for package \"" + packageContext + "\"");
    }

    /**
     * Put a declared type into the registry.
     *
     * @param declaredType The type to register.
     */
    public <T> void putDeclaredType(TDeclaredDescriptor<T> declaredType) {
        String declaredTypeName = declaredType.getQualifiedName(null);
        if (mDeclaredTypes.containsKey(declaredTypeName)) {
            throw new IllegalStateException("Type " + declaredTypeName + " already exists");
        }
        mDeclaredTypes.put(declaredTypeName, declaredType);
    }

    /**
     *
     * @param typeName
     * @param identifier
     */
    public void putTypedef(String typeName, String identifier) {
        if (identifier == null || typeName == null) {
            throw new IllegalArgumentException("NOOO!");
        }
        mTypedefs.put(identifier, typeName);
    }

    /**
     * Given a type name and a package context, fetches the type provider for
     * the given type.
     *
     * @param typeName Name of type, without any spaces.
     * @param packageContext The package context of the type.
     * @return The type provider.
     */
    @SuppressWarnings("unchecked")
    public <T> TDescriptorProvider<T> getProvider(String typeName,
                                                  final String packageContext) {
        while (mTypedefs.containsKey(typeName)) {
            typeName = mTypedefs.get(typeName);
        }

        // Prepend package context to name
        TPrimitive primitive = TPrimitive.findByName(typeName);
        if (primitive != null) {
            return (TDescriptorProvider<T>) primitive.provider();
        }

        // Collection types are a bit complex, so handle it here.
        if (typeName.startsWith("map<") && typeName.endsWith(">")) {
            String[] parts = typeName.substring(4, typeName.length() - 1).split(",", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException(typeName + " is not a valid map descriptor, wrong number of types.");
            }
            String keyType = parts[0];
            String valueType = parts[1];
            return (TDescriptorProvider<T>) TMap.provider(getProvider(keyType, packageContext),
                                                          getProvider(valueType, packageContext));
        }
        if (typeName.startsWith("set<") && typeName.endsWith(">")) {
            String itemType = typeName.substring(4, typeName.length() - 1);
            return (TDescriptorProvider<T>) TSet.provider(getProvider(itemType, packageContext));
        }
        if (typeName.startsWith("list<") && typeName.endsWith(">")) {
            String itemType = typeName.substring(5, typeName.length() - 1);
            return (TDescriptorProvider<T>) TList.provider(getProvider(itemType, packageContext));
        }

        final String name = typeName;

        // Otherwise it's a declared type.
        return new TDescriptorProvider<T>() {
            @Override
            public TDescriptor<T> descriptor() {
                return (TDescriptor<T>) getDescriptor(name, packageContext);
            }
        };
    }
}
