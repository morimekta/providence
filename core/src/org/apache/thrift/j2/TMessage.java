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

package org.apache.thrift.j2;

import org.apache.thrift.j2.descriptor.TStructDescriptor;

/**
 * Base class for all messages.
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public interface TMessage<T extends TMessage<T>>
        extends TValue<T> {
    /**
     * @param key The key of the field.
     * @return Whether the field is present.
     */
    boolean has(int key);

    /**
     * @param key The key of the field.
     * @return Number of values for the field.
     */
    int num(int key);

    /**
     * @param key The key of the field.
     * @return The value of the field.
     */
    Object get(int key);

    /**
     * Get a builder that extends the current object.
     *
     * @return The builder instance.
     */
    TMessageBuilder<T> mutate();

    /**
     * @return True if the message is valid.
     */
    boolean isValid();

    /**
     * @return If the message is compact.
     */
    boolean isCompact();

    @Override TStructDescriptor<T> getDescriptor();
}
