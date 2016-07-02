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

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentOptions;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Flag;
import net.morimekta.console.args.Option;
import net.morimekta.console.util.TerminalSize;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.converter.options.ConvertStream;
import net.morimekta.providence.converter.options.ConvertStreamParser;
import net.morimekta.providence.converter.options.Format;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.ThriftDocumentParser;
import net.morimekta.providence.reflect.util.ReflectionUtils;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.FastBinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.streams.MessageCollectors;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;

import java.io.BufferedInputStream;
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

/**
 * Options used by the providence converter.
 */
@SuppressWarnings("all")
public class ConvertOptions {
    protected List<File> includes = new LinkedList<>();
    protected ConvertStream in = new ConvertStream(Format.json, null);
    protected ConvertStream out = new ConvertStream(Format.pretty, null);
    protected boolean strict = false;
    protected String type;
    protected boolean mHelp;

    public ArgumentParser getArgumentParser(String prog, String version, String description) {
        ArgumentOptions opts = ArgumentOptions.defaults().withUsageWidth(
                Math.min(120, TerminalSize.get().cols));
        ArgumentParser parser = new ArgumentParser(prog, version, description, opts);

        parser.add(new Option("--include", "I", "Include from directories.", "dir", dir(this::addInclude), "${PWD}", true, false, false));
        parser.add(new Option("--in", "i", "Input specification", "spec", new ConvertStreamParser().andApply(this::setIn)));
        parser.add(new Option("--out", "o", "Output specification", "spec", new ConvertStreamParser().andApply(this::setOut)));
        parser.add(new Flag("--strict", "S", "Read incoming messages strictly.", this::setStrict));
        parser.add(new Argument("type", "Qualified identifier name from definitions to use for parsing source file.", this::setType));
        parser.add(new Flag("--help", "h?", "This help listing.", this::setHelp));

        return parser;
    }

    public void addInclude(File include) {
        this.includes.add(include);
    }

    public void setIn(ConvertStream in) {
        this.in = in;
    }

    public void setOut(ConvertStream out) {
        this.out = out;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setHelp(boolean mHelp) {
        this.mHelp = mHelp;
    }

    protected Serializer getSerializer(Format format) {
        switch (format) {
            case binary:
                return new BinarySerializer(strict);
            case json:
                return new JsonSerializer(strict, JsonSerializer.IdType.ID);
            case named_json:
                return new JsonSerializer(strict, JsonSerializer.IdType.NAME);
            case pretty_json:
                return new JsonSerializer(strict, JsonSerializer.IdType.NAME, JsonSerializer.IdType.NAME, true);
            case fast_binary:
                return new FastBinarySerializer(strict);
            case binary_protocol:
                return new TBinaryProtocolSerializer(strict);
            case json_protocol:
                return new TJsonProtocolSerializer(strict);
            case compact_protocol:
                return new TCompactProtocolSerializer(strict);
            case tuple_protocol:
                return new TTupleProtocolSerializer(strict);
            case pretty:
                return new PrettySerializer("  ", " ", "\n", "", false, true);
        }

        throw new ArgumentException("Unknown format %s", format.name());
    }

    public boolean isHelp() {
        return mHelp;
    }

    public void collectIncludes(File dir, Map<String, File> includes) {
        if (!dir.exists()) {
            throw new ArgumentException("No such include directory: " + dir.getPath());
        }
        if (!dir.isDirectory()) {
            throw new ArgumentException("Not a directory: " + dir.getPath());
        }
        for (File file : dir.listFiles()) {
            if (file.isHidden()) {
                continue;
            }
            if (file.isFile() && file.canRead() && ReflectionUtils.isThriftFile(file.getName())) {
                includes.put(ReflectionUtils.packageFromName(file.getName()), file);
            }
        }
    }

    public <Message extends PMessage<Message, Field>, Field extends PField>
    PStructDescriptor<Message, Field> getDefinition()
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

        TypeLoader loader = new TypeLoader(rootSet, new ThriftDocumentParser());

        try {
            loader.load(includeMap.get(namespace));
        } catch (IOException e) {
            throw new ArgumentException(e.getLocalizedMessage());
        }

        @SuppressWarnings("unchecked")
        PStructDescriptor<Message, Field> descriptor = (PStructDescriptor) loader.getRegistry()
                                                                                 .getDescriptor(type, null);
        if (descriptor == null) {
            throw new ArgumentException("No available type for name %s", type);
        }

        return descriptor;
    }

    public <Message extends PMessage<Message, Field>, Field extends PField>
    Collector<Message, OutputStream, Integer> getOutput()
            throws IOException {
        Format fmt = Format.pretty;
        File file = null;
        if (out != null) {
            fmt = out.format != null ? out.format : fmt;
            file = out.file;
        }

        final Serializer serializer = getSerializer( fmt);
        if (file != null) {
            if (file.exists() && !file.isFile()) {
                throw new ArgumentException("%s exists and is not a file.", file.getAbsolutePath());
            }

            return MessageCollectors.toFile(file, serializer);
        } else {
            return MessageCollectors.toStream(System.out, serializer);
        }
    }

    public <Message extends PMessage<Message, Field>, Field extends PField>
    Stream<Message> getInput() throws ParseException {
        PStructDescriptor<Message, Field> descriptor = getDefinition();

        Format fmt = Format.pretty;
        File file = null;
        if (in != null) {
            fmt = in.format != null ? in.format : fmt;
            file = in.file;
        }

        Serializer serializer = getSerializer(fmt);
        if (file != null) {
            try {
                return MessageStreams.file(file, serializer, descriptor);
            } catch (IOException e) {
                throw new ArgumentException("Unable to read file %s", file.getName());
            }
        } else {
            BufferedInputStream is = new BufferedInputStream(System.in);
            try {
                return MessageStreams.stream(is, serializer, descriptor);
            } catch (IOException e) {
                throw new ArgumentException("Broken pipe");
            }
        }
    }
}
