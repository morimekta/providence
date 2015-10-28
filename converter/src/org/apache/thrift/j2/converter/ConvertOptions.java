/*
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

package org.apache.thrift.j2.converter;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.mio.TFileMessageReader;
import org.apache.thrift.j2.mio.TMessageReader;
import org.apache.thrift.j2.mio.TMessageWriter;
import org.apache.thrift.j2.mio.TRecordMessageWriter;
import org.apache.thrift.j2.mio.TShardMessageReader;
import org.apache.thrift.j2.mio.utils.Sequence;
import org.apache.thrift.j2.mio.utils.Shard;
import org.apache.thrift.j2.mio.utils.ShardUtil;
import org.apache.thrift.j2.protocol.TBinaryProtocolSerializer;
import org.apache.thrift.j2.protocol.TCompactProtocolSerializer;
import org.apache.thrift.j2.protocol.TJsonProtocolSerializer;
import org.apache.thrift.j2.protocol.TSimpleJsonProtocolSerializer;
import org.apache.thrift.j2.protocol.TTupleProtocolSerializer;
import org.apache.thrift.j2.reflect.parser.TMessageParser;
import org.apache.thrift.j2.reflect.parser.TParser;
import org.apache.thrift.j2.reflect.parser.TThriftParser;
import org.apache.thrift.j2.serializer.TBinarySerializer;
import org.apache.thrift.j2.serializer.TJsonSerializer;
import org.apache.thrift.j2.serializer.TSerializeException;
import org.apache.thrift.j2.serializer.TSerializer;
import org.apache.thrift.j2.util.TPrettyPrinter;
import org.apache.thrift.j2.util.io.CountingOutputStream;
import org.apache.utils.FormatString;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.EnumOptionHandler;
import org.kohsuke.args4j.spi.Setter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Stein Eldar Johnsen
 * @since 27.09.15
 */
public class ConvertOptions {
    public static class FormatOptionHandler extends EnumOptionHandler<Format> {
        public FormatOptionHandler(CmdLineParser parser,
                                   OptionDef option,
                                   Setter<Format> setter) {
            super(parser, option, setter, Format.class);
        }

        @Override
        public String getDefaultMetaVariable() {
            return "[format]";
        }
    }

    protected enum Format {
        // TSerializer
        json("Compact JSON with IDs."),
        named("Compact JSON with names."),
        binary("Compact binary_protocol serialization."),
        // TProtocolSerializer
        json_protocol("TJsonProtocol"),
        binary_protocol("TBinaryProtocol"),
        compact_protocol("TCompactProtocol"),
        tuple_protocol("TTupleProtocol"),
        // Pseudo (out only)
        simple_json_protocol("TSimpleJSONProtocol (output only)"),
        pretty("Pretty-Printer (output only)"),;

        public String desc;

        Format(String desc) {
            this.desc = desc;
        }
    }

    protected enum Syntax {
        thrift,
        json
    }

    @Option(name = "--includes",
            aliases = { "-I" },
            metaVar = "dir",
            usage = "Include from directories")
    protected List<String> mIncludes = new LinkedList<>();

    @Option(name = "--def",
            required = true,
            metaVar = "file",
            usage = "Definition file to read")
    protected String mDefinition;

    @Option(name = "--syntax",
            usage = "File syntax of definition")
    protected Syntax mSyntax = Syntax.thrift;

    @Option(name = "--type",
            required = true,
            metaVar = "type.Name",
            usage = "Qualified identifier name from definitions to use for parsing source file.")
    protected String mType;

    @Option(name = "--if",
            required = true,
            usage = "Input format",
            handler = FormatOptionHandler.class)
    protected Format mInFormat;

    @Option(name = "--of",
            required = true,
            usage = "Output format",
            handler = FormatOptionHandler.class)
    protected Format mOutFormat;

    @Option(name = "--shard",
            aliases = {"-s"},
            usage = "Output shard")
    protected int mShard;

    @Option(name = "--out",
            aliases = {"-o"},
            usage = "Output file pattern to write to.")
    protected String mOut;

    @Argument(metaVar = "file",
            required = true,
            usage = "Source file to read")
    protected String mFile;

    @Option(name = "--help",
            aliases = { "-h", "-?" },
            help = true,
            usage = "This help listing.")
    protected boolean mHelp;

