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

package net.morimekta.providence.compiler.format.java2;

import net.morimekta.providence.Binary;
import net.morimekta.providence.PException;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PMessageBuilderFactory;
import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.PType;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.compiler.generator.GeneratorException;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDefaultValueProvider;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PExceptionDescriptor;
import net.morimekta.providence.descriptor.PExceptionDescriptorProvider;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptorProvider;
import net.morimekta.providence.descriptor.PUnionDescriptor;
import net.morimekta.providence.descriptor.PUnionDescriptorProvider;
import net.morimekta.providence.descriptor.PValueProvider;
import net.morimekta.providence.util.PTypeUtils;
import net.morimekta.providence.util.io.IndentedPrintWriter;
import net.morimekta.providence.util.json.JsonException;
import net.morimekta.providence.util.json.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import static net.morimekta.providence.util.PStringUtils.camelCase;

/**
 * @author Stein Eldar Johnsen
 * @since 20.09.15
 */
public class JMessageFormat {
    public static final String DBL_INDENT =
            IndentedPrintWriter.INDENT +
            IndentedPrintWriter.INDENT;

    private final JHelper  helper;
    private final JOptions options;

    public JMessageFormat(JHelper helper,
                          JOptions options) {
        this.helper = helper;
        this.options = options;
    }

    public void format(IndentedPrintWriter writer, PStructDescriptor<?, ?> descriptor) throws GeneratorException, IOException {
        JMessage message = new JMessage(descriptor, helper);
        JAndroid android = new JAndroid(writer, helper);

        appendFileHeader(writer, message);

        if (descriptor.getComment() != null) {
            JUtils.appendBlockComment(writer, descriptor.getComment());
            if (JAnnotation.isDeprecated(descriptor)) {
                writer.appendln(JAnnotation.DEPRECATED);
            }
        }

        appendClassDefinitionStart(writer, message);

        appendFieldDefaultConstants(writer, message);
        appendFieldDeclarations(writer, message);

        appendBuilderConstructor(writer, message);
        appendCreateConstructor(writer, message);

        appendFieldGetters(writer, message);
        appendInheritedGetter_has(writer, message);
        appendInheritedGetter_num(writer, message);
        appendInheritedGetter_get(writer, message);

        appendObjectCompact(writer, message);
        appendObjectEquals(writer, message);
        appendObjectHashCode(writer, message);
        appendObjectToString(writer);

        appendFieldEnum(writer, message);
        appendDescriptor(writer, message);

        if (options.android) {
            android.appendParcelable(message);
        }

        appendBuilder(writer, message);
        appendClassDefinitionEnd(writer);
    }

