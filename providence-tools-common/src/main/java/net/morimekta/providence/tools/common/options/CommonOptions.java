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

package net.morimekta.providence.tools.common.options;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentOptions;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Flag;
import net.morimekta.console.args.Option;
import net.morimekta.console.util.STTY;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.morimekta.console.util.Parser.file;
import static net.morimekta.providence.tools.common.options.Utils.collectConfigIncludes;
import static net.morimekta.providence.tools.common.options.Utils.collectIncludes;
import static net.morimekta.providence.tools.common.options.Utils.getVersionString;

/**
 * Options used by the providence converter.
 */
@SuppressWarnings("all")
public class CommonOptions {
    private boolean help;
    private boolean verbose;
    private boolean version;
    private STTY    tty;
    private File    rc = new File(System.getenv("HOME"), ".pvdrc");

    public CommonOptions(STTY tty) {
        this.tty = tty;
    }

    public ArgumentOptions getArgumentOptions() {
        return ArgumentOptions.defaults(tty).withMaxUsageWidth(120);
    }

    public ArgumentParser getArgumentParser(String prog, String description) throws IOException {
        ArgumentParser parser = new ArgumentParser(prog, getVersionString(), description, getArgumentOptions());

        parser.add(new Flag("--help", "h?", "This help listing.", this::setHelp));
        parser.add(new Flag("--verbose", "V", "Show verbose output and error messages.", this::setVerbose));
        parser.add(new Flag("--version", "v", "Show program version.", this::setVersion));
        parser.add(new Option("--rc", null, "FILE", "Providence RC to use", file(this::setRc), "~" + File.separator + ".pvdrc"));

        return parser;
    }

    public boolean showHelp() {
        return help;
    }
    public boolean showVersion() {
        return version;
    }
    public boolean verbose() {
        return verbose;
    }
    public File getRc() {
        return rc;
    }
    private void setHelp(boolean help) {
        this.help = help;
    }
    private void setVersion(boolean version) {
        this.version = version;
    }
    private void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    private void setRc(File file) {
        this.rc = file;
    }

    public Map<String, File> getIncludeMap(List<File> includes) throws IOException {
        Map<String, File> includeMap = new HashMap<>();
        if (includes.isEmpty()) {
            collectConfigIncludes(getRc(), includeMap);
        }
        if (includes.isEmpty()) {
            throw new ArgumentException("No includes, use --include/-I or update ~/.pvdrc");
        }
        if (includeMap.isEmpty()) {
            for (File file : includes) {
                collectIncludes(file, includeMap);
            }
        }
        return includeMap;
    }

}
