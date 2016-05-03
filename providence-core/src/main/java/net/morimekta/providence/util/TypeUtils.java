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

package net.morimekta.providence.util;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.util.Binary;
import net.morimekta.util.Strings;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Providence type utilities.
 */
public class TypeUtils {
    private static final String NULL = "null";

    /**
     * Stringify a message.
     *
     * @param message The message to stringify.
     * @return The resulting message.
     */
    public static String asString(PMessage<?> message) {
        if (message == null) {
            return NULL;
        }
        return message.asString();
    }

    /**
     * Make an object into a string using the typed tools here.
     *
     * @param o The object to stringify.
     * @return The resulting string.
     */
    public static String asString(Object o) {
        if (o == null) {
            return NULL;
        } else if (o instanceof PMessage) {
            return asString((PMessage<?>) o);
        } else {
            return Strings.asString(o);
        }
    }

    /**
     * Check if the two descriptors has the same qualified name, i..e
     * symbolically represent the same type.
     *
     * @param a The first type.
     * @param b The second type.
     * @return If the two types are the same.
     */
    public static boolean equalsQualifiedName(PDescriptor a, PDescriptor b) {
        if ((a == null) != (b == null)) {
            return false;
        }
        if (a == null) {
            return true;
        }
        return a.getQualifiedName(null)
                .equals(b.getQualifiedName(null));
    }

    private static <T extends PMessage<T>> int compare(T m1, T m2) {
        int c = 0;
        c = m1.descriptor()
              .getQualifiedName(null)
              .compareTo(m2.descriptor()
                           .getQualifiedName(null));
        if (c != 0) {
            return c;
        }
        for (PField field : m1.descriptor()
                                 .getFields()) {
            c = Boolean.compare(m1.has(field.getKey()), m2.has(field.getKey()));
            if (c != 0) {
                return c;
            }
            if (m1.has(field.getKey())) {
                c = compare((Comparable) m1.get(field.getKey()), (Comparable) m2.get(field.getKey()));
                if (c != 0) {
                    return c;
                }
            }
        }
        return 0;
    }

    /**
     * Compare two values to each other.
     *
     * @param o1 The first value.
     * @param o2 The second value.
     * @param <T> The object type.
     * @return The compare value (-1, 0 or 1).
     */
    public static <T extends Comparable<T>> int compare(T o1, T o2) {
        if (o1 == null || o2 == null) {
            return Boolean.compare(o1 != null, o2 != null);
        } else if (o1 instanceof Boolean && o2 instanceof Boolean) {
            return Boolean.compare((Boolean) o1, (Boolean) o2);
        } else if (o1 instanceof Short && o2 instanceof Short) {
            return Short.compare((Short) o1, (Short) o2);
        } else if (o1 instanceof Integer && o2 instanceof Integer) {
            return Integer.compare((Integer) o1, (Integer) o2);
        } else if (o1 instanceof Long && o2 instanceof Long) {
            return Long.compare((Long) o1, (Long) o2);
        } else if (o1 instanceof Double && o2 instanceof Double) {
            return Double.compare((Double) o1, (Double) o2);
        } else if (o1 instanceof String && o2 instanceof String) {
            return ((String) o1).compareTo((String) o2);
        } else if (o1 instanceof Binary && o2 instanceof Binary) {
            return ((Binary) o1).compareTo((Binary) o2);
        } else if (o1 instanceof PEnumValue && o2 instanceof PEnumValue) {
            return Integer.compare(((PEnumValue) o1).getValue(), ((PEnumValue) o2).getValue());
        } else if (o1 instanceof PMessage && o2 instanceof PMessage) {
            return compare((PMessage) o1, (PMessage) o2);
        } else if (o1 instanceof Map && o2 instanceof Map) {
            // Maps cannot be compared to each other.
        } else if (o1 instanceof Set && o2 instanceof Set) {
            // Sets cannot be compared to each other.
        } else if (o1 instanceof List && o2 instanceof List) {
            // Lists cannot be compared to each other.
        }
        return 0;
    }

}
