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

import javax.annotation.Nonnull;
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
        CField field = descriptor().findFieldById(key);
        return field != null && values().containsKey(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <T> T get(int key) {
        CField field = descriptor().findFieldById(key);
        if (field != null) {
            return (T) values().get(key);
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
