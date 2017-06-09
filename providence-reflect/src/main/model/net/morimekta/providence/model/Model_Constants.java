package net.morimekta.providence.model;

import net.morimekta.providence.descriptor.PSet;

/**
 * Reflective thrift IDL description.
 * <p>
 * Comments are gathered before the start of the next statement.
 * <p>
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
        kThriftKeywords = new PSet.DefaultBuilder<String>()
                .add("struct")
                .add("exception")
                .add("bool")
                .add("string")
                .add("const")
                .add("i32")
                .add("required")
                .add("i16")
                .add("map")
                .add("include")
                .add("set")
                .add("void")
                .add("byte")
                .add("i64")
                .add("double")
                .add("optional")
                .add("union")
                .add("list")
                .add("throws")
                .add("typedef")
                .add("enum")
                .add("oneway")
                .add("i8")
                .add("extends")
                .add("service")
                .add("binary")
                .add("namespace")
                .build();
    }

    public static final java.util.Set<String> kReservedWords;
    static {
        kReservedWords = new PSet.DefaultBuilder<String>()
                .add("private")
                .add("byte")
                .add("for")
                .add("do")
                .add("float")
                .add("while")
                .add("int")
                .add("long")
                .add("public")
                .add("protected")
                .add("else")
                .add("short")
                .add("unsigned")
                .add("class")
                .add("if")
                .build();
    }
}