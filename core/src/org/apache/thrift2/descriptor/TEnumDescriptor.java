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

import org.apache.thrift2.TEnumBuilderFactory;
import org.apache.thrift2.TEnumValue;
import org.apache.thrift2.TType;
import org.apache.thrift2.util.TTypeUtils;

import java.util.List;

/**
 * The definition of a thrift enum.
 *
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 25.08.15
 */
public class TEnumDescriptor<T extends TEnumValue<T>>
        extends TDeclaredDescriptor<T> {
    // According to doc it's 1, but the current c++ compiler makes it 0...
    public static final int DEFAULT_FIRST_VALUE = 0;

    public final static class Value {
        private final String mComment;
        private final String mName;
        private final int    mValue;

        public Value(String comment, String name, int value) {
            mComment = comment;
            mName = name;
            mValue = value;
        }

        public String getComment() {
            return mComment;
        }

        public String getName() {
            return mName;
        }

        public int getValue() {
            return mValue;
        }

        @Override
        public String toString() {
            return String.format("EnumValue{%s,%d}", mName, mValue);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Value)) {
                return false;
            }
            Value other = (Value) o;
            return mValue == other.mValue &&
                   mName.equals(other.mName);
        }
    }

    private final List<Value>            mValues;
    private final TEnumBuilderFactory<T> mProvider;

    public TEnumDescriptor(String comment,
                           String packageName,
                           String name,
                           List<Value> values,
                           TEnumBuilderFactory<T> provider) {
        super(comment, packageName, name);
        mValues = values;
        mProvider = provider;
    }

    @Override
    public TType getType() {
        return TType.ENUM;
    }

    public List<Value> getValues() {
        return mValues;
    }

    public Value getValueById(int id) {
        for (Value value : getValues()) {
            if (value.getValue() == id) {
                return value;
            }
        }
        return null;
    }

    public Value getValueByName(String name) {
        for (Value value : getValues()) {
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
        for (Value value : getValues()) {
            Value ovI = other.getValueById(value.getValue());
            if (!value.equals(ovI)) {
                return false;
            }
        }

        return true;
    }
}
