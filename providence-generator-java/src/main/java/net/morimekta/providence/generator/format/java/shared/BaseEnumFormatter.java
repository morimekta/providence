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
package net.morimekta.providence.generator.format.java.shared;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JAnnotation;
import net.morimekta.providence.generator.format.java.utils.JUtils;
import net.morimekta.providence.reflect.contained.CEnumDescriptor;
import net.morimekta.providence.reflect.contained.CEnumValue;
import net.morimekta.providence.util.ThriftAnnotation;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Base formatter for all enums.
 */
public class BaseEnumFormatter {
    private final IndentedPrintWriter       writer;
    private final List<EnumMemberFormatter> formatters;

    public BaseEnumFormatter(IndentedPrintWriter writer,
                             List<EnumMemberFormatter> formatters) {
        this.writer = writer;
        this.formatters = ImmutableList.copyOf(formatters);
    }

    public void appendEnumClass(CEnumDescriptor type) throws GeneratorException {
        String simpleClass = JUtils.getClassName(type);

        BlockCommentBuilder classComment = null;
        if (type.getDocumentation() != null) {
            classComment = new BlockCommentBuilder(writer);
            classComment.comment(type.getDocumentation());
        }

        String deprecatedReason = type.getAnnotationValue(ThriftAnnotation.DEPRECATED);
        if (deprecatedReason != null && deprecatedReason.trim().length() > 0) {
            if (classComment == null) {
                classComment = new BlockCommentBuilder(writer);
            } else {
                classComment.newline();
            }
            classComment.deprecated_(deprecatedReason);
        }
        if (classComment != null) {
            classComment.finish();
        }

        if (type.getDocumentation() != null) {
            new BlockCommentBuilder(writer)
                    .comment(type.getDocumentation())
                    .finish();
        }
        formatters.forEach(f -> f.appendClassAnnotations(type));

        writer.formatln("public enum %s", simpleClass)
              .begin();

        Set<String> impl = new LinkedHashSet<>();
        formatters.forEach(f -> impl.addAll(f.getExtraImplements(type)));
        if (impl.size() > 0) {
            writer.formatln("    implements ")
                  .begin(   "               ");
            boolean first = true;
            for (String i : impl) {
                if (first) {
                    first = false;
                } else {
                    writer.append(',').appendln();
                }
                writer.append(i);
            }
            writer.end();
        }

        writer.append(" {");

        appendEnumValues(type);
        appendEnumFields(type);
        appendEnumConstructor(type);

        formatters.forEach(f -> f.appendMethods(type));
        formatters.forEach(f -> f.appendExtraProperties(type));

        writer.end()
              .appendln('}')
              .newline();
    }

    private void appendEnumValues(CEnumDescriptor type) {
        for (CEnumValue v : type.getValues()) {
            if (v.getDocumentation() != null) {
                new BlockCommentBuilder(writer)
                        .comment(v.getDocumentation())
                        .finish();
            }
            if (JAnnotation.isDeprecated(v)) {
                writer.appendln(JAnnotation.DEPRECATED);
            }
            writer.formatln("%s(%d, \"%s\"),",
                            JUtils.enumConst(v),
                            v.asInteger(),
                            v.asString());
        }
        writer.appendln(';')
              .newline();
    }

    private void appendEnumFields(CEnumDescriptor type) {
        writer.appendln("private final int    mId;")
              .appendln("private final String mName;")
              .newline();
    }

    private void appendEnumConstructor(CEnumDescriptor type) {
        writer.formatln("%s(int id, String name) {", JUtils.getClassName(type))
              .begin()
              .appendln("mId = id;")
              .appendln("mName = name;")
              .end()
              .appendln("}")
              .newline();
    }
}
