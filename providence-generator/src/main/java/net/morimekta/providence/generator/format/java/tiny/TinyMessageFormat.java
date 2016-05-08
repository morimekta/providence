/*
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

package net.morimekta.providence.generator.format.java.tiny;

import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.ContainerType;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.generator.format.java.utils.JOptions;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.generator.format.java.utils.ValueBuilder;
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.util.Stringable;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;

import static net.morimekta.providence.generator.format.java.utils.JUtils.camelCase;

/**
 * @author Stein Eldar Johnsen
 * @since 20.09.15
 */
public class TinyMessageFormat {
    public static final String DBL_INDENT = IndentedPrintWriter.INDENT + IndentedPrintWriter.INDENT;

    private final JHelper  helper;
    private final JOptions options;

    public TinyMessageFormat(JHelper helper, JOptions options) {
        this.helper = helper;
        this.options = options;
    }

    public void format(IndentedPrintWriter writer,
                       PStructDescriptor<?,?> descriptor)
            throws GeneratorException, IOException {
        @SuppressWarnings("unchecked")
        JMessage<?> message = new JMessage(descriptor, helper);

        TinyMessageOverridesFormat overrides = new TinyMessageOverridesFormat(writer, options, helper);
        TinyMessageBuilderFormat builder = new TinyMessageBuilderFormat(writer, helper, options);
        ValueBuilder values = new ValueBuilder(writer, options, helper);

        CAnnotatedDescriptor annotatedDescriptor = (CAnnotatedDescriptor) descriptor;
        if (annotatedDescriptor.getComment() != null) {
            new BlockCommentBuilder(writer)
                    .comment(annotatedDescriptor.getComment())
                    .finish();
        }
        if (JAnnotation.isDeprecated(annotatedDescriptor)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }

        if (options.jackson) {
            writer.formatln("@%s(ignoreUnknown = true)", JsonIgnoreProperties.class.getName());
            if (!message.isUnion()) {
                writer.formatln("@%s(%s.%s)",
                                JsonInclude.class.getName(),
                                JsonInclude.Include.class.getName()
                                                         .replaceAll("[$]", "."),
                                JsonInclude.Include.NON_EMPTY.name())
                      .formatln("@%s({", JsonPropertyOrder.class.getName())
                      .begin("        ");
                boolean first = true;
                for (JField field : message.fields()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(", ");
                    }
                    writer.formatln("\"%s\"", field.name());
                }
                writer.end()
                      .format("})");
            } else {
                writer.formatln("@%s(", JsonSerialize.class.getName())
                      .formatln("        using = %s._Serializer.class)", message.instanceType());
            }
            writer.formatln("@%s(", JsonDeserialize.class.getName())
                  .formatln("        using = %s._Deserializer.class)", message.instanceType());
        }
        if (JAnnotation.isDeprecated(message.descriptor())) {
            writer.appendln(JAnnotation.DEPRECATED);
        }

        writer.appendln("@SuppressWarnings(\"unused\")")
              .formatln("public class %s", message.instanceType())
              .begin(DBL_INDENT);
        if (message.variant()
                   .equals(PMessageVariant.EXCEPTION)) {
            writer.appendln("extends " + Exception.class.getName());
        }
        writer.formatln("implements %s, %s, Comparable<%s>",
                        Serializable.class.getName(),
                        Stringable.class.getName(),
                        message.instanceType());
        writer.append(" {")
              .end()  // double indent.
              .begin();

        writer.formatln("private final static long serialVersionUID = %dL;",
                        JUtils.generateSerialVersionUID(message.descriptor()))
              .newline();

        values.appendDefaultConstants(message.fields());

        appendFieldDeclarations(writer, message);

        appendBuilderConstructor(writer, message);
        appendCreateConstructor(writer, message);

        if (message.isException()) {
            appendCreateMessage(writer, message);
        }

        appendFieldGetters(writer, message);

        overrides.appendOverrides(message);

        if (message.isUnion()) {
            if (options.jackson) {
                appendUnionSerializer(writer, message);
            }
            appendFieldEnum(writer, message);
        }

