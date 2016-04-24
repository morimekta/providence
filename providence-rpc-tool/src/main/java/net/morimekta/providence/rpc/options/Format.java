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
    versioned_binary("Binary serialization with version spec."),
    fast_binary("Fast binary protocol based on proto format."),

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
