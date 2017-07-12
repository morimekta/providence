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
import net.morimekta.providence.descriptor.PExceptionDescriptor;
import net.morimekta.providence.descriptor.PExceptionDescriptorProvider;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptorProvider;
import net.morimekta.providence.descriptor.PUnionDescriptor;
import net.morimekta.providence.descriptor.PUnionDescriptorProvider;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CMessage;
import net.morimekta.providence.util.ThriftAnnotation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static net.morimekta.providence.reflect.contained.CStructDescriptor.MAX_COMPACT_FIELDS;

/**
 *
 */
public class JMessage<T extends CMessage<T>> {
    private final PMessageDescriptor<T, CField> descriptor;
    private final ArrayList<JField>        declaredFields;
    private final ArrayList<JField>        numericalFields;

    public JMessage(PMessageDescriptor<T, CField> descriptor, JHelper helper) {
        this.descriptor = descriptor;
        this.declaredFields = new ArrayList<>(descriptor.getFields().length);

        CField[] fields = descriptor.getFields();
        for (int i = 0; i < fields.length; ++i) {
            this.declaredFields.add(new JField(fields[i], helper, i));
        }
        // The same declaredFields, but in ID numerical order.
        this.numericalFields = new ArrayList<>(this.declaredFields);
        this.numericalFields.sort(Comparator.comparingInt(JField::id));
    }

    public PMessageDescriptor<T, CField> descriptor() {
        return descriptor;
    }

    public PMessageVariant variant() {
        return descriptor.getVariant();
    }

    public boolean isException() {
        return descriptor.getVariant() == PMessageVariant.EXCEPTION;
    }

    public boolean isUnion() {
        return descriptor.getVariant() == PMessageVariant.UNION;
    }

    public boolean jsonCompactible() {
        // note: legacy annotation name.
        if (descriptor().getVariant() != PMessageVariant.STRUCT) {
            return false;
        }
        if (!(hasAnnotation(ThriftAnnotation.JSON_COMPACT) ||
              // legacy annotation version special handling.
              hasAnnotation("compact"))) {
            return false;
        }

        if (declaredFields.size() > MAX_COMPACT_FIELDS) {
            return false;
        }
        int next = 1;
        boolean hasOptional = false;
        for (JField field : declaredFields) {
            if (field.id() != next) {
                return false;
            }
            if (hasOptional && field.isRequired()) {
                return false;
            }
            if (!field.isRequired()) {
                hasOptional = true;
            }
            next++;
        }
        return true;

    }

    /**
     * The short class name of the message.
     *
     * @return The class short name.
     */
    public String instanceType() {
        return JUtils.getClassName(descriptor);
    }

    public List<JField> declaredOrderFields() {
        return declaredFields;
    }

    public List<JField> numericalOrderFields() {
        return numericalFields;
    }

    public String getDescriptorClass() throws GeneratorException {
        switch (variant()) {
            case STRUCT:
                return PStructDescriptor.class.getName();
            case UNION:
                return PUnionDescriptor.class.getName();
            case EXCEPTION:
                return PExceptionDescriptor.class.getName();
            default:
                throw new GeneratorException("Unable to determine type class for " + variant());
        }
    }

    public String getProviderClass() throws GeneratorException {
        switch (variant()) {
            case STRUCT:
                return PStructDescriptorProvider.class.getName();
            case UNION:
                return PUnionDescriptorProvider.class.getName();
            case EXCEPTION:
                return PExceptionDescriptorProvider.class.getName();
            default:
                throw new GeneratorException("Unable to determine type class for " + variant());
        }
    }

    public String extraImplements() {
        return getAnnotationValue(ThriftAnnotation.JAVA_IMPLEMENTS);
    }

    public String exceptionBaseClass() {
        if (!isException()) {
            return null;
        }
        if (hasAnnotation(ThriftAnnotation.JAVA_EXCEPTION_CLASS)) {
            return getAnnotationValue(ThriftAnnotation.JAVA_EXCEPTION_CLASS);
        }
        return Exception.class.getSimpleName();
    }

    /**
     * If the message has a 'string' field named 'message', which can be used as the 'message' for exceptions.
     *
     * @return Optional message field.
     */
    public Optional<JField> exceptionMessageField() {
        return numericalOrderFields().stream().filter(f -> f.name().equals("message") && f.type() == PType.STRING).findFirst();
    }

    /**
     * Check if the annotation is present.
     *
     * @param annotation The annotation to check.
     * @return True if the annotaiton is present.
     */
    public boolean hasAnnotation(ThriftAnnotation annotation) {
        return descriptor instanceof CAnnotatedDescriptor &&
               ((CAnnotatedDescriptor) descriptor).hasAnnotation(annotation);
    }

    /**
     * Check if the annotation is present.
     *
     * @param annotation The annotation to check.
     * @return True if the annotaiton is present.
     */
    public boolean hasAnnotation(String annotation) {
        return descriptor instanceof CAnnotatedDescriptor &&
               ((CAnnotatedDescriptor) descriptor).hasAnnotation(annotation);
    }

    /**
     * Get the annotation value.
     * @param annotation The annotation to get.
     * @return The value of the annotation, or null if not present.
     */
    private String getAnnotationValue(ThriftAnnotation annotation) {
        return getAnnotationValue(annotation.tag);
    }

    /**
     * Get the annotation value.
     * @param annotation The annotation to get.
     * @return The value of the annotation, or null if not present.
     */
    private String getAnnotationValue(String annotation) {
        if (descriptor instanceof CAnnotatedDescriptor) {
            if (((CAnnotatedDescriptor) descriptor).hasAnnotation(annotation)) {
                return ((CAnnotatedDescriptor) descriptor).getAnnotationValue(annotation);
            }
        }
        return null;
    }
}
