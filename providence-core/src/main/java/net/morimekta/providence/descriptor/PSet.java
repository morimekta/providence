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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Descriptor for a set with item type.
 */
public class PSet<Item> extends PContainer<Set<Item>> {
    private final BuilderFactory<Item> builderFactory;

    public PSet(PDescriptorProvider itemType,
                BuilderFactory<Item> builderFactory) {
        super(itemType);
        this.builderFactory = builderFactory;
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

    public interface Builder<I> extends PBuilder<Set<I>> {
        @Nonnull
        Builder<I> add(@Nonnull I value);
        @Nonnull
        Builder<I> addAll(@Nonnull Collection<I> items);
        @Nonnull
        Builder<I> clear();

        @Nonnull
        @Override
        Set<I> build();
    }

    private interface BuilderFactory<I> extends PBuilderFactory<Set<I>> {
        @Nonnull
        @Override
        Builder<I> builder();
    }

    public static class ImmutableSetBuilder<I> extends LinkedHashSetBuilder<I> {
        @Nonnull
        @Override
        public Set<I> build() {
            return ImmutableSet.copyOf(builder);
        }
    }

    public static class ImmutableSortedSetBuilder<I extends Comparable<I>> extends LinkedHashSetBuilder<I> {
        @Nonnull
        @Override
        public Set<I> build() {
            return ImmutableSortedSet.copyOf(builder);
        }
    }

    public static class LinkedHashSetBuilder<I> implements Builder<I> {
        final LinkedHashSet<I> builder;

        public LinkedHashSetBuilder() {
            this.builder = new LinkedHashSet<>();
        }

        @Nonnull
        @Override
        public LinkedHashSetBuilder<I> add(@Nonnull I value) {
            builder.add(value);
            return this;
        }

        @Nonnull
        @Override
        public LinkedHashSetBuilder<I> addAll(@Nonnull Collection<I> items) {
            builder.addAll(items);
            return this;
        }

        @Nonnull
        @Override
        public LinkedHashSetBuilder<I> clear() {
            builder.clear();
            return this;
        }

        @Nonnull
        @Override
        public Set<I> build() {
            return Collections.unmodifiableSet(builder);
        }
    }

    @Override
    public Builder<Item> builder() {
        return builderFactory.builder();
    }

    public static <I> PContainerProvider<Set<I>, PSet<I>> provider(PDescriptorProvider itemDesc) {
        return provider(itemDesc, ImmutableSetBuilder::new);
    }

    public static <I extends Comparable<I>> PContainerProvider<Set<I>, PSet<I>> sortedProvider(PDescriptorProvider itemDesc) {
        return provider(itemDesc, ImmutableSortedSetBuilder::new);
    }

    public static <I extends Comparable<I>> PContainerProvider<Set<I>, PSet<I>> orderedProvider(PDescriptorProvider itemDesc) {
        return provider(itemDesc, LinkedHashSetBuilder::new);
    }

    private static <I> PContainerProvider<Set<I>, PSet<I>> provider(PDescriptorProvider itemDesc,
                                                                      BuilderFactory<I> builderFactory) {
        return new PContainerProvider<>(new PSet<>(itemDesc, builderFactory));
    }
}
