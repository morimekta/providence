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

package org.apache.thrift.j2.reflect.contained;

import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TUnionDescriptorProvider;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TServiceMethod;

import java.util.List;

/**
 * @author Stein Eldar Johnsen
 * @since 18.09.15
 */
public class TContainedServiceMethod
        extends TServiceMethod<TDescriptor,
                               TContainedStruct,
                               TContainedUnion> {
    public TContainedServiceMethod(String comment,
                                   boolean oneway,
                                   String serviceName,
                                   String methodName,
                                   String packageContext,
                                   TDescriptorProvider<TDescriptor> returnTypeProvider,
                                   List<TField<?>> paramList,
                                   List<TField<?>> exceptionList) {
        super(comment,
              oneway,
              methodName,
              returnTypeProvider,
              paramsProvider(serviceName, methodName, packageContext, paramList),
              exceptionsProvider(serviceName, methodName, packageContext, exceptionList));
    }

    protected static TStructDescriptorProvider<TContainedStruct> paramsProvider(
            String serviceName,
            String methodName,
            String packageContext,
            List<TField<?>> paramsList) {
        if (paramsList == null || paramsList.isEmpty()) return null;

        final TContainedStructDescriptor paramsType = new TContainedStructDescriptor(
                null,
                packageContext,
                serviceName + "." + methodName + "_Params",
                paramsList);
        return new TStructDescriptorProvider<TContainedStruct>() {
            @Override
            public TContainedStructDescriptor descriptor() {
                return paramsType;
            }
        };
    }

    protected static TUnionDescriptorProvider<TContainedUnion> exceptionsProvider(
            String serviceName,
            String methodName,
            String packageContext,
            List<TField<?>> paramsList) {
        if (paramsList == null || paramsList.isEmpty()) {
            return null;
        }

        final TContainedUnionDescriptor paramsType = new TContainedUnionDescriptor(
                null,
                packageContext,
                serviceName + "." + methodName + "_Exceptions",
                paramsList);
        return new TUnionDescriptorProvider<TContainedUnion>() {
            @Override
            public TContainedUnionDescriptor descriptor() {
                return paramsType;
            }
        };
    }
}
