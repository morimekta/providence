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

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.Parser;
import net.morimekta.util.Strings;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

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
        ParserProperties props = ParserProperties
                .defaults()
                .withUsageWidth(120);
        CmdLineParser cli = new CmdLineParser(mOpts, props);
        try {
            cli.parseArgument(args);
            if (mOpts.isHelp()) {
                System.out.println("pvdc [-I dir] [--out dir] --gen spec[:opt[,opt]*] file...");
                System.out.println();
                System.out.println("Example code to run:");
                System.out.println("$ pvdc -I thrift/ --out target/ --gen java:android thrift/the-one.thrift");
                System.out.println();
                cli.printUsage(System.out);
                System.out.println();
                System.out.println("Available generators:");
                for (GeneratorSpec generator : GeneratorSpec.values()) {
                    System.out.println(String.format(" - %-10s : %s", generator.name(), generator.desc));
                }
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
            e.printStackTrace();
            if (e.getToken() != null) {
                int lineNo = e.getToken().getLineNo();
                int linePos = e.getToken().getLinePos();
                int len = e.getToken().length();

                System.err.format(
                        "Error at line %d, pos %d-%d: %s\n" +
                        "    %s\n"                          +
                        "    %s%c\n",
                        lineNo,
                        linePos,
                        linePos + len,
                        e.getLocalizedMessage(),
                        e.getLine(),
                        Strings.times("~", linePos),
                        '^');
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
