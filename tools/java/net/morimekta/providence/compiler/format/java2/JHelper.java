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

package net.morimekta.providence.compiler.format.java2;

import net.morimekta.providence.compiler.generator.GeneratorException;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.util.TypeRegistry;
import net.morimekta.util.Binary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static net.morimekta.util.Strings.camelCase;

/**
 *
 */
public class JHelper {
    public static String packageSeparator = ".";

    private final TypeRegistry mRegistry;

    private final Class<? extends Set> setClass;
    private final Class<? extends Map> mapClass;

    public JHelper(TypeRegistry registry, JOptions options) {
        mRegistry = registry;

        switch (options.containers) {
            case ORDERED:
                setClass = LinkedHashSet.class;
                mapClass = LinkedHashMap.class;
                break;
            case SORTED:
                setClass = TreeSet.class;
                mapClass = TreeMap.class;
                break;
            default:
                setClass = HashSet.class;
                mapClass = HashMap.class;
                break;
        }
    }

    public String getJavaPackage(PDeclaredDescriptor<?> type) throws GeneratorException {
        String packageContext = type.getPackageName();
        CDocument document = mRegistry.getDocumentForPackage(packageContext);
        return JUtils.getJavaPackage(document);
    }

    public String getJavaPackage(CDocument document) throws GeneratorException {
        return JUtils.getJavaPackage(document);
    }

    public String getQualifiedInstanceClassName(PDescriptor<?> type) throws GeneratorException {
        switch (type.getType()) {
            case BOOL:
                return Boolean.class.getName();
            case BYTE:
                return Byte.class.getName();
            case I16:
                return Short.class.getName();
            case I32:
                return Integer.class.getName();
            case I64:
                return Long.class.getName();
            case DOUBLE:
                return Double.class.getName();
            case STRING:
                return String.class.getName();
            case BINARY:
                return Binary.class.getName();
            case MAP:
                return mapClass.getName();
            case SET:
                return setClass.getName();
            case LIST:
                return LinkedList.class.getName();
            case ENUM:
            case MESSAGE:
                PDeclaredDescriptor<?> dt = (PDeclaredDescriptor<?>) type;
                return getJavaPackage(dt) + packageSeparator + JUtils.getClassName(dt);
        }
        throw new IllegalArgumentException("Unhandled type group" + type.getType());
    }

    public String getInstanceClassName(PDescriptor type) {
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
                return Binary.class.getSimpleName();
            case MAP:
                return mapClass.getSimpleName();
            case SET:
                return setClass.getSimpleName();
            case LIST:
                return LinkedList.class.getSimpleName();
            case ENUM:
            case MESSAGE:
                return JUtils.getClassName((PDeclaredDescriptor<?>) type);
        }
        throw new IllegalArgumentException("Unhandled type group" + type.getType());
    }

    public String getConstantsClassName(CDocument document) {
        return camelCase("", document.getPackageName()) + "_Constants";
    }

    public String getQualifiedValueTypeName(PDescriptor type) throws GeneratorException {
        switch (type.getType()) {
            case MAP:
                return Map.class.getName();
            case SET:
                return Set.class.getName();
            case LIST:
                return List.class.getName();
            case ENUM:
            case MESSAGE:
                PDeclaredDescriptor<?> dt = (PDeclaredDescriptor<?>) type;
                return getJavaPackage(dt) + "." + JUtils.getClassName((PDeclaredDescriptor<?>) type);
            default:
                return null;
        }
    }

    public String getValueType(PDescriptor type) {
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
                return Binary.class.getSimpleName();
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) type;
                return String.format("%s<%s,%s>",
                                     Map.class.getSimpleName(),
                                     getFieldType(mType.keyDescriptor()),
                                     getFieldType(mType.itemDescriptor()));
            case SET:
                PSet<?> sType = (PSet<?>) type;
                return String.format("%s<%s>", Set.class.getSimpleName(), getFieldType(sType.itemDescriptor()));
            case LIST:
                PList<?> lType = (PList<?>) type;
                return String.format("%s<%s>", List.class.getSimpleName(), getFieldType(lType.itemDescriptor()));
            case ENUM:
            case MESSAGE:
                return JUtils.getClassName((PDeclaredDescriptor<?>) type);
        }
        throw new IllegalArgumentException("Unhandled type group" + type.getType());
    }

    public String getFieldType(PDescriptor type) {
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
                return Binary.class.getSimpleName();
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) type;
                return String.format("%s<%s,%s>",
                                     Map.class.getSimpleName(),
                                     getFieldType(mType.keyDescriptor()),
                                     getFieldType(mType.itemDescriptor()));
            case SET:
                PSet<?> sType = (PSet<?>) type;
                return String.format("%s<%s>", Set.class.getSimpleName(), getFieldType(sType.itemDescriptor()));
            case LIST:
                PList<?> lType = (PList<?>) type;
                return String.format("%s<%s>", List.class.getSimpleName(), getFieldType(lType.itemDescriptor()));
            case ENUM:
            case MESSAGE:
                return JUtils.getClassName((PDeclaredDescriptor<?>) type);
        }
        throw new IllegalArgumentException("Unhandled type group" + type.getType());
    }

    public Object getDefaultValue(PField<?> field) {
        if (field.hasDefaultValue()) {
            return field.getDefaultValue();
        }
        if (field.getDescriptor() instanceof PPrimitive) {
            return ((PPrimitive) field.getDescriptor()).getDefaultValue();
        }
        return null;
    }

    public String getProviderName(PDescriptor type) {
        switch (type.getType()) {
            case ENUM:
            case MESSAGE:
                return String.format("%s.provider()", getInstanceClassName(type));
            case LIST:
                PList<?> lType = (PList<?>) type;
                return String.format("PList.provider(%s)", getProviderName(lType.itemDescriptor()));
            case SET:
                PSet<?> sType = (PSet<?>) type;
                return String.format("PSet.provider(%s)", getProviderName(sType.itemDescriptor()));
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) type;
                return String.format("PMap.provider(%s,%s)",
                                     getProviderName(mType.keyDescriptor()),
                                     getProviderName(mType.itemDescriptor()));
            default:
                if (!(type instanceof PPrimitive)) {
                    throw new IllegalArgumentException("Unhandled type group " + type.getType());
                }
                return String.format("PPrimitive.%s.provider()",
                                     type.getName()
                                         .toUpperCase());
        }
    }
}
