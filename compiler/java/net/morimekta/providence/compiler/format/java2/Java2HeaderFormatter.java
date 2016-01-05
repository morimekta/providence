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

package net.morimekta.providence.compiler.format.java2;

import net.morimekta.providence.util.io.IndentedPrintWriter;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class Java2HeaderFormatter {
    private final TreeSet<String> mIncludes;
    private final String          mJavaPackage;

    public Java2HeaderFormatter(String javaPackage) {
        mJavaPackage = javaPackage;
        mIncludes = new TreeSet<>();
    }

    public boolean hasIncluded(String javaClass) {
        return mIncludes.contains(javaClass);
    }

    public void include(String javaClass) {
        if (javaClass == null) return;
        int pos = javaClass.lastIndexOf('.');
        if (pos > 0) {
            String pkg = javaClass.substring(0, pos);

            if (!pkg.equals(mJavaPackage) && !pkg.equals("java.lang")) {
                mIncludes.add(javaClass);
            }
        }
        // Otherwise it has no package, so it's not a class, but a primitive type.
        // e.g. byte or byte[]. Ignore.
    }

    public void format(IndentedPrintWriter writer) {
        Set<String> includes = new TreeSet<>(mIncludes);
        Set<String> done = new HashSet<>();

        writer.format("package %s;", mJavaPackage)
              .newline();
        // Order of imports:
        //  - java.*
        //  - android.*
        //  - org.apache.thrift2.*
        //  - *
        for (String include : includes) {
            if (include.startsWith("java.")) {
                writer.formatln("import %s;", include);
                done.add(include);
            }
        }
        if (done.size() > 0) {
            writer.newline();
            includes.removeAll(done);
            done.clear();
        }
        for (String include : includes) {
            if (include.startsWith("android.")) {
                writer.formatln("import %s;", include);
                done.add(include);
            }
        }
        if (done.size() > 0) {
            writer.newline();
            includes.removeAll(done);
            done.clear();
        }
        for (String include : includes) {
            if (include.startsWith("org.apache.thrift2.")) {
                writer.formatln("import %s;", include);
                done.add(include);
            }
        }
        if (done.size() > 0) {
            writer.append('\n');
            includes.removeAll(done);
            done.clear();
        }
        for (String include : includes) {
            writer.formatln("import %s;", include);
        }
        if (includes.size() > 0) {
            writer.newline();
        }
    }
}
