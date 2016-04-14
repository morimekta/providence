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

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PValueProvider;
import net.morimekta.providence.util.PTypeUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class CField<T> implements PField<T>, CAnnotatedDescriptor {
    private final String                 comment;
    private final int                    key;
    private final PRequirement           requirement;
    private final PDescriptorProvider<T> typeProvider;
    private final String                 name;
    private final PValueProvider<T>      defaultValue;
    private final Map<String, String>    annotations;

    public CField(String comment,
                  int key,
                  PRequirement requirement,
                  String name,
                  PDescriptorProvider<T> typeProvider,
                  PValueProvider<T> defaultValue,
                  Map<String, String> annotations) {
        this.comment = comment;
        this.key = key;
        this.requirement = requirement;
        this.typeProvider = typeProvider;
        this.name = name;
        this.defaultValue = defaultValue;
        this.annotations = annotations;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public PRequirement getRequirement() {
        return requirement;
    }

    @Override
    public PType getType() {
        return typeProvider.descriptor()
                           .getType();
    }

    @Override
    public PDescriptor<T> getDescriptor() {
        return typeProvider.descriptor();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    @Override
    public T getDefaultValue() {
        return hasDefaultValue() ? defaultValue.get() : null;
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
    public String getAnnotation(String name) {
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
        builder.append(getDescriptor().getQualifiedName(null))
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
        CField<?> other = (CField<?>) o;
        return key == other.key &&
               requirement == other.requirement &&
               // We cannot test that the types are deep-equals as it may have circular
               // containment.
               PTypeUtils.equalsQualifiedName(getDescriptor(), other.getDescriptor()) &&
               name.equals(other.name) &&
               PTypeUtils.equals(defaultValue, other.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(CField.class, key, requirement, name, getDefaultValue());
    }
}
