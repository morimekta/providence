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

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Flag;
import net.morimekta.console.args.Option;
import net.morimekta.console.util.STTY;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.tools.common.options.CommonOptions;
import net.morimekta.providence.tools.common.options.ConvertStream;
import net.morimekta.providence.tools.common.options.ConvertStreamParser;
import net.morimekta.providence.tools.common.options.Format;
import net.morimekta.providence.tools.common.options.Utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static net.morimekta.console.util.Parser.dir;
import static net.morimekta.providence.tools.common.options.Utils.collectIncludes;

/**
 * Options used by the providence converter.
 */
@SuppressWarnings("all")
public class ConvertOptions extends CommonOptions {
    protected List<File> includes = new LinkedList<>();
    protected ConvertStream in = new ConvertStream(Format.json, null);
    protected ConvertStream out = new ConvertStream(Format.pretty, null);
    protected boolean strict = false;
    protected String type;

    public ConvertOptions(STTY tty) {
        super(tty);
    }

    @Override
    public ArgumentParser getArgumentParser(String prog, String description) throws IOException {
        ArgumentParser parser = super.getArgumentParser(prog, description);

        parser.add(new Option("--include", "I", "dir", "Include from directories.", dir(this::addInclude), "${PWD}", true, false, false));
        parser.add(new Option("--in", "i", "spec", "Input specification", new ConvertStreamParser().andApply(this::setIn), "binary"));
        parser.add(new Option("--out", "o", "spec", "Output specification", new ConvertStreamParser().andApply(this::setOut), "pretty"));
        parser.add(new Flag("--strict", "S", "Read incoming messages strictly.", this::setStrict));
        parser.add(new Argument("type", "Qualified identifier name from definitions to use for parsing source file.", this::setType));

        return parser;
    }

    private void addInclude(File include) {
        this.includes.add(include);
    }

    private void setIn(ConvertStream in) {
        this.in = in;
    }

    private void setOut(ConvertStream out) {
        this.out = out;
    }

    private void setStrict(boolean strict) {
        this.strict = strict;
    }

    private void setType(String type) {
        this.type = type;
    }

    private Serializer getSerializer(Format format) {
        return format.createSerializer(strict);
    }

    public <Message extends PMessage<Message, Field>, Field extends PField>
    PMessageDescriptor<Message, Field> getDefinition()
            throws ParseException {
        if (type.isEmpty()) {
            throw new ArgumentException("Input type.");
        }

        Map<String, File> includeMap = new HashMap<>();
        if (includes.isEmpty()) {
            includes.add(new File("."));
        }
        for (File file : includes) {
            collectIncludes(file, includeMap);
        }

        Set<File> rootSet = new TreeSet<File>();
        for (File file : includeMap.values()) {
            rootSet.add(file.getParentFile());
        }

        String namespace = type.substring(0, type.lastIndexOf("."));
        namespace = namespace.replaceAll("[-.]", "_");

        TypeLoader loader = new TypeLoader(rootSet, new ThriftProgramParser());

        try {
            loader.load(includeMap.get(namespace));
        } catch (IOException e) {
            throw new ArgumentException(e.getLocalizedMessage());
        }

        @SuppressWarnings("unchecked")
        PMessageDescriptor<Message, Field> descriptor = (PMessageDescriptor) loader.getRegistry().getDeclaredType(type);
        if (descriptor == null) {
            throw new ArgumentException("No available type for name %s", type);
        }

        return descriptor;
    }

    public <Message extends PMessage<Message, Field>, Field extends PField>
    Collector<Message, OutputStream, Integer> getOutput()
            throws IOException {
        return Utils.getOutput(Format.pretty, out, strict);
    }

    public <Message extends PMessage<Message, Field>, Field extends PField>
    Stream<Message> getInput() throws ParseException {
        PMessageDescriptor<Message, Field> descriptor = getDefinition();
        return Utils.getInput(descriptor, in, Format.binary, strict);
    }
}
