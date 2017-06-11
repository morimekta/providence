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
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.util.Strings;
import net.morimekta.util.io.IndentedPrintWriter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Strings.isNullOrEmpty;
import static net.morimekta.providence.generator.format.java.messages.CoreOverridesFormatter.UNION_FIELD;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class CommonOverridesFormatter implements MessageMemberFormatter {
    protected final IndentedPrintWriter writer;

    public CommonOverridesFormatter(IndentedPrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public Collection<String> getExtraImplements(JMessage<?> message) throws GeneratorException {
        LinkedList<String> impl = new LinkedList<>();

        impl.add(Comparable.class.getSimpleName() + "<" + message.instanceType() + ">");
        if (!message.isException()) {
            impl.add(Serializable.class.getName());
        }

        if (!isNullOrEmpty(message.extraImplements())) {
            String[] extras = message.extraImplements().split("[,]");
            // TODO: Figure out if we need to have a filter here.
            Arrays.stream(extras)
                  .map(String::trim)
                  .forEachOrdered(impl::add);
        }

        return impl;
    }

    private String caseFieldConstant(JField field) {
        return field.fieldEnum();
    }

    @Override
    public void appendFields(JMessage<?> message) throws GeneratorException {
        writer.appendln("private volatile int tHashCode;")
              .newline();
    }

    @Override
    public void appendMethods(JMessage message) {
        // Object
        appendEquals(message);
        appendHashCode(message);
        appendToString(message);
        // Stringable
        appendAsString(message);
        // Comparable
        appendCompareTo(message);
    }

    private void appendEquals(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public boolean equals(Object o) {")
              .begin()
              .appendln("if (o == this) return true;")
              .appendln("if (o == null || !o.getClass().equals(getClass())) return false;");
        if (message.numericalOrderFields()
                   .size() > 0) {
            boolean first = true;
            writer.formatln("%s other = (%s) o;", message.instanceType(), message.instanceType())
                  .appendln("return ");
            if (message.isUnion()) {
                writer.format("%s.equals(%s, other.%s)",
                              Objects.class.getName(),
                              UNION_FIELD,
                              UNION_FIELD);
                first = false;
            }
            for (JField field : message.declaredOrderFields()) {
                if (field.isVoid()) continue;

                if (first) {
                    first = false;
                } else {
                    writer.append(" &&")
                          .appendln("       ");
                }
                writer.format("%s.equals(%s, other.%s)",
                              Objects.class.getName(),
                              field.member(), field.member());
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
              .appendln("if (tHashCode == 0) {")
              .begin()
              .formatln("tHashCode = %s.hash(",
                        Objects.class.getName())
              .begin("        ")
              .formatln("%s.class", message.instanceType());
        message.numericalOrderFields()
               .stream()
               .filter(field -> !field.isVoid())
               .forEach(field -> {
                   writer.append(",");
                   writer.formatln("_Field.%s, %s", field.fieldEnum(), field.member());
               });

        writer.end()
              .append(");")
              .end()
              .appendln('}')
              .appendln("return tHashCode;")
              .end()
              .appendln("}")
              .newline();
    }

    private void appendToString(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln("public String toString() {")
              .begin()
              .formatln("return \"%s\" + asString();",
                        message.descriptor()
                               .getQualifiedName())
              .end()
              .appendln('}')
              .newline();
    }

    private void appendAsString(JMessage<?> message) {
        writer.appendln("@Override")
              .appendln(JAnnotation.NON_NULL)
              .appendln("public String asString() {")
              .begin()
              .appendln("StringBuilder out = new StringBuilder();")
              .appendln("out.append(\"{\");")
              .newline();

        if (message.isUnion()) {
            writer.formatln("switch (%s) {", UNION_FIELD)
                  .begin();

            for (JField field : message.declaredOrderFields()) {
                writer.formatln("case %s: {", caseFieldConstant(field))
                      .begin()
                      .formatln("out.append(\"%s:\")", field.name());

                switch (field.type()) {
                    case VOID:
                        writer.formatln("   .append(\"true\");");
                        break;
                    case BOOL:
                    case I32:
                    case I64:
                        writer.formatln("   .append(%s);", field.member());
                        break;
                    case BYTE:
                    case I16:
                        writer.formatln("   .append((int) %s);", field.member());
                        break;
                    case DOUBLE:
                    case SET:
                    case LIST:
                    case MAP:
                        writer.formatln("   .append(%s.asString(%s));",
                                        Strings.class.getName(),
                                        field.member());
                        break;
                    case STRING:
                        writer.formatln("   .append('\\\"').append(%s.escape(%s)).append('\\\"');",
                                        Strings.class.getName(),
                                        field.member());
                        break;
                    case BINARY:
                        writer.formatln("   .append(\"b64(\").append(%s.toBase64()).append(')');", field.member());
                        break;
                    case MESSAGE:
                    case ENUM:
                        writer.formatln("   .append(%s.asString());", field.member());
                        break;
                    default:
                        writer.formatln("   .append(%s.toString());", field.member());
                        break;
                }

                writer.appendln("break;")
                      .end()
                      .appendln('}');
            }
            writer.end()
                  .appendln('}');
        } else {
            boolean hasFirstFlag = false;
            boolean allowSeparator = false;
            boolean requireSeparator = false;

            List<JField> fieldList = message.declaredOrderFields();

            JField[] fields = fieldList.toArray(new JField[fieldList.size()]);
            for (int i = 0; i < fields.length; ++i) {
                JField field = fields[i];

                boolean last = (i == (fields.length - 1));
                if (!field.alwaysPresent()) {
                    if (!last && !requireSeparator && !hasFirstFlag) {
                        writer.appendln("boolean first = true;");
                        hasFirstFlag = true;
                    }
                    writer.formatln("if (%s()) {", field.presence());
                    writer.begin();
                }

                if (requireSeparator) {
                    writer.appendln("out.append(',');");
                } else {
                    if (allowSeparator) {
                        if (hasFirstFlag) {
                            if (last || field.alwaysPresent()) {
                                writer.appendln("if (!first) out.append(',');");
                            } else {
                                writer.appendln("if (first) first = false;")
                                      .appendln("else out.append(',');");
                            }
                        }
                    } else if (hasFirstFlag) {
                        writer.appendln("first = false;");
                    }
                }

                writer.formatln("out.append(\"%s:\")", field.name());
                switch (field.type()) {
                    case BOOL:
                    case I32:
                    case I64:
                        writer.formatln("   .append(%s);", field.member());
                        break;
                    case BYTE:
                    case I16:
                        writer.formatln("   .append((int) %s);", field.member());
                        break;
                    case DOUBLE:
                    case MAP:
                    case SET:
                    case LIST:
                        writer.formatln("   .append(%s.asString(%s));",
                                        Strings.class.getName(),
                                        field.member());
                        break;
                    case STRING:
                        writer.appendln("   .append('\\\"')")
                              .formatln("   .append(%s.escape(%s))",
                                        Strings.class.getName(),
                                        field.member())
                              .appendln("   .append('\\\"');");
                        break;
                    case BINARY:
                        writer.appendln("   .append(\"b64(\")")
                              .formatln("   .append(%s.toBase64())", field.member())
                              .appendln("   .append(')');");
                        break;
                    case MESSAGE:
                    case ENUM:
                        writer.formatln("   .append(%s.asString());", field.member());
                        break;
                    default:
                        writer.formatln("   .append(%s.toString());", field.member());
                        break;
                }

                if (field.alwaysPresent()) {
                    requireSeparator = true;
                } else {
                    writer.end()
                          .appendln('}');
                    allowSeparator = true;
                }
            }
        }

        writer.appendln("out.append('}');")
              .appendln("return out.toString();")
              .end()
              .appendln("}")
              .newline();
    }

    private void appendCompareTo(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public int compareTo(%s other) {", message.instanceType())
              .begin();

        if (message.isUnion()) {
            writer.formatln("int c = %s.compareTo(other.%s);",
                            UNION_FIELD, UNION_FIELD)
                  .appendln("if (c != 0) return c;")
                  .newline()
                  .formatln("switch (%s) {", UNION_FIELD)
                  .begin();

            for (JField field : message.numericalOrderFields()) {
                writer.formatln("case %s:", caseFieldConstant(field))
                      .begin();

                switch (field.type()) {
                    case BOOL:
                        writer.formatln("return Boolean.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case BYTE:
                        writer.formatln("return Byte.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case I16:
                        writer.formatln("return Short.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case I32:
                        writer.formatln("return Integer.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case I64:
                        writer.formatln("return Long.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case DOUBLE:
                        writer.formatln("return Double.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case STRING:
                    case BINARY:
                    case MESSAGE:
                        writer.formatln("return %s.compareTo(other.%s);", field.member(), field.member());
                        break;
                    case ENUM:
                        writer.formatln("return Integer.compare(%s.asInteger(), other.%s.asInteger());",
                                        field.member(),
                                        field.member());
                        break;
                    case SET:
                    case LIST:
                    case MAP:
                        // containers aren't really comparable, just make some consistent comparison.
                        writer.formatln("return Integer.compare(%s.hashCode(), other.%s.hashCode());",
                                        field.member(),
                                        field.member());
                        break;
                    default:
                        break;
                }

                writer.end();
            }
            writer.appendln("default: return 0;")
                  .end()
                  .appendln('}');
        } else {
            writer.appendln("int c;");

            for (JField field : message.numericalOrderFields()) {
                writer.newline();

                if (!field.alwaysPresent()) {
                    writer.formatln("c = Boolean.compare(%s != null, other.%s != null);",
                                    field.member(),
                                    field.member())
                          .appendln("if (c != 0) return c;")
                          .formatln("if (%s != null) {", field.member())
                          .begin();
                }
                switch (field.type()) {
                    case BOOL:
                        writer.formatln("c = Boolean.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case BYTE:
                        writer.formatln("c = Byte.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case I16:
                        writer.formatln("c = Short.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case I32:
                        writer.formatln("c = Integer.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case I64:
                        writer.formatln("c = Long.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case DOUBLE:
                        writer.formatln("c = Double.compare(%s, other.%s);", field.member(), field.member());
                        break;
                    case STRING:
                    case BINARY:
                    case MESSAGE:
                        writer.formatln("c = %s.compareTo(other.%s);", field.member(), field.member());
                        break;
                    case ENUM:
                        writer.formatln("c = Integer.compare(%s.ordinal(), %s.ordinal());",
                                        field.member(),
                                        field.member());
                        break;
                    case SET:
                    case LIST:
                    case MAP:
                        writer.formatln("c = Integer.compare(%s.hashCode(), other.%s.hashCode());",
                                        field.member(),
                                        field.member());
                        break;
                    default:
                        break;
                }
                writer.appendln("if (c != 0) return c;");

                if (!field.alwaysPresent()) {
                    writer.end()
                          .appendln('}');
                }
            }
            writer.newline()
                  .appendln("return 0;");
        }

        writer.end()
              .appendln("}")
              .newline();
    }
}
