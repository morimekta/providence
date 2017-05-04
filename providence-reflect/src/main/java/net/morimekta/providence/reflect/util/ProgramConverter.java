/*
 * Copyright 2016 Providence Authors
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
package net.morimekta.providence.reflect.util;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PServiceProvider;
import net.morimekta.providence.model.ConstType;
import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.model.EnumType;
import net.morimekta.providence.model.EnumValue;
import net.morimekta.providence.model.FieldType;
import net.morimekta.providence.model.FunctionType;
import net.morimekta.providence.model.MessageType;
import net.morimekta.providence.model.MessageVariant;
import net.morimekta.providence.model.ProgramType;
import net.morimekta.providence.model.ServiceType;
import net.morimekta.providence.reflect.contained.CConst;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.providence.reflect.contained.CEnumValue;
import net.morimekta.providence.reflect.contained.CExceptionDescriptor;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.providence.reflect.contained.CServiceMethod;
import net.morimekta.providence.reflect.contained.CStructDescriptor;
import net.morimekta.providence.reflect.contained.CUnionDescriptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class ProgramConverter {
    private final ProgramRegistry registry;

    public ProgramConverter(ProgramRegistry registry) {
        this.registry = registry;
    }

    /**
     * Convert document model to declared document.
     *
     * @param document Document model to convert.
     * @return The declared thrift document.
     */
    public CProgram convert(ProgramType document) {
        ImmutableList.Builder<PDeclaredDescriptor<?>> declaredTypes = ImmutableList.builder();
        ImmutableList.Builder<CConst> constants = ImmutableList.builder();
        ImmutableMap.Builder<String, String> typedefs = ImmutableMap.builder();
        ImmutableList.Builder<CService> services = ImmutableList.builder();

        for (Declaration decl : document.getDecl()) {
            switch (decl.unionField()) {
                case DECL_ENUM: {
                    EnumType enumType = decl.getDeclEnum();

                    int nextValue = PEnumDescriptor.DEFAULT_FIRST_VALUE;
                    CEnumDescriptor type = new CEnumDescriptor(enumType.getDocumentation(),
                                                               document.getProgramName(),
                                                               enumType.getName(),
                                                               enumType.getAnnotations());
                    List<CEnumValue> values = new LinkedList<>();
                    for (EnumValue value : enumType.getValues()) {
                        int v = value.hasValue() ? value.getValue() : nextValue;
                        nextValue = v + 1;
                        values.add(new CEnumValue(value.getDocumentation(), value.getValue(), value.getName(), type, value.getAnnotations()));
                    }
                    type.setValues(values);
                    declaredTypes.add(type);
                    registry.putDeclaredType(type);
                    break;
                }
                case DECL_STRUCT: {
                    MessageType messageType = decl.getDeclStruct();

                    List<CField> fields = new LinkedList<>();
                    if (messageType.hasFields()) {
                        fields.addAll(messageType.getFields()
                                                .stream()
                                                .map(field -> makeField(document.getProgramName(), field, messageType.getVariant()))
                                                .collect(Collectors.toList()));
                    }
                    PMessageDescriptor<?, ?> type;
                    switch (messageType.getVariant()) {
                        case STRUCT:
                            type = new CStructDescriptor(messageType.getDocumentation(),
                                                         document.getProgramName(),
                                                         messageType.getName(),
                                                         fields,
                                                         messageType.getAnnotations());
                            break;
                        case UNION:
                            type = new CUnionDescriptor(messageType.getDocumentation(),
                                                        document.getProgramName(),
                                                        messageType.getName(),
                                                        fields,
                                                        messageType.getAnnotations());
                            break;
                        case EXCEPTION:
                            type = new CExceptionDescriptor(messageType.getDocumentation(),
                                                            document.getProgramName(),
                                                            messageType.getName(),
                                                            fields,
                                                            messageType.getAnnotations());
                            break;
                        default:
                            throw new IllegalArgumentException("Unhandled struct type " + messageType.getVariant());
                    }
                    declaredTypes.add(type);
                    registry.putDeclaredType(type);
                    break;
                }
                case DECL_CONST: {
                    ConstType constant = decl.getDeclConst();
                    constants.add(makeConst(document.getProgramName(), constant));
                    break;
                }
                case DECL_TYPEDEF: {
                    typedefs.put(decl.getDeclTypedef()
                                     .getName(),
                                 decl.getDeclTypedef()
                                     .getType());
                    registry.putTypedef(decl.getDeclTypedef()
                                            .getName(),
                                        document.getProgramName(),
                                        decl.getDeclTypedef()
                                             .getType());
                    break;
                }
                case DECL_SERVICE: {
                    ServiceType serviceType = decl.getDeclService();
                    ImmutableList.Builder<CServiceMethod> methodBuilder = ImmutableList.builder();
                    if (serviceType.hasMethods()) {
                        for (FunctionType sm : serviceType.getMethods()) {
                            List<CField> rqFields = new LinkedList<>();
                            if (sm.numParams() > 0) {
                                for (FieldType field : sm.getParams()) {
                                    rqFields.add(makeField(document.getProgramName(), field, MessageVariant.STRUCT));
                                }
                            }
                            CStructDescriptor request = new CStructDescriptor(null,
                                                                              document.getProgramName(),
                                                                              serviceType.getName() + '.' +
                                                                              sm.getName() + ".request",
                                                                              rqFields,
                                                                              null);

                            CUnionDescriptor response = null;
                            if (!sm.isOneWay()) {
                                List<CField> rsFields = new LinkedList<>();
                                CField success;
                                if (sm.getReturnType() != null) {
                                    PDescriptorProvider type = registry.getProvider(sm.getReturnType(),
                                                                                    document.getProgramName(),
                                                                                    sm.getAnnotations());
                                    success = new CField(null, 0, PRequirement.OPTIONAL, "success", type, null, null);
                                } else {
                                    success = new CField(null,
                                                         0,
                                                         PRequirement.OPTIONAL,
                                                         "success",
                                                         PPrimitive.VOID.provider(),
                                                         null,
                                                         null);
                                }
                                rsFields.add(success);

                                if (sm.numExceptions() > 0) {
                                    for (FieldType field : sm.getExceptions()) {
                                        rsFields.add(makeField(document.getProgramName(), field, MessageVariant.UNION));
                                    }
                                }

                                response = new CUnionDescriptor(null,
                                                                document.getProgramName(),
                                                                serviceType.getName() + '.' +
                                                                sm.getName() + ".response",
                                                                rsFields,
                                                                null);
                            }

                            CServiceMethod method = new CServiceMethod(sm.getDocumentation(),
                                                                       sm.getName(),
                                                                       sm.isOneWay(),
                                                                       request,
                                                                       response,
                                                                       sm.getAnnotations());

                            methodBuilder.add(method);
                        }  // for each method
                    }  // if has methods

                    PServiceProvider extendsProvider = null;
                    if (serviceType.hasExtend()) {
                        extendsProvider = registry.getServiceProvider(serviceType.getExtend(), document.getProgramName());
                    }

                    CService service = new CService(serviceType.getDocumentation(),
                                                    document.getProgramName(),
                                                    serviceType.getName(),
                                                    extendsProvider,
                                                    methodBuilder.build(),
                                                    serviceType.getAnnotations());

                    services.add(service);
                    registry.putService(service);
                }
            }
        }

        return new CProgram(document.getDocumentation(),
                            document.getProgramName(),
                            document.getNamespaces(),
                            getIncludedProgramNames(document),
                            document.getIncludes(),
                            typedefs.build(),
                            declaredTypes.build(),
                            services.build(),
                            constants.build());
    }

    private Set<String> getIncludedProgramNames(ProgramType document) {
        Set<String> out = new TreeSet<>();
        if (document.hasIncludes()) {
            for (String include : document.getIncludes()) {
                int i = include.lastIndexOf('.');
                if (i > 0) {
                    include = include.substring(0, i);
                }
                if (include.contains("/")) {
                    include = include.replaceAll(".*[/]", "");
                }
                out.add(include);
            }
        }
        return out;
    }

    private CConst makeConst(String pkg, ConstType field) {
        PDescriptorProvider type = registry.getProvider(field.getType(), pkg, field.getAnnotations());
        if (!field.hasValue()) {
            throw new IllegalArgumentException("Const " + pkg + "." + field.getName() + " does not have a value.");
        }
        ConstProvider defaultValue = new ConstProvider(registry, field.getType(), pkg, field.getValue());

        @SuppressWarnings("unchecked")
        CConst made = new CConst(field.getDocumentation(),
                                 field.getName(),
                                 type,
                                 defaultValue,
                                 field.getAnnotations());
        return made;
    }

    private CField makeField(String pkg, FieldType field, MessageVariant variant) {
        PDescriptorProvider type = registry.getProvider(field.getType(), pkg, field.getAnnotations());
        ConstProvider defaultValue = null;
        if (field.hasDefaultValue()) {
            defaultValue = new ConstProvider(registry, field.getType(), pkg, field.getDefaultValue());
        }
        PRequirement requirement = PRequirement.valueOf(field.getRequirement()
                                                             .getName());
        if (variant == MessageVariant.UNION) {
            if (requirement == PRequirement.REQUIRED) {
                throw new IllegalArgumentException("Required field in union");
            }
            requirement = PRequirement.OPTIONAL;
        }
        @SuppressWarnings("unchecked")
        CField made = new CField(field.getDocumentation(),
                                 field.getKey(),
                                 requirement,
                                 field.getName(),
                                 type,
                                 defaultValue,
                                 field.getAnnotations());
        return made;
    }
}
