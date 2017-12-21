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
import net.morimekta.providence.generator.format.js.formatter.TSDefinitionFormatter;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.util.ProgramRegistry;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Generate JS message models for providence.
 *
 * Supports the providence styled JSON (both named and compact) as data
 * format.
 */
public class JSGenerator extends Generator {
    private final GeneratorOptions         generatorOptions;
    private final JSOptions                options;

    public JSGenerator(FileManager manager, GeneratorOptions generatorOptions, JSOptions jsOptions) throws GeneratorException {
        super(manager);

        this.options = jsOptions;
        this.generatorOptions = generatorOptions;
    }

    @Override
    @SuppressWarnings("resource")
    public void generate(ProgramTypeRegistry registry) throws IOException, GeneratorException {
        CProgram program = registry.getProgram();
        if (program.getNamespaceForLanguage("js") == null) {
            // just skip programs with missing js namespace.
            return;
        }

        // Generate .d.ts definition files if typescript is enabled.
        if (options.type_script) {
            TSDefinitionFormatter tsdf = new TSDefinitionFormatter(options, registry);
            String fileName = tsdf.getFileName(program);
            String filePath = tsdf.getFilePath(program);
            OutputStream out = new BufferedOutputStream(getFileManager().create(filePath, fileName));
            try {
                IndentedPrintWriter writer = new IndentedPrintWriter(out);

                writer.format("// Generated with %s %s", generatorOptions.generator_program_name, generatorOptions.program_version)
                      .newline();

                tsdf.format(writer, program);
            } finally {
                getFileManager().finalize(out);
            }
        }

        JSProgramFormatter formatter = new JSProgramFormatter(options, registry);
        String fileName = formatter.getFileName(program);
        String filePath = formatter.getFilePath(program);
        OutputStream out = new BufferedOutputStream(getFileManager().create(filePath, fileName));
        try {
            IndentedPrintWriter writer = new IndentedPrintWriter(out);

            writer.format("// Generated with %s %s", generatorOptions.generator_program_name, generatorOptions.program_version)
                  .newline();

            formatter.format(writer, program);
        } finally {
            getFileManager().finalize(out);
        }
    }

    @Override
    public void generateGlobal(ProgramRegistry registry,
                               Collection<File> inputFiles) throws IOException, GeneratorException {
        boolean service = false;
        for (ProgramTypeRegistry reg : registry.getLoadedRegistries()) {
            CProgram program = reg.getProgram();
            if (program.getServices().size() > 0) {
                service = true;
                break;
            }
        }

        if (service) {
            InputStream source;

            String targetPath = Strings.join(File.separator, "morimekta", "providence");
            if (options.node_js || options.type_script) {
                source = getClass().getResourceAsStream("/node_module/morimekta-providence/service.js");
                targetPath = "morimekta-providence";
            } else if (options.closure) {
                source = getClass().getResourceAsStream("/closure/morimekta/providence/service.js");
            } else {
                source = getClass().getResourceAsStream("/js/morimekta/providence/service.js");
            }

            OutputStream out = getFileManager().create(targetPath, "service.js");

            try {
                IOUtils.copy(source, out);
            } finally {
                getFileManager().finalize(out);
            }

            if (options.type_script) {
                // copy .d.ts.
                out = getFileManager().create(targetPath, "service.d.ts");
                try {
                    IOUtils.copy(getClass().getResourceAsStream("/type_script/morimekta-providence/service.d.ts"), out);
                } finally {
                    getFileManager().finalize(out);
                }
            }
        }

    }
}
