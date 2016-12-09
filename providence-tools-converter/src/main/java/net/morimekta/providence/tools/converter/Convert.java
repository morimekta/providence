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
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.tools.common.options.Format;
import net.morimekta.util.Strings;

import java.io.IOException;
import java.io.UncheckedIOException;

import static net.morimekta.providence.tools.common.options.Utils.getVersionString;

/**
 * Converter main class.
 *
 * tail -f data.bin | pvd -i binary -o pretty
 * tail -f data.bin | pvd -i binary -o json,file:out.json
 * pvd -i binary_protocol,file:data.bin -o pretty_json
 */
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

            cli.parse(args);
            if (options.help) {
                System.out.println("Providence Converter - " + getVersionString());
                System.out.println("Usage: pvd [-i spec] [-o spec] [-I dir] [-S] type");
                System.out.println();
                System.out.println("Example code to run:");
                System.out.println("$ cat call.json | pvd -I thrift/ -s cal.Calculator");
                System.out.println("$ pvd -i binary,file:my.data -f json_protocol -I thrift/ -s cal.Calculator");
                System.out.println();
                cli.printUsage(System.out);
                System.out.println();
                System.out.println("Available formats are:");
                for (Format format : Format.values()) {
                    System.out.println(String.format(" - %-20s : %s", format.name(), format.desc));
                }
                return;
            } else if (options.version) {
                System.out.println("Providence Converter - " + getVersionString());
                return;
            }

            cli.validate();

            options.getInput().collect(options.getOutput());
            return;
        } catch (ArgumentException e) {
            System.err.println("Usage: pvd [-i spec] [-o spec] [-I dir] [-S] type");
            if (e.getLocalizedMessage()
                 .length() > 0) {
                System.err.println(e.getLocalizedMessage());
            } else {
                e.printStackTrace();
            }
            System.err.println();
            System.err.println("Run $ pvd --help # for available options.");
            if (options.verbose) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            System.out.flush();
            System.err.println();
            if (e.getLine() != null) {
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
            if (options.verbose) {
                e.printStackTrace();
            }
        } catch (UncheckedIOException | IOException e) {
            System.out.flush();
            System.err.println();
            System.err.println("I/O error: " + e.getMessage());
            if (options.verbose) {
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
