/*
 * Copyright 2017 Providence Authors
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
package net.morimekta.providence.generator.format.js;

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.format.js.formatter.JSProgramFormatter;
import net.morimekta.providence.generator.format.js.formatter.ProgramFormatter;
import net.morimekta.providence.generator.format.js.utils.JSUtils;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Generate JS message models for providence.
 *
 * Supports the providence styled JSON (both named and compact) as data
 * format.
 */
public class JSGenerator extends Generator {
    private final List<ProgramFormatter> formatter;
    private final GeneratorOptions       generatorOptions;

    public JSGenerator(FileManager manager,
                       ProgramTypeRegistry registry, GeneratorOptions generatorOptions, JSOptions jsOptions) throws GeneratorException {
        super(manager);

        ImmutableList.Builder<ProgramFormatter> builder = ImmutableList.builder();
        builder.add(new JSProgramFormatter(jsOptions, registry));
        this.formatter = builder.build();
        this.generatorOptions = generatorOptions;
    }

    @Override
    @SuppressWarnings("resource")
    public void generate(CProgram program) throws IOException, GeneratorException {
        String path = JSUtils.getPackageClassPath(program);

        for (ProgramFormatter formatter : formatter) {
            String fileName = formatter.getFileName(program);
            OutputStream out = new BufferedOutputStream(getFileManager().create(path, fileName));
            try {
                IndentedPrintWriter writer = new IndentedPrintWriter(out);

                writer.format("// Generated with %s %s",
                              generatorOptions.generator_program_name,
                              generatorOptions.program_version)
                      .newline();

                formatter.format(writer, program);
            } finally {
                getFileManager().finalize(out);
            }
        }
    }
}
