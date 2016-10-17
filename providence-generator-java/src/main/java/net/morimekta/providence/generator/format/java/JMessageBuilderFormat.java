package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class JMessageBuilderFormat {
    private final IndentedPrintWriter writer;
    private final JHelper             helper;

    public JMessageBuilderFormat(IndentedPrintWriter writer, JHelper helper, JOptions options) {
        this.writer = writer;
        this.helper = helper;
    }

    public void appendBuilder(JMessage<?> message) throws GeneratorException {
        appendMutators(message);

        if (JAnnotation.isDeprecated(message.descriptor())) {
            writer.appendln(JAnnotation.DEPRECATED);
        }

        writer.appendln("public static class _Builder")
              .begin()
              .formatln("    extends %s<%s,_Field> {",
                        PMessageBuilder.class.getName(),
                        message.instanceType());

        appendFields(message);

        appendDefaultConstructor(message);
        appendMutateConstructor(message);

        appendMerge(message);

        for (JField field : message.fields()) {
            appendSetter(message, field);
            if (field.container()) {
                appendAdder(message, field);
            }
            appendIsSet(message, field);
            appendResetter(message, field);
            appendMutableGetters(message, field);
        }

        appendOverrideMutator(message);
        appendOverrideSetter(message);
        appendOverrideAdder(message);
        appendOverrideResetter(message);
        appendOverrideIsValid(message);
        appendOverrideValidate(message);
        appendOverrideDescriptor(message);

        writer.appendln("@Override")
              .formatln("public %s build() {", message.instanceType())
              .begin()
              .formatln("return new %s(this);", message.instanceType())
              .end()
              .appendln('}');

        writer.end()
              .appendln('}');
    }

    private void appendMutators(JMessage message) {
        writer.appendln("@Override")
              .appendln("public _Builder mutate() {")
              .begin()
              .appendln("return new _Builder(this);")
              .end()
              .appendln('}')
              .newline();

        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Make a " + message.descriptor().getQualifiedName(null) + " builder.")
               .return_("The builder instance.")
               .finish();
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
            if (field.type() == PType.MESSAGE) {
                writer.formatln("private %s._Builder %s_builder;", field.builderFieldType(), field.member());
            }
        }
        if (message.fields()
                   .size() > 0) {
            writer.newline();
        }
    }

    private void appendDefaultConstructor(JMessage<?> message) throws GeneratorException {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Make a " + message.descriptor().getQualifiedName(null) + " builder.")
               .finish();
        writer.appendln("public _Builder() {")
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
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Make a mutating builder off a base " + message.descriptor().getQualifiedName(null) + ".")
               .newline()
               .param_("base", "The base " + message.descriptor().getName())
               .finish();
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


    private void appendMerge(JMessage<?> message) throws GeneratorException {
        writer.appendln("@Override")
              .formatln("public _Builder merge(%s from) {", message.instanceType())
              .begin();

        if (message.isUnion()) {
            writer.appendln("if (from.unionField() == null) {")
                  .appendln("    return this;")
                  .appendln("}")
                  .newline()
                  .appendln("switch (from.unionField()) {")
                  .begin();

            for (JField field : message.fields()) {
                writer.formatln("case %s: {", field.fieldEnum())
                      .begin();

                switch (field.type()) {
                    case MESSAGE:
                        writer.formatln("if (tUnionField == _Field.%s && %s != null) {",
                                        field.fieldEnum(), field.member())
                              .formatln("    %s = %s.mutate().merge(from.%s()).build();",
                                        field.member(), field.member(), field.getter())
                              .appendln("} else {")
                              .formatln("    %s(from.%s());",
                                        field.setter(), field.getter())
                              .appendln('}');
                        break;
                    case SET:
                        writer.formatln("if (tUnionField == _Field.%s) {",
                                        field.fieldEnum())
                              .formatln("    %s.addAll(from.%s()):",
                                        field.member(), field.getter())
                              .appendln("} else {")
                              .formatln("    %s(from.%s());",
                                        field.setter(), field.getter())
                              .appendln('}');
                        break;
                    case MAP:
                        writer.formatln("if (tUnionField == _Field.%s) {",
                                        field.fieldEnum())
                              .formatln("    %s.putAll(from.%s()):",
                                        field.member(), field.getter())
                              .appendln("} else {")
                              .formatln("    %s(from.%s());",
                                        field.setter(), field.getter())
                              .appendln('}');
                        break;
                    default:
                        writer.formatln("%s(from.%s());", field.setter(), field.getter());
                        break;
                }

                writer.appendln("break;")
                      .end()
                      .appendln('}');
            }

            writer.end()
                  .appendln('}');
        } else {
            boolean first = true;
            for (JField field : message.fields()) {
                if (first) {
                    first = false;
                } else {
                    writer.newline();
                }

                if (!field.alwaysPresent()) {
                    writer.formatln("if (from.%s()) {", field.presence())
                          .begin();
                }

                writer.formatln("optionals.set(%d);", field.index());

                switch (field.type()) {
                    case MESSAGE:
                        // Message fields are merged.
                        writer.formatln("if (%s_builder != null) {", field.member())
                              .formatln("    %s_builder.merge(from.%s());",
                                        field.member(),
                                        field.getter())
                              .formatln("} else if (%s != null) {", field.member())
                              .formatln("    %s_builder = %s.mutate().merge(from.%s());",
                                        field.member(),
                                        field.member(),
                                        field.getter())
                              .formatln("    %s = null;", field.member())
                              .appendln("} else {")
                              .formatln("    %s = from.%s();", field.member(), field.getter())
                              .appendln('}');
                        break;
                    case SET:
                        writer.formatln("%s.addAll(from.%s());", field.member(), field.getter());
                        break;
                    case MAP:
                        writer.formatln("%s.putAll(from.%s());", field.member(), field.getter());
                        break;
                    case LIST:
                        writer.formatln("%s.clear();", field.member());
                        writer.formatln("%s.addAll(from.%s());", field.member(), field.getter());
                        break;
                    default:
                        writer.formatln("%s = from.%s();", field.member(), field.getter());
                        break;
                }

                if (!field.alwaysPresent()) {
                    writer.end()
                          .appendln('}');
                }
            }
        }

        writer.appendln("return this;")
              .end()
              .appendln('}')
              .newline();

    }

    private void appendSetter(JMessage message, JField field) throws GeneratorException {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Sets the value of " + field.name() + ".")
               .newline();
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
            case MESSAGE:
                writer.formatln("%s_builder = null;", field.member());
                // intentional overrun.
            default:
                writer.formatln("%s = value;", field.member());
                break;
        }

        writer.appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendAdder(JMessage message, JField field) throws GeneratorException {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        if (field.type() == PType.MAP) {
            comment.comment("Adds a mapping to " + field.name() + ".")
                   .newline();
        } else {
            comment.comment("Adds entries to " + field.name() + ".")
                   .newline();
        }

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
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Checks for presence of the " + field.name() + " field.")
               .newline();
        if (field.hasComment()) {
            comment.comment(field.comment())
                   .newline();
        }
        comment.return_(String.format("True iff %s has been set.", field.name()))
               .finish();
        writer.formatln("public boolean %s() {", field.isSet())
              .begin();

        if (message.isUnion()) {
            writer.formatln("return tUnionField == _Field.%s;", field.fieldEnum());
        } else {
            writer.formatln("return optionals.get(%d);", field.index());
        }
        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendResetter(JMessage message, JField field) {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Clears the " + field.name() + " field.")
               .newline();
        if (field.hasComment()) {
            comment.comment(field.comment())
                   .newline();
        }
        comment.return_("The builder")
               .finish();
        writer.formatln("public _Builder %s() {", field.resetter())
              .begin();

        if (message.isUnion()) {
            writer.formatln("if (tUnionField == _Field.%s) tUnionField = null;", field.fieldEnum());
        } else {
            writer.formatln("optionals.clear(%d);", field.index());
        }

        if (field.container()) {
            writer.formatln("%s.clear();", field.member());
        } else if (field.alwaysPresent()) {
            writer.formatln("%s = %s;", field.member(), field.kDefault());
        } else {
            writer.formatln("%s = null;", field.member());
            if (field.type() == PType.MESSAGE) {
                writer.formatln("%s_builder = null;", field.member());
            }
        }

        writer.appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }

    /**
     * Get mutable values. This returns message builders for messages, collection
     * builders for collections, and the normal immutable value for everything
     * else.
     *
     * @param message The message to get mutable getters for.
     * @param field The field to generate getter for.
     */
    private void appendMutableGetters(JMessage message, JField field) throws GeneratorException {
        if (field.getPField().getDescriptor() instanceof PPrimitive ||
            field.type() == PType.ENUM) {
            return;
        }

        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Gets the builder for the contained " + field.name() + ".")
               .newline();
        if (field.hasComment()) {
            comment.comment(field.comment())
                   .newline();
        }
        comment.return_("The field builder")
               .finish();
        if (JAnnotation.isDeprecated(field)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }
        switch (field.type()) {
            case MESSAGE: {
                writer.formatln("public %s._Builder %s() {", field.instanceType(),
                                Strings.camelCase("mutable", field.name()))
                      .begin();

                if (message.isUnion()) {
                    writer.formatln("if (tUnionField != _Field.%s) {", field.fieldEnum())
                          .formatln("    %s();", field.resetter())
                          .appendln('}')
                          .formatln("tUnionField = _Field.%s;", field.fieldEnum());
                } else {
                    writer.formatln("optionals.set(%d);", field.index());
                }

                writer.newline()
                      .formatln("if (%s != null) {", field.member())
                      .formatln("    %s_builder = %s.mutate();", field.member(), field.member())
                      .formatln("    %s = null;", field.member())
                      .formatln("} else if (%s_builder == null) {", field.member())
                      .formatln("    %s_builder = %s.builder();", field.member(), field.instanceType())
                      .appendln('}')
                      .formatln("return %s_builder;", field.member());

                writer.end()
                      .appendln('}')
                      .newline();
                break;
            }
            case SET:
            case LIST:
            case MAP:
                writer.formatln("public %s %s() {", field.builderFieldType(),
                                Strings.camelCase("mutable", field.name()))
                      .begin();

                if (message.isUnion()) {
                    writer.formatln("if (tUnionField != _Field.%s) {", field.fieldEnum())
                          .formatln("    %s();", field.resetter())
                          .appendln('}')
                          .formatln("tUnionField = _Field.%s;", field.fieldEnum());
                } else {
                    writer.formatln("optionals.set(%d);", field.index());
                }

                writer.formatln("return %s;", field.member());

                writer.end()
                      .appendln('}')
                      .newline();
                break;
            default:
                throw new GeneratorException("Unexpected field type: " + field.type());
        }
    }

    private void appendOverrideMutator(JMessage<?> message) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("@SuppressWarnings(\"unchecked\")")
              .formatln("public %s mutator(int key) {",
                        PMessageBuilder.class.getName())
              .begin()
              .appendln("switch (key) {")
              .begin();
        message.fields()
               .stream()
               .filter(field -> field.type() == PType.MESSAGE)
               .forEachOrdered(field -> writer.formatln("case %d: return %s();",
                                                        field.id(),
                                                        Strings.camelCase("mutable", field.name())));
        writer.appendln("default: throw new IllegalArgumentException(\"Not a message field ID: \" + key);")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendOverrideSetter(JMessage<?> message) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("@SuppressWarnings(\"unchecked\")")
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
            writer.appendln("if (tUnionField == null) {")
                  .appendln("    return false;")
                  .appendln('}')
                  .newline()
                  .appendln("switch (tUnionField) {")
                  .begin();
            message.fields()
                   .stream()
                   .filter(field -> !field.alwaysPresent())
                   .forEachOrdered(field -> {
                       if (field.type() == PType.MESSAGE) {
                           writer.formatln("case %s: return %s != null || %s_builder != null;",
                                           field.fieldEnum(),
                                           field.member(),
                                           field.member());
                       } else {
                           writer.formatln("case %s: return %s != null;", field.fieldEnum(), field.member());
                       }
                   });
            writer.appendln("default: return true;")
                  .end()
                  .appendln('}');
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

    private void appendOverrideValidate(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public void validate() {")
              .begin();
        if (message.isUnion()) {
            writer.appendln("if (!isValid()) {")
                  .formatln("    throw new %s(\"No union field set in %s\");",
                            IllegalStateException.class.getName(),
                            message.descriptor().getQualifiedName(null))
                  .appendln("}");
        } else {
            boolean hasRequired =
                    message.fields()
                           .stream()
                           .filter(JField::isRequired)
                           .findFirst()
                           .isPresent();
            if (hasRequired) {
                writer.appendln("if (!isValid()) {")
                      .begin()
                      .formatln("%s<String> missing = new %s<>();",
                                LinkedList.class.getName(),
                                LinkedList.class.getName())
                      .newline();

                message.fields()
                       .stream()
                       .filter(JField::isRequired)
                       .forEachOrdered(field -> writer
                               .formatln("if (!optionals.get(%d)) {", field.index())
                               .formatln("    missing.add(\"%s\");", field.name())
                               .appendln("}")
                               .newline());

                writer.formatln("throw new %s(", IllegalStateException.class.getName())
                      .appendln("        \"Missing required fields \" +")
                      .appendln("        String.join(\",\", missing) +")
                      .formatln("        \" in message %s\");", message.descriptor().getQualifiedName(null))
                      .end()
                      .appendln("}");
            }
        }
        writer.end()
              .appendln('}')
              .newline();

    }

    private void appendOverrideDescriptor(JMessage<?> message) throws GeneratorException {
        String typeClass = message.getDescriptorClass();
        writer.appendln("@Override")
              .formatln("public %s<%s,_Field> descriptor() {", typeClass, message.instanceType())
              .begin()
              .appendln("return kDescriptor;")
              .end()
              .appendln('}')
              .newline();

    }
}
