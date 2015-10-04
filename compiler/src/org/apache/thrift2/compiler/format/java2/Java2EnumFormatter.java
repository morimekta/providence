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

package org.apache.thrift2.compiler.format.java2;

import org.apache.thrift2.compiler.generator.GeneratorException;
import org.apache.thrift2.descriptor.TEnumDescriptor;
import org.apache.thrift2.util.io.IndentedPrintWriter;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 20.09.15
 */
public class Java2EnumFormatter {
    private final Java2TypeHelper mTypeHelper;

    public Java2EnumFormatter(Java2TypeHelper helper) {
        mTypeHelper = helper;
    }

    public void format(IndentedPrintWriter writer, TEnumDescriptor<?> type) throws GeneratorException {
        Java2HeaderFormatter header = new Java2HeaderFormatter(mTypeHelper.getJavaPackage(type));

        header.include("org.apache.thrift2.TEnumBuilder");
        header.include("org.apache.thrift2.TEnumBuilderFactory");
        header.include("org.apache.thrift2.TEnumValue");
        header.include("org.apache.thrift2.descriptor.TEnumDescriptor");
        header.include("org.apache.thrift2.descriptor.TEnumDescriptorProvider");

        header.include("java.util.LinkedList");
        header.include("java.util.List");

        header.format(writer);

        String simpleClass = mTypeHelper.getSimpleClassName(type);

        if (type.getComment() != null) {
            Java2Utils.appendBlockComment(writer, type.getComment());
        }
        writer.formatln("public enum %s implements TEnumValue<%s> {", simpleClass, simpleClass)
              .begin();

        for (TEnumDescriptor.Value v : type.getValues()) {
            writer.formatln("%s(%d),", v.getName(), v.getValue());
        }
        writer.appendln(';')
              .newline();

        writer.appendln("private final int mValue;")
              .newline()
              .formatln("%s(int value) {", simpleClass)
              .begin()
              .appendln("mValue = value;")
              .end()
              .appendln("}")
              .newline();

        writer.appendln("@Override")
              .appendln("public int getValue() {")
              .begin()
              .appendln("return mValue;")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static %s valueOf(int value) {", simpleClass)
              .begin()
              .formatln("for (%s e : values()) {", simpleClass)
              .begin()
              .appendln("if (e.mValue == value) return e;")
              .end()
              .appendln('}')
              .appendln("return null;")
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
        String simpleClass = mTypeHelper.getSimpleClassName(type);

        writer.formatln("public static final TEnumDescriptor<%s> DESCRIPTOR = _createDescriptor();", simpleClass)
              .newline();

        writer.appendln("@Override")
              .formatln("public TEnumDescriptor<%s> descriptor() {", simpleClass)
              .begin()
              .appendln("return DESCRIPTOR;")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static TEnumDescriptorProvider<%s> provider() {", simpleClass)
              .begin()
              .formatln("return new TEnumDescriptorProvider<%s>(DESCRIPTOR);", simpleClass)
              .end()
              .appendln('}')
              .newline();

        writer.appendln("private static class _Factory")
              .begin()
              .formatln("    extends TEnumBuilderFactory<%s> {", simpleClass)
              .appendln("@Override")
              .formatln("public %s.Builder builder() {", simpleClass)
              .begin()
              .formatln("return new %s.Builder();", simpleClass)
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        writer.formatln("private static TEnumDescriptor<%s> _createDescriptor() {", simpleClass)
              .begin()
              .appendln("List<TEnumDescriptor.Value> enumValues = new LinkedList<>();");
        for (TEnumDescriptor.Value value : type.getValues()) {
            writer.formatln("enumValues.add(new TEnumDescriptor.Value(null, \"%s\", %d));",
                            value.getName(), value.getValue());
        }
        writer.formatln("return new TEnumDescriptor<>(null, \"%s\", \"%s\", enumValues, new _Factory());",
                        type.getPackageName(), type.getName())
              .end()
              .appendln('}');
    }

    protected void appendBuilder(IndentedPrintWriter writer, TEnumDescriptor<?> type) {
        String simpleClass = mTypeHelper.getSimpleClassName(type);
        writer.formatln("public static class Builder extends TEnumBuilder<%s> {", simpleClass)
              .begin()
              .formatln("%s mValue;", simpleClass)
              .newline();

        writer.appendln("@Override")
              .appendln("public Builder setByValue(int value) {")
              .begin()
              .formatln("mValue = %s.valueOf(value);", simpleClass)
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public Builder setByName(String name) {")
              .begin()
              .formatln("mValue = %s.valueOf(name);", simpleClass)
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
