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

package net.morimekta.providence.tools.compiler;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentOptions;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Flag;
import net.morimekta.console.args.Option;
import net.morimekta.console.util.STTY;
import net.morimekta.providence.config.ProvidenceConfigException;
import net.morimekta.providence.config.UncheckedProvidenceConfigException;
import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorFactory;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.format.json.JsonGeneratorFactory;
import net.morimekta.providence.generator.util.FactoryLoader;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.ProgramParser;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.providence.tools.common.ProvidenceTools;
import net.morimekta.providence.tools.common.options.CommonOptions;
import net.morimekta.providence.tools.common.options.Utils;
import net.morimekta.providence.tools.compiler.options.GeneratorSpec;
import net.morimekta.providence.tools.compiler.options.GeneratorSpecParser;
import net.morimekta.providence.tools.compiler.options.HelpOption;
import net.morimekta.providence.tools.compiler.options.HelpSpec;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static net.morimekta.console.util.Parser.dir;
import static net.morimekta.console.util.Parser.file;
import static net.morimekta.console.util.Parser.outputDir;

/**
 * @author Stein Eldar Johnsen
 * @since 15.09.15
 */
@SuppressWarnings("all")
public class CompilerOptions extends CommonOptions {
    protected File          out              = new File(".");
    protected List<File>    includes         = new ArrayList<>();
    protected HelpSpec      help             = null;
    protected GeneratorSpec gen              = null;
    protected List<File>    files            = new ArrayList<>();
    protected boolean       version          = false;
    protected boolean       verbose          = false;
    protected boolean       requireEnumValue = false;
    protected boolean       requireFieldId   = false;

    public ArgumentParser getArgumentParser(String prog, String description) throws IOException {
        ArgumentOptions opts = ArgumentOptions.defaults(tty).withMaxUsageWidth(120);
        ArgumentParser parser = new ArgumentParser(prog, Utils.getVersionString(), description, opts);

        parser.add(new Option("--gen", "g", "generator", "Generate files for this language spec.",
                              new GeneratorSpecParser(this::getFactories).andApply(this::setGenerator)));
        parser.add(new HelpOption("--help", "h?", "Show this help or about language.", this::getFactories, this::setHelp));
        parser.add(new Flag("--verbose", "V", "Show verbose output and error messages.", this::setVerbose));
        parser.add(new Flag("--version", "v", "Show program version.", this::setVersion));
        parser.add(new Option("--rc", null, "FILE", "Providence RC to use", file(this::setRc), "~" + File.separator + ".pvdrc"));
        parser.add(new Option("--include", "I", "dir", "Allow includes of files in directory", dir(this::addInclude), null, true, false, false));
        parser.add(new Option("--out", "o", "dir", "Output directory", outputDir(this::setOut), "${PWD}"));
        parser.add(new Flag("--require-field-id", null, "Require all fields to have a defined ID", this::setRequireFieldId));
        parser.add(new Flag("--require-enum-value", null, "Require all enum values to have a defined ID", this::setRequireEnumValue));
        parser.add(new Argument("file", "Files to compile.", file(this::addFile), null, null, true, true, false));

        return parser;
    }

    public File currentJarDirectory() {
        try {
            ProvidenceTools config = getConfig();
            List<File> paths = new ArrayList<>();
            URL url = getClass().getProtectionDomain().getCodeSource().getLocation();

            if ("file".equals(url.getProtocol())) {
                String path = url.getPath();
                if (path.endsWith(".jar")) {
                    return new File(path).getParentFile();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<GeneratorFactory> getFactories() {
        try {
            List<GeneratorFactory> factories = new ArrayList<>();
            factories.add(new JsonGeneratorFactory());

            File currentDir = currentJarDirectory();
            if (currentDir != null) {
                File generators = new File(currentDir, "generator");
                if (generators.isDirectory()) {
                    FactoryLoader loader = new FactoryLoader(generators);
                    factories.addAll(loader.getFactories());
                }
            }

            {
                ProvidenceTools config = getConfig();
                if (config.hasGeneratorPaths()) {
                    for (String path : config.getGeneratorPaths()) {
                        File file = new File(path);
                        FactoryLoader loader = new FactoryLoader(file);
                        factories.addAll(loader.getFactories());
                    }
                }
            }

            Collections.sort(factories, Comparator.comparing(GeneratorFactory::generatorName));

            return factories;
        } catch (ProvidenceConfigException e) {
            throw new UncheckedProvidenceConfigException(e);
        }
    }

    private void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    private void setVersion(boolean version) {
        this.version = version;
    }
    private void setRequireEnumValue(boolean requireEnumValue) {
        this.requireEnumValue = requireEnumValue;
    }
    private void setRequireFieldId(boolean requireFieldId) {
        this.requireFieldId = requireFieldId;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public void addInclude(File includes) {
        this.includes.add(includes);
    }

    public void setHelp(HelpSpec help) {
        this.help = help;
    }

    public void setGenerator(GeneratorSpec gen) {
        this.gen = gen;
    }

    public void addFile(File files) {
        this.files.add(files);
    }

    public CompilerOptions(STTY tty) {
        super(tty);
    }

    public boolean isHelp() {
        return help != null;
    }

    public List<File> getIncludes() throws ArgumentException {
        return this.includes;
    }

    public List<File> getInputFiles() throws ArgumentException {
        return files;
    }

    public FileManager getFileManager() throws ArgumentException {
        if (!out.exists()) {
            if (!out.mkdirs()) {
                throw new ArgumentException("Unable to create directory %s", out.toString());
            }
        }
        return new FileManager(out);
    }

    public ProgramParser getParser() throws ArgumentException {
        return new ThriftProgramParser(requireFieldId, requireEnumValue);
    }

    public Generator getGenerator(TypeLoader loader) throws ArgumentException, GeneratorException, IOException {
        GeneratorOptions generatorOptions = new GeneratorOptions();
        generatorOptions.generator_program_name = "pvdc";
        generatorOptions.program_version = Utils.getVersionString();

        try {
            return gen.factory.createGenerator(getFileManager(), generatorOptions, gen.options);
        } catch (GeneratorException e) {
            throw new ArgumentException(e, e.getMessage());
        }
    }
}
