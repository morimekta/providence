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

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.descriptor.PEnumDescriptor;

/**
 * Contained enum value. This emulates enum values to used in thrift
 * reflection.
 */
public class CEnum implements PEnumValue<CEnum> {
    private final int                    mValue;
    private final String                 mName;
    private final PEnumDescriptor<CEnum> mType;
    private final String                 mComment;

    public CEnum(String comment, int value, String name, PEnumDescriptor<CEnum> type) {
        mComment = comment;
        mValue = value;
        mName = name;
        mType = type;
    }

    @Override
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
    public PEnumDescriptor<CEnum> descriptor() {
        return mType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof CEnum)) {
            return false;
        }
        CEnum other = (CEnum) o;
        return other.descriptor()
                    .getQualifiedName(null)
                    .equals(mType.getQualifiedName(null)) &&
               other.getName()
                    .equals(mName) &&
               other.getValue() == mValue;
    }

    @Override
    public int compareTo(CEnum other) {
        return Integer.compare(mValue, other.mValue);
    }

    @Override
    public String toString() {
        return mName.toUpperCase();
    }

    public static class Builder extends PEnumBuilder<CEnum> {
        private final CEnumDescriptor mType;

        private CEnum mValue = null;

        public Builder(CEnumDescriptor type) {
            mType = type;
        }

        @Override
        public CEnum build() {
            return mValue;
        }

        @Override
        public boolean isValid() {
            return mValue != null;
        }

        @Override
        public Builder setByValue(int id) {
            mValue = mType.getValueById(id);
            return this;
        }

        @Override
        public Builder setByName(String name) {
            mValue = mType.getValueByName(name);
            return this;
        }
    }
}
