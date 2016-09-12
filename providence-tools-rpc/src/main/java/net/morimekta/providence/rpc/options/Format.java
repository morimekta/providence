package net.morimekta.providence.rpc.options;

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

    // TProtocolSerializer
    json_protocol("TJsonProtocol"),
    binary_protocol("TBinaryProtocol"),
    compact_protocol("TCompactProtocol"),
    tuple_protocol("TTupleProtocol"),
    ;

    public String desc;

    Format(String desc) {
        this.desc = desc;
    }
}
