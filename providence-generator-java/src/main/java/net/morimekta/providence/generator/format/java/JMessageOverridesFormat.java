package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.Objects;

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

        appendCompact(message);

        // Object

        appendEquals(message);
        appendHashCode(message);
        appendToString(message);
        appendAsString(message);
        appendCompareTo(message);
    }

    private void appendCompact(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public boolean compact() {")
              .begin();

        if (message.descriptor()
                   .isCompactible()) {
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

    private void appendGetter(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public Object get(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (JField field : message.fields()) {
            if (field.isVoid()) {
                // Void fields have no value.
                writer.formatln("case %d: return %s() ? Boolean.FALSE : null;",
                                field.id(), field.presence());
            } else {
                writer.formatln("case %d: return %s();", field.id(), field.getter());
            }
        }

        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendPresence(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public boolean has(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (JField field : message.fields()) {
            if (field.container()) {
                writer.formatln("case %d: return %s() > 0;", field.id(), field.counter());
            } else if (field.alwaysPresent() && !message.isUnion()) {
                writer.formatln("case %d: return true;", field.id());
            } else {
                writer.formatln("case %d: return %s();", field.id(), field.presence());
            }
        }

        writer.appendln("default: return false;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendCounter(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public int num(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (JField field : message.fields()) {
            if (field.container()) {
                writer.formatln("case %d: return %s();", field.id(), field.counter());
            } else if (field.alwaysPresent() && !message.isUnion()) {
                writer.formatln("case %d: return 1;", field.id());
            } else {
                writer.formatln("case %d: return %s() ? 1 : 0;", field.id(), field.presence());
            }
        }

        writer.appendln("default: return 0;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendEquals(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public boolean equals(Object o) {")
              .begin()
              .appendln("if (o == this) return true;")
              .formatln("if (o == null || !(o instanceof %s)) return false;", message.instanceType());
        if (message.fields()
                   .size() > 0) {
            boolean first = true;
            writer.formatln("%s other = (%s) o;", message.instanceType(), message.instanceType())
                  .appendln("return ");
            if (message.isUnion()) {
                writer.format("%s.equals(tUnionField, other.tUnionField)",
                              Objects.class.getName());
                first = false;
            }
            for (JField field : message.fields()) {
                // Void fields have no value.
                if (field.isVoid()) {
                    continue;
                }
                if (first) {
                    first = false;
                } else {
                    writer.append(" &&")
                          .appendln("       ");
                }
                if (field.container()) {
                    writer.format("%s.equals(%s, other.%s)",
                                  Objects.class.getName(),
                                  field.member(),
                                  field.member());
                } else {
                    writer.format("%s.equals(%s, other.%s)",
                                  Objects.class.getName(),
                                  field.member(),
                                  field.member());
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

    private void appendHashCode(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public int hashCode() {")
              .begin()
              .appendln("if (tHashCode == 0) {")
              .begin()
              .formatln("tHashCode = %s.hash(",
                        Objects.class.getName())
              .begin("        ")
              .formatln("%s.class", message.instanceType());
        for (JField field : message.fields()) {
            // Void fields have no value.
            if (field.isVoid()) {
                continue;
            }
            writer.append(",");
            writer.formatln("_Field.%s, %s", field.fieldEnum(), field.member());
        }

        writer.end()
              .append(");")
              .end()
              .appendln('}')
              .appendln("return tHashCode;")
              .end()
              .appendln("}")
              .newline();
    }

    private void appendToString(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public String toString() {")
              .begin()
              .formatln("return \"%s\" + asString();",
                        message.descriptor()
                               .getQualifiedName(null))
              .end()
              .appendln('}')
              .newline();
    }

    private void appendAsString(JMessage<?> message) {
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
                      .formatln("out.append(\"%s:\")", field.name());

                switch (field.type()) {
                    case VOID:
                        // Void fields have no value, and is always 'true' if present.
                        writer.appendln("   .append(\"true\");");
                        break;
                    case BOOL:
                    case I32:
                    case I64:
                        writer.formatln("   .append(%s);", field.member());
                        break;
                    case BYTE:
                    case I16:
                        writer.formatln("   .append((int) %s);", field.member());
                        break;
                    case DOUBLE:
                    case SET:
                    case LIST:
                    case MAP:
                        writer.formatln("   .append(%s.asString(%s));",
                                        Strings.class.getName(),
                                        field.member());
                        break;
                    case STRING:
                        writer.formatln("   .append('\\\"').append(%s.escape(%s)).append('\\\"');",
                                        Strings.class.getName(),
                                        field.member());
                        break;
                    case BINARY:
                        writer.formatln("   .append(\"b64(\").append(%s.toBase64()).append(')');", field.member());
                        break;
                    case MESSAGE:
                        writer.formatln("   .append(%s.asString());", field.member());
                        break;
                    default:
                        writer.formatln("   .append(%s.getName());", field.member());
                        break;
                }

                writer.appendln("break;")
                      .end()
                      .appendln('}');
            }
            writer.end()
                  .appendln('}');
        } else {
            boolean firstFirstCheck = true;
            boolean alwaysAfter = false;
            boolean last;
            boolean first;
            int i = 0;
            int lastPos = message.fields().size() - 1;
            for (JField field : message.fields()) {
                // Void fields have no value.
                if (field.isVoid()) {
                    lastPos--;
                    continue;
                }
                last  = i == lastPos;
                first = i == 0;
                ++i;

                if (!field.alwaysPresent()) {
                    if (!alwaysAfter && firstFirstCheck && !last) {
                        writer.appendln("boolean first = true;");
                    }
                    if (field.container()) {
                        writer.formatln("if (%s != null && %s.size() > 0) {", field.member(), field.member());
                    } else {
                        writer.formatln("if (%s != null) {", field.member());
                    }
                    writer.begin();
                }

                if (alwaysAfter) {
                    writer.appendln("out.append(',');");
                } else if (!field.alwaysPresent()) {
                    if (firstFirstCheck || first) {
                        if (!last) {
                            writer.appendln("first = false;");
                        }
                    } else if (last) {
                        writer.appendln("if (!first) out.append(',');");
                    } else {
                        writer.appendln("if (first) first = false;")
                              .appendln("else out.append(',');");
                    }
                }

                writer.formatln("out.append(\"%s:\")", field.name());
                switch (field.type()) {
                    case BOOL:
                    case I32:
                    case I64:
                        writer.formatln("   .append(%s);", field.member());
                        break;
                    case BYTE:
                    case I16:
                        writer.formatln("   .append((int) %s);", field.member());
                        break;
                    case DOUBLE:
                    case MAP:
                    case SET:
                    case LIST:
                        writer.formatln("   .append(%s.asString(%s));",
                                        Strings.class.getName(),
                                        field.member());
                        break;
                    case STRING:
                        writer.appendln("   .append('\\\"')")
                              .formatln("   .append(%s.escape(%s))",
                                        Strings.class.getName(),
                                        field.member())
                              .appendln("   .append('\\\"');");
                        break;
                    case BINARY:
                        writer.appendln("   .append(\"b64(\")")
                              .formatln("   .append(%s.toBase64())", field.member())
                              .appendln("   .append(')');");
                        break;
                    case MESSAGE:
                        writer.formatln("   .append(%s.asString());", field.member());
                        break;
                    default:
                        writer.formatln("   .append(%s.toString());", field.member());
                        break;
                }

                if (!field.alwaysPresent()) {
                    writer.end().appendln('}');
                    if (!alwaysAfter && firstFirstCheck) {
                        firstFirstCheck = false;
                    }
                } else {
                    alwaysAfter = true;
                }
            }
        }

        writer.appendln("out.append('}');")
              .appendln("return out.toString();")
              .end()
              .appendln("}")
              .newline();
    }

    private void appendCompareTo(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public int compareTo(%s other) {", message.instanceType())
              .begin();

        if (message.isUnion()) {
            writer.appendln("int c = Integer.compare(tUnionField.getKey(), other.tUnionField.getKey());")
                  .appendln("if (c != 0) return c;")
                  .newline()
                  .appendln("switch (tUnionField) {")
                  .begin();

            for (JField field : message.fields()) {
                writer.formatln("case %s:", field.fieldEnum())
                      .begin();

                switch (field.type()) {
                    case VOID:
                        // Void is always equal to void.
                        writer.appendln("return 0;");
                        break;
                    case BOOL:
                        writer.formatln("return Boolean.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case BYTE:
                        writer.formatln("return Byte.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case I16:
                        writer.formatln("return Short.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case I32:
                        writer.formatln("return Integer.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case I64:
                        writer.formatln("return Long.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case DOUBLE:
                        writer.formatln("return Double.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case STRING:
                    case BINARY:
                    case MESSAGE:
                        writer.formatln("return %s.compareTo(other.%s);", field.member(), field.member());
                        break;
                    case ENUM:
                        writer.formatln("return Integer.compare(%s.getValue(), other.%s.getValue());",
                                        field.member(),
                                        field.member());
                        break;
                    case SET:
                    case LIST:
                    case MAP:
                        // containers aren't really comparable, just make some consistent comparison.
                        writer.formatln("return Integer.compare(%s.hashCode(), other.%s.hashCode());",
                                        field.member(),
                                        field.member());
                        break;
                }

                writer.end();
            }
            writer.appendln("default: return 0;")
                  .end()
                  .appendln('}');
        } else {
            writer.appendln("int c;");

            for (JField field : message.fields()) {
                // Void fields have no value.
                if (field.isVoid()) {
                    continue;
                }

                writer.newline();

                if (!field.alwaysPresent()) {
                    writer.formatln("c = Boolean.compare(%s != null, other.%s != null);",
                                    field.member(),
                                    field.member())
                          .appendln("if (c != 0) return c;")
                          .formatln("if (%s != null) {", field.member())
                          .begin();
                }
                switch (field.type()) {
                    case BOOL:
                        writer.formatln("c = Boolean.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case BYTE:
                        writer.formatln("c = Byte.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case I16:
                        writer.formatln("c = Short.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case I32:
                        writer.formatln("c = Integer.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case I64:
                        writer.formatln("c = Long.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case DOUBLE:
                        writer.formatln("c = Double.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case STRING:
                    case BINARY:
                    case MESSAGE:
                        writer.formatln("c = %s.compareTo(other.%s);", field.member(), field.member());
                        break;
                    case ENUM:
                        writer.formatln("c = Integer.compare(%s.getValue(), %s.getValue());",
                                        field.member(),
                                        field.member());
                        break;
                    case SET:
                    case LIST:
                    case MAP:
                        writer.formatln("c = Integer.compare(%s.hashCode(), other.%s.hashCode());",
                                        field.member(),
                                        field.member());
                        break;
                }
                writer.appendln("if (c != 0) return c;");

                if (!field.alwaysPresent()) {
                    writer.end()
                          .appendln('}');
                }
            }
            writer.newline()
                  .appendln("return 0;");
        }

        writer.end()
              .appendln("}")
              .newline();
    }
}
