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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.thrift2.TMessageBuilder;
import org.apache.thrift2.descriptor.TField;
import org.apache.thrift2.descriptor.TStructDescriptor;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 26.08.15
 */
public class TContainedStruct
        extends TContainedMessage<TContainedStruct> {
    TStructDescriptor<TContainedStruct> mType;

    protected TContainedStruct(Builder builder) {
        super(Collections.unmodifiableMap(new LinkedHashMap<>(builder.mFields)));
        mType = builder.mType;
    }

    @Override
    public TMessageBuilder<TContainedStruct> mutate() {
        return new Builder(mType);
    }

    @Override
    public boolean isValid() {
        for (TField<?> field : mType.getFields()) {
            if (field.getRequired()) {
                if (!mFields.containsKey(field.getKey())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public TStructDescriptor<TContainedStruct> descriptor() {
        return mType;
    }

    public static class Builder
            extends TMessageBuilder<TContainedStruct> {
        private final TStructDescriptor<TContainedStruct> mType;
        private final Map<Integer, Object>                mFields;

        public Builder(TStructDescriptor<TContainedStruct> type) {
            mType = type;
            mFields = new TreeMap<>();
        }

        @Override
        public TContainedStruct build() {
            return new TContainedStruct(this);
        }

        @Override
        public boolean isValid() {
            for (TField<?> field : mType.getFields()) {
                if (field.getRequired()) {
                    if (!mFields.containsKey(field.getKey())) {
                        return false;
                    }
                }
            }

            return true;
        }

        @Override
        public Builder set(int key, Object value) {
            TField<?> field = mType.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            if (value != null) {
                mFields.put(field.getKey(), value);
            }
            return this;
        }
    }
}
