package net.morimekta.providence.tools.common.options;

import net.morimekta.console.args.ArgumentException;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.FastBinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.thrift.TBinaryProtocolSerializer;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;

/**
 * Input and output formats available to the converter.
 */
public enum Format {
    // PSerializer
    json("Readable JSON with numeric field IDs and enums."),
    named_json("Compact JSON with names fields and enums."),
    pretty_json("Prettified named json output (multiline)."),
    binary("Binary serialization."),
    unversioned_binary("Binary serialization without version spec (deprecated)."),
    fast_binary("Fast binary protocol based on proto format."),
    pretty("Debug format that allows comments with an easy to read syntax."),
    pretty_compact("Compact format similar to toString of messages."),
    config("As a complete config (see providence-config)"),

    // TProtocolSerializer
    json_protocol("TJsonProtocol"),
    binary_protocol("TBinaryProtocol"),
    compact_protocol("TCompactProtocol"),
    tuple_protocol("TTupleProtocol");

    public final String desc;

    Format(String desc) {
        this.desc = desc;
    }

    public static Format forName(String name) {
        for (Format fmt : values()) {
            if (fmt.name().equalsIgnoreCase(name)) {
                return fmt;
            }
        }
        throw new ArgumentException("No such format " + name);
    }

    public Serializer createSerializer(boolean strict) {
        switch (this) {
            case binary:
                return new BinarySerializer(strict, true);
            case unversioned_binary:
                return new BinarySerializer(strict, false);
            case json:
                return new JsonSerializer(strict);
            case named_json:
                return new JsonSerializer(strict).named();
            case pretty_json:
                return new JsonSerializer(strict).pretty();
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
                return new PrettySerializer(strict);
            case config:
                return new PrettySerializer(strict).config();
            case pretty_compact:
                return new PrettySerializer(strict).compact();
            default:
                throw new IllegalStateException("Unknown format: " + this);
        }
    }
}
