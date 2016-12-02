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

/**
 * Simple utility for type checking and matching.
 */
public class ReflectionUtils {
    public static boolean isThriftFile(String filePath) {
        // This is in case windows has default upper-cased the file name.
        filePath = filePath.toLowerCase();

        return filePath.endsWith(".providence") ||
               filePath.endsWith(".thrift") ||
               filePath.endsWith(".thr") ||
               filePath.endsWith(".pvd");
    }

    public static String programNameFromPath(String filePath) {
        String lowerCased = filePath.toLowerCase();
        if (lowerCased.endsWith(".providence")) {
            filePath = filePath.substring(0, filePath.length() - 11);
        } else if (lowerCased.endsWith(".thrift")) {
            filePath = filePath.substring(0, filePath.length() - 7);
        } else if (lowerCased.endsWith(".thr") || lowerCased.endsWith(".pvd")) {
            filePath = filePath.substring(0, filePath.length() - 4);
        }
        if (filePath.contains("/")) {
            filePath = filePath.replaceAll(".*[/]", "");
        }

        return filePath.replaceAll("[-.]", "_");
    }
}
