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

import org.apache.thrift.j2.TType;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 25.08.15
 */
public interface TDescriptor<V> {
    /**
     * The package name is derived form the .thrift file name that is
     * parsed or if read from serialized format is explicit. Note that
     * there may be package name conflicts globally.
     *
     * @return The package of the type. If empty the type is not in any
     *         namespace.
     */
    String getPackageName();

    /**
     * The name of the type is the Identifier string from the IDL.
     *
     * @return The name of the type. Not including package.
     */
    String getName();

    /**
     * This will return the qualifying name of the type given package
     * context.
     *
     * @return The name of the type. Including package if not matching with
     *         packageName.
     */
    String getQualifiedName(String packageContext);

    /**
     * @return Get the field type.
     */
    TType getType();
}
