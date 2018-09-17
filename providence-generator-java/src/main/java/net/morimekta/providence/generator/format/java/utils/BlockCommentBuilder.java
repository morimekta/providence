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
package net.morimekta.providence.generator.format.java.utils;

import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;
import net.morimekta.util.io.IndentedPrintWriter;

import javax.annotation.Nonnull;

/**
 * Builds a proper block javadoc-compatible comment.
 */
public class BlockCommentBuilder {
    private static final Escaper HTML = HtmlEscapers.htmlEscaper();

    private final IndentedPrintWriter writer;

    public BlockCommentBuilder(IndentedPrintWriter writer) {
        this.writer = writer;

        writer.appendln("/**")
              .begin(" *");
    }

    public BlockCommentBuilder comment(String comment) {
        String escaped = HTML.escape(comment).replaceAll("[@]", "&#64;");
        for (String line : escaped.trim().split("\r?\n")) {
            if (line.trim().length() == 0) {
                writer.appendln(" <p>");
            } else {
                writer.appendln(" " + line.replaceAll("[ ]*$", ""));
            }
        }

        return this;
    }

    public BlockCommentBuilder commentRaw(String comment) {
        for (String line : comment.trim().split("\r?\n")) {
            if (line.trim().length() == 0) {
                writer.appendln(" <p>");
            } else {
                writer.appendln(" " + line.replaceAll("[ ]*$", ""));
            }
        }

        return this;
    }

    public BlockCommentBuilder newline() {
        writer.appendln();
        return this;
    }

    public BlockCommentBuilder paragraph() {
        writer.appendln(" <p>");
        return this;
    }

    public BlockCommentBuilder param_(String name, String comment) {
        writer.formatln(" @param %s %s", name, HTML.escape(comment));
        return this;
    }

    public BlockCommentBuilder return_(String comment) {
        writer.formatln(" @return %s", comment);
        return this;
    }

    public BlockCommentBuilder throws_(Class<?> klass, String comment) {
        return throws_(klass.getName().replaceAll("[$]", "."), comment);
    }

    public BlockCommentBuilder throws_(String klass, String comment) {
        writer.formatln(" @throws %s %s",
                        klass,
                        comment);
        return this;
    }

    public BlockCommentBuilder deprecated_(@Nonnull String reason) {
        writer.formatln(" @deprecated %s", HTML.escape(reason));
        return this;
    }

    public void finish() {
        writer.end()
              .appendln(" */");
    }
}
