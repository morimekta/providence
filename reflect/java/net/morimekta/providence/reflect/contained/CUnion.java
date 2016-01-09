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

import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class CUnion
        extends CMessage<CUnion> {
    private final CUnionDescriptor mType;

    protected CUnion(Builder builder) {
        super(Collections.unmodifiableMap(new LinkedHashMap<>(builder.mFields)));
        mType = builder.mType;
    }

    @Override
    public PMessageBuilder<CUnion> mutate() {
        return new Builder(mType);
    }

    @Override
    public CUnionDescriptor descriptor() {
        return mType;
    }

    public static class Builder
            extends PMessageBuilder<CUnion> {
        private final CUnionDescriptor mType;
        private final Map<Integer, Object> mFields;

        public Builder(CUnionDescriptor type) {
            mType = type;
            mFields = new TreeMap<>();
        }

        @Override
        public CUnion build() {
            return new CUnion(this);
        }

        @Override
        public boolean isValid() {
            return mFields.size() == 1;
        }

        @Override
        public Builder set(int key, Object value) {
            PField<?> field = mType.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            mFields.clear();
            if (value != null) {
                mFields.put(field.getKey(), value);
            }
            return this;
        }

        @Override
        public Builder clear(int key) {
            mFields.remove(key);
            return this;
        }
    }
}
