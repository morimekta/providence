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
import org.apache.thrift.j2.descriptor.TDescriptor;

/**
 * @author Stein Eldar Johnsen
 * @since 21.10.15
 */
public interface TField<V> {
    /**
     * The type comment is the last block of comment written before
     * the type declaration. Comments on the same line, after the
     * declaration is ignored.
     *
     * @return The comment string containing all formatting (not
     *         including the comment delimiter and the leading space.
     */
    String getComment();

    /**
     * @return The field numeric ID or key.
     */
    int getKey();

    /**
     * @return Whether the field is required for validity.
     */
    boolean getRequired();

    /**
     * @return The data type of the field.
     */
    TType getType();

    /**
     * @return The type descriptor for the field data type.
     */
    TDescriptor<V> getDescriptor();

    /**
     * @return The field name (original).
     */
    String getName();

    /**
     * @return Whether the field has an explicit default value.
     */
    boolean hasDefaultValue();

    /**
     * @return The default value or null if none.
     */
    V getDefaultValue();
}
