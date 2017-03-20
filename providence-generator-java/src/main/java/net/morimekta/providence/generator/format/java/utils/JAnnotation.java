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
package net.morimekta.providence.generator.format.java.utils;

import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.util.ThriftAnnotation;

/**
 *
 */
public class JAnnotation {
    public static final String DEPRECATED = "@Deprecated";

    public static boolean isDeprecated(JField field) {
        return isDeprecated(field.field());
    }

    public static boolean isDeprecated(CAnnotatedDescriptor value) {
        return value.hasAnnotation(ThriftAnnotation.DEPRECATED);
    }

    public static boolean isDeprecated(PDescriptor descriptor) {
        return descriptor instanceof CAnnotatedDescriptor && isDeprecated((CAnnotatedDescriptor) descriptor);
    }

    static ContainerType containerType(CField field) {
        return containerType((CAnnotatedDescriptor) field);
    }

    static ContainerType containerType(JField field) {
        return containerType((CAnnotatedDescriptor) field.field());
    }

    private static ContainerType containerType(CAnnotatedDescriptor descriptor) {
        if (descriptor.hasAnnotation(ThriftAnnotation.CONTAINER)) {
            return ContainerType.valueOf(descriptor.getAnnotationValue(ThriftAnnotation.CONTAINER));
        }
        return ContainerType.DEFAULT;
    }
}
