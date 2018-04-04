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
package net.morimekta.providence.generator.format.java.enums;

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptorProvider;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.EnumMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Locale;

/**
 * Formatting core (providence extended) content.
 */
public class CoreMemberFormatter implements EnumMemberFormatter {
    private final IndentedPrintWriter writer;

    public CoreMemberFormatter(IndentedPrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public Collection<String> getExtraImplements(CEnumDescriptor type) throws GeneratorException {
        return ImmutableList.of(
                String.format(Locale.US, "%s<%s>", PEnumValue.class.getName(), JUtils.getClassName(type))
        );
    }

    @Override
    public void appendMethods(CEnumDescriptor type) throws GeneratorException {
        writer.appendln("@Override")
              .appendln("public int asInteger() {")
              .begin()
              .appendln("return mId;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln(JAnnotation.NON_NULL)
              .appendln("public String asString() {")
              .begin()
              .appendln("return mName;")
              .end()
              .appendln('}')
              .newline();
    }

    @Override
    public void appendExtraProperties(CEnumDescriptor type) throws GeneratorException {
        appendBuilder(type);
        appendDescriptor(type);
    }

    private void appendDescriptor(PEnumDescriptor<?> type) {
        String simpleClass = JUtils.getClassName(type);

        writer.formatln("public static final %s<%s> kDescriptor;",
                        PEnumDescriptor.class.getName(),
                        simpleClass)
              .newline();

        writer.appendln("@Override")
              .formatln("public %s<%s> descriptor() {",
                        PEnumDescriptor.class.getName(),
                        simpleClass)
              .begin()
              .appendln("return kDescriptor;")
              .end()
              .appendln('}')
              .newline();

        writer.formatln("public static %s<%s> provider() {",
                        PEnumDescriptorProvider.class.getName(),
                        simpleClass)
              .begin()
              .formatln("return new %s<%s>(kDescriptor);",
                        PEnumDescriptorProvider.class.getName(),
                        simpleClass)
              .end()
              .appendln('}')
              .newline();

        writer.appendln("private static class _Descriptor")
              .formatln("        extends %s<%s> {",
                        PEnumDescriptor.class.getName(),
                        simpleClass)
              .begin()
              .appendln("public _Descriptor() {")
              .begin()
              .formatln("super(\"%s\", \"%s\", _Builder::new);",
                        type.getProgramName(),
                        type.getName(),
                        simpleClass)
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln(JAnnotation.NON_NULL)
              .formatln("public %s[] getValues() {", simpleClass)
              .begin()
              .formatln("return %s.values();", simpleClass)
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln(JAnnotation.NULLABLE)
              .formatln("public %s findById(int id) {", simpleClass)
              .begin()
              .formatln("return %s.findById(id);", simpleClass)
              .end()
              .appendln('}')
              .newline()
              .appendln("@Override")
              .appendln(JAnnotation.NULLABLE)
              .formatln("public %s findByName(String name) {", simpleClass)
              .begin()
              .formatln("return %s.findByName(name);", simpleClass)
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

    private void appendBuilder(PEnumDescriptor<?> type) {
        String simpleClass = JUtils.getClassName(type);
        writer.formatln("public static class _Builder extends %s<%s> {",
                        PEnumBuilder.class.getName(),
                        simpleClass)
              .begin()
              .formatln("private %s mValue;", simpleClass)
              .newline();

        writer.appendln("@Override")
              .appendln(JAnnotation.NON_NULL)
              .appendln("public _Builder setById(int value) {")
              .begin()
              .formatln("mValue = %s.findById(value);", simpleClass)
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln(JAnnotation.NON_NULL)
              .appendln("public _Builder setByName(String name) {")
              .begin()
              .formatln("mValue = %s.findByName(name);", simpleClass)
              .appendln("return this;")
              .end()
              .appendln('}')
              .newline();

        writer.appendln("@Override")
              .appendln("public boolean valid() {")
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
