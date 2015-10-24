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

import org.apache.thrift.j2.util.TTypeUtils;
import org.apache.thrift.j2.TEnumBuilderFactory;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.TType;

import java.util.LinkedList;
import java.util.List;

/**
 * The definition of a thrift enum.
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class TEnumDescriptor<T extends TEnumValue<T>>
        extends TDeclaredDescriptor<T> {
    // According to doc it's 1, but the current c++ compiler makes it 0...
    public static final int DEFAULT_FIRST_VALUE = 0;

    private final List<T>    mValues;
    private final TEnumBuilderFactory<T> mProvider;

    private static <T> List<T> valuesList(T[] values) {
        LinkedList<T> list = new LinkedList<>();
        for (T value : values) {
            list.add(value);
        }
        return list;
    }

    public TEnumDescriptor(String comment,
                           String packageName,
                           String name,
                           T[] values,
                           TEnumBuilderFactory<T> provider) {
        this(comment, packageName, name, valuesList(values), provider);
    }

    public TEnumDescriptor(String comment,
                           String packageName,
                           String name,
                           List<T> values,
                           TEnumBuilderFactory<T> provider) {
        super(comment, packageName, name);
        mValues = values;
        mProvider = provider;
    }

    @Override
    public TType getType() {
        return TType.ENUM;
    }

    public List<T> getValues() {
        return mValues;
    }

    public T getValueById(int id) {
        for (T value : getValues()) {
            if (value.getValue() == id) {
                return value;
            }
        }
        return null;
    }

    public T getValueByName(String name) {
        for (T value : getValues()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public TEnumBuilderFactory<T> factory() {
        return mProvider;
    }

    @Override
    public String toString() {
        return getQualifiedName(null);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TEnumDescriptor)) {
            return false;
        }
        TEnumDescriptor<?> other = (TEnumDescriptor<?>) o;
        if (!TTypeUtils.equals(getComment(), other.getComment()) ||
            !getQualifiedName(null).equals(other.getQualifiedName(null)) ||
            getValues().size() != other.getValues().size()) {
            return false;
        }
        for (TEnumValue<?> value : getValues()) {
            TEnumValue<?> ovI = other.getValueById(value.getValue());
            if (!value.equals(ovI)) {
                return false;
            }
        }

        return true;
    }
}
