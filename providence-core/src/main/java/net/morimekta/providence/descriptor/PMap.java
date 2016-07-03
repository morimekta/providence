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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Descriptor for a map with key and item type.
 */
public class PMap<Key, Value> extends PContainer<Map<Key, Value>> {
    private final PDescriptorProvider        keyDescriptor;
    private final BuilderFactory<Key, Value> builderFactory;

    public PMap(PDescriptorProvider keyDesc,
                PDescriptorProvider itemDesc,
                BuilderFactory<Key, Value> builderFactory) {
        super(itemDesc);
        this.keyDescriptor = keyDesc;
        this.builderFactory = builderFactory;
    }

    public PDescriptor keyDescriptor() {
        return keyDescriptor.descriptor();
    }

    @Override
    public String getName() {
        return "map<" + keyDescriptor().getName() + "," + itemDescriptor().getName() + ">";
    }

    @Override
    public String getQualifiedName(String packageName) {
        return "map<" + keyDescriptor().getQualifiedName(packageName) + "," +
               itemDescriptor().getQualifiedName(packageName) + ">";
    }

    @Override
    public PType getType() {
        return PType.MAP;
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

    public interface Builder<K, V> extends PBuilder<Map<K, V>> {
        Builder<K, V> put(K key, V value);
        Builder<K, V> putAll(Map<K, V> map);
        Builder<K, V> clear();

        @Override
        Map<K, V> build();
    }

    private interface BuilderFactory<K, V> extends PBuilderFactory<Map<K, V>> {
        Builder<K, V> builder();
    }

    public static class ImmutableMapBuilder<K, V> implements Builder<K, V> {
        private ImmutableMap.Builder<K, V> builder;

        public ImmutableMapBuilder() {
            this.builder = ImmutableMap.builder();
        }

        @Override
        public ImmutableMapBuilder<K, V> put(K key, V value) {
            builder.put(key, value);
            return this;
        }

        @Override
        public ImmutableMapBuilder<K, V> putAll(Map<K, V> map) {
            builder.putAll(map);
            return this;
        }

        @Override
        public ImmutableMapBuilder<K, V> clear() {
            builder = ImmutableMap.builder();
            return this;
        }

        @Override
        public Map<K, V> build() {
            return builder.build();
        }
    }

    public static class ImmutableSortedMapBuilder<K extends Comparable, V> implements Builder<K, V> {
        private ImmutableSortedMap.Builder<K, V> builder;

        public ImmutableSortedMapBuilder() {
            this.builder = ImmutableSortedMap.naturalOrder();
        }

        @Override
        public ImmutableSortedMapBuilder<K, V> put(K key, V value) {
            builder.put(key, value);
            return this;
        }

        @Override
        public ImmutableSortedMapBuilder<K, V> putAll(Map<K, V> map) {
            builder.putAll(map);
            return this;
        }

        @Override
        public ImmutableSortedMapBuilder<K, V> clear() {
            builder = ImmutableSortedMap.naturalOrder();
            return this;
        }

        @Override
        public Map<K, V> build() {
            return builder.build();
        }
    }

    public static class LinkedHashMapBuilder<K, V> implements Builder<K, V> {
        private final LinkedHashMap<K, V> builder;

        public LinkedHashMapBuilder() {
            this.builder = new LinkedHashMap<>();
        }

        @Override
        public LinkedHashMapBuilder<K, V> put(K key, V value) {
            builder.put(key, value);
            return this;
        }

        @Override
        public LinkedHashMapBuilder<K, V> putAll(Map<K, V> map) {
            builder.putAll(map);
            return this;
        }

        @Override
        public LinkedHashMapBuilder<K, V> clear() {
            builder.clear();
            return this;
        }

        @Override
        public Map<K, V> build() {
            return Collections.unmodifiableMap(builder);
        }
    }

    @Override
    public Builder<Key, Value> builder() {
        return builderFactory.builder();
    }

    public static <K, V> PContainerProvider<Map<K, V>, PMap<K, V>> provider(PDescriptorProvider keyDesc,
                                                                            PDescriptorProvider itemDesc) {
        return provider(keyDesc, itemDesc, ImmutableMapBuilder::new);
    }

    public static <K extends Comparable<K>, V> PContainerProvider<Map<K, V>, PMap<K, V>> sortedProvider(PDescriptorProvider keyDesc,
                                                                                                        PDescriptorProvider itemDesc) {
        return provider(keyDesc, itemDesc, ImmutableSortedMapBuilder::new);
    }

    public static <K, V> PContainerProvider<Map<K, V>, PMap<K, V>> orderedProvider(PDescriptorProvider keyDesc,
                                                                                   PDescriptorProvider itemDesc) {
        return provider(keyDesc, itemDesc, LinkedHashMapBuilder::new);
    }

    private static <K, V> PContainerProvider<Map<K, V>, PMap<K, V>> provider(PDescriptorProvider keyDesc,
                                                                             PDescriptorProvider itemDesc,
                                                                             BuilderFactory<K, V> builderFactory) {
        return new PContainerProvider<>(new PMap<>(keyDesc, itemDesc, builderFactory));
    }
}
