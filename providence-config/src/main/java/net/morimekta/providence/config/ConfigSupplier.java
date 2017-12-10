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
package net.morimekta.providence.config;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * A supplier and instance holder for config objects. This supplier can be
 * listened to for changes in the config object. When something triggers
 * a change (<code>supplier.set(config)</code>) that will cause a config
 * change call to each listener regardless of if the config values actually
 * did change.
 */
public interface ConfigSupplier<M extends PMessage<M,F>, F extends PField> extends Supplier<M> {
    /**
     * Add a listener to changes to this config. Note that this will store a
     * weak reference to the listener instance, so the one adding the listener
     * must make sure the listener is not GC'd.
     *
     * @param listener The config change listener to be added.
     */
    void addListener(@Nonnull ConfigListener<M, F> listener);

    /**
     * Remove a config change listener.
     *
     * @param listener The config change listener to be removed.
     */
    void removeListener(@Nonnull ConfigListener<M,F> listener);

    /**
     * Get a simple descriptive name for this config supplier.
     *
     * @return The supplier name.
     */
    String getName();

    /**
     * Get the last update time as a millisecond timestamp.
     *
     * @return The timestamp of last update of the config.
     */
    long configTimestamp();

    /**
     * Get a snapshot of the current config.
     *
     * @return Non-modifiable supplier of current config containing a snapshot.
     */
    default ConfigSupplier<M,F> snapshot() {
        return new FixedConfigSupplier<>(this);
    }
}
