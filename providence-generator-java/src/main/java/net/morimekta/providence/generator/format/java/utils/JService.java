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
package net.morimekta.providence.generator.format.java.utils;

import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.providence.reflect.contained.CServiceMethod;
import net.morimekta.providence.util.ThriftAnnotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by morimekta on 4/24/16.
 */
public class JService {
    private final CService service;
    private final JHelper  helper;

    public JService(CService service, JHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    public String className() {
        return JUtils.getClassName(service);
    }

    /**
     * All methods that apply for the service.
     *
     * @return The method array.
     */
    public JServiceMethod[] methods() {
        CService top = service;

        List<CServiceMethod> methods = new ArrayList<>();
        while (top != null) {
            // Always keep 'parent' methods on the top.
            methods.addAll(0, top.getMethods());
            top = top.getExtendsService();
        }

        CServiceMethod[] ma = methods.toArray(new CServiceMethod[methods.size()]);
        JServiceMethod[] ret = new JServiceMethod[ma.length];
        for (int i = 0; i < methods.size(); ++i) {
            ret[i] = new JServiceMethod(service, ma[i], helper);
        }
        return ret;
    }

    /**
     * Methods declared in the given service only.
     *
     * @return The method array.
     */
    public JServiceMethod[] declaredMethods() {
        List<CServiceMethod> methods = new ArrayList<>(service.getMethods());

        CServiceMethod[] ma = methods.toArray(new CServiceMethod[methods.size()]);
        JServiceMethod[] ret = new JServiceMethod[ma.length];
        for (int i = 0; i < methods.size(); ++i) {
            ret[i] = new JServiceMethod(service, ma[i], helper);
        }
        return ret;
    }

    public String getRequestClassRef(JServiceMethod method) {
        if (!isDeclaredMethod(method)) {
            if (service.getExtendsService() == null) {
                throw new GeneratorException("Unable to find source service of method: " + method.name() +
                                             " context: " + service.getQualifiedName());
            }
            return new JService(service.getExtendsService(), helper).getRequestClassRef(method);
        }

        return helper.getJavaPackage(service) + "." + className() + "." + method.getRequestClass();
    }

    public String getResponseClassRef(JServiceMethod method) {
        if (method.getResponseClass() == null) {
            return null;
        }
        if (!isDeclaredMethod(method)) {
            if (service.getExtendsService() == null) {
                throw new GeneratorException("Unable to find source service of method: " + method.name() +
                                             " context: " + service.getQualifiedName());
            }
            return new JService(service.getExtendsService(), helper).getResponseClassRef(method);
        }

        return helper.getJavaPackage(service) + "." + className() + "." + method.getResponseClass();
    }

    public boolean isDeclaredMethod(JServiceMethod ref) {
        for (PServiceMethod method : service.getMethods()) {
            if (method.getName().equals(ref.name())) {
                return true;
            }
        }
        return false;
    }

    public String methodsThrows(JServiceMethod method) {
        // Make sure we get the annotation of the service that declares the method.
        if (!isDeclaredMethod(method)) {
            if (service.getExtendsService() == null) {
                throw new GeneratorException("Unable to find source service of method: " + method.name() +
                                             " context: " + service.getQualifiedName());
            }
            return new JService(service.getExtendsService(), helper).methodsThrows(method);
        }

        if (service.hasAnnotation(ThriftAnnotation.JAVA_SERVICE_METHOD_THROWS)) {
            String doThrow = service.getAnnotationValue(ThriftAnnotation.JAVA_SERVICE_METHOD_THROWS);
            if (!IOException.class.getName().equals(doThrow)) {
                // Explicitly disallow declaring 'Exception' here.
                if (    Exception.class.getName().equals(doThrow) ||
                        Exception.class.getSimpleName().equals(doThrow)) {
                    throw new GeneratorException(
                            "Not allowed to declare '" + doThrow + "' as the service thrown exception. " +
                            "Annotation: " + ThriftAnnotation.JAVA_SERVICE_METHOD_THROWS.tag);
                }
                return doThrow;
            }
        }
        return null;
    }

    public CService getService() {
        return service;
    }
}
