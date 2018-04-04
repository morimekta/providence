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
package net.morimekta.providence.reflect.util;

import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Simple utility for type checking and matching.
 */
public class ReflectionUtils {
    public static boolean isThriftFile(@Nonnull File file) {
        return isThriftFile(file.getName());
    }

    public static boolean isThriftFile(@Nonnull String filePath) {
        // This is in case windows has default upper-cased the file name.
        return programNameFromPath(filePath).length() > 0;
    }

    @Nonnull
    public static String programNameFromPath(@Nonnull String filePath) {
        String lowerCased = filePath.toLowerCase(Locale.US);

        if (!lowerCased.endsWith(".providence") &&
            !lowerCased.endsWith(".thrift") &&
            !lowerCased.endsWith(".thr") &&
            !lowerCased.endsWith(".pvd")) {
            return "";
        }
        if (filePath.contains("/") || filePath.contains("\\")) {
            filePath = filePath.replaceAll(".*[/\\\\]", "");
        }
        if (lowerCased.endsWith(".providence")) {
            filePath = filePath.substring(0, filePath.length() - 11);
        } else if (lowerCased.endsWith(".thrift")) {
            filePath = filePath.substring(0, filePath.length() - 7);
        } else if (lowerCased.endsWith(".thr") || lowerCased.endsWith(".pvd")) {
            filePath = filePath.substring(0, filePath.length() - 4);
        }
        return filePath.replaceAll("[-.]", "_");
    }

    @Nonnull
    public static String longestCommonPrefixPath(Collection<String> paths) {
        if (paths.size() == 0) throw new IllegalArgumentException("Empty paths");
        String prefix = paths.iterator().next();
        for (String s : paths) {
            prefix = Strings.commonPrefix(s, prefix);
        }
        if (prefix.contains("/")) {
            return prefix.replaceAll("/[^/]*$", "/");
        }
        return "";
    }

    @Nonnull
    public static List<String> stripCommonPrefix(List<String> paths) {
        String prefix = longestCommonPrefixPath(paths);
        if (prefix.length() > 0) {
            return paths.stream()
                        .map(s -> s.substring(prefix.length()))
                        .collect(Collectors.toList());
        }
        return paths;
    }

    private ReflectionUtils() {}
}
