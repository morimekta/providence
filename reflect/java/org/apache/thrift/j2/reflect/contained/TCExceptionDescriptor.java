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
public class TCExceptionDescriptor
        extends TStructDescriptor<TCException, TCField> {
    private final TCField[]             mFields;
    private final Map<Integer, TCField> mFieldIdMap;
    private final Map<String, TCField>  mFieldNameMap;

    public TCExceptionDescriptor(String comment,
                                 String packageName,
                                 String name,
                                 List<TCField> fields) {
        super(comment, packageName, name, new _Factory(),
              // overrides isSimple instead to avoid having to check fields
              // types before it's converted.
              false, false);
        ((_Factory) factory()).setType(this);

        mFields = fields.toArray(new TCField[fields.size()]);

        Map<Integer, TCField> fieldIdMap = new LinkedHashMap<>();
        Map<String, TCField> fieldNameMap = new LinkedHashMap<>();
        for (TCField field : fields) {
            fieldIdMap.put(field.getKey(), field);
            fieldNameMap.put(field.getName(), field);
        }
        mFieldIdMap = fieldIdMap;
        mFieldNameMap = fieldNameMap;
    }

    @Override
    public TCField[] getFields() {
        return mFields;
    }

    @Override
    public TCField getField(String name) {
        return mFieldNameMap.get(name);
    }

    @Override
    public TCField getField(int key) {
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
            extends TMessageBuilderFactory<TCException> {
        private TCExceptionDescriptor mType;

        public void setType(TCExceptionDescriptor type) {
            mType = type;
        }

        @Override
        public TMessageBuilder<TCException> builder() {
            // TODO Auto-generated method stub
            return new TCException.Builder(mType);
        }
    }
}
