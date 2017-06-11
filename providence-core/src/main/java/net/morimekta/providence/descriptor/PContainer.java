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

import javax.annotation.Nonnull;

/**
 * Generic descriptor for a container type.
 * <p>
 * See {@link PList}, {@link PSet} and {@link PMap} which specializes for each
 * type of container.
 *
 * @param <Container> The container type.
 */
public abstract class PContainer<Container> implements PDescriptor {
    private final PDescriptorProvider itemDescriptorProvider;

    protected PContainer(PDescriptorProvider provider) {
        itemDescriptorProvider = provider;
    }

    public PDescriptor itemDescriptor() {
        return itemDescriptorProvider.descriptor();
    }

    @Override
    public String getProgramName() {
        return null;
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

    /**
     * Get an instance builder for the container.
     * @return The instance builder.
     */
    @Nonnull
    public abstract PBuilder<Container> builder();
}
