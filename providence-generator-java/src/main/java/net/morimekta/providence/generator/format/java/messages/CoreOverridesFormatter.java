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

import net.morimekta.providence.PException;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PDefaultValueProvider;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PValueProvider;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.serializer.json.JsonCompactible;
import net.morimekta.providence.serializer.json.JsonCompactibleDescriptor;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Locale;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class CoreOverridesFormatter implements MessageMemberFormatter {
    public static final String UNION_FIELD = "tUnionField";

    protected final IndentedPrintWriter writer;

    public CoreOverridesFormatter(IndentedPrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public Collection<String> getExtraImplements(JMessage<?> message) throws GeneratorException {
        ImmutableList.Builder<String> builder = ImmutableList.builder();

        builder.add(String.format(Locale.US, "%s<%s,%s._Field>",
                                  message.isUnion() ? PUnion.class.getName() : PMessage.class.getName(),
                                  message.instanceType(),
                                  message.instanceType()));

        if (message.isException()) {
            builder.add(PException.class.getName());
        }
        if (message.jsonCompactible()) {
            builder.add(JsonCompactible.class.getName());
        }

        return builder.build();
    }

    @Override
    public void appendFields(JMessage<?> message) throws GeneratorException {
        if (message.isUnion()) {
            writer.formatln("private transient final _Field %s;", UNION_FIELD)
                  .newline();
        }
    }

    @Override
    public void appendMethods(JMessage message) {
        appendPresence(message);
        appendGetter(message);
        appendJsonCompact(message);

        // Exception
        if (message.isException()) {
            appendPExceptionOverrides();
            appendExceptionOverrides(message);
        }

        if (message.isUnion()) {
            writer.appendln("@Override")
                  .appendln("public boolean unionFieldIsSet() {")
                  .formatln("    return %s != null;", UNION_FIELD)
                  .appendln('}')
                  .newline();

            writer.appendln("@Override")
                  .appendln("@" + Nonnull.class.getName())
                  .appendln("public _Field unionField() {")
                  .formatln("    if (%s == null) throw new IllegalStateException(\"No union field set in %s\");",
                            UNION_FIELD, message.descriptor().getQualifiedName())
                  .formatln("    return %s;", UNION_FIELD)
                  .appendln('}')
                  .newline();
        }
    }

    @Override
    public void appendExtraProperties(JMessage<?> message) throws GeneratorException {
        appendFieldEnum(message);
        appendDescriptor(message);
    }

    private void appendPExceptionOverrides() {
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

    private void appendExceptionOverrides(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public %s initCause(Throwable cause) {", message.instanceType())
              .formatln("    return (%s) super.initCause(cause);", message.instanceType())
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .formatln("public %s fillInStackTrace() {", message.instanceType())
              .formatln("    return (%s) super.fillInStackTrace();", message.instanceType())
              .appendln('}')
              .newline();
    }

    private void appendDescriptor(JMessage<?> message) throws GeneratorException {
        String typeClass = message.getDescriptorClass();
        String providerClass = message.getProviderClass();

        writer.appendln("@" + Nonnull.class.getName())
              .formatln("public static %s<%s,_Field> provider() {", providerClass, message.instanceType())
              .begin()
              .formatln("return new _Provider();")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("@" + Nonnull.class.getName())
              .formatln("public %s<%s,_Field> descriptor() {", typeClass, message.instanceType())
              .begin()
              .appendln("return kDescriptor;")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static final %s<%s,_Field> kDescriptor;", typeClass, message.instanceType())
              .newline();

        String jsonCompactibleDescriptor = "";
        if (message.jsonCompactible()) {
            jsonCompactibleDescriptor = " implements " + JsonCompactibleDescriptor.class.getName();
        }

        writer.formatln("private static class _Descriptor")
              .formatln("        extends %s<%s,_Field>%s {", typeClass, message.instanceType(), jsonCompactibleDescriptor)
              .begin()
              .appendln("public _Descriptor() {")
              .begin();
        writer.formatln("super(\"%s\", \"%s\", _Builder::new, %b);",
                        message.descriptor().getProgramName(),
                        message.descriptor().getName(),
                        message.descriptor().isSimple());

        writer.end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln(JAnnotation.NON_NULL)
              .appendln("public _Field[] getFields() {")
              .begin()
              .appendln("return _Field.values();")
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln(JAnnotation.NULLABLE)
              .appendln("public _Field findFieldByName(String name) {")
              .begin()
              .appendln("return _Field.findByName(name);")
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln(JAnnotation.NULLABLE)
              .appendln("public _Field findFieldById(int id) {")
              .begin()
              .appendln("return _Field.findById(id);")
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
    }


    private void appendFieldEnum( JMessage<?> message) throws GeneratorException {
        writer.formatln("public enum _Field implements %s {", PField.class.getName())
              .begin();

        for (JField field : message.numericalOrderFields()) {
            String provider = field.getProvider();
            String defValue = "null";
            if (field.field()
                     .hasDefaultValue()) {
                defValue = String.format(Locale.US, "new %s<>(%s)",
                                         PDefaultValueProvider.class.getName(),
                                         field.kDefault());
            }

            writer.formatln("%s(%d, %s.%s, \"%s\", %s, %s),",
                            field.fieldEnum(),
                            field.id(),
                            PRequirement.class.getName(),
                            field.field()
                                 .getRequirement()
                                 .name(),
                            field.name(),
                            provider,
                            defValue);
        }
        writer.appendln(';')
              .newline();

        writer.appendln("private final int mId;")
              .formatln("private final %s mRequired;", PRequirement.class.getName())
              .appendln("private final String mName;")
              .formatln("private final %s mTypeProvider;", PDescriptorProvider.class.getName())
              .formatln("private final %s<?> mDefaultValue;", PValueProvider.class.getName())
              .newline()
              .formatln("_Field(int id, %s required, String name, %s typeProvider, %s<?> defaultValue) {",
                        PRequirement.class.getName(),
                        PDescriptorProvider.class.getName(),
                        PValueProvider.class.getName())
              .begin()
              .appendln("mId = id;")
              .appendln("mRequired = required;")
              .appendln("mName = name;")
              .appendln("mTypeProvider = typeProvider;")
              .appendln("mDefaultValue = defaultValue;")
              .end()
              .appendln('}')
              .newline();
        writer.appendln("@Override")
              .appendln("public int getId() { return mId; }")
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
              .formatln("    return %s.asString(this);", PField.class.getName())
              .appendln('}')
              .newline();

        new BlockCommentBuilder(writer)
                .param_("id", "Field name")
                .return_("The identified field or null")
                .finish();
        writer.appendln("public static _Field findById(int id) {")
              .begin()
              .appendln("switch (id) {")
              .begin();
        for (JField field : message.declaredOrderFields()) {
            writer.formatln("case %d: return _Field.%s;", field.id(), field.fieldEnum());
        }
        writer.end()
              .appendln('}')
              .appendln("return null;")
              .end()
              .appendln('}')
              .newline();

        new BlockCommentBuilder(writer)
                .param_("name", "Field name")
                .return_("The named field or null")
                .finish();
        writer.appendln("public static _Field findByName(String name) {")
              .begin()
              .appendln("switch (name) {")
              .begin();
        for (JField field : message.declaredOrderFields()) {
            writer.formatln("case \"%s\": return _Field.%s;", field.name(), field.fieldEnum());
        }
        writer.end()
              .appendln('}')
              .appendln("return null;")
              .end()
              .appendln('}');

        new BlockCommentBuilder(writer)
                .param_("id", "Field name")
                .return_("The identified field")
                .throws_("IllegalArgumentException", "If no such field")
                .finish();
        writer.appendln("public static _Field fieldForId(int id) {")
              .begin()
              .appendln("_Field field = findById(id);")
              .appendln("if (field == null) {")
              .formatln("    throw new IllegalArgumentException(\"No such field id \" + id + \" in %s\");",
                        message.descriptor().getQualifiedName())
              .appendln("}")
              .appendln("return field;")
              .end()
              .appendln('}')
              .newline();

        new BlockCommentBuilder(writer)
                .param_("name", "Field name")
                .return_("The named field")
                .throws_("IllegalArgumentException", "If no such field")
                .finish();
        writer.appendln("public static _Field fieldForName(String name) {")
              .begin()
              .appendln("_Field field = findByName(name);")
              .appendln("if (field == null) {")
              .formatln("    throw new IllegalArgumentException(\"No such field \\\"\" + name + \"\\\" in %s\");",
                        message.descriptor().getQualifiedName())
              .appendln("}")
              .appendln("return field;")
              .end()
              .appendln('}')
              .newline();


        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendJsonCompact(JMessage<?> message) {
        if (!message.jsonCompactible()) {
            return;
        }

        writer.appendln("@Override")
              .appendln("public boolean jsonCompact() {")
              .begin();

        boolean hasCheck = false;
        boolean hasMissingVar = false;
        for (JField field : message.numericalOrderFields()) {
            if (!field.alwaysPresent()) {
                if (!hasMissingVar) {
                    writer.appendln("boolean missing = false;");
                }
                hasMissingVar = true;
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
    }

    private void appendGetter(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("@SuppressWarnings(\"unchecked\")")
              .appendln("public <T> T get(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        for (JField field : message.numericalOrderFields()) {
            if (field.isVoid()) {
                // Void fields have no value.
                writer.formatln("case %d: return %s() ? (T) Boolean.TRUE : null;",
                                field.id(), field.presence());
            } else if (field.isPrimitiveJavaValue()) {
                if (field.alwaysPresent()) {
                    writer.formatln("case %d: return (T) (%s) %s;",
                                    field.id(), field.instanceType(), field.member());
                } else {
                    writer.formatln("case %d: return (T) %s;",
                                    field.id(), field.member());
                }
            } else {
                writer.formatln("case %d: return (T) %s;",
                                field.id(), field.member());
            }
        }

        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendPresence(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public boolean has(int key) {")
              .begin()
              .appendln("switch(key) {")
              .begin();

        if (message.isUnion()) {
            for (JField field : message.numericalOrderFields()) {
                writer.formatln("case %s: return %s == _Field.%s;",
                                field.id(), UNION_FIELD, field.fieldEnum());
            }
        } else {
            for (JField field : message.numericalOrderFields()) {
                if (field.alwaysPresent()) {
                    writer.formatln("case %d: return true;", field.id());
                } else {
                    writer.formatln("case %d: return %s != null;", field.id(), field.member());
                }
            }
        }

        writer.appendln("default: return false;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }
}
