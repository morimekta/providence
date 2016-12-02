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

import java.util.Collections;
import java.util.Map;
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
        this.annotations = annotations;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isOneway() {
        return oneway;
    }

    @Override
    public CStructDescriptor getRequestType() {
        return requestType;
    }

    @Override
    public CUnionDescriptor getResponseType() {
        return responseType;
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
    public String getDocumentation() {
        return comment;
    }
}
