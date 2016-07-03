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
import net.morimekta.providence.PType;
import net.morimekta.providence.PUnion;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.descriptor.PStructDescriptor;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Set;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class CUnion extends CMessage<CUnion,CField> implements PUnion<CUnion,CField> {
    private final CUnionDescriptor descriptor;
    private final CField           unionField;

    private CUnion(Builder builder) {
        super(builder.getValueMap());
        this.unionField = builder.unionField;
        this.descriptor = builder.descriptor;
    }

    @Override
    public PMessageBuilder<CUnion,CField> mutate() {
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

    public static class Builder extends PMessageBuilder<CUnion,CField> {
        private final CUnionDescriptor descriptor;

        private CField unionField;
        private Object currentValue;

        public Builder(CUnionDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        private Map<Integer, Object> getValueMap() {
            if (unionField == null) {
                return ImmutableMap.of();
            } else if (currentValue == null) {
                return ImmutableMap.of(unionField.getKey(), null);
            } else {
                switch (unionField.getType()) {
                    case LIST:
                        return ImmutableMap.of(unionField.getKey(), ((PList.Builder) this.currentValue).build());
                    case SET:
                        return ImmutableMap.of(unionField.getKey(), ((PSet.Builder) this.currentValue).build());
                    case MAP:
                        return ImmutableMap.of(unionField.getKey(), ((PMap.Builder) this.currentValue).build());
                    case MESSAGE:
                        if (currentValue instanceof PMessageBuilder) {
                            return ImmutableMap.of(unionField.getKey(), ((PMessageBuilder) currentValue).build());
                        }
                    default:
                        return ImmutableMap.of(unionField.getKey(), this.currentValue);
                }
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public PMessageBuilder mutator(int key) {
            CField field = descriptor.getField(key);
            if (field == null) {
                throw new IllegalArgumentException("No such unionField ID " + key);
            } else if (field.getType() != PType.MESSAGE) {
                throw new IllegalArgumentException("Not a message unionField ID " + key + ": " + field.getName());
            }
            if (unionField != field) {
                unionField = field;
                currentValue = null;
            }

            if (currentValue == null) {
                currentValue = ((PStructDescriptor) field.getDescriptor()).builder();
            } else if (currentValue instanceof PMessage) {
                currentValue = ((PMessage) currentValue).mutate();
            } else if (!(currentValue instanceof PMessageBuilder)) {
                // This should in theory not be possible. This is just a safe-guard.
                throw new IllegalArgumentException("Invalid currentValue in map on message type: " + currentValue.getClass().getSimpleName());
            }

            return (PMessageBuilder) currentValue;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Builder merge(CUnion from) {
            if (unionField == null || unionField != from.unionField()) {
                set(from.unionField.getKey(), from.get(from.unionField.getKey()));
            } else {
                int key = unionField.getKey();
                switch (unionField.getType()) {
                    case MESSAGE: {
                        PMessage src = (PMessage) currentValue;
                        PMessage toMerge = (PMessage) from.get(key);

                        currentValue = src.mutate().merge(toMerge).build();
                        break;
                    }
                    case SET:
                        ((PSet.Builder<Object>) currentValue).addAll((Set<Object>) from.get(key));
                        break;
                    case MAP:
                        ((PMap.Builder<Object, Object>) currentValue).putAll((Map<Object, Object>) from.get(key));
                        break;
                    default:
                        set(key, from.get(key));
                        break;
                }
            }

            return this;
        }

        @Override
        public PStructDescriptor<CUnion, CField> descriptor() {
            return descriptor;
        }

        @Override
        public CUnion build() {
            return new CUnion(this);
        }

        @Override
        public boolean isValid() {
            return unionField != null;
        }

        @Override
        public Builder set(int key, Object value) {
            CField field = descriptor.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            this.unionField = field;
            switch (field.getType()) {
                case SET:
                    this.currentValue = ((PSet) field.getDescriptor()).builder();
                    break;
                case LIST:
                    this.currentValue = ((PList) field.getDescriptor()).builder();
                    break;
                case MAP:
                    this.currentValue = ((PMap) field.getDescriptor()).builder();
                    break;
                default:
                    this.currentValue = value;
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
            if (this.unionField != field || this.currentValue == null) {
                this.unionField = field;
                switch (field.getType()) {
                    case LIST: {
                        PList lType = (PList) field.getDescriptor();
                        this.currentValue = lType.builder();
                        break;
                    }
                    case SET: {
                        PSet lType = (PSet) field.getDescriptor();
                        this.currentValue = lType.builder();
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unable to accept addTo on non-list unionField " + field.getName());
                    }
                }
            }
            switch (field.getType()) {
                case LIST: {
                    ((PList.Builder) this.currentValue).add(value);
                    break;
                }
                case SET: {
                    ((PList.Builder) this.currentValue).add(value);
                    break;
                }
            }
            return this;
        }

        @Override
        public Builder clear(int key) {
            this.unionField = null;
            this.currentValue = null;
            return this;
        }
    }
}
