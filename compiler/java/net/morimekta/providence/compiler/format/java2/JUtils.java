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

import net.morimekta.providence.compiler.generator.GeneratorException;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.reflect.contained.CDocument;
import net.morimekta.providence.util.PStringUtils;
import net.morimekta.providence.util.io.IndentedPrintWriter;

import java.io.File;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class JUtils {
    public static long generateSerialVersionUID(PStructDescriptor<?,?> type) {
        String string = type.getVariant().getName() + " " + type.getQualifiedName(null);

        long hash = 1125899906842597L; // prime
        final int len = string.length();
        for (int i = 0; i < len; i++) {
            hash = 4909L * hash + 7919L * string.charAt(i);
        }
        return hash;
    }

    public static String getClassName(PDeclaredDescriptor<?> type) {
        return PStringUtils.camelCase("", type.getName());
    }

    public static String getJavaPackage(CDocument document) throws GeneratorException {
        String javaPackage = document.getNamespaceForLanguage("java");
        if (javaPackage == null) {
            throw new GeneratorException("No java namespace for thrift package " +
                                         document.getPackageName());
        }
        return javaPackage;
    }

    public static String getPackageClassPath(String javaPackage) throws GeneratorException {
        String[] parts = javaPackage.split("[.]");
        return PStringUtils.join(File.separator, parts);
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
