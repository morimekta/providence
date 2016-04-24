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

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumBuilderFactory;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PType;
import net.morimekta.providence.util.PTypeUtils;

/**
 * The definition of a thrift enum.
 */
public abstract class PEnumDescriptor<T extends PEnumValue<T>> extends PDeclaredDescriptor<T> {
    // According to doc it's 1, but the current c++ compiler makes it 0...
    public static final int DEFAULT_FIRST_VALUE = 0;

    private final PEnumBuilderFactory<T> factory;

    public PEnumDescriptor(String packageName, String name, PEnumBuilderFactory<T> provider) {
        super(packageName, name);
        factory = provider;
    }

    @Override
    public PType getType() {
        return PType.ENUM;
    }

    /**
     * @return The array of enum instances.
     */
    public abstract T[] getValues();

    /**
     * @param id Value to look up enum from.
     * @return Enum if found, null otherwise.
     */
    public abstract T getValueById(int id);

    /**
     * @param name Name to look up enum from.
     * @return Enum if found, null otherwise.
     */
    public abstract T getValueByName(String name);

    @Override
    public PEnumBuilder<T> builder() {
        return factory.builder();
    }

    @Override
    public String toString() {
        return getQualifiedName(null);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof PEnumDescriptor)) {
            return false;
        }
        PEnumDescriptor<?> other = (PEnumDescriptor<?>) o;
        if (!getQualifiedName(null).equals(other.getQualifiedName(null)) ||
            getValues().length != other.getValues().length) {
            return false;
        }
        for (PEnumValue<?> value : getValues()) {
            PEnumValue<?> ovI = other.getValueById(value.getValue());
            if (!value.equals(ovI)) {
                return false;
            }
        }

        return true;
    }

    protected PEnumBuilderFactory<T> getFactoryInternal() {
        return factory;
    }
}
