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
import net.morimekta.providence.util.TypeRegistry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static net.morimekta.providence.reflect.util.ReflectionUtils.programNameFromPath;

/**
 * Helper class that converts a parsed structured thrift model into the
 * contained descriptors used when managing thrift models and descriptors
 * in memory. This is use both in the various providence tools and in
 * the providence generators.
 */
public class ProgramConverter {
    private final ProgramRegistry programRegistry;

    /**
     * Create a program converter that uses the given registry for type
     * references.
     *
     * @param programRegistry The program registry.
     */
    public ProgramConverter(ProgramRegistry programRegistry) {
        this.programRegistry = programRegistry;
    }

    /**
     * Convert document model to declared document.
     *
     * @param path The program file path.
     * @param path Path of the program file to convert.
     * @param program Program model to convert.
     * @return The declared thrift document.
     */
    public CProgram convert(String path, ProgramType program) {
        ImmutableList.Builder<PDeclaredDescriptor<?>> declaredTypes = ImmutableList.builder();
        ImmutableList.Builder<CConst> constants = ImmutableList.builder();
        ImmutableMap.Builder<String, String> typedefs = ImmutableMap.builder();
        ImmutableList.Builder<CService> services = ImmutableList.builder();

        RecursiveTypeRegistry registry = programRegistry.registryForPath(path);

        File dir = new File(path).getParentFile();
        if (program.hasIncludes()) {
            for (String include : program.getIncludes()) {
                String includePath = new File(dir, include).getPath();
                registry.registerInclude(ReflectionUtils.programNameFromPath(include),
                                         programRegistry.registryForPath(includePath));
            }
        }

        for (Declaration decl : program.getDecl()) {
            switch (decl.unionField()) {
                case DECL_ENUM: {
                    EnumType enumType = decl.getDeclEnum();

                    int nextValue = PEnumDescriptor.DEFAULT_FIRST_VALUE;
                    CEnumDescriptor type = new CEnumDescriptor(enumType.getDocumentation(),
                                                               program.getProgramName(),
                                                               enumType.getName(),
                                                               enumType.getAnnotations());
                    List<CEnumValue> values = new ArrayList<>();
                    for (EnumValue value : enumType.getValues()) {
                        int v = value.hasId() ? value.getId() : nextValue;
                        nextValue = v + 1;
                        values.add(new CEnumValue(value.getDocumentation(), value.getId(), value.getName(), type, value.getAnnotations()));
                    }
                    type.setValues(values);
                    declaredTypes.add(type);
                    registry.register(type);
                    break;
                }
                case DECL_STRUCT: {
                    MessageType messageType = decl.getDeclStruct();

                    List<CField> fields = new ArrayList<>();
                    if (messageType.hasFields()) {
                        fields.addAll(messageType.getFields()
                                                .stream()
                                                .map(field -> makeField(registry, program.getProgramName(), field, messageType.getVariant()))
                                                .collect(Collectors.toList()));
                    }
                    PMessageDescriptor<?, ?> type;
                    switch (messageType.getVariant()) {
                        case STRUCT:
                            type = new CStructDescriptor(messageType.getDocumentation(),
                                                         program.getProgramName(),
                                                         messageType.getName(),
                                                         fields,
                                                         messageType.getAnnotations());
                            break;
                        case UNION:
                            type = new CUnionDescriptor(messageType.getDocumentation(),
                                                        program.getProgramName(),
                                                        messageType.getName(),
                                                        fields,
                                                        messageType.getAnnotations());
                            break;
                        case EXCEPTION:
                            type = new CExceptionDescriptor(messageType.getDocumentation(),
                                                            program.getProgramName(),
                                                            messageType.getName(),
                                                            fields,
                                                            messageType.getAnnotations());
                            break;
                        default:
                            throw new UnsupportedOperationException("Unhandled message variant " + messageType.getVariant());
                    }
                    declaredTypes.add(type);
                    registry.register(type);
                    break;
                }
                case DECL_CONST: {
                    ConstType constant = decl.getDeclConst();
                    CConst cv = makeConst(registry, program.getProgramName(), constant);
                    constants.add(cv);
                    registry.registerConstant(cv.getName(), program.getProgramName(), cv.getDefaultValue());
                    break;
                }
                case DECL_TYPEDEF: {
                    typedefs.put(decl.getDeclTypedef()
                                     .getName(),
                                 decl.getDeclTypedef()
                                     .getType());
                    registry.registerTypedef(decl.getDeclTypedef()
                                            .getName(),
                                        program.getProgramName(),
                                        decl.getDeclTypedef()
                                            .getType());
                    break;
                }
                case DECL_SERVICE: {
                    ServiceType serviceType = decl.getDeclService();
                    ImmutableList.Builder<CServiceMethod> methodBuilder = ImmutableList.builder();
                    if (serviceType.hasMethods()) {
                        for (FunctionType sm : serviceType.getMethods()) {
                            List<CField> rqFields = new ArrayList<>();
                            if (sm.numParams() > 0) {
                                for (FieldType field : sm.getParams()) {
                                    rqFields.add(makeField(registry, program.getProgramName(), field, MessageVariant.STRUCT));
                                }
                            }
                            CStructDescriptor request = new CStructDescriptor(null,
                                                                              program.getProgramName(),
                                                                              serviceType.getName() + '.' +
                                                                              sm.getName() + ".request",
                                                                              rqFields,
                                                                              null);

                            CUnionDescriptor response = null;
                            if (!sm.isOneWay()) {
                                List<CField> rsFields = new ArrayList<>();
                                CField success;
                                if (sm.getReturnType() != null) {
                                    PDescriptorProvider type = registry.getProvider(sm.getReturnType(),
                                                                                    program.getProgramName(),
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
                                        rsFields.add(makeField(registry, program.getProgramName(), field, MessageVariant.UNION));
                                    }
                                }

                                response = new CUnionDescriptor(null,
                                                                program.getProgramName(),
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
                        extendsProvider = registry.getServiceProvider(serviceType.getExtend(), program.getProgramName());
                    }

                    CService service = new CService(serviceType.getDocumentation(),
                                                    program.getProgramName(),
                                                    serviceType.getName(),
                                                    extendsProvider,
                                                    methodBuilder.build(),
                                                    serviceType.getAnnotations());

                    services.add(service);
                    registry.registerRecursively(service);
                }
            }
        }

        return new CProgram(path,
                            program.getDocumentation(),
                            program.getProgramName(),
                            program.getNamespaces(),
                            getIncludedProgramNames(program),
                            program.getIncludes(),
                            typedefs.build(),
                            declaredTypes.build(),
                            services.build(),
                            constants.build());
    }

    private Set<String> getIncludedProgramNames(ProgramType document) {
        Set<String> out = new TreeSet<>();
        if (document.hasIncludes()) {
            for (String include : document.getIncludes()) {
                String program = programNameFromPath(include);
                if (out.contains(program)) {
                    throw new IllegalArgumentException("Program " + document.getProgramName() + " includes multiple programs of name " + program);
                }
                out.add(program);
            }
        }
        return out;
    }

    private CConst makeConst(TypeRegistry registry, String pkg, ConstType field) {
        PDescriptorProvider type = registry.getProvider(field.getType(), pkg, field.getAnnotations());
        if (!field.hasValue()) {
            throw new IllegalArgumentException("Const " + pkg + "." + field.getName() + " does not have a value.");
        }
        ConstProvider defaultValue = new ConstProvider(registry,
                                                       field.getType(),
                                                       pkg,
                                                       field.getValue(),
                                                       field.getStartLineNo(),
                                                       field.getStartLinePos());

        @SuppressWarnings("unchecked")
        CConst made = new CConst(field.getDocumentation(),
                                 field.getName(),
                                 type,
                                 defaultValue,
                                 field.getAnnotations());
        return made;
    }

    private CField makeField(TypeRegistry registry, String pkg, FieldType field, MessageVariant variant) {
        PDescriptorProvider type = registry.getProvider(field.getType(), pkg, field.getAnnotations());
        ConstProvider defaultValue = null;
        if (field.hasDefaultValue()) {
            defaultValue = new ConstProvider(registry,
                                             field.getType(),
                                             pkg,
                                             field.getDefaultValue(),
                                             field.getStartLineNo(),
                                             field.getStartLinePos());
        }
        PRequirement requirement = PRequirement.valueOf(field.getRequirement()
                                                             .asString());
        if (variant == MessageVariant.UNION) {
            if (requirement == PRequirement.REQUIRED) {
                throw new IllegalArgumentException("Required field in union");
            }
            requirement = PRequirement.OPTIONAL;
        }
        @SuppressWarnings("unchecked")
        CField made = new CField(field.getDocumentation(),
                                 field.getId(),
                                 requirement,
                                 field.getName(),
                                 type,
                                 defaultValue,
                                 field.getAnnotations());
        return made;
    }
}
