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

import com.google.common.collect.ImmutableList;

import java.util.Collection;
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

    @Override
    public String getName() {
        return "list<" + itemDescriptor().getName() + ">";
    }

    @Override
    public String getQualifiedName(String packageName) {
        return "list<" + itemDescriptor().getQualifiedName(packageName) + ">";
    }

    @Override
    public PType getType() {
        return PType.LIST;
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
        Builder<I> add(I value);
        Builder<I> addAll(Collection<I> items);
        Builder<I> clear();

        @Override
        List<I> build();
    }

    private interface BuilderFactory<I> extends PBuilderFactory<List<I>> {
        @Override
        Builder<I> builder();
    }

    public static class ImmutableListBuilder<I> implements Builder<I> {
        private ImmutableList.Builder<I> builder;

        public ImmutableListBuilder() {
            builder = ImmutableList.builder();
        }

        @Override
        public ImmutableListBuilder<I> add(I value) {
            builder.add(value);
            return this;
        }

        @Override
        public ImmutableListBuilder<I> addAll(Collection<I> items) {
            builder.addAll(items);
            return this;
        }

        @Override
        public ImmutableListBuilder<I> clear() {
            builder = ImmutableList.builder();
            return this;
        }

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
        return provider(itemDesc, ImmutableListBuilder::new);
    }

    public static <I> PContainerProvider<List<I>, PList<I>> provider(PDescriptorProvider itemDesc,
                                                                     BuilderFactory<I> builderFactory) {
        return new PContainerProvider<>(new PList<>(itemDesc, builderFactory));
    }
}
