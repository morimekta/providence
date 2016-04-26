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

import net.morimekta.providence.compiler.options.Language;
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
import java.util.Properties;

/**
 * @author Stein Eldar Johnsen
 * @since 15.09.15
 */
public class Compiler {
    private final CompilerOptions options;

    public Compiler() {
        options = new CompilerOptions();
    }

    public void run(String... args) {
        ParserProperties props = ParserProperties
                .defaults()
                .withUsageWidth(120);
        CmdLineParser cli = new CmdLineParser(options, props);
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/build.properties"));

            cli.parseArgument(args);
            if (options.isHelp()) {
                System.out.println("Providence compiler - v" + properties.getProperty("build.version"));
                System.out.println("Usage: pvdc [-I dir] [-o dir] generator[:opt[,opt]*] file...");
                System.out.println();
                if (options.help.generator != null) {
                    System.out.format("%s : %s\n",
                                      options.help.generator.name(),
                                      options.help.generator.desc);
                    System.out.println("Available options");
                    System.out.println();
                    switch (options.help.generator) {
                        case java:
                            System.out.println(" - android : Add android parcelable interface to model classes.");
                            System.out.println(" - jackson : Add jackson 2 annotations to model classes.");
                            break;
                        default:
                            System.out.println("None.");
                            break;
                    }
                } else {
                    System.out.println("Example code to run:");
                    System.out.println("$ pvdc -I thrift/ --out target/ --gen java:android thrift/the-one.thrift");
                    System.out.println();
                    cli.printUsage(System.out);
                    System.out.println();
                    System.out.println("Available generators:");
                    for (Language lang : Language.values()) {
                        System.out.format(" - %-10s : %s\n", lang.name(), lang.desc);
                    }
                }
                return;
            }

            Parser parser = options.getParser(cli);
            List<File> includes = options.getIncludes(cli);
            List<File> input = options.getInputFiles(cli);

            TypeLoader loader = new TypeLoader(includes, parser);
            Generator generator = options.getGenerator(cli, loader);

            List<CDocument> docs = new LinkedList<>();
            for (File f : input) {
                docs.add(loader.load(f));
            }

            for (CDocument doc : docs) {
                generator.generate(doc);
            }

            return;
        } catch (CmdLineException e) {
            System.err.println("Usage: pvdc [-I dir] [-o dir] generator[:opt[,opt]*] file...");
            if (e.getLocalizedMessage()
                 .length() > 0) {
                System.err.println(e.getLocalizedMessage());
            } else {
                e.printStackTrace();
            }
            System.err.println();
            System.err.println("Run $ pvdc --help # for available options.");
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
        exit(1);
    }

    protected void exit(int i) {
        System.exit(i);
    }

    public static void main(String[] args) throws Throwable {
        new Compiler().run(args);
    }

}
