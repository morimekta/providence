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

import net.morimekta.console.args.ArgumentOptions;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Flag;
import net.morimekta.console.util.STTY;

import java.io.IOException;

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
    private void setHelp(boolean help) {
        this.help = help;
    }
    private void setVersion(boolean version) {
        this.version = version;
    }
    private void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
