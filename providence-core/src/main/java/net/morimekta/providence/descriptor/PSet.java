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
import net.morimekta.providence.PType;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Descriptor for a set with item type.
 */
public class PSet<Item> extends PContainer<Set<Item>> {
    private final Supplier<Builder<Item>> builderSupplier;

    public PSet(PDescriptorProvider itemType,
                Supplier<Builder<Item>> builderSupplier) {
        super(itemType);
        this.builderSupplier = builderSupplier;
    }

    @Nonnull
    @Override
    public String getName() {
        return "set<" + itemDescriptor().getName() + ">";
    }

    @Nonnull
    @Override
    public String getQualifiedName(String programContext) {
        return "set<" + itemDescriptor().getQualifiedName(programContext) + ">";
    }

    @Nonnull
    @Override
    public PType getType() {
        return PType.SET;
    }

    @Nullable
    @Override
    public Object getDefaultValue() {
        return Collections.EMPTY_SET;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof PSet)) {
            return false;
        }
        PSet<?> other = (PSet<?>) o;
        return other.itemDescriptor()
                    .equals(itemDescriptor());
    }

    @Override
    public int hashCode() {
        return PSet.class.hashCode() + itemDescriptor().hashCode();
    }

    @Nonnull
    @Override
    public Builder<Item> builder() {
        return builderSupplier.get();
    }

    /**
     * Container builder used in serialization.
     *
     * @param <I> The item type.
     */
    public interface Builder<I> extends PBuilder<Set<I>> {
        @Nonnull
        Builder<I> add(@Nonnull I value);
        @Nonnull
        Builder<I> addAll(@Nonnull Collection<I> items);

        @Nonnull
        @Override
        Set<I> build();
    }

    /**
     * Default builder returning an ImmutableSet.
     *
     * @param <I> The item type.
     */
    public static class DefaultBuilder<I> implements Builder<I> {
        private ImmutableSet.Builder<I> builder;

        public DefaultBuilder() {
            builder = ImmutableSet.builder();
        }

        @Nonnull
        @Override
        public Builder<I> add(@Nonnull I value) {
            builder.add(value);
            return this;
        }

        @Nonnull
        @Override
        public Builder<I> addAll(@Nonnull Collection<I> items) {
            builder.addAll(items);
            return this;
        }

        @Nonnull
        @Override
        public Set<I> build() {
            return builder.build();
        }
    }

    /**
     * Default builder returning an ImmutableSortedSet.
     *
     * @param <I> The item type.
     */
    public static class SortedBuilder<I extends Comparable<I>> implements Builder<I> {
        private ImmutableSortedSet.Builder<I> builder;

        public SortedBuilder() {
            builder = ImmutableSortedSet.naturalOrder();
        }

        @Nonnull
        @Override
        public Builder<I> add(@Nonnull I value) {
            builder.add(value);
            return this;
        }

        @Nonnull
        @Override
        public Builder<I> addAll(@Nonnull Collection<I> items) {
            builder.addAll(items);
            return this;
        }

        @Nonnull
        @Override
        public Set<I> build() {
            return builder.build();
        }
    }

    /**
     * Default builder returning an ImmutableSet. The immutable set is
     * order preserving.
     *
     * @param <I> The item type.
     */
    public static class OrderedBuilder<I> extends DefaultBuilder<I> {
    }

    @Nonnull
    public static <I> PContainerProvider<Set<I>, PSet<I>> provider(PDescriptorProvider itemDesc) {
        return provider(itemDesc, DefaultBuilder::new);
    }

    @Nonnull
    public static <I extends Comparable<I>> PContainerProvider<Set<I>, PSet<I>> sortedProvider(PDescriptorProvider itemDesc) {
        return provider(itemDesc, SortedBuilder::new);
    }

    @Nonnull
    public static <I extends Comparable<I>> PContainerProvider<Set<I>, PSet<I>> orderedProvider(PDescriptorProvider itemDesc) {
        return provider(itemDesc, OrderedBuilder::new);
    }

    private static <I> PContainerProvider<Set<I>, PSet<I>> provider(PDescriptorProvider itemDesc,
                                                                    Supplier<Builder<I>> builderFactory) {
        return new PContainerProvider<>(new PSet<>(itemDesc, builderFactory));
    }
}
