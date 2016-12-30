/*
 * Copyright 2015 Providence Authors
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
package net.morimekta.providence;

/**
 * A builder-helper for getting a correct enum entity from value or name.
 */
public abstract class PEnumBuilder<T> implements PBuilder<T> {
    /**
     * Check if the enum builder has been given a valid value.
     *
     * @return True if valid.
     */
    public abstract boolean valid();

    /**
     * Set the enum entity from integer value as it was defined in the thrift
     * IDL.
     *
     * @param value The value to match.
     * @return The builder.
     */
    public abstract PEnumBuilder<T> setByValue(int value);

    /**
     * Set the enum entity from name as it was written in the thrift IDL.
     *
     * @param name The name to match.
     * @return The builder.
     */
    public abstract PEnumBuilder<T> setByName(String name);
}
