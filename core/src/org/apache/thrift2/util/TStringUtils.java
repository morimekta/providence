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

package org.apache.thrift2.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.apache.thrift2.util.io.TerminatedInputStream;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 18.09.15
 */
public class TStringUtils {
    private final static int BUFFER_SIZE = 4096; // 2 << 12 / 4k

    /**
     * Join set of strings with delimiter.
     *
     * @param delimiter The delimiter.
     * @param strings The strings to join.
     * @return The joined string.
     */
    public static String join(String delimiter, String... strings) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String string : strings) {
            if (first) first = false;
            else builder.append(delimiter);
            builder.append(string);
        }
        return builder.toString();
    }

    /**
     * Join collection with delimiter.
     *
     * @param delimiter The delimiter.
     * @param strings The string collection to join.
     * @return The joined string.
     */
    public static String join(String delimiter, Collection<String> strings) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String string : strings) {
            if (first) first = false;
            else builder.append(delimiter);
            builder.append(string);
        }
        return builder.toString();
    }

    /**
     * Check if the string is representing an integer (or long) value.
     *
     * @param key The key to check if is an integer.
     * @return True if key is an integer.
     */
    public static boolean isInteger(String key) {
        return key.matches("[0-9]+");
    }

    /**
     * Make a hex string from a byte array.
     *
     * @param bytes The bytes to hexify.
     * @return The hex string.
     */
    public static String toHexString(final byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            builder.append(String.format("%02x", bytes[i]));
        }
        return builder.toString();
    }

    /**
     * Parse a hex string as bytes.
     *
     * @param hex The hex string.
     * @return The corresponding bytes.
     */
    public static byte[] fromHexString(String hex) {
        if (hex.length() % 2 != 0) throw new AssertionError("Wrong hex string length");
        int len = hex.length() / 2;
        byte[] out = new byte[len];
        for (int i = 0; i < len; ++i) {
            int pos = i * 2;
            String part = hex.substring(pos, pos + 2);
            out[i] = (byte) Integer.parseInt(part, 16);
        }
        return out;
    }

    /**
     * Read next string from input stream.
     *
     * @param is The input stream to read.
     * @return The resulting string.
     * @throws IOException
     */
    public static String readString(InputStream is) throws IOException {
        return readString(is, "\0");
    }

    /**
     * Read next string from input stream.
     *
     * @param is The input stream to read.
     * @param term Terminator character.
     * @return The string up until, but not including the terminator.
     * @throws IOException
     */
    public static String readString(InputStream is, String term) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        @SuppressWarnings("resource")
        TerminatedInputStream tis = new TerminatedInputStream(is, term.getBytes(), null);

        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = tis.read(buffer, 0, BUFFER_SIZE)) > 0) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Multiply a string N times.
     *
     * @param s The string to multiply.
     * @param num N
     * @return The result.
     */
    public static String times(String s, int num) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; ++i) {
            builder.append(s);
        }
        return builder.toString();
    }

    /**
     * Format a prefixed name as camelCase. The prefix is kept verbatim, while
     * tha name is split on '_' chars, and joined with each part capitalized.
     *
     * @param prefix The prefix.
     * @param name The name to camel-case.
     * @return theCamelCasedName
     */
    public static String camelCase(String prefix, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append(prefix);

        String[] parts = name.split("[_]");
        for (String part : parts) {
            if (part.isEmpty()) continue;
            builder.append(capitalize(part));
        }
        return builder.toString();
    }

    /**
     * Format a prefixed name as c_case. The prefix is kept verbatim, while
     * the name has a '_' character inserted before each upper-case letter,
     * not including the first character. Then the whole thing is lower-cased.
     *
     * Note that this will mangle upper-case abbreviations.
     *
     * @param prefix The prefix.
     * @param name The name to c-case.
     * @return the_c_cased_name
     */
    public static String c_case(String prefix, String name) {
        // Assume we insert at most 4 '_' chars for a majority of names.
        StringBuilder builder = new StringBuilder(prefix.length() + name.length() + 4);
        builder.append(prefix);
        // This does the opposite of camelCase, and inserts a '_' before each
        // capital letter, and lower cases the whole string. But avoids adding
        // separator before the first character (by lower casing the first
        // character of the name.
        if (Character.isUpperCase(name.charAt(0))) {
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }

        for (char c : name.toCharArray()) {
            if (Character.isUpperCase(c)) {
                builder.append('_');
            }
            builder.append(Character.toLowerCase(c));
        }

        return builder.toString();
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
