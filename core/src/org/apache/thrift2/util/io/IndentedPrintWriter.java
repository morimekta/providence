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

package org.apache.thrift2.util.io;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Stack;

import org.apache.thrift2.util.TStringUtils;

public class IndentedPrintWriter
        extends PrintWriter {
    public final static String NEWLINE = "\n";
    public final static String INDENT  = "    ";

    private final Stack<String> mIndents;
    private final String        mIndent;
    private final String        mNewline;
    private       String        mCurrentIndent;

    public IndentedPrintWriter(OutputStream out) {
        this(new PrintWriter(out), INDENT, NEWLINE);
    }

    public IndentedPrintWriter(Writer out, String indent, String newline) {
        super(out);
        mIndent = indent;
        mNewline = newline;

        mIndents = new Stack<>();
        mCurrentIndent = "";
    }

    public IndentedPrintWriter begin() {
        return begin(mIndent);
    }

    public IndentedPrintWriter begin(String indent) {
        mIndents.push(indent);
        mCurrentIndent = TStringUtils.join("", mIndents);
        return this;
    }

    public IndentedPrintWriter end() {
        if (mIndents.isEmpty())
            throw new IllegalStateException("No indent to end");
        mIndents.pop();
        mCurrentIndent = TStringUtils.join("", mIndents);
        return this;
    }

    public IndentedPrintWriter newline() {
        return append(mNewline);
    }

    public IndentedPrintWriter appendln() {
        newline().append(mCurrentIndent);
        return this;
    }

    public IndentedPrintWriter appendln(char c) {
        return appendln().append(c);
    }

    public IndentedPrintWriter appendln(CharSequence str) {
        return appendln().append(str);
    }

    public IndentedPrintWriter formatln(String format, Object... args) {
        return appendln().append(String.format(format, args));
    }

    // --- Override PrintWriter methods to return IndentedPrintWriter.

    @Override
    public IndentedPrintWriter printf(String format, Object... args) {
        return format(format, args);
    }

    @Override
    public IndentedPrintWriter printf(Locale l, String format, Object... args) {
        return format(l, format, args);
    }

    @Override
    public IndentedPrintWriter format(String format, Object... args) {
        super.format(format, args);
        return this;
    }

    @Override
    public IndentedPrintWriter format(Locale l, String format, Object... args) {
        super.format(l, format, args);
        return this;
    }

    @Override
    public IndentedPrintWriter append(CharSequence str) {
        super.append(str);
        return this;
    }

    @Override
    public IndentedPrintWriter append(CharSequence str, int start, int end) {
        super.append(str, start, end);
        return this;
    }

    @Override
    public IndentedPrintWriter append(char c) {
        super.append(c);
        return this;
    }

    // --- Override PrintWriter methods to work like IndentedPrintWriter.

    @Override
    public void println() {
        newline();
    }

    @Override
    public void println(boolean x) {
        appendln().print(x);
    }

    @Override
    public void println(char x) {
        appendln().print(x);
    }

    @Override
    public void println(int x) {
        appendln().print(x);
    }

    @Override
    public void println(long x) {
        appendln().print(x);
    }

    @Override
    public void println(float x) {
        appendln().print(x);
    }

    @Override
    public void println(double x) {
        appendln().print(x);
    }

    @Override
    public void println(char[] x) {
        appendln().print(x);
    }

    @Override
    public void println(String x) {
        appendln(x);
    }

    @Override
    public void println(Object x) {
        appendln(String.valueOf(x));
    }
}
