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

package net.morimekta.providence.tools.config.options;

import net.morimekta.console.args.ArgumentOptions;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Flag;
import net.morimekta.console.args.Option;
import net.morimekta.console.args.Property;
import net.morimekta.console.args.SubCommand;
import net.morimekta.console.args.SubCommandSet;
import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.ThriftDocumentParser;
import net.morimekta.providence.reflect.util.ReflectionUtils;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.tools.config.cmd.Command;
import net.morimekta.providence.tools.config.cmd.Help;
import net.morimekta.providence.tools.config.cmd.Params;
import net.morimekta.providence.tools.config.cmd.Print;
import net.morimekta.providence.tools.config.cmd.Validate;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static net.morimekta.console.util.Parser.dir;

/**
 * Options used by the providence converter.
 */
// @SuppressWarnings("all")
public class Options {
    private boolean             help     = false;
    private Map<String, File>   includes = new TreeMap<>();
    private Map<String, String> params   = new TreeMap<>();
    private List<File>          roots    = new LinkedList<>();
    private Command             command  = null;

    private SubCommandSet<Command> commandSet;

    public ArgumentParser getArgumentParser(String prog, String version, String description) {
        ArgumentOptions opts = ArgumentOptions.defaults().withMaxUsageWidth(120);
        ArgumentParser parser = new ArgumentParser(prog, version, description, opts);
        parser.add(new Flag("--help", "h?", "This help message.", this::setHelp));
        parser.add(new Option("--include", "I", "dir", "Read config definitions from these directories.", dir().andApply(dir -> this.collectIncludes(dir, includes)), null, true, false, false));
        parser.add(new Option("--config", "C", "dir", "Config directory locations.", dir(roots::add), null, true, false, false));
        parser.add(new Property("--param", 'P', "key", "value", "Config parameter override.", params::put, false));

        commandSet = new SubCommandSet<>("cmd", "Config action.", this::setCommand, null, true, opts);
        commandSet.add(new SubCommand<>("help", "Show help for sub-commands.", false, () -> new Help(commandSet, parser), cmd -> cmd.parser(parser)));
        commandSet.add(new SubCommand<>("print", "Print the resulting config.", false, Print::new, cmd -> cmd.parser(parser), "p", "pr"));
        commandSet.add(new SubCommand<>("validate", "Validate the file, print an error if not valid.", false, Validate::new, cmd -> cmd.parser(parser)));
        commandSet.add(new SubCommand<>("params", "Show params that can be applied on the config.", false, Params::new, cmd -> cmd.parser(parser)));
        parser.add(commandSet);

        return parser;
    }

    public SubCommandSet<Command> getCommandSet() {
        return commandSet;
    }

    private void setHelp(boolean help) {
        this.help = help;
    }

    private void setCommand(Command command) {
        this.command = command;
    }

    public boolean isHelp() {
        return help || command == null;
    }

    private void collectIncludes(File dir, Map<String, File> includes) {
        for (File file : dir.listFiles()) {
            if (file.isHidden()) {
                continue;
            }
            if (file.isFile() && file.canRead() && ReflectionUtils.isThriftFile(file.getName())) {
                includes.put(ReflectionUtils.packageFromName(file.getName()), file);
            }
        }
    }

    public void execute() throws SerializerException {
        Set<File> rootSet = includes.values()
                                    .stream()
                                    .map(File::getParentFile)
                                    .collect(Collectors.toSet());
        TypeLoader loader = new TypeLoader(rootSet, new ThriftDocumentParser());
        for (File file : includes.values()) {
            try {
                loader.load(file);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        command.execute(new ProvidenceConfig(loader.getRegistry(), params, roots));
    }
}
