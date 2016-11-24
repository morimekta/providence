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

package net.morimekta.providence.generator.format.java.utils;

import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.reflect.contained.CAnnotatedDescriptor;
import net.morimekta.providence.reflect.util.ThriftAnnotation;

/**
 *
 */
public class JAnnotation {
    public static final String DEPRECATED = "@Deprecated";

    public static boolean isDeprecated(PField field) {
        if (field instanceof CAnnotatedDescriptor) {
            return isDeprecated((CAnnotatedDescriptor) field);
        }
        return false;
    }

    public static boolean isDeprecated(JField field) {
        return isDeprecated(field.getPField());
    }

    public static boolean isDeprecated(CAnnotatedDescriptor value) {
        return value.hasAnnotation(ThriftAnnotation.DEPRECATED);
    }

    public static boolean isDeprecated(PDescriptor descriptor) {
        if (descriptor instanceof CAnnotatedDescriptor) {
            return isDeprecated((CAnnotatedDescriptor) descriptor);
        }
        return false;
    }

    public static ContainerType containerType(PField field) {
        if (field instanceof CAnnotatedDescriptor) {
            return containerType((CAnnotatedDescriptor) field);
        }
        return ContainerType.DEFAULT;
    }

    public static ContainerType containerType(JField field) {
        return containerType(field.getPField());
    }

    public static ContainerType containerType(CAnnotatedDescriptor descriptor) {
        if (descriptor.hasAnnotation(ThriftAnnotation.CONTAINER)) {
            return ContainerType.valueOf(descriptor.getAnnotationValue(ThriftAnnotation.CONTAINER));
        }
        return ContainerType.DEFAULT;
    }
}
