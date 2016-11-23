package net.morimekta.providence.generator.format.java.messages;

import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.BaseMessageFormatter;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.List;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class CommonBuilderFormatter
        extends BaseMessageFormatter
        implements MessageMemberFormatter {
    public CommonBuilderFormatter(IndentedPrintWriter writer,
                                  JHelper helper,
                                  List<MessageMemberFormatter> formatters) {
        super(true, false, writer, helper, formatters);
    }

    @Override
    public String getClassName(JMessage<?> message) {
        return "_Builder";
    }

    @Override
    public void appendMethods(JMessage<?> message) throws GeneratorException {
        appendMutate(message);
    }

    @Override
    public void appendExtraProperties(JMessage<?> message) throws GeneratorException {
        appendStaticMakeBuilder(message);
        appendMessageClass(message.descriptor());
    }

    @Override
    public void appendConstructors(JMessage<?> message) throws GeneratorException {
        appendBuilderConstructors(message);
    }

    @Override
    protected void appendClassExtends(JMessage<?> message) throws GeneratorException {
        writer.formatln("    extends %s<%s,_Field>",
                        PMessageBuilder.class.getName(),
                        message.instanceType());
    }

    protected void appendMutate(JMessage message) {
        writer.appendln("@Override")
              .appendln("public _Builder mutate() {")
              .begin()
              .appendln("return new _Builder(this);")
              .end()
              .appendln('}')
              .newline();
    }

    protected void appendStaticMakeBuilder(JMessage message) {
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

    private void appendBuilderConstructors(JMessage<?> message) throws GeneratorException {
        writer.formatln("private %s(_Builder builder) {", message.instanceType())
              .begin();
        if (message.isUnion()) {
            writer.appendln("tUnionField = builder.tUnionField;")
                  .newline();

            for (JField field : message.declaredOrderFields()) {
                switch (field.type()) {
                    case VOID:
                        // Void fields have no value.
                        break;
                    case LIST:
                        writer.formatln(
                                "%s = tUnionField == _Field.%s ? builder.%s.build() : null;",
                                field.member(),
                                field.fieldEnum(),
                                field.member());
                        break;
                    case SET:
                        writer.formatln(
                                "%s = tUnionField == _Field.%s ? builder.%s.build() : null;",
                                field.member(),
                                field.fieldEnum(),
                                field.member());
                        break;
                    case MAP:
                        writer.formatln(
                                "%s = tUnionField == _Field.%s ? builder.%s.build() : null;",
                                field.member(),
                                field.fieldEnum(),
                                field.member());
                        break;
                    case MESSAGE:
                        writer.formatln("%s = tUnionField != _Field.%s", field.member(), field.fieldEnum())
                              .appendln("        ? null")
                              .formatln("        : builder.%s_builder != null ? builder.%s_builder.build() : builder.%s;",
                                        field.member(), field.member(), field.member());
                        break;
                    default:
                        if (field.alwaysPresent()) {
                            writer.formatln("%s = tUnionField == _Field.%s ? builder.%s : %s;",
                                            field.member(),
                                            field.fieldEnum(),
                                            field.member(),
                                            field.kDefault());
                        } else {
                            writer.formatln("%s = tUnionField == _Field.%s ? builder.%s : null;",
                                            field.member(),
                                            field.fieldEnum(),
                                            field.member());
                        }
                        break;
                }
            }
        } else {
            if (message.isException()) {
                writer.appendln("super(createMessage(")
                      .begin(   "                    ");
                boolean first = true;
                for (JField field : message.declaredOrderFields()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(',')
                              .appendln();
                    }
                    if (field.container()) {
                        writer.format("builder.%s() ? builder.%s.build() : null",
                                      field.isSet(), field.member());
                    } else if (!field.isVoid()) {
                        // Void fields have no value.
                        writer.format("builder.%s", field.member());
                    }
                }
                writer.append("));")
                      .end()
                      .newline();
            }

            for (JField field : message.declaredOrderFields()) {
                if (field.container()) {
                    writer.formatln("if (builder.%s()) {", field.isSet())
                          .formatln("    %s = builder.%s.build();", field.member(), field.member())
                          .appendln("} else {")
                          .formatln("    %s = null;", field.member())
                          .appendln('}');
                } else if (field.type() == PType.MESSAGE) {
                    writer.formatln("%s = builder.%s_builder != null ? builder.%s_builder.build() : builder.%s;",
                                    field.member(), field.member(), field.member(), field.member());
                } else if (!field.isVoid()){
                    // Void fields have no value.
                    writer.formatln("%s = builder.%s;", field.member(), field.member());
                }
            }
        }
        writer.end()
              .appendln('}')
              .newline();
    }
}
