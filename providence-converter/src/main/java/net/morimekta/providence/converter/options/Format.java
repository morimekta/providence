package net.morimekta.providence.converter.options;

/**
 * Input and output formats available to the converter.
 */
public enum Format {
    // PSerializer
    json("Readable JSON with ID enums."),
    named_json("Compact JSON with all named entities."),
    pretty_json("Prettified named json output (multiline)."),
    binary("Compact binary_protocol serialization."),
    fast_binary("Fast binary protocol based on proto format"),

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
