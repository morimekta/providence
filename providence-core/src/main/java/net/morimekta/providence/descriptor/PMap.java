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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Descriptor for a map with key and item type.
 */
public class PMap<Key, Value> extends PContainer<Map<Key, Value>> {
    private final PDescriptorProvider           keyDescriptor;
    private final Supplier<Builder<Key, Value>> builderSupplier;

    public PMap(PDescriptorProvider keyDesc,
                PDescriptorProvider itemDesc,
                Supplier<Builder<Key, Value>> builderSupplier) {
        super(itemDesc);
        this.keyDescriptor = keyDesc;
        this.builderSupplier = builderSupplier;
    }

    public PDescriptor keyDescriptor() {
        return keyDescriptor.descriptor();
    }

    @Nonnull
    @Override
    public String getName() {
        return "map<" + keyDescriptor().getName() + "," + itemDescriptor().getName() + ">";
    }

    @Nonnull
    @Override
    public String getQualifiedName(String programContext) {
        return "map<" + keyDescriptor().getQualifiedName(programContext) + "," +
               itemDescriptor().getQualifiedName(programContext) + ">";
    }

    @Nonnull
    @Override
    public PType getType() {
        return PType.MAP;
    }

    @Nullable
    @Override
    public Object getDefaultValue() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof PMap)) {
            return false;
        }
        PMap<?, ?> other = (PMap<?, ?>) o;
        return other.itemDescriptor()
                    .equals(itemDescriptor()) && other.keyDescriptor()
                                                      .equals(keyDescriptor());
    }

    @Override
    public int hashCode() {
        return PMap.class.hashCode() +
               itemDescriptor().hashCode() +
               keyDescriptor().hashCode();
    }

    /**
     * Container builder to be used in serialization.
     *
     * @param <K> The key type.
     * @param <V> The value type.
     */
    public interface Builder<K, V> extends PBuilder<Map<K, V>> {
        @Nonnull
        Builder<K, V> put(@Nonnull K key, @Nonnull V value);
        @Nonnull
        Builder<K, V> putAll(@Nonnull Map<K, V> map);

        @Nonnull
        @Override
        Map<K, V> build();
    }

    /**
     * Default map builder returning an ImmutableMap.
     *
     * @param <K> The key type.
     * @param <V> The value type.
     */
    public static class DefaultBuilder<K, V> implements Builder<K,V> {
        private ImmutableMap.Builder<K,V> builder;

        public DefaultBuilder() {
            builder = ImmutableMap.builder();
        }

        @Nonnull
        @Override
        public PMap.Builder<K,V> put(@Nonnull K key, @Nonnull V value) {
            builder.put(key, value);
            return this;
        }

        @Nonnull
        @Override
        public PMap.Builder<K,V> putAll(@Nonnull Map<K,V> items) {
            builder.putAll(items);
            return this;
        }

        @Nonnull
        @Override
        public Map<K,V> build() {
            return builder.build();
        }
    }

    /**
     * Sorted map builder returning an ImmutableSortedMap.
     *
     * @param <K> The key type.
     * @param <V> The value type.
     */
    public static class SortedBuilder<K extends Comparable, V> implements Builder<K, V> {
        private ImmutableSortedMap.Builder<K,V> builder;

        public SortedBuilder() {
            builder = ImmutableSortedMap.naturalOrder();
        }

        @Nonnull
        @Override
        public PMap.Builder<K,V> put(@Nonnull K key, @Nonnull V value) {
            builder.put(key, value);
            return this;
        }

        @Nonnull
        @Override
        public PMap.Builder<K,V> putAll(@Nonnull Map<K,V> items) {
            builder.putAll(items);
            return this;
        }

        @Nonnull
        @Override
        public Map<K,V> build() {
            return builder.build();
        }
    }

    /**
     * Ordered map builder returning an ImmutableMap. The immutable map
     * is order preserving, so the effect is the same.
     *
     * @param <K> The key type.
     * @param <V> The value type.
     */
    public static class OrderedBuilder<K, V> extends DefaultBuilder<K, V> {
    }

    @Nonnull
    @Override
    public Builder<Key, Value> builder() {
        return builderSupplier.get();
    }

    @Nonnull
    public static <K, V> PContainerProvider<Map<K, V>, PMap<K, V>> provider(PDescriptorProvider keyDesc,
                                                                            PDescriptorProvider itemDesc) {
        return provider(keyDesc, itemDesc, DefaultBuilder::new);
    }

    @Nonnull
    public static <K extends Comparable<K>, V> PContainerProvider<Map<K, V>, PMap<K, V>> sortedProvider(PDescriptorProvider keyDesc,
                                                                                                        PDescriptorProvider itemDesc) {
        return provider(keyDesc, itemDesc, SortedBuilder::new);
    }

    @Nonnull
    public static <K, V> PContainerProvider<Map<K, V>, PMap<K, V>> orderedProvider(PDescriptorProvider keyDesc,
                                                                                   PDescriptorProvider itemDesc) {
        return provider(keyDesc, itemDesc, OrderedBuilder::new);
    }

    private static <K, V> PContainerProvider<Map<K, V>, PMap<K, V>> provider(PDescriptorProvider keyDesc,
                                                                             PDescriptorProvider itemDesc,
                                                                             Supplier<Builder<K, V>> builderFactory) {
        return new PContainerProvider<>(new PMap<>(keyDesc, itemDesc, builderFactory));
    }
}
