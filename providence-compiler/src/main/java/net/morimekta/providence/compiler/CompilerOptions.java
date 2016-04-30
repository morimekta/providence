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

import net.morimekta.console.FormatString;
import net.morimekta.providence.compiler.options.GeneratorOptionHandler;
import net.morimekta.providence.compiler.options.GeneratorOptions;
import net.morimekta.providence.compiler.options.HelpOptionHandler;
import net.morimekta.providence.compiler.options.HelpOptions;
import net.morimekta.providence.compiler.options.Syntax;
import net.morimekta.providence.compiler.options.SyntaxOptionHandler;
import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.format.java.JGenerator;
import net.morimekta.providence.generator.format.java.JOptions;
import net.morimekta.providence.generator.format.json.JsonGenerator;
import net.morimekta.providence.generator.format.thrift.ThriftGenerator;
import net.morimekta.providence.generator.util.FakeFileManager;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.MessageParser;
import net.morimekta.providence.reflect.parser.Parser;
import net.morimekta.providence.reflect.parser.ThriftParser;
import net.morimekta.providence.serializer.JsonSerializer;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static net.morimekta.console.FormatString.except;

/**
 * @author Stein Eldar Johnsen
 * @since 15.09.15
 */
@SuppressWarnings("all")
public class CompilerOptions {
    @Option(name = "--out",
            aliases = {"-o"},
            metaVar = "dir",
            usage = "Output directory.")
    protected String out = ".";

    @Option(name = "--include",
            aliases = "-I",
            metaVar = "dir",
            usage = "Allow includes of files in directory.")
    protected List<String> includes = new LinkedList<>();

    @Option(name = "--syntax",
            hidden = true,
            metaVar = "syntax",
            handler = SyntaxOptionHandler.class,
            usage = "Input file syntax.")
    protected Syntax syntax = Syntax.thrift;

    @Option(name = "--help",
            aliases = {"-h", "-?"},
            help = true,
            handler = HelpOptionHandler.class,
            usage = "Show this help or about language.")
    protected HelpOptions help = null;

    @Option(name = "--gen",
            aliases = {"-g"},
            metaVar = "generator",
            required = true,
            handler = GeneratorOptionHandler.class,
            usage = "Generate files for this language spec.")
    protected GeneratorOptions gen;

    @Argument(metaVar = "file",
              required = true,
              multiValued = true,
              usage = "Files to compile.")
    protected List<String> files = new LinkedList<>();

    public CompilerOptions() {
    }

    public boolean isHelp() {
        return help != null;
    }

    public List<File> getIncludes(CmdLineParser cli) throws CmdLineException {
        List<File> includes = new LinkedList<>();
        for (String include : this.includes) {
            File file = new File(include);
            if (!file.exists()) {
                throw new CmdLineException(cli, new FormatString("Included dir %s does not exist."), include);
            }
            if (!file.isDirectory()) {
                throw new CmdLineException(cli, new FormatString("Included dir %s is not a directory."), include);
            }
            includes.add(file);
        }
        return includes;
    }

    public List<File> getInputFiles(CmdLineParser cli) throws CmdLineException {
        List<File> files = new LinkedList<>();
        if (this.files.isEmpty()) {
            throw new CmdLineException(cli, new FormatString("No input file(s)."));
        }

        for (String f : this.files) {
            File file = new File(f);
            if (!file.exists()) {
                throw new CmdLineException(cli, new FormatString("No such file %s."), f);
            }
            if (file.isDirectory()) {
                throw new CmdLineException(cli, new FormatString("%s is a directory."), f);
            }
            files.add(file);
        }
        return files;
    }

    public FileManager getFileManager(CmdLineParser cli) throws CmdLineException {
        if (out != null) {
            File file = new File(out);
            if (!file.exists()) {
                throw new CmdLineException(cli, new FormatString("Output dir %s does not exist."), out);
            }
            if (!file.isDirectory()) {
                throw new CmdLineException(cli, new FormatString("Output fir %s is not a directory."), out);
            }
            return new FileManager(file);
        }
        return new FakeFileManager(new File("."));
    }

    public Parser getParser(CmdLineParser cli) throws CmdLineException {
        switch (syntax) {
            case thrift:
                return new ThriftParser();
            case json:
                return new MessageParser(new JsonSerializer());
            default:
                throw new CmdLineException(cli, new FormatString("Unknown SLI syntax %s."), syntax.name());
        }
    }

    public Generator getGenerator(CmdLineParser cli, TypeLoader loader) throws CmdLineException {
        switch (gen.generator) {
            case thrift:
                return new ThriftGenerator(getFileManager(cli));
            case json:
                return new JsonGenerator(getFileManager(cli), loader);
            case java:
                JOptions options = new JOptions();
                for (String opt : this.gen.options) {
                    switch (opt) {
                        case "android":
                            options.android = true;
                            break;
                        case "jackson":
                            options.jackson = true;
                            break;
                        default:
                            throw except(cli, "No such option for java generator: " + opt);
                    }
                }
                return new JGenerator(getFileManager(cli), loader.getRegistry(), options);
            default:
                throw except(cli, "Unknown language %s.", gen.generator.name());
        }
    }
}
