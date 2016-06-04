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

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PSet;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Set;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class CUnion extends CMessage<CUnion> implements PUnion<CUnion> {
    private final CUnionDescriptor descriptor;
    private final CField           unionField;

    private CUnion(Builder builder) {
        super(builder.getValueMap());
        this.unionField = builder.field;
        this.descriptor = builder.descriptor;
    }

    @Override
    public PMessageBuilder<CUnion> mutate() {
        return new Builder(descriptor);
    }

    @Override
    public CUnionDescriptor descriptor() {
        return descriptor;
    }

    @Override
    public CField unionField() {
        return unionField;
    }

    public static class Builder extends PMessageBuilder<CUnion> {
        private final CUnionDescriptor descriptor;

        private CField field;
        private Object value;

        public Builder(CUnionDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        private Map<Integer, Object> getValueMap() {
            if (field == null) {
                return ImmutableMap.of();
            } else if (value == null) {
                return ImmutableMap.of(field.getKey(), null);
            } else {
                switch (field.getType()) {
                    case LIST:
                        return ImmutableMap.of(field.getKey(), ((PList.Builder) this.value).build());
                    case SET:
                        return ImmutableMap.of(field.getKey(), ((PSet.Builder) this.value).build());
                    case MAP:
                        return ImmutableMap.of(field.getKey(), ((PMap.Builder) this.value).build());
                    default:
                        return ImmutableMap.of(field.getKey(), this.value);
                }
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public Builder merge(CUnion from) {
            if (field == null || field != from.unionField()) {
                set(from.unionField.getKey(), from.get(from.unionField.getKey()));
            } else {
                int key = field.getKey();
                switch (field.getType()) {
                    case MESSAGE: {
                        PMessage src = (PMessage) value;
                        PMessage toMerge = (PMessage) from.get(key);

                        value = src.mutate().merge(toMerge).build();
                        break;
                    }
                    case SET:
                        ((PSet.Builder<Object>) value).addAll((Set<Object>) from.get(key));
                        break;
                    case MAP:
                        ((PMap.Builder<Object, Object>) value).putAll((Map<Object, Object>) from.get(key));
                        break;
                    default:
                        set(key, from.get(key));
                        break;
                }
            }

            return this;
        }


        @Override
        public CUnion build() {
            return new CUnion(this);
        }

        @Override
        public boolean isValid() {
            return field != null;
        }

        @Override
        public Builder set(int key, Object value) {
            CField field = descriptor.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            this.field = field;
            switch (field.getType()) {
                case SET:
                    this.value = ((PSet) field.getDescriptor()).builder();
                    break;
                case LIST:
                    this.value = ((PList) field.getDescriptor()).builder();
                    break;
                case MAP:
                    this.value = ((PMap) field.getDescriptor()).builder();
                    break;
                default:
                    this.value = value;
                    break;
            }

            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Builder addTo(int key, Object value) {
            CField field = descriptor.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            if (this.field != field || this.value == null) {
                this.field = field;
                switch (field.getType()) {
                    case LIST: {
                        PList lType = (PList) field.getDescriptor();
                        this.value = lType.builder();
                        break;
                    }
                    case SET: {
                        PSet lType = (PSet) field.getDescriptor();
                        this.value = lType.builder();
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unable to accept addTo on non-list field " + field.getName());
                    }
                }
            }
            switch (field.getType()) {
                case LIST: {
                    ((PList.Builder) this.value).add(value);
                    break;
                }
                case SET: {
                    ((PList.Builder) this.value).add(value);
                    break;
                }
            }
            return this;
        }

        @Override
        public Builder clear(int key) {
            this.field = null;
            this.value = null;
            return this;
        }
    }
}
