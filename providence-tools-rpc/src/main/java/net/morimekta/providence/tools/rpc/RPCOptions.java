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

package net.morimekta.providence.tools.rpc;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Flag;
import net.morimekta.console.args.Option;
import net.morimekta.console.util.STTY;
import net.morimekta.providence.PServiceCallHandler;
import net.morimekta.providence.client.HttpClientHandler;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.mio.FileMessageReader;
import net.morimekta.providence.mio.FileMessageWriter;
import net.morimekta.providence.mio.IOMessageReader;
import net.morimekta.providence.mio.IOMessageWriter;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.ThriftProgramParser;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerProvider;
import net.morimekta.providence.thrift.ThriftSerializerProvider;
import net.morimekta.providence.thrift.client.NonblockingSocketClientHandler;
import net.morimekta.providence.thrift.client.SocketClientHandler;
import net.morimekta.providence.tools.common.handler.SetHeadersInitializer;
import net.morimekta.providence.tools.common.options.CommonOptions;
import net.morimekta.providence.tools.common.options.ConvertStream;
import net.morimekta.providence.tools.common.options.ConvertStreamParser;
import net.morimekta.providence.tools.common.options.Format;
import net.morimekta.providence.tools.common.options.Utils;
import net.morimekta.util.Strings;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static net.morimekta.console.util.Parser.dir;
import static net.morimekta.console.util.Parser.i32;
import static net.morimekta.console.util.Parser.oneOf;
import static net.morimekta.providence.tools.common.options.Utils.collectConfigIncludes;
import static net.morimekta.providence.tools.common.options.Utils.collectIncludes;

/**
 * Options used by the providence converter.
 */
@SuppressWarnings("all")
public class RPCOptions extends CommonOptions {
    protected ConvertStream in = new ConvertStream(Format.json, null);
    protected ConvertStream out = new ConvertStream(Format.pretty_json, null);
    protected boolean strict = false;
    protected List<File> includes = new LinkedList<>();
    protected String service = "";
    protected Format format = Format.binary;
    protected int connect_timeout = 10000;
    protected int read_timeout = 10000;
    protected List<String> headers = new LinkedList<>();
    protected String endpoint = "";

    public RPCOptions(STTY tty) {
        super(tty);
    }

