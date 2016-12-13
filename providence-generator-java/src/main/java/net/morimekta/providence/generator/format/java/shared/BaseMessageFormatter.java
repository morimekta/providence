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

import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.utils.BlockCommentBuilder;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO(steineldar): Make a proper class description.
 */
public abstract class BaseMessageFormatter {
    protected final IndentedPrintWriter          writer;

    private final JHelper                        helper;
    private final   List<MessageMemberFormatter> formatters;
    private final   boolean                      inner;
    private final boolean                        makeProtected;

    public BaseMessageFormatter(boolean inner,
                                boolean makeProtected,
                                IndentedPrintWriter writer,
                                JHelper helper,
                                List<MessageMemberFormatter> formatters) {
        this.inner = inner;
        this.makeProtected = makeProtected;
        this.writer = writer;
        this.helper = helper;
        this.formatters = ImmutableList.copyOf(formatters);
    }

    protected void appendClassExtends(JMessage<?> message) {
        if (message.isException()) {
            writer.appendln("extends " + message.exceptionBaseClass());
        }
    }

    protected abstract String getClassName(JMessage<?> message);

    public void appendMessageClass(PStructDescriptor<?,?> descriptor) throws GeneratorException {
        @SuppressWarnings("unchecked")
        JMessage<?> message = new JMessage(descriptor, helper);

        if (message.descriptor() instanceof CAnnotatedDescriptor) {
            CAnnotatedDescriptor annotatedDescriptor = (CAnnotatedDescriptor) message.descriptor();
            if (annotatedDescriptor.getDocumentation() != null) {
                new BlockCommentBuilder(writer)
                        .comment(annotatedDescriptor.getDocumentation())
                        .finish();
            }
        }

        formatters.forEach(f -> f.appendClassAnnotations(message));
        writer.formatln("%s %sclass %s",
                        makeProtected ? "protected" : "public",
                        inner ? "static " : "",
                        getClassName(message))
              .begin().begin();
        appendClassExtends(message);
        writer.end();

        Set<String> impl = new LinkedHashSet<>();
        formatters.forEach(f -> impl.addAll(f.getExtraImplements(message)));
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

        formatters.forEach(f -> f.appendConstants(message));
        formatters.forEach(f -> f.appendFields(message));
        formatters.forEach(f -> f.appendConstructors(message));
        formatters.forEach(f -> f.appendMethods(message));
        formatters.forEach(f -> f.appendExtraProperties(message));

        writer.end()
              .appendln('}');
    }
}
