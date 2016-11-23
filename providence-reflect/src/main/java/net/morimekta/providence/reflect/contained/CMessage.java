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

package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.util.Binary;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Stein Eldar Johnsen
 * @since 26.08.15
 */
public abstract class CMessage<Message extends PMessage<Message, Field>, Field extends PField>
        implements PMessage<Message, Field> {
    private static final PrettySerializer PRETTY_SERIALIZER = new PrettySerializer("", "", "", ",", true, false);

    private final Map<Integer, Object> values;

    CMessage(Map<Integer, Object> fields) {
        values = fields;
    }

    @Override
    public boolean has(int key) {
        PField field = descriptor().getField(key);
        if (field == null) {
            return false;
        }
        return values.containsKey(key);
    }

    @Override
    public int num(int key) {
        PField field = descriptor().getField(key);
        if (field == null) {
            return 0;
        }

        // Non-present containers are empty.
        if (!values.containsKey(key)) {
            return 0;
        }

        switch (field.getDescriptor()
                     .getType()) {
            case MAP:
                return ((Map<?, ?>) values.get(key)).size();
            case LIST:
            case SET:
                return ((Collection<?>) values.get(key)).size();
            default:
                // present non-containers also empty.
                return 0;
        }
    }

    @Override
    public Object get(int key) {
        PField field = descriptor().getField(key);
        if (field != null) {
            Object value = values.get(key);
            if (value != null) {
                return value;
            } else if (field.hasDefaultValue()) {
                return field.getDefaultValue();
            } else if (field.getDescriptor() instanceof PPrimitive) {
                return ((PPrimitive) field.getDescriptor()).getDefaultValue();
            }
        }
        return null;
    }

    @Override
    public boolean compact() {
        if (!descriptor().isCompactible()) {
            return false;
        }
        boolean missing = false;
        for (PField field : descriptor().getFields()) {
            if (has(field.getKey())) {
                if (missing) {
                    return false;
                }
            } else {
                missing = true;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof CMessage)) {
            return false;
        }

        CMessage other = (CMessage) o;
        PStructDescriptor<?, ?> type = other.descriptor();
        if (!descriptor().getQualifiedName(null)
                         .equals(type.getQualifiedName(null)) || !descriptor().getVariant()
                                                                              .equals(type.getVariant())) {
            return false;
        }

        for (PField field : descriptor().getFields()) {
            int id = field.getKey();
            if (has(id) != other.has(id)) {
                return false;
            }
            if (!Objects.equals(get(id), other.get(id))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        for (Map.Entry<Integer, Object> entry : values.entrySet()) {
            PField field = descriptor().getField(entry.getKey());
            hash += Objects.hash(field, entry.getValue());
        }
        return hash;
    }

    @Override
    public int compareTo(Message other) {
        return compare((Message) this, other);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + asString();
    }

    @Override
    public String asString() {
        return asString((PMessage) this);
    }

    /**
     * Prints a compact string representation of the message.
     *
     * @param message The message to stringify.
     * @param <T> The message type.
     * @param <F> The field type.
     * @return The resulting string.
     */
    protected static <T extends PMessage<T, F>, F extends PField> String asString(T message) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PRETTY_SERIALIZER.serialize(baos, message);
        return new String(baos.toByteArray(), UTF_8);
    }

    /**
     * Compare two values to each other.
     *
     * @param o1 The first value.
     * @param o2 The second value.
     * @param <T> The object type.
     * @return The compare value (-1, 0 or 1).
     */
    protected static <T extends Comparable<T>> int compare(T o1, T o2) {
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
            return compareMessages((PMessage) o1, (PMessage) o2);
        } else if (o1 instanceof Map && o2 instanceof Map) {
            // Maps cannot be compared to each other.
        } else if (o1 instanceof Set && o2 instanceof Set) {
            // Sets cannot be compared to each other.
        } else if (o1 instanceof List && o2 instanceof List) {
            // Lists cannot be compared to each other.
        }
        return 0;
    }

    private static <T extends PMessage<T, F>, F extends PField> int compareMessages(T m1, T m2) {
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

}
