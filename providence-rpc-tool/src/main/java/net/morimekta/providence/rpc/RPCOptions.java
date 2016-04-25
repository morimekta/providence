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

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import net.morimekta.providence.PClientHandler;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.ThriftParser;
import net.morimekta.providence.rpc.handler.FileMessageReader;
import net.morimekta.providence.rpc.handler.FileMessageWriter;
import net.morimekta.providence.rpc.handler.HttpClientHandler;
import net.morimekta.providence.rpc.handler.SetHeadersInitializer;
import net.morimekta.providence.rpc.options.ConvertStream;
import net.morimekta.providence.rpc.options.Format;
import net.morimekta.providence.rpc.options.FormatOptionsHandler;
import net.morimekta.providence.rpc.options.StreamOptionHandler;
import net.morimekta.providence.serializer.*;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static net.morimekta.console.FormatString.except;

/**
 * Options used by the providence converter.
 */
@SuppressWarnings("all")
public class RPCOptions {
    @Option(name = "--help",
            aliases = {"-h", "-?"},
            help = true,
            usage = "This help listing.")
    protected boolean mHelp;

    @Option(name = "--in",
            aliases = {"-i"},
            usage = "Input specification",
            metaVar = "spec",
            handler = StreamOptionHandler.class)
    protected ConvertStream in = new ConvertStream(Format.json, null);

    @Option(name = "--out",
            aliases = {"-o"},
            usage = "Output specification",
            metaVar = "spec",
            handler = StreamOptionHandler.class)
    protected ConvertStream out = new ConvertStream(Format.pretty_json, null);

    @Option(name = "--strict",
            aliases = {"-S"},
            usage = "Read incoming messages strictly.")
    protected boolean strict = false;

    @Option(name = "--include",
            aliases = {"-I"},
            metaVar = "dir",
            usage = "Include from directories. Defaults to PWD.")
    protected List<File> includes = new LinkedList<>();

    @Option(name = "--service",
            aliases = {"-s"},
            required = true,
            metaVar = "srv",
            usage = "Qualified identifier name from definitions to use for parsing source file.")
    protected String service;

    @Option(name = "--format",
            aliases = {"-f"},
            metaVar = "fmt",
            handler = FormatOptionsHandler.class,
            usage = "Request RPC format")
    protected Format format = Format.versioned_binary;

    @Option(name = "--header",
            aliases = {"-H"},
            metaVar = "hdr",
            usage = "Header to set on the request, K/V separated by ':'.")
    protected List<String> headers = new LinkedList<>();

    @Argument(required = true,
              metaVar = "URL")
    protected String endpoint;

    protected Serializer getSerializer(CmdLineParser cli, Format format) throws CmdLineException {
        switch (format) {
            case binary:
                return new BinarySerializer(strict, false);
            case versioned_binary:
                return new BinarySerializer(strict, true);
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
        }

        throw except(cli, "Unknown format %s", format.name());
    }

    public boolean isHelp() {
        return mHelp;
    }

    public void collectIncludes(File dir, Map<String, File> includes, CmdLineParser cli) throws CmdLineException {
        if (!dir.exists()) {
            throw except(cli, "No such include directory: " + dir.getPath());
        }
        if (!dir.isDirectory()) {
            throw except(cli, "Not a directory: " + dir.getPath());
        }
        for (File file : dir.listFiles()) {
            if (file.isHidden()) {
                continue;
            }
            if (file.isFile() && file.canRead() && isThriftFile(file.getName())) {
                includes.put(namespaceFromFile(file), file);
            }
        }
    }

    private boolean isThriftFile(String name) {
        return name.endsWith(".providence") ||
                name.endsWith(".thrift") ||
                name.endsWith(".thr") ||
                name.endsWith(".thr");
    }

    private String namespaceFromFile(File file) {
        String name = file.getName();
        if (name.endsWith(".providence")) {
            name = name.substring(0, name.length() - 11);
        } else if (name.endsWith(".thrift")) {
            name = name.substring(0, name.length() - 7);
        } else if (name.endsWith(".thr") || name.endsWith(".pvd")) {
            name = name.substring(0, name.length() - 4);
        }

        return name.replaceAll("[-.]", "_");
    }

    public PService getDefinition(CmdLineParser cli)
            throws CmdLineException, ParseException {
        if (service.isEmpty()) {
            throw except(cli, "Input type.");
        }

        Map<String, File> includeMap = new HashMap<>();
        if (includes.isEmpty()) {
            includes.add(new File("."));
        }
        for (File file : includes) {
            collectIncludes(file, includeMap, cli);
        }

        Set<File> rootSet = new TreeSet<File>();
        for (File file : includeMap.values()) {
            rootSet.add(file.getParentFile());
        }

        String namespace = service.substring(0, service.lastIndexOf("."));
        namespace = namespace.replaceAll("[-.]", "_");

        TypeLoader loader = new TypeLoader(rootSet, new ThriftParser());

        try {
            loader.load(includeMap.get(namespace));
        } catch (IOException e) {
            throw except(cli, e.getLocalizedMessage());
        }

        PService srv = loader.getRegistry().getServiceProvider(service, null).getService();
        if (srv == null) {
            throw except(cli, "");
        }

        return srv;
    }

    public MessageReader getInput(CmdLineParser cli)
            throws CmdLineException, ParseException {
        Format fmt = Format.json;
        File file = null;
        if (in != null) {
            fmt = in.format != null ? in.format : fmt;
            file = in.file;
        }

        Serializer serializer = getSerializer(cli, fmt);
        if (file != null) {
            return new FileMessageReader(file, serializer);
        } else {
            BufferedInputStream is = new BufferedInputStream(System.in);
            return new IOMessageReader(is, serializer);
        }
    }

    public MessageWriter getOutput(CmdLineParser cli)
            throws CmdLineException, IOException {
        Format fmt = Format.pretty_json;
        File file = null;
        if (out != null) {
            fmt = out.format != null ? out.format : fmt;
            file = out.file;
        }

        final Serializer serializer = getSerializer(cli, fmt);
        if (file != null) {
            if (file.exists() && !file.isFile()) {
                throw except(cli, "%s exists and is not a file.", file.getAbsolutePath());
            }

            return new FileMessageWriter(file, serializer);
        } else {
            return new IOMessageWriter(System.out, serializer);
        }
    }

    public PClientHandler getHandler(CmdLineParser cli) throws CmdLineException {
        GenericUrl endpoint = new GenericUrl(this.endpoint);
        Serializer serializer = getSerializer(cli, format);

        Map<String, String> hdrs = new HashMap<>();
        for (String hdr : headers) {
            String[] parts = hdr.split("[:]", 2);
            if (parts.length != 2) {
                throw except(cli, "Invalid headers param: " + hdr);
            }
            hdrs.put(parts[0].trim(), parts[1].trim());
        }

        HttpTransport transport = new ApacheHttpTransport();

        return new HttpClientHandler(endpoint, transport.createRequestFactory(new SetHeadersInitializer(hdrs)), serializer);
    }
}
