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

package net.morimekta.providence.compiler;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentOptions;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Option;
import net.morimekta.console.util.TerminalSize;
import net.morimekta.providence.compiler.options.GeneratorSpec;
import net.morimekta.providence.compiler.options.GeneratorSpecParser;
import net.morimekta.providence.compiler.options.HelpOption;
import net.morimekta.providence.compiler.options.HelpSpec;
import net.morimekta.providence.compiler.options.Syntax;
import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.JGenerator;
import net.morimekta.providence.generator.format.java.JOptions;
import net.morimekta.providence.generator.format.java.tiny.TinyGenerator;
import net.morimekta.providence.generator.format.java.tiny.TinyOptions;
import net.morimekta.providence.generator.format.json.JsonGenerator;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.DocumentParser;
import net.morimekta.providence.reflect.parser.MessageDocumentParser;
import net.morimekta.providence.reflect.parser.ThriftDocumentParser;
import net.morimekta.providence.serializer.JsonSerializer;

import java.io.File;
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
    protected File          out      = new File(".");
    protected List<File>    includes = new LinkedList<>();
    protected Syntax        syntax   = Syntax.thrift;
    protected HelpSpec      help     = null;
    protected GeneratorSpec gen      = null;
    protected List<File>    files    = new LinkedList<>();

    public ArgumentParser getArgumentParser(String prog, String version, String description) {
        ArgumentOptions opts = ArgumentOptions.defaults().withUsageWidth(
                Math.min(120, TerminalSize.get().cols));
        ArgumentParser parser = new ArgumentParser(prog, version, description, opts);
        parser.add(new Option("--gen", "g", "generator", "Generate files for this language spec.",
                              new GeneratorSpecParser().then(this::setGenerator)));
        parser.add(new HelpOption("--help", "h?", "Show this help or about language.", this::setHelp));
        parser.add(new Option("--include", "I", "dir", "Allow includes of files in directory", dir(this::addInclude), null, true, false, false));
        parser.add(new Option("--out", "o", "dir", "Output directory", dir(this::setOut), "${PWD}"));
        parser.add(new Option("--syntax", null, "syntax", "Input file syntax", oneOf(Syntax.class, this::setSyntax), "thrift", false, false, true));
        parser.add(new Argument("file", "Files to compile.", null, file(this::addFile), null, true, true, false));
        return parser;
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

    public CompilerOptions() {
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

    public DocumentParser getParser() throws ArgumentException {
        switch (syntax) {
            case thrift:
                return new ThriftDocumentParser();
            case json:
                return new MessageDocumentParser(new JsonSerializer());
            default:
                throw new ArgumentException("Unknown SLI syntax %s.", syntax.name());
        }
    }

    public Generator getGenerator(TypeLoader loader) throws ArgumentException, GeneratorException {
        switch (gen.generator) {
            case json:
                return new JsonGenerator(getFileManager(), loader);
            case java: {
                JOptions options = new JOptions();
                for (String opt : gen.options) {
                    switch (opt) {
                        case "android":
                            options.android = true;
                            break;
                        default:
                            throw new ArgumentException("No such option for java generator: " + opt);
                    }
                }
                return new JGenerator(getFileManager(), loader.getRegistry(), options);
            }
            case tiny_java: {
                TinyOptions options = new TinyOptions();
                for (String opt : gen.options) {
                    switch (opt) {
                        case "jackson":
                            options.jackson = true;
                            break;
                        default:
                            throw new ArgumentException("No such option for tiny_java generator: " + opt);
                    }
                }
                return new TinyGenerator(getFileManager(), loader.getRegistry(), options);
            }
            default:
                throw new ArgumentException("Unknown language %s.", gen.generator.name());
        }
    }
}
