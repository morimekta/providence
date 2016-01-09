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

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumBuilderFactory;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.compiler.generator.GeneratorException;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptorProvider;
import net.morimekta.providence.util.io.IndentedPrintWriter;

/**
 * @author Stein Eldar Johnsen
 * @since 20.09.15
 */
public class JEnumFormat {
    private final JHelper  helper;
    private final JOptions options;

    public JEnumFormat(JHelper helper, JOptions options) {
        this.helper = helper;
        this.options = options;
    }

    public void format(IndentedPrintWriter writer, PEnumDescriptor<?> type) throws GeneratorException {
        JHeader header = new JHeader(helper.getJavaPackage(type));

        header.include(PEnumBuilder.class.getName());
        header.include(PEnumBuilderFactory.class.getName());
        header.include(PEnumValue.class.getName());
        header.include(PEnumDescriptor.class.getName());
        header.include(PEnumDescriptorProvider.class.getName());

        if (options.jackson) {
            header.include("com.fasterxml.jackson.annotation.JsonCreator");
            header.include("com.fasterxml.jackson.annotation.JsonValue");
        }

        header.format(writer);

        String simpleClass = helper.getInstanceClassName(type);

        if (type.getComment() != null) {
            JUtils.appendBlockComment(writer, type.getComment());
            if (JAnnotation.isDeprecated(type)) {
                writer.appendln(JAnnotation.DEPRECATED);
            }
        }
        writer.formatln("public enum %s implements PEnumValue<%s> {", simpleClass, simpleClass)
              .begin();

        for (PEnumValue<?> v : type.getValues()) {
            /* TODO: The enum value comments are buggy. It attaches to a different enum that it should...
            if (v.getComment() != null) {
                JUtils.appendBlockComment(writer, v.getComment());
                if (JAnnotation.isDeprecated(v)) {
                    writer.appendln(JAnnotation.DEPRECATED);
                }
            } */
            writer.formatln("%s(%d, \"%s\"),", v.getName().toUpperCase(), v.getValue(), v.getName());
        }
        writer.appendln(';')
              .newline();

        writer.appendln("private final int mValue;")
              .appendln("private final String mName;")
              .newline()
              .formatln("%s(int value, String name) {", simpleClass)
              .begin()
              .appendln("mValue = value;")
              .appendln("mName = name;")
              .end()
              .appendln("}")
              .newline();

        writer.appendln("@Override")
              .appendln("public String getComment() {")
              .begin()
              .appendln("return null;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public int getValue() {")
              .begin()
              .appendln("return mValue;")
              .end()
              .appendln('}')
              .newline();

        if (options.jackson) {
            writer.appendln("@JsonValue");
        }

        writer.appendln("@Override")
              .appendln("public String getName() {")
              .begin()
              .appendln("return mName;")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static %s forValue(int value) {", simpleClass)
              .begin()
              .appendln("switch (value) {")
              .begin();
        for (PEnumValue<?> value : type.getValues()) {
            writer.formatln("case %d: return %s.%s;",
                            value.getValue(), simpleClass, value.getName().toUpperCase());
        }
        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        if (options.jackson) {
            writer.appendln("@JsonCreator");
        }
        writer.formatln("public static %s forName(String name) {", simpleClass)
              .begin()
              .appendln("switch (name) {")
              .begin();
        for (PEnumValue<?> value : type.getValues()) {
            writer.formatln("case \"%s\": return %s.%s;",
                            value.getName(), simpleClass, value.getName().toUpperCase());
        }
        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        appendBuilder(writer, type);

        appendDescriptor(writer, type);

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendDescriptor(IndentedPrintWriter writer, PEnumDescriptor<?> type) {
        String simpleClass = helper.getInstanceClassName(type);

        writer.formatln("public static final PEnumDescriptor<%s> kDescriptor;", simpleClass)
              .newline();

        writer.appendln("@Override")
              .formatln("public PEnumDescriptor<%s> descriptor() {", simpleClass)
              .begin()
              .appendln("return kDescriptor;")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static PEnumDescriptorProvider<%s> provider() {", simpleClass)
              .begin()
              .formatln("return new PEnumDescriptorProvider<%s>(kDescriptor);", simpleClass)
              .end()
              .appendln('}')
              .newline();

        writer.appendln("private static class _Factory")
              .begin()
              .formatln("    extends PEnumBuilderFactory<%s> {", simpleClass)
              .appendln("@Override")
              .formatln("public %s._Builder builder() {", simpleClass)
              .begin()
              .formatln("return new %s._Builder();", simpleClass)
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        writer.appendln("private static class _Descriptor")
              .formatln("        extends PEnumDescriptor<%s> {", simpleClass)
              .begin()
              .appendln("public _Descriptor() {")
              .begin()
              .formatln("super(null, \"%s\", \"%s\", new _Factory());",
                        type.getPackageName(), type.getName(), simpleClass)
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .formatln("public %s[] getValues() {", simpleClass)
              .begin()
              .formatln("return %s.values();", simpleClass)
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .formatln("public %s getValueById(int id) {", simpleClass)
              .begin()
              .formatln("return %s.forValue(id);", simpleClass)
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .formatln("public %s getValueByName(String name) {", simpleClass)
              .begin()
              .formatln("return %s.forName(name);", simpleClass)
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        writer.formatln("static {", simpleClass)
              .begin();
        writer.appendln("kDescriptor = new _Descriptor();")
              .end()
              .appendln('}');
    }

    protected void appendBuilder(IndentedPrintWriter writer, PEnumDescriptor<?> type) {
        String simpleClass = helper.getInstanceClassName(type);
        writer.formatln("public static class _Builder extends PEnumBuilder<%s> {", simpleClass)
              .begin()
              .formatln("%s mValue;", simpleClass)
              .newline();

        writer.appendln("@Override")
              .appendln("public _Builder setByValue(int value) {")
              .begin()
              .formatln("mValue = %s.forValue(value);", simpleClass)
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public _Builder setByName(String name) {")
              .begin()
              .formatln("mValue = %s.forName(name);", simpleClass)
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public boolean isValid() {")
              .begin()
              .appendln("return mValue != null;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .formatln("public %s build() {", simpleClass)
              .begin()
              .appendln("return mValue;")
              .end()
              .appendln('}');

        writer.end()
              .appendln('}')
              .newline();
    }
}
