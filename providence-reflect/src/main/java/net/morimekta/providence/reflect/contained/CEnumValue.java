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

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.descriptor.PEnumDescriptor;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Contained enum value. This emulates enum values to used in thrift
 * reflection.
 */
public class CEnumValue implements PEnumValue<CEnumValue>, CAnnotatedDescriptor {
    private final int                         value;
    @Nonnull
    private final String                      name;
    @Nonnull
    private final PEnumDescriptor<CEnumValue> type;
    @Nullable
    private final String                      comment;
    @Nonnull
    private final Map<String, String>         annotations;

    public CEnumValue(@Nullable String comment,
                      int value,
                      @Nonnull String name,
                      @Nonnull PEnumDescriptor<CEnumValue> type,
                      @Nullable Map<String, String> annotations) {
        this.comment = comment;
        this.value = value;
        this.name = name;
        this.type = type;
        this.annotations = annotations == null ? ImmutableMap.of() : ImmutableMap.copyOf(annotations);
    }

    @Override
    public String getDocumentation() {
        return comment;
    }

    @Override
    public int asInteger() {
        return value;
    }

    @Nonnull
    @Override
    public String asString() {
        return name;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getAnnotations() {
        return annotations.keySet();
    }

    @Override
    public boolean hasAnnotation(@Nonnull String name) {
        return annotations.containsKey(name);
    }

    @Override
    public String getAnnotationValue(@Nonnull String name) {
        return annotations.get(name);
    }

    @Nonnull
    @Override
    public PEnumDescriptor<CEnumValue> descriptor() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof CEnumValue)) {
            return false;
        }
        CEnumValue other = (CEnumValue) o;
        return other.descriptor()
                    .getQualifiedName()
                    .equals(type.getQualifiedName()) &&
               other.asString()
                    .equals(name) &&
               other.asInteger() == value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(CEnumValue.class,
                            descriptor().getQualifiedName(),
                            name, value);
    }

    @Override
    public int compareTo(CEnumValue other) {
        return Integer.compare(value, other.value);
    }

    @Override
    public String toString() {
        return name.toUpperCase();
    }

    public static class Builder extends PEnumBuilder<CEnumValue> {
        private final CEnumDescriptor mType;

        private CEnumValue mValue = null;

        public Builder(CEnumDescriptor type) {
            mType = type;
        }

        @Override
        public CEnumValue build() {
            return mValue;
        }

        @Override
        public boolean valid() {
            return mValue != null;
        }

        @Nonnull
        @Override
        public Builder setById(int id) {
            mValue = mType.findById(id);
            return this;
        }

        @Nonnull
        @Override
        public Builder setByName(String name) {
            mValue = mType.findByName(name);
            return this;
        }
    }
}
