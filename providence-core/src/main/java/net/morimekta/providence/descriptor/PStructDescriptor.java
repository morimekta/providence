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

package net.morimekta.providence.descriptor;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PMessageBuilderFactory;
import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.PType;

/**
 * The definition of a thrift structure.
 */
public abstract class PStructDescriptor<T extends PMessage<T>, F extends PField> extends PDeclaredDescriptor<T> {
    private final PMessageBuilderFactory<T> factory;
    private final boolean                   compactible;
    private final boolean                   simple;

    public PStructDescriptor(String packageName,
                             String name,
                             PMessageBuilderFactory<T> factory,
                             boolean simple,
                             boolean compactible) {
        super(packageName, name);

        this.factory = factory;
        this.simple = simple;
        this.compactible = compactible;
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
     * @return True iff the struct can be (de)serialized with compact message
     *         format.
     */
    public boolean isCompactible() {
        return compactible;
    }

    /**
     * @return True iff the struct is simple. A simple struct contains no containers, and no
     */
    public boolean isSimple() {
        return simple;
    }

    /**
     * @return The struct variant.
     */
    public PMessageVariant getVariant() {
        return PMessageVariant.STRUCT;
    }

    @Override
    public PType getType() {
        return PType.MESSAGE;
    }

    @Override
    public PMessageBuilder<T> builder() {
        return factory.builder();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof PStructDescriptor)) {
            return false;
        }
        PStructDescriptor<?, ?> other = (PStructDescriptor<?, ?>) o;
        if (!getQualifiedName(null).equals(other.getQualifiedName(null)) ||
            !getVariant().equals(other.getVariant()) ||
            getFields().length != other.getFields().length) {
            return false;
        }
        for (PField field : getFields()) {
            if (!field.equals(other.getField(field.getKey()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = PStructDescriptor.class.hashCode() +
                   getQualifiedName(null).hashCode() +
                   getVariant().hashCode();
        for (PField field : getFields()) {
            hash += field.hashCode();
        }
        return hash;
    }

    /**
     * Get the actual builder factory instance. For contained structs only.
     * @return The builder factory.
     */
    protected PMessageBuilderFactory<T> getFactoryInternal() {
        return factory;
    }
}
