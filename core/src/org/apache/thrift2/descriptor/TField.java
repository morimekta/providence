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

package org.apache.thrift2.descriptor;

import org.apache.thrift2.TType;
import org.apache.thrift2.util.TTypeUtils;

import static org.apache.thrift2.util.TTypeUtils.equalsQualifiedName;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 25.08.15
 */
public class TField<T> implements Comparable<TField<T>>{
    private final String                 mComment;
    private final int                    mKey;
    private final boolean                mRequired;
    private final TDescriptorProvider<T> mTypeProvider;
    private final String                 mName;
    private final TValueProvider<T>      mDefaultValue;

    public TField(String comment,
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

    /**
     * The type comment is the last block of comment written before
     * the type declaration. Comments on the same line, after the
     * declaration is ignored.
     *
     * @return The comment string containing all formatting (not
     *         including the comment delimiter and the leading space.
     */
    public String getComment() {
        return mComment;
    }

    public int getKey() {
        return mKey;
    }

    public boolean getRequired() {
        return mRequired;
    }

    public TType getType() {
        return mTypeProvider.descriptor().getType();
    }

    public TDescriptor descriptor() {
        return mTypeProvider.descriptor();
    }

    public String getName() {
        return mName;
    }

    public boolean hasDefaultValue() {
        return mDefaultValue != null;
    }

    public Object getDefaultValue() {
        return hasDefaultValue() ? mDefaultValue.get() : null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName())
               .append('{')
               .append(mKey)
               .append(": ");
        if (mRequired) {
            builder.append("required ");
        }
        builder.append(descriptor().getQualifiedName(null))
               .append(" ")
               .append(mName)
               .append("}");

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TField)) {
            return false;
        }
        TField<?> other = (TField<?>) o;
        return mKey == other.mKey &&
               mRequired == other.mRequired &&
               // We cannot test that the types are deep-equals as it may have circular
               // containment.
               equalsQualifiedName(descriptor(), other.descriptor()) &&
               mName.equals(other.mName) &&
               TTypeUtils.equals(mDefaultValue, other.mDefaultValue);
    }

    @Override
    public int hashCode() {
        return TField.class.hashCode() +
               descriptor().hashCode() +
               TTypeUtils.hashCode(mKey) +
               TTypeUtils.hashCode(mRequired) +
               TTypeUtils.hashCode(mName) +
               TTypeUtils.hashCode(getDefaultValue());
    }

    @Override
    @SuppressWarnings("rawtypes")
    public int compareTo(TField o) {
        return Integer.compare(mKey, o.getKey());
    }
}
