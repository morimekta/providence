package net.morimekta.providence.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PSet;

/**
 * Reflective thrift IDL description.
 * <p/>
 * Comments are gathered before the start of the next statement.
 * <ul>
 *   <li> Line comments are accumulated with newline delimiter.
 *        Each line is individually trimmed. </li>
 *   <li> Block comments replace the entire comment.
 *        The first space after '*' on each line is ignored. </li>
 * <ul>
 */
@SuppressWarnings("unused")
public class Model_Constants {
    private Model_Constants() {}

    public static final Set<String> kReservedWords;
    static {
        LinkedHashSet builder = new LinkedHashSet<>();
        builder.add("struct");
        builder.add("exception");
        builder.add("private");
        builder.add("bool");
        builder.add("string");
        builder.add("const");
        builder.add("for");
        builder.add("i32");
        builder.add("do");
        builder.add("float");
        builder.add("while");
        builder.add("required");
        builder.add("long");
        builder.add("i16");
        builder.add("public");
        builder.add("protected");
        builder.add("else");
        builder.add("map");
        builder.add("class");
        builder.add("if");
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
        builder.add("int");
        builder.add("extends");
        builder.add("service");
        builder.add("binary");
        builder.add("short");
        builder.add("unsigned");
        kReservedWords = Collections.unmodifiableSet(builder);
    }

}