        builder.appendBuilder(message);

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendUnionSerializer(IndentedPrintWriter writer, JMessage<?> message) {
        writer.formatln("public static class _Serializer extends %s<%s> {",
                        JsonSerializer.class.getName(),
                        message.instanceType())
              .begin()
              .appendln("@Override")
              .formatln("public void serialize(%s value, %s jgen, %s provider)",
                        message.instanceType(),
                        JsonGenerator.class.getName(),
                        SerializerProvider.class.getName())
              .formatln("        throws %s, %s {",
                        IOException.class.getName(), JsonProcessingException.class.getName())
              .begin();

        writer.appendln("jgen.writeStartObject();")
              .appendln("switch (value.tUnionField) {")
              .begin();

        for (JField field : message.fields()) {
            writer.formatln("case %s: {", field.fieldEnum())
                  .begin();

            if (field.type() == PType.BINARY) {
                writer.formatln("provider.defaultSerializeField(\"%s\", value.%s.toBase64(), jgen);", field.name(), field.member());
            } else {
                writer.formatln("provider.defaultSerializeField(\"%s\", value.%s, jgen);", field.name(), field.member());
            }
            writer.appendln("break;")
                  .end()
                  .appendln('}');
        }

        writer.end()
              .appendln('}')
              .appendln("jgen.writeEndObject();");

        writer.end()
              .appendln('}')
              .end()
              .appendln('}');
    }

    private void appendFieldEnum(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        for (JField field : message.fields()) {
            writer.formatln("public static final int %s = %d;", field.fieldEnum(), field.id());
        }
        writer.newline();
    }

    private void appendFieldGetters(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        for (JField field : message.fields()) {
            if (field.hasComment()) {
                new BlockCommentBuilder(writer)
                        .comment(field.comment())
                        .finish();
            }
            if (JAnnotation.isDeprecated(field)) {
                writer.appendln(JAnnotation.DEPRECATED);
            }
            if (options.jackson) {
                writer.formatln("@com.fasterxml.jackson.annotation.JsonProperty(\"%s\")", field.name());
                if (field.binary()) {
                    writer.appendln("@com.fasterxml.jackson.databind.annotation.JsonSerialize(" +
                                    "using = net.morimekta.providence.jackson.BinaryJsonSerializer.class) ");
                }
            }
            writer.formatln("public %s %s() {", field.valueType(), field.getter());
            if (!field.container() && !field.alwaysPresent() && field.getPField()
                                                                     .hasDefaultValue()) {
                writer.formatln("    return %s != null ? %s : %s;", field.member(), field.member(), field.kDefault());
            } else {
                writer.formatln("    return %s;", field.member());
            }
            writer.appendln('}')
                  .newline();
        }

        if (message.isUnion()) {
            writer.appendln("public int unionField() {")
                  .appendln("    return tUnionField;")
                  .appendln('}')
                  .newline();
        }
    }

    private void appendFieldDeclarations(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        for (JField field : message.fields()) {
            writer.formatln("private final %s %s;", field.fieldType(), field.member());
        }
        if (message.isUnion()) {
            writer.newline()
                  .appendln("private final int tUnionField;");
        }
        writer.appendln()
              .appendln("private volatile int tHashCode;")
              .newline();
    }

