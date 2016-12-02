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

import net.morimekta.providence.PBuilder;

/**
 * Descriptor for a declared type. A declared type is a value type that is
 * derived from a thrift definition.
 */
public abstract class PDeclaredDescriptor<T> implements PDescriptor {
    private final String programName;
    private final String name;

    protected PDeclaredDescriptor(String programName, String name) {
        this.programName = programName;
        this.name = name;
    }

    @Override
    public final String getProgramName() {
        return programName;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String getQualifiedName(String programContext) {
        if (!this.programName.equals(programContext)) {
            return getProgramName() + "." + getName();
        }
        return getName();
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

    /**
     * Get the builder for the given declared type.
     *
     * @return The type specific builder provider.
     */
    public abstract PBuilder<T> builder();
}
