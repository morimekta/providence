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

import org.apache.thrift2.TMessage;
import org.apache.thrift2.TMessageBuilderFactory;
import org.apache.thrift2.TMessageVariant;
import org.apache.thrift2.TType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The definition of a thrift structure.
 *
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 25.08.15
 */
public class TStructDescriptor<T extends TMessage<T>>
        extends TDeclaredDescriptor<T> {
    private final List<TField<?>>           mFields;
    private final Map<Integer, TField<?>>   mFieldIdMap;
    private final Map<String, TField<?>>    mFieldNameMap;
    private final TMessageBuilderFactory<T> mProvider;

    public TStructDescriptor(String comment,
                             String packageName,
                             String name,
                             List<TField<?>> fields,
                             TMessageBuilderFactory<T> provider) {
        super(comment, packageName, name);

        Map<Integer, TField<?>> fieldIdMap = new LinkedHashMap<>();
        Map<String, TField<?>> fieldNameMap = new LinkedHashMap<>();
        for (TField<?> field : fields) {
            fieldIdMap.put(field.getKey(), field);
            fieldNameMap.put(field.getName(), field);
        }
        mFields = Collections.unmodifiableList(fields);
        mFieldIdMap = Collections.unmodifiableMap(fieldIdMap);
        mFieldNameMap = Collections.unmodifiableMap(fieldNameMap);

        mProvider = provider;
    }

    @Override
    public TType getType() {
        return TType.MESSAGE;
    }

    public TMessageVariant getVariant() {
        return TMessageVariant.STRUCT;
    }

    @Override
    public TMessageBuilderFactory<T> factory() {
        return mProvider;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TStructDescriptor)) {
            return false;
        }
        TStructDescriptor<?> other = (TStructDescriptor<?>) o;
        if (!getQualifiedName(null).equals(other.getQualifiedName(null)) ||
            !getVariant().equals(other.getVariant()) ||
            mFields.size() != other.getFields().size()) {
            return false;
        }
        for (TField<?> field : mFields) {
            if (!field.equals(other.getField(field.getKey()))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = TStructDescriptor.class.hashCode() +
                   getQualifiedName(null).hashCode() +
                   getVariant().hashCode();
        for (TField<?> field : getFields()) {
            hash += field.hashCode();
        }
        return hash;
    }

    public List<TField<?>> getFields() {
        return mFields;
    }

    public TField<?> getField(String name) {
        return mFieldNameMap.get(name);
    }

    public TField<?> getField(int key) {
        return mFieldIdMap.get(key);
    }
}
