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

import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CService;
import net.morimekta.providence.reflect.contained.CServiceMethod;
import net.morimekta.util.Strings;

import java.util.ArrayList;

/**
 * Created by morimekta on 4/24/16.
 */
public class JServiceMethod {
    private final CService service;
    private final CServiceMethod method;
    private final JHelper helper;

    public JServiceMethod(CService service,
                          CServiceMethod method,
                          JHelper helper) {
        this.service = service;
        this.method = method;
        this.helper = helper;
    }

    public CServiceMethod getMethod() {
        return method;
    }

    public CService getService() {
        return service;
    }

    public String constant() {
        return Strings.c_case(method.getName()).toUpperCase();
    }

    public String name() {
        return method.getName();
    }

    public String methodName() {
        return method.getName();
    }

    public String getRequestClass() {
        return JUtils.getClassName(method.getRequestType());
    }

    public String getResponseClass() {
        if (method.getResponseType() != null) {
            return JUtils.getClassName(method.getResponseType());
        }
        return null;
    }

    public JField getResponse() {
        if (method.getResponseType() != null) {
            if (method.getResponseType().findFieldById(0) != null) {
                return new JField(method.getResponseType().findFieldById(0),
                                  helper,
                                  0);
            }
        }
        return null;
    }

    public JField[] params() {
        CField[] fields = method.getRequestType().getFields();
        JField[] ret = new JField[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            ret[i] = new JField(fields[i], helper, i);
        }
        return ret;
    }

    public JField[] exceptions() {
        if (method.getResponseType() == null) return new JField[0];
        ArrayList<JField> ret = new ArrayList<>();

        int idx = 0;
        for (CField field : method.getResponseType().getFields()) {
            if (field.getId() != 0) {
                ret.add(new JField(field, helper, idx++));
            }
        }
        return ret.toArray(new JField[ret.size()]);
    }
}
