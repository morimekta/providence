package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.BitSet;
import java.util.Collection;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class JMessageBuilderFormat {
    IndentedPrintWriter writer;
    JHelper             helper;

    public JMessageBuilderFormat(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    public void appendBuilder(JMessage<?> message) throws GeneratorException {
        appendMutators();

        writer.appendln("public static class _Builder")
              .begin()
              .formatln("    extends %s<%s> {",
                        PMessageBuilder.class.getName(),
                        message.instanceType());

        appendFields(message);

        appendDefaultConstructor(message);
        appendMutateConstructor(message);

        for (JField field : message.fields()) {
            appendSetter(message, field);
            if (field.container()) {
                appendAdder(message, field);
            }
            appendIsSet(message, field);
            appendResetter(message, field);
        }

        appendOverrideSetter(message);
        appendOverrideAdder(message);
        appendOverrideResetter(message);
        appendOverrideIsValid(message);

        if (message.isException()) {
            appendCreateMessage(message);
        }

        writer.appendln("@Override")
              .formatln("public %s build() {", message.instanceType())
              .begin()
              .formatln("return new %s(this);", message.instanceType())
              .end()
              .appendln('}');

        writer.end()
              .appendln('}');
    }

    private void appendMutators() {
        writer.appendln("@Override")
              .appendln("public _Builder mutate() {")
              .begin()
              .appendln("return new _Builder(this);")
              .end()
              .appendln('}')
              .newline();
        writer.formatln("public static _Builder builder() {")
              .begin()
              .appendln("return new _Builder();")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendFields(JMessage<?> message) throws GeneratorException {
        if (message.isUnion()) {
            writer.appendln("private _Field tUnionField;");
        } else {
            writer.formatln("private %s optionals;",
                            BitSet.class.getName());
        }
        writer.newline();
        for (JField field : message.fields()) {
            writer.formatln("private %s %s;", field.builderFieldType(), field.member());
        }
        if (message.fields()
                   .size() > 0) {
            writer.newline();
        }
    }

    private void appendDefaultConstructor(JMessage<?> message) throws GeneratorException {
        writer.newline()
              .appendln("public _Builder() {")
              .begin();
        if (!message.isUnion()) {
            writer.formatln("optionals = new %s(%d);",
                            BitSet.class.getName(),
                            message.fields()
                                   .size());
        }
        for (JField field : message.fields()) {
            if (field.container()) {
                writer.formatln("%s = new %s<>();", field.member(), field.builderInstanceType());
            } else if (field.alwaysPresent()) {
                writer.formatln("%s = %s;", field.member(), field.kDefault());
            }
        }
        writer.end()
              .appendln('}')
              .newline();

    }

    private void appendMutateConstructor(JMessage<?> message) {
        writer.formatln("public _Builder(%s base) {", message.instanceType())
              .begin()
              .appendln("this();")
              .newline();
        if (message.isUnion()) {
            writer.appendln("tUnionField = base.tUnionField;")
                  .newline();
        }
        for (JField field : message.fields()) {
            boolean checkPresence = message.isUnion() ? field.container() : !field.alwaysPresent();
            if (checkPresence) {
                if (field.container()) {
                    writer.formatln("if (base.%s() > 0) {", field.counter())
                          .begin();
                } else {
                    writer.formatln("if (base.%s()) {", field.presence())
                          .begin();
                }
            }
            if (!message.isUnion()) {
                writer.formatln("optionals.set(%d);", field.index());
            }
            switch (field.type()) {
                case LIST:
                case SET:
                    writer.formatln("%s.addAll(base.%s);", field.member(), field.member());
                    break;
                case MAP:
                    writer.formatln("%s.putAll(base.%s);", field.member(), field.member());
                    break;
                default:
                    writer.formatln("%s = base.%s;", field.member(), field.member());
                    break;
            }
            if (checkPresence) {
                writer.end()
                      .appendln('}');
            }
        }

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendSetter(JMessage message, JField field) throws GeneratorException {
        if (field.hasComment()) {
            JUtils.appendBlockComment(writer, field.comment());
            if (JAnnotation.isDeprecated(field)) {
                writer.appendln(JAnnotation.DEPRECATED);
            }
        }
        if (field.type() == PType.SET || field.type() == PType.LIST) {
            PContainer<?> cType = (PContainer<?>) field.getPField()
                                                             .getDescriptor();
            String iType = helper.getFieldType(cType.itemDescriptor());
            writer.formatln("public _Builder %s(%s<%s> value) {",
                            field.setter(), Collection.class.getName(), iType);
        } else {
            writer.formatln("public _Builder %s(%s value) {", field.setter(), field.valueType());
        }
        writer.begin();

        if (message.isUnion()) {
            writer.formatln("tUnionField = _Field.%s;", field.fieldEnum());
        } else {
            writer.formatln("optionals.set(%d);", field.index());
        }

        switch (field.type()) {
            case SET:
            case LIST:
                writer.formatln("%s.clear();", field.member())
                      .formatln("%s.addAll(value);", field.member());
                break;
            case MAP:
                writer.formatln("%s.clear();", field.member())
                      .formatln("%s.putAll(value);", field.member());
                break;
            default:
                writer.formatln("%s = value;", field.member());
                break;
        }

        writer.appendln("return this;")
              .end()
              .appendln('}');
    }

    private void appendAdder(JMessage message, JField field) throws GeneratorException {
        if (field.hasComment()) {
            JUtils.appendBlockComment(writer, field.comment());
            if (JAnnotation.isDeprecated(field)) {
                writer.appendln(JAnnotation.DEPRECATED);
            }
        }

        switch (field.type()) {
            case MAP: {
                PMap<?, ?> mType = (PMap<?, ?>) field.getPField()
                                                     .getDescriptor();
                String mkType = helper.getValueType(mType.keyDescriptor());
                String miType = helper.getValueType(mType.itemDescriptor());

                writer.formatln("public _Builder %s(%s key, %s value) {", field.adder(), mkType, miType)
                      .begin();
                if (message.isUnion()) {
                    writer.formatln("tUnionField = _Field.%s;", field.fieldEnum());
                } else {
                    writer.formatln("optionals.set(%d);", field.index());
                }
                writer.formatln("%s.put(key, value);", field.member())
                      .appendln("return this;")
                      .end()
                      .appendln('}')
                      .newline();

                break;
            }
            case SET:
            case LIST: {
                PContainer<?> lType = (PContainer<?>) field.getPField()
                                                                 .getDescriptor();
                String liType = helper.getValueType(lType.itemDescriptor());

                writer.formatln("public _Builder %s(%s... values) {", field.adder(), liType)
                      .begin();
                if (message.isUnion()) {
                    writer.formatln("tUnionField = _Field.%s;", field.fieldEnum());
                } else {
                    writer.formatln("optionals.set(%d);", field.index());
                }
                writer.formatln("for (%s item : values) {", liType)
                      .begin()
                      .formatln("%s.add(item);", field.member())
                      .end()
                      .appendln('}')
                      .appendln("return this;")
                      .end()
                      .appendln('}')
                      .newline();

                break;
            }
        }
    }

    private void appendIsSet(JMessage message, JField field) {
        writer.formatln("public boolean %s() {", field.isSet())
              .begin();

        if (message.isUnion()) {
            writer.formatln("return tUnionField == _Field.%s;", field.fieldEnum());
        } else {
            writer.formatln("return optionals.get(%d);", field.index());
        }
        writer.end()
              .appendln('}');
    }

    private void appendResetter(JMessage message, JField field) {
        writer.formatln("public _Builder %s() {", field.resetter())
              .begin();

        if (message.isUnion()) {
            writer.formatln("if (tUnionField == _Field.%s) tUnionField = null;", field.fieldEnum());
        } else {
            writer.formatln("optionals.set(%d, false);", field.index());
        }

        if (field.container()) {
            writer.formatln("%s.clear();", field.member());
        } else if (field.alwaysPresent()) {
            writer.formatln("%s = %s;", field.member(), field.kDefault());
        } else {
            writer.formatln("%s = null;", field.member());
        }

        writer.appendln("return this;")
              .end()
              .appendln('}');
    }

    private void appendOverrideSetter(JMessage<?> message) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("public _Builder set(int key, Object value) {")
              .begin()
              .appendln("if (value == null) return clear(key);")
              .appendln("switch (key) {")
              .begin();
        for (JField field : message.fields()) {
            writer.formatln("case %d: %s((%s) value); break;", field.id(), field.setter(), field.valueType());
        }
        writer.end()
              .appendln('}')
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendOverrideAdder(JMessage<?> message) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("public _Builder addTo(int key, Object value) {")
              .begin()
              .appendln("switch (key) {")
              .begin();
        for (JField field : message.fields()) {
            if (field.type() == PType.LIST || field.type() == PType.SET) {
                PContainer<?> ct = (PContainer<?>) field.getPField()
                                                              .getDescriptor();
                PDescriptor itype = ct.itemDescriptor();
                writer.formatln("case %d: %s((%s) value); break;",
                                field.id(),
                                field.adder(),
                                helper.getValueType(itype));
            }
        }
        writer.appendln("default: break;")
              .end()
              .appendln('}')
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendOverrideResetter(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public _Builder clear(int key) {")
              .begin()
              .appendln("switch (key) {")
              .begin();
        for (JField field : message.fields()) {
            writer.formatln("case %d: %s(); break;", field.id(), field.resetter());
        }
        writer.end()
              .appendln('}')
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendOverrideIsValid(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public boolean isValid() {")
              .begin();
        if (message.isUnion()) {
            writer.appendln("return tUnionField != null;");
        } else {
            writer.appendln("return ")
                  .begin("       ");
            boolean first = true;
            for (JField field : message.fields()) {
                if (field.isRequired()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(" &&")
                              .appendln("");
                    }
                    writer.format("optionals.get(%d)", field.index());
                }
            }
            if (first) {
                writer.append("true");
            }
            writer.end()  // alignment indent
                  .append(';');
        }
        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendCreateMessage(JMessage<?> message) {
        writer.appendln("protected String createMessage() {")
              .begin()
              .appendln("StringBuilder builder = new StringBuilder();")
              .appendln("builder.append('{');")
              .appendln("boolean first = true;");
        boolean alwaysAfter = false;
        for (JField field : message.fields()) {
            if (!field.alwaysPresent()) {
                if (field.container()) {
                    writer.formatln("if (%s.size() > 0) {", field.member());
                } else {
                    writer.formatln("if (%s != null) {", field.member());
                }
                writer.begin();
            }

            if (alwaysAfter) {
                writer.appendln("builder.append(',');");
            } else {
                writer.appendln("if (first) first = false;")
                      .appendln("else builder.append(',');");
            }

            writer.formatln("builder.append(\"%s:\")", field.name());
            switch (field.type()) {
                case BOOL:
                case I32:
                case I64:
                    writer.formatln("       .append(%s);", field.member());
                    break;
                case BYTE:
                case I16:
                    writer.formatln("       .append((int) %s);", field.member());
                    break;
                case DOUBLE:
                case MAP:
                case SET:
                case LIST:
                    writer.formatln("       .append(%s.asString(%s));",
                                    Strings.class.getName(),
                                    field.member());
                    break;
                case STRING:
                    writer.formatln("       .append(%s);", field.member());
                    break;
                case BINARY:
                    writer.appendln("       .append(\"b64(\")")
                          .formatln("       .append(%s.toBase64())", field.member())
                          .appendln("       .append(')');");
                    break;
                case MESSAGE:
                    writer.formatln("       .append(%s.asString());", field.member());
                    break;
                default:
                    writer.formatln("       .append(%s.toString());", field.member());
                    break;
            }

            if (!field.alwaysPresent()) {
                writer.end()
                      .appendln('}');
            } else {
                alwaysAfter = true;
            }
        }
        writer.appendln("builder.append('}');")
              .appendln("return builder.toString();")
              .end()
              .appendln('}')
              .newline();
    }
}
