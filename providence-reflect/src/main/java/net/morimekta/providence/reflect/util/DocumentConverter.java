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
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PServiceProvider;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.model.EnumType;
import net.morimekta.providence.model.EnumValue;
import net.morimekta.providence.model.ServiceMethod;
import net.morimekta.providence.model.ServiceType;
import net.morimekta.providence.model.StructType;
import net.morimekta.providence.model.ThriftDocument;
import net.morimekta.providence.model.ThriftField;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.contained.CEnumValue;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.providence.reflect.contained.CExceptionDescriptor;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.providence.reflect.contained.CServiceMethod;
import net.morimekta.providence.reflect.contained.CStructDescriptor;
import net.morimekta.providence.reflect.contained.CUnionDescriptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class DocumentConverter {
    private final TypeRegistry registry;

    public DocumentConverter(TypeRegistry registry) {
        this.registry = registry;
    }

    /**
     * Convert document model to declared document.
     *
     * @param document Document model to convert.
     * @return The declared thrift document.
     */
    public CDocument convert(ThriftDocument document) {
        ImmutableList.Builder<PDeclaredDescriptor<?>> declaredTypes = ImmutableList.builder();
        ImmutableList.Builder<CField> constants = ImmutableList.builder();
        ImmutableMap.Builder<String, String> typedefs = ImmutableMap.builder();
        ImmutableList.Builder<CService> services = ImmutableList.builder();

        for (Declaration decl : document.getDecl()) {
            switch (decl.unionField()) {
                case DECL_ENUM: {
                    EnumType enumType = decl.getDeclEnum();

                    int nextValue = PEnumDescriptor.DEFAULT_FIRST_VALUE;
                    CEnumDescriptor type = new CEnumDescriptor(enumType.getComment(),
                                                               document.getPackage(),
                                                               enumType.getName(),
                                                               enumType.getAnnotations());
                    List<CEnumValue> values = new LinkedList<>();
                    for (EnumValue value : enumType.getValues()) {
                        int v = value.hasValue() ? value.getValue() : nextValue;
                        nextValue = v + 1;
                        values.add(new CEnumValue(value.getComment(), value.getValue(), value.getName(), type, value.getAnnotations()));
                    }
                    type.setValues(values);
                    declaredTypes.add(type);
                    registry.putDeclaredType(type);
                    break;
                }
                case DECL_STRUCT: {
                    StructType structType = decl.getDeclStruct();

                    List<CField> fields = new LinkedList<>();
                    if (structType.hasFields()) {
                        fields.addAll(structType.getFields()
                                                .stream()
                                                .map(field -> makeField(document.getPackage(), field))
                                                .collect(Collectors.toList()));
                    }
                    PStructDescriptor<?, ?> type;
                    switch (structType.getVariant()) {
                        case STRUCT:
                            type = new CStructDescriptor(structType.getComment(),
                                                         document.getPackage(),
                                                         structType.getName(),
                                                         fields,
                                                         structType.getAnnotations());
                            break;
                        case UNION:
                            type = new CUnionDescriptor(structType.getComment(),
                                                        document.getPackage(),
                                                        structType.getName(),
                                                        fields,
                                                        structType.getAnnotations());
                            break;
                        case EXCEPTION:
                            type = new CExceptionDescriptor(structType.getComment(),
                                                            document.getPackage(),
                                                            structType.getName(),
                                                            fields,
                                                            structType.getAnnotations());
                            break;
                        default:
                            throw new IllegalArgumentException("Unhandled struct type " + structType.getVariant());
                    }
                    declaredTypes.add(type);
                    registry.putDeclaredType(type);
                    break;
                }
                case DECL_CONST: {
                    ThriftField constant = decl.getDeclConst();
                    constants.add(makeField(document.getPackage(), constant));
                    break;
                }
                case DECL_TYPEDEF: {
                    typedefs.put(decl.getDeclTypedef()
                                     .getName(),
                                 decl.getDeclTypedef()
                                     .getType());
                    registry.putTypedef(decl.getDeclTypedef()
                                            .getType(),
                                        decl.getDeclTypedef()
                                             .getName());
                    break;
                }
                case DECL_SERVICE: {
                    ServiceType serviceType = decl.getDeclService();
                    ImmutableList.Builder<CServiceMethod> methodBuilder = ImmutableList.builder();
                    for (ServiceMethod sm : serviceType.getMethods()) {
                        List<CField> rqFields = new LinkedList<>();
                        if (sm.numParams() > 0) {
                            for (ThriftField field : sm.getParams()) {
                                rqFields.add(makeField(document.getPackage(), field));
                            }
                        }
                        CStructDescriptor request = new CStructDescriptor(null,
                                                                          document.getPackage(),
                                                                          sm.getName() + "___request",
                                                                          rqFields,
                                                                          null);

                        CUnionDescriptor response = null;
                        if (!sm.isOneWay()) {
                            List<CField> rsFields = new LinkedList<>();
                            if (sm.getReturnType() != null) {
                                PDescriptorProvider type = registry.getProvider(sm.getReturnType(), document.getPackage());

                                CField success = new CField(null,
                                                            0,
                                                            PRequirement.OPTIONAL,
                                                            "success",
                                                            type,
                                                            null,
                                                            null);
                                rsFields.add(success);
                            }

                            if (sm.numExceptions() > 0) {
                                for (ThriftField field : sm.getExceptions()) {
                                    rsFields.add(makeField(document.getPackage(), field));
                                }
                            }

                            response = new CUnionDescriptor(null,
                                                            document.getPackage(),
                                                            sm.getName() + "___response",
                                                            rsFields,
                                                            null);
                        }

                        CServiceMethod method = new CServiceMethod(sm.getComment(),
                                                                   sm.getName(),
                                                                   sm.isOneWay(),
                                                                   request,
                                                                   response,
                                                                   sm.getAnnotations());

                        methodBuilder.add(method);
                    }

                    PServiceProvider extendsProvider = null;
                    if (serviceType.hasExtend()) {
                        extendsProvider = registry.getServiceProvider(serviceType.getExtend(), document.getPackage());
                    }

                    CService service = new CService(serviceType.getComment(),
                                                    document.getPackage(),
                                                    serviceType.getName(),
                                                    extendsProvider,
                                                    methodBuilder.build(),
                                                    serviceType.getAnnotations());

                    services.add(service);
                    registry.putService(service);
                }
            }
        }

        return new CDocument(document.getComment(),
                             document.getPackage(),
                             document.getNamespaces(),
                             getIncludes(document),
                             typedefs.build(),
                             declaredTypes.build(),
                             services.build(),
                             constants.build());
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
        PDescriptorProvider type = registry.getProvider(field.getType(), pkg);
        ConstProvider defaultValue = null;
        if (field.hasDefaultValue()) {
            defaultValue = new ConstProvider(registry, field.getType(), pkg, field.getDefaultValue());
        }
        @SuppressWarnings("unchecked")
        CField made = new CField(field.getComment(),
                                 field.getKey(),
                                 PRequirement.valueOf(field.getRequirement()
                                                           .getName()),
                                 field.getName(),
                                 type,
                                 defaultValue,
                                 field.getAnnotations());
        return made;
    }
}
