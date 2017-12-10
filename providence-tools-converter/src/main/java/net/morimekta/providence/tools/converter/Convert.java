/*
 * Copyright (c) 2016, Providence Authors
 *
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

package net.morimekta.providence.tools.converter;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.util.STTY;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.reflect.contained.CExceptionDescriptor;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.contained.CStructDescriptor;
import net.morimekta.providence.reflect.contained.CUnionDescriptor;
import net.morimekta.providence.reflect.util.ProgramRegistry;
import net.morimekta.providence.reflect.util.ProgramTypeRegistry;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.tools.common.options.Format;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Convert {
    private final ConvertOptions options;

    public Convert() {
        this(new STTY());
    }

    protected Convert(STTY tty) {
        options = new ConvertOptions(tty);
    }

    @SuppressWarnings("unchecked")
    void run(String... args) {
        try {
            ArgumentParser cli = options.getArgumentParser("pvd", "Providence Converter");
            try {
                cli.parse(args);
                if (options.showHelp()) {
                    System.out.println(cli.getProgramDescription());
                    System.out.println("Usage: " + cli.getSingleLineUsage());
                    System.out.println();
                    System.out.println("Example code to run:");
                    System.out.println("$ cat call.json | pvd -I thrift/ -S cal.Calculator");
                    System.out.println("$ pvd -i binary,file:my.data -o json_protocol -I thrift/ cal.Operation");
                    System.out.println();
                    System.out.println("Note that when handling service calls, only 1 call can be converted.");
                    System.out.println();
                    cli.printUsage(System.out);
                    System.out.println();
                    System.out.println("Available formats are:");
                    for (Format format : Format.values()) {
                        System.out.println(String.format(" - %-20s : %s", format.name(), format.desc));
                    }
                    return;
                }
                if (options.showVersion()) {
                    System.out.println(cli.getProgramDescription());
                    return;
                }

                if (options.listTypes) {
                    ProgramRegistry registry = options.getProgramRegistry();
                    for (ProgramTypeRegistry pr : registry.getLoadedRegistries()) {
                        CProgram program = pr.getProgram();
                        System.out.println(program.getProgramFilePath() + ":");
                        for (PDeclaredDescriptor dd : program.getDeclaredTypes()) {
                            if (dd instanceof CStructDescriptor) {
                                System.out.println("  struct    " + dd.getQualifiedName());
                            } else if (dd instanceof CUnionDescriptor) {
                                System.out.println("  union     " + dd.getQualifiedName());
                            } else if (dd instanceof CExceptionDescriptor) {
                                System.out.println("  exception " + dd.getQualifiedName());
                            }
                        }
                        for (PService s : program.getServices()) {
                            System.out.println("  service   " + s.getQualifiedName());
                        }
                    }
                    return;
                }

                cli.validate();

                if (options.getDefinition() == null || options.listTypes) {
                    MessageReader in = options.getServiceInput();
                    MessageWriter out = options.getServiceOutput();
                    PService service = options.getServiceDefinition();

                    PServiceCall call = in.read(service);
                    out.write(call);
                    out.separator();
                    // TODO: Validate we don't have garbage data after call.
                } else {
                    AtomicInteger num = new AtomicInteger(0);
                    int size = options.getInput()
                                      .peek(m -> num.incrementAndGet())
                                      .collect(options.getOutput());
                    if (num.get() == 0 || size == 0) {
                        throw new IOException("No data");
                    }
                }
                return;
            } catch (ArgumentException e) {
                System.err.println(e.getMessage());
                System.out.println("Usage: " + cli.getSingleLineUsage());
                System.err.println();
                System.err.println("Run $ pvd --help # for available options.");
                if (options.verbose()) {
                    System.err.println();
                    e.printStackTrace();
                }
            } catch (SerializerException e) {
                System.out.flush();
                System.err.println();
                System.err.println(e.asString());
                if (options.verbose()) {
                    System.err.println();
                    e.printStackTrace();
                }
            } catch (UncheckedIOException | IOException e) {
                System.out.flush();
                System.err.println();
                System.err.println("I/O error: " + e.getMessage());
                if (options.verbose()) {
                    System.err.println();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.flush();
            System.err.println();
            System.err.println("Unchecked exception: " + e.getMessage());
            if (options.verbose()) {
                System.err.println();
                e.printStackTrace();
            }
        }
        exit(1);
    }

    protected void exit(int i) {
        System.exit(i);
    }

    public static void main(String[] args) throws Throwable {
        new Convert().run(args);
    }
}
