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

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.util.Strings;

import java.io.File;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class JUtils {
    public static long generateSerialVersionUID(PMessageDescriptor<?, ?> type) {
        String string = type.getVariant()
                            .toString() + " " + type.getQualifiedName();

        long hash = 1125899906842597L; // prime
        final int len = string.length();
        for (int i = 0; i < len; i++) {
            hash = 4909L * hash + 7919L * string.charAt(i);
        }
        return hash;
    }

    public static String getClassName(PDeclaredDescriptor<?> type) {
        return camelCase("", type.getName());
    }

    public static String getClassName(PService service) {
        return camelCase("", service.getName());
    }

    public static String getJavaPackage(CProgram document) throws GeneratorException {
        String javaPackage = document.getNamespaceForLanguage("java");
        if (javaPackage == null) {
            throw new GeneratorException("No java namespace for thrift package " + document.getProgramName());
        }
        return javaPackage;
    }

    public static String getPackageClassPath(String javaPackage) throws GeneratorException {
        String[] parts = javaPackage.split("[.]");
        return Strings.join(File.separator, parts);
    }

    /**
     * Method to get the thrift file as a class name for a hazelcast factory.
     *
     * @param type PStructDescriptor with the information to fetch thrift info from.
     * @return class name for the hazelcast factory.
     */
    public static String getHazelcastFactory(PMessageDescriptor<?, ?> type) {
        return camelCase("", type.getProgramName()).concat("_Factory");
    }

    /**
     * Method to get the message constant for the thrift struct.
     *
     * @param type PStructDescriptor with the information to fetch message info from.
     * @return macro cased constant value of the message.
     */
    public static String getHazelcastClassId(PMessageDescriptor<?, ?> type) {
        return getHazelcastClassId(type.getName());
    }

    /**
     * Method to get the message constant for the thrift struct.
     *
     * @param name String with the class name to create format from.
     * @return macro cased constant value of the message.
     */
    public static String getHazelcastClassId(String name) {
        return macroCase(name).concat("_ID");
    }

    /**
     * Format a prefixed name as camelCase. The prefix is kept verbatim, while
     * tha name is split on '_' chars, and joined with each part capitalized.
     *
     * @param name   The name to camel-case.
     * @return theCamelCasedName
     */
    public static String camelCase(String name) {
        StringBuilder builder = new StringBuilder();

        String[] parts = name.split("[-._]");
        boolean first = true;
        int skipped = 0;
        for (String part : parts) {
            if (part.isEmpty()) {
                skipped++;
                continue;
            }
            if (first) {
                first = false;
                builder.append(part);
            } else if (skipped > 1) {
                builder.append('_');
                builder.append(part);
            } else {
                builder.append(Strings.capitalize(part));
            }
            skipped = 0;
        }

        return builder.toString();
    }

    /**
     * Format a prefixed name as camelCase. The prefix is kept verbatim, while
     * tha name is split on '_' chars, and joined with each part capitalized.
     *
     * @param prefix The prefix.
     * @param name   The name to camel-case.
     * @return theCamelCasedName
     */
    public static String camelCase(String prefix, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append(prefix);

        String[] parts = name.split("[-._]");
        int skipped = 0;
        for (String part : parts) {
            if (part.isEmpty()) {
                skipped++;
                continue;
            }
            if (skipped > 1) {
                builder.append('_');
                builder.append(part);
            } else {
                builder.append(Strings.capitalize(part));
            }
            skipped = 0;
        }
        return builder.toString();
    }

    /**
     * Format a prefixed name as MACRO_CASE.
     *
     * @param name   The name to macro-case.
     * @return THE_MACRO_CASED_NAME
     */
    public static String macroCase(String name) {
        String result = name.replaceAll("([A-Z])", "_$1");
        while( result.startsWith("_") ) {
            result = result.substring(1, result.length());
        }
        return result.toUpperCase();
    }

    public static String enumConst(PEnumValue value) {
        return Strings.c_case("", value.getName()).toUpperCase();
    }
}
