package net.morimekta.providence.generator.format.java.messages;

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.BitSet;
import java.util.Collection;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class BuilderCommonMemberFormatter implements MessageMemberFormatter {
    protected final IndentedPrintWriter        writer;
    protected final JHelper                    helper;

    public BuilderCommonMemberFormatter(IndentedPrintWriter writer,
                                        JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    @Override
    public void appendClassAnnotations(JMessage<?> message) throws GeneratorException {
        if (JAnnotation.isDeprecated(message.descriptor())) {
            writer.appendln(JAnnotation.DEPRECATED);
        }
    }

    @Override
    public void appendConstructors(JMessage<?> message) throws GeneratorException {
        appendDefaultConstructor(message);
        appendMutateConstructor(message);
    }

    @Override
    public void appendFields(JMessage<?> message) throws GeneratorException {
        if (message.isUnion()) {
            appendUnionFields(message);
        } else {
            appendStructFields(message);
        }

        for (JField field : message.declaredOrderFields()) {
            // Void fields have no value.
            if (field.isVoid()) {
                continue;
            }
            writer.formatln("private %s %s;", field.builderFieldType(), field.member());
            if (field.type() == PType.MESSAGE) {
                writer.formatln("private %s._Builder %s_builder;", field.builderFieldType(), field.member());
            }
        }
        if (message.declaredOrderFields()
                   .size() > 0) {
            writer.newline();
        }
    }

    @Override
    public void appendMethods(JMessage<?> message) throws GeneratorException {
        for (JField field : message.declaredOrderFields()) {
            appendSetter(message, field);
            if (field.container()) {
                appendAdder(message, field);
            }
            appendIsSet(message, field);
            appendResetter(message, field);
            appendMutableGetters(message, field);
        }
    }

    @Override
    public void appendExtraProperties(JMessage<?> message) throws GeneratorException {
        appendBuilderBuild(message);
    }

    private void appendBuilderBuild(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public %s build() {", message.instanceType())
              .begin()
              .formatln("return new %s(this);", message.instanceType())
              .end()
              .appendln('}');
    }


    private void appendUnionFields(JMessage<?> message) {
        writer.appendln("private _Field tUnionField;")
              .newline();
    }

    private void appendStructFields(JMessage<?> message) {
        writer.formatln("private %s optionals;",
                        BitSet.class.getName())
              .newline();
    }

    private void appendDefaultConstructor(JMessage<?> message) throws GeneratorException {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Make a " + message.descriptor().getQualifiedName() + " builder.")
               .finish();
        writer.appendln("public _Builder() {")
              .begin();
        if (!message.isUnion()) {
            writer.formatln("optionals = new %s(%d);",
                            BitSet.class.getName(),
                            message.declaredOrderFields()
                                   .size());
        }
        for (JField field : message.declaredOrderFields()) {
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
        comment.comment("Make a mutating builder off a base " + message.descriptor().getQualifiedName() + ".")
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
        for (JField field : message.declaredOrderFields()) {
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
                case VOID:
                    // Void fields have no value.
                    break;
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
            comment.comment(field.comment());
        } else {
            comment.comment("Sets the value of " + field.name() + ".");
        }
        comment.newline()
               .param_("value", "The new value")
               .return_("The builder")
               .finish();
        if (JAnnotation.isDeprecated(field)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }
        if (field.isVoid()) {
            // Void fields have no value.
            writer.formatln("public _Builder %s() {", field.setter());
        } else if (field.type() == PType.SET || field.type() == PType.LIST) {
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
            case VOID:
                // Void fields have no value.
                break;
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

        if (field.hasComment()) {
            comment.comment(field.comment());
        } else if (field.type() == PType.MAP) {
            comment.comment("Adds a mapping to " + field.name() + ".");
        } else {
            comment.comment("Adds entries to " + field.name() + ".");
        }
        comment.newline();

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
        if (field.hasComment()) {
            comment.comment(field.comment());
        } else {
            comment.comment("Checks for presence of the " + field.name() + " field.");
        }

        comment.newline()
               .return_(String.format("True iff %s has been set.", field.name()))
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
        if (field.hasComment()) {
            comment.comment(field.comment());
        } else {
            comment.comment("Clears the " + field.name() + " field.");
        }
        comment.newline()
               .return_("The builder")
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
        } else if (field.isVoid()) {
            // Void fields have no value.
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
        if (field.hasComment()) {
            comment.comment(field.comment());
        } else {
            comment.comment("Gets the builder for the contained " + field.name() + ".");
        }
        comment.newline()
               .return_("The field builder")
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

}
