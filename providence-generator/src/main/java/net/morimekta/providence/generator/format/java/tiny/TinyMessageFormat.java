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
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.IOException;
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
        TinyValueFormat values = new TinyValueFormat(writer, options, helper);

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
            writer.appendln("@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)")
                  .appendln("@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY)")
                  .appendln("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(")
                  .formatln("        builder = %s._Builder.class)", message.instanceType());
        }

        writer.appendln("@SuppressWarnings(\"unused\")")
              .formatln("public class %s", message.instanceType())
              .begin(DBL_INDENT);
        if (message.variant()
                   .equals(PMessageVariant.EXCEPTION)) {
            writer.appendln("extends " + Exception.class.getName());
        }
        writer.formatln("implements java.io.Serializable, Comparable<%s>",
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

        appendFieldGetters(writer, message);

        overrides.appendOverrides(message);

        appendFieldEnum(writer, message);

        builder.appendBuilder(message);

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendFieldEnum(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        writer.formatln("public enum _Field {")
              .begin();

        for (JField field : message.fields()) {
            writer.formatln("%s(%d, \"%s\"),",
                            field.fieldEnum(),
                            field.id(),
                            field.name());
        }
        writer.appendln(';')
              .newline();

        writer.appendln("private final int mKey;")
              .appendln("private final String mName;")
              .newline()
              .formatln("_Field(int key, String name) {")
              .begin()
              .appendln("mKey = key;")
              .appendln("mName = name;")
              .end()
              .appendln('}')
              .newline();
        writer.appendln("public int getKey() { return mKey; }")
              .newline();
        writer.appendln("public String getName() { return mName; }")
              .newline();

        writer.appendln("@Override")
              .appendln("public String toString() {")
              .appendln("    StringBuilder builder = new StringBuilder();")
              .formatln("    builder.append(\"%s._Field(\")", message.instanceType())
              .appendln("           .append(mKey)")
              .appendln("           .append(':')")
              .appendln("           .append(mName)")
              .appendln("           .append(')');")
              .appendln("    return builder.toString();")
              .appendln('}')
              .newline();

        writer.appendln("public static _Field forKey(int key) {")
              .begin()
              .appendln("switch (key) {")
              .begin();
        for (JField field : message.fields()) {
            writer.formatln("case %d: return _Field.%s;", field.id(), field.fieldEnum());
        }
        writer.end()
              .appendln('}')
              .appendln("return null;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("public static _Field forName(String name) {")
              .begin()
              .appendln("switch (name) {")
              .begin();
        for (JField field : message.fields()) {
            writer.formatln("case \"%s\": return _Field.%s;", field.name(), field.fieldEnum());
        }
        writer.end()
              .appendln('}')
              .appendln("return null;")
              .end()
              .appendln('}');

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendFieldGetters(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        for (JField field : message.fields()) {
            if (message.isUnion()) {
                if (field.container()) {
                    writer.formatln("public int %s() {", field.counter())
                          .formatln("    return tUnionField == _Field.%s ? %s.size() : 0;",
                                    field.fieldEnum(),
                                    field.member())
                          .appendln('}')
                          .newline();
                }
                if (field.alwaysPresent()) {
                    writer.formatln("public boolean %s() {", field.presence())
                          .formatln("    return tUnionField == _Field.%s;", field.fieldEnum())
                          .appendln('}')
                          .newline();
                } else {
                    writer.formatln("public boolean %s() {", field.presence())
                          .formatln("    return tUnionField == _Field.%s && %s != null;",
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
                          .formatln("    return true;")
                          .appendln('}')
                          .newline();
                } else {
                    writer.formatln("public boolean %s() {", field.presence())
                          .formatln("    return %s != null;", field.member())
                          .appendln('}')
                          .newline();
                }
            }

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
                writer.formatln("    return %s() ? %s : %s;", field.presence(), field.member(), field.kDefault());
            } else {
                writer.formatln("    return %s;", field.member());
            }
            writer.appendln('}')
                  .newline();
        }

        if (message.isUnion()) {
            writer.appendln("public _Field unionField() {")
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
                  .appendln("private final _Field tUnionField;");
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
                switch (field.type()) {
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
                writer.appendln("super(builder.createMessage());")
                      .newline();
            }
            for (JField field : message.fields()) {
                if (field.container()) {
                    writer.formatln("if (builder.%s()) {", field.isSet())
                          .formatln("    %s = builder.%s.build();", field.member(), field.member())
                          .appendln("} else {")
                          .formatln("    %s = null;", field.member())
                          .appendln('}');
                } else {
                    writer.formatln("%s = builder.%s;", field.member(), field.member());
                }
            }
        }
        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendCreateConstructor(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        if (message.isException()) {
            // TODO(steineldar): Handle constructing exception!
        } else if (message.isUnion()) {
            for (JField field : message.fields()) {
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
