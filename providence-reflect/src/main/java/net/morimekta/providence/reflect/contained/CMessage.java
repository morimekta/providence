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

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PRequirement;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

/**
 * @author Stein Eldar Johnsen
 * @since 26.08.15
 */
public interface CMessage<Message extends PMessage<Message, CField>>
        extends PMessage<Message, CField> {
    /**
     * Method to access all values in the message.
     *
     * @return The values map.
     */
    Map<Integer, Object> values();

    @Override
    default boolean has(int key) {
        CField field = descriptor().getField(key);
        return field != null && values().containsKey(key);
    }

    @Override
    default int num(int key) {
        CField field = descriptor().getField(key);
        if (field == null) {
            return 0;
        }

        // Non-present containers are empty.
        if (!values().containsKey(key)) {
            return 0;
        }

        switch (field.getDescriptor().getType()) {
            case MAP:
                return ((Map<?, ?>) values().get(key)).size();
            case LIST:
            case SET:
                return ((Collection<?>) values().get(key)).size();
            default:
                // present non-containers also empty.
                return 0;
        }
    }

    @Override
    default Object get(int key) {
        CField field = descriptor().getField(key);
        if (field != null) {
            Object value = values().get(key);
            if (value != null) {
                return value;
            } else if (field.hasDefaultValue()) {
                return field.getDefaultValue();
            } else if ((field.getDescriptor() instanceof PPrimitive && ((PPrimitive) field.getDescriptor()).isNativePrimitive()) ||
                       (field.getRequirement() != PRequirement.OPTIONAL)) {
                return field.getDescriptor().getDefaultValue();
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    default int compareTo(@Nonnull Message other) {
        return CStruct.compareMessages((Message) this, other);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    default String asString() {
        return CStruct.asString((PMessage) this);
    }
}
