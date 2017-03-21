/*
 * Copyright 2016 Providence Authors
 *
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

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contained enum descriptor type.
 * <p>
 * Also see {@link CEnumValue}.
 */
public class CEnumDescriptor extends PEnumDescriptor<CEnumValue> implements CAnnotatedDescriptor {
    private CEnumValue[] values;

    private final Map<String, String> annotations;
    private final String              comment;

    public CEnumDescriptor(String comment, String packageName, String name, Map<String, String> annotations) {
        super(packageName, name, new _Factory());
        this.values = new CEnumValue[0];
        this.comment = comment;
        this.annotations = annotations;
        ((_Factory) getFactoryInternal()).setType(this);
    }

    public void setValues(List<CEnumValue> values) {
        this.values = new CEnumValue[values.size()];
        Iterator<CEnumValue> iter = values.iterator();
        for (int i = 0; i < this.values.length; ++i) {
            this.values[i] = iter.next();
        }
    }

    @Override
    public final String getDocumentation() {
        return comment;
    }

    @Nonnull
    @Override
    public CEnumValue[] getValues() {
        return Arrays.copyOf(values, values.length);
    }

    @Override
    public CEnumValue getValueById(int id) {
        for (CEnumValue value : getValues()) {
            if (value.getValue() == id) {
                return value;
            }
        }
        return null;
    }

    @Override
    public CEnumValue getValueByName(String name) {
        for (CEnumValue value : getValues()) {
            if (value.getName()
                     .equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getAnnotations() {
        if (annotations != null) {
            return annotations.keySet();
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public boolean hasAnnotation(@Nonnull String name) {
        if (annotations != null) {
            return annotations.containsKey(name);
        }
        return false;
    }

    @Override
    public String getAnnotationValue(@Nonnull String name) {
        if (annotations != null) {
            return annotations.get(name);
        }
        return null;
    }

    private static class _Factory extends PEnumBuilderFactory<CEnumValue> {
        private CEnumDescriptor mType;

        public void setType(CEnumDescriptor type) {
            mType = type;
        }

        @Nonnull
        @Override
        public PEnumBuilder<CEnumValue> builder() {
            return new CEnumValue.Builder(mType);
        }
    }
}
