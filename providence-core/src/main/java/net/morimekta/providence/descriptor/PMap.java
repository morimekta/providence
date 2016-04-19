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
public class PMap<K, V> extends PContainer<V, Map<K, V>> {
    private final PDescriptorProvider<K> keyDescriptor;
    private final BuilderFactory<K, V>   builderFactory;

    public PMap(PDescriptorProvider<K> keyDesc,
                PDescriptorProvider<V> itemDesc,
                BuilderFactory<K, V> builderFactory) {
        super(itemDesc);
        this.keyDescriptor = keyDesc;
        this.builderFactory = builderFactory;
    }

    public PDescriptor<K> keyDescriptor() {
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
        void put(K key, V value);
        void putAll(Map<K, V> map);
        void clear();

        @Override
        Map<K, V> build();
    }

    public interface BuilderFactory<K, V> extends PBuilderFactory<Map<K, V>> {
        Builder<K, V> builder();
    }

    public static class ImmutableMapBuilder<K, V> implements Builder<K, V> {
        private ImmutableMap.Builder<K, V> builder;

        public ImmutableMapBuilder() {
            this.builder = ImmutableMap.builder();
        }

        @Override
        public void put(K key, V value) {
            builder.put(key, value);
        }

        @Override
        public void putAll(Map<K, V> map) {
            builder.putAll(map);
        }

        @Override
        public void clear() {
            builder = ImmutableMap.builder();
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
        public void put(K key, V value) {
            builder.put(key, value);
        }

        @Override
        public void putAll(Map<K, V> map) {
            builder.putAll(map);
        }

        @Override
        public void clear() {
            builder = ImmutableSortedMap.naturalOrder();
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
        public void put(K key, V value) {
            builder.put(key, value);
        }

        @Override
        public void putAll(Map<K, V> map) {
            builder.putAll(map);
        }

        @Override
        public void clear() {
            builder.clear();
        }

        @Override
        public Map<K, V> build() {
            return Collections.unmodifiableMap(builder);
        }
    }

    @Override
    public Builder<K, V> builder() {
        return builderFactory.builder();
    }

    public static <K, V> PContainerProvider<V, Map<K, V>, PMap<K, V>> provider(PDescriptorProvider<K> keyDesc,
                                                                               PDescriptorProvider<V> itemDesc) {
        BuilderFactory<K, V> factory = new BuilderFactory<K, V>() {
            @Override
            public Builder<K, V> builder() {
                return new ImmutableMapBuilder<>();
            }
        };
        return provider(keyDesc, itemDesc, factory);
    }

    public static <K extends Comparable<K>, V> PContainerProvider<V, Map<K, V>, PMap<K, V>> sortedProvider(PDescriptorProvider<K> keyDesc,
                                                                                                           PDescriptorProvider<V> itemDesc) {
        BuilderFactory<K, V> factory = new BuilderFactory<K, V>() {
            @Override
            public Builder<K, V> builder() {
                return new ImmutableSortedMapBuilder<>();
            }
        };
        return provider(keyDesc, itemDesc, factory);
    }

    public static <K, V> PContainerProvider<V, Map<K, V>, PMap<K, V>> orderedProvider(PDescriptorProvider<K> keyDesc,
                                                                                      PDescriptorProvider<V> itemDesc) {
        BuilderFactory<K, V> factory = new BuilderFactory<K, V>() {
            @Override
            public Builder<K, V> builder() {
                return new LinkedHashMapBuilder<>();
            }
        };
        return provider(keyDesc, itemDesc, factory);
    }

    public static <K, V> PContainerProvider<V, Map<K, V>, PMap<K, V>> provider(PDescriptorProvider<K> keyDesc,
                                                                               PDescriptorProvider<V> itemDesc,
                                                                               BuilderFactory<K, V> builderFactory) {
        return new PContainerProvider<>(new PMap<>(keyDesc, itemDesc, builderFactory));
    }
}
