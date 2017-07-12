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

package net.morimekta.providence.tools.rpc;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.util.STTY;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallHandler;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.tools.common.options.Format;

import com.google.api.client.http.HttpResponseException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ConnectException;

import static net.morimekta.providence.tools.common.options.Utils.getVersionString;

public class RPC {
    private final RPCOptions options;

    protected RPC() {
        this(new STTY());
    }

    protected RPC(STTY tty) {
        options = new RPCOptions(tty);
    }

    @SuppressWarnings("unchecked")
    void run(String... args) {
        try {
            ArgumentParser cli = options.getArgumentParser("pvdrpc", "Providence RPC Tool");
            try {

                cli.parse(args);
                if (options.showHelp()) {
                    System.out.println("Providence RPC Tool - " + getVersionString());
                    System.out.println("Usage: " + cli.getSingleLineUsage());
                    System.out.println();
                    System.out.println("Example code to run:");
                    System.out.println(
                            "$ cat call.json | pvdrpc -I thrift/ -s cal.Calculator http://localhost:8080/service");
                    System.out.println(
                            "$ pvdrpc -i binary,file:my.data -f json_protocol -I thrift/ -s cal.Calculator http://localhost:8080/service");
                    System.out.println();
                    cli.printUsage(System.out);
                    System.out.println();
                    System.out.println("Available formats are:");
                    for (Format format : Format.values()) {
                        System.out.println(String.format(" - %-20s : %s", format.name(), format.desc));
                    }
                    return;
                } else if (options.showVersion()) {
                    System.out.println("Providence RPC Tool - " + getVersionString());
                    return;
                }

                cli.validate();

                MessageReader in = options.getInput();
                MessageWriter out = options.getOutput();
                PService service = options.getDefinition();
                PServiceCallHandler handler = options.getHandler();

                PServiceCall call = in.read(service);
                PServiceCall resp = handler.handleCall(call, service);

                out.write(resp);
                out.separator();
                return;
            } catch (ConnectException e) {
                System.out.flush();
                System.err.format("Unable to connect to %s: %s%n", options.endpoint, e.getMessage());
                if (options.verbose()) {
                    System.err.println();
                    e.printStackTrace();
                }
            } catch (HttpResponseException e) {
                System.out.flush();
                System.err.println("Received " + e.getStatusCode() + " " + e.getStatusMessage());
                System.err.println(" - from: " + options.endpoint);
                if (options.verbose()) {
                    System.err.println();
                    e.printStackTrace();
                }
            } catch (ArgumentException e) {
                System.out.flush();
                System.err.println(e.getMessage());
                System.err.println("Usage: " + cli.getSingleLineUsage());
                System.err.println();
                System.err.println("Run $ pvdrpc --help # for available options.");
            } catch (SerializerException e) {
                System.out.flush();
                System.err.println("Serializer error: " + e.asString());
                if (options.verbose()) {
                    System.err.println();
                    e.printStackTrace();
                }
            } catch (UncheckedIOException | IOException e) {
                System.out.flush();
                System.err.println("I/O error: " + e.getMessage());
                if (options.verbose()) {
                    System.out.flush();
                    e.printStackTrace();
                }
            } catch (IllegalArgumentException e) {
                System.out.flush();
                System.err.println("Internal Error: " + e.getMessage());
                if (options.verbose()) {
                    System.err.println();
                    e.printStackTrace();
                }
            }
        } catch (RuntimeException|IOException e) {
            System.out.flush();
            System.err.println("Unchecked exception: " + e.getMessage());
            if (options.verbose()) {
                System.out.flush();
                e.printStackTrace();
            }
        }
        System.err.flush();
        exit(1);
    }

    protected void exit(int i) {
        System.exit(i);
    }

    public static void main(String[] args) {
        new RPC().run(args);
    }
}
