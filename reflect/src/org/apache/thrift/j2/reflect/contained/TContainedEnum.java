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

import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TEnumValue;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;

/**
 * @author Stein Eldar Johnsen
 * @since 26.08.15
 */
public class TContainedEnum
        implements TEnumValue<TContainedEnum> {
    private final int                             mValue;
    private final String                          mName;
    private final TEnumDescriptor<TContainedEnum> mType;
    private final String                          mComment;

    public TContainedEnum(String comment, int value, String name, TEnumDescriptor<TContainedEnum> type) {
        mComment = comment;
        mValue = value;
        mName = name;
        mType = type;
    }

    // @Override
    public String getComment() {
        return mComment;
    }

    @Override
    public int getValue() {
        return mValue;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public TEnumDescriptor<TContainedEnum> getDescriptor() {
        return mType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TContainedEnum)) {
            return false;
        }
        TContainedEnum other = (TContainedEnum) o;
        return other.getDescriptor().getQualifiedName(null).equals(mType.getQualifiedName(null)) &&
               other.getName().equals(mName) &&
               other.getValue() == mValue;
    }

    @Override
    public String toString() {
        return mName.toUpperCase();
    }

    public static class Builder
            extends TEnumBuilder<TContainedEnum> {
        private final TContainedEnumDescriptor mType;

        private int mValue = -1;  // Illegal enum value.

        public Builder(TContainedEnumDescriptor type) {
            mType = type;
        }

        @Override
        public TContainedEnum build() {
            if (isValid()) {
                TEnumValue<?> value = mType.getValueById(mValue);
                return new TContainedEnum(value.getComment(),
                                          value.getValue(),
                                          value.getName(),
                                          mType);
            }
            // throw new IllegalArgumentException("no such enumeration " +
            // name);
            return null;
        }

        @Override
        public boolean isValid() {
            for (TEnumValue<?> value : mType.getValues()) {
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
            for (TEnumValue<?> value : mType.getValues()) {
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
