package net.morimekta.providence.compiler.format.java2;

import net.morimekta.providence.util.io.IndentedPrintWriter;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class JMessageOverridesFormat {
    IndentedPrintWriter writer;
    JHelper             helper;

    public JMessageOverridesFormat(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
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
    }

    private void appendIsCompact(JMessage message) {
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
            for (JField field : message.fields()) {
                if (first)
                    first = false;
                else {
                    writer.append(" &&")
                          .appendln("       ");
                }
                writer.format("PTypeUtils.equals(%s, other.%s)", field.member(), field.member());
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
              .formatln("return %s.class.hashCode()", message.instanceType())
              .begin("       ");
        for (JField field : message.fields()) {
            writer.append(" +")
                  .formatln("PTypeUtils.hashCode(_Field.%s, %s)", field.fieldEnum(), field.member());
        }
        writer.end()
              .append(";")
              .end()
              .appendln("}")
              .newline();
    }

    private void appendToString(JMessage message) {
        writer.appendln("@Override")
              .appendln("public String toString() {")
              .begin()
              .appendln("return descriptor().getQualifiedName(null) + PTypeUtils.toString(this);")
              .end()
              .appendln("}")
              .newline();
    }
}
