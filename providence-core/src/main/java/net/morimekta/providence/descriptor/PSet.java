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

package net.morimekta.providence.descriptor;

import net.morimekta.providence.PBuilder;
import net.morimekta.providence.PBuilderFactory;
import net.morimekta.providence.PType;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Descriptor for a set with item type.
 */
public class PSet<I> extends PContainer<Set<I>> {
    private final BuilderFactory<I> builderFactory;

    public PSet(PDescriptorProvider itemType,
                BuilderFactory<I> builderFactory) {
        super(itemType);
        this.builderFactory = builderFactory;
    }

    @Override
    public String getName() {
        return "set<" + itemDescriptor().getName() + ">";
    }

    @Override
    public String getQualifiedName(String packageName) {
        return "set<" + itemDescriptor().getQualifiedName(packageName) + ">";
    }

    @Override
    public PType getType() {
        return PType.SET;
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
        void add(I value);
        void addAll(Collection<I> items);
        void clear();

        @Override
        Set<I> build();
    }

    public interface BuilderFactory<I> extends PBuilderFactory<Set<I>> {
        @Override
        Builder<I> builder();
    }

    public static class ImmutableSetBuilder<I> implements Builder<I> {
        private ImmutableSet.Builder<I> builder;

        public ImmutableSetBuilder() {
            this.builder = ImmutableSet.builder();
        }

        @Override
        public void add(I value) {
            builder.add(value);
        }

        @Override
        public void addAll(Collection<I> items) {
            builder.addAll(items);
        }

        @Override
        public void clear() {
            builder = ImmutableSet.builder();
        }

        @Override
        public Set<I> build() {
            return builder.build();
        }
    }

    public static class ImmutableSortedSetBuilder<I extends Comparable<I>> implements Builder<I> {
        private ImmutableSortedSet.Builder<I> builder;

        public ImmutableSortedSetBuilder() {
            this.builder = ImmutableSortedSet.naturalOrder();
        }

        @Override
        public void add(I value) {
            builder.add(value);
        }

        @Override
        public void addAll(Collection<I> items) {
            builder.addAll(items);
        }

        @Override
        public void clear() {
            builder = ImmutableSortedSet.naturalOrder();
        }

        @Override
        public Set<I> build() {
            return builder.build();
        }
    }

    public static class LinkedHashSetBuilder<I> implements Builder<I> {
        private final LinkedHashSet<I> builder;

        public LinkedHashSetBuilder() {
            this.builder = new LinkedHashSet<>();
        }

        @Override
        public void add(I value) {
            builder.add(value);
        }

        @Override
        public void addAll(Collection<I> items) {
            builder.addAll(items);
        }

        @Override
        public void clear() {
            builder.clear();
        }

        @Override
        public Set<I> build() {
            return Collections.unmodifiableSet(builder);
        }
    }

    @Override
    public Builder<I> builder() {
        return builderFactory.builder();
    }

    public static <I> PContainerProvider<Set<I>, PSet<I>> provider(PDescriptorProvider itemDesc) {
        BuilderFactory<I> factory = new BuilderFactory<I>() {
            @Override
            public Builder<I> builder() {
                return new ImmutableSetBuilder<>();
            }
        };
        return provider(itemDesc, factory);
    }

    public static <I extends Comparable<I>> PContainerProvider<Set<I>, PSet<I>> sortedProvider(PDescriptorProvider itemDesc) {
        BuilderFactory<I> factory = new BuilderFactory<I>() {
            @Override
            public Builder<I> builder() {
                return new ImmutableSortedSetBuilder<>();
            }
        };
        return provider(itemDesc, factory);
    }

    public static <I extends Comparable<I>> PContainerProvider<Set<I>, PSet<I>> orderedProvider(PDescriptorProvider itemDesc) {
        BuilderFactory<I> factory = new BuilderFactory<I>() {
            @Override
            public Builder<I> builder() {
                return new LinkedHashSetBuilder<>();
            }
        };
        return provider(itemDesc, factory);
    }

    private static <I> PContainerProvider<Set<I>, PSet<I>> provider(PDescriptorProvider itemDesc,
                                                                      BuilderFactory<I> builderFactory) {
        return new PContainerProvider<>(new PSet<>(itemDesc, builderFactory));
    }
}
