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

package org.apache.thrift.j2.descriptor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.j2.TMessage;

/**
 * The definition of a thrift structure.
 *
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 13.09.15
 */
public class TServiceDescriptor {
    private final List<TServiceMethod<?, ?, ?>>        mMethods;
    private final Map<String, TServiceMethod<?, ?, ?>> mMethodMap;

    private final String mComment;
    private final String mPackageName;
    private final String mName;

    public TServiceDescriptor(String comment,
                              String packageName,
                              String name,
                              List<TServiceMethod<?, ?, ?>> methods) {
        mComment = comment;
        mPackageName = packageName;
        mName = name;

        Map<String, TServiceMethod<?, ?, ?>> methodMap = new LinkedHashMap<>();
        for (TServiceMethod<?, ?, ?> field : methods) {
            methodMap.put(field.getName(), field);
        }

        mMethods = Collections.unmodifiableList(methods);
        mMethodMap = Collections.unmodifiableMap(methodMap);
    }

    public List<TServiceMethod<?, ?, ?>> getMethods() {
        return mMethods;
    }

    @SuppressWarnings("unchecked")
    public <R, P extends TMessage<P>, E extends TMessage<E>>
    TServiceMethod<R, P, E> getMethodByName(String name) {
        return (TServiceMethod<R, P, E>) mMethodMap.get(name);
    }

    public String getComment() {
        return mComment;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getName() {
        return mName;
    }
}
