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

package net.morimekta.providence.converter;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.util.Strings;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author Stein Eldar Johnsen
 * @since 27.09.15
 */
public class Convert {
    private final ConvertOptions options;

    public Convert() {
        options = new ConvertOptions();
    }

    @SuppressWarnings("unchecked")
    public <T extends PMessage<T>> void run(String... args) {
        CmdLineParser cli = new CmdLineParser(options);
        try {
            cli.parseArgument(args);
            if (options.isHelp()) {
                cli.printSingleLineUsage(System.out);
                System.out.println();
                cli.printUsage(System.out);
                System.out.println();
                System.out.println("Available formats are:");
                for (ConvertOptions.Format format : ConvertOptions.Format.values()) {
                    System.out.println(String.format(" - %-18s : %s", format.name(), format.desc));
                }
                return;
            }

            options.getInput(cli)
                   .collect(options.getOutput(cli));

            System.exit(0);
            return;
        } catch (CmdLineException e) {
            System.out.flush();
            System.err.println(e.getMessage());
            cli.printSingleLineUsage(System.err);
            System.err.println();
            cli.printUsage(System.err);
            System.err.println();
        } catch (ParseException e) {
            System.out.flush();
            System.err.println();
            if (e.getLine() != null) {
                System.err.format("Error at line %d, pos %d-%d: %s\n" +
                                  "    %s\n"                          +
                                  "    %s%c\n",
                                  e.getLineNo(),
                                  e.getPos(),
                                  e.getPos() + e.getLen(),
                                  e.getLocalizedMessage(),
                                  e.getLine(),
                                  Strings.times("~", e.getPos()),
                                  '^');
            } else {
                System.err.println("Parser error: " + e.getLocalizedMessage());
            }
        } catch (UncheckedIOException | IOException e) {
            System.out.flush();
            System.err.println();
            System.err.print("I/O error: ");
            e.printStackTrace();
        }
        System.exit(1);
    }

    public static void main(String[] args) throws Throwable {
        new Convert().run(args);
    }
}
