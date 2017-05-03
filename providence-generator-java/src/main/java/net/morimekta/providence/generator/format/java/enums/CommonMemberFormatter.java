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

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.JavaOptions;
import net.morimekta.providence.generator.format.java.shared.EnumMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.util.io.IndentedPrintWriter;

import javax.annotation.Generated;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Formatter for common (non-extended) enum content.
 */
public class CommonMemberFormatter implements EnumMemberFormatter {
    private final IndentedPrintWriter writer;
    private final JavaOptions options;
    private final String version;

    public CommonMemberFormatter(IndentedPrintWriter writer, JavaOptions options) {
        this.writer = writer;
        this.options = options;

        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/java_generator_version.properties"));

            this.version = properties.getProperty("java_generator_version");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void appendClassAnnotations(CEnumDescriptor type) throws GeneratorException {
        if (JAnnotation.isDeprecated((CAnnotatedDescriptor) type)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }
        if (options.generated_annotation) {
            writer.formatln("@%s(\"providence java generator %s\")", Generated.class.getName(), version);
        }
    }

    @Override
    public void appendExtraProperties(CEnumDescriptor type) throws GeneratorException {
        String simpleClass = JUtils.getClassName(type);

        writer.formatln("public static %s forValue(int value) {", simpleClass)
              .begin()
              .appendln("switch (value) {")
              .begin();
        for (PEnumValue<?> value : type.getValues()) {
            writer.formatln("case %d: return %s.%s;",
                            value.getValue(),
                            simpleClass,
                            JUtils.enumConst(value));
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
        for (PEnumValue<?> value : type.getValues()) {
            writer.formatln("case \"%s\": return %s.%s;",
                            value.getName(),
                            simpleClass,
                            JUtils.enumConst(value));
        }
        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();
    }
}
