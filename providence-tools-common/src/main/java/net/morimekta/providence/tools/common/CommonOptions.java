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

package net.morimekta.providence.tools.common;

import net.morimekta.console.args.ArgumentOptions;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Flag;
import net.morimekta.console.args.Option;
import net.morimekta.console.util.STTY;
import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.config.ProvidenceConfigException;
import net.morimekta.providence.util.SimpleTypeRegistry;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;

import static net.morimekta.console.util.Parser.file;

/**
 * Options used by the providence converter.
 */
@SuppressWarnings("all")
public class CommonOptions {
    private   boolean help;
    private   boolean verbose;
    private   boolean version;
    protected STTY    tty;
    private File    rc = new File(System.getenv("HOME"), ".pvdrc");

    public CommonOptions(STTY tty) {
        this.tty = tty;
    }

    public ArgumentOptions getArgumentOptions() {
        return ArgumentOptions.defaults(tty).withMaxUsageWidth(120);
    }

    public ArgumentParser getArgumentParser(String prog, String description) throws IOException {
        ArgumentParser parser = new ArgumentParser(prog, Utils.getVersionString(), description, getArgumentOptions());

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
    public ProvidenceTools getConfig() throws ProvidenceConfigException {
        SimpleTypeRegistry registry = new SimpleTypeRegistry();
        registry.registerRecursively(ProvidenceTools.kDescriptor);
        ProvidenceConfig loader = new ProvidenceConfig(registry);

        ProvidenceTools config = ProvidenceTools.builder()
                                                .setGeneratorPaths(ImmutableList.of())
                                                .build();
        if (rc.isFile()) {
            config = loader.getConfig(rc, config);
        }
        return config;
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
    protected void setRc(File file) {
        this.rc = file;
    }
}
