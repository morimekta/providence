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

import java.util.List;

import org.apache.thrift.j2.TType;

/**
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public class TList<I>
        extends TContainer<I, List<I>> {
    public TList(TDescriptorProvider<I> itemType) {
        super(itemType);
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
    public TType getType() {
        return TType.LIST;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TList)) {
            return false;
        }
        TList<?> other = (TList<?>) o;
        return other.itemDescriptor().equals(itemDescriptor());
    }

    @Override
    public int hashCode() {
        return TList.class.hashCode() +
               itemDescriptor().hashCode();
    }

    public static <I> TContainerProvider<I, List<I>, TList<I>> provider(TDescriptorProvider<I> itemType) {
        return new TContainerProvider<>(new TList<>(itemType));
    }
}