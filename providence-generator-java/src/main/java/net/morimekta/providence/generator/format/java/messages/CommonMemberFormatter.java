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

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.format.java.JavaOptions;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.generator.format.java.utils.ValueBuilder;
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.providence.util.ThriftAnnotation;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import javax.annotation.Generated;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

import static net.morimekta.providence.generator.format.java.messages.CoreOverridesFormatter.UNION_FIELD;
import static net.morimekta.providence.generator.format.java.utils.JUtils.camelCase;

/**
 * Appends stuff that is common for all variants of providence message types.
 *
 * - field value constants.
 * - field value getters
 */
public class CommonMemberFormatter implements MessageMemberFormatter {
    protected final IndentedPrintWriter writer;
    private final JHelper               helper;
    private final JavaOptions           javaOptions;
    private final GeneratorOptions      generatorOptions;

    public CommonMemberFormatter(IndentedPrintWriter writer,
                                 JHelper helper,
                                 GeneratorOptions generatorOptions,
                                 JavaOptions javaOptions) {
        this.writer = writer;
        this.helper = helper;
        this.generatorOptions = generatorOptions;
        this.javaOptions = javaOptions;
    }

    @Override
    public void appendClassAnnotations(JMessage<?> message) throws GeneratorException {
        if (JAnnotation.isDeprecated((CAnnotatedDescriptor) message.descriptor())) {
            writer.appendln(JAnnotation.DEPRECATED);
        }
        writer.appendln("@SuppressWarnings(\"unused\")");
        if (javaOptions.generated_annotation_version) {
            writer.formatln("@%s(\"%s %s\")",
                            Generated.class.getName(),
                            generatorOptions.generator_program_name,
                            generatorOptions.program_version);
        } else {
            writer.formatln("@%s(\"%s\")",
                            Generated.class.getName(),
                            generatorOptions.generator_program_name);
        }
        writer.formatln("@%s", Immutable.class.getName());
    }

    @Override
    public void appendConstants(JMessage<?> message) throws GeneratorException {
        // Because of Serializable.
        writer.formatln("private final static long serialVersionUID = %dL;",
                        JUtils.generateSerialVersionUID(message.descriptor()))
              .newline();

        appendFieldDefaultValues(message);
    }

    @Override
    public void appendFields(JMessage<?> message) throws GeneratorException {
        appendFieldDeclarations(message);
    }

    @Override
    public void appendConstructors(JMessage<?> message) throws GeneratorException {
        appendCreateConstructor(message);
    }

    @Override
    public void appendMethods(JMessage<?> message) throws GeneratorException {
        appendFieldGetters(message);

        if (message.isException()) {
            appendCreateMessage(message);
        }
    }

    private void appendFieldDefaultValues(JMessage<?> message) throws GeneratorException {
        ValueBuilder values = new ValueBuilder(writer, helper);

        values.appendDefaultConstants(message.declaredOrderFields());
    }

    private void appendFieldGetters(JMessage<?> message) throws GeneratorException {
        for (JField field : message.declaredOrderFields()) {
            if (message.isUnion()) {
                if (field.container()) {
                    writer.formatln("public int %s() {", field.counter())
                          .formatln("    return %s == _Field.%s ? %s.size() : 0;",
                                    UNION_FIELD,
                                    field.fieldEnum(),
                                    field.member())
                          .appendln('}')
                          .newline();
                }
                if (field.alwaysPresent() || field.isVoid()) {
                    writer.formatln("public boolean %s() {", field.presence())
                          .formatln("    return %s == _Field.%s;",
                                    UNION_FIELD,
                                    field.fieldEnum())
                          .appendln('}')
                          .newline();
                } else {
                    writer.formatln("public boolean %s() {", field.presence())
                          .formatln("    return %s == _Field.%s && %s != null;",
                                    UNION_FIELD,
                                    field.fieldEnum(),
                                    field.member())
                          .appendln('}')
                          .newline();
                }
            } else {
                if (field.container()) {
                    writer.formatln("public int %s() {", field.counter())
                          .formatln("    return %s != null ? %s.size() : 0;", field.member(), field.member())
                          .appendln('}')
                          .newline();
                }
                if (field.alwaysPresent()) {
                    writer.formatln("public boolean %s() {", field.presence())
                          .begin()
                          .formatln("return true;")
                          .end()
                          .appendln('}')
                          .newline();
                } else {
                    writer.formatln("public boolean %s() {", field.presence())
                          .begin()
                          .formatln("return %s != null;", field.member())
                          .end()
                          .appendln('}')
                          .newline();
                }
            }

            if (field.isVoid()) {
                // Void fields have no value.
                continue;
            }

            BlockCommentBuilder comment = new BlockCommentBuilder(writer);
            if (field.hasComment()) {
                comment.comment(field.comment())
                       .newline();
            }
            comment.return_("The field value")
                   .finish();
            if (JAnnotation.isDeprecated(field)) {
                writer.appendln(JAnnotation.DEPRECATED);
            }
            if (field.alwaysPresent() && !field.isPrimitiveJavaValue()) {
                writer.appendln(JAnnotation.NON_NULL);
            }
            writer.formatln("public %s %s() {", field.valueType(), field.getter());
            if ((field.isPrimitiveJavaValue() ||
                 field.field().hasDefaultValue()) &&
                !field.alwaysPresent()) {
                writer.formatln("    return %s() ? %s : %s;", field.presence(), field.member(), field.kDefault());
            } else {
                writer.formatln("    return %s;", field.member());
            }
            writer.appendln('}')
                  .newline();
        }
    }

