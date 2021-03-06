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
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.providence.generator.format.java.JavaOptions;
import net.morimekta.providence.generator.format.java.shared.EnumMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.util.io.IndentedPrintWriter;

import javax.annotation.Generated;

/**
 * Formatter for common (non-extended) enum content.
 */
public class CommonMemberFormatter implements EnumMemberFormatter {
    private final IndentedPrintWriter writer;
    private final JavaOptions         javaOptions;
    private final GeneratorOptions    generatorOptions;

    public CommonMemberFormatter(IndentedPrintWriter writer,
                                 GeneratorOptions generatorOptions,
                                 JavaOptions javaOptions) {
        this.writer = writer;
        this.generatorOptions = generatorOptions;
        this.javaOptions = javaOptions;
    }

    @Override
    public void appendClassAnnotations(CEnumDescriptor type) throws GeneratorException {
        if (JAnnotation.isDeprecated((CAnnotatedDescriptor) type)) {
            writer.appendln(JAnnotation.DEPRECATED);
        }
        if (javaOptions.generated_annotation_version) {
            writer.formatln("@%s(\"%s %s\")",
                            Generated.class.getName(),
                            generatorOptions.generator_program_name,
                            generatorOptions.program_version);
        } else {
            writer.formatln("@%s(\"%s\")",
                            Generated.class.getName(),
                            generatorOptions.generator_program_name);
        }
    }

    public void appendStaticGetter_FindBy(CEnumDescriptor type, String simpleClass) {
        new BlockCommentBuilder(writer)
                .comment("Find a value based in its ID")
                .newline()
                .param_("id", "Id of value")
                .return_("Value found or null")
                .finish();
        writer.formatln("public static %s findById(int id) {", simpleClass)
              .begin()
              .appendln("switch (id) {")
              .begin();
        for (PEnumValue<?> value : type.getValues()) {
            writer.formatln("case %d: return %s.%s;",
                            value.asInteger(),
                            simpleClass,
                            JUtils.enumConst(value));
        }
        writer.appendln("default: return null;")
              .end()
              .appendln('}')
              .end()
              .appendln('}')
              .newline();

        new BlockCommentBuilder(writer)
                .comment("Find a value based in its ID")
                .newline()
                .param_("id", "Id of value")
                .return_("Value found or null")
                .finish();
        writer.formatln("public static %s findById(Integer id) {", simpleClass)
              .appendln("    return id == null ? null : findById(id.intValue());")
              .appendln('}')
              .newline();

        new BlockCommentBuilder(writer)
                .comment("Find a value based in its name")
                .newline()
                .param_("name", "Name of value")
                .return_("Value found or null")
                .finish();
        writer.formatln("public static %s findByName(String name) {", simpleClass)
              .begin()
              .appendln("if (name == null) {")
              .formatln("    throw new IllegalArgumentException(\"Null name given\");", type.getQualifiedName())
              .appendln("}")
              .appendln("switch (name) {")
              .begin();
        for (PEnumValue<?> value : type.getValues()) {
            writer.formatln("case \"%s\": return %s.%s;",
                            value.asString(),
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

    public void appendStaticGetter_ValueFor(CEnumDescriptor type, String simpleClass) {
        new BlockCommentBuilder(writer)
                .comment("Get a value based in its ID")
                .newline()
                .param_("id", "Id of value")
                .return_("Value found")
                .throws_(IllegalArgumentException.class.getSimpleName(), "If no value for id is found")
                .finish();
        writer.appendln(JAnnotation.NON_NULL)
              .formatln("public static %s valueForId(int id) {", simpleClass)
              .formatln("    %s value = findById(id);", simpleClass)
              .appendln("    if (value == null) {")
              .formatln("        throw new IllegalArgumentException(\"No %s for id \" + id);", type.getQualifiedName())
              .appendln("    }")
              .appendln("    return value;")
              .appendln('}')
              .newline();

        new BlockCommentBuilder(writer)
                .comment("Get a value based in its name")
                .newline()
                .param_("name", "Name of value")
                .return_("Value found")
                .throws_(IllegalArgumentException.class.getSimpleName(), "If no value for name is found, or null name")
                .finish();
        writer.appendln(JAnnotation.NON_NULL)
              .formatln("public static %s valueForName(String name) {", simpleClass)
              .formatln("    %s value = findByName(name);", simpleClass)
              .appendln("    if (value == null) {")
              .formatln("        throw new IllegalArgumentException(\"No %s for name \\\"\" + name + \"\\\"\");", type.getQualifiedName())
              .appendln("    }")
              .appendln("    return value;")
              .appendln('}')
              .newline();
    }

    @Override
    public void appendExtraProperties(CEnumDescriptor type) throws GeneratorException {
        String simpleClass = JUtils.getClassName(type);

        appendStaticGetter_FindBy(type, simpleClass);
        appendStaticGetter_ValueFor(type, simpleClass);
    }
}
