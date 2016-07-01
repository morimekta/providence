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

package net.morimekta.providence.rpc;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.providence.PClientHandler;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.rpc.options.Format;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.Strings;

import com.google.api.client.http.HttpResponseException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ConnectException;
import java.util.Properties;

public class RPC {
    private final RPCOptions options;

    protected RPC() {
        options = new RPCOptions();
    }

    @SuppressWarnings("unchecked")
    void run(String... args) {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/build.properties"));

            ArgumentParser cli = options.getArgumentParser("pvdrpc", "" + properties.getProperty("build.version"),
                                                           "Providence RPC Tool");

            cli.parse(args);
            if (options.isHelp()) {
                System.out.println("Providence RPC Tool - v" + properties.getProperty("build.version"));
                System.out.println("Usage: pvdrpc [-i spec] [-o spec] [-I dir] [-S] [-f fmt] [-H hdr] -s srv URL");
                System.out.println();
                System.out.println("Example code to run:");
                System.out.println("$ cat call.json | pvdrpc -I thrift/ -s cal.Calculator http://localhost:8080/service");
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
            }

            cli.validate();

            MessageReader in = options.getInput();
            MessageWriter out = options.getOutput();
            PService service = options.getDefinition();
            PClientHandler handler = options.getHandler();

            PServiceCall call = in.read(service);
            PServiceCall resp = handler.handleCall(call, service);

            out.write(resp);
            return;
        } catch (ConnectException e) {
            System.err.format("Unable to connect to %s: %s\n",
                              options.endpoint,
                              e.getMessage());
        } catch (HttpResponseException e) {
            System.err.println("Received " + e.getStatusCode() + " " + e.getStatusMessage());
            System.err.println(" - from: " + options.endpoint);
        } catch (ArgumentException e) {
            System.err.println("Usage: pvdrpc [-i spec] [-o spec] [-I dir] [-S] [-f fmt] [-H hdr] -s srv URL");
            System.err.println(e.getMessage());
            System.err.println();
            System.err.println("Run $ pvdrpc --help # for available options.");
        } catch (SerializerException e) {
            System.out.flush();
            System.err.println();
            System.err.println("Serialization error: " + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.flush();
            System.err.println();
            if (e.getLine() != null) {
                int lineNo = e.getToken()
                              .getLineNo();
                int linePos = e.getToken()
                               .getLinePos();
                int len = e.getToken()
                           .length();

                System.err.format("Error at line %d, pos %d-%d: %s\n" +
                                  "    %s\n" +
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
        } catch (IllegalArgumentException e) {
            System.out.flush();
            System.err.print(e.getMessage());
        } catch (UncheckedIOException | IOException e) {
            System.out.flush();
            System.err.println();
            System.err.print("I/O error: ");
            e.printStackTrace();
        }
        exit(1);
    }

    protected void exit(int i) {
        System.exit(i);
    }

    public static void main(String[] args) {
        new RPC().run(args);
    }
}
