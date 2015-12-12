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

package org.apache.thrift.j2.compiler.format.java2;

import org.apache.thrift.j2.compiler.generator.GeneratorException;
import org.apache.thrift.j2.descriptor.TDeclaredDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.reflect.contained.TContainedDocument;
import org.apache.thrift.j2.util.TStringUtils;
import org.apache.thrift.j2.util.io.IndentedPrintWriter;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class Java2Utils {
    private static final Pattern DEPRECATED_RE = Pattern.compile("[@][Dd]eprecated\\b", Pattern.MULTILINE);
    protected static final String DEPRECATED = "@Deprecated";

    public static long generateSerialVersionUID(TStructDescriptor<?,?> type) {
        String string = type.getVariant().getName() + " " + type.getQualifiedName(null);

        long hash = 1125899906842597L; // prime
        final int len = string.length();
        for (int i = 0; i < len; i++) {
            hash = 4909L * hash + 7919L * string.charAt(i);
        }
        return hash;
    }

    public static boolean hasDeprecatedAnnotation(String comment) {
        if (comment == null) return false;
        return DEPRECATED_RE.matcher(comment).find();
    }

    public static String getClassName(TDeclaredDescriptor<?> type) {
        return TStringUtils.camelCase("", type.getName());
    }

    public static String getJavaPackage(TContainedDocument document) throws GeneratorException {
        String javaPackage = document.getNamespaceForLanguage("java2");
        if (javaPackage == null) {
            javaPackage = document.getNamespaceForLanguage("java");
        }
        if (javaPackage == null) {
            throw new GeneratorException("No java namespace for thrift package " +
                                         document.getPackageName());
        }
        return javaPackage;
    }

    public static String getPackageClassPath(String javaPackage) throws GeneratorException {
        System.out.println("PATH OF = " + javaPackage);
        String[] parts = javaPackage.split("[.]");
        return TStringUtils.join(File.separator, parts);
    }

    public static void appendBlockComment(IndentedPrintWriter writer, String comment) {
        String[] lines = comment.split("\n");
        if (lines.length == 1) {
            writer.formatln("/** %s */", comment);
        } else {
            writer.appendln("/**");
            for (String l : lines) {
                writer.formatln(" * %s", l);
            }
            writer.appendln(" */");
        }
    }
}
