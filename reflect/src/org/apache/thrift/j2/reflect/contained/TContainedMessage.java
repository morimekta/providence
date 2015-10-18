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
import java.util.Map;

import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.util.TPrettyPrinter;
import org.apache.thrift.j2.util.TTypeUtils;
import org.apache.thrift.j2.TMessage;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 26.08.15
 */
public abstract class TContainedMessage<T extends TMessage<T>>
        implements TMessage<T> {
    protected final Map<Integer, Object> mFields;

    protected TContainedMessage(Map<Integer, Object> fields) {
        mFields = fields;
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
        if (o == null || !(o instanceof TContainedMessage)) {
            return false;
        }

        TContainedMessage other = (TContainedMessage) o;
        TStructDescriptor<?> type = other.descriptor();
        if (!descriptor().getQualifiedName(null).equals(type.getQualifiedName(null)) ||
            !descriptor().getVariant().equals(type.getVariant())) {
            return false;
        }

        for (TField<?> field : descriptor().getFields()) {
            int id = field.getKey();
            if (has(id) != other.has(id)) return false;
            if (!TTypeUtils.equals(get(id), other.get(id))) return false;
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
}
