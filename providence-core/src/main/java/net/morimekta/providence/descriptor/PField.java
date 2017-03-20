/*
 * Copyright 2015-2016 Providence Authors
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
package net.morimekta.providence.descriptor;

import net.morimekta.providence.PType;

import javax.annotation.Nonnull;

/**
 * Field descriptor. All struct variants contains a set of fields. This
 * interface describes the properties each field has. It is an interface so the
 * fields themselves may be implemented as an enum.
 */
public interface PField {
    /**
     * @return The field numeric ID or key.
     */
    int getKey();

    /**
     * @return How the field is required for validity.
     */
    @Nonnull
    PRequirement getRequirement();

    /**
     * @return The data type of the field.
     */
    @Nonnull
    default PType getType() {
        return getDescriptor().getType();
    }

    /**
     * @return The type descriptor for the field data type.
     */
    @Nonnull
    PDescriptor getDescriptor();

    /**
     * @return The field name (original).
     */
    @Nonnull
    String getName();

    /**
     * @return Whether the field has an explicit default value.
     */
    boolean hasDefaultValue();

    /**
     * @return The default value or null if none. This should return value also
     *         where the field has an <b>implicit</b> default value, e.g.
     *         numerical types.
     */
    Object getDefaultValue();

    /**
     * toString helper for fields.
     *
     * @param field The field to make string of.
     * @return The field toString.
     */
    static String toString(PField field) {
        StringBuilder builder = new StringBuilder();
        builder.append(field.getClass().getSimpleName().replaceAll("[$]", "."))
               .append("(")
               .append(field.getKey())
               .append(": ");
        if (field.getRequirement() != net.morimekta.providence.descriptor.PRequirement.DEFAULT) {
            builder.append(field.getRequirement().label).append(" ");
        }
        builder.append(field.getDescriptor().getQualifiedName())
               .append(' ')
               .append(field.getName())
               .append(')');
        return builder.toString();
    }
}