    private void appendObjectCompact(IndentedPrintWriter writer, JMessage message) {
        if (message.descriptor().isCompactible()) {
            writer.appendln("@Override")
                  .appendln("public boolean isCompact() {")
                  .begin()
                  .appendln("boolean missing = false;");

            boolean hasCheck = false;
            for (JField field : message.fields()) {
                if (!field.alwaysPresent()) {
                    hasCheck = true;
                    writer.formatln("if (%s()) {", field.presence())
                          .appendln("    if (missing) return false;")
                          .appendln("} else {")
                          .appendln("    missing = true;")
                          .appendln('}');
                } else if (hasCheck) {
                    writer.appendln("if (missing) return false;");
                    hasCheck = false;
                }
            }

            writer.appendln("return true;")
                  .end()
                  .appendln('}')
                  .newline();
        } else {
            writer.appendln("@Override")
                  .appendln("public boolean isCompact() {")
                  .begin()
                  .appendln("return false;")
                  .end()
                  .appendln('}')
                  .newline();
        }
        writer.appendln("@Override")
              .appendln("public boolean isSimple() {")
              .begin()
              .appendln("return descriptor().isSimple();")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendBuilder(IndentedPrintWriter writer, JMessage message) {
        JMessageBuilderFormat format = new JMessageBuilderFormat(writer, helper);
        format.appendBuilder(message);
    }

    private void appendFieldEnum(IndentedPrintWriter writer, JMessage message) {
        writer.appendln("public enum _Field implements PField {")
              .begin();

        for (JField field : message.fields()) {
            String provider = helper.getProviderName(field.getPField().getDescriptor());
            String defValue = "null";
            if (field.getPField().hasDefaultValue()) {
                defValue = String.format("new PDefaultValueProvider<>(%s)", field.kDefault());
            }

            writer.formatln("%s(%d, PRequirement.%s, \"%s\", %s, %s),",
                            field.fieldEnum(),
                            field.id(),
                            field.getPField().getRequirement().name(),
                            field.name(),
                            provider,
                            defValue);
        }
        writer.appendln(';')
              .newline();

        writer.appendln("private final int mKey;")
              .appendln("private final PRequirement mRequired;")
              .appendln("private final String mName;")
              .appendln("private final PDescriptorProvider<?> mTypeProvider;")
              .appendln("private final PValueProvider<?> mDefaultValue;")
              .newline()
              .appendln(
                      "_Field(int key, PRequirement required, String name, PDescriptorProvider<?> typeProvider, PValueProvider<?> defaultValue) {")
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
              .appendln("public String getComment() { return null; }")
              .newline();
        writer.appendln("@Override")
              .appendln("public int getKey() { return mKey; }")
              .newline();
        writer.appendln("@Override")
              .appendln("public PRequirement getRequirement() { return mRequired; }")
              .newline();
        writer.appendln("@Override")
              .appendln("public PType getType() { return getDescriptor().getType(); }")
              .newline();
        writer.appendln("@Override")
              .appendln("public PDescriptor<?> getDescriptor() { return mTypeProvider.descriptor(); }")
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
              .formatln("builder.append(%s.class.getSimpleName())", message.instanceType())
              .appendln("       .append('{')")
              .appendln("       .append(mKey)")
              .appendln("       .append(\": \");")
              .appendln("if (mRequired != PRequirement.DEFAULT) {")
              .appendln("    builder.append(mRequired.label).append(\" \");")
              .appendln("}")
              .appendln("builder.append(getDescriptor().getQualifiedName(null))")
              .appendln("       .append(' ')")
              .appendln("       .append(mName)")
              .appendln("       .append('}');")
              .appendln("return builder.toString();")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("public static _Field forKey(int key) {")
              .begin()
              .appendln("switch (key) {")
              .begin();
        for (JField field : message.fields()) {
            writer.formatln("case %d: return _Field.%s;",
                            field.id(), field.fieldEnum());
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
            writer.formatln("case \"%s\": return _Field.%s;",
                            field.name(), field.fieldEnum());
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

    private void appendDescriptor(IndentedPrintWriter writer, JMessage message) throws GeneratorException {
        String typeClass;
        switch (message.variant()) {
            case STRUCT:
                typeClass = PStructDescriptor.class.getSimpleName();
                break;
            case UNION:
                typeClass = PUnionDescriptor.class.getSimpleName();
                break;
            case EXCEPTION:
                typeClass = PExceptionDescriptor.class.getSimpleName();
                break;
            default:
                throw new GeneratorException("Unable to determine type class for " + message.variant());
        }

        writer.formatln("public static %sProvider<%s,_Field> provider() {", typeClass, message.instanceType())
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
            writer.formatln("super(null, \"%s\", \"%s\", new _Factory(), %s);",
                            message.descriptor().getPackageName(),
                            message.descriptor().getName(),
                            message.descriptor().isSimple());
        } else {
            writer.formatln("super(null, \"%s\", \"%s\", new _Factory(), %b, %b);",
                            message.descriptor().getPackageName(),
                            message.descriptor().getName(),
                            message.descriptor().isSimple(),
                            message.descriptor().isCompactible());
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

        writer.formatln("private final static class _Provider extends %sProvider<%s,_Field> {", typeClass, message.instanceType())
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
              .formatln("    extends PMessageBuilderFactory<%s> {", message.instanceType())
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

    private void appendObjectToString(IndentedPrintWriter writer) {
        writer.appendln("@Override")
              .appendln("public String toString() {")
              .begin()
              .appendln("return descriptor().getQualifiedName(null) + PTypeUtils.toString(this);")
              .end()
              .appendln("}")
              .newline();
    }

    private void appendObjectEquals(IndentedPrintWriter writer, JMessage message) {
        writer.appendln("@Override")
              .appendln("public boolean equals(Object o) {")
              .begin()
              .formatln("if (o == null || !(o instanceof %s)) return false;", message.instanceType());
        if (message.fields().size() > 0) {
            boolean first = true;
            writer.formatln("%s other = (%s) o;", message.instanceType(), message.instanceType())
                  .appendln("return ");
            for (JField field : message.fields()) {
                if (first)
                    first = false;
                else {
                    writer.append(" &&")
                          .appendln("       ");
                }
                writer.format("PTypeUtils.equals(%s, other.%s)", field.member(), field.member());
            }
            writer.append(';');
        } else {
            writer.appendln("return true;");
        }
        writer.end()
              .appendln("}")
              .newline();
    }

    private void appendObjectHashCode(IndentedPrintWriter writer, JMessage message) {
        writer.appendln("@Override")
              .appendln("public int hashCode() {")
              .begin()
              .formatln("return %s.class.hashCode()", message.instanceType())
              .begin("       ");
        for (JField field : message.fields()) {
            writer.append(" +")
                  .formatln("PTypeUtils.hashCode(_Field.%s, %s)", field.fieldEnum(), field.member());
        }
        writer.end()
              .append(";")
              .end()
              .appendln("}")
              .newline();
    }

    private void appendInheritedGetter_has(IndentedPrintWriter writer, JMessage message) {
        writer.appendln("@Override")
              .appendln("public boolean has(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (JField field : message.fields()) {
            if (field.container()) {
                writer.formatln("case %d: return %s() > 0;",
                                field.id(), field.counter());
            } else if (field.alwaysPresent()) {
                writer.formatln("case %d: return true;",
                                field.id());
            } else {
                writer.formatln("case %d: return %s();",
                                field.id(), field.presence());
            }
        }

        writer.appendln("default: return false;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendInheritedGetter_num(IndentedPrintWriter writer, JMessage message) {
        writer.appendln("@Override")
              .appendln("public int num(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (JField field : message.fields()) {
            if (field.container()) {
                writer.formatln("case %d: return %s();",
                                field.id(), field.counter());
            } else if (field.alwaysPresent()) {
                writer.formatln("case %d: return 1;", field.id());
            } else {
                writer.formatln("case %d: return %s() ? 1 : 0;",
                                field.id(), field.presence());
            }
        }

        writer.appendln("default: return 0;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendInheritedGetter_get(IndentedPrintWriter writer, JMessage message) {
        writer.appendln("@Override")
              .appendln("public Object get(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (JField field : message.fields()) {
            writer.formatln("case %d: return %s();",
                            field.id(), field.getter());
        }

        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendFieldDefaultConstants(IndentedPrintWriter writer, JMessage message)
            throws GeneratorException {
        boolean hasDefault = false;
        for (JField field : message.fields()) {
            if (field.hasDefault()) {
                Object defaultValue = helper.getDefaultValue(field.getPField());
                if (defaultValue != null) {
                    hasDefault = true;
                    writer.formatln("private final static %s %s = ",
                                    field.valueType(),
                                    field.kDefault())
                          .begin(DBL_INDENT);
                    appendTypedValue(writer, defaultValue, field.getPField().getDescriptor());
                    writer.append(';')
                          .end();
                }
            }
        }

        if (hasDefault) {
            writer.newline();
        }
    }

    private void appendFieldGetters(IndentedPrintWriter writer, JMessage message) throws GeneratorException {
        for (JField field : message.fields()) {
            if (message.isUnion()) {
                if (field.container()) {
                    writer.formatln("public int %s() {", field.counter())
                          .formatln("    return tUnionField == _Field.%s ? %s.size() : 0;",
                                    field.fieldEnum(), field.member())
                          .appendln('}')
                          .newline();
                } else if (field.alwaysPresent()) {
                    writer.formatln("public boolean %s() {", field.presence())
                          .formatln("    return tUnionField == _Field.%s;",
                                    field.fieldEnum())
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
                if (field.isRequired()) {
                    writer.formatln("public boolean %s() {", field.presence())
                          .begin()
                          .formatln("return true;")
                          .end()
                          .appendln('}')
                          .newline();
                } else {
                    writer.formatln("public boolean %s() {", field.presence())
                          .begin()
                          .formatln("return %s != %s;", field.member(), field.kDefault())
                          .end()
                          .appendln('}')
                          .newline();
                }
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
                writer.formatln("@JsonProperty(\"%s\")", field.name());
                if (field.binary()) {
                    writer.appendln("@JsonSerialize(using = BinaryJsonSerializer.class) ");
                }
            }
            writer.formatln("public %s %s() {",
                            field.valueType(),
                            field.getter());
            if (!field.container() && !field.alwaysPresent() && field.getPField().hasDefaultValue()) {
                writer.formatln("    return %s() ? %s : %s;",
                                field.presence(), field.member(), field.kDefault());
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

    private void appendTypedValue(IndentedPrintWriter writer,
                                  Object defaultValue,
                                  PDescriptor type)
            throws GeneratorException {
        switch (type.getType()) {
            case BOOL:
                writer.append(defaultValue.toString());
                break;
            case BYTE:
                writer.append("(byte)").append(defaultValue.toString());
                break;
            case I16:
                writer.append("(short)").append(defaultValue.toString());
                break;
            case I32:
                writer.append(defaultValue.toString());
                break;
            case I64:
                writer.append(defaultValue.toString()).append("L");
                break;
            case DOUBLE:
                writer.append(defaultValue.toString()).append("d");
                break;
            case BINARY:
                writer.append("Binary.wrap(new byte[]{");
                byte[] bytes = (byte[]) defaultValue;
                boolean first = true;
                for (byte b : bytes) {
                    if (first)
                        first = false;
                    else
                        writer.append(',');
                    writer.format("0x%02x", b);
                }
                writer.append("})");
                break;
            case STRING:
                try {
                    JsonWriter json = new JsonWriter(writer);
                    json.value(defaultValue.toString());
                    json.flush();
                } catch (JsonException je) {
                    throw new GeneratorException("Unable to format string value");
                }
                break;
            case ENUM:
                writer.format("%s.%s", helper.getInstanceClassName(type), defaultValue.toString());
                break;
            case MESSAGE:
                // writer.write("null");
                throw new GeneratorException("Message structs cannot have default values");
            case MAP:
            case LIST:
            case SET:
                // writer.write("null");
                throw new GeneratorException("Collections cannot have default value.");
        }
    }

    private void appendFieldDeclarations(IndentedPrintWriter writer, JMessage message) {
        for (JField field : message.fields()) {
            writer.formatln("private final %s %s;",
                            field.fieldType(), field.member());
        }
        if (message.isUnion()) {
            writer.appendln("private final _Field tUnionField;")
                  .newline();
        }
        writer.newline();
    }

    private void appendBuilderConstructor(IndentedPrintWriter writer, JMessage message) {
        writer.formatln("private %s(_Builder builder) {", message.instanceType())
              .begin();
        if (message.isUnion()) {
            writer.appendln("tUnionField = builder.tUnionField;")
                  .newline();

            for (JField field : message.fields()) {
                switch (field.type()) {
                    case LIST:
                        writer.formatln("%s = tUnionField == _Field.%s ? Collections.unmodifiableList(new %s<>(builder.%s)) : null;",
                                        field.member(),
                                        field.fieldEnum(),
                                        field.instanceType(),
                                        field.member());
                        break;
                    case SET:
                        writer.formatln("%s = tUnionField == _Field.%s ? Collections.unmodifiableSet(new %s<>(builder.%s)) : null;",
                                        field.member(),
                                        field.fieldEnum(),
                                        field.instanceType(),
                                        field.member());
                        break;
                    case MAP:
                        writer.formatln("%s = tUnionField == _Field.%s ? Collections.unmodifiableMap(new %s<>(builder.%s)) : null;",
                                        field.member(),
                                        field.fieldEnum(),
                                        field.instanceType(),
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
                switch (field.type()) {
                    case LIST:
                        writer.formatln("%s = Collections.unmodifiableList(new %s<>(builder.%s));",
                                        field.member(),
                                        field.instanceType(),
                                        field.member());
                        break;
                    case SET:
                        writer.formatln("%s = Collections.unmodifiableSet(new %s<>(builder.%s));",
                                        field.member(),
                                        field.instanceType(),
                                        field.member());
                        break;
                    case MAP:
                        writer.formatln("%s = Collections.unmodifiableMap(new %s<>(builder.%s));",
                                        field.member(),
                                        field.instanceType(),
                                        field.member());
                        break;
                    default:
                        writer.formatln("%s = builder.%s;", field.member(), field.member());
                        break;
                }
            }
        }
        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendCreateConstructor(IndentedPrintWriter writer, JMessage message) {
        if (message.isException()) {
            // TODO(steineldar): Handle constructing exception!
        } else if (message.isUnion()) {
            for (JField field : message.fields()) {
                writer.formatln("public %s %s(%s value) {",
                                message.instanceType(),
                                camelCase("with", field.name()),
                                field.valueType())
                      .begin()
                      .formatln("return new _Builder().%s(value).build();", field.setter())
                      .end()
                      .appendln("}")
                      .newline();
            }
        } else {
            if (options.jackson) {
                writer.appendln("@JsonCreator");
            }
            String spaces = message.instanceType().replaceAll("[\\S]", " ");
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
                    writer.format("@JsonProperty(\"%s\") ", field.name());
                    if (field.binary()) {
                        writer.append("@JsonDeserialize(using = BinaryJsonDeserializer.class) ");
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
                        writer.formatln("%s = Collections.unmodifiableList(new %s<>(%s));",
                                        field.member(), field.instanceType(), field.param());
                        break;
                    case SET:
                        writer.formatln("%s = Collections.unmodifiableSet(new %s<>(%s));",
                                        field.member(), field.instanceType(), field.param());
                        break;
                    case MAP:
                        writer.formatln("%s = Collections.unmodifiableMap(new %s<>(%s));",
                                        field.member(), field.instanceType(), field.param());
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

    private void appendClassDefinitionStart(IndentedPrintWriter writer, JMessage message) {
        writer.appendln("@SuppressWarnings(\"unused\")")
              .formatln("public class %s", message.instanceType())
              .begin(DBL_INDENT);
        if (message.variant().equals(PMessageVariant.EXCEPTION)) {
            writer.appendln("extends PException");
        }
        writer.formatln("implements %s<%s>, Serializable",
                        message.isUnion() ? "PUnion" : "PMessage",
                        message.instanceType());
        if (options.android) {
            writer.format(", Parcelable");
        }
        writer.append(" {")
              .end()  // double indent.
              .begin();

        writer.formatln("private final static long serialVersionUID = %dL;",
                        JUtils.generateSerialVersionUID(message.descriptor()))
              .newline();
    }

    private void appendClassDefinitionEnd(IndentedPrintWriter writer) {
        writer.end()
              .appendln('}')
              .newline();
    }

    private void addTypeImports(JHeader header, PDescriptor<?> descriptor) throws GeneratorException {
        switch (descriptor.getType()) {
            case ENUM:
            case MESSAGE:
                // Avoid never-ending recursion (with circular contained
                // structs) by stopping on already included structs and enums.
                header.include(helper.getQualifiedInstanceClassName(descriptor));
                header.include(helper.getQualifiedValueTypeName(descriptor));
                break;
            case LIST:
                PContainer<?, ?> lType = (PContainer<?, ?>) descriptor;
                header.include(java.util.Collection.class.getName());
                header.include(java.util.Collections.class.getName());
                header.include(PList.class.getName());
                header.include(helper.getQualifiedInstanceClassName(descriptor));
                header.include(helper.getQualifiedValueTypeName(descriptor));
                addTypeImports(header, lType.itemDescriptor());
                break;
            case SET:
                PContainer<?, ?> sType = (PContainer<?, ?>) descriptor;
                header.include(java.util.Collection.class.getName());
                header.include(java.util.Collections.class.getName());
                header.include(PSet.class.getName());
                header.include(helper.getQualifiedInstanceClassName(descriptor));
                header.include(helper.getQualifiedValueTypeName(descriptor));
                if (options.android) {
                    header.include(ArrayList.class.getName());
                }
                addTypeImports(header, sType.itemDescriptor());
                break;
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) descriptor;
                header.include(java.util.Collections.class.getName());
                header.include(PMap.class.getName());
                header.include(helper.getQualifiedInstanceClassName(descriptor));
                header.include(helper.getQualifiedValueTypeName(descriptor));
                header.include(helper.getQualifiedInstanceClassName(mType.itemDescriptor()));
                header.include(helper.getQualifiedInstanceClassName(mType.keyDescriptor()));
                addTypeImports(header, mType.keyDescriptor());
                addTypeImports(header, mType.itemDescriptor());
                break;
            case BINARY:
                header.include(Arrays.class.getName());
                header.include(PPrimitive.class.getName());
                header.include(Binary.class.getName());
                break;
            default:
                header.include(PPrimitive.class.getName());
                break;
        }
    }

    private void appendFileHeader(IndentedPrintWriter writer, JMessage message)
            throws GeneratorException, IOException {
        JHeader header = new JHeader(helper.getJavaPackage(message.descriptor()));
        header.include(java.io.Serializable.class.getName());
        header.include(PMessageBuilder.class.getName());
        header.include(PMessageBuilderFactory.class.getName());
        header.include(PField.class.getName());
        header.include(PTypeUtils.class.getName());
        header.include(PType.class.getName());
        header.include(PRequirement.class.getName());
        header.include(PDescriptorProvider.class.getName());
        header.include(PValueProvider.class.getName());
        header.include(PDescriptor.class.getName());
        if (!message.isUnion()) {
            header.include(BitSet.class.getName());
        }
        switch (message.variant()) {
            case STRUCT:
                header.include(PMessage.class.getName());
                header.include(PStructDescriptor.class.getName());
                header.include(PStructDescriptorProvider.class.getName());
                break;
            case UNION:
                header.include(PUnion.class.getName());
                header.include(PUnionDescriptor.class.getName());
                header.include(PUnionDescriptorProvider.class.getName());
                break;
            case EXCEPTION:
                header.include(PMessage.class.getName());
                header.include(PException.class.getName());
                header.include(PExceptionDescriptor.class.getName());
                header.include(PExceptionDescriptorProvider.class.getName());
                break;
        }
        for (JField field : message.fields()) {
            addTypeImports(header, field.getPField().getDescriptor());
            if (field.getPField().hasDefaultValue()) {
                header.include(PDefaultValueProvider.class.getName());
            }
            if (options.jackson && field.binary()) {
                header.include("com.fasterxml.jackson.databind.annotation.JsonDeserialize");
                header.include("com.fasterxml.jackson.databind.annotation.JsonSerialize");
                header.include("net.morimekta.providence.jackson.BinaryJsonDeserializer");
                header.include("net.morimekta.providence.jackson.BinaryJsonSerializer");
            }
        }
        if (options.android) {
            header.include("android.os.Parcel");
            header.include("android.os.Parcelable");
        }
        if (options.jackson) {
            header.include("com.fasterxml.jackson.annotation.JsonCreator");
            header.include("com.fasterxml.jackson.annotation.JsonProperty");
        }

        header.format(writer);
    }
}
