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

package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.PEnumBuilder;
import net.morimekta.providence.PEnumBuilderFactory;
import net.morimekta.providence.descriptor.PEnumDescriptor;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contained enum descriptor type.
 * <p>
 * Also see {@link CEnum}.
 */
public class CEnumDescriptor extends PEnumDescriptor<CEnum> implements CAnnotatedDescriptor {
    private CEnum[] values;
    private Map<String, String> annotations;

    public CEnumDescriptor(String comment, String packageName, String name, Map<String, String> annotations) {
        super(comment, packageName, name, new _Factory());
        this.values = new CEnum[0];
        this.annotations = annotations;
        ((_Factory) getFactoryInternal()).setType(this);
    }

    public void setValues(List<CEnum> values) {
        this.values = new CEnum[values.size()];
        Iterator<CEnum> iter = values.iterator();
        for (int i = 0; i < this.values.length; ++i) {
            this.values[i] = iter.next();
        }
    }

    @Override
    public CEnum[] getValues() {
        return values;
    }

    @Override
    public CEnum getValueById(int id) {
        for (CEnum value : getValues()) {
            if (value.getValue() == id) {
                return value;
            }
        }
        return null;
    }

    @Override
    public CEnum getValueByName(String name) {
        for (CEnum value : getValues()) {
            if (value.getName()
                     .equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getAnnotations() {
        if (annotations != null) {
            return annotations.keySet();
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public boolean hasAnnotation(String name) {
        if (annotations != null) {
            return annotations.containsKey(name);
        }
        return false;
    }

    @Override
    public String getAnnotation(String name) {
        if (annotations != null) {
            return annotations.get(name);
        }
        return null;
    }

    private static class _Factory extends PEnumBuilderFactory<CEnum> {
        private CEnumDescriptor mType;

        public void setType(CEnumDescriptor type) {
            mType = type;
        }

        @Override
        public PEnumBuilder<CEnum> builder() {
            // TODO Auto-generated method stub
            return new CEnum.Builder(mType);
        }
    }
}
