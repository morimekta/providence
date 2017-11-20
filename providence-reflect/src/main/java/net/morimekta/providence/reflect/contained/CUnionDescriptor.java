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

import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PUnionDescriptor;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class CUnionDescriptor extends PUnionDescriptor<CUnion, CField> implements CMessageDescriptor {
    private final CField[]             fields;
    private final Map<Integer, CField> fieldIdMap;
    private final Map<String, CField>  fieldNameMap;
    private final Map<String, String>  annotations;
    private final String               comment;

    public CUnionDescriptor(String comment,
                            String packageName,
                            String name,
                            List<CField> fields,
                            Map<String, String> annotations) {
        super(packageName, name, new _Factory(),
              // overrides isSimple instead to avoid having to check fields
              // types before it's converted.
              false);
        ((_Factory) getBuilderSupplier()).setType(this);

        this.comment = comment;
        this.fields = fields.toArray(new CField[fields.size()]);
        this.annotations = annotations;

        Map<Integer, CField> fieldIdMap = new LinkedHashMap<>();
        Map<String, CField> fieldNameMap = new LinkedHashMap<>();
        for (CField field : fields) {
            fieldIdMap.put(field.getId(), field);
            fieldNameMap.put(field.getName(), field);
        }
        this.fieldIdMap = fieldIdMap;
        this.fieldNameMap = fieldNameMap;
    }

    @Override
    public final String getDocumentation() {
        return comment;
    }

    @Nonnull
    @Override
    public CField[] getFields() {
        return Arrays.copyOf(fields, fields.length);
    }

    @Override
    public CField findFieldByName(String name) {
        return fieldNameMap.get(name);
    }

    @Override
    public CField findFieldById(int id) {
        return fieldIdMap.get(id);
    }

    @Override
    public boolean isSimple() {
        for (PField field : getFields()) {
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

    @Nonnull
    @Override
    public CUnion.Builder builder() {
        return (CUnion.Builder) super.builder();
    }

    private static class _Factory implements Supplier<PMessageBuilder<CUnion,CField>> {
        private CUnionDescriptor mType;

        public void setType(CUnionDescriptor type) {
            mType = type;
        }

        @Nonnull
        @Override
        public PMessageBuilder<CUnion,CField> get() {
            return new CUnion.Builder(mType);
        }
    }
}
