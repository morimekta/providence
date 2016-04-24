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

package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.util.TypeRegistry;
import net.morimekta.util.Binary;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.morimekta.util.Strings.camelCase;

/**
 *
 */
public class JHelper {
    public static String packageSeparator = ".";

    private final TypeRegistry mRegistry;

    public JHelper(TypeRegistry registry, JOptions options) {
        mRegistry = registry;
    }

    public String getJavaPackage(PDeclaredDescriptor<?> type) throws GeneratorException {
        String packageContext = type.getPackageName();
        CDocument document = mRegistry.getDocumentForPackage(packageContext);
        return JUtils.getJavaPackage(document);
    }

    public String getJavaPackage(PService type) throws GeneratorException {
        String packageContext = type.getPackageName();
        CDocument document = mRegistry.getDocumentForPackage(packageContext);
        return JUtils.getJavaPackage(document);
    }

    public String getJavaPackage(CDocument document) throws GeneratorException {
        return JUtils.getJavaPackage(document);
    }

    public String getInstanceClassName(PField field) throws GeneratorException {
        switch (field.getType()) {
            case BOOL:
                return Boolean.class.getSimpleName();
            case BYTE:
                return Byte.class.getSimpleName();
            case I16:
                return Short.class.getSimpleName();
            case I32:
                return Integer.class.getSimpleName();
            case I64:
                return Long.class.getSimpleName();
            case DOUBLE:
                return Double.class.getSimpleName();
            case STRING:
                return String.class.getSimpleName();
            case BINARY:
                return Binary.class.getName();
            case MAP: {
                ContainerType ct = JAnnotation.containerType(field);
                switch (ct) {
                    case DEFAULT:
                        return ImmutableMap.class.getName();
                    case SORTED:
                        return ImmutableSortedMap.class.getName();
                    case ORDERED:
                        return LinkedHashMap.class.getName();
                }
            }
            case SET:{
                ContainerType ct = JAnnotation.containerType(field);
                switch (ct) {
                    case DEFAULT:
                        return ImmutableSet.class.getName();
                    case SORTED:
                        return ImmutableSortedSet.class.getName();
                    case ORDERED:
                        return LinkedHashSet.class.getName();
                }
            }
            case LIST:
                return LinkedList.class.getName();
            case ENUM:
            case MESSAGE:
                PDeclaredDescriptor<?> dt = (PDeclaredDescriptor<?>) field.getDescriptor();
                return getJavaPackage(dt) + packageSeparator + JUtils.getClassName(dt);
        }
        throw new IllegalArgumentException("Unhandled type group" + field.getType());
    }

    public String getConstantsClassName(CDocument document) {
        return camelCase("", document.getPackageName()) + "_Constants";
    }

