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

package org.apache.thrift.j2.reflect.contained;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TMessageVariant;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TStructDescriptor;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class TContainedExceptionDescriptor
        extends TStructDescriptor<TContainedException, TContainedField> {
    private final TContainedField[]             mFields;
    private final Map<Integer, TContainedField> mFieldIdMap;
    private final Map<String, TContainedField>  mFieldNameMap;

    public TContainedExceptionDescriptor(String comment,
                                         String packageName,
                                         String name,
                                         List<TContainedField> fields) {
        super(comment, packageName, name, new _Factory(),
              // overrides isSimple instead to avoid having to check fields
              // types before it's converted.
              false, false);
        ((_Factory) factory()).setType(this);

        mFields = fields.toArray(new TContainedField[fields.size()]);

        Map<Integer, TContainedField> fieldIdMap = new LinkedHashMap<>();
        Map<String, TContainedField> fieldNameMap = new LinkedHashMap<>();
        for (TContainedField field : fields) {
            fieldIdMap.put(field.getKey(), field);
            fieldNameMap.put(field.getName(), field);
        }
        mFieldIdMap = fieldIdMap;
        mFieldNameMap = fieldNameMap;
    }

    @Override
    public TContainedField[] getFields() {
        return mFields;
    }

    @Override
    public TContainedField getField(String name) {
        return mFieldNameMap.get(name);
    }

    @Override
    public TContainedField getField(int key) {
        return mFieldIdMap.get(key);
    }

    @Override
    public boolean isSimple() {
        for (TField<?> field : getFields()) {
            switch (field.getType()) {
                case MAP:
                case SET:
                case LIST:
                case MESSAGE:
                    return false;
                default:
                    break;
            }
        }
        return true;
    }

    @Override
    public TMessageVariant getVariant() {
        return TMessageVariant.EXCEPTION;
    }

    private static class _Factory
            extends TMessageBuilderFactory<TContainedException> {
        private TContainedExceptionDescriptor mType;

        public void setType(TContainedExceptionDescriptor type) {
            mType = type;
        }

        @Override
        public TMessageBuilder<TContainedException> builder() {
            // TODO Auto-generated method stub
            return new TContainedException.Builder(mType);
        }
    }
}
