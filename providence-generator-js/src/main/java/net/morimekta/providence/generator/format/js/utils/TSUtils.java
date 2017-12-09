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

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.generator.format.js.JSOptions;

import javax.annotation.Nonnull;

import static net.morimekta.providence.generator.format.js.utils.JSUtils.getClassName;

/**
 * General utilities for js generator.
 */
public class TSUtils {
    public static String getTypeReference(@Nonnull String programContext,
                                          @Nonnull PDeclaredDescriptor descriptor) {
        if (programContext.equals(descriptor.getProgramName())) {
            return getClassName(descriptor);
        } else {
            return "_" + descriptor.getProgramName() + "." + getClassName(descriptor);
        }
    }

    public static String getTypeString(@Nonnull String programContext,
                                       @Nonnull PDescriptor descriptor,
                                       JSOptions options) {
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
            case MESSAGE:
                return getTypeReference(programContext, (PDeclaredDescriptor) descriptor);
            case LIST:
            case SET:
                PContainer container = (PContainer) descriptor;
                return getTypeString(programContext, container.itemDescriptor(), options) + "[]";
            case MAP:
                PMap map = (PMap) descriptor;
                String keyDesc = getTypeString(programContext, map.keyDescriptor(), options);
                String valueDesc = getTypeString(programContext, map.itemDescriptor(), options);
                if (map.keyDescriptor().getType() == PType.MESSAGE) {
                    // TODO: Make better workaround!
                    // Messages use the compact JSON string version for the key.
                    // javascript does not support objects as keys, as all object instances
                    // are non-equal.
                    keyDesc = "string";
                }

                if (options.useMaps()) {
                    return "Map<" + keyDesc + "," + valueDesc + ">";
                }

                if (map.keyDescriptor().getType() == PType.BOOL) {
                    // translates to 0 and 1.
                    keyDesc = "number";
                } else if (map.keyDescriptor().getType() == PType.ENUM) {
                    // translates to enum value.
                    keyDesc = "number";
                }
                return "{[key:" + keyDesc + "]:" + valueDesc + "}";
            default:
                throw new IllegalArgumentException("Unhandled type: " + descriptor.getType());
        }
    }
}
