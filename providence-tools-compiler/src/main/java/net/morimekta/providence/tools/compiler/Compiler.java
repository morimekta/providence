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
import net.morimekta.console.util.STTY;
import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorFactory;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.ProgramParser;
import net.morimekta.providence.tools.common.options.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Stein Eldar Johnsen
 * @since 15.09.15
 */
public class Compiler {
    private final CompilerOptions options;

    public Compiler() {
        this(new STTY());
    }

    protected Compiler(STTY tty) {
        this(new CompilerOptions(tty));
    }

    protected Compiler(CompilerOptions options) {
        this.options = options;
    }

    public void run(String... args) {
        try {
            ArgumentParser cli = options.getArgumentParser("pvdc", "Providence compiler");

            cli.parse(args);
            if (options.isHelp()) {
                System.out.println("Providence compiler - " + Utils.getVersionString());
                System.out.println("Usage: pvdc [-I dir] [-o dir] -g generator[:opt[,opt]*] file...");
                System.out.println();
                if (options.help.factory != null) {
                    System.out.format("%s : %s%n",
                                      options.help.factory.generatorName(),
                                      options.help.factory.generatorDescription());
                    System.out.println();
                    System.out.println("Available options");
                    System.out.println();
                    options.help.factory.printGeneratorOptionsHelp(System.out);
                } else {
                    System.out.println("Example code to run:");
                    System.out.println("$ pvdc -I thrift/ --out target/ --gen java:android thrift/the-one.thrift");
                    System.out.println();
                    cli.printUsage(System.out);
                    System.out.println();
                    System.out.println("Available generators:");

                    Map<String,GeneratorFactory> factories = options.getFactories();

                    for (GeneratorFactory lang : factories.values()) {
                        System.out.format(" - %-10s : %s%n", lang.generatorName(), lang.generatorDescription());
                    }
                }
                return;
            } else if (options.version) {
                System.out.println("Providence compiler - " + Utils.getVersionString());
                return;
            }

            cli.validate();

            ProgramParser parser = options.getParser();
            List<File> includes = options.getIncludes();
            List<File> input = options.getInputFiles();

            TypeLoader loader = new TypeLoader(includes, parser);
            Generator generator = options.getGenerator(loader);

            for (File f : input) {
                generator.generate(loader.load(f));
            }
            generator.generateGlobal(loader.getProgramRegistry(), input);
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
            System.err.println("Generator error: " + e.getMessage());
            if (options.verbose) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
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
