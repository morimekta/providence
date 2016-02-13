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
import net.morimekta.providence.compiler.format.java2.JGenerator;
import net.morimekta.providence.compiler.format.java2.JOptions;
import net.morimekta.providence.compiler.format.json.JsonGenerator;
import net.morimekta.providence.compiler.format.thrift.ThriftGenerator;
import net.morimekta.providence.compiler.generator.Generator;
import net.morimekta.providence.compiler.util.FakeFileManager;
import net.morimekta.providence.compiler.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.MessageParser;
import net.morimekta.providence.reflect.parser.Parser;
import net.morimekta.providence.reflect.parser.ThriftParser;
import net.morimekta.providence.serializer.PJsonSerializer;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Stein Eldar Johnsen
 * @since 15.09.15
 */
public class CompilerOptions {
    protected enum Syntax {
        thrift,
        json
    }

    protected enum Language {
        thrift,
        json,
        java2
    }

    @Option(name = "--out",
            metaVar = "dir",
            usage = "Output directory. If none, print to std::out.")
    protected String mOutputFolder;

    @Option(name = "--include",
            aliases = "-I",
            metaVar = "dir",
            usage = "Include files in directory for imports.")
    protected List<String> mIncludes = new LinkedList<>();

    @Option(name = "--syntax",
            metaVar = "syntax",
            usage = "Input file syntax.")
    protected Syntax mSyntax = Syntax.thrift;

    @Option(name = "--gen",
            metaVar = "lang",
            usage = "Generate files for this language spec.")
    protected Language mGenerate = Language.thrift;

    @Option(name = "--options",
            metaVar = "opt",
            usage = "Colon ':' separated list of language specific options.")
    protected String mOptions = null;

    @Option(name = "--help",
            aliases = {"-?", "-h"},
            help = true,
            usage = "This help message.")
    protected boolean mHelp = false;

    @Argument(metaVar = "file",
              required = true,
              usage = "Input files to compile.")
    protected List<String> mInputFiles = new LinkedList<>();

    public CompilerOptions() {
    }

    public boolean isHelp() {
        return mHelp;
    }

    public List<File> getIncludes(CmdLineParser cli) throws CmdLineException {
        List<File> includes = new LinkedList<>();
        for (String include : mIncludes) {
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
        if (mInputFiles.isEmpty()) {
            throw new CmdLineException(cli, new FormatString("No input file(s)."));
        }

        for (String f : mInputFiles) {
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
        if (mOutputFolder != null) {
            File file = new File(mOutputFolder);
            if (!file.exists()) {
                throw new CmdLineException(cli, new FormatString("Output dir %s does not exist."), mOutputFolder);
            }
            if (!file.isDirectory()) {
                throw new CmdLineException(cli, new FormatString("Output fir %s is not a directory."), mOutputFolder);
            }
            return new FileManager(file);
        }
        return new FakeFileManager(new File("."));
    }

    public Parser getParser(CmdLineParser cli) throws CmdLineException {
        switch (mSyntax) {
            case thrift:
                return new ThriftParser();
            case json:
                return new MessageParser(new PJsonSerializer());
            default:
                throw new CmdLineException(cli, new FormatString("Unknown SLI syntax %s."), mSyntax.name());
        }
    }

    public Generator getGenerator(CmdLineParser cli, TypeLoader loader) throws CmdLineException {
        switch (mGenerate) {
            case thrift:
                return new ThriftGenerator(getFileManager(cli));
            case json:
                return new JsonGenerator(getFileManager(cli), loader);
            case java2:
                JOptions options = new JOptions();
                if (mOptions != null) {
                    CmdLineParser optParser = new CmdLineParser(options);
                    optParser.parseArgument(mOptions.split(":"));
                }
                return new JGenerator(getFileManager(cli), loader.getRegistry(), options);
            default:
                throw new CmdLineException(cli, new FormatString("Unknown language %s."), mGenerate.name());
        }
    }
}
