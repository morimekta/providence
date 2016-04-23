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
import net.morimekta.providence.converter.options.ConvertStream;
import net.morimekta.providence.converter.options.Format;
import net.morimekta.providence.converter.options.PrettyPrintSerializer;
import net.morimekta.providence.converter.options.StreamOptionHandler;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.ThriftParser;
import net.morimekta.providence.serializer.PBinarySerializer;
import net.morimekta.providence.serializer.PFastBinarySerializer;
import net.morimekta.providence.serializer.PJsonSerializer;
import net.morimekta.providence.serializer.PSerializer;
import net.morimekta.providence.streams.MessageCollectors;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.providence.thrift.*;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.morimekta.console.FormatString.except;

/**
 * Options used by the providence converter.
 */
@SuppressWarnings("all")
public class ConvertOptions {
    @Option(name = "--include",
            aliases = {"-I"},
            metaVar = "dir",
            usage = "Include from directories")
    protected List<File> include = new LinkedList<>();

    @Option(name = "--in",
            aliases = {"-i"},
            usage = "Input specification",
            required = true,
            metaVar = "spec",
            handler = StreamOptionHandler.class)
    protected ConvertStream in = new ConvertStream(Format.json, null);

    @Option(name = "--out",
            aliases = {"-o"},
            usage = "Output specification",
            metaVar = "spec",
            handler = StreamOptionHandler.class)
    protected ConvertStream out = new ConvertStream(Format.pretty, null);

    @Option(name = "--strict",
            aliases = {"-s"},
            usage = "Read incoming messages strictly.")
    protected boolean strict = false;

    @Argument(required = true,
              metaVar = "type",
              usage = "Qualified identifier name from definitions to use for parsing source file.")
    protected String type;

    @Option(name = "--help",
            aliases = {"-h", "-?"},
            help = true,
            usage = "This help listing.")
    protected boolean mHelp;

    protected PSerializer getSerializer(CmdLineParser cli, Format format) throws CmdLineException {
        switch (format) {
            case binary:
                return new PBinarySerializer(strict);
            case json:
                return new PJsonSerializer(strict, PJsonSerializer.IdType.ID);
            case named_json:
                return new PJsonSerializer(strict, PJsonSerializer.IdType.NAME);
            case pretty_json:
                return new PJsonSerializer(strict, PJsonSerializer.IdType.NAME, PJsonSerializer.IdType.NAME, true);
            case fast_binary:
                return new PFastBinarySerializer(strict);
            case binary_protocol:
                return new TBinaryProtocolSerializer(strict);
            case json_protocol:
                return new TJsonProtocolSerializer(strict);
            case simple_json_protocol:
                return new TSimpleJsonProtocolSerializer();
            case compact_protocol:
                return new TCompactProtocolSerializer(strict);
            case tuple_protocol:
                return new TTupleProtocolSerializer(strict);
            case pretty:
                return new PrettyPrintSerializer();
        }

        throw except(cli, "Unknown format %s", format.name());
    }

    public boolean isHelp() {
        return mHelp;
    }

    public void collectIncludes(File dir, Map<String, File> includes) {
        for (File file : dir.listFiles()) {
            if (file.isHidden()) {
                continue;
            }
            if (file.isDirectory()) {
                collectIncludes(file, includes);
            } else {
                if (file.canRead() && file.getName()
                                          .endsWith(".thrift")) {
                    String name = file.getName();
                    name = name.substring(0, name.length() - 7);
                    name = name.replaceAll("[-.]", "_");
                    includes.put(name, file);
                }
            }
        }
    }

    public <T extends PMessage<T>, F extends PField> PStructDescriptor<T, F> getDefinition(CmdLineParser cli)
            throws CmdLineException, ParseException {
        if (type.isEmpty()) {
            throw except(cli, "Input type.");
        }

        Map<String, File> includeMap = new HashMap<>();

        for (File inc : include) {
            if (inc.isFile()) {
                if (inc.getName()
                       .endsWith(".thrift")) {
                    String name = inc.getName();
                    name = name.substring(0, name.length() - 7);
                    name = name.replaceAll("[-.]", "_");
                    includeMap.put(name, inc);
                } else {
                    throw except(cli, "%s is not a thrift file.", inc.getName());
                }
            } else if (inc.isDirectory()) {
                collectIncludes(inc, includeMap);
            }
        }

        List<File> rootSet = includeMap.values()
                                       .stream()
                                       .map(File::getParentFile)
                                       .collect(Collectors.toList());

        String namespace = type.substring(0, type.lastIndexOf("."));
        namespace = namespace.replaceAll("[-.]", "_");

        TypeLoader loader = new TypeLoader(rootSet, new ThriftParser());

        try {

            loader.load(includeMap.get(namespace));
        } catch (IOException e) {
            throw except(cli, e.getLocalizedMessage());
        }

        @SuppressWarnings("unchecked")
        PStructDescriptor<T, F> descriptor = (PStructDescriptor) loader.getRegistry()
                                                                       .getDescriptor(type, null);
        if (descriptor == null) {
            throw except(cli, "No available type for name %s", type);
        }

        return descriptor;
    }

    public <T extends PMessage<T>> Collector<T, OutputStream, Integer> getOutput(CmdLineParser cli)
            throws CmdLineException, IOException {
        Format fmt = Format.pretty;
        File file = null;
        if (out != null) {
            fmt = out.format != null ? out.format : fmt;
            file = out.file;
        }

        final PSerializer serializer = getSerializer(cli, fmt);
        if (file != null) {
            if (file.exists() && !file.isFile()) {
                throw except(cli, "%s exists and is not a file.", file.getAbsolutePath());
            }

            return MessageCollectors.toFile(file, serializer);
        } else {
            return MessageCollectors.toStream(System.out, serializer);
        }
    }

    public <T extends PMessage<T>, F extends PField> Stream<T> getInput(CmdLineParser cli)
            throws CmdLineException, ParseException {
        PStructDescriptor<T, F> descriptor = getDefinition(cli);

        Format fmt = Format.pretty;
        File file = null;
        if (in != null) {
            fmt = in.format != null ? in.format : fmt;
            file = in.file;
        }

        PSerializer serializer = getSerializer(cli, fmt);
        if (file != null) {
            try {
                return MessageStreams.file(file, serializer, descriptor);
            } catch (IOException e) {
                throw except(cli, "Unable to read file %s", file.getName());
            }
        } else {
            BufferedInputStream is = new BufferedInputStream(System.in);
            try {
                return MessageStreams.stream(is, serializer, descriptor);
            } catch (IOException e) {
                throw except(cli, "Broken pipe");
            }
        }
    }

}
