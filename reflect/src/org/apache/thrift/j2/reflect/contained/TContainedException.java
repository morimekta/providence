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

package org.apache.thrift.j2.reflect.contained;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.util.TPrettyPrinter;
import org.apache.thrift.j2.util.TTypeUtils;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 07.09.15
 */
public class TContainedException
        extends Throwable
        implements TMessage<TContainedException> {
    private final TContainedExceptionDescriptor mType;
    private final Map<Integer, Object>          mFields;

    protected TContainedException(Builder builder) {
        mFields = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mFields));
        mType = builder.mType;
    }

    @Override
    public boolean has(int key) {
        TField<?> field = descriptor().getField(key);
        if (field == null) {
            return false;
        }
        switch (field.descriptor().getType()) {
            case MAP:
            case LIST:
            case SET:
                return num(key) > 0;
            default:
                return mFields.containsKey(key);
        }
    }

    @Override
    public int num(int key) {
        TField<?> field = descriptor().getField(key);
        if (field == null) {
            return 0;
        }
        switch (field.descriptor().getType()) {
            case MAP:
                Map<?, ?> value = (Map<?, ?>) mFields.get(key);
                return value == null ? 0 : value.size();
            case LIST:
            case SET:
                Collection<?> collection = (Collection<?>) mFields.get(key);
                return collection == null ? 0 : collection.size();
            default:
                // Non container fields are either present or not.
                return mFields.containsKey(key) ? 1 : 0;
        }
    }

    @Override
    public Object get(int key) {
        TField<?> field = descriptor().getField(key);
        if (field != null) {
            Object value = mFields.get(key);
            if (value != null) {
                return value;
            } else if (field.hasDefaultValue()) {
                return field.getDefaultValue();
            } else if (field.descriptor() instanceof TPrimitive) {
                return ((TPrimitive) field.descriptor()).getDefaultValue();
            }
        }
        return null;
    }

    @Override
    public boolean compact() {
        if (!descriptor().isCompactible()) return false;
        boolean missing = false;
        for (TField<?> field : descriptor().getFields()) {
            if (has(field.getKey())) {
                if (missing) return false;
            } else {
                missing = true;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TContainedException)) {
            return false;
        }

        TContainedException other = (TContainedException) o;
        TStructDescriptor<?> type = other.descriptor();
        if (!descriptor().getQualifiedName(null).equals(type.getQualifiedName(null)) ||
            !descriptor().getVariant().equals(type.getVariant())) {
            return false;
        }

        for (TField<?> field : descriptor().getFields()) {
            int id = field.getKey();
            if (has(id) != other.has(id)) return false;
            if (TTypeUtils.equals(get(id), other.get(id))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        for (Object o : mFields.values()) {
            hash += TTypeUtils.hashCode(o);
        }
        return hash;
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) +
               new TPrettyPrinter("", "", "").format(this);
    }

    @Override
    public TMessageBuilder<TContainedException> mutate() {
        return new Builder(mType);
    }

    @Override
    public boolean isValid() {
        return mFields.size() == 1;
    }

    @Override
    public TContainedExceptionDescriptor descriptor() {
        return mType;
    }

    public static class Builder
            extends TMessageBuilder<TContainedException> {
        private final TContainedExceptionDescriptor mType;
        private final Map<Integer, Object>          mFields;

        public Builder(TContainedExceptionDescriptor type) {
            mType = type;
            mFields = new TreeMap<>();
        }

        @Override
        public TContainedException build() {
            return new TContainedException(this);
        }

        @Override
        public boolean isValid() {
            return mFields.size() == 1;
        }

        @Override
        public Builder set(int key, Object value) {
            TField<?> field = mType.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            if (value != null) {
                mFields.put(field.getKey(), value);
            }
            return this;
        }
    }
}
