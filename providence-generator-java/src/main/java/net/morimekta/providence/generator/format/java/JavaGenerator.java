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
package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.format.java.program.extras.HazelcastPortableProgramFormatter;
import net.morimekta.providence.generator.format.java.shared.BaseEnumFormatter;
import net.morimekta.providence.generator.format.java.shared.BaseMessageFormatter;
import net.morimekta.providence.generator.format.java.shared.BaseProgramFormatter;
import net.morimekta.providence.generator.format.java.shared.BaseServiceFormatter;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;
import net.morimekta.util.io.IndentedPrintWriter;

import javax.annotation.Nonnull;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Stein Eldar Johnsen
 * @since 05.09.15
 */
public class JavaGenerator extends Generator {
    private final GeneratorOptions generatorOptions;
    private final JavaOptions      javaOptions;

    public JavaGenerator(FileManager manager,
                         GeneratorOptions generatorOptions,
                         JavaOptions javaOptions) throws GeneratorException {
        super(manager);

        this.generatorOptions = generatorOptions;
        this.javaOptions = javaOptions;
    }

    private BaseMessageFormatter messageFormatter(IndentedPrintWriter writer, JHelper helper) {
        return new JavaMessageFormatter(writer, helper, generatorOptions, javaOptions);
    }

    private BaseEnumFormatter enumFormatter(IndentedPrintWriter writer) {
        return new JavaEnumFormatter(writer, generatorOptions, javaOptions);
    }

    private BaseProgramFormatter constFomatter(IndentedPrintWriter writer, JHelper helper) {
        return new JavaConstantsFormatter(writer, helper, generatorOptions, javaOptions);
    }

    private BaseProgramFormatter hazelcastFomatter(IndentedPrintWriter writer, JHelper helper) {
        return new HazelcastPortableProgramFormatter(writer, helper, generatorOptions, javaOptions);
    }

    private BaseServiceFormatter serviceFormatter(IndentedPrintWriter writer, JHelper helper) {
        return new JavaServiceFormatter(
                writer,
                helper,
                new JavaMessageFormatter(true,
                                         true,
                                         writer,
                                         helper,
                                         generatorOptions,
                                         javaOptions),
                generatorOptions,
                javaOptions);
    }

    @Override
    @SuppressWarnings("resource")
    public void generate(@Nonnull ProgramTypeRegistry registry) throws IOException, GeneratorException {
        CProgram program = registry.getProgram();
        String javaPackage = JUtils.getJavaPackage(program);
        JHelper helper = new JHelper(registry);

        String path = JUtils.getPackageClassPath(javaPackage);

        if (program.getConstants().size() > 0) {
            String file = helper.getConstantsClassName(program) + ".java";
            OutputStream out = new BufferedOutputStream(getFileManager().create(path, file));
            try {
                IndentedPrintWriter writer = new IndentedPrintWriter(out);

                appendFileHeader(writer, helper, program);
                constFomatter(writer, helper).appendProgramClass(program);

                writer.flush();
            } finally {
                try {
                    getFileManager().finalize(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (javaOptions.hazelcast_portable &&
            program.getConstants()
                   .stream().anyMatch(t -> t.getName().equals("FACTORY_ID"))) {

            String file = helper.getHazelcastFactoryClassName(program) + ".java";
            OutputStream out = new BufferedOutputStream(getFileManager().create(path, file));
            try {
                IndentedPrintWriter writer = new IndentedPrintWriter(out);

                appendFileHeader(writer, helper, program);
                hazelcastFomatter(writer, helper).appendProgramClass(program);

                writer.flush();
            } finally {
                try {
                    getFileManager().finalize(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (PDeclaredDescriptor<?> type : program.getDeclaredTypes()) {
            String file = JUtils.getClassName(type) + ".java";
            OutputStream out = new BufferedOutputStream(getFileManager().create(path, file));
            try {
                IndentedPrintWriter writer = new IndentedPrintWriter(out);

                appendFileHeader(writer, helper, program);

                switch (type.getType()) {
                    case MESSAGE:
                        messageFormatter(writer, helper).appendMessageClass((PMessageDescriptor<?, ?>) type);
                        break;
                    case ENUM:
                        enumFormatter(writer).appendEnumClass((CEnumDescriptor) type);
                        break;
                    default:
                        throw new GeneratorException("Unhandled declaration type.");

                }
                writer.flush();
            } finally {
                try {
                    getFileManager().finalize(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (CService service : program.getServices()) {
            String file = JUtils.getClassName(service) + ".java";
            OutputStream out = new BufferedOutputStream(getFileManager().create(path, file));
            try {
                IndentedPrintWriter writer = new IndentedPrintWriter(out);
                appendFileHeader(writer, helper, program);
                serviceFormatter(writer, helper).appendServiceClass(service);
                writer.flush();
            } finally {
                try {
                    getFileManager().finalize(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void appendFileHeader(IndentedPrintWriter writer,
                                  JHelper helper,
                                  CProgram document)
            throws GeneratorException, IOException {
        writer.format("package %s;", helper.getJavaPackage(document))
              .newline();
    }
}
