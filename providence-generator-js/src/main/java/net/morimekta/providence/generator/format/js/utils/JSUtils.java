/*
 * Copyright 2017 Providence Authors
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
package net.morimekta.providence.generator.format.js.utils;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PEnumDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.reflect.contained.CField;
import net.morimekta.providence.reflect.contained.CMessageDescriptor;
import net.morimekta.providence.reflect.contained.CProgram;
import net.morimekta.providence.serializer.json.JsonCompactibleDescriptor;
import net.morimekta.util.Strings;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * General utilities for js generator.
 */
public class JSUtils {
    public static String getClassName(@Nonnull CMessageDescriptor descriptor) {
        return getClassName((PDeclaredDescriptor) descriptor);
    }

    public static String getClassName(@Nonnull PDeclaredDescriptor type) {
        return Strings.camelCase("", type.getName());
    }

    public static String getFieldOptionality(@Nonnull CField field) {
        if (!alwaysPresent(field)) {
            return "?";
        }
        return "";
    }

    public static String getFieldType(@Nonnull CField field) {
        return getDescriptorType(field.getDescriptor()) + getFieldOptionality(field);
    }

    public static String getDescriptorType(@Nonnull PDescriptor descriptor) {
        switch (descriptor.getType()) {
            case VOID:
            case BOOL:
                return "boolean";
            case BYTE:
            case I16:
            case I32:
            case I64:
            case DOUBLE:
                return "number";
            case BINARY:
            case STRING:
                return "string";
            case ENUM:
                return descriptor.getProgramName() + "." + getClassName((PEnumDescriptor) descriptor);
            case MESSAGE:
                return descriptor.getProgramName() + "." + getClassName((PDeclaredDescriptor) descriptor);
            case LIST:
            case SET:
                PContainer container = (PContainer) descriptor;
                return "Array<" + getDescriptorType(container.itemDescriptor()) + ">";
            case MAP:
                PMap map = (PMap) descriptor;
                String keyDesc = getDescriptorType(map.keyDescriptor());
                if (map.keyDescriptor().getType() == PType.MESSAGE) {
                    // TODO: Make better workaround!
                    // Messages use the compact JSON string version for the key.
                    // es51 does not support objects as keys, as all object instances
                    // ar non-equal.
                    keyDesc = "string";
                }
                return "Map<" + keyDesc + "," + getDescriptorType(map.itemDescriptor()) + ">";
            default:
                throw new IllegalArgumentException("Unhandled type: " + descriptor.getType());
        }
    }

    public static boolean alwaysPresent(CField field) {
        return field.getRequirement() != PRequirement.OPTIONAL &&
               defaultValue(field) != null;
    }

    public static String getPackage(@Nonnull CProgram document) throws GeneratorException {
        String javaPackage = document.getNamespaceForLanguage("js");
        if (javaPackage == null) {
            return document.getProgramName();
        }
        return javaPackage + "." + document.getProgramName();
    }

    public static String getPackageClassPath(@Nonnull CProgram document) throws GeneratorException {
        String javaPackage = document.getNamespaceForLanguage("js");
        if (javaPackage == null) {
            return document.getProgramName();
        }
        return Strings.join(File.separator, (Object[]) javaPackage.split("[.]"));
    }

    public static String enumConst(PEnumValue value) {
        return Strings.c_case("", value.asString()).toUpperCase();
    }

    public static boolean jsonCompactible(CMessageDescriptor descriptor) {
        return descriptor instanceof JsonCompactibleDescriptor &&
               ((JsonCompactibleDescriptor) descriptor).isJsonCompactible();
    }

    public static Object defaultValue(CField field) {
        if (field.getDefaultValue() != null) {
            return field.getDefaultValue();
        }
        return field.getDescriptor().getDefaultValue();
    }
}
