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
import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.PType;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PDefaultValueProvider;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PExceptionDescriptor;
import net.morimekta.providence.descriptor.PExceptionDescriptorProvider;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptorProvider;
import net.morimekta.providence.descriptor.PUnionDescriptor;
import net.morimekta.providence.descriptor.PUnionDescriptorProvider;
import net.morimekta.providence.descriptor.PValueProvider;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.IOException;
import java.util.Collections;

import static net.morimekta.providence.generator.format.java.JUtils.camelCase;

/**
 * @author Stein Eldar Johnsen
 * @since 20.09.15
 */
public class JMessageFormat {
    public static final String DBL_INDENT = IndentedPrintWriter.INDENT + IndentedPrintWriter.INDENT;

    private final JHelper  helper;
    private final JOptions options;

    public JMessageFormat(JHelper helper, JOptions options) {
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
        JMessageBuilderFormat builder = new JMessageBuilderFormat(writer, helper);
        JValueFormat values = new JValueFormat(writer, options, helper);

        CAnnotatedDescriptor annotatedDescriptor = (CAnnotatedDescriptor) descriptor;
        if (annotatedDescriptor.getComment() != null) {
            JUtils.appendBlockComment(writer, annotatedDescriptor.getComment());
            if (JAnnotation.isDeprecated(annotatedDescriptor)) {
                writer.appendln(JAnnotation.DEPRECATED);
            }
        }

        if (options.jackson) {
            writer.appendln("@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)")
                  .appendln("@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY)");
        }

        String mod = "";
        if (containingService != null) {
            mod = "static ";
        }

        writer.appendln("@SuppressWarnings(\"unused\")")
              .formatln("public %sclass %s", mod, message.instanceType())
              .begin(DBL_INDENT);
        if (message.variant()
                   .equals(PMessageVariant.EXCEPTION)) {
            writer.appendln("extends " + PException.class.getName());
        }
        writer.formatln("implements %s<%s>, java.io.Serializable, Comparable<%s>",
                        message.isUnion() ? PUnion.class.getName() : PMessage.class.getName(),
                        message.instanceType(),
                        message.instanceType());
        if (options.android) {
            writer.format(", android.os.Parcelable");
        }
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
        appendDescriptor(writer, message);

        if (options.android) {
            android.appendParcelable(message);
        }

        builder.appendBuilder(message);

        writer.end()
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
              .formatln("public %s getType() { return getDescriptor().getType(); }",
                        PType.class.getName())
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
              .begin()
              .appendln("StringBuilder builder = new StringBuilder();")
              .formatln("builder.append(\"%s._Field(\")", message.instanceType())
              .appendln("       .append(mKey)")
              .appendln("       .append(\": \");")
              .formatln("if (mRequired != %s.DEFAULT) {",
                        PRequirement.class.getName())
              .appendln("    builder.append(mRequired.label).append(\" \");")
              .appendln("}")
              .appendln("builder.append(getDescriptor().getQualifiedName(null))")
              .appendln("       .append(' ')")
              .appendln("       .append(mName)")
              .appendln("       .append(')');")
              .appendln("return builder.toString();")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("public static _Field forKey(int key) {")
              .begin()
              .appendln("switch (key) {")
              .begin();
        for (JField field : message.fields()) {
            writer.formatln("case %d: return _Field.%s;", field.id(), field.fieldEnum());
        }
        writer.appendln("default: return null;")
              .end()
              .appendln('}')
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
        String typeClass;
        String providerClass;
        switch (message.variant()) {
            case STRUCT:
                typeClass = PStructDescriptor.class.getName();
                providerClass = PStructDescriptorProvider.class.getName();
                break;
            case UNION:
                typeClass = PUnionDescriptor.class.getName();
                providerClass = PUnionDescriptorProvider.class.getName();
                break;
            case EXCEPTION:
                typeClass = PExceptionDescriptor.class.getName();
                providerClass = PExceptionDescriptorProvider.class.getName();
                break;
            default:
                throw new GeneratorException("Unable to determine type class for " + message.variant());
        }

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
              .formatln("    extends %s<%s> {",
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
                } else if (field.alwaysPresent()) {
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
            } else if (field.container()) {
                writer.formatln("public int %s() {", field.counter())
                      .formatln("    return %s != null ? %s.size() : 0;", field.member(), field.member())
                      .appendln('}')
                      .newline();
            } else if (field.alwaysPresent()) {
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

            if (field.hasComment()) {
                JUtils.appendBlockComment(writer, field.comment());
                if (JAnnotation.isDeprecated(field)) {
                    writer.appendln(JAnnotation.DEPRECATED);
                }
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
            writer.appendln("@Override")
                  .appendln("public _Field unionField() {")
                  .begin()
                  .appendln("return tUnionField;")
                  .end()
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
                      .begin()
                      .formatln("return new _Builder().%s(value).build();", field.setter())
                      .end()
                      .appendln('}')
                      .newline();
            }
        } else {
            if (options.jackson) {
                writer.appendln("@com.fasterxml.jackson.annotation.JsonCreator");
            }
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
                if (options.jackson) {
                    writer.format("@com.fasterxml.jackson.annotation.JsonProperty(\"%s\") ", field.name());
                    if (field.binary()) {
                        writer.append("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = net.morimekta.providence.jackson.BinaryJsonDeserializer.class) ");
                    }
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
