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

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;

import java.util.regex.Pattern;

/**
 *
 */
public class JAnnotation {
    public static final String DEPRECATED = "@Deprecated";

    private static final Pattern DEPRECATED_RE = Pattern.compile("[@][Dd]eprecated\\b", Pattern.MULTILINE);
    private static final Pattern COMPACT_RE    = Pattern.compile("[@][Cc]ompact\\b", Pattern.MULTILINE);

    public static boolean isDeprecated(PField<?> field) {
        return field.getComment() != null &&
               DEPRECATED_RE.matcher(field.getComment()).find();
    }

    public static boolean isDeprecated(JField field) {
        return field.hasComment() &&
               DEPRECATED_RE.matcher(field.comment()).find();
    }

    public static boolean isDeprecated(PDeclaredDescriptor<?> type) {
        return type.getComment() != null &&
               DEPRECATED_RE.matcher(type.getComment()).find();
    }

    public static boolean isDeprecated(PEnumValue<?> value) {
        return value.getComment() != null &&
               DEPRECATED_RE.matcher(value.getComment()).find();
    }

    public static boolean isCompact(PDeclaredDescriptor<?> type) {
        return type instanceof PStructDescriptor &&
               type.getComment() != null &&
               COMPACT_RE.matcher(type.getComment()).find();
    }
}
