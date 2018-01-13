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
import net.morimekta.providence.tools.common.CommonOptions;
import net.morimekta.providence.tools.common.formats.ConvertStream;
import net.morimekta.providence.tools.common.formats.ConvertStreamParser;
import net.morimekta.providence.tools.common.formats.Format;
import net.morimekta.providence.tools.common.formats.FormatUtils;
import net.morimekta.providence.tools.rpc.utils.SetHeadersInitializer;
import net.morimekta.util.Strings;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static net.morimekta.console.util.Parser.dir;
import static net.morimekta.console.util.Parser.i32;
import static net.morimekta.console.util.Parser.oneOf;

/**
 * Options used by the providence converter.
 */
@SuppressWarnings("all")
public class RPCOptions extends CommonOptions {
    protected ConvertStream in              = new ConvertStream(Format.json);
    protected ConvertStream out             = new ConvertStream(Format.pretty_json);
    protected boolean       strict          = false;
    protected List<File>    includes        = new ArrayList<>();
    protected String        service         = "";
    protected Format        format          = Format.binary;
    protected int           connect_timeout = 10000;
    protected int           read_timeout    = 10000;
    protected List<String>  headers         = new ArrayList<>();
    protected String        endpoint        = "";

    public RPCOptions(STTY tty) {
        super(tty);
    }

    @Override
    public ArgumentParser getArgumentParser(String prog, String description) throws IOException {
        ArgumentParser parser = super.getArgumentParser(prog, description);

        parser.add(new Option("--include", "I", "dir", "Allow includes of files in directory",
                              dir(this::addInclude), null, true, false, false));
        parser.add(new Option("--in", "i", "spec", "Input specification",
                              new ConvertStreamParser(in).andApply(this::setIn), in.toString()));
        parser.add(new Option("--out", "o", "spec", "Output Specification",
                              new ConvertStreamParser(out).andApply(this::setOut), out.toString()));
        parser.add(new Option("--service", "s", "srv", "Qualified identifier name from definitions to use for parsing source file.",
                              this::setService, null, false, true, false));
        parser.add(new Option("--format", "f", "fmt", "Request RPC format",
                              oneOf(Format.class, this::setFormat), format.name()));
        parser.add(new Option("--connect_timeout", "C", "ms", "Connection timeout in milliseconds. 0 means infinite.",
                              i32(this::setConnectTimeout), "10000"));
        parser.add(new Option("--read_timeout", "R", "ms", "Request timeout in milliseconds. 0 means infinite.",
                              i32(this::setReadTimeout), "10000"));
        parser.add(new Option("--header", "H", "hdr", "Header to set on the request, K/V separated by ':'.",
                              this::addHeaders, null, true, false, false));
        parser.add(new Flag("--strict", "S", "Read incoming messages strictly.",
                            this::setStrict));
        parser.add(new Argument("URI", "The endpoint URI",
                                this::setEndpoint, null, s -> {
            try {
                if (!s.contains("://")) return false;

                URI uri = new URI(s);
                if (isNullOrEmpty(uri.getAuthority())) {
                    throw new ArgumentException("Missing authority in URI: '" + s + "'");
                }
                return true;
            } catch (URISyntaxException e) {
                throw new ArgumentException(e, e.getMessage());
            }
        }, false, true, false));

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
        Map<String, File> includeMap = FormatUtils.getIncludeMap(getRc(), includes);
        if (service.isEmpty()) {
            throw new ArgumentException("Missing service type name");
        }

        Set<File> rootSet = new TreeSet<File>();
        for (File file : includeMap.values()) {
            rootSet.add(file.getParentFile());
        }

        String programName = service.substring(0, service.lastIndexOf("."));

        TypeLoader loader = new TypeLoader(rootSet, new ThriftProgramParser());

        try {
            if (!includeMap.containsKey(programName)) {
                throw new ArgumentException("No program " + programName + " found in include path.\n" +
                                            "Found: " + Strings.join(", ", new TreeSet<Object>(includeMap.keySet())));
            }

            loader.load(includeMap.get(programName));
        } catch (IOException e) {
            throw new ArgumentException(e.getLocalizedMessage());
        }

        String filePath = includeMap.get(programName).getCanonicalFile().getAbsolutePath();

        PService srv = loader.getProgramRegistry().getService(service, null);
        if (srv == null) {
            CProgram document = loader.getProgramRegistry().registryForPath(filePath).getProgram();
            Set<String> services = new TreeSet<>(
                    document.getServices()
                            .stream().map(s -> s.getQualifiedName())
                            .collect(Collectors.toSet()));

            throw new ArgumentException(
                         "Unknown service %s in %s.\n" +
                         "Found %s",
                         service, programName,
                         services.size() == 0 ? "none" : Strings.join(", ", services));
        }

        return srv;
    }

    public MessageReader getInput() throws IOException {
        return FormatUtils.getServiceInput(in, strict);
    }

    public MessageWriter getOutput()
            throws IOException {
        return FormatUtils.getServiceOutput(out, strict);
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

        HttpTransport transport = new NetHttpTransport();
        HttpRequestFactory factory = transport.createRequestFactory(new SetHeadersInitializer(hdrs, connect_timeout, read_timeout));
        SerializerProvider serializerProvider = new ThriftSerializerProvider(serializer.mediaType());
        return new HttpClientHandler(() -> url, factory, serializerProvider);
    }
}
