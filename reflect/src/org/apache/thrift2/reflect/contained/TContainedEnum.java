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

package org.apache.thrift2.reflect.contained;

import org.apache.thrift2.TEnumBuilder;
import org.apache.thrift2.TEnumValue;
import org.apache.thrift2.descriptor.TEnumDescriptor;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 26.08.15
 */
public class TContainedEnum
        implements TEnumValue<TContainedEnum> {
    private final int                             mValue;
    private final TEnumDescriptor<TContainedEnum> mType;

    public TContainedEnum(int value, TEnumDescriptor<TContainedEnum> type) {
        mValue = value;
        mType = type;
    }

    @Override
    public int getValue() {
        return mValue;
    }

    @Override
    public TEnumDescriptor<TContainedEnum> descriptor() {
        return mType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TContainedEnum)) {
            return false;
        }
        TContainedEnum other = (TContainedEnum) o;
        return other.descriptor().equals(mType) && other.getValue() == mValue;
    }

    @Override
    public String toString() {
        for (TEnumDescriptor.Value value : mType.getValues()) {
            if (value.getValue() == mValue) {
                return value.getName();
            }
        }
        return mType.getQualifiedName(null) + "(" + mValue + ")";
    }

    public static class Builder
            extends TEnumBuilder<TContainedEnum> {
        private final TEnumDescriptor<TContainedEnum> mType;

        private int mValue = -1;  // Illegal enum value.

        public Builder(TEnumDescriptor<TContainedEnum> type) {
            mType = type;
        }

        @Override
        public TContainedEnum build() {
            if (isValid()) {
                return new TContainedEnum(mValue, mType);
            }
            // throw new IllegalArgumentException("no such enumeration " +
            // name);
            return null;
        }

        @Override
        public boolean isValid() {
            for (TEnumDescriptor.Value value : mType.getValues()) {
                if (value.getValue() == mValue) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Builder setByValue(int value) {
            mValue = value;
            return this;
        }

        @Override
        public Builder setByName(String name) {
            for (TEnumDescriptor.Value value : mType.getValues()) {
                if (value.getName().equals(name)) {
                    mValue = value.getValue();
                    return this;
                }
            }
            // throw new IllegalArgumentException("no such enumeration " +
            // name);
            return this;
        }

    }
}
