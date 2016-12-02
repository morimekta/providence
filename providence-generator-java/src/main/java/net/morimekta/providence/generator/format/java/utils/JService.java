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

import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.providence.reflect.contained.CServiceMethod;

import java.util.LinkedList;
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

        List<CServiceMethod> methods = new LinkedList<>();
        while (top != null) {
            // Always keep 'parent' methods on the top.
            methods.addAll(0, top.getMethods());
            top = (CService) top.getExtendsService();
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
        List<CServiceMethod> methods = new LinkedList<>(service.getMethods());

        CServiceMethod[] ma = methods.toArray(new CServiceMethod[methods.size()]);
        JServiceMethod[] ret = new JServiceMethod[ma.length];
        for (int i = 0; i < methods.size(); ++i) {
            ret[i] = new JServiceMethod(service, ma[i], helper);
        }
        return ret;
    }

    public CService getService() {
        return service;
    }
}
