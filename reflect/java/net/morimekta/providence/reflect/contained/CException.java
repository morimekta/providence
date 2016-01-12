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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.util.PTypeUtils;
import net.morimekta.providence.util.PPrettyPrinter;
import net.morimekta.providence.descriptor.PPrimitive;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class CException
        extends Throwable
        implements PMessage<CException> {
    private final CExceptionDescriptor mType;
    private final Map<Integer, Object>  mFields;

    protected CException(Builder builder) {
        mFields = Collections.unmodifiableMap(new LinkedHashMap<>(builder.mFields));
        mType = builder.mType;
    }

    @Override
    public boolean has(int key) {
        PField<?> field = descriptor().getField(key);
        if (field == null) {
            return false;
        }
        switch (field.getDescriptor().getType()) {
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
        PField<?> field = descriptor().getField(key);
        if (field == null) {
            return 0;
        }
        switch (field.getDescriptor().getType()) {
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
        PField<?> field = descriptor().getField(key);
        if (field != null) {
            Object value = mFields.get(key);
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
    public boolean isCompact() {
        if (!descriptor().isCompactible()) return false;
        boolean missing = false;
        for (PField<?> field : descriptor().getFields()) {
            if (has(field.getKey())) {
                if (missing) return false;
            } else {
                missing = true;
            }
        }
        return true;
    }

    @Override
    public boolean isSimple() {
        return descriptor().isSimple();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof CException)) {
            return false;
        }

        CException other = (CException) o;
        PStructDescriptor<?,?> type = other.descriptor();
        if (!descriptor().getQualifiedName(null).equals(type.getQualifiedName(null)) ||
            !descriptor().getVariant().equals(type.getVariant())) {
            return false;
        }

        for (PField<?> field : descriptor().getFields()) {
            int id = field.getKey();
            if (has(id) != other.has(id)) return false;
            if (PTypeUtils.equals(get(id), other.get(id))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        for (Map.Entry<Integer, Object> entry : mFields.entrySet()) {
            PField<?> field = descriptor().getField(entry.getKey());
            hash += PTypeUtils.hashCode(field, entry.getValue());
        }
        return hash;
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName(null) + asString();
    }

    @Override
    public String asString() {
        return new PPrettyPrinter("", "", "").format(this);
    }

    @Override
    public PMessageBuilder<CException> mutate() {
        return new Builder(mType);
    }

    @Override
    public CExceptionDescriptor descriptor() {
        return mType;
    }

    public static class Builder
            extends PMessageBuilder<CException> {
        private final CExceptionDescriptor mType;
        private final Map<Integer, Object>  mFields;

        public Builder(CExceptionDescriptor type) {
            mType = type;
            mFields = new TreeMap<>();
        }

        @Override
        public CException build() {
            return new CException(this);
        }

        @Override
        public boolean isValid() {
            return mFields.size() == 1;
        }

        @Override
        public Builder set(int key, Object value) {
            PField<?> field = mType.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            if (value != null) {
                mFields.put(field.getKey(), value);
            }
            return this;
        }

        @Override
        public Builder clear(int key) {
            mFields.remove(key);
            return this;
        }
    }
}
