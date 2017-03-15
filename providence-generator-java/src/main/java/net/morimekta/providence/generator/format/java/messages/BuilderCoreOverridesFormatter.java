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
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.LinkedList;

import static net.morimekta.providence.generator.format.java.messages.CoreOverridesFormatter.UNION_FIELD;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class BuilderCoreOverridesFormatter implements MessageMemberFormatter {
    private final IndentedPrintWriter writer;
    private final JHelper             helper;

    public BuilderCoreOverridesFormatter(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    @Override
    public void appendConstructors(JMessage<?> message) throws GeneratorException {
        appendMerge(message);
    }

    @Override
    public void appendMethods(JMessage<?> message) throws GeneratorException {
        appendOverrideMutator(message);
        appendOverrideSetter(message);
        appendOverrideIsSet(message);
        appendOverrideIsModified(message);
        appendOverrideAdder(message);
        appendOverrideResetter(message);
        appendOverrideIsValid(message);
        appendOverrideValidate(message);
        appendOverrideDescriptor(message);
    }

    private void appendMerge(JMessage<?> message) throws GeneratorException {
        writer.appendln("@Override")
              .formatln("public _Builder merge(%s from) {", message.instanceType())
              .begin();

        if (message.isUnion()) {
            writer.appendln("if (from.unionField() == null) {")
                  .appendln("    return this;")
                  .appendln("}")
                  .newline()
                  .appendln("switch (from.unionField()) {")
                  .begin();

            for (JField field : message.declaredOrderFields()) {
                writer.formatln("case %s: {", field.fieldEnum())
                      .begin();

                switch (field.type()) {
                    case VOID:
                        // Void fields have no value.
                        writer.formatln("%s = _Field.%s;", UNION_FIELD, field.fieldEnum());
                        break;
                    case MESSAGE:
                        writer.formatln("if (%s == _Field.%s && %s != null) {",
                                        UNION_FIELD,
                                        field.fieldEnum(),
                                        field.member())
                              .formatln("    %s = %s.mutate().merge(from.%s()).build();",
                                        field.member(),
                                        field.member(),
                                        field.getter())
                              .appendln("} else {")
                              .formatln("    %s(from.%s());", field.setter(), field.getter())
                              .appendln('}');
                        break;
                    case SET:
                        writer.formatln("if (%s == _Field.%s) {", UNION_FIELD, field.fieldEnum())
                              .formatln("    %s.addAll(from.%s()):", field.member(), field.getter())
                              .appendln("} else {")
                              .formatln("    %s(from.%s());", field.setter(), field.getter())
                              .appendln('}');
                        break;
                    case MAP:
                        writer.formatln("if (%s == _Field.%s) {", UNION_FIELD, field.fieldEnum())
                              .formatln("    %s.putAll(from.%s()):", field.member(), field.getter())
                              .appendln("} else {")
                              .formatln("    %s(from.%s());", field.setter(), field.getter())
                              .appendln('}');
                        break;
                    default:
                        writer.formatln("%s(from.%s());", field.setter(), field.getter());
                        break;
                }

                writer.appendln("break;")
                      .end()
                      .appendln('}');
            }

            writer.end()
                  .appendln('}');
        } else {
            boolean first = true;
            for (JField field : message.declaredOrderFields()) {
                if (first) {
                    first = false;
                } else {
                    writer.newline();
                }

                if (!field.alwaysPresent()) {
                    writer.formatln("if (from.%s()) {", field.presence())
                          .begin();
                }

                writer.formatln("optionals.set(%d);", field.index());
                writer.formatln("modified.set(%d);", field.index());

                switch (field.type()) {
                    case MESSAGE:
                        // Message fields are merged.
                        writer.formatln("if (%s_builder != null) {", field.member())
                              .formatln("    %s_builder.merge(from.%s());", field.member(), field.getter())
                              .formatln("} else if (%s != null) {", field.member())
                              .formatln("    %s_builder = %s.mutate().merge(from.%s());",
                                        field.member(),
                                        field.member(),
                                        field.getter())
                              .formatln("    %s = null;", field.member())
                              .appendln("} else {")
                              .formatln("    %s = from.%s();", field.member(), field.getter())
                              .appendln('}');
                        break;
                    case SET:
                        writer.formatln("%s.addAll(from.%s());", field.member(), field.getter());
                        break;
                    case MAP:
                        writer.formatln("%s.putAll(from.%s());", field.member(), field.getter());
                        break;
                    case LIST:
                        writer.formatln("%s.clear();", field.member());
                        writer.formatln("%s.addAll(from.%s());", field.member(), field.getter());
                        break;
                    default:
                        writer.formatln("%s = from.%s();", field.member(), field.getter());
                        break;
                }

                if (!field.alwaysPresent()) {
                    writer.end()
                          .appendln('}');
                }
            }
        }

        writer.appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendOverrideMutator(JMessage<?> message) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("@SuppressWarnings(\"unchecked\")")
              .formatln("public %s mutator(int key) {", PMessageBuilder.class.getName())
              .begin()
              .appendln("switch (key) {")
              .begin();
        message.numericalOrderFields()
               .stream()
               .filter(field -> field.type() == PType.MESSAGE)
               .forEachOrdered(field -> writer.formatln("case %d: return %s();",
                                                        field.id(),
                                                        Strings.camelCase("mutable", field.name())));
        writer.appendln("default: throw new IllegalArgumentException(\"Not a message field ID: \" + key);")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }

    private void appendOverrideSetter(JMessage<?> message) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("@SuppressWarnings(\"unchecked\")")
              .appendln("public _Builder set(int key, Object value) {")
              .begin()
              .appendln("if (value == null) return clear(key);")
              .appendln("switch (key) {")
              .begin();
        for (JField field : message.numericalOrderFields()) {
            if (field.isVoid()) {
                // Void fields have no value.
                writer.formatln("case %d: %s(); break;", field.id(), field.setter(), field.valueType());
            } else {
                writer.formatln("case %d: %s((%s) value); break;", field.id(), field.setter(), field.valueType());
            }
        }
        writer.appendln("default: break;")
              .end()
              .appendln('}')
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendOverrideIsSet(JMessage<?> message) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("public boolean isSet(int key) {")
              .begin()
              .appendln("switch (key) {")
              .begin();
        if (message.isUnion()) {
            for (JField field : message.numericalOrderFields()) {
                writer.formatln("case %d: return %s == _Field.%s;", field.id(), UNION_FIELD, field.fieldEnum());
            }
        } else {
            for (JField field : message.numericalOrderFields()) {
                writer.formatln("case %d: return optionals.get(%d);", field.id(), field.index());
            }
        }
        writer.appendln("default: break;")
              .end()
              .appendln('}')
              .appendln("return false;")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendOverrideIsModified(JMessage<?> message) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("public boolean isModified(int key) {")
              .begin();
        if (!message.isUnion()) {
            writer.appendln("switch (key) {")
                  .begin();
            for (JField field : message.numericalOrderFields()) {
                writer.formatln("case %d: return modified.get(%d);", field.id(), field.index());
            }
            writer.appendln("default: break;")
                  .end()
                  .appendln('}')
                  .appendln("return false;");
        } else {
            writer.appendln("return modified;");
        } writer.end()
                .appendln('}')
                .newline();
    }

    private void appendOverrideAdder(JMessage<?> message) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("public _Builder addTo(int key, Object value) {")
              .begin()
              .appendln("switch (key) {")
              .begin();
        message.numericalOrderFields()
               .stream()
               .filter(field -> field.type() == PType.LIST || field.type() == PType.SET)
               .forEachOrdered(field -> {
                   PContainer<?> ct = (PContainer<?>) field.getPField()
                                                           .getDescriptor();
                   PDescriptor itype = ct.itemDescriptor();
                   writer.formatln("case %d: %s((%s) value); break;",
                                   field.id(),
                                   field.adder(),
                                   helper.getValueType(itype));
               });
        writer.appendln("default: break;")
              .end()
              .appendln('}')
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendOverrideResetter(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public _Builder clear(int key) {")
              .begin()
              .appendln("switch (key) {")
              .begin();
        for (JField field : message.numericalOrderFields()) {
            writer.formatln("case %d: %s(); break;", field.id(), field.resetter());
        }
        writer.appendln("default: break;")
              .end()
              .appendln('}')
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendOverrideIsValid(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public boolean valid() {")
              .begin();
        if (message.isUnion()) {
            writer.formatln("if (%s == null) {", UNION_FIELD)
                  .appendln("    return false;")
                  .appendln('}')
                  .newline()
                  .formatln("switch (%s) {", UNION_FIELD)
                  .begin();
            message.numericalOrderFields()
                   .stream()
                   .filter(field -> !field.alwaysPresent())
                   .forEachOrdered(field -> {
                       if (field.isVoid()) {
                           // Void fields have no value.
                           writer.formatln("case %s: return true;", field.fieldEnum());
                       } else if (field.type() == PType.MESSAGE) {
                           writer.formatln("case %s: return %s != null || %s_builder != null;",
                                           field.fieldEnum(),
                                           field.member(),
                                           field.member());
                       } else {
                           writer.formatln("case %s: return %s != null;", field.fieldEnum(), field.member());
                       }
                   });
            writer.appendln("default: return true;")
                  .end()
                  .appendln('}');
        } else {
            writer.appendln("return ")
                  .begin("       ");
            boolean first = true;
            for (JField field : message.declaredOrderFields()) {
                if (field.isRequired()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(" &&")
                              .appendln("");
                    }
                    writer.format("optionals.get(%d)", field.index());
                }
            }
            if (first) {
                writer.append("true");
            }
            writer.end()  // alignment indent
                  .append(';');
        }
        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendOverrideValidate(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public void validate() {")
              .begin();
        if (message.isUnion()) {
            writer.appendln("if (!valid()) {")
                  .formatln("    throw new %s(\"No union field set in %s\");",
                            IllegalStateException.class.getName(),
                            message.descriptor()
                                   .getQualifiedName())
                  .appendln("}");
        } else {
            boolean hasRequired = message.declaredOrderFields()
                                         .stream()
                                         .anyMatch(JField::isRequired);
            if (hasRequired) {
                writer.appendln("if (!valid()) {")
                      .begin()
                      .formatln("%s<String> missing = new %s<>();",
                                LinkedList.class.getName(),
                                LinkedList.class.getName())
                      .newline();

                message.declaredOrderFields()
                       .stream()
                       .filter(JField::isRequired)
                       .forEachOrdered(field -> writer.formatln("if (!optionals.get(%d)) {", field.index())
                                                      .formatln("    missing.add(\"%s\");", field.name())
                                                      .appendln("}")
                                                      .newline());

                writer.formatln("throw new %s(", IllegalStateException.class.getName())
                      .appendln("        \"Missing required fields \" +")
                      .appendln("        String.join(\",\", missing) +")
                      .formatln("        \" in message %s\");",
                                message.descriptor()
                                       .getQualifiedName())
                      .end()
                      .appendln("}");
            }
        }
        writer.end()
              .appendln('}')
              .newline();

    }

    private void appendOverrideDescriptor(JMessage<?> message) throws GeneratorException {
        String typeClass = message.getDescriptorClass();
        writer.appendln("@Override")
              .formatln("public %s<%s,_Field> descriptor() {", typeClass, message.instanceType())
              .begin()
              .appendln("return kDescriptor;")
              .end()
              .appendln('}')
              .newline();

    }
}
