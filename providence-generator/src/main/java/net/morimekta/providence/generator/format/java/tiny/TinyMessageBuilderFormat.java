package net.morimekta.providence.generator.format.java.tiny;

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.generator.format.java.utils.JOptions;
import net.morimekta.util.LinkedHashMapBuilder;
import net.morimekta.util.LinkedHashSetBuilder;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

import java.util.BitSet;
import java.util.Collection;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class TinyMessageBuilderFormat {
    private final JOptions options;
    private final IndentedPrintWriter writer;
    private final JHelper             helper;

    public TinyMessageBuilderFormat(IndentedPrintWriter writer, JHelper helper, JOptions options) {
        this.writer = writer;
        this.helper = helper;
        this.options = options;
    }

    public void appendBuilder(JMessage<?> message) throws GeneratorException {
        appendMutators();

        if (options.jackson) {
            writer.appendln("@com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = \"set\")");
        }

        writer.appendln("public static class _Builder {")
              .begin();

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

        if (message.isException()) {
            appendCreateMessage(message);
        }

        writer.formatln("public %s build() {", message.instanceType())
              .begin()
              .formatln("return new %s(this);", message.instanceType())
              .end()
              .appendln('}');

        writer.end()
              .appendln('}');
    }

    private void appendMutators() {
        writer.appendln("public _Builder mutate() {")
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
            switch (field.type()) {
                case MAP: {
                    PMap<?,?> mType = (PMap<?,?>) field.getPField().getDescriptor();
                    switch (JAnnotation.containerType(field)) {
                        case ORDERED:
                            writer.formatln("private %s<%s,%s> %s;",
                                            LinkedHashMapBuilder.class.getName().replaceAll("[$]", "."),
                                            helper.getFieldType(mType.keyDescriptor()),
                                            helper.getFieldType(mType.itemDescriptor()),
                                            field.member());
                            break;
                        case SORTED:
                            writer.formatln("private %s<%s,%s> %s;",
                                            ImmutableSortedMap.Builder.class.getName().replaceAll("[$]", "."),
                                            helper.getFieldType(mType.keyDescriptor()),
                                            helper.getFieldType(mType.itemDescriptor()),
                                            field.member());
                            break;
                        case DEFAULT:
                            writer.formatln("private %s<%s,%s> %s;",
                                            ImmutableMap.Builder.class.getName().replaceAll("[$]", "."),
                                            helper.getFieldType(mType.keyDescriptor()),
                                            helper.getFieldType(mType.itemDescriptor()),
                                            field.member());
                            break;
                    }
                    break;
                }
                case SET: {
                    PSet<?> sType = (PSet<?>) field.getPField().getDescriptor();
                    switch (JAnnotation.containerType(field)) {
                        case ORDERED:
                            writer.formatln("private %s<%s> %s;",
                                            LinkedHashSetBuilder.class.getName().replaceAll("[$]", "."),
                                            helper.getFieldType(sType.itemDescriptor()),
                                            field.member());
                            break;
                        case SORTED:
                            writer.formatln("private %s<%s> %s;",
                                            ImmutableSortedSet.Builder.class.getName().replaceAll("[$]", "."),
                                            helper.getFieldType(sType.itemDescriptor()),
                                            field.member());
                            break;
                        case DEFAULT:
                            writer.formatln("private %s<%s> %s;",
                                            ImmutableSet.Builder.class.getName().replaceAll("[$]", "."),
                                            helper.getFieldType(sType.itemDescriptor()),
                                            field.member());
                            break;
                    }
                    break;
                }
                case LIST: {
                    PList<?> lType = (PList<?>) field.getPField().getDescriptor();
                    writer.formatln("private %s<%s> %s;",
                                    ImmutableList.Builder.class.getName().replaceAll("[$]", "."),
                                    helper.getFieldType(lType.itemDescriptor()),
                                    field.member());
                    break;
                }
                default:
                    writer.formatln("private %s %s;", field.builderFieldType(), field.member());
                    break;
            }
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
            switch (field.type()) {
                case MAP: {
                    switch (JAnnotation.containerType(field)) {
                        case ORDERED:
                            writer.formatln("%s = new %s<>();",
                                            field.member(),
                                            LinkedHashMapBuilder.class.getName().replaceAll("[$]", "."));
                            break;
                        case SORTED:
                            writer.formatln("%s = %s.naturalOrder();",
                                            field.member(),
                                            ImmutableSortedMap.class.getName().replaceAll("[$]", "."));
                            break;
                        case DEFAULT:
                            writer.formatln("%s = %s.builder();",
                                            field.member(),
                                            ImmutableMap.class.getName().replaceAll("[$]", "."));
                            break;
                    }
                    break;
                }
                case SET: {
                    switch (JAnnotation.containerType(field)) {
                        case ORDERED:
                            writer.formatln("%s = new %s<>();",
                                            field.member(),
                                            LinkedHashSetBuilder.class.getName().replaceAll("[$]", "."));
                            break;
                        case SORTED:
                            writer.formatln("%s = %s.builder();",
                                            field.member(),
                                            ImmutableSortedSet.class.getName().replaceAll("[$]", "."));
                            break;
                        case DEFAULT:
                            writer.formatln("%s = %s.builder();",
                                            field.member(),
                                            ImmutableSet.class.getName().replaceAll("[$]", "."));
                            break;
                    }
                    break;
                }
                case LIST: {
                    writer.formatln("%s = %s.builder();",
                                    field.member(),
                                    ImmutableList.class.getName().replaceAll("[$]", "."));
                    break;
                }
                default:
                    if (field.hasDefault()) {
                        writer.formatln("%s = %s;", field.member(), field.kDefault());
                    }
                    break;

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
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        if (field.hasComment()) {
            comment.comment(field.comment())
                   .newline();
        }
        comment.param_("value", "The new value")
               .return_("The builder")
               .finish();
        if (JAnnotation.isDeprecated(field)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }
        if (options.jackson) {
            writer.formatln("@com.fasterxml.jackson.annotation.JsonProperty(\"%s\") ", field.name());
            if (field.binary()) {
                writer.appendln("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = net.morimekta.providence.jackson.BinaryJsonDeserializer.class) ");
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
            case MAP: {
                switch (JAnnotation.containerType(field)) {
                    case ORDERED:
                        writer.formatln("%s = new %s<>();",
                                        field.member(),
                                        LinkedHashMapBuilder.class.getName()
                                                                  .replaceAll("[$]", "."));
                        break;
                    case SORTED:
                        writer.formatln("%s = %s.naturalOrder();",
                                        field.member(),
                                        ImmutableSortedMap.class.getName()
                                                                .replaceAll("[$]", "."));
                        break;
                    case DEFAULT:
                        writer.formatln("%s = %s.builder();",
                                        field.member(),
                                        ImmutableMap.class.getName()
                                                          .replaceAll("[$]", "."));
                        break;
                }
                break;
            }
            case SET: {
                switch (JAnnotation.containerType(field)) {
                    case ORDERED:
                        writer.formatln("%s = new %s<>();",
                                        field.member(),
                                        LinkedHashSetBuilder.class.getName()
                                                                  .replaceAll("[$]", "."));
                        break;
                    case SORTED:
                        writer.formatln("%s = %s.builder();",
                                        field.member(),
                                        ImmutableSortedSet.class.getName()
                                                                .replaceAll("[$]", "."));
                        break;
                    case DEFAULT:
                        writer.formatln("%s = %s.builder();",
                                        field.member(),
                                        ImmutableSet.class.getName()
                                                          .replaceAll("[$]", "."));
                        break;
                }
                break;
            }
            case LIST: {
                writer.formatln("%s = %s.builder();",
                                field.member(),
                                ImmutableList.class.getName()
                                                   .replaceAll("[$]", "."));
                break;
            }
            default:
                break;
        }

        switch (field.type()) {
            case SET:
            case LIST:
                writer.formatln("%s.addAll(value);", field.member());
                break;
            case MAP:
                writer.formatln("%s.putAll(value);", field.member());
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
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        if (field.hasComment()) {
            comment.comment(field.comment())
                   .newline();
        }
        if (field.type() == PType.MAP) {
            comment.param_("key", "The inserted key")
                   .param_("value", "The inserted value");
        } else {
            comment.param_("values", "The added value");
        }
        comment.return_("The builder")
               .finish();

        if (JAnnotation.isDeprecated(field)) {
            writer.appendln(JAnnotation.DEPRECATED);
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

        switch (field.type()) {
            case MAP: {
                switch (JAnnotation.containerType(field)) {
                    case ORDERED:
                        writer.formatln("%s = new %s<>();",
                                        field.member(),
                                        LinkedHashMapBuilder.class.getName()
                                                                  .replaceAll("[$]", "."));
                        break;
                    case SORTED:
                        writer.formatln("%s = %s.naturalOrder();",
                                        field.member(),
                                        ImmutableSortedMap.class.getName()
                                                                .replaceAll("[$]", "."));
                        break;
                    case DEFAULT:
                        writer.formatln("%s = %s.builder();",
                                        field.member(),
                                        ImmutableMap.class.getName()
                                                          .replaceAll("[$]", "."));
                        break;
                }
                break;
            }
            case SET: {
                switch (JAnnotation.containerType(field)) {
                    case ORDERED:
                        writer.formatln("%s = new %s<>();",
                                        field.member(),
                                        LinkedHashSetBuilder.class.getName()
                                                                  .replaceAll("[$]", "."));
                        break;
                    case SORTED:
                        writer.formatln("%s = %s.builder();",
                                        field.member(),
                                        ImmutableSortedSet.class.getName()
                                                                .replaceAll("[$]", "."));
                        break;
                    case DEFAULT:
                        writer.formatln("%s = %s.builder();",
                                        field.member(),
                                        ImmutableSet.class.getName()
                                                          .replaceAll("[$]", "."));
                        break;
                }
                break;
            }
            case LIST: {
                writer.formatln("%s = %s.builder();",
                                field.member(),
                                ImmutableList.class.getName()
                                                   .replaceAll("[$]", "."));
                break;
            }
            default:
                if (field.hasDefault()) {
                    writer.formatln("%s = %s;", field.member(), field.kDefault());
                } else {
                    writer.formatln("%s = null;", field.member());
                }
                break;

        }

        writer.appendln("return this;")
              .end()
              .appendln('}');
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
