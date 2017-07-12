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
package net.morimekta.providence.util;

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PServiceProvider;
import net.morimekta.providence.descriptor.PSet;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for declared types referenced in a flat space program context.
 * The registry itself does not have a context per se, as these may
 * reference each other recursively.
 */
public abstract class BaseTypeRegistry implements WritableTypeRegistry {
    private final Map<String, String> typedefs;

    protected BaseTypeRegistry() {
        this.typedefs = new HashMap<>();
    }

    @Override
    public void registerTypedef(@Nonnull String identifier,
                                @Nonnull String programContext,
                                @Nonnull String target) {
        typedefs.put(qualifiedNameFromIdAndContext(identifier, programContext),
                     finalTypename(target, programContext));
    }

    @Override
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
            switch (ThriftCollection.forName(annotations.get(ThriftAnnotation.CONTAINER.tag))) {
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
            switch (ThriftCollection.forName(annotations.get(ThriftAnnotation.CONTAINER.tag))) {
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

        if (name.split("[.]").length != 2) {
            throw new IllegalArgumentException(name + " is not a valid declared type identifier.");
        }

        final String finalName = name;

        // Otherwise it's a declared type.
        return () -> getDeclaredType(finalName, context);
    }

    @Override
    public PServiceProvider getServiceProvider(final String serviceName, final String programContext) {
        return () -> getService(serviceName, programContext);
    }

    @Override
    public void registerRecursively(@Nonnull PService service) {
        if (register(service)) {
            if (service.getExtendsService() != null) {
                registerRecursively(service.getExtendsService());
            }

            // TODO(morimekta): Figure out if we need to register these too. Probably not.
            //        for (PServiceMethod method : service.getMethods()) {
            //            registerRecursively(method.getRequestType());
            //            if (method.getResponseType() != null) {
            //                registerRecursively(method.getResponseType());
            //            }
            //        }
        }
    }

    @Override
    public <T> void registerRecursively(PDeclaredDescriptor<T> declaredType) {
        if (register(declaredType)) {
            if (declaredType instanceof PMessageDescriptor) {
                PMessageDescriptor descriptor = (PMessageDescriptor) declaredType;
                for (PField field : descriptor.getFields()) {
                    if (field.getType() == PType.ENUM || field.getType() == PType.MESSAGE) {
                        registerRecursively((PDeclaredDescriptor<?>) field.getDescriptor());
                    } else if (field.getType() == PType.MAP || field.getType() == PType.LIST || field.getType() == PType.SET) {
                        registerListType((PContainer) field.getDescriptor());
                    }
                }
            }
        }
    }

    /**
     * Get the final typename of the given identifier within the context.
     *
     * @param typeName The identifier name.
     * @param programContext The program context.
     * @return The final typename.
     */
    protected String finalTypename(String typeName, String programContext) {
        String qualifiedName = qualifiedTypenameInternal(typeName, programContext);

        if (typedefs.containsKey(qualifiedName)) {
            String resolved = typedefs.get(qualifiedName);
            return finalTypename(resolved, programContext);
        }

        return qualifiedName;
    }

    /**
     * Make a qualified type name from a name identifier string and the program context.
     * @param name The type name.
     * @param context The program context.
     * @return The qualified name containing program name and type name.
     */
    protected static String qualifiedNameFromIdAndContext(String name, String context) {
        if (!name.contains(".")) {
            return context + "." + name;
        }
        return name;
    }

    /**
     * Register a list type part of recursive registering.
     *
     * @param containerType
     */
    protected void registerListType(PContainer containerType) {
        PDescriptor itemType = containerType.itemDescriptor();
        if (itemType.getType() == PType.MAP ||
            itemType.getType() == PType.LIST ||
            itemType.getType() == PType.SET) {
            registerListType((PContainer) itemType);
        } else if (itemType.getType() == PType.ENUM ||
                   itemType.getType() == PType.MESSAGE){
            registerRecursively((PDeclaredDescriptor<?>) itemType);
        }
        if (containerType instanceof PMap) {
            PDescriptor keyType = containerType.itemDescriptor();
            if (keyType.getType() == PType.ENUM ||
                keyType.getType() == PType.MESSAGE){
                registerRecursively((PDeclaredDescriptor<?>) keyType);
            }
        }
        // Else ignore.
    }

    /**
     * Get the qualified name of the type. Note that this will put in the
     * <i>final</i> typename for generic types.
     *
     * @param typeName The identifier name.
     * @param programContext The package context.
     * @return The qualified typename.
     */
    @Nonnull
    private String qualifiedTypenameInternal(@Nonnull String typeName, @Nonnull String programContext) {
        if (PPrimitive.findByName(typeName) != null) return typeName;
        if (typeName.startsWith("map<") && typeName.endsWith(">")) {
            String[] generic = typeName.substring(4, typeName.length() - 1).split(",");
            if (generic.length != 2) {
                throw new IllegalArgumentException("Invalid map generic part: \"" + typeName + "\"");
            }
            return "map<" + finalTypename(generic[0].trim(), programContext) +
                   "," + finalTypename(generic[1].trim(), programContext) + ">";
        } else if (typeName.startsWith("set<") && typeName.endsWith(">")) {
            String generic = typeName.substring(4, typeName.length() - 1);
            return "set<" + finalTypename(generic.trim(), programContext) + ">";
        } else if (typeName.startsWith("list<") && typeName.endsWith(">")) {
            String generic = typeName.substring(5, typeName.length() - 1);
            return "list<" + finalTypename(generic.trim(), programContext) + ">";
        } else {
            return qualifiedNameFromIdAndContext(typeName, programContext);
        }
    }
}
