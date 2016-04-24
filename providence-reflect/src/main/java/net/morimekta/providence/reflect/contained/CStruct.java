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
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PStructDescriptor;

import com.google.common.collect.ImmutableSortedMap;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Stein Eldar Johnsen
 * @since 26.08.15
 */
public class CStruct extends CMessage<CStruct> {
    PStructDescriptor<CStruct, CField> descriptor;

    private CStruct(Builder builder) {
        super(ImmutableSortedMap.copyOf(builder.values));
        descriptor = builder.descriptor;
    }

    @Override
    public PMessageBuilder<CStruct> mutate() {
        return new Builder(descriptor);
    }

    @Override
    public PStructDescriptor<CStruct, CField> descriptor() {
        return descriptor;
    }

    public static class Builder extends PMessageBuilder<CStruct> {
        private final PStructDescriptor<CStruct, CField> descriptor;
        private final Map<Integer, Object>               values;

        public Builder(PStructDescriptor<CStruct, CField> type) {
            descriptor = type;
            values = new TreeMap<>();
        }

        @Override
        public CStruct build() {
            return new CStruct(this);
        }

        @Override
        public boolean isValid() {
            for (PField<?> field : descriptor.getFields()) {
                if (field.getRequirement() == PRequirement.REQUIRED) {
                    if (!values.containsKey(field.getKey())) {
                        return false;
                    }
                }
            }

            return true;
        }

        @Override
        public Builder set(int key, Object value) {
            PField<?> field = descriptor.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            if (value != null) {
                values.put(field.getKey(), value);
            }
            return this;
        }

        @Override
        public Builder addTo(int key, Object value) {
            PField<?> field = descriptor.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            if (value != null) {
                if (field.getType() == PType.LIST) {
                    @SuppressWarnings("unchecked")
                    List<Object> list = (List<Object>) values.get(field.getKey());
                    if (list == null) {
                        list = new LinkedList<>();
                        values.put(field.getKey(), list);
                    }
                    list.add(value);
                } else if (field.getType() == PType.SET) {
                    @SuppressWarnings("unchecked")
                    Set<Object> set = (Set<Object>) values.get(field.getKey());
                    if (set == null) {
                        set = new HashSet<>();
                        values.put(field.getKey(), set);
                    }
                    set.add(value);
                }
            }
            return this;
        }

        @Override
        public Builder clear(int key) {
            values.remove(key);
            return this;
        }
    }
}
