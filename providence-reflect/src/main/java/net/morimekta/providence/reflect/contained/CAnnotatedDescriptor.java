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

import net.morimekta.providence.util.ThriftAnnotation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 */
public interface CAnnotatedDescriptor {
    /**
     * The type comment is the last block of comment written before the type
     * declaration. Comments on the same line, after the declaration is
     * ignored.
     *
     * @return The comment string containing all formatting (not including the
     *         comment delimiter and the leading space.
     */
    @Nullable
    String getDocumentation();

    /**
     * Get set of available annotations.
     * @return The annotation set.
     */
    @Nonnull
    Set<String> getAnnotations();

    /**
     * Get the given annotation value.
     * @param name Name of annotation.
     * @return If the annotation is present.
     */
    boolean hasAnnotation(@Nonnull String name);

    /**
     * Get the given annotation value.
     * @param name Name of annotation.
     * @return The annotation value or null.
     */
    @Nullable
    String getAnnotationValue(@Nonnull String name);

    /**
     * Get the given annotation value.
     * @param annotation The annotation.
     * @return If the annotation is present.
     */
    default boolean hasAnnotation(@Nonnull ThriftAnnotation annotation) {
        return hasAnnotation(annotation.tag);
    }

    /**
     * Get the given annotation value.
     * @param annotation The annotation.
     * @return The annotation value or null.
     */
    @Nullable
    default String getAnnotationValue(@Nonnull ThriftAnnotation annotation) {
        return getAnnotationValue(annotation.tag);
    }
}
