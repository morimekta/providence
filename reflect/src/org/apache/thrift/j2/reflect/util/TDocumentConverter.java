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

import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.model.EnumValue;
import org.apache.thrift.j2.reflect.contained.TContainedDocument;
import org.apache.thrift.j2.reflect.contained.TContainedEnumDescriptor;
import org.apache.thrift.j2.reflect.contained.TContainedStructDescriptor;
import org.apache.thrift.j2.descriptor.TDeclaredDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TServiceDescriptor;
import org.apache.thrift.j2.descriptor.TServiceMethod;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.model.Declaration;
import org.apache.thrift.j2.model.EnumType;
import org.apache.thrift.j2.model.ServiceMethod;
import org.apache.thrift.j2.model.ServiceType;
import org.apache.thrift.j2.model.StructType;
import org.apache.thrift.j2.model.ThriftDocument;
import org.apache.thrift.j2.model.ThriftField;
import org.apache.thrift.j2.reflect.contained.TContainedExceptionDescriptor;
import org.apache.thrift.j2.reflect.contained.TContainedServiceDescriptor;
import org.apache.thrift.j2.reflect.contained.TContainedServiceMethod;
import org.apache.thrift.j2.reflect.contained.TContainedUnionDescriptor;

/**
 *
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 07.09.15
 *
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
    public TContainedDocument convert(ThriftDocument document) {
        List<TDeclaredDescriptor<?>> declaredTypes = new LinkedList<>();
        List<TServiceDescriptor> services = new LinkedList<>();
        List<TField<?>> constants = new LinkedList<>();
        Map<String, String> typedefs = new LinkedHashMap<>();

        for (Declaration decl : document.getDecl()) {
            if (decl.hasDeclEnum()) {
                EnumType enumType = decl.getDeclEnum();

                List<TEnumDescriptor.Value> values = new LinkedList<>();
                int nextValue = TEnumDescriptor.DEFAULT_FIRST_VALUE;
                for (EnumValue value : enumType.getValues()) {
                    int v = value.hasValue() ? value.getValue() : nextValue;
                    nextValue = v + 1;
                    values.add(new TEnumDescriptor.Value(value.getComment(),
                                                   value.getName(),
                                                   v));
                }
                TEnumDescriptor<?> type = new TContainedEnumDescriptor(enumType.getComment(),
                                                           document.getPackage(),
                                                           enumType.getName(),
                                                           values);
                declaredTypes.add(type);
                mRegistry.putDeclaredType(type);
            }
            if (decl.hasDeclStruct()) {
                StructType structType = decl.getDeclStruct();

                List<TField<?>> fields = new LinkedList<>();
                for (ThriftField field : structType.getFields()) {
                    fields.add(makeField(document.getPackage(), field));
                }
                TStructDescriptor<?> type;
                switch (structType.getVariant()) {
                    case STRUCT:
                        type = new TContainedStructDescriptor(structType.getComment(),
                                                              document.getPackage(),
                                                              structType.getName(),
                                                              fields);
                        break;
                    case UNION:
                        type = new TContainedUnionDescriptor(structType.getComment(),
                                                             document.getPackage(),
                                                             structType.getName(),
                                                             fields);
                        break;
                    case EXCEPTION:
                        type = new TContainedExceptionDescriptor(structType.getComment(),
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
            if (decl.hasDeclService()) {
                ServiceType service = decl.getDeclService();

                List<TServiceMethod<?,?,?>> methods = new LinkedList<>();
                for (ServiceMethod method : service.getMethods()) {
                    TDescriptorProvider returnType = TPrimitive.VOID.provider();
                    if (method.hasReturnType()) {
                        returnType = mRegistry.getProvider(method.getReturnType(),
                                                           document.getPackage());
                    }
                    List<TField<?>> params = new LinkedList<>();
                    for (ThriftField field : method.getParams()) {
                        params.add(makeField(document.getPackage(), field));
                    }

                    List<TField<?>> exceptions = new LinkedList<>();
                    for (ThriftField field : method.getExceptions()) {
                        exceptions.add(makeField(document.getPackage(), field));
                    }

                    methods.add(new TContainedServiceMethod(method.getComment(),
                                                            method.getIsOneway(),
                                                            service.getName(),
                                                            method.getName(),
                                                            document.getPackage(),
                                                            returnType,
                                                            params,
                                                            exceptions));
                }
                services.add(new TContainedServiceDescriptor(service.getComment(),
                                                       document.getPackage(),
                                                       service.getName(),
                                                       methods));
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

        return new TContainedDocument(document.getComment(),
                                      document.getPackage(),
                                      document.getNamespaces(),
                                      getIncludes(document),
                                      typedefs,
                                      declaredTypes,
                                      services,
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

    private TField<?> makeField(String pkg, ThriftField field) {
        TDescriptorProvider type = mRegistry.getProvider(field.getType(), pkg);
        TConstProvider defaultValue = null;
        if (field.hasDefaultValue()) {
            defaultValue = new TConstProvider(mRegistry,
                                              field.getType(),
                                              pkg,
                                              field.getDefaultValue());
        }
        @SuppressWarnings("unchecked")
        TField<?> made = new TField<>(field.getComment(),
                                      field.getKey(),
                                      field.getIsRequired(),
                                      field.getName(),
                                      type,
                                      defaultValue);
        return made;
    }
}