    private void appendFieldDeclarations(JMessage<?> message) throws GeneratorException {
        for (JField field : message.declaredOrderFields()) {
            if (field.isVoid()) {
                // Void fields have no value.
                continue;
            }
            writer.formatln("private final %s %s;", field.fieldType(), field.member());
        }
        writer.newline();
    }

    private void appendCreateMessage(JMessage<?> message) throws GeneratorException {
        // If an exception class contains a field named 'message' (no caps), it will use that
        // as the exception message unmodified.
        if (message.exceptionMessageField().isPresent()) {
            return;
        }

        writer.appendln("private static String createMessage(")
              .begin(   "                                    ");

        boolean first = true;
        for (JField fld : message.declaredOrderFields()) {
            if (!fld.isVoid()) {
                // Void fields have no value.
                if (first) {
                    first = false;
                } else {
                    writer.append(',')
                          .appendln();
                }
                writer.format("%s %s", fld.fieldType(), fld.param());
            }
        }

        writer.append(") {")
              .end()
              .begin()
              .appendln("StringBuilder out = new StringBuilder();")
              .appendln("out.append('{');");

        boolean firstFirstCheck = true;
        boolean alwaysAfter = false;
        boolean last;
        int i = 0;
        int lastPos = message.declaredOrderFields().size() - 1;
        for (JField field : message.declaredOrderFields()) {
            if (field.isVoid()) {
                // Void fields have no value.
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
                    writer.formatln("if (%s != null && %s.size() > 0) {", field.param(), field.param());
                } else {
                    writer.formatln("if (%s != null) {", field.param());
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
                    writer.formatln("   .append(%s);", field.param());
                    break;
                case BYTE:
                case I16:
                    writer.formatln("   .append((int) %s);", field.param());
                    break;
                case DOUBLE:
                case MAP:
                case SET:
                case LIST:
                    writer.formatln("   .append(%s.asString(%s));",
                                    Strings.class.getName(),
                                    field.param());
                    break;
                case STRING:
                    writer.formatln("   .append('\\\"')")
                          .formatln("   .append(%s.escape(%s))",
                                    Strings.class.getName(),
                                    field.param())
                          .appendln("   .append('\\\"');");
                    break;
                case BINARY:
                    writer.appendln("   .append(\"b64(\")")
                          .formatln("   .append(%s.toBase64())", field.param())
                          .appendln("   .append(')');");
                    break;
                case MESSAGE:
                    writer.formatln("   .append(%s.asString());", field.param());
                    break;
                default:
                    writer.formatln("   .append(%s.asString());", field.param());
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
        writer.appendln("out.append('}');")
              .appendln("return out.toString();")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendCreateConstructor(JMessage<?> message) throws GeneratorException {
        if (message.isUnion()) {
            for (JField field : message.declaredOrderFields()) {
                BlockCommentBuilder block = new BlockCommentBuilder(writer);
                if (field.hasComment()) {
                    block.comment(field.comment());
                }
                if (field.isVoid()) {
                    block.return_("The created union.")
                         .finish();
                    writer.formatln("public static %s %s() {",
                                    message.instanceType(),
                                    camelCase("with", field.name()))
                          .begin()
                          .formatln("return new _Builder().%s().build();", field.setter())
                          .end()
                          .appendln('}')
                          .newline();
                } else {
                    block.param_("value", "The union value")
                         .return_("The created union.")
                         .finish();
                    writer.formatln("public static %s %s(%s value) {",
                                    message.instanceType(),
                                    camelCase("with", field.name()),
                                    field.valueType())
                          .begin()
                          .formatln("return new _Builder().%s(value).build();", field.setter())
                          .end()
                          .appendln('}')
                          .newline();
                }
            }
        } else {
            if (message.hasAnnotation(ThriftAnnotation.JAVA_PUBLIC_CONSTRUCTOR) ||
                javaOptions.public_constructors) {
                String spaces = message.instanceType()
                                       .replaceAll("[\\S]", " ");
                writer.formatln("public %s(", message.instanceType())
                      .begin("        " + spaces);
                boolean first = true;
                for (JField field : message.declaredOrderFields()) {
                    if (field.isVoid()) {
                        // Void fields have no value.
                        continue;
                    }
                    if (first) {
                        first = false;
                    } else {
                        writer.append(',')
                              .appendln();
                    }
                    writer.format("%s %s", field.paramType(), field.param());
                }
                writer.end()
                      .append(") {")
                      .begin();

                if (message.isException()) {
                    // If an exception class contains a field named 'message' (no caps), it will use that
                    // as the exception message unmodified.
                    Optional<JField> msg = message.exceptionMessageField();
                    if (msg.isPresent()) {
                        writer.formatln("super(%s);",
                                        msg.get()
                                           .param())
                              .newline();
                    } else {
                        writer.appendln("super(createMessage(")
                              .begin("                    ");
                        first = true;
                        for (JField field : message.declaredOrderFields()) {
                            // Void fields have no value.
                            if (field.isVoid()) {
                                continue;
                            }

                            if (first) {
                                first = false;
                            } else {
                                writer.append(',')
                                      .appendln();
                            }
                            writer.format("%s", field.param());
                        }
                        writer.append("));")
                              .end()
                              .newline();
                    }
                }

                for (JField field : message.declaredOrderFields()) {
                    // Void fields have no value.
                    if (field.isVoid()) {
                        continue;
                    }
                    switch (field.type()) {
                        case LIST: {
                            writer.formatln("if (%s != null) {", field.param())
                                  .formatln("    %s = %s;", field.member(), field.fieldInstanceCopy(field.param()))
                                  .appendln("} else {");
                            if (field.alwaysPresent()) {
                                writer.formatln("    %s = %s;", field.member(), field.kDefault());
                            } else {
                                writer.formatln("    %s = null;", field.member());
                            }
                            writer.appendln('}');
                            break;
                        }
                        case SET: {
                            writer.formatln("if (%s != null) {", field.param())
                                  .formatln("    %s = %s;", field.member(), field.fieldInstanceCopy(field.param()))
                                  .appendln("} else {");
                            if (field.alwaysPresent()) {
                                writer.formatln("    %s = %s;", field.member(), field.kDefault());
                            } else {
                                writer.formatln("    %s = null;", field.member());
                            }
                            writer.appendln('}');
                            break;
                        }
                        case MAP: {
                            writer.formatln("if (%s != null) {", field.param())
                                  .formatln("    %s = %s;", field.member(), field.fieldInstanceCopy(field.param()))
                                  .appendln("} else {");
                            if (field.alwaysPresent()) {
                                writer.formatln("    %s = %s;", field.member(), field.kDefault());
                            } else {
                                writer.formatln("    %s = null;", field.member());
                            }
                            writer.appendln('}');
                            break;
                        }
                        default: {
                            if (field.alwaysPresent() && !(field.isRequired() && field.isPrimitiveJavaValue())) {
                                writer.formatln("if (%s != null) {", field.param())
                                      .formatln("    %s = %s;", field.member(), field.param())
                                      .appendln("} else {")
                                      .formatln("    %s = %s;", field.member(), field.kDefault())
                                      .appendln('}');
                            } else {
                                writer.formatln("%s = %s;", field.member(), field.param());
                            }
                            break;
                        }
                    }
                }
                writer.end()
                      .appendln('}')
                      .newline();
            }
        }
    }
}
