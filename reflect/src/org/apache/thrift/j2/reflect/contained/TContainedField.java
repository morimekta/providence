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

import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TValueProvider;
import org.apache.thrift.j2.util.TTypeUtils;

import static org.apache.thrift.j2.util.TTypeUtils.equalsQualifiedName;

/**
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class TContainedField<T> implements TField<T> {
    private final String                 mComment;
    private final int                    mKey;
    private final boolean                mRequired;
    private final TDescriptorProvider<T> mTypeProvider;
    private final String                 mName;
    private final TValueProvider<T> mDefaultValue;

    public TContainedField(String comment,
                           int key,
                           boolean required,
                           String name,
                           TDescriptorProvider<T> typeProvider,
                           TValueProvider<T> defaultValue) {
        mComment = comment;
        mKey = key;
        mRequired = required;
        mTypeProvider = typeProvider;
        mName = name;
        mDefaultValue = defaultValue;
    }

    @Override
    public String getComment() {
        return mComment;
    }

    @Override
    public int getKey() {
        return mKey;
    }

    @Override
    public boolean getRequired() {
        return mRequired;
    }

    @Override
    public TType getType() {
        return mTypeProvider.descriptor().getType();
    }

    @Override
    public TDescriptor<T> getDescriptor() {
        return mTypeProvider.descriptor();
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean hasDefaultValue() {
        return mDefaultValue != null;
    }

    @Override
    public T getDefaultValue() {
        return hasDefaultValue() ? mDefaultValue.get() : null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(TField.class.getSimpleName())
               .append('{')
               .append(mKey)
               .append(": ");
        if (mRequired) {
            builder.append("required ");
        }
        builder.append(getDescriptor().getQualifiedName(null))
               .append(" ")
               .append(mName)
               .append("}");

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TContainedField)) {
            return false;
        }
        TContainedField<?> other = (TContainedField<?>) o;
        return mKey == other.mKey &&
               mRequired == other.mRequired &&
               // We cannot test that the types are deep-equals as it may have circular
               // containment.
               equalsQualifiedName(getDescriptor(), other.getDescriptor()) &&
               mName.equals(other.mName) &&
               TTypeUtils.equals(mDefaultValue, other.mDefaultValue);
    }

    @Override
    public int hashCode() {
        return TContainedField.class.hashCode() +
               getDescriptor().hashCode() +
               TTypeUtils.hashCode(mKey) +
               TTypeUtils.hashCode(mRequired) +
               TTypeUtils.hashCode(mName) +
               TTypeUtils.hashCode(getDefaultValue());
    }
}
