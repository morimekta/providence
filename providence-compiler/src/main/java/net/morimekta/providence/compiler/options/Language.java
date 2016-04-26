package net.morimekta.providence.compiler.options;

/**
 * Which providence generator to use.
 */
public enum Language {
    java("Main java (1.7+) code generator."),

    // extras
    thrift("Re-generate thrift files with the same spec."),
    json("Create JSON specification files."),
    ;

    public final String desc;

    Language(String desc) {
        this.desc = desc;
    }
}
