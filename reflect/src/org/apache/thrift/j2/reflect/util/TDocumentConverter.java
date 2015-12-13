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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.thrift.j2.descriptor.TDeclaredDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.model.Declaration;
import org.apache.thrift.j2.model.EnumType;
import org.apache.thrift.j2.model.EnumValue;
import org.apache.thrift.j2.model.StructType;
import org.apache.thrift.j2.model.ThriftDocument;
import org.apache.thrift.j2.model.ThriftField;
import org.apache.thrift.j2.reflect.contained.TCDocument;
import org.apache.thrift.j2.reflect.contained.TCEnum;
import org.apache.thrift.j2.reflect.contained.TCEnumDescriptor;
import org.apache.thrift.j2.reflect.contained.TCExceptionDescriptor;
import org.apache.thrift.j2.reflect.contained.TCField;
import org.apache.thrift.j2.reflect.contained.TCStructDescriptor;
import org.apache.thrift.j2.reflect.contained.TCUnionDescriptor;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class TDocumentConverter {
    private final TTypeRegistry mRegistry;

    public TDocumentConverter(TTypeRegistry registry) {
        mRegistry = registry;
    }

    /**
     * Convert document model to declared document.
     *
     * @param document Document model to convert.
     * @return The declared thrift document.
     */
    public TCDocument convert(ThriftDocument document) {
        List<TDeclaredDescriptor<?>> declaredTypes = new LinkedList<>();
        List<TField<?>> constants = new LinkedList<>();
        Map<String, String> typedefs = new LinkedHashMap<>();

        for (Declaration decl : document.getDecl()) {
            if (decl.hasDeclEnum()) {
                EnumType enumType = decl.getDeclEnum();

                int nextValue = TEnumDescriptor.DEFAULT_FIRST_VALUE;
                TCEnumDescriptor type =
                        new TCEnumDescriptor(enumType.getComment(),
                                             document.getPackage(),
                                             enumType.getName());
                List<TCEnum> values = new LinkedList<>();
                for (EnumValue value : enumType.getValues()) {
                    int v = value.hasValue() ? value.getValue() : nextValue;
                    nextValue = v + 1;
                    values.add(new TCEnum(value.getComment(),
                                          value.getValue(),
                                          value.getName(),
                                          type));
                }
                type.setValues(values);
                declaredTypes.add(type);
                mRegistry.putDeclaredType(type);
            }
            if (decl.hasDeclStruct()) {
                StructType structType = decl.getDeclStruct();

                List<TCField> fields = new LinkedList<>();
                for (ThriftField field : structType.getFields()) {
                    fields.add(makeField(document.getPackage(), field));
                }
                TStructDescriptor<?, ?> type;
                switch (structType.getVariant()) {
                    case STRUCT:
                        type = new TCStructDescriptor(structType.getComment(),
                                                      document.getPackage(),
                                                      structType.getName(),
                                                      fields);
                        break;
                    case UNION:
                        type = new TCUnionDescriptor(structType.getComment(),
                                                     document.getPackage(),
                                                     structType.getName(),
                                                     fields);
                        break;
                    case EXCEPTION:
                        type = new TCExceptionDescriptor(structType.getComment(),
                                                         document.getPackage(),
                                                         structType.getName(),
                                                         fields);
                        break;
                    default:
                        throw new IllegalArgumentException("Unhandled struct type " +
                                                           structType.getVariant());
                }
                declaredTypes.add(type);
                mRegistry.putDeclaredType(type);
            }

            if (decl.hasDeclConst()) {
                ThriftField constant = decl.getDeclConst();

                constants.add(makeField(document.getPackage(), constant));
            }
            if (decl.hasDeclTypedef()) {
                typedefs.put(decl.getDeclTypedef().getName(),
                             decl.getDeclTypedef().getType());
                mRegistry.putTypedef(decl.getDeclTypedef().getType(),
                                     decl.getDeclTypedef().getName());
            }
        }

        return new TCDocument(document.getComment(),
                              document.getPackage(),
                              document.getNamespaces(),
                              getIncludes(document),
                              typedefs,
                              declaredTypes,
                              constants);
    }

    private List<String> getIncludes(ThriftDocument document) {
        List<String> out = new LinkedList<>();
        for (String include : document.getIncludes()) {
            int i = include.lastIndexOf('.');
            if (i > 0) {
                include = include.substring(0, i);
            }
            out.add(include);
        }
        return out;
    }

    private TCField makeField(String pkg, ThriftField field) {
        TDescriptorProvider type = mRegistry.getProvider(field.getType(), pkg);
        TConstProvider defaultValue = null;
        if (field.hasDefaultValue()) {
            defaultValue = new TConstProvider(mRegistry,
                                              field.getType(),
                                              pkg,
                                              field.getDefaultValue());
        }
        @SuppressWarnings("unchecked")
        TCField made = new TCField<>(
                field.getComment(),
                field.getKey(),
                field.getIsRequired(),
                field.getName(),
                type,
                defaultValue);
        return made;
    }
}
