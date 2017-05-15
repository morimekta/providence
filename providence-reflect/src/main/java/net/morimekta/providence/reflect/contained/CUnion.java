/*
 * Copyright 2016 Providence Authors
 *
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
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.descriptor.PUnionDescriptor;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class CUnion implements PUnion<CUnion,CField> {
    private final CUnionDescriptor descriptor;
    private final CField           unionField;
    private final Object           unionValue;

    private CUnion(Builder builder) {
        this.unionField = builder.unionField;
        this.descriptor = builder.descriptor;
        if (builder.currentValue instanceof PMessageBuilder) {
            this.unionValue = ((PMessageBuilder) builder.currentValue).build();
        } else if (builder.currentValue instanceof PMap.Builder) {
            this.unionValue = ((PMap.Builder) builder.currentValue).build();
        } else if (builder.currentValue instanceof PList.Builder) {
            this.unionValue = ((PList.Builder) builder.currentValue).build();
        } else if (builder.currentValue instanceof PSet.Builder) {
            this.unionValue = ((PSet.Builder) builder.currentValue).build();
        } else {
            this.unionValue = builder.currentValue;
        }
    }

    @Override
    public boolean has(int key) {
        return unionField != null && unionField.getKey() == key && unionValue != null;
    }

    @Override
    public int num(int key) {
        if (!has(key)) return 0;
        switch (unionField.getType()) {
            case MAP:
                return ((Map) unionValue).size();
            case LIST:
            case SET:
                return ((Collection) unionValue).size();
        }
        return 0;
    }

    @Override
    public Object get(int key) {
        return has(key) ? unionValue : null;
    }

    @Nonnull
    @Override
    public PMessageBuilder<CUnion,CField> mutate() {
        return new Builder(descriptor).merge(this);
    }

    @Nonnull
    @Override
    public String asString() {
        return CStruct.asString(this);
    }

    @Nonnull
    @Override
    public CUnionDescriptor descriptor() {
        return descriptor;
    }

    @Nonnull
    @Override
    public CField unionField() {
        return unionField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof CUnion)) {
            return false;
        }

        CUnion other = (CUnion) o;
        return Objects.equals(descriptor, other.descriptor) &&
               Objects.equals(unionField, other.unionField) &&
               Objects.equals(unionValue, other.unionValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor().getQualifiedName(), unionField, unionValue);
    }

    @Override
    public int compareTo(@Nonnull CUnion other) {
        return CStruct.compareMessages(this, other);
    }

    public static class Builder extends PMessageBuilder<CUnion,CField> {
        private final CUnionDescriptor descriptor;

        private CField unionField;
        private Object currentValue;

        public Builder(CUnionDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        @Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public PMessageBuilder mutator(int key) {
            CField field = descriptor.getField(key);
            if (field == null) {
                throw new IllegalArgumentException("No such field ID " + key);
            } else if (field.getType() != PType.MESSAGE) {
                throw new IllegalArgumentException("Not a message field ID " + key + ": " + field.getName());
            }
            if (unionField != field) {
                unionField = field;
                currentValue = null;
            }

            if (currentValue == null) {
                currentValue = ((PMessageDescriptor) field.getDescriptor()).builder();
            } else if (currentValue instanceof PMessage) {
                currentValue = ((PMessage) currentValue).mutate();
            } else if (!(currentValue instanceof PMessageBuilder)) {
                // This should in theory not be possible. This is just a safe-guard.
                throw new IllegalArgumentException("Invalid currentValue in map on message type: " + currentValue.getClass().getSimpleName());
            }

            return (PMessageBuilder) currentValue;
        }

        @Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public Builder merge(CUnion from) {
            if (unionField == null || unionField != from.unionField()) {
                set(from.unionField.getKey(), from.get(from.unionField.getKey()));
            } else {
                int key = unionField.getKey();
                switch (unionField.getType()) {
                    case MESSAGE: {
                        PMessageBuilder src;
                        if (currentValue instanceof PMessageBuilder) {
                            src = (PMessageBuilder) currentValue;
                        } else {
                            src = ((PMessage) currentValue).mutate();
                        }
                        PMessage toMerge = (PMessage) from.get(key);

                        currentValue = src.merge(toMerge);
                        break;
                    }
                    case SET:
                        ((PSet.Builder<Object>) currentValue).addAll((Collection<Object>) from.get(key));
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

        @Nonnull
        @Override
        public PUnionDescriptor<CUnion, CField> descriptor() {
            return descriptor;
        }

        @Nonnull
        @Override
        public CUnion build() {
            return new CUnion(this);
        }

        @Override
        public boolean valid() {
            return unionField != null && currentValue != null;
        }

        @Override
        public void validate() {
            if (!valid()) {
                throw new IllegalStateException("No union field set in " +
                                                descriptor().getQualifiedName());
            }
        }

        @Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public Builder set(int key, Object value) {
            CField field = descriptor.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            if (value == null) {
                return clear(key);
            }
            this.unionField = field;
            switch (field.getType()) {
                case SET:
                    this.currentValue = ((PSet) field.getDescriptor()).builder().addAll((Collection) value);
                    break;
                case LIST:
                    this.currentValue = ((PList) field.getDescriptor()).builder().addAll((Collection) value);
                    break;
                case MAP:
                    this.currentValue = ((PMap) field.getDescriptor()).builder().putAll((Map) value);
                    break;
                default:
                    this.currentValue = value;
                    break;
            }

            return this;
        }

        @Override
        public boolean isSet(int key) {
            return unionField != null && unionField.getKey() == key;
        }

        @Override
        public boolean isModified(int key) {
            return false;
        }

        @Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public Builder addTo(int key, Object value) {
            CField field = descriptor.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            if (field.getType() != PType.LIST &&
                field.getType() != PType.SET) {
                throw new IllegalArgumentException("Unable to accept addTo on non-list field " + field.getName());
            }
            if (value == null) {
                throw new IllegalArgumentException("Adding null item to collection " + field.getName());
            }
            if (this.unionField != field || this.currentValue == null) {
                this.unionField = field;
                switch (field.getType()) {
                    case LIST: {
                        this.currentValue = ((PList) field.getDescriptor()).builder();
                        break;
                    }
                    case SET: {
                        this.currentValue = ((PSet) field.getDescriptor()).builder();
                        break;
                    }
                    default:
                        break;
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
                default:
                    break;
            }
            return this;
        }

        @Nonnull
        @Override
        public Builder clear(int key) {
            if (isSet(key)) {
                this.unionField = null;
                this.currentValue = null;
            }
            return this;
        }
    }
}
