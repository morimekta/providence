package net.morimekta.providence.compiler.format.java2;

import net.morimekta.providence.util.io.IndentedPrintWriter;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class JMessageOverridesFormat {
    private final IndentedPrintWriter writer;
    private final JOptions            options;
    private final JHelper             helper;

    public JMessageOverridesFormat(IndentedPrintWriter writer, JOptions options, JHelper helper) {
        this.writer = writer;
        this.options = options;
        this.helper = helper;
    }

    public void appendOverrides(JMessage message) {
        // PStructDescriptor

        appendPresence(message);
        appendCounter(message);
        appendGetter(message);

        appendIsCompact(message);
        appendIsSimple();

        // Object

        appendEquals(message);
        appendHashCode(message);
        appendToString(message);
        appendAsString(message);
    }

    private void appendIsCompact(JMessage message) {
        if (options.jackson) {
            writer.appendln("@JsonIgnore");
        }
        writer.appendln("@Override")
              .appendln("public boolean isCompact() {")
              .begin();

        if (message.descriptor().isCompactible()) {
            writer.appendln("boolean missing = false;");

            boolean hasCheck = false;
            for (JField field : message.fields()) {
                if (!field.alwaysPresent()) {
                    hasCheck = true;
                    writer.formatln("if (%s()) {", field.presence())
                          .appendln("    if (missing) return false;")
                          .appendln("} else {")
                          .appendln("    missing = true;")
                          .appendln('}');
                } else if (hasCheck) {
                    writer.appendln("if (missing) return false;");
                    hasCheck = false;
                }
            }

            writer.appendln("return true;");
        } else {
            writer.appendln("return false;");
        }
        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendIsSimple() {
        if (options.jackson) {
            writer.appendln("@JsonIgnore");
        }
        writer.appendln("@Override")
              .appendln("public boolean isSimple() {")
              .begin()
              .appendln("return descriptor().isSimple();")
              .end()
              .appendln('}')
              .newline();

    }

    private void appendGetter(JMessage message) {
        writer.appendln("@Override")
              .appendln("public Object get(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (JField field : message.fields()) {
            writer.formatln("case %d: return %s();",
                            field.id(), field.getter());
        }

        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendPresence(JMessage message) {
        writer.appendln("@Override")
              .appendln("public boolean has(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (JField field : message.fields()) {
            if (field.container()) {
                writer.formatln("case %d: return %s() > 0;",
                                field.id(), field.counter());
            } else if (field.alwaysPresent()) {
                writer.formatln("case %d: return true;",
                                field.id());
            } else {
                writer.formatln("case %d: return %s();",
                                field.id(), field.presence());
            }
        }

        writer.appendln("default: return false;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendCounter(JMessage message) {
        writer.appendln("@Override")
              .appendln("public int num(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (JField field : message.fields()) {
            if (field.container()) {
                writer.formatln("case %d: return %s();",
                                field.id(), field.counter());
            } else if (field.alwaysPresent()) {
                writer.formatln("case %d: return 1;", field.id());
            } else {
                writer.formatln("case %d: return %s() ? 1 : 0;",
                                field.id(), field.presence());
            }
        }

        writer.appendln("default: return 0;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendEquals(JMessage message) {
        writer.appendln("@Override")
              .appendln("public boolean equals(Object o) {")
              .begin()
              .formatln("if (o == null || !(o instanceof %s)) return false;", message.instanceType());
        if (message.fields().size() > 0) {
            boolean first = true;
            writer.formatln("%s other = (%s) o;", message.instanceType(), message.instanceType())
                  .appendln("return ");
            if (message.isUnion()) {
                writer.append("Objects.equals(tUnionField, other.tUnionField)");
                first = false;
            }
            for (JField field : message.fields()) {
                if (first)
                    first = false;
                else {
                    writer.append(" &&")
                          .appendln("       ");
                }
                if (field.container()) {
                    writer.format("PTypeUtils.equals(%s, other.%s)", field.member(), field.member());
                } else {
                    writer.format("Objects.equals(%s, other.%s)", field.member(), field.member());
                }
            }
            writer.append(';');
        } else {
            writer.appendln("return true;");
        }
        writer.end()
              .appendln("}")
              .newline();
    }

    private void appendHashCode(JMessage message) {
        writer.appendln("@Override")
              .appendln("public int hashCode() {")
              .begin()
              .formatln("return tHashCode;");
        writer.end()
              .appendln("}")
              .newline();
    }

    private void appendToString(JMessage message) {
        writer.appendln("@Override")
              .appendln("public String toString() {")
              .begin()
              .formatln("return \"%s\" + asString();", message.descriptor().getQualifiedName(null))
              .end()
              .appendln('}')
              .newline();
    }

    private void appendAsString(JMessage message) {
        writer.appendln("@Override")
              .appendln("public String asString() {")
              .begin()
              .appendln("StringBuilder out = new StringBuilder();")
              .appendln("out.append(\"{\");")
              .newline();

        if (message.isUnion()) {
            writer.appendln("switch (tUnionField) {")
                  .begin();

            for (JField field : message.fields()) {
                writer.formatln("case %s: {", field.fieldEnum())
                      .begin()
                      .formatln("out.append(\"%s:\");", field.name());

                switch (field.type()) {
                    case BOOL:
                        writer.formatln("out.append(%s);", field.member());
                        break;
                    case BYTE:
                        writer.formatln("out.append(%s);", field.member());
                        break;
                    case I16:
                        writer.formatln("out.append(%s);", field.member());
                        break;
                    case I32:
                        writer.formatln("out.append(%s);", field.member());
                        break;
                    case I64:
                        writer.formatln("out.append(%s);", field.member());
                        break;
                    case DOUBLE:
                        writer.formatln("out.append(PTypeUtils.toString(%s));", field.member());
                        break;
                    case STRING:
                        writer.formatln("out.append('\\\"').append(%s).append('\\\"');", field.member());
                        break;
                    case BINARY:
                        writer.formatln("out.append(\"b64(\").append(%s.toBase64()).append(')');", field.member());
                        break;
                    case ENUM:
                        writer.formatln("out.append(%s.getName());", field.member());
                        break;
                    case MESSAGE:
                        writer.formatln("out.append(%s.asString());", field.member());
                        break;
                    case SET:
                    case LIST:
                    case MAP:
                        writer.formatln("out.append(PTypeUtils.toString(%s));", field.member());
                        break;
                }

                writer.appendln("break;")
                      .end()
                      .appendln('}');
            }
            writer.end()
                  .appendln('}');
        } else {
            writer.appendln("boolean first = true;");

            boolean first = true;
            for (JField field : message.fields()) {
                if (field.container()) {
                    writer.formatln("if (%s() > 0) {", field.counter());
                } else {
                    writer.formatln("if (%s()) {", field.presence());
                }
                writer.begin();
                if (first) {
                    first = false;
                } else {
                    writer.appendln("if (!first) out.append(',');");
                }
                writer.appendln("first = false;")
                      .formatln("out.append(\"%s:\");", field.name());

                switch (field.type()) {
                    case BOOL:
                        writer.formatln("out.append(%s ? \"true\" : \"false\");", field.member());
                        break;
                    case BYTE:
                        writer.formatln("out.append(Byte.toString(%s));", field.member());
                        break;
                    case I16:
                        writer.formatln("out.append(Short.toString(%s));", field.member());
                        break;
                    case I32:
                        writer.formatln("out.append(Integer.toString(%s));", field.member());
                        break;
                    case I64:
                        writer.formatln("out.append(Long.toString(%s));", field.member());
                        break;
                    case DOUBLE:
                        writer.formatln("out.append(PTypeUtils.toString(%s));", field.member());
                        break;
                    case STRING:
                        writer.formatln("out.append('\\\"').append(%s).append('\\\"');", field.member());
                        break;
                    case BINARY:
                        writer.formatln("out.append(\"hex(\").append(%s.toHexString()).append(')');", field.member());
                        break;
                    case ENUM:
                        writer.formatln("out.append(%s.getName());", field.member());
                        break;
                    case MESSAGE:
                        writer.formatln("out.append(%s.asString());", field.member());
                        break;
                    case SET:
                    case LIST:
                    case MAP:
                        writer.formatln("out.append(PTypeUtils.toString(%s));", field.member());
                        break;
                }

                writer.end()
                      .appendln('}');
            }
        }

        writer.appendln("out.append('}');")
              .appendln("return out.toString();")
              .end()
              .appendln("}")
              .newline();
    }
}
