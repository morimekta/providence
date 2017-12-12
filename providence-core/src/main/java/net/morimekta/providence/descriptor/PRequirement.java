/*
 * Copyright 2015-2016 Providence Authors
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
package net.morimekta.providence.descriptor;

/**
 * Field requirement designation.
 *
 * <ul>
 *     <li>
 *         <b>OPTIONAL:</b> Value may always be missing.
 *     </li>
 *     <li>
 *         <b>DEFAULT:</b> Value is always present unless nullable. Not required for
 *         validity. Also called <code>optional-in-required-out</code> some places
 *         in Apache Thrift.
 *     </li>
 *     <li>
 *         <b>REQUIRED:</b> Value is always present if a default value exists.
 *         Explicit value required for validity in reading.
 *     </li>
 * </ul>
*/
public enum PRequirement {
    DEFAULT(true, false, ""),
    OPTIONAL(false, false, "optional"),
    REQUIRED(false, true, "required");

    public final boolean fieldIsValueType;
    public final boolean presenceRequired;
    public final String  label;

    PRequirement(boolean value, boolean presence, String name) {
        fieldIsValueType = value;
        presenceRequired = presence;
        label = name;
    }
}
