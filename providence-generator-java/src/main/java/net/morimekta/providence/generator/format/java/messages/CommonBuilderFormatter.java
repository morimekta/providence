/*
 * Copyright 2016 Providence Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.morimekta.providence.generator.format.java.messages;

import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.BaseMessageFormatter;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.List;
import java.util.Optional;

import static net.morimekta.providence.generator.format.java.messages.CoreOverridesFormatter.UNION_FIELD;

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
        writer.formatln("extends %s<%s,_Field>",
                        PMessageBuilder.class.getName(),
                        message.instanceType());
    }

    private void appendMutate(JMessage message) {
        writer.appendln(JAnnotation.NON_NULL)
              .appendln("@Override")
              .appendln("public _Builder mutate() {")
              .begin()
              .appendln("return new _Builder(this);")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendStaticMakeBuilder(JMessage message) {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.commentRaw("Make a <code>" + message.descriptor().getQualifiedName() + "</code> builder.")
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
            writer.formatln("%s = builder.%s;",
                            UNION_FIELD, UNION_FIELD)
                  .newline();

            for (JField field : message.declaredOrderFields()) {
                switch (field.type()) {
                    case VOID:
                        // Void fields have no value.
                        break;
                    case LIST:
                    case MAP:
                    case SET:
                        writer.formatln(
                                "%s = %s == _Field.%s ? %s : null;",
                                field.member(),
                                UNION_FIELD,
                                field.fieldEnum(),
                                field.fieldInstanceCopy("builder." + field.member()),
                                field.member());
                        break;
                    case MESSAGE:
                        writer.formatln("%s = %s != _Field.%s", field.member(), UNION_FIELD, field.fieldEnum())
                              .appendln("        ? null")
                              .formatln("        : builder.%s_builder != null ? builder.%s_builder.build() : builder.%s;",
                                        field.member(), field.member(), field.member());
                        break;
                    default:
                        if (field.alwaysPresent()) {
                            writer.formatln("%s = %s == _Field.%s ? builder.%s : %s;",
                                            field.member(),
                                            UNION_FIELD,
                                            field.fieldEnum(),
                                            field.member(),
                                            field.kDefault());
                        } else {
                            writer.formatln("%s = %s == _Field.%s ? builder.%s : null;",
                                            field.member(),
                                            UNION_FIELD,
                                            field.fieldEnum(),
                                            field.member());
                        }
                        break;
                }
            }
        } else {
            if (message.isException()) {
                // If an exception class contains a field named 'message' (no caps), it will use that
                // as the exception message unmodified.
                Optional<JField> msg = message.exceptionMessageField();
                if (msg.isPresent()) {
                    writer.formatln("super(builder.%s);", msg.get().member())
                          .newline();
                } else {
                    writer.appendln("super(createMessage(")
                          .begin("                    ");
                    boolean first = true;
                    for (JField field : message.declaredOrderFields()) {
                        if (first) {
                            first = false;
                        } else {
                            writer.append(',')
                                  .appendln();
                        }
                        if (field.container()) {
                            writer.format("builder.%s() ? %s : null", field.isSet(), field.fieldInstanceCopy("builder." + field.member()));
                        } else if (!field.isVoid()) {
                            // Void fields have no value.
                            writer.format("builder.%s", field.member());
                        }
                    }
                    writer.append("));")
                          .end()
                          .newline();
                }
            }

            for (JField field : message.declaredOrderFields()) {
                if (field.container()) {
                    writer.formatln("if (builder.%s()) {", field.isSet())
                          .formatln("    %s = %s;", field.member(), field.fieldInstanceCopy("builder." + field.member()))
                          .appendln("} else {");
                    if (field.alwaysPresent()) {
                        writer.formatln("    %s = %s;", field.member(), field.kDefault());
                    } else {
                        writer.formatln("    %s = null;", field.member());
                    }
                    writer.appendln('}');
                } else if (field.type() == PType.MESSAGE) {
                    writer.formatln("%s = builder.%s_builder != null ? builder.%s_builder.build() : builder.%s;",
                                    field.member(), field.member(), field.member(), field.member());
                } else if (!field.isVoid()){
                    // Void fields have no value.
                    // And primitive java values keep the default state in the builder, so no need to
                    // explicitly set it here.
                    if (field.alwaysPresent() && !field.isPrimitiveJavaValue()) {
                        writer.formatln("if (builder.%s()) {", field.isSet())
                              .formatln("    %s = builder.%s;", field.member(), field.member())
                              .appendln("} else {")
                              .formatln("    %s = %s;", field.member(), field.kDefault())
                              .appendln('}');
                    } else {
                        writer.formatln("%s = builder.%s;", field.member(), field.member());
                    }
                }
            }
        }
        writer.end()
              .appendln('}')
              .newline();
    }
}
