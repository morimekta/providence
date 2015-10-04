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

package org.apache.thrift2.descriptor;

import java.util.Map;

import org.apache.thrift2.TType;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 25.08.15
 */
public class TMap<K, V>
        extends TContainer<V, Map<K, V>> {
    private final TDescriptorProvider<K> mKeyDescriptor;

    public TMap(TDescriptorProvider<K> keyDesc,
                TDescriptorProvider<V> itemDesc) {
        super(itemDesc);
        mKeyDescriptor = keyDesc;
    }

    public TDescriptor<K> keyDescriptor() {
        return mKeyDescriptor.descriptor();
    }

    @Override
    public String getName() {
        return "map<" + keyDescriptor().getName() + "," + itemDescriptor().getName() + ">";
    }

    @Override
    public String getQualifiedName(String packageName) {
        return "map<" + keyDescriptor().getQualifiedName(packageName) + "," + itemDescriptor().getQualifiedName(
                packageName) + ">";
    }

    @Override
    public TType getType() {
        return TType.MAP;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TMap)) {
            return false;
        }
        TMap<?, ?> other = (TMap<?, ?>) o;
        return other.itemDescriptor().equals(itemDescriptor()) &&
               other.keyDescriptor().equals(keyDescriptor());
    }

    @Override
    public int hashCode() {
        return TMap.class.hashCode() +
               itemDescriptor().hashCode() +
               keyDescriptor().hashCode();
    }

    public static <K, V>
    TContainerProvider<V, Map<K, V>, TMap<K, V>> provider(
            TDescriptorProvider<K> keyDesc,
            TDescriptorProvider<V> itemDesc) {
        return new TContainerProvider<>(new TMap<>(keyDesc, itemDesc));
    }
}
