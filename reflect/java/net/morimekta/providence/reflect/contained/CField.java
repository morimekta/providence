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

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PValueProvider;
import net.morimekta.providence.util.PTypeUtils;

/**
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class CField<T> implements PField<T> {
    private final String                 mComment;
    private final int                    mKey;
    private final boolean                mRequired;
    private final PDescriptorProvider<T> mTypeProvider;
    private final String                 mName;
    private final PValueProvider<T> mDefaultValue;

    public CField(String comment,
                  int key,
                  boolean required,
                  String name,
                  PDescriptorProvider<T> typeProvider,
                  PValueProvider<T> defaultValue) {
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
    public PType getType() {
        return mTypeProvider.descriptor().getType();
    }

    @Override
    public PDescriptor<T> getDescriptor() {
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
        builder.append(PField.class.getSimpleName())
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
        if (o == null || !(o instanceof CField)) {
            return false;
        }
        CField<?> other = (CField<?>) o;
        return mKey == other.mKey &&
               mRequired == other.mRequired &&
               // We cannot test that the types are deep-equals as it may have circular
               // containment.
               PTypeUtils.equalsQualifiedName(getDescriptor(), other.getDescriptor()) &&
               mName.equals(other.mName) &&
               PTypeUtils.equals(mDefaultValue, other.mDefaultValue);
    }

    @Override
    public int hashCode() {
        return CField.class.hashCode() +
               getDescriptor().hashCode() +
               PTypeUtils.hashCode(mKey) +
               PTypeUtils.hashCode(mRequired) +
               PTypeUtils.hashCode(mName) +
               PTypeUtils.hashCode(getDefaultValue());
    }
}