    private void appendBuilderConstructor(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        writer.formatln("private %s(_Builder builder) {", message.instanceType())
              .begin();
        if (message.isUnion()) {
            writer.appendln("tUnionField = builder.tUnionField;")
                  .newline();

            for (JField field : message.fields()) {
                if (field.alwaysPresent()) {
                    writer.formatln("%s = tUnionField == %s ? builder.%s : %s;",
                                    field.member(),
                                    field.fieldEnum(),
                                    field.member(),
                                    field.kDefault());
                } else {
                    writer.formatln("%s = tUnionField == %s ? builder.%s : null;",
                                    field.member(),
                                    field.fieldEnum(),
                                    field.member());
                }
            }
        } else {
            if (message.isException()) {
                writer.appendln("super(createMessage(")
                      .begin(   "                    ");
                boolean first = true;
                for (JField field : message.fields()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(',')
                              .appendln();
                    }
                    writer.format("builder.%s", field.member());
                }
                writer.append("));")
                      .end()
                      .newline();
            }

            for (JField field : message.fields()) {
                writer.formatln("%s = builder.%s;", field.member(), field.member());
            }
        }
        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendCreateMessage(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        writer.appendln("private static String createMessage(")
              .begin(   "                                    ");

        boolean first = true;
        for (JField fld : message.fields()) {
            if (first) {
                first = false;
            } else {
                writer.append(',')
                      .appendln();
            }
            writer.format("%s %s", fld.valueType(), fld.param());
        }

        writer.append(") {")
              .end()
              .begin()
              .appendln("StringBuilder out = new StringBuilder();")
              .appendln("out.append('{');");

        boolean firstFirstCheck = true;
        boolean alwaysAfter = false;
        boolean last;
        JField[] fields = message.fields().toArray(new JField[message.fields().size()]);
        for (int i = 0; i < fields.length; ++i) {
            first = i == 0;
            last  = i == (fields.length - 1);

            JField field = fields[i];
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
                    writer.formatln("   .append(%s.toString());", field.param());
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

    private void appendCreateConstructor(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        if (message.isUnion()) {
            for (JField field : message.fields()) {
                BlockCommentBuilder block = new BlockCommentBuilder(writer);
                if (field.hasComment()) {
                    block.comment(field.comment());
                }
                block.param_("value", "The union value")
                     .return_("The created union.")
                     .finish();
                writer.formatln("public static %s %s(%s value) {",
                                message.instanceType(),
                                camelCase("with", field.name()),
                                field.valueType())
                      .formatln("    return new _Builder().%s(value).build();", field.setter())
                      .appendln('}')
                      .newline();
            }
        } else {
            String spaces = message.instanceType()
                                   .replaceAll("[\\S]", " ");
            writer.formatln("public %s(", message.instanceType())
                  .begin("        " + spaces);
            boolean first = true;
            for (JField field : message.fields()) {
                if (first) {
                    first = false;
                } else {
                    writer.append(',')
                          .appendln();
                }
                writer.format("%s %s", field.valueType(), field.param());
            }
            writer.end()
                  .append(") {")
                  .begin();

            if (message.isException()) {
                writer.appendln("super(createMessage(")
                      .begin(   "                    ");
                first = true;
                for (JField field : message.fields()) {
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

            for (JField field : message.fields()) {
                switch (field.type()) {
                    case LIST:
                        writer.formatln("if (%s != null) {", field.param())
                              .formatln("    %s = %s.copyOf(%s);",
                                        field.member(),
                                        field.fieldInstanceType(),
                                        field.param())
                              .appendln("} else {")
                              .formatln("    %s = null;", field.member())
                              .appendln('}');
                        break;
                    case SET:
                        writer.formatln("if (%s != null) {", field.param())
                              .begin();
                        if (field.containerType() == ContainerType.ORDERED) {
                            writer.formatln("%s = %s.unmodifiableSet(%s);",
                                            field.member(),
                                            Collections.class.getName(),
                                            field.param());
                        } else {
                            writer.formatln("%s = %s.copyOf(%s);",
                                            field.member(),
                                            field.fieldInstanceType(),
                                            field.param());
                        }
                        writer.end()
                              .appendln("} else {")
                              .formatln("    %s = null;", field.member())
                              .appendln('}');
                        break;
                    case MAP:
                        writer.formatln("if (%s != null) {", field.param())
                              .begin();
                        if (field.containerType() == ContainerType.ORDERED) {
                            writer.formatln("%s = %s.unmodifiableMap(%s);",
                                            field.member(),
                                            Collections.class.getName(),
                                            field.param());
                        } else {
                            writer.formatln("%s = %s.copyOf(%s);",
                                            field.member(),
                                            field.fieldInstanceType(),
                                            field.param());
                        }
                        writer.end()
                              .appendln("} else {")
                              .formatln("    %s = null;", field.member())
                              .appendln('}');
                        break;
                    default:
                        writer.formatln("%s = %s;", field.member(), field.param());
                        break;
                }
            }
            writer.end()
                  .appendln('}')
                  .newline();
        }
    }
}
