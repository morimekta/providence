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

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.io.IndentedPrintWriter;

import java.util.BitSet;
import java.util.Collection;
import java.util.Objects;

import static net.morimekta.providence.generator.format.java.messages.CoreOverridesFormatter.UNION_FIELD;
import static net.morimekta.util.Strings.camelCase;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class BuilderCommonMemberFormatter implements MessageMemberFormatter {
    protected final IndentedPrintWriter        writer;
    protected final JHelper                    helper;

    public BuilderCommonMemberFormatter(IndentedPrintWriter writer,
                                        JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    @Override
    public void appendClassAnnotations(JMessage<?> message) throws GeneratorException {
        if (JAnnotation.isDeprecated(message.descriptor())) {
            writer.appendln(JAnnotation.DEPRECATED);
        }
    }

    @Override
    public void appendConstructors(JMessage<?> message) throws GeneratorException {
        appendDefaultConstructor(message);
        appendMutateConstructor(message);
    }

    @Override
    public void appendFields(JMessage<?> message) throws GeneratorException {
        if (message.isUnion()) {
            appendUnionFields(message);
        } else {
            if (message.isException()) {
                appendExceptionFields(message);
            }
            appendStructFields(message);
        }
        appendModifiedFields(message);

        for (JField field : message.declaredOrderFields()) {
            // Void fields have no value.
            if (field.isVoid()) {
                continue;
            }
            writer.formatln("private %s %s;", field.fieldType(), field.member());
            if (field.type() == PType.MESSAGE) {
                writer.formatln("private %s._Builder %s_builder;", field.builderFieldType(), field.member());
            }
        }
        if (message.declaredOrderFields()
                   .size() > 0) {
            writer.newline();
        }
    }

    @Override
    public void appendMethods(JMessage<?> message) throws GeneratorException {
        for (JField field : message.declaredOrderFields()) {
            appendSetter(message, field);
            if (field.container()) {
                appendAdder(message, field);
            }
            appendIsSet(message, field);
            if( !message.isUnion() ) {
                appendIsModified(message, field);
            }
            appendResetter(message, field);
            appendMutableGetters(message, field);
        }
        if( message.isUnion() ) {
            appendIsUnionModified(message);
        }
        if (message.isException()) {
            appendInitCause(message);
        }
        appendEquals(message);
        appendHashCode(message);
    }

    @Override
    public void appendExtraProperties(JMessage<?> message) throws GeneratorException {
        appendBuilderBuild(message);
    }

    private void appendBuilderBuild(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public %s build() {", message.instanceType())
              .begin();
        if (message.isException()) {
            writer.formatln("%s e = new %s(this);", message.instanceType(), message.instanceType())
                  .newline();

            writer.appendln("try {")
                  .appendln("    StackTraceElement[] stackTrace = e.getStackTrace();")
                  .appendln("    StackTraceElement[] subTrace = new StackTraceElement[stackTrace.length - 1];")
                  .appendln("    System.arraycopy(stackTrace, 1, subTrace, 0, subTrace.length);")
                  .appendln("    e.setStackTrace(subTrace);")
                  .appendln("} catch (Throwable ignored) {")
                  .appendln("}")
                  .newline();

            writer.appendln("if (cause != null) {")
                  .appendln("    e.initCause(cause);")
                  .appendln("}")
                  .newline()
                  .appendln("return e;");
        } else {
            writer.formatln("return new %s(this);", message.instanceType());
        }
        writer.end()
              .appendln('}');
    }

    private void appendEquals(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public boolean equals(Object o) {")
              .begin()
              .appendln("if (o == this) return true;")
              .appendln("if (o == null || !o.getClass().equals(getClass())) return false;");
        if (message.numericalOrderFields()
                   .size() > 0) {
            writer.formatln("%s._Builder other = (%s._Builder) o;", message.instanceType(), message.instanceType())
                  .appendln("return ");
            if (message.isUnion()) {
                writer.format("%s.equals(%s, other.%s)",
                              Objects.class.getName(),
                              UNION_FIELD,
                              UNION_FIELD);
            } else {
                writer.format("%s.equals(optionals, other.optionals)",
                              Objects.class.getName());
            }

            for (JField field : message.declaredOrderFields()) {
                if (field.isVoid()) continue;

                writer.append(" &&")
                      .appendln("       ");
                if (field.type() == PType.MESSAGE) {
                    writer.format("%s.equals(%s(), other.%s())",
                                  Objects.class.getName(),
                                  field.getter(),
                                  field.getter());
                } else {
                    writer.format("%s.equals(%s, other.%s)",
                                  Objects.class.getName(),
                                  field.member(),
                                  field.member());
                }
            }
            writer.append(';');
        } else {
            writer.appendln("return true;");
        }
        writer.end()
              .appendln("}")
              .newline();
    }

    private void appendHashCode(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public int hashCode() {")
              .begin()
              .formatln("return %s.hash(",
                        Objects.class.getName())
              .begin("        ")
              .formatln("%s.class", message.instanceType());
        if (!message.isUnion()) {
            writer.append(", optionals");
        }
        message.numericalOrderFields()
               .stream()
               .filter(field -> !field.isVoid())
               .forEach(field -> {
                   if (field.type() == PType.MESSAGE) {
                       writer.append(",");
                       writer.formatln("_Field.%s, %s()", field.fieldEnum(), field.getter());
                   } else {
                       writer.append(",");
                       writer.formatln("_Field.%s, %s", field.fieldEnum(), field.member());
                   }
               });

        writer.end()
              .append(");")
              .end()
              .appendln("}")
              .newline();
    }

    private void appendUnionFields(JMessage<?> message) {
        writer.formatln("private _Field %s;", UNION_FIELD)
              .newline();
    }

    private void appendExceptionFields(JMessage<?> message) {
        writer.formatln("private Throwable cause;");
    }

    private void appendStructFields(JMessage<?> message) {
        writer.formatln("private %s optionals;",
                        BitSet.class.getName());
    }

    private void appendModifiedFields(JMessage<?> message) {
        if (!message.isUnion()) {
            writer.formatln("private %s modified;", BitSet.class.getName())
                  .newline();
        } else {
            writer.formatln("private %s modified;", boolean.class.getName())
                  .newline();
        }
    }

    private void appendDefaultConstructor(JMessage<?> message) throws GeneratorException {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Make a " + message.descriptor().getQualifiedName() + " builder.")
               .finish();
        writer.appendln("public _Builder() {")
              .begin();
        if (!message.isUnion()) {
            writer.formatln("optionals = new %s(%d);",
                            BitSet.class.getName(),
                            message.declaredOrderFields()
                                   .size());
            writer.formatln("modified = new %s(%d);",
                            BitSet.class.getName(),
                            message.declaredOrderFields()
                                   .size());
        } else {
            writer.appendln("modified = false;");
        }

        for (JField field : message.declaredOrderFields()) {
            if (field.alwaysPresent()) {
                writer.formatln("%s = %s;", field.member(), field.kDefault());
            }
        }
        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendMutateConstructor(JMessage<?> message) {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Make a mutating builder off a base " + message.descriptor().getQualifiedName() + ".")
               .newline()
               .param_("base", "The base " + message.descriptor().getName())
               .finish();
        writer.formatln("public _Builder(%s base) {", message.instanceType())
              .begin()
              .appendln("this();")
              .newline();
        if (message.isUnion()) {
            writer.formatln("%s = base.%s;", UNION_FIELD, UNION_FIELD)
                  .newline();
        }
        for (JField field : message.declaredOrderFields()) {
            boolean checkPresence = message.isUnion() ? field.container() : !field.alwaysPresent();
            if (checkPresence) {
                writer.formatln("if (base.%s()) {", field.presence())
                      .begin();
            }
            if (!message.isUnion()) {
                writer.formatln("optionals.set(%d);", field.index());
            }
            if (field.type() != PType.VOID) {
                writer.formatln("%s = base.%s;", field.member(), field.member());
            }
            if (checkPresence) {
                writer.end()
                      .appendln('}');
            }
        }

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendSetter(JMessage message, JField field) throws GeneratorException {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        if (field.hasComment()) {
            comment.comment(field.comment());
        } else {
            comment.comment("Sets the value of " + field.name() + ".");
        }
        comment.newline();
        if (!field.isVoid()) {
            comment.param_("value", "The new value");
        }
        comment.return_("The builder")
               .finish();
        if (JAnnotation.isDeprecated(field)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }
        writer.appendln(JAnnotation.NON_NULL);
        if (field.isVoid()) {
            // Void fields have no value.
            writer.formatln("public _Builder %s() {", field.setter());
        } else if (field.type() == PType.SET || field.type() == PType.LIST) {
            PContainer<?> cType = (PContainer<?>) field.field()
                                                       .getDescriptor();
            String iType = helper.getFieldType(cType.itemDescriptor());
            writer.formatln("public _Builder %s(%s<%s> value) {",
                            field.setter(), Collection.class.getName(), iType);
        } else {
            writer.formatln("public _Builder %s(%s value) {", field.setter(), field.valueType());
        }
        writer.begin();
        if (!field.isPrimitiveJavaValue()) {
            writer.formatln("if (value == null) {")
                  .formatln("    return %s();", field.resetter())
                  .appendln('}')
                  .newline();
        }

        if (message.isUnion()) {
            writer.formatln("%s = _Field.%s;", UNION_FIELD, field.fieldEnum());
            writer.appendln("modified = true;");
        } else {
            writer.formatln("optionals.set(%d);", field.index());
            writer.formatln("modified.set(%d);", field.index());
        }

        switch (field.type()) {
            case VOID:
                // Void fields have no value.
                break;
            case SET:
            case LIST:
            case MAP:
                writer.formatln("%s = %s;", field.member(), field.fieldInstanceCopy("value"));
                break;
            case MESSAGE:
                writer.formatln("%s = value;", field.member());
                writer.formatln("%s_builder = null;", field.member());
                break;
            default:
                writer.formatln("%s = value;", field.member());
                break;
        }

        writer.appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }

    private void appendAdder(JMessage message, JField field) throws GeneratorException {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);

        if (field.hasComment()) {
            comment.comment(field.comment());
        } else if (field.type() == PType.MAP) {
            comment.comment("Adds a mapping to " + field.name() + ".");
        } else {
            comment.comment("Adds entries to " + field.name() + ".");
        }
        comment.newline();

        if (field.type() == PType.MAP) {
            comment.param_("key", "The inserted key")
                   .param_("value", "The inserted value");
        } else {
            comment.param_("values", "The added value");
        }
        comment.return_("The builder")
               .finish();

        if (JAnnotation.isDeprecated(field)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }
        writer.appendln(JAnnotation.NON_NULL);

        switch (field.type()) {
            case MAP: {
                PMap<?, ?> mType = (PMap<?, ?>) field.field()
                                                     .getDescriptor();
                String mkType = helper.getValueType(mType.keyDescriptor());
                String miType = helper.getValueType(mType.itemDescriptor());

                writer.formatln("public _Builder %s(%s key, %s value) {", field.adder(), mkType, miType)
                      .begin();
                if (message.isUnion()) {
                    writer.formatln("%s = _Field.%s;", UNION_FIELD, field.fieldEnum());
                    writer.appendln("modified = true;");
                } else {
                    writer.formatln("optionals.set(%d);", field.index());
                    writer.formatln("modified.set(%d);", field.index());
                }
                writer.formatln("%s().put(key, value);", field.mutable())
                      .appendln("return this;")
                      .end()
                      .appendln('}')
                      .newline();

                break;
            }
            case SET:
            case LIST: {
                PContainer<?> lType = (PContainer<?>) field.field()
                                                                 .getDescriptor();
                String liType = helper.getValueType(lType.itemDescriptor());

                writer.formatln("public _Builder %s(%s... values) {", field.adder(), liType)
                      .begin();
                if (message.isUnion()) {
                    writer.formatln("%s = _Field.%s;", UNION_FIELD, field.fieldEnum());
                    writer.appendln("modified = true;");
                } else {
                    writer.formatln("optionals.set(%d);", field.index());
                    writer.formatln("modified.set(%d);", field.index());
                }
                writer.formatln("%s _container = %s();", field.fieldType(), field.mutable())
                      .formatln("for (%s item : values) {", liType)
                      .begin()
                      .appendln("_container.add(item);")
                      .end()
                      .appendln('}')
                      .appendln("return this;")
                      .end()
                      .appendln('}')
                      .newline();

                break;
            }
        }
    }

    private void appendIsSet(JMessage message, JField field) {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        if (field.hasComment()) {
            comment.comment(field.comment());
        } else {
            comment.comment("Checks for presence of the " + field.name() + " field.");
        }

        comment.newline()
               .return_(String.format("True if %s has been set.", field.name()))
               .finish();
        writer.formatln("public boolean %s() {", field.isSet())
              .begin();

        if (message.isUnion()) {
            writer.formatln("return %s == _Field.%s;", UNION_FIELD, field.fieldEnum());
        } else {
            writer.formatln("return optionals.get(%d);", field.index());
        }
        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendIsModified(JMessage message, JField field) {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        if (field.hasComment()) {
            comment.comment(field.comment());
        } else {
            comment.comment("Checks if " + field.name() + " has been modified since the _Builder was created.");
        }

        comment.newline()
               .return_(String.format("True if %s has been modified.", field.name()))
               .finish();
        writer.formatln("public boolean %s() {", field.isModified())
              .begin();
        writer.formatln("return modified.get(%d);", field.index());
        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendIsUnionModified(JMessage message) {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        comment.comment("Checks if " + message.descriptor().getName() + " has been modified since the _Builder " +
                        "was created.");

        comment.newline()
               .return_(String.format("True if %s has been modified.", message.descriptor().getName()))
               .finish();
        writer.formatln("public boolean %s() {", "isUnionModified")
              .begin();
        writer.appendln("return modified;");
        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendResetter(JMessage message, JField field) {
        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        if (field.hasComment()) {
            comment.comment(field.comment());
        } else {
            comment.comment("Clears the " + field.name() + " field.");
        }
        comment.newline()
               .return_("The builder")
               .finish();
        writer.appendln(JAnnotation.NON_NULL);
        writer.formatln("public _Builder %s() {", field.resetter())
              .begin();

        if (message.isUnion()) {
            writer.formatln("if (%s == _Field.%s) %s = null;", UNION_FIELD, field.fieldEnum(), UNION_FIELD);
            writer.appendln("modified = true;");
        } else {
            writer.formatln("optionals.clear(%d);", field.index());
            writer.formatln("modified.set(%d);", field.index());
        }

        // Void fields have no value.
        if (!field.isVoid()) {
            if (field.alwaysPresent()) {
                writer.formatln("%s = %s;", field.member(), field.kDefault());
            } else {
                writer.formatln("%s = null;", field.member());
                if (field.type() == PType.MESSAGE) {
                    writer.formatln("%s_builder = null;", field.member());
                }
            }
        }

        writer.appendln("return this;")
              .end()
              .appendln('}')
              .newline();
    }

    /**
     * Get mutable values. This returns message builders for messages, collection
     * builders for collections, and the normal immutable value for everything
     * else.
     *
     * @param message The message to get mutable getters for.
     * @param field The field to generate getter for.
     */
    private void appendMutableGetters(JMessage message, JField field) throws GeneratorException {
        if (field.field().getDescriptor() instanceof PPrimitive ||
            field.type() == PType.ENUM) {
            // The other fields will have ordinary non-mutable getters.
            appendGetter(field);
            return;
        }

        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        if (field.hasComment()) {
            comment.comment(field.comment());
        } else {
            comment.comment("Gets the builder for the contained " + field.name() + ".");
        }
        comment.newline()
               .return_("The field builder")
               .finish();
        if (JAnnotation.isDeprecated(field)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }
        writer.appendln(JAnnotation.NON_NULL);
        switch (field.type()) {
            case MESSAGE: {
                writer.formatln("public %s._Builder %s() {", field.instanceType(), field.mutable())
                      .begin();

                if (message.isUnion()) {
                    writer.formatln("if (%s != _Field.%s) {", UNION_FIELD, field.fieldEnum())
                          .formatln("    %s();", field.resetter())
                          .appendln('}')
                          .formatln("%s = _Field.%s;", UNION_FIELD, field.fieldEnum());
                    writer.appendln("modified = true;");
                } else {
                    writer.formatln("optionals.set(%d);", field.index());
                    writer.formatln("modified.set(%d);", field.index());
                }

                writer.newline()
                      .formatln("if (%s != null) {", field.member())
                      .formatln("    %s_builder = %s.mutate();", field.member(), field.member())
                      .formatln("    %s = null;", field.member())
                      .formatln("} else if (%s_builder == null) {", field.member())
                      .formatln("    %s_builder = %s.builder();", field.member(), field.instanceType())
                      .appendln('}')
                      .formatln("return %s_builder;", field.member());

                writer.end()
                      .appendln('}')
                      .newline();

                // Also add "normal" getter for the message field, which will
                // return the message or the builder dependent on which is set.
                // It will not change the state of the builder.
                comment = new BlockCommentBuilder(writer);
                if (field.hasComment()) {
                    comment.comment(field.comment());
                } else {
                    comment.comment("Gets the value for the contained " + field.name() + ".");
                }
                comment.newline()
                       .return_("The field value")
                       .finish();
                if (JAnnotation.isDeprecated(field)) {
                    writer.appendln(JAnnotation.DEPRECATED);
                }
                writer.formatln("public %s %s() {", field.instanceType(), field.getter())
                      .begin();

                if (message.isUnion()) {
                    writer.formatln("if (%s != _Field.%s) {", UNION_FIELD, field.fieldEnum())
                          .formatln("    return null;")
                          .appendln('}');
                }

                writer.newline()
                      .formatln("if (%s_builder != null) {", field.member())
                      .formatln("    return %s_builder.build();", field.member())
                      .appendln('}')
                      .formatln("return %s;", field.member());

                writer.end()
                      .appendln('}')
                      .newline();
                break;
            }
            case SET:
            case LIST:
            case MAP:
                writer.formatln("public %s %s() {", field.fieldType(), field.mutable())
                      .begin();

                if (message.isUnion()) {
                    writer.formatln("if (%s != _Field.%s) {", UNION_FIELD, field.fieldEnum())
                          .formatln("    %s();", field.resetter())
                          .appendln('}')
                          .formatln("%s = _Field.%s;", UNION_FIELD, field.fieldEnum());
                    writer.appendln("modified = true;");
                } else {
                    writer.formatln("optionals.set(%d);", field.index());
                    writer.formatln("modified.set(%d);", field.index());
                }
                writer.newline()
                      .formatln("if (%s == null) {", field.member())
                      .formatln("    %s = new %s<>();", field.member(), field.builderMutableType())
                      .formatln("} else if (!(%s instanceof %s)) {", field.member(), field.builderMutableType())
                      .formatln("    %s = new %s<>(%s);", field.member(), field.builderMutableType(), field.member())
                      .appendln("}");

                writer.formatln("return %s;", field.member());

                writer.end()
                      .appendln('}')
                      .newline();
                break;
            default:
                throw new GeneratorException("Unexpected field type: " + field.type());
        }
    }

    private void appendGetter(JField field) {
        if (field.isVoid()) {
            return;
        }

        BlockCommentBuilder comment = new BlockCommentBuilder(writer);
        if (field.hasComment()) {
            comment.comment(field.comment());
        } else {
            comment.comment("Gets the value of the contained " + field.name() + ".");
        }
        comment.newline()
               .return_("The field value")
               .finish();
        if (JAnnotation.isDeprecated(field)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }

        // Builder getters are *always* get*, never is* (e.g. for bool)
        writer.formatln("public %s %s() {",
                        field.valueType(), camelCase("get", field.name()))
              .begin();

        if ((field.isPrimitiveJavaValue() ||
             field.field().hasDefaultValue()) &&
            !field.alwaysPresent()){
            writer.formatln("return %s() ? %s : %s;",
                            field.isSet(),
                            field.member(),
                            field.kDefault());
        } else {
            writer.formatln("return %s;", field.member());
        }

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendInitCause(JMessage<?> message) {
        new BlockCommentBuilder(writer)
                .comment("Initializes the cause of the " + message.descriptor().getQualifiedName())
                .newline()
                .param_("cause", "The cause")
                .return_("Builder instance")
                .finish();
        writer.appendln(JAnnotation.NON_NULL);
        writer.appendln("public _Builder initCause(Throwable cause) {")
              .appendln("    this.cause = cause;")
              .appendln("    return this;")
              .appendln("}")
              .newline();
    }
}
