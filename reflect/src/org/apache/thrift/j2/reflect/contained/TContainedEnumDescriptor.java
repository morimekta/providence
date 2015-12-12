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

import java.util.Iterator;
import java.util.List;

import org.apache.thrift.j2.TEnumBuilder;
import org.apache.thrift.j2.TEnumBuilderFactory;
import org.apache.thrift.j2.descriptor.TEnumDescriptor;

/**
 * Contained enum descriptor type.
 *
 * Also see {@link TContainedEnum}.
 */
public class TContainedEnumDescriptor
        extends TEnumDescriptor<TContainedEnum> {
    private TContainedEnum[] values;

    public TContainedEnumDescriptor(String comment,
                                    String packageName,
                                    String name) {
        super(comment, packageName, name, new _Factory());
        values = new TContainedEnum[0];
        ((_Factory) factory()).setType(this);
    }

    public void setValues(List<TContainedEnum> values) {
        this.values = new TContainedEnum[values.size()];
        Iterator<TContainedEnum> iter = values.iterator();
        for (int i = 0; i < this.values.length; ++i) {
            this.values[i] = iter.next();
        }
    }

    @Override
    public TContainedEnum[] getValues() {
        return values;
    }

    @Override
    public TContainedEnum getValueById(int id)  {
        for (TContainedEnum value : getValues()) {
            if (value.getValue() == id) {
                return value;
            }
        }
        return null;
    }

    @Override
    public TContainedEnum getValueByName(String name) {
        for (TContainedEnum value : getValues()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    private static class _Factory
            extends TEnumBuilderFactory<TContainedEnum> {
        private TContainedEnumDescriptor mType;

        public void setType(TContainedEnumDescriptor type) {
            mType = type;
        }

        @Override
        public TEnumBuilder<TContainedEnum> builder() {
            // TODO Auto-generated method stub
            return new TContainedEnum.Builder(mType);
        }
    }
}
