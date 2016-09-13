package net.morimekta.providence.tools.config.cmd;

import net.morimekta.console.args.Argument;
import net.morimekta.console.args.ArgumentException;
import net.morimekta.console.args.ArgumentParser;
import net.morimekta.console.args.Option;
import net.morimekta.console.util.Parser;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.config.ProvidenceConfig;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.FastBinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;
import net.morimekta.providence.tools.config.options.Format;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Print the resulting config.
 */
public class Print implements Command {
    private Serializer serializer = new PrettySerializer("  ", " ", "\n", "", true, false);
    private File       file       = null;

    @Override
    public void execute(ProvidenceConfig config) throws SerializerException {
        try {
            serializer.serialize(System.out, (PMessage) config.load(file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public ArgumentParser parser(ArgumentParser parent) {
        ArgumentParser parser = new ArgumentParser(parent.getProgram() + " print", parent.getVersion(), "");
        parser.add(new Option("--format", "f", "fmt", "the output format", this::setSerializer, "pretty"));
        parser.add(new Argument("file", "Config file to parse and print", Parser.file(this::setFile), null, null, false, true, false));
        return parser;
    }

    private void setFile(File file) {
        this.file = file;
    }

    private void setSerializer(String name) {
        Format format = Format.forName(name);
        if (format == null) {
            throw new ArgumentException("Unknown output format: " + name);
        }

        final boolean strict = false;
        switch (format) {
            case binary:
                serializer = new BinarySerializer(strict, true);
                break;
            case unversioned_binary:
                serializer = new BinarySerializer(strict, false);
                break;
            case json:
                serializer = new JsonSerializer(strict, JsonSerializer.IdType.ID);
                break;
            case named_json:
                serializer = new JsonSerializer(strict, JsonSerializer.IdType.NAME);
                break;
            case pretty_json:
                serializer = new JsonSerializer(strict, JsonSerializer.IdType.NAME, JsonSerializer.IdType.NAME, true);
                break;
            case fast_binary:
                serializer = new FastBinarySerializer(strict);
                break;
            case pretty:
                // default, no change.
                break;
            case binary_protocol:
                serializer = new TBinaryProtocolSerializer(strict);
                break;
            case json_protocol:
                serializer = new TJsonProtocolSerializer(strict);
                break;
            case compact_protocol:
                serializer = new TCompactProtocolSerializer(strict);
                break;
            case tuple_protocol:
                serializer = new TTupleProtocolSerializer(strict);
                break;
            default:
                throw new ArgumentException("Unhandled format option: " + format);
        }
    }
}
