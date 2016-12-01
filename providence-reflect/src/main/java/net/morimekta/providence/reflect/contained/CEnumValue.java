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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Contained enum value. This emulates enum values to used in thrift
 * reflection.
 */
public class CEnumValue implements PEnumValue<CEnumValue>, CAnnotatedDescriptor {
    private final int                         value;
    private final String                      name;
    private final PEnumDescriptor<CEnumValue> type;
    private final String                      comment;
    private final Map<String, String>         annotations;

    public CEnumValue(String comment,
                      int value,
                      String name,
                      PEnumDescriptor<CEnumValue> type,
                      Map<String, String> annotations) {
        this.comment = comment;
        this.value = value;
        this.name = name;
        this.type = type;
        this.annotations = annotations;
    }

    @Override
    public String getDocumentation() {
        return comment;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int asInteger() {
        return value;
    }

    @Override
    public String asString() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getAnnotations() {
        if (annotations != null) {
            return annotations.keySet();
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public boolean hasAnnotation(String name) {
        if (annotations != null) {
            return annotations.containsKey(name);
        }
        return false;
    }

    @Override
    public String getAnnotationValue(String name) {
        if (annotations != null) {
            return annotations.get(name);
        }
        return null;
    }

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
               other.getName()
                    .equals(name) &&
               other.getValue() == value;
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
