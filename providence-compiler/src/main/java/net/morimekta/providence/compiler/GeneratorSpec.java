package net.morimekta.providence.compiler;

/**
 * Which providence generator to use.
 */
enum GeneratorSpec {
    java("Main java (1.7+) code generator."),

    // extras
    thrift("Re-generate thrift files with the same spec."),
    json("Create JSON specification files."),
    ;

    public final String desc;

    GeneratorSpec(String desc) {
        this.desc = desc;
    }
}
