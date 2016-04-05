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

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Providence type utilities.
 */
public class PTypeUtils {
    /**
     * Make a minimal printable string from a double value.
     *
     * @param d The double value.
     * @return The string value.
     */
    public static String toString(double d) {
        long l = (long) d;
        if (d == l) {
            // actually an integer or long value.
            return Long.toString(l);
        } else if (d > ((10 << 9) - 1) || (1 / d) > (10 << 6)) {
            // Scientific notation should be used.
            return new DecimalFormat("0.#########E0").format(d);
        } else {
            return Double.toString(d);
        }
    }

    /**
     * Make a minimal printable string from a binary value.
     *
     * @param bytes The binary value.
     * @return The string value.
     */
    public static String toString(Binary bytes) {
        if (bytes == null) {
            return NULL;
        }
        return String.format("b64(%s)", bytes.toBase64());
    }

    /**
     * Make a printable string from a collection using the tools here.
     *
     * @param collection The collection to stringify.
     * @return The collection string value.
     */
    public static String toString(Collection<?> collection) {
        if (collection == null) {
            return NULL;
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        boolean first = true;
        for (Object item : collection) {
            if (first) {
                first = false;
            } else {
                builder.append(',');
            }
            builder.append(toString(item));
        }
        builder.append(']');
        return builder.toString();
    }

    /**
     * Make a minimal printable string value from a typed map.
     *
     * @param map The map to stringify.
     * @return The resulting string.
     */
    public static String toString(Map<?, ?> map) {
        if (map == null) {
            return NULL;
        }
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append(',');
            }
            builder.append(toString(entry.getKey()))
                   .append(':')
                   .append(toString(entry.getValue()));
        }
        builder.append('}');
        return builder.toString();
    }

    /**
     * Stringify a message.
     *
     * @param message The message to stringify.
     * @return The resulting message.
     */
    public static String toString(PMessage<?> message) {
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
    public static String toString(Object o) {
        if (o == null) {
            return NULL;
        } else if (o instanceof Map) {
            return toString((Map<?, ?>) o);
        } else if (o instanceof Collection) {
            return toString((Collection<?>) o);
        } else if (o instanceof Binary) {
            return toString((Binary) o);
        } else if (o instanceof PMessage) {
            return toString((PMessage<?>) o);
        } else if (o instanceof Double) {
            return toString(((Double) o).doubleValue());
        } else {
            return o.toString();
        }
    }

    /**
     * Check if two providence value objects are equal.
     *
     * @param v1 The first object.
     * @param v2 The second object.
     * @return True iff they are the same.
     */
    public static boolean equals(Object v1, Object v2) {
        if (v1 == v2) {
            return true;
        }
        if ((v1 == null) != (v2 == null)) {
            return false; // only one is null
        } else if (v1 instanceof List && v2 instanceof List) {
            List<?> l1 = (List<?>) v1;
            List<?> l2 = (List<?>) v2;
            if (l1.size() != l2.size()) {
                return false;
            }
            for (int i = 0; i < l1.size(); ++i) {
                if (!equals(l1.get(i), l2.get(i))) {
                    return false;
                }
            }
            return true;
        } else if (v1 instanceof Set && v2 instanceof Set) {
            Set<?> s1 = (Set<?>) v1;
            Set<?> s2 = (Set<?>) v2;
            // Maps are equal-checked based on content only, so Tree vs
            // LinkedHash vs Hash should not matter.
            if (s1.size() != s2.size()) {
                return false;
            }
            for (Object o : s1) {
                if (!s2.contains(o)) {
                    return false;
                }
            }
            return true;
        } else if (v1 instanceof Map && v2 instanceof Map) {
            Map<?, ?> m1 = (Map<?, ?>) v1;
            Map<?, ?> m2 = (Map<?, ?>) v2;
            // Maps are equal-checked based on content only, so Tree vs
            // LinkedHash vs Hash should not matter.
            if (m1.size() != m2.size()) {
                return false;
            }
            for (Object key : m1.keySet()) {
                if (!equals(m1.get(key), m2.get(key))) {
                    return false;
                }
            }
            return true;
        } else {
            return v1.equals(v2);
        }
    }

    /**
     * Make a typed hashcode for providence message fields.
     *
     * @param field The field descriptor.
     * @param object The field value.
     * @return The hash code.
     */
    public static int hashCode(PField<?> field, Object object) {
        return hashCode(field) * hashCode(object);
    }

    /**
     * Make a type-safe hash code for an object.
     *
     * @param object The object to hash.
     * @return The hash code.
     */
    public static int hashCode(Object object) {
        if (object == null) {
            return 1;
        }
        if (object instanceof List) {
            return hashCodeList((List<?>) object);
        }
        if (object instanceof Set) {
            return hashCodeSet((Set<?>) object);
        }
        if (object instanceof Map) {
            return hashCodeMap((Map<?, ?>) object);
        }
        return object.getClass()
                     .hashCode() * object.hashCode();
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

    private static <T> int hashCodeList(List<T> list) {
        int hash = List.class.hashCode();
        int i = 31;
        for (T t : list) {
            hash ^= (++i * t.getClass()
                            .hashCode() * hashCode(t));
        }
        return hash;
    }

    private static final String NULL = "null";

    private static <T> int hashCodeSet(Set<T> list) {
        int hash = Set.class.hashCode();
        for (T t : list) {
            hash ^= t.getClass()
                     .hashCode() * hashCode(t);
        }
        return hash;
    }

    private static <K, V> int hashCodeMap(Map<K, V> map) {
        int hash = Map.class.hashCode();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            hash ^= hashCode(entry.getKey()) * hashCode(entry.getValue());
        }
        return hash;
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
        for (PField<?> field : m1.descriptor()
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
        } else if (o2 instanceof Map && o2 instanceof Map) {
            // Maps cannot be compared to each other.
        } else if (o2 instanceof Set && o2 instanceof Set) {
            // Sets cannot be compared to each other.
        } else if (o2 instanceof List && o2 instanceof List) {
            // Lists cannot be compared to each other.
        }
        return 0;
    }

}
