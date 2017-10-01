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

import net.morimekta.providence.descriptor.PServiceMethod;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Descriptor for a single service method.
 */
@SuppressWarnings("unchecked")
public class CServiceMethod implements PServiceMethod,
                                       CAnnotatedDescriptor {
    private final String              name;
    private final boolean             oneway;
    private final CStructDescriptor   requestType;
    private final CUnionDescriptor    responseType;
    private final String              comment;
    private final Map<String, String> annotations;

    public CServiceMethod(String comment,
                          String name,
                          boolean oneway,
                          CStructDescriptor requestType,
                          CUnionDescriptor responseType,
                          Map<String, String> annotations) {
        this.comment = comment;
        this.name = name;
        this.oneway = oneway;
        this.requestType = requestType;
        this.responseType = responseType;
        this.annotations = annotations == null
                           ? Collections.EMPTY_MAP
                           : annotations;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isOneway() {
        return oneway;
    }

    @Nonnull
    @Override
    public CStructDescriptor getRequestType() {
        return requestType;
    }

    @Override
    public CUnionDescriptor getResponseType() {
        return responseType;
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

    @Override
    public String getDocumentation() {
        return comment;
    }

    @Override
    public int hashCode() {
        return Objects.hash(CServiceMethod.class,
                            name, oneway, responseType, requestType, comment, annotations);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        CServiceMethod other = (CServiceMethod) o;

        return Objects.equals(name, other.name) &&
               Objects.equals(oneway, other.oneway) &&
               Objects.equals(responseType, other.responseType) &&
               Objects.equals(requestType, other.requestType) &&
               Objects.equals(comment, other.comment) &&
               Objects.equals(annotations, other.annotations);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServiceMethod(");

        if (oneway) {
            builder.append("oneway ");
        }
        if (responseType != null) {
            builder.append(responseType.fieldForId(0).getDescriptor().getQualifiedName());
        } else {
            builder.append("void");
        }

        builder.append(" ");
        builder.append(name);
        builder.append("([");
        builder.append(requestType.getQualifiedName());

        builder.append("])");
        return builder.toString();
    }
}
