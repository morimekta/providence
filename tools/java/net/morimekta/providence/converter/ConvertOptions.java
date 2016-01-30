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

package net.morimekta.providence.converter;

import net.morimekta.console.FormatString;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.mio.PFileMessageReader;
import net.morimekta.providence.mio.PFileMessageWriter;
import net.morimekta.providence.mio.PMessageReader;
import net.morimekta.providence.mio.PMessageWriter;
import net.morimekta.providence.mio.PRecordMessageReader;
import net.morimekta.providence.mio.PSequenceMessageReader;
import net.morimekta.providence.mio.PSequenceMessageWriter;
import net.morimekta.providence.mio.PShardedMessageReader;
import net.morimekta.providence.mio.utils.Sequence;
import net.morimekta.providence.mio.utils.Shard;
import net.morimekta.providence.mio.utils.ShardUtil;
import net.morimekta.providence.reflect.parser.MessageParser;
import net.morimekta.providence.reflect.parser.Parser;
import net.morimekta.providence.reflect.parser.ThriftParser;
import net.morimekta.providence.serializer.PBinarySerializer;
import net.morimekta.providence.serializer.PJsonSerializer;
import net.morimekta.providence.serializer.PSerializeException;
import net.morimekta.providence.serializer.PSerializer;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TSimpleJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;
import net.morimekta.providence.util.PPrettyPrinter;
import net.morimekta.util.io.CountingOutputStream;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.EnumOptionHandler;
import org.kohsuke.args4j.spi.Setter;

import java.io.File;
import java.io.FileNotFoundException;
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
        public FormatOptionHandler(CmdLineParser parser, OptionDef option, Setter<Format> setter) {
            super(parser, option, setter, Format.class);
        }

        @Override
        public String getDefaultMetaVariable() {
            return "[format]";
        }
    }

    protected enum Format {
        // PSerializer
        json("Readable JSON with ID enums."),
        simple("Compact JSON with all named entities."),
        compact("Compact JSON with all ID identities."),
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
            aliases = {"-I"},
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
            aliases = {"-h", "-?"},
            help = true,
            usage = "This help listing.")
    protected boolean mHelp;

    protected PSerializer getSerializer(CmdLineParser cli, Format format) throws CmdLineException {
        switch (format) {
            case binary:
                return new PBinarySerializer();
            case json:
                return new PJsonSerializer(PJsonSerializer.IdType.NAME, PJsonSerializer.IdType.ID);
            case compact:
                return new PJsonSerializer(PJsonSerializer.IdType.ID);
            case simple:
                return new PJsonSerializer(PJsonSerializer.IdType.NAME);
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
                return new PSerializer() {
                    @Override
                    public int serialize(OutputStream output, PMessage<?> message)
                            throws IOException, PSerializeException {
                        CountingOutputStream out = new CountingOutputStream(output);
                        out.write(new PPrettyPrinter().format(message)
                                                      .getBytes(StandardCharsets.UTF_8));
                        out.write('\n');
                        out.flush();
                        return out.getByteCount();
                    }

                    @Override
                    public <T> int serialize(OutputStream output, PDescriptor<T> descriptor, T value)
                            throws IOException, PSerializeException {
                        throw new IOException("Only able to print messages.");
                    }

                    @Override
                    public <T> T deserialize(InputStream input, PDescriptor<T> descriptor)
                            throws IOException, PSerializeException {
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

    public Parser getParser(CmdLineParser cli) throws CmdLineException {
        switch (mSyntax) {
            case thrift:
                return new ThriftParser();
            case json:
                return new MessageParser(new PJsonSerializer());
            default:
                throw new CmdLineException(cli, new FormatString("Unknown SLI syntax %s."), mSyntax.name());
        }
    }

    public <T extends PMessage<T>> PMessageWriter<T> getOutput(CmdLineParser cli) throws CmdLineException, IOException {
        final PSerializer serializer = getSerializer(cli, mOutFormat);
        if (mOut != null) {
            if (ShardUtil.shardedName(mOut)) {
                Shard shard = new Shard(mOut);
                return new PSequenceMessageWriter<>(shard.sequence(mShard), serializer);
            }
            if (ShardUtil.sequencedName(mOut)) {
                Sequence sequence = new Sequence(ShardUtil.sequencePrefix(mOut));
                return new PSequenceMessageWriter<>(sequence, serializer);
            }

            File file = new File(mOut);
            if (!file.exists()) {
                throw new CmdLineException(cli, new FormatString("No such input file %s"), mOut);
            }
            if (!file.isFile()) {
                throw new CmdLineException(cli, new FormatString("%s is not a file."), mOut);
            }
            byte[] sep = null;
            switch (mOutFormat) {
                case json:
                case simple:
                case compact:
                case pretty:
                    // out own JSON serializers.
                    sep = new byte[]{'\n'};
                    break;
                case json_protocol:
                case simple_json_protocol:
                    // and the original thrift JSON protocols.
                    sep = new byte[]{'\n'};
                    break;
            }
            return new PFileMessageWriter<>(file, serializer, sep);
        } else {
            return new PMessageWriter<T>() {
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
                public int write(PMessage message) throws IOException {
                    try {
                        return serializer.serialize(System.out, message);
                    } catch (PSerializeException tse) {
                        throw new IOException("Unable to serialize output.", tse);
                    }
                }
            };
        }
    }

    public PMessageReader<?> getInput(CmdLineParser cli, PStructDescriptor<?, ?> descriptor) throws CmdLineException {
        PSerializer serializer = getSerializer(cli, mInFormat);
        try {

            if (ShardUtil.shardedName(mFile)) {
                return new PShardedMessageReader<>(mFile, serializer, descriptor);
            } else if (ShardUtil.sequencedName(mFile)) {
                Sequence sequence = new Sequence(ShardUtil.sequencePrefix(mFile));
                return new PSequenceMessageReader<>(sequence, serializer, descriptor);
            } else if (PRecordMessageReader.hasFileMagic(new File(mFile))) {
                return new PRecordMessageReader<>(new File(mFile), serializer, descriptor);
            } else {
                return new PFileMessageReader<>(new File(mFile), serializer, descriptor);
            }
        } catch (FileNotFoundException e) {
            throw new CmdLineException(cli, new FormatString("Unable to stat file magic on " + mFile));
        }
    }
}
