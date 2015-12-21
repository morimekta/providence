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

package org.apache.thrift.j2.descriptor;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TMessageVariant;
import org.apache.thrift.j2.TType;

/**
 * The definition of a thrift structure.
 */
public abstract class TStructDescriptor<T extends TMessage<T>, F extends TField>
        extends TDeclaredDescriptor<T> {
    private final TMessageBuilderFactory<T> mProvider;
    private final boolean                   mCompactible;
    private final boolean                   mSimple;

    public TStructDescriptor(String comment,
                             String packageName,
                             String name,
                             TMessageBuilderFactory<T> provider,
                             boolean simple,
                             boolean compactible) {
        super(comment, packageName, name);

        mProvider = provider;
        mSimple = simple;
        mCompactible = compactible;
    }

    /**
     * @return An unmodifiable list of fields that the struct holds.
     */
    public abstract F[] getFields();

    /**
     * @param name Name of field to get.
     * @return The field if present.
     */
    public abstract F getField(String name);

    /**
     * @param key The ID of the field to get.
     * @return The field if present.
     */
    public abstract F getField(int key);

    /**
     * @return True iff the struct can be (de)serialized with isCompact message
     *         format.
     */
    public boolean isCompactible() {
        return mCompactible;
    }

    /**
     * @return True iff the struct is simple. A simple struct contains no containers, and no
     */
    public boolean isSimple() {
        return mSimple;
    }

    /**
     * @return The struct variant.
     */
    public TMessageVariant getVariant() {
        return TMessageVariant.STRUCT;
    }

    @Override
    public TType getType() {
        return TType.MESSAGE;
    }

    @Override
    public TMessageBuilderFactory<T> factory() {
        return mProvider;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TStructDescriptor)) {
            return false;
        }
        TStructDescriptor<?,?> other = (TStructDescriptor<?,?>) o;
        if (!getQualifiedName(null).equals(other.getQualifiedName(null)) ||
            !getVariant().equals(other.getVariant()) ||
            getFields().length != other.getFields().length) {
            return false;
        }
        for (TField<?> field : getFields()) {
            if (!field.equals(other.getField(field.getKey()))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = TStructDescriptor.class.hashCode() +
                   getQualifiedName(null).hashCode() +
                   getVariant().hashCode();
        for (TField<?> field : getFields()) {
            hash += field.hashCode();
        }
        return hash;
    }
}