    public String getValueType(PDescriptor type) throws GeneratorException {
        switch (type.getType()) {
            case BOOL:
                return Boolean.TYPE.getSimpleName();
            case BYTE:
                return Byte.TYPE.getSimpleName();
            case I16:
                return Short.TYPE.getSimpleName();
            case I32:
                return Integer.TYPE.getSimpleName();
            case I64:
                return Long.TYPE.getSimpleName();
            case DOUBLE:
                return Double.TYPE.getSimpleName();
            case STRING:
                return String.class.getSimpleName();
            case BINARY:
                return Binary.class.getName();
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) type;
                return String.format("%s<%s,%s>",
                                     Map.class.getName(),
                                     getFieldType(mType.keyDescriptor()),
                                     getFieldType(mType.itemDescriptor()));
            case SET:
                PSet<?> sType = (PSet<?>) type;
                return String.format("%s<%s>",
                                     Set.class.getName(),
                                     getFieldType(sType.itemDescriptor()));
            case LIST:
                PList<?> lType = (PList<?>) type;
                return String.format("%s<%s>",
                                     List.class.getName(),
                                     getFieldType(lType.itemDescriptor()));
            case ENUM:
            case MESSAGE:
                PDeclaredDescriptor<?> dt = (PDeclaredDescriptor<?>) type;
                return getJavaPackage(dt) + "." + JUtils.getClassName((PDeclaredDescriptor<?>) type);
        }
        throw new IllegalArgumentException("Unhandled type group" + type.getType());
    }

    public String getFieldType(PDescriptor type) throws GeneratorException {
        switch (type.getType()) {
            case BOOL:
                return Boolean.class.getSimpleName();
            case BYTE:
                return Byte.class.getSimpleName();
            case I16:
                return Short.class.getSimpleName();
            case I32:
                return Integer.class.getSimpleName();
            case I64:
                return Long.class.getSimpleName();
            case DOUBLE:
                return Double.class.getSimpleName();
            case STRING:
                return String.class.getSimpleName();
            case BINARY:
                return Binary.class.getName();
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) type;
                return String.format("%s<%s,%s>",
                                     Map.class.getName(),
                                     getFieldType(mType.keyDescriptor()),
                                     getFieldType(mType.itemDescriptor()));
            case SET:
                PSet<?> sType = (PSet<?>) type;
                return String.format("%s<%s>", Set.class.getName(), getFieldType(sType.itemDescriptor()));
            case LIST:
                PList<?> lType = (PList<?>) type;
                return String.format("%s<%s>", List.class.getName(), getFieldType(lType.itemDescriptor()));
            case ENUM:
            case MESSAGE:
                PDeclaredDescriptor<?> dt = (PDeclaredDescriptor<?>) type;
                return getJavaPackage(dt) + packageSeparator + JUtils.getClassName(dt);
        }
        throw new IllegalArgumentException("Unhandled type group" + type.getType());
    }

    public Object getDefaultValue(PField field) {
        if (field.hasDefaultValue()) {
            return field.getDefaultValue();
        }
        if (field.getDescriptor() instanceof PPrimitive) {
            return ((PPrimitive) field.getDescriptor()).getDefaultValue();
        }
        return null;
    }

    public String getProviderName(PDescriptor type) throws GeneratorException {
        switch (type.getType()) {
            case ENUM:
            case MESSAGE:
                return String.format("%s.provider()", getFieldType(type));
            case LIST:
                PList<?> lType = (PList<?>) type;
                return String.format("%s.provider(%s)",
                                     PList.class.getName(),
                                     getProviderName(lType.itemDescriptor()));
            case SET:
                PSet<?> sType = (PSet<?>) type;
                return String.format("%s.provider(%s)",
                                     PSet.class.getName(),
                                     getProviderName(sType.itemDescriptor()));
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) type;
                return String.format("%s.provider(%s,%s)",
                                     PMap.class.getName(),
                                     getProviderName(mType.keyDescriptor()),
                                     getProviderName(mType.itemDescriptor()));
            default:
                if (!(type instanceof PPrimitive)) {
                    throw new IllegalArgumentException("Unhandled type group " + type.getType());
                }
                return String.format("%s.%s.provider()",
                                     PPrimitive.class.getName(),
                                     type.getName().toUpperCase());
        }
    }

    public String getProviderName(PField field) throws GeneratorException {
        switch (field.getType()) {
            case ENUM:
            case MESSAGE:
                return String.format("%s.provider()", getFieldType(field.getDescriptor()));
            case LIST:
                PList<?> lType = (PList<?>) field.getDescriptor();
                return String.format("%s.provider(%s)",
                                     PList.class.getName(),
                                     getProviderName(lType.itemDescriptor()));
            case SET:
                PSet<?> sType = (PSet<?>) field.getDescriptor();
                switch (JAnnotation.containerType(field)) {
                    case DEFAULT:
                        return String.format("%s.provider(%s)", PSet.class.getName(), getProviderName(sType.itemDescriptor()));
                    case SORTED:
                        return String.format("%s.sortedProvider(%s)", PSet.class.getName(), getProviderName(sType.itemDescriptor()));
                    case ORDERED:
                        return String.format("%s.orderedProvider(%s)", PSet.class.getName(), getProviderName(sType.itemDescriptor()));
                }
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) field.getDescriptor();
                switch (JAnnotation.containerType(field)) {
                    case DEFAULT:
                        return String.format("%s.provider(%s,%s)",
                                             PMap.class.getName(),
                                             getProviderName(mType.keyDescriptor()),
                                             getProviderName(mType.itemDescriptor()));
                    case SORTED:
                        return String.format("%s.sortedProvider(%s,%s)",
                                             PMap.class.getName(),
                                             getProviderName(mType.keyDescriptor()),
                                             getProviderName(mType.itemDescriptor()));
                    case ORDERED:
                        return String.format("%s.orderedProvider(%s,%s)",
                                             PMap.class.getName(),
                                             getProviderName(mType.keyDescriptor()),
                                             getProviderName(mType.itemDescriptor()));
                }
            default:
                if (!(field instanceof PPrimitive)) {
                    throw new IllegalArgumentException("Unhandled type group " + field.getType());
                }
                return String.format("%s.%s.provider()",
                                     PPrimitive.class.getName(),
                                     field.getName().toUpperCase());
        }
    }
}
