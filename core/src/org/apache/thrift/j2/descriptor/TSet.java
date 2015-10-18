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

package org.apache.thrift.j2.descriptor;

import java.util.Set;

import org.apache.thrift.j2.TType;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 25.08.15
 */
public class TSet<I>
        extends TContainer<I, Set<I>> {
    public TSet(TDescriptorProvider<I> itemType) {
        super(itemType);
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
    public TType getType() {
        return TType.SET;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TSet)) {
            return false;
        }
        TSet<?> other = (TSet<?>) o;
        return other.itemDescriptor().equals(itemDescriptor());
    }

    @Override
    public int hashCode() {
        return TSet.class.hashCode() +
               itemDescriptor().hashCode();
    }

    public static <I> TContainerProvider<I, Set<I>, TSet<I>> provider(TDescriptorProvider<I> itemType) {
        return new TContainerProvider<>(new TSet<>(itemType));
    }
}
