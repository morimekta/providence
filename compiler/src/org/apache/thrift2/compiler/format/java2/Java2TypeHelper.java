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

package org.apache.thrift2.compiler.format.java2;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift2.compiler.generator.GeneratorException;
import org.apache.thrift2.descriptor.TDeclaredDescriptor;
import org.apache.thrift2.descriptor.TDescriptor;
import org.apache.thrift2.descriptor.TField;
import org.apache.thrift2.descriptor.TList;
import org.apache.thrift2.descriptor.TMap;
import org.apache.thrift2.descriptor.TPrimitive;
import org.apache.thrift2.descriptor.TSet;
import org.apache.thrift2.reflect.contained.TContainedDocument;
import org.apache.thrift2.reflect.util.TTypeRegistry;

import static org.apache.thrift2.util.TStringUtils.camelCase;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 20.09.15
 */
public class Java2TypeHelper {
    public static String packageSeparator = ".";
    public static char packageSeparatorChar = '.';

    private final TTypeRegistry       mRegistry;

    public Java2TypeHelper(TTypeRegistry registry) {
        mRegistry = registry;
    }

    public String getJavaPackage(TDeclaredDescriptor<?> type) throws GeneratorException {
        String packageContext = type.getPackageName();
        TContainedDocument document = mRegistry.getDocumentForPackage(packageContext);
        return Java2Utils.getJavaPackage(document);
    }

    public String getQualifiedClassName(TDescriptor type) throws GeneratorException {
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
                return byte[].class.getCanonicalName();
            case MAP:
                return LinkedHashMap.class.getName();
            case SET:
                return LinkedHashSet.class.getName();
            case LIST:
                return LinkedList.class.getName();
            case ENUM:
            case MESSAGE:
                TDeclaredDescriptor<?> dt = (TDeclaredDescriptor<?>) type;
                return getJavaPackage(dt) + packageSeparator + Java2Utils.getClassName(dt);
        }
        throw new IllegalArgumentException("Unhandled type group" + type.getType());
    }

    public String getQualifiedClassName(TContainedDocument document) throws GeneratorException {
        return Java2Utils.getJavaPackage(document) + "." + camelCase("", document.getPackageName());
    }

    public String getSimpleClassName(TDescriptor type) {
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
                return "byte[]";
            case MAP:
                return LinkedHashMap.class.getSimpleName();
            case SET:
                return LinkedHashSet.class.getSimpleName();
            case LIST:
                return LinkedList.class.getSimpleName();
            case ENUM:
            case MESSAGE:
                return Java2Utils.getClassName((TDeclaredDescriptor<?>) type);
        }
        throw new IllegalArgumentException("Unhandled type group" + type.getType());
    }

    public String getConstantsClassName(TContainedDocument document) {
        return camelCase("", document.getPackageName()) + "_Constants";
    }

    public String getQualifiedValueTypeName(TDescriptor type) throws GeneratorException {
        switch (type.getType()) {
            case MAP:
                return Map.class.getName();
            case SET:
                return Set.class.getName();
            case LIST:
                return List.class.getName();
            case ENUM:
            case MESSAGE:
                TDeclaredDescriptor<?> dt = (TDeclaredDescriptor<?>) type;
                return getJavaPackage(dt) + "." + Java2Utils.getClassName((TDeclaredDescriptor<?>) type);
            default:
                return null;
        }
    }

    public String getValueType(TDescriptor type) {
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
                return byte[].class.getSimpleName();
            case MAP:
                TMap<?, ?> mType = (TMap<?, ?>) type;
                return String.format("%s<%s,%s>",
                                     Map.class.getSimpleName(),
                                     getFieldType(mType.keyDescriptor()),
                                     getFieldType(mType.itemDescriptor()));
            case SET:
                TSet<?> sType = (TSet<?>) type;
                return String.format("%s<%s>",
                                     Set.class.getSimpleName(),
                                     getFieldType(sType.itemDescriptor()));
            case LIST:
                TList<?> lType = (TList<?>) type;
                return String.format("%s<%s>",
                                     List.class.getSimpleName(),
                                     getFieldType(lType.itemDescriptor()));
            case ENUM:
            case MESSAGE:
                return Java2Utils.getClassName((TDeclaredDescriptor<?>) type);
        }
        throw new IllegalArgumentException("Unhandled type group" + type.getType());
    }

    public String getFieldType(TDescriptor type) {
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
                return byte[].class.getSimpleName();
            case MAP:
                TMap<?, ?> mType = (TMap<?, ?>) type;
                return String.format("%s<%s,%s>",
                                     Map.class.getSimpleName(),
                                     getFieldType(mType.keyDescriptor()),
                                     getFieldType(mType.itemDescriptor()));
            case SET:
                TSet<?> sType = (TSet<?>) type;
                return String.format("%s<%s>",
                                     Set.class.getSimpleName(),
                                     getFieldType(sType.itemDescriptor()));
            case LIST:
                TList<?> lType = (TList<?>) type;
                return String.format("%s<%s>",
                                     List.class.getSimpleName(),
                                     getFieldType(lType.itemDescriptor()));
            case ENUM:
            case MESSAGE:
                return Java2Utils.getClassName((TDeclaredDescriptor<?>) type);
        }
        throw new IllegalArgumentException("Unhandled type group" + type.getType());
    }

    public Object getDefaultValue(TField<?> field) {
        if (field.hasDefaultValue()) {
            return field.getDefaultValue();
        }
        if (field.descriptor() instanceof TPrimitive) {
            return ((TPrimitive) field.descriptor()).getDefaultValue();
        }
        return null;
    }

    public String getProviderName(TDescriptor type) {
        switch (type.getType()) {
            case ENUM:
            case MESSAGE:
                return String.format("%s.provider()", getSimpleClassName(type));
            case LIST:
                TList<?> lType = (TList<?>) type;
                return String.format("TList.provider(%s)", getProviderName(lType.itemDescriptor()));
            case SET:
                TSet<?> sType = (TSet<?>) type;
                return String.format("TSet.provider(%s)", getProviderName(sType.itemDescriptor()));
            case MAP:
                TMap<?,?> mType = (TMap<?,?>) type;
                return String.format("TMap.provider(%s,%s)",
                                     getProviderName(mType.keyDescriptor()),
                                     getProviderName(mType.itemDescriptor()));
            default:
                if (!(type instanceof TPrimitive)) {
                    throw new IllegalArgumentException("Unhandled type group " + type.getType());
                }
                return String.format("TPrimitive.%s.provider()", type.getName().toUpperCase());
        }
    }
}
