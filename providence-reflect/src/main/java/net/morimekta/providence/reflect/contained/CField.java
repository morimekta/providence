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

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PValueProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class CField implements PField, CAnnotatedDescriptor {
    private final String              comment;
    private final int                 key;
    private final PRequirement        requirement;
    private final PDescriptorProvider typeProvider;
    private final String              name;
    private final PValueProvider      defaultValue;
    private final Map<String, String> annotations;

    public CField(@Nullable String docs,
                  int key,
                  @Nonnull PRequirement requirement,
                  @Nonnull String name,
                  @Nonnull PDescriptorProvider typeProvider,
                  @Nullable PValueProvider defaultValue,
                  @Nullable Map<String, String> annotations) {
        this.comment = docs;
        this.key = key;
        this.requirement = requirement;
        this.typeProvider = typeProvider;
        this.name = name;
        this.defaultValue = defaultValue;
        this.annotations = annotations;
    }

    @Override
    public String getDocumentation() {
        return comment;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Nonnull
    @Override
    public PRequirement getRequirement() {
        return requirement;
    }

    @Nonnull
    @Override
    public PType getType() {
        return typeProvider.descriptor()
                           .getType();
    }

    @Nonnull
    @Override
    public PDescriptor getDescriptor() {
        return typeProvider.descriptor();
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    @Override
    public Object getDefaultValue() {
        try {
            return defaultValue != null ? defaultValue.get() : null;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse default value " + getName(), e);
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getAnnotations() {
        if (annotations != null) {
            return annotations.keySet();
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public boolean hasAnnotation(@Nonnull String name) {
        if (annotations != null) {
            return annotations.containsKey(name);
        }
        return false;
    }

    @Override
    public String getAnnotationValue(@Nonnull String name) {
        if (annotations != null) {
            return annotations.get(name);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(PField.class.getSimpleName())
               .append('{')
               .append(key)
               .append(": ");
        if (requirement != PRequirement.DEFAULT) {
            builder.append(requirement.label)
                   .append(" ");
        }
        builder.append(getDescriptor().getQualifiedName())
               .append(" ")
               .append(name)
               .append("}");

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof CField)) {
            return false;
        }
        CField other = (CField) o;
        return key == other.key &&
               requirement == other.requirement &&
               // We cannot test that the types are deep-equals as it may have circular
               // containment.
               equalsQualifiedName(getDescriptor(), other.getDescriptor()) &&
               name.equals(other.name) &&
               Objects.equals(defaultValue, other.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(CField.class, key, requirement, name, getDefaultValue());
    }

    /**
     * Check if the two descriptors has the same qualified name, i..e
     * symbolically represent the same type.
     *
     * @param a The first type.
     * @param b The second type.
     * @return If the two types are the same.
     */
    private static boolean equalsQualifiedName(PDescriptor a, PDescriptor b) {
        return (a != null) && (b != null) && (a.getQualifiedName()
                                               .equals(b.getQualifiedName()));
    }
}
