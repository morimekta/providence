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
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PStructDescriptor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Stein Eldar Johnsen
 * @since 26.08.15
 */
public class CStruct
        extends CMessage<CStruct> {
    PStructDescriptor<CStruct,CField> mType;

    protected CStruct(Builder builder) {
        super(Collections.unmodifiableMap(new LinkedHashMap<>(builder.mFields)));
        mType = builder.mType;
    }

    @Override
    public PMessageBuilder<CStruct> mutate() {
        return new Builder(mType);
    }

    @Override
    public boolean isValid() {
        for (PField<?> field : mType.getFields()) {
            if (field.getRequirement() == PRequirement.REQUIRED) {
                if (!mFields.containsKey(field.getKey())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public PStructDescriptor<CStruct,CField> descriptor() {
        return mType;
    }

    public static class Builder
            extends PMessageBuilder<CStruct> {
        private final PStructDescriptor<CStruct,CField> mType;
        private final Map<Integer, Object>                mFields;

        public Builder(PStructDescriptor<CStruct,CField> type) {
            mType = type;
            mFields = new TreeMap<>();
        }

        @Override
        public CStruct build() {
            return new CStruct(this);
        }

        @Override
        public boolean isValid() {
            for (PField<?> field : mType.getFields()) {
                if (field.getRequirement() == PRequirement.REQUIRED) {
                    if (!mFields.containsKey(field.getKey())) {
                        return false;
                    }
                }
            }

            return true;
        }

        @Override
        public Builder set(int key, Object value) {
            PField<?> field = mType.getField(key);
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