    protected TSerializer getSerializer(CmdLineParser cli, Format format) throws CmdLineException {
        switch (format) {
            case binary:
                return new TBinarySerializer();
            case json:
                return new TJsonSerializer(TJsonSerializer.IdType.ID);
            case named:
                return new TJsonSerializer(TJsonSerializer.IdType.NAME);
            case binary_protocol:
                return new TBinaryProtocolSerializer();
            case json_protocol:
                return new TJsonProtocolSerializer();
            case simple_json_protocol:
                return new TSimpleJsonProtocolSerializer();
            case compact_protocol:
                return new TCompactProtocolSerializer();
            case tuple_protocol:
                return new TTupleProtocolSerializer();
            case pretty:
                return new TSerializer() {
                    @Override
                    public int serialize(OutputStream output, TMessage<?> message)
                            throws IOException, TSerializeException {
                        CountingOutputStream out = new CountingOutputStream(output);
                        out.write(new TPrettyPrinter().format(message).getBytes(StandardCharsets.UTF_8));
                        out.flush();
                        return out.getByteCount();
                    }

                    @Override
                    public <T> int serialize(OutputStream output, TDescriptor<T> descriptor, T value)
                            throws IOException, TSerializeException {
                        throw new IOException("Only able to print messages.");
                    }

                    @Override
                    public <T> T deserialize(InputStream input, TDescriptor<T> descriptor)
                            throws IOException, TSerializeException {
                        throw new IOException("Pretty printer not allowed as input type.");
                    }
                };
        }

        throw new CmdLineException(cli, new FormatString("Unknown format %s"), format.name());
    }

    public boolean isHelp() {
        return mHelp;
    }

    public List<File> getIncludes(CmdLineParser cli) throws CmdLineException {
        List<File> includes = new LinkedList<>();
        for (String include : mIncludes) {
            File file = new File(include);
            if (!file.exists()) {
                throw new CmdLineException(cli, new FormatString("Included dir %s does not exist."), include);
            }
            if (!file.isDirectory()) {
                throw new CmdLineException(cli, new FormatString("Included dir %s is not a directory."), include);
            }
            includes.add(file);
        }
        return includes;
    }

    public File getDefinition(CmdLineParser cli) throws CmdLineException {
        if (mDefinition.isEmpty()) {
            throw new CmdLineException(cli, new FormatString("No input file(s)."));
        }

        File file = new File(mDefinition);
        if (!file.exists()) {
            throw new CmdLineException(cli, new FormatString("No such file %s."), mDefinition);
        }
        if (file.isDirectory()) {
            throw new CmdLineException(cli, new FormatString("%s is a directory."), mDefinition);
        }
        return file;
    }

    public TParser getParser(CmdLineParser cli) throws CmdLineException {
        switch (mSyntax) {
            case thrift:
                return new TThriftParser();
            case json:
                return new TMessageParser(new TJsonSerializer());
            default:
                throw new CmdLineException(cli, new FormatString("Unknown SLI syntax %s."), mSyntax.name());
        }
    }

    public <T extends TMessage<T>> TMessageWriter<T> getOutput(CmdLineParser cli) throws CmdLineException, IOException {
        final TSerializer serializer = getSerializer(cli, mOutFormat);
        if (mOut != null) {
            if (ShardUtil.shardedName(mOut)) {
                Shard shard = new Shard(mOut);
                Sequence sequence = new Sequence(String.format("%s-%04d", shard.name, mShard));
                return new TRecordMessageWriter<>(sequence, serializer);
            }
            File file = new File(mOut);
            if (!file.exists()) {
                throw new CmdLineException(cli, new FormatString("No such input file %s"), mOut);
            }
            if (!file.isFile()) {
                throw new CmdLineException(cli, new FormatString("%s is not a file."), mOut);
            }
            final FileOutputStream out = new FileOutputStream(file);
            return new TMessageWriter<T>() {
                @Override
                public void flush() throws IOException {
                    out.flush();
                }

                @Override
                public void close() throws IOException {
                    out.close();
                }

                @Override
                public void write(TMessage message) throws IOException {
                    try {
                        serializer.serialize(out, message);
                    } catch (TSerializeException tse) {
                        throw new IOException("Unable to serialize output.", tse);
                    }
                }
            };
        } else {
            return new TMessageWriter<T>() {
                @Override
                public void flush() throws IOException {
                    System.out.flush();
                }

                @Override
                public void close() throws IOException {
                    System.out.println();
                    System.out.flush();
                }

                @Override
                public void write(TMessage message) throws IOException {
                    try {
                        serializer.serialize(System.out, message);
                    } catch (TSerializeException tse) {
                        throw new IOException("Unable to serialize output.", tse);
                    }
                }
            };
        }
    }

    public TMessageReader<?> getInput(CmdLineParser cli, TStructDescriptor<?> descriptor) throws CmdLineException {
        TSerializer serializer = getSerializer(cli, mInFormat);

        if (ShardUtil.shardedName(mFile)) {
            return new TShardMessageReader<>(mFile, serializer, descriptor);
        } else if (ShardUtil.sequencedName(mFile)) {
            return new TShardMessageReader<>(ShardUtil.sequencePrefix(mFile), serializer, descriptor);
        } else {
            return new TFileMessageReader<>(new File(mFile), serializer, descriptor);
        }
    }
}
