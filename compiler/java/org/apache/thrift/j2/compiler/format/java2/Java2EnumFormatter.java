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

package org.apache.thrift.j2.compiler.format.java2;

import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TEnumBuilderFactory;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.compiler.generator.GeneratorException;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;
import org.apache.thrift.j2.descriptor.TEnumDescriptorProvider;
import org.apache.thrift.j2.util.io.IndentedPrintWriter;

/**
 * @author Stein Eldar Johnsen
 * @since 20.09.15
 */
public class Java2EnumFormatter {
    private final Java2TypeHelper mTypeHelper;

    public Java2EnumFormatter(Java2TypeHelper helper) {
        mTypeHelper = helper;
    }

    public void format(IndentedPrintWriter writer, TEnumDescriptor<?> type) throws GeneratorException {
        Java2HeaderFormatter header = new Java2HeaderFormatter(mTypeHelper.getJavaPackage(type));

        header.include(TEnumBuilder.class.getName());
        header.include(TEnumBuilderFactory.class.getName());
        header.include(TEnumValue.class.getName());
        header.include(TEnumDescriptor.class.getName());
        header.include(TEnumDescriptorProvider.class.getName());

        header.format(writer);

        String simpleClass = mTypeHelper.getInstanceClassName(type);

        if (type.getComment() != null) {
            Java2Utils.appendBlockComment(writer, type.getComment());
        }
        if (Java2Utils.hasDeprecatedAnnotation(type.getComment())) {
            writer.appendln(Java2Utils.DEPRECATED);
        }
        writer.formatln("public enum %s implements TEnumValue<%s> {", simpleClass, simpleClass)
              .begin();

        for (TEnumValue<?> v : type.getValues()) {
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
        for (TEnumValue<?> value : type.getValues()) {
            writer.formatln("case %d: return %s.%s;",
                            value.getValue(), simpleClass, value.getName().toUpperCase());
        }
        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static %s forName(String name) {", simpleClass)
              .begin()
              .appendln("switch (name) {")
              .begin();
        for (TEnumValue<?> value : type.getValues()) {
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

    private void appendDescriptor(IndentedPrintWriter writer, TEnumDescriptor<?> type) {
        String simpleClass = mTypeHelper.getInstanceClassName(type);

        writer.formatln("public static final TEnumDescriptor<%s> kDescriptor;", simpleClass)
              .newline();

        writer.appendln("@Override")
              .formatln("public TEnumDescriptor<%s> descriptor() {", simpleClass)
              .begin()
              .appendln("return kDescriptor;")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static TEnumDescriptorProvider<%s> provider() {", simpleClass)
              .begin()
              .formatln("return new TEnumDescriptorProvider<%s>(kDescriptor);", simpleClass)
              .end()
              .appendln('}')
              .newline();

        writer.appendln("private static class _Factory")
              .begin()
              .formatln("    extends TEnumBuilderFactory<%s> {", simpleClass)
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
              .formatln("        extends TEnumDescriptor<%s> {", simpleClass)
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

    protected void appendBuilder(IndentedPrintWriter writer, TEnumDescriptor<?> type) {
        String simpleClass = mTypeHelper.getInstanceClassName(type);
        writer.formatln("public static class _Builder extends TEnumBuilder<%s> {", simpleClass)
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
