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
import net.morimekta.providence.PBuilderFactory;
import net.morimekta.providence.PType;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Descriptor for a list with item type.
 */
public class PList<Item> extends PContainer<List<Item>> {
    private final BuilderFactory<Item> builderFactory;

    public PList(PDescriptorProvider itemType,
                 BuilderFactory<Item> builderFactory) {
        super(itemType);
        this.builderFactory = builderFactory;
    }

    @Nonnull
    @Override
    public String getName() {
        return "list<" + itemDescriptor().getName() + ">";
    }

    @Nonnull
    @Override
    public String getQualifiedName(String programContext) {
        return "list<" + itemDescriptor().getQualifiedName(programContext) + ">";
    }

    @Nonnull
    @Override
    public PType getType() {
        return PType.LIST;
    }

    @Nullable
    @Override
    public Object getDefaultValue() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof PList)) {
            return false;
        }
        PList<?> other = (PList<?>) o;
        return other.itemDescriptor()
                    .equals(itemDescriptor());
    }

    @Override
    public int hashCode() {
        return PList.class.hashCode() + itemDescriptor().hashCode();
    }

    public interface Builder<I> extends PBuilder<List<I>> {
        @Nonnull
        Builder<I> add(@Nonnull I value);
        @Nonnull
        Builder<I> addAll(@Nonnull Collection<I> items);
        @Nonnull
        Builder<I> clear();

        @Nonnull
        @Override
        List<I> build();
    }

    private interface BuilderFactory<I> extends PBuilderFactory<List<I>> {
        @Nonnull
        @Override
        Builder<I> builder();
    }

    public static class DefaultBuilder<I> implements Builder<I> {
        private ImmutableList.Builder<I> builder;

        public DefaultBuilder() {
            builder = ImmutableList.builder();
        }

        @Nonnull
        @Override
        public DefaultBuilder<I> add(@Nonnull I value) {
            builder.add(value);
            return this;
        }

        @Nonnull
        @Override
        public DefaultBuilder<I> addAll(@Nonnull Collection<I> items) {
            builder.addAll(items);
            return this;
        }

        @Nonnull
        @Override
        public DefaultBuilder<I> clear() {
            builder = ImmutableList.builder();
            return this;
        }

        @Nonnull
        @Override
        public List<I> build() {
            return builder.build();
        }
    }

    @Override
    public Builder<Item> builder() {
        return builderFactory.builder();
    }

    public static <I> PContainerProvider<List<I>, PList<I>> provider(PDescriptorProvider itemDesc) {
        return provider(itemDesc, DefaultBuilder::new);
    }

    public static <I> PContainerProvider<List<I>, PList<I>> provider(PDescriptorProvider itemDesc,
                                                                     BuilderFactory<I> builderFactory) {
        return new PContainerProvider<>(new PList<>(itemDesc, builderFactory));
    }
}
