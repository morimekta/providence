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
import net.morimekta.providence.generator.format.js.JSOptions;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.ProgramParser;
import net.morimekta.providence.tools.common.options.Utils;
import net.morimekta.util.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
        options = new CompilerOptions(tty);
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
                    System.out.format("%s : %s%n",
                                      options.help.generator.name(),
                                      options.help.generator.desc);
                    System.out.println();
                    System.out.println("Available options");
                    System.out.println();
                    switch (options.help.generator) {
                        case java:
                            System.out.println(" - android             : Add android parcelable interface to model classes.");
                            System.out.println(" - jackson             : Add jackson 2 annotations to model classes.");
                            System.out.println(" - no_rw_binary        : Skip adding the binary RW methods to generated code. [Default on]");
                            System.out.println(" - hazelcast_portable  : Add hazelcast portable to annotated model classes, and add portable\n" +
                                               "                         factories.");

                            System.out.println(" - no_generated_annotation_version : Remove providence version from the <code>@Generated</code>\n" +
                                               "                         annotation for each generated class. [Default on]");
                            System.out.println(" - public_constructors : Generate public constructors for all structs and exceptions. Have no\n" +
                                               "                         effect on unions.");
                            break;
                        case js:
                            System.out.println(" - es51                : Generate for ECMA Script 5.1 (no maps, promises).");
                            System.out.println(" - ts                  : Generate definition files for typescript.");
                            System.out.println(" - closure             : Generate google closure dependencies (goog.require and goog.provide).");
                            System.out.println(" - node_js             : Generate node.js module wrapper.");
                            System.out.println(" - pvd                 : Provide core providence models for services if needed.");
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
                        System.out.format(" - %-10s : %s%n", lang.name(), lang.desc);
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

            List<CProgram> docs = new ArrayList<>();
            for (File f : input) {
                docs.add(loader.load(f).getProgram());
            }

            for (CProgram doc : docs) {
                Generator generator = options.getGenerator(doc.getProgramFilePath(), loader);
                generator.generate(doc);
            }

            if (options.gen.generator == Language.js &&
                options.gen.options.contains("pvd")) {
                boolean service = false;
                for (CProgram program : docs) {
                    if (program.getServices().size() > 0) {
                        service = true;
                        break;
                    }
                }
                if (service) {
                    JSOptions opts = options.makeJsOptions();
                    InputStream source;
                    File target = new File(options.out, String.join(File.separator, "net", "morimekta", "providence", "service.js"));
                    if (opts.type_script) {
                        // copy ts.
                        source = getClass().getResourceAsStream("/type_script/net/morimekta/providence/service.ts");
                        target = new File(options.out, String.join(File.separator, "net", "morimekta", "providence", "service.ts"));
                    } else if (opts.node_js) {
                        source = getClass().getResourceAsStream("/node_js/net/morimekta/providence/service.js");
                    } else if (opts.closure) {
                        source = getClass().getResourceAsStream("/closure/net/morimekta/providence/service.js");
                    } else {
                        source = getClass().getResourceAsStream("/js/net/morimekta/providence/service.js");
                    }

                    target.getParentFile().mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(target);
                         BufferedOutputStream out = new BufferedOutputStream(fos)) {
                        IOUtils.copy(source, out);
                    }
                }
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
