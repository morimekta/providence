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
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class TypeRegistry {
    private final Map<String, PDeclaredDescriptor<?>> declaredTypes;
    private final HashSet<String>                     knownPrograms;
    private final Map<String, String>                 typedefs;
    private final Map<String, PService>               services;

    public TypeRegistry() {
        knownPrograms = new HashSet<>();
        declaredTypes = new LinkedHashMap<>();
        typedefs      = new HashMap<>();
        services      = new HashMap<>();
    }

    /**
     * Get the declared type with the given name and package context.
     *
     * @param name    Name of type, without any spaces.
     * @param context The program context of the type.
     * @param <T>     The described type.
     * @return The type descriptor.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T extends PDeclaredDescriptor<T>> T getDeclaredType(@Nonnull String name,
                                                                @Nonnull String context) {
        String declaredTypeName = finalTypename(name, context);

        context = declaredTypeName.replaceAll("\\..*", "");
        name = declaredTypeName.replaceAll(".*\\.", "");

        if (declaredTypes.containsKey(declaredTypeName)) {
            return (T) declaredTypes.get(declaredTypeName);
        }

        if (knownPrograms.contains(context)) {
            throw new IllegalArgumentException("No such type \"" + name + "\" in package \"" + context + "\"");
        } else {
            throw new IllegalArgumentException("No such package \"" + context + "\" exists for type \"" + name + "\"");
        }
    }

    /**
     * Get a declared type by its qualified type name.
     *
     * @param name The name of the type.
     * @param <T>  The described type.
     * @return The type descriptor.
     */
    @Nonnull
    public <T extends PDeclaredDescriptor<T>> T getDeclaredType(@Nonnull String name) {
        String[] parts = name.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Requesting global typename without package: \"" + name + "\"");
        } else if (parts.length > 2) {
            throw new IllegalArgumentException("Invalid declared type: \"" + name + "\"");
        }
        return getDeclaredType(parts[1],
                               parts[0]);
    }

    /**
     * Put a declared type into the registry.
     *
     * @param declaredType The type to register.
     * @param <T> The descriptor object type.
     */
    public <T> void putDeclaredType(@Nonnull PDeclaredDescriptor<T> declaredType) {
        String declaredTypeName = declaredType.getQualifiedName();
        if (declaredTypes.containsKey(declaredTypeName)) {
            throw new IllegalStateException("Type " + declaredTypeName + " already exists");
        }
        knownPrograms.add(declaredType.getProgramName());
        declaredTypes.put(declaredTypeName, declaredType);
    }

    /**
     * Services are not handled as "declared types", so they need to be registered
     * separately.
     *
     * @param service the service to register.
     */
    public void putService(PService service) {
        if (service == null) {
            throw new IllegalArgumentException("No service to register");
        }
        // Services cannot be aliased with typedefs, so no need to resolve the
        // qualified or final name.
        String name = service.getQualifiedName();
        if (services.containsKey(name)) {
            throw new IllegalStateException("Service " + name + " already registered");
        }
        services.put(name, service);
    }

    /**
     * Get a service definition from the name and context.
     *
     * @param name    The service name.
     * @param context The context to get the service for.
     * @return The service or null if not found.
     */
    @Nullable
    public PService getService(String name, String context) {
        return services.get(qualifiedName(name, context));
    }

    /**
     * Register a declared type recursively. If the type is a message, then
     * iterate through the fields and register those types recursively.
     *
     * @param declaredType The descriptor for the type.
     * @param <T> The declared java type.
     */
    public <T> void registerRecursively(PDeclaredDescriptor<T> declaredType) {
        String declaredTypeName = declaredType.getQualifiedName();
        if (declaredTypes.containsKey(declaredTypeName)) {
            return;
        }
        putDeclaredType(declaredType);
        if (declaredType instanceof PMessageDescriptor) {
            PMessageDescriptor descriptor = (PMessageDescriptor) declaredType;
            for (PField field : descriptor.getFields()) {
                if (field.getType() == PType.ENUM || field.getType() == PType.MESSAGE) {
                    registerRecursively((PDeclaredDescriptor<?>) field.getDescriptor());
                } else if (field.getType() == PType.MAP ||
                           field.getType() == PType.LIST ||
                           field.getType() == PType.SET) {
                    registerListType((PContainer) field.getDescriptor());
                }
            }
        }
    }

    /**
     * Registers a typedef definition.
     *
     * @param identifier The typedef name (the alias) to put.
     * @param context The package context of the typedef.
     * @param target The qualified name that the name represents.
     */
    public void putTypedef(String identifier, String context, String target) {
        if (identifier == null || context == null || target == null) {
            throw new IllegalArgumentException("Null argument to putTypedef");
        }

        identifier = qualifiedTypename(identifier, context);
        target = qualifiedTypename(target, context);
        typedefs.put(identifier, target);
    }

    /**
     * Get the final typename of the given identifier within the context.
     *
     * @param name The identifier name.
     * @param context The package context.
     * @return The final typename.
     */
    protected String finalTypename(String name, String context) {
        String typename = qualifiedTypename(name, context);
        if (typedefs.containsKey(typename)) {
            typename = typedefs.get(typename);
            return finalTypename(typename, context);
        }
        return typename;
    }

    /**
     * Get the qualified name of the type. Note that this will put in the
     * <i>final</i> typename for generic types.
     *
     * @param name The identifier name.
     * @param context The package context.
     * @return The qualified typename.
     */
    @Nonnull
    private String qualifiedTypename(String name, String context) {
        if (name == null || context == null) {
            throw new IllegalArgumentException("Null argument for qualified typename");
        }
        if (PPrimitive.findByName(name) != null) return name;
        if (name.startsWith("map<") && name.endsWith(">")) {
            String[] generic = name.substring(4, name.length() - 1).split(",");
            if (generic.length != 2) {
                throw new IllegalArgumentException();
            }
            return "map<" + finalTypename(generic[0].trim(), context) +
                   "," + finalTypename(generic[1].trim(), context) + ">";
        } else if (name.startsWith("set<") && name.endsWith(">")) {
            String generic = name.substring(4, name.length() - 1);
            return "set<" + finalTypename(generic.trim(), context) + ">";
        } else if (name.startsWith("list<") && name.endsWith(">")) {
            String generic = name.substring(5, name.length() - 1);
            return "list<" + finalTypename(generic.trim(), context) + ">";
        } else {
            return qualifiedName(name, context);
        }
    }

    private void registerListType(PContainer containerType) {
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

    private static String qualifiedName(String name, String context) {
        if (!name.contains(".")) {
            return context + "." + name;
        }
        return name;
    }
}
