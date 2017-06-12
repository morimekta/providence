/*
 * Copyright 2015-2016 Providence Authors
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
package net.morimekta.providence.generator.format.java.utils;

import net.morimekta.providence.PMessageVariant;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CField;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;

import static net.morimekta.providence.generator.format.java.utils.JUtils.camelCase;
import static net.morimekta.util.Strings.c_case;

/**
 *
 */
public class JField {
    private final CField  field;
    private final JHelper helper;
    private final int     index;

    public JField(CField field, JHelper helper, int index) {
        this.field = field;
        this.helper = helper;
        this.index = index;
    }

    public int index() {
        return index;
    }

    public CField field() {
        return field;
    }

    public PType type() {
        return field.getType();
    }

    public boolean binary() {
        return field.getType() == PType.BINARY;
    }

    public int id() {
        return field.getId();
    }

    public String name() {
        return field.getName();
    }

    public String hasName() { return camelCase("__has_", field.getName()); }

    public String sizeName() { return camelCase("__size_", field.getName()); }

    public String param() {
        return camelCase("p", field.getName());
    }

    public String member() {
        return camelCase("m", field.getName());
    }

    public String mutable() {
        return camelCase("mutable", field.getName());
    }

    public String isSet() {
        return camelCase("isSet", field.getName());
    }

    public String isModified() {
        return camelCase("isModified", field.getName());
    }

    public String getter() {
        if (field.getType() == PType.BOOL) {
            return camelCase("is", field.getName());
        }
        return camelCase("get", field.getName());
    }

    public String presence() {
        return camelCase("has", field.getName());
    }

    public String counter() {
        return camelCase("num", field.getName());
    }

    public String setter() {
        return camelCase("set", field.getName());
    }

    public String adder() {
        if (field.getType() == PType.MAP) {
            return camelCase("putIn", field.getName());
        } else {
            return camelCase("addTo", field.getName());
        }
    }

    public String resetter() {
        return camelCase("clear", field.getName());
    }

    public String fieldEnum() {
        return c_case(field.getName()).toUpperCase();
    }

    public String kDefault() {
        return camelCase("kDefault", field.getName());
    }

    public boolean hasDefaultConstant() {
        return isPrimitiveJavaValue() ||
               alwaysPresent() ||
               field.hasDefaultValue();
    }

    public boolean isRequired() {
        return field.getRequirement() == PRequirement.REQUIRED;
    }

    public boolean container() {
        switch (field.getType()) {
            case MAP:
            case SET:
            case LIST:
                return true;
            default:
                return false;
        }
    }

    public ContainerType containerType() {
        return JAnnotation.containerType(this);
    }

    public boolean alwaysPresent() {
        return field.getRequirement() != PRequirement.OPTIONAL &&
               helper.getDefaultValue(field) != null;
    }

    public boolean isPrimitiveJavaValue() {
        return field.getDescriptor() instanceof PPrimitive &&
               ((PPrimitive) field.getDescriptor()).isNativePrimitive();
    }

    public String valueType() throws GeneratorException {
        return helper.getValueType(field.getDescriptor());
    }

    public String fieldType() throws GeneratorException {
        if (alwaysPresent()) {
            return valueType();
        }
        return helper.getFieldType(field.getDescriptor());
    }

    public String paramType() throws GeneratorException {
        if (alwaysPresent() && isRequired()) {
            return valueType();
        }
        return helper.getFieldType(field.getDescriptor());
    }

    public String instanceType() throws GeneratorException {
        return helper.getInstanceClassName(field);
    }

    public String builderFieldType() throws GeneratorException  {
        switch (field.getType()) {
            case MAP: {
                PMap mType = (PMap) field.getDescriptor();
                String kType = helper.getFieldType(mType.keyDescriptor());
                String iType = helper.getFieldType(mType.itemDescriptor());
                return String.format(
                        "%s<%s,%s>",
                        PMap.Builder.class.getName().replace('$', '.'),
                        kType, iType);
            }
            case SET: {
                PSet sType = (PSet) field.getDescriptor();
                String iType = helper.getFieldType(sType.itemDescriptor());
                return String.format(
                        "%s<%s>",
                        PSet.Builder.class.getName().replace('$', '.'),
                        iType);
            }
            case LIST: {
                PList lType = (PList) field.getDescriptor();
                String iType = helper.getFieldType(lType.itemDescriptor());
                return String.format(
                        "%s<%s>",
                        PList.Builder.class.getName().replace('$', '.'),
                        iType);
            }
            default:
                return fieldType();
        }
    }

    public String builderMutableType() throws GeneratorException  {
        switch (field.getType()) {
            case LIST:
                return LinkedList.class.getName().replace('$', '.');
            case MAP:
                switch (containerType()) {
                    case DEFAULT: return HashMap.class.getName().replace('$', '.');
                    case SORTED: return TreeMap.class.getName().replace('$', '.');
                    case ORDERED: return LinkedHashMap.class.getName().replace('$', '.');
                }
            case SET:
                switch (containerType()) {
                    case DEFAULT: return HashSet.class.getName().replace('$', '.');
                    case SORTED: return TreeSet.class.getName().replace('$', '.');
                    case ORDERED: return LinkedHashSet.class.getName().replace('$', '.');
                }
            default:
                return fieldType();
        }
    }

