/*
 * Copyright 2015-2016 Providence Authors
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
package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.BaseConstantsFormatter;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.ValueBuilder;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.util.io.IndentedPrintWriter;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class JavaConstantsFormatter implements BaseConstantsFormatter {
    private static final String DBL_INDENT = IndentedPrintWriter.INDENT + IndentedPrintWriter.INDENT;

    private final JHelper  helper;
    private final IndentedPrintWriter writer;

    public JavaConstantsFormatter(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    @Override
    public void appendConstantsClass(CProgram document) throws GeneratorException {
        ValueBuilder value = new ValueBuilder(writer, helper);

        if (document.getComment() != null) {
            new BlockCommentBuilder(writer)
                    .comment(document.getComment())
                    .finish();
        }

        writer.appendln("@SuppressWarnings(\"unused\")")
              .formatln("public class %s {", helper.getConstantsClassName(document))
              .begin()
              .formatln("private %s() {}", helper.getConstantsClassName(document));

        for (CField c : document.getConstants()) {
            writer.newline();

            try {
                String name = c.getName();

                writer.formatln("public static final %s %s;", helper.getValueType(c.getDescriptor()), name)
                      .appendln("static {")
                      .begin()
                      .formatln("%s = ", name)
                      .begin();

                value.appendTypedValue(c.getDefaultValue(), c.getDescriptor());

                writer.append(';')
                      .end()
                      .end()
                      .appendln('}');

            } catch (Exception e) {
                throw new GeneratorException("Unable to generate constant " + document.getProgramName() + "." + c.getName(),
                                             e);
            }
        }

        writer.end()
              .appendln('}');
    }
}
