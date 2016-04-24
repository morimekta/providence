package net.morimekta.providence.model;

/**
 * Reflective thrift IDL description.
 * 
 * Comments are gathered before the start of the next statement.
 * 
 * - Line comments are accumulated with newline delimiter.
 *   Each line is individually trimmed.
 * - Block comments replace the entire comment.
 *   The first space after &#39;*&#39; on each line is ignored.
 */
@SuppressWarnings("unused")
public class Model_Constants {
    private Model_Constants() {}

    public static final java.util.Set<String> kThriftKeywords;
    static {
        net.morimekta.providence.descriptor.PSet.Builder builder = new net.morimekta.providence.descriptor.PSet.ImmutableSetBuilder<>();
        builder.add("struct");
        builder.add("exception");
        builder.add("bool");
        builder.add("string");
        builder.add("const");
        builder.add("i32");
        builder.add("required");
        builder.add("i16");
        builder.add("map");
        builder.add("include");
        builder.add("set");
        builder.add("void");
        builder.add("byte");
        builder.add("i64");
        builder.add("double");
        builder.add("optional");
        builder.add("union");
        builder.add("list");
        builder.add("throws");
        builder.add("typedef");
        builder.add("enum");
        builder.add("oneway");
        builder.add("i8");
        builder.add("extends");
        builder.add("service");
        builder.add("binary");
        builder.add("namespace");
        kThriftKeywords = builder.build();
    }

    public static final java.util.Set<String> kReservedWords;
    static {
        net.morimekta.providence.descriptor.PSet.Builder builder = new net.morimekta.providence.descriptor.PSet.ImmutableSetBuilder<>();
        builder.add("private");
        builder.add("byte");
        builder.add("for");
        builder.add("do");
        builder.add("float");
        builder.add("while");
        builder.add("int");
        builder.add("long");
        builder.add("public");
        builder.add("protected");
        builder.add("else");
        builder.add("short");
        builder.add("unsigned");
        builder.add("class");
        builder.add("if");
        kReservedWords = builder.build();
    }

}