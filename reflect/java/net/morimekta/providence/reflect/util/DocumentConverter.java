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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.morimekta.providence.descriptor.*;
import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.reflect.contained.CEnum;
import net.morimekta.providence.reflect.contained.CUnionDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.model.EnumType;
import net.morimekta.providence.model.EnumValue;
import net.morimekta.providence.model.StructType;
import net.morimekta.providence.model.ThriftDocument;
import net.morimekta.providence.model.ThriftField;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.providence.reflect.contained.CExceptionDescriptor;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CStructDescriptor;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class DocumentConverter {
    private final TypeRegistry mRegistry;

    public DocumentConverter(TypeRegistry registry) {
        mRegistry = registry;
    }

    /**
     * Convert document model to declared document.
     *
     * @param document Document model to convert.
     * @return The declared thrift document.
     */
    public CDocument convert(ThriftDocument document) {
        List<PDeclaredDescriptor<?>> declaredTypes = new LinkedList<>();
        List<PField<?>> constants = new LinkedList<>();
        Map<String, String> typedefs = new LinkedHashMap<>();

        for (Declaration decl : document.getDecl()) {
            if (decl.hasDeclEnum()) {
                EnumType enumType = decl.getDeclEnum();

                int nextValue = PEnumDescriptor.DEFAULT_FIRST_VALUE;
                CEnumDescriptor type =
                        new CEnumDescriptor(enumType.getComment(),
                                             document.getPackage(),
                                             enumType.getName());
                List<CEnum> values = new LinkedList<>();
                for (EnumValue value : enumType.getValues()) {
                    int v = value.hasValue() ? value.getValue() : nextValue;
                    nextValue = v + 1;
                    values.add(new CEnum(value.getComment(),
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

                List<CField> fields = new LinkedList<>();
                for (ThriftField field : structType.getFields()) {
                    fields.add(makeField(document.getPackage(), field));
                }
                PStructDescriptor<?, ?> type;
                switch (structType.getVariant()) {
                    case STRUCT:
                        type = new CStructDescriptor(structType.getComment(),
                                                      document.getPackage(),
                                                      structType.getName(),
                                                      fields);
                        break;
                    case UNION:
                        type = new CUnionDescriptor(structType.getComment(),
                                                     document.getPackage(),
                                                     structType.getName(),
                                                     fields);
                        break;
                    case EXCEPTION:
                        type = new CExceptionDescriptor(structType.getComment(),
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

        return new CDocument(document.getComment(),
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

    private CField makeField(String pkg, ThriftField field) {
        PDescriptorProvider type = mRegistry.getProvider(field.getType(), pkg);
        ConstProvider defaultValue = null;
        if (field.hasDefaultValue()) {
            defaultValue = new ConstProvider(mRegistry,
                                              field.getType(),
                                              pkg,
                                              field.getDefaultValue());
        }
        @SuppressWarnings("unchecked")
        CField made = new CField<>(
                field.getComment(),
                field.getKey(),
                field.getIsRequired(),
                field.getName(),
                type,
                defaultValue);
        return made;
    }
}