    public String fieldInstanceCopy(String var) throws GeneratorException  {
        switch (field.getType()) {
            case LIST:
                return ImmutableList.class.getName().replace('$', '.') + ".copyOf(" + var + ")";
            case MAP:
                switch (containerType()) {
                    case SORTED: return ImmutableSortedMap.class.getName().replace('$', '.') + ".copyOf(" + var + ")";
                    // ImmutableMap is both both hash-map and order-preserving
                    default: return ImmutableMap.class.getName().replace('$', '.') + ".copyOf(" + var + ")";
                }
            case SET:
                switch (containerType()) {
                    case SORTED: return ImmutableSortedSet.class.getName().replace('$', '.') + ".copyOf(" + var + ")";
                    // ImmutableSet is both both hash-set and order-preserving
                    default: return ImmutableSet.class.getName().replace('$', '.') + ".copyOf(" + var + ")";
                }
        }
        throw new GeneratorException("Unable to instance copy " + field.getType());
    }

    public String builderInstanceType() throws GeneratorException  {
        switch (field.getType()) {
            case MAP:
                switch (containerType()) {
                    case DEFAULT: return PMap.DefaultBuilder.class.getName().replace('$', '.');
                    case SORTED: return PMap.SortedBuilder.class.getName().replace('$', '.');
                    case ORDERED: return PMap.OrderedBuilder.class.getName().replace('$', '.');
                }
            case SET:
                switch (containerType()) {
                    case DEFAULT: return PSet.DefaultBuilder.class.getName().replace('$', '.');
                    case SORTED: return PSet.SortedBuilder.class.getName().replace('$', '.');
                    case ORDERED: return PSet.OrderedBuilder.class.getName().replace('$', '.');
                }
            case LIST:
                return PList.DefaultBuilder.class.getName().replace('$', '.');
            default:
                return fieldType();
        }
    }

    public String getProvider()  throws GeneratorException {
        String container = field.getAnnotationValue("container");
        String containerProvider = "provider";
        if (container != null) {
            ContainerType containerType = ContainerType.valueOf(container.toUpperCase());
            switch (containerType) {
                case DEFAULT:
                    containerProvider = "provider";
                    break;
                case SORTED:
                    containerProvider = "sortedProvider";
                    break;
                case ORDERED:
                    containerProvider = "orderedProvider";
                    break;
                default:
                    break;
            }
        }

        switch (field.getType()) {
            case ENUM:
            case MESSAGE:
                return String.format("%s.provider()", helper.getFieldType(field.getDescriptor()));
            case LIST:
                PList<?> lType = (PList<?>) field.getDescriptor();
                return String.format("%s.provider(%s)", PList.class.getName(), helper.getProviderName(lType.itemDescriptor()));
            case SET:
                PSet<?> sType = (PSet<?>) field.getDescriptor();
                return String.format("%s.%s(%s)",
                                     PSet.class.getName(),
                                     containerProvider,
                                     helper.getProviderName(sType.itemDescriptor()));
            case MAP:
                PMap<?, ?> mType = (PMap<?, ?>) field.getDescriptor();
                return String.format("%s.%s(%s,%s)",
                                     PMap.class.getName(),
                                     containerProvider,
                                     helper.getProviderName(mType.keyDescriptor()),
                                     helper.getProviderName(mType.itemDescriptor()));
            default:
                if (!(field.getDescriptor() instanceof PPrimitive)) {
                    throw new IllegalArgumentException("Unhandled type group " + field.getType());
                }
                return String.format("%s.%s.provider()",
                                     PPrimitive.class.getName(),
                                     field.getDescriptor().getName().toUpperCase());
        }
    }

    public boolean hasComment() {
        return field.getDocumentation() != null;
    }

    public String comment() {
        return field.getDocumentation();
    }

    public boolean isVoid() {
        return field.getType() == PType.VOID;
    }

    public PList toPList() {
        return (PList) (field()
                             .getDescriptor());
    }

    public PSet toPSet() {
        return (PSet) (field()
                .getDescriptor());
    }

    public PMap toPMap() {
        return (PMap) (field()
                .getDescriptor());
    }

    public boolean isUnion() {
        boolean result = false;
        switch ( type() ) {
            case MESSAGE:
                result = ((PMessageDescriptor)field().getDescriptor()).getVariant().equals(PMessageVariant.UNION);
                break;
            case SET:
            case LIST:
                PDescriptor descriptor = extractItemDescriptor(field().getDescriptor());
                if( descriptor.getType().equals(PType.MESSAGE) ) {
                    result = ((PMessageDescriptor)descriptor).getVariant().equals(PMessageVariant.UNION);
                }
                break;
        }
        return result;

    }

    private PDescriptor extractItemDescriptor(PDescriptor descriptor) {
        PDescriptor result;
        switch (descriptor.getType()) {
            case SET:
                result = extractItemDescriptor(((PSet)descriptor).itemDescriptor());
                break;
            case LIST:
                result = extractItemDescriptor(((PList)descriptor).itemDescriptor());
                break;
            default: {
                result = descriptor;
            }
        }
        return result;
    }



}
