package net.morimekta.providence.generator;

/**
 * Which providence generator to use.
 */
public enum Language {
    java("Main java (1.8+) code generator."),
    tiny_java("Minimalistic java (1.7+) code generator."),
    json("Generates JSON specification files."),
    ;

    public final String desc;

    Language(String desc) {
        this.desc = desc;
    }
}
