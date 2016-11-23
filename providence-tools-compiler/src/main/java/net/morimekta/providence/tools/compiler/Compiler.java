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

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.Language;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.reflect.parser.DocumentParser;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.tools.common.options.Utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
        try {
            ArgumentParser cli = options.getArgumentParser("pvdc", "Providence compiler");

            cli.parse(args);
            if (options.isHelp()) {
                System.out.println("Providence compiler - " + Utils.getVersionString());
                System.out.println("Usage: pvdc [-I dir] [-o dir] -g generator[:opt[,opt]*] file...");
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
                            System.out.println("No options available for " + options.help.generator + ".");
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
            } else if (options.version) {
                System.out.println("Providence compiler - " + Utils.getVersionString());
                return;
            }

            cli.validate();

            DocumentParser parser = options.getParser();
            List<File> includes = options.getIncludes();
            List<File> input = options.getInputFiles();

            TypeLoader loader = new TypeLoader(includes, parser);
            Generator generator = options.getGenerator(loader);

            List<CDocument> docs = new LinkedList<>();
            for (File f : input) {
                docs.add(loader.load(f));
            }

            for (CDocument doc : docs) {
                generator.generate(doc);
            }

            return;
        } catch (ArgumentException e) {
            System.err.println("Usage: pvdc [-I dir] [-o dir] -g generator[:opt[,opt]*] file...");
            System.err.println(e.getLocalizedMessage());
            System.err.println();
            System.err.println("Run $ pvdc --help # for available options.");
            if (options.verbose) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            System.err.println(e.asString());
            if (options.verbose) {
                e.printStackTrace();
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            if (options.verbose) {
                e.printStackTrace();
            }
        } catch (GeneratorException e) {
            System.err.print("Generator error: " + e.getMessage());
            if (options.verbose) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.print("I/O error: " + e.getMessage());
            if (options.verbose) {
                e.printStackTrace();
            }
        } catch (Exception e) {
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
