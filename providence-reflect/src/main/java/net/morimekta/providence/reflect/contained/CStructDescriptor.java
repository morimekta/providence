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
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.json.JsonCompactibleDescriptor;
import net.morimekta.providence.util.ThriftAnnotation;

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
public class CStructDescriptor extends PStructDescriptor<CStruct, CField> implements CMessageDescriptor,
                                                                                     JsonCompactibleDescriptor {
    public static final int     MAX_COMPACT_FIELDS = 10;

    private final String               comment;
    private final CField[]             fields;
    private final Map<Integer, CField> fieldIdMap;
    private final Map<String, CField>  fieldNameMap;
    private final Map<String, String>  annotations;
    private final boolean              compactible;

    public CStructDescriptor(String comment, String programName, String name, List<CField> fields, Map<String, String> annotations) {
        super(programName, name, new _Factory(), false);
        ((_Factory) getBuilderSupplier()).setType(this);

        this.comment = comment;
        this.fields = fields.toArray(new CField[fields.size()]);
        this.annotations = annotations;
        this.compactible = isCompactCompatible(fields, annotations);

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

    @Override
    public boolean isSimple() {
        for (CField field : getFields()) {
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
    public boolean isJsonCompactible() {
        return compactible;
    }

    private static class _Factory implements Supplier<PMessageBuilder<CStruct,CField>> {
        private CStructDescriptor mType;

        public void setType(CStructDescriptor type) {
            mType = type;
        }

        @Nonnull
        @Override
        public PMessageBuilder<CStruct,CField> get() {
            return new CStruct.Builder(mType);
        }
    }

    private static boolean isCompactCompatible(List<CField> fields, Map<String, String> annotations) {
        if (annotations == null) {
            return false;
        }
        if (!annotations.containsKey(ThriftAnnotation.JSON_COMPACT.tag) &&
            // legacy annotation version special handling.
            !annotations.containsKey("compact")) {
            return false;
        }
        if (fields.size() > MAX_COMPACT_FIELDS) {
            return false;
        }
        int next = 1;
        boolean hasOptional = false;
        for (CField field : fields) {
            if (field.getId() != next) {
                return false;
            }
            if (hasOptional && field.getRequirement() == PRequirement.REQUIRED) {
                return false;
            }
            if (field.getRequirement() == PRequirement.OPTIONAL) {
                hasOptional = true;
            }
            next++;
        }
        return true;
    }

}
