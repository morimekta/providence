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

import net.morimekta.providence.compiler.generator.Generator;
import net.morimekta.providence.compiler.generator.GeneratorException;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.Parser;
import net.morimekta.providence.util.PStringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Stein Eldar Johnsen
 * @since 15.09.15
 */
public class Compiler {
    private final CompilerOptions mOpts;

    public Compiler() {
        mOpts = new CompilerOptions();
    }

    public void run(String... args) {
        CmdLineParser cli = new CmdLineParser(mOpts);
        try {
            cli.parseArgument(args);
            if (mOpts.isHelp()) {
                System.out.println("compiler --gen [language] file...");
                System.out.println();
                cli.printUsage(System.out);
                return;
            }

            Parser parser = mOpts.getParser(cli);
            List<File> includes = mOpts.getIncludes(cli);
            List<File> input = mOpts.getInputFiles(cli);

            TypeLoader loader = new TypeLoader(includes, parser);
            Generator generator = mOpts.getGenerator(cli, loader);

            List<CDocument> docs = new LinkedList<>();
            for (File f : input) {
                docs.add(loader.load(f));
            }

            for (CDocument doc : docs) {
                generator.generate(doc);
            }

            return;
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("compiler --gen [language] file...");
            System.err.println();
            cli.printUsage(System.err);
        } catch (ParseException e) {
            if (e.getLine() != null) {
                System.err.format("Error at line %d, pos %d-%d: %s\n" +
                                  "    %s\n" +
                                  "    %s%c\n",
                                  e.getLineNo(), e.getPos(), e.getPos() + e.getLen(),
                                  e.getLocalizedMessage(),
                                  e.getLine(),
                                  PStringUtils.times("~", e.getPos()), '^');
            } else {
                System.err.println("Parser error: " + e.getLocalizedMessage());
            }
        } catch (GeneratorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.print("I/O error: ");
            e.printStackTrace();
        }
        System.exit(1);
    }

    public static void main(String[] args) throws Throwable {
        new Compiler().run(args);
    }

}
