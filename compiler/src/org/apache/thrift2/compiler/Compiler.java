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

package org.apache.thrift2.compiler;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift2.compiler.generator.Generator;
import org.apache.thrift2.compiler.generator.GeneratorException;
import org.apache.thrift2.reflect.TTypeLoader;
import org.apache.thrift2.reflect.contained.TContainedDocument;
import org.apache.thrift2.reflect.parser.TParseException;
import org.apache.thrift2.reflect.parser.TParser;
import org.apache.thrift2.serializer.TSerializeException;
import org.apache.thrift2.util.TStringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 15.09.15
 */
public class Compiler {
    private final CompilerOptions mOpts;

    public Compiler() {
        mOpts = new CompilerOptions();
    }

    public void run(String... args) {
        CmdLineParser cli = new CmdLineParser(mOpts);
        try {
            cli.parseArgument(args);
            if (mOpts.isHelp()) {
                System.out.println("compiler --gen [language] file...");
                System.out.println();
                cli.printUsage(System.out);
                return;
            }

            TParser parser = mOpts.getParser(cli);
            List<File> includes = mOpts.getIncludes(cli);
            List<File> input = mOpts.getInputFiles(cli);

            TTypeLoader loader = new TTypeLoader(includes, parser);
            Generator generator = mOpts.getGenerator(cli, loader);

            List<TContainedDocument> docs = new LinkedList<>();
            for (File f : input) {
                docs.add(loader.load(f));
            }

            for (TContainedDocument doc : docs) {
                generator.generate(doc);
            }

            return;
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("compiler --gen [language] file...");
            System.err.println();
            cli.printUsage(System.err);
        } catch (TParseException e) {
            if (e.getLine() != null) {
                System.err.format("Error at line %d, pos %d-%d: %s\n" +
                                  "    %s\n" +
                                  "    %s%c\n",
                                  e.getLineNo(), e.getPos(), e.getPos() + e.getLen(),
                                  e.getLocalizedMessage(),
                                  e.getLine(),
                                  TStringUtils.times("~", e.getPos()), '^');
            } else {
                System.err.println("Parser error: " + e.getLocalizedMessage());
            }
        } catch (GeneratorException e) {
            e.printStackTrace();
        } catch (TSerializeException e) {
            System.err.print("Deserialization error: ");
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
