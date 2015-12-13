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

import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.TMessageBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class TCUnion
        extends TCMessage<TCUnion> {
    private final TCUnionDescriptor mType;

    protected TCUnion(Builder builder) {
        super(Collections.unmodifiableMap(new LinkedHashMap<>(builder.mFields)));
        mType = builder.mType;
    }

    @Override
    public TMessageBuilder<TCUnion> mutate() {
        return new Builder(mType);
    }

    @Override
    public boolean isValid() {
        return mFields.size() == 1;
    }

    @Override
    public TCUnionDescriptor descriptor() {
        return mType;
    }

    public static class Builder
            extends TMessageBuilder<TCUnion> {
        private final TCUnionDescriptor    mType;
        private final Map<Integer, Object> mFields;

        public Builder(TCUnionDescriptor type) {
            mType = type;
            mFields = new TreeMap<>();
        }

        @Override
        public TCUnion build() {
            return new TCUnion(this);
        }

        @Override
        public boolean isValid() {
            return mFields.size() == 1;
        }

        @Override
        public Builder set(int key, Object value) {
            TField<?> field = mType.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            mFields.clear();
            if (value != null) {
                mFields.put(field.getKey(), value);
            }
            return this;
        }
    }
}
