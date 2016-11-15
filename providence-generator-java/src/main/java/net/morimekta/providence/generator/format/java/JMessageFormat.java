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

package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.PException;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilderFactory;
import net.morimekta.providence.PType;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PDefaultValueProvider;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.descriptor.PValueProvider;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.ContainerType;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.generator.format.java.utils.ValueBuilder;
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;

import static net.morimekta.providence.generator.format.java.utils.JUtils.camelCase;

/**
 * @author Stein Eldar Johnsen
 * @since 20.09.15
 */
public class JMessageFormat {
    private static final String DBL_INDENT = IndentedPrintWriter.INDENT + IndentedPrintWriter.INDENT;

    private final JHelper  helper;
    private final JOptions options;

    JMessageFormat(JHelper helper, JOptions options) {
        this.helper = helper;
        this.options = options;
    }

    public void format(IndentedPrintWriter writer, PStructDescriptor<?,?> descriptor)
            throws GeneratorException, IOException {
        format(writer, descriptor, null);
    }

    public void format(IndentedPrintWriter writer,
                       PStructDescriptor<?,?> descriptor,
                       CService containingService)
            throws GeneratorException, IOException {
        @SuppressWarnings("unchecked")
        JMessage<?> message = new JMessage(descriptor, helper);

        JMessageAndroidFormat android = new JMessageAndroidFormat(writer, helper);
        JMessageOverridesFormat overrides = new JMessageOverridesFormat(writer, options, helper);
        JMessageBuilderFormat builder = new JMessageBuilderFormat(writer, helper, options);
        ValueBuilder values = new ValueBuilder(writer, helper);

        CAnnotatedDescriptor annotatedDescriptor = (CAnnotatedDescriptor) descriptor;
        if (annotatedDescriptor.getComment() != null) {
            new BlockCommentBuilder(writer)
                    .comment(annotatedDescriptor.getComment())
                    .finish();
        }
        if (JAnnotation.isDeprecated(annotatedDescriptor)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }

        String mod = "public";
        if (containingService != null) {
            mod = "private static";
        }

        writer.appendln("@SuppressWarnings(\"unused\")")
              .formatln("%s class %s", mod, message.instanceType())
              .begin(DBL_INDENT);
        if (message.isException()) {
            writer.appendln("extends " + message.exceptionBaseClass());
        }
        writer.formatln("implements %s<%s,%s._Field>",
                        message.isUnion() ? PUnion.class.getName() : PMessage.class.getName(),
                        message.instanceType(),
                        message.instanceType())
              .begin("           ");
        if (message.isException()) {
            writer.append(',')
                  .appendln(PException.class.getName());
        } else {
            // because it may be contained in an exception.
            writer.append(',')
                  .appendln(Serializable.class.getName());

        }
        if (options.android) {
            writer.append(",")
                  .formatln("android.os.Parcelable");
        }

        writer.append(',')
              .formatln("Comparable<%s>", message.instanceType());

        if (message.extraImplements() != null) {
            writer.append(",")
                  .appendln(message.extraImplements());
        }
        writer.end()  // "implements" indent
              .append(" {")
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

        appendFieldEnum(writer, message);
        appendDescriptor(writer, message);

        if (options.android) {
            android.appendParcelable(message);
        }
        if (message.isException()) {
            appendOriginalGetMessage(writer);
        }

        builder.appendBuilder(message);

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendOriginalGetMessage(IndentedPrintWriter writer) {
        writer.appendln("@Override")
              .appendln("public String origGetMessage() {")
              .appendln("    return super.getMessage();")
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public String origGetLocalizedMessage() {")
              .appendln("    return super.getLocalizedMessage();")
              .appendln('}')
              .newline();
    }

    private void appendFieldEnum(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        writer.formatln("public enum _Field implements %s {", PField.class.getName())
              .begin();

        for (JField field : message.fields()) {
            String provider = field.getProvider();
            String defValue = "null";
            if (field.getPField()
                     .hasDefaultValue()) {
                defValue = String.format("new %s<>(%s)",
                                         PDefaultValueProvider.class.getName(),
                                         field.kDefault());
            }

            writer.formatln("%s(%d, %s.%s, \"%s\", %s, %s),",
                            field.fieldEnum(),
                            field.id(),
                            PRequirement.class.getName(),
                            field.getPField()
                                 .getRequirement()
                                 .name(),
                            field.name(),
                            provider,
                            defValue);
        }
        writer.appendln(';')
              .newline();

        writer.appendln("private final int mKey;")
              .formatln("private final %s mRequired;", PRequirement.class.getName())
              .appendln("private final String mName;")
              .formatln("private final %s mTypeProvider;", PDescriptorProvider.class.getName())
              .formatln("private final %s<?> mDefaultValue;", PValueProvider.class.getName())
              .newline()
              .formatln("_Field(int key, %s required, String name, %s typeProvider, %s<?> defaultValue) {",
                        PRequirement.class.getName(),
                        PDescriptorProvider.class.getName(),
                        PValueProvider.class.getName())
              .begin()
              .appendln("mKey = key;")
              .appendln("mRequired = required;")
              .appendln("mName = name;")
              .appendln("mTypeProvider = typeProvider;")
              .appendln("mDefaultValue = defaultValue;")
              .end()
              .appendln('}')
              .newline();
        writer.appendln("@Override")
              .appendln("public int getKey() { return mKey; }")
              .newline();
        writer.appendln("@Override")
              .formatln("public %s getRequirement() { return mRequired; }",
                        PRequirement.class.getName())
              .newline();
        writer.appendln("@Override")
              .formatln("public %s getDescriptor() { return mTypeProvider.descriptor(); }",
                        PDescriptor.class.getName())
              .newline();
        writer.appendln("@Override")
              .appendln("public String getName() { return mName; }")
              .newline();
        writer.appendln("@Override")
              .appendln("public boolean hasDefaultValue() { return mDefaultValue != null; }")
              .newline();
        writer.appendln("@Override")
              .appendln("public Object getDefaultValue() {")
              .appendln("    return hasDefaultValue() ? mDefaultValue.get() : null;")
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public String toString() {")
              .formatln("    return %s.toString(this);", PField.class.getName())
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

    private void appendDescriptor(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        String typeClass = message.getDescriptorClass();
        String providerClass = message.getProviderClass();

        writer.formatln("public static %s<%s,_Field> provider() {", providerClass, message.instanceType())
              .begin()
              .formatln("return new _Provider();")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .formatln("public %s<%s,_Field> descriptor() {", typeClass, message.instanceType())
              .begin()
              .appendln("return kDescriptor;")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static final %s<%s,_Field> kDescriptor;", typeClass, message.instanceType())
              .newline();

        writer.formatln("private static class _Descriptor")
              .formatln("        extends %s<%s,_Field> {", typeClass, message.instanceType())
              .begin()
              .appendln("public _Descriptor() {")
              .begin();
        if (message.isException() || message.isUnion()) {
            writer.formatln("super(\"%s\", \"%s\", new _Factory(), %s);",
                            message.descriptor()
                                   .getPackageName(),
                            message.descriptor()
                                   .getName(),
                            message.descriptor()
                                   .isSimple());
        } else {
            writer.formatln("super(\"%s\", \"%s\", new _Factory(), %b, %b);",
                            message.descriptor()
                                   .getPackageName(),
                            message.descriptor()
                                   .getName(),
                            message.descriptor()
                                   .isSimple(),
                            message.descriptor()
                                   .isCompactible());
        }
        writer.end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln("public _Field[] getFields() {")
              .begin()
              .appendln("return _Field.values();")
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln("public _Field getField(String name) {")
              .begin()
              .appendln("return _Field.forName(name);")
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln("public _Field getField(int key) {")
              .begin()
              .appendln("return _Field.forKey(key);")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        writer.formatln("static {", typeClass, message.instanceType())
              .begin()
              .appendln("kDescriptor = new _Descriptor();")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("private final static class _Provider extends %s<%s,_Field> {",
                        providerClass,
                        message.instanceType())
              .begin()
              .appendln("@Override")
              .formatln("public %s<%s,_Field> descriptor() {", typeClass, message.instanceType())
              .begin()
              .appendln("return kDescriptor;")
              .end()
              .appendln('}')
              .end()
              .appendln("}")
              .newline();

        writer.appendln("private final static class _Factory")
              .begin()
              .formatln("    extends %s<%s,_Field> {",
                        PMessageBuilderFactory.class.getName(),
                        message.instanceType())
              .appendln("@Override")
              .appendln("public _Builder builder() {")
              .begin()
              .appendln("return new _Builder();")
              .end()
              .appendln('}')
              .end()
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
                if (field.alwaysPresent() || field.isVoid()) {
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
            writer.formatln("public %s %s() {", field.valueType(), field.getter());
            if ((field.isPrimitiveJavaValue() && !field.alwaysPresent()) || (
                    !field.container() && !field.alwaysPresent() && field.getPField().hasDefaultValue())) {
                writer.formatln("    return %s() ? %s : %s;", field.presence(), field.member(), field.kDefault());
            } else {
                writer.formatln("    return %s;", field.member());
            }
            writer.appendln('}')
                  .newline();
        }

        if (message.isUnion()) {
            writer.appendln("@Override")
                  .appendln("public _Field unionField() {")
                  .appendln("    return tUnionField;")
                  .appendln('}')
                  .newline();
        }
    }

    private void appendFieldDeclarations(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        for (JField field : message.fields()) {
            if (field.isVoid()) {
                // Void fields have no value.
                continue;
            }
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
                for (JField field : message.fields()) {
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

            for (JField field : message.fields()) {
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

    private void appendCreateMessage(IndentedPrintWriter writer, JMessage<?> message) throws GeneratorException {
        writer.appendln("private static String createMessage(")
              .begin(   "                                    ");

        boolean first = true;
        for (JField fld : message.fields()) {
            if (!fld.isVoid()) {
                // Void fields have no value.
                if (first) {
                    first = false;
                } else {
                    writer.append(',')
                          .appendln();
                }
                writer.format("%s %s", fld.valueType(), fld.param());
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
        int lastPos = message.fields().size() - 1;
        for (JField field : message.fields()) {
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
                if (field.isVoid()) {
                    writer.formatln("public static %s %s() {",
                                    message.instanceType(),
                                    camelCase("with", field.name()))
                          .begin()
                          .formatln("return new _Builder().%s().build();", field.setter())
                          .end()
                          .appendln('}')
                          .newline();
                } else {
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
            String spaces = message.instanceType()
                                   .replaceAll("[\\S]", " ");
            writer.formatln("public %s(", message.instanceType())
                  .begin("        " + spaces);
            boolean first = true;
            for (JField field : message.fields()) {
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
                writer.appendln("super(createMessage(")
                      .begin(   "                    ");
                first = true;
                for (JField field : message.fields()) {
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

            for (JField field : message.fields()) {
                // Void fields have no value.
                if (field.isVoid()) {
                    continue;
                }
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
                    default: {
                        if (field.alwaysPresent() && !field.isRequired()){
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
