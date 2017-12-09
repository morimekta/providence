/*
 * Copyright 2017 Providence Authors
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
package net.morimekta.providence.generator.format.js.utils;

import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;

/**
 * Builds a proper block javadoc-compatible comment.
 */
public class ClosureDocBuilder {
    private final IndentedPrintWriter writer;
    private final Escaper             html;

    public ClosureDocBuilder(IndentedPrintWriter writer) {
        this.html = HtmlEscapers.htmlEscaper();
        this.writer = writer;

        writer.appendln("/**")
              .begin(" *");
    }

    public ClosureDocBuilder comment(String comment) {
        String escaped = html.escape(comment).replaceAll("[@]", "&#64;");
        for (String line : escaped.trim().split("\r?\n")) {
            if (line.trim().length() == 0) {
                writer.appendln(" ");
            } else {
                writer.appendln(" " + line.replaceAll("[ ]*$", ""));
            }
        }

        return this;
    }

    public ClosureDocBuilder newline() {
        writer.appendln();
        return this;
    }

    public ClosureDocBuilder param_(String name, String type, String comment) {
        if (comment == null) {
            writer.formatln(" @param {%s} %s", type, name);
        } else {
            writer.formatln(" @param {%s} %s %s", type, name, html.escape(comment));
        }
        return this;
    }

    public ClosureDocBuilder constructor_() {
        writer.formatln(" @constructor");
        return this;
    }

    public ClosureDocBuilder interface_() {
        writer.appendln(" @interface");
        return this;
    }

    public ClosureDocBuilder enum_(String type) {
        writer.formatln(" @enum {%s}", type);
        return this;
    }

    public ClosureDocBuilder const_(String type) {
        writer.formatln(" @const {%s}", type);
        return this;
    }

    public ClosureDocBuilder type_(String type) {
        writer.formatln(" @type {%s}", type);
        return this;
    }

    public ClosureDocBuilder return_(String type, String comment) {
        if (comment == null) {
            writer.formatln(" @return {%s}", type);
        } else {
            writer.formatln(" @return {%s} %s", type, html.escape(comment).trim());
        }
        return this;
    }

    public ClosureDocBuilder private_() {
        writer.formatln(" @private");
        return this;
    }

    public ClosureDocBuilder deprecated_(String reason) {
        if (reason == null) {
            writer.formatln(" @deprecated");
        } else {
            writer.formatln(" @deprecated %s", html.escape(reason).trim());
        }
        return this;
    }

    public ClosureDocBuilder extends_(String ext) {
        writer.formatln(" @extends {%s}", ext);
        return this;
    }

    public void finish() {
        writer.end()
              .appendln(" */");
    }
}
