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

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.util.PrettyPrinter;
import net.morimekta.providence.util.TypeUtils;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class CException extends Throwable implements PMessage<CException> {
    private final CExceptionDescriptor descriptor;
    private final Map<Integer, Object> values;

    private CException(Builder builder) {
        values = ImmutableMap.copyOf(builder.values);
        descriptor = builder.descriptor;
    }

    @Override
    public boolean has(int key) {
        PField field = descriptor().getField(key);
        if (field == null) {
            return false;
        }
        switch (field.getDescriptor()
                     .getType()) {
            case MAP:
            case LIST:
            case SET:
                return num(key) > 0;
            default:
                return values.containsKey(key);
        }
    }

    @Override
    public int num(int key) {
        PField field = descriptor().getField(key);
        if (field == null) {
            return 0;
        }
        switch (field.getDescriptor()
                     .getType()) {
            case MAP:
                Map<?, ?> value = (Map<?, ?>) values.get(key);
                return value == null ? 0 : value.size();
            case LIST:
            case SET:
                Collection<?> collection = (Collection<?>) values.get(key);
                return collection == null ? 0 : collection.size();
            default:
                // Non container fields are either present or not.
                return values.containsKey(key) ? 1 : 0;
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
        if (o == null || !(o instanceof CException)) {
            return false;
        }

        CException other = (CException) o;
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
            if (Objects.equals(get(id), other.get(id))) {
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
            hash ^= Objects.hash(field, entry.getValue());
        }
        return hash;
    }

    @Override
    public int compareTo(CException other) {
        return TypeUtils.compare(this, other);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + asString();
    }

    @Override
    public String asString() {
        return new PrettyPrinter("", "", "").format(this);
    }

    @Override
    public PMessageBuilder<CException> mutate() {
        return new Builder(descriptor);
    }

    @Override
    public CExceptionDescriptor descriptor() {
        return descriptor;
    }

    public static class Builder extends PMessageBuilder<CException> {
        private final CExceptionDescriptor descriptor;
        private final Map<Integer, Object> values;

        public Builder(CExceptionDescriptor type) {
            descriptor = type;
            values = new TreeMap<>();
        }

        @Override
        public CException build() {
            return new CException(this);
        }

        @Override
        public boolean isValid() {
            return values.size() == 1;
        }

        @Override
        public Builder set(int key, Object value) {
            PField field = descriptor.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            if (value != null) {
                values.put(field.getKey(), value);
            }
            return this;
        }

        @Override
        public Builder addTo(int key, Object value) {
            PField field = descriptor.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            if (value != null) {
                if (field.getType() == PType.LIST) {
                    @SuppressWarnings("unchecked")
                    List<Object> list = (List<Object>) values.get(field.getKey());
                    if (list == null) {
                        list = new LinkedList<>();
                        values.put(field.getKey(), list);
                    }
                    list.add(value);
                } else if (field.getType() == PType.SET) {
                    @SuppressWarnings("unchecked")
                    Set<Object> set = (Set<Object>) values.get(field.getKey());
                    if (set == null) {
                        set = new HashSet<>();
                        values.put(field.getKey(), set);
                    }
                    set.add(value);
                }
            }
            return this;
        }

        @Override
        public Builder clear(int key) {
            values.remove(key);
            return this;
        }
    }
}
