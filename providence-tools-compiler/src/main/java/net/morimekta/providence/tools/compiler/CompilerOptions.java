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
import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.format.java.JavaGenerator;
import net.morimekta.providence.generator.format.java.JavaOptions;
import net.morimekta.providence.generator.format.json.JsonGenerator;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.MessageProgramParser;
import net.morimekta.providence.reflect.parser.ProgramParser;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.tools.common.options.Utils;
import net.morimekta.providence.tools.compiler.options.GeneratorSpec;
import net.morimekta.providence.tools.compiler.options.GeneratorSpecParser;
import net.morimekta.providence.tools.compiler.options.HelpOption;
import net.morimekta.providence.tools.compiler.options.HelpSpec;
import net.morimekta.providence.tools.compiler.options.Syntax;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static net.morimekta.console.util.Parser.dir;
import static net.morimekta.console.util.Parser.file;
import static net.morimekta.console.util.Parser.oneOf;

/**
 * @author Stein Eldar Johnsen
 * @since 15.09.15
 */
@SuppressWarnings("all")
public class CompilerOptions {
    private final STTY tty;

    protected File out = new File(".");
    protected List<File>    includes = new LinkedList<>();
    protected Syntax        syntax   = Syntax.thrift;
    protected HelpSpec      help     = null;
    protected GeneratorSpec gen      = null;
    protected List<File>    files    = new LinkedList<>();
    protected boolean       version;
    protected boolean       verbose;

    public ArgumentParser getArgumentParser(String prog, String description) throws IOException {
        ArgumentOptions opts = ArgumentOptions.defaults(tty).withMaxUsageWidth(120);
        ArgumentParser parser = new ArgumentParser(prog, Utils.getVersionString(), description, opts);

        parser.add(new Option("--gen", "g", "generator", "Generate files for this language spec.",
                              new GeneratorSpecParser().andApply(this::setGenerator)));
        parser.add(new HelpOption("--help", "h?", "Show this help or about language.", this::setHelp));
        parser.add(new Flag("--verbose", "V", "Show verbose output and error messages.", this::setVerbose));
        parser.add(new Flag("--version", "v", "Show program version.", this::setVersion));
        parser.add(new Option("--include", "I", "dir", "Allow includes of files in directory", dir(this::addInclude), null, true, false, false));
        parser.add(new Option("--out", "o", "dir", "Output directory", dir(this::setOut), "${PWD}"));
        parser.add(new Option("--syntax", null, "syntax", "Input file syntax", oneOf(Syntax.class, this::setSyntax), "thrift", false, false, true));
        parser.add(new Argument("file", "Files to compile.", file(this::addFile), null, null, true, true, false));

        return parser;
    }

    private void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    private void setVersion(boolean version) {
        this.version = version;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public void addInclude(File includes) {
        this.includes.add(includes);
    }

    public void setSyntax(Syntax syntax) {
        this.syntax = syntax;
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
        this.tty = tty;
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
        switch (syntax) {
            case thrift:
                return new ThriftProgramParser();
            case json:
                return new MessageProgramParser(new JsonSerializer());
            default:
                throw new ArgumentException("Unknown SLI syntax %s.", syntax.name());
        }
    }

    public Generator getGenerator(TypeLoader loader) throws ArgumentException, GeneratorException, IOException {
        GeneratorOptions generatorOptions = new GeneratorOptions();
        generatorOptions.generator_program_name = "pvdc";
        generatorOptions.program_version = Utils.getVersionString();
        switch (gen.generator) {
            case json:
                return new JsonGenerator(getFileManager(), loader);
            case java: {
                JavaOptions options = new JavaOptions();
                for (String opt : gen.options) {
                    switch (opt) {
                        case "android":
                            options.android = true;
                            break;
                        case "jackson":
                            options.jackson = true;
                            break;
                        case "no_rw_binary":
                            options.rw_binary = false;
                            break;
                        case "hazelcast_portable":
                            options.hazelcast_portable = true;
                            break;
                        case "no_generated_annotation_version":
                            options.generated_annotation_version = false;
                            break;
                        case "public_constructors":
                            options.public_constructors = true;
                            break;
                        default:
                            throw new ArgumentException("No such option for java generator: " + opt);
                    }
                }
                return new JavaGenerator(getFileManager(), loader.getRegistry(), generatorOptions, options);
            }
            default:
                throw new ArgumentException("Unknown language %s.", gen.generator.name());
        }
    }
}