    @Override
    public ArgumentParser getArgumentParser(String prog, String description) throws IOException {
        ArgumentParser parser = super.getArgumentParser(prog, description);

        parser.add(new Option("--include", "I", "dir", "Allow includes of files in directory", dir(this::addInclude), null, true, false, false));
        parser.add(new Option("--in", "i", "spec", "Input specification", new ConvertStreamParser().andApply(this::setIn), "json"));
        parser.add(new Option("--out", "o", "spec", "Output Specification", new ConvertStreamParser().andApply(this::setOut), "pretty_json"));
        parser.add(new Option("--service", "s", "srv", "Qualified identifier name from definitions to use for parsing source file.",
                              this::setService, null, false, true, false));
        parser.add(new Option("--format", "f", "fmt", "Request RPC format", oneOf(Format.class, this::setFormat), "binary"));
        parser.add(new Option("--connect_timeout", "C", "ms", "Connection timeout in milliseconds. 0 means infinite.", i32(this::setConnectTimeout), "10000"));
        parser.add(new Option("--read_timeout", "R", "ms", "Request timeout in milliseconds. 0 means infinite.", i32(this::setReadTimeout), "10000"));
        parser.add(new Option("--header", "H", "hdr", "Header to set on the request, K/V separated by ':'.", this::addHeaders, null, true, false, false));
        parser.add(new Flag("--strict", "S", "Read incoming messages strictly.", this::setStrict));
        parser.add(new Argument("URL", "The endpoint URI", this::setEndpoint));

        return parser;
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

    public void addInclude(File include) {
        this.includes.add(include);
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public void setConnectTimeout(int connect_timeout) {
        this.connect_timeout = connect_timeout;
    }

    public void setReadTimeout(int read_timeout) {
        this.read_timeout = read_timeout;
    }

    public void addHeaders(String header) {
        this.headers.add(header);
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    protected Serializer getSerializer(Format format) {
        return format.createSerializer(strict);
    }

    public PService getDefinition() throws ParseException, IOException {
        Map<String, File> includeMap = getIncludeMap(includes);
        if (service.isEmpty()) {
            throw new ArgumentException("Missing service type name");
        }

        Set<File> rootSet = new TreeSet<File>();
        for (File file : includeMap.values()) {
            rootSet.add(file.getParentFile());
        }

        String namespace = service.substring(0, service.lastIndexOf("."));
        namespace = namespace.replaceAll("[-.]", "_");

        TypeLoader loader = new TypeLoader(rootSet, new ThriftProgramParser());

        try {
            if (!includeMap.containsKey(namespace)) {
                throw new ArgumentException("No package " + namespace + " found in include path.\nFound: " +
                                            Strings.join(", ", new TreeSet<Object>(includeMap.keySet())));
            }

            loader.load(includeMap.get(namespace));
        } catch (IOException e) {
            throw new ArgumentException(e.getLocalizedMessage());
        }

        PService srv = loader.getRegistry().getService(service, null);
        if (srv == null) {
            CProgram document = loader.getRegistry().getDocumentForPackage(namespace);
            Set<String> services = new TreeSet<>(
                    document.getServices()
                            .stream().map(s -> s.getQualifiedName())
                            .collect(Collectors.toSet()));

            throw new ArgumentException(
                         "Unknown service %s in %s.\n" +
                         "Found %s",
                         service, namespace,
                         services.size() == 0 ? "none" : Strings.join(", ", services));
        }

        return srv;
    }

    public MessageReader getInput() throws ParseException {
        Format fmt = Format.json;
        File file = null;
        if (in != null) {
            fmt = in.format != null ? in.format : fmt;
            file = in.file;
        }

        Serializer serializer = getSerializer(fmt);
        if (file != null) {
            return new FileMessageReader(file, serializer);
        } else {
            BufferedInputStream is = new BufferedInputStream(System.in);
            return new IOMessageReader(is, serializer);
        }
    }

    public MessageWriter getOutput()
            throws IOException {
        Format fmt = Format.pretty_json;
        File file = null;
        if (out != null) {
            fmt = out.format != null ? out.format : fmt;
            file = out.file;
        }

        final Serializer serializer = getSerializer(fmt);
        if (file != null) {
            if (file.exists() && !file.isFile()) {
                throw new ArgumentException("%s exists and is not a file.", file.getAbsolutePath());
            }

            return new FileMessageWriter(file, serializer);
        } else {
            return new IOMessageWriter(System.out, serializer);
        }
    }

    public PServiceCallHandler getHandler() {
        Serializer serializer = getSerializer(format);
        URI uri = URI.create(endpoint);
        if (uri.getScheme() == null || uri.getScheme()
                                          .length() == 0) {
            throw new ArgumentException("No protocol on URI: " + endpoint);
        }
        if (uri.getScheme().startsWith("thrift")) {
            if (    // Must have host and port.
                    (uri.getPort() < 1) ||
                    (uri.getHost() == null || uri.getHost().length() == 0) ||
                    // No path, query or fragment.
                    (uri.getFragment() != null && uri.getFragment().length() > 0) ||
                    (uri.getQuery() != null && uri.getQuery().length() > 0) ||
                    (uri.getPath() != null && uri.getPath().length() > 0)) {
                throw new ArgumentException("Illegal thrift URI: " + endpoint);
            }

            InetSocketAddress address = new InetSocketAddress(uri.getHost(), uri.getPort());

            switch (uri.getScheme()) {
                case "thrift":
                    return new SocketClientHandler(serializer, address, connect_timeout, read_timeout);
                case "thrift+nonblocking":
                    return new NonblockingSocketClientHandler(serializer, address, connect_timeout, read_timeout);
                default:
                    throw new ArgumentException("Unknown thrift protocol " + uri.getScheme());
            }
        }


        GenericUrl url = new GenericUrl(endpoint);
        Map<String, String> hdrs = new HashMap<>();
        for (String hdr : headers) {
            String[] parts = hdr.split("[:]", 2);
            if (parts.length != 2) {
                throw new ArgumentException("Invalid headers param: " + hdr);
            }
            hdrs.put(parts[0].trim(), parts[1].trim());
        }

        HttpTransport transport = Utils.createTransport();
        HttpRequestFactory factory = transport.createRequestFactory(new SetHeadersInitializer(hdrs, connect_timeout, read_timeout));
        SerializerProvider serializerProvider = new ThriftSerializerProvider(serializer.mimeType());
        return new HttpClientHandler(() -> url, factory, serializerProvider);
    }
}
