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
package net.morimekta.providence.config.impl;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.config.ConfigListener;
import net.morimekta.providence.config.ConfigSupplier;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.time.Clock;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A supplier and instance holder for config objects. This supplier can be
 * listened to for changes in the config object. When something triggers
 * a change (<code>supplier.set(config)</code>) that will cause a config
 * change call to each listener regardless of if the config values actually
 * did change.
 */
public abstract class UpdatingConfigSupplier<M extends PMessage<M,F>, F extends PField> implements ConfigSupplier<M,F> {
    private final AtomicReference<M>                             instance;
    private final ArrayList<WeakReference<ConfigListener<M, F>>> listeners;
    private final Clock                                          clock;
    private final AtomicLong                                     lastUpdateTimestamp;

    /**
     * Initialize supplier with empty config.
     *
     * @param clock The clock to use in timing config loads.
     */
    protected UpdatingConfigSupplier(@Nonnull Clock clock) {
        this.instance = new AtomicReference<>();
        this.listeners = new ArrayList<>();
        this.clock = clock;
        this.lastUpdateTimestamp = new AtomicLong(0L);
    }

    @Nonnull
    @Override
    public final M get() {
        M config = instance.get();
        if (config == null) {
            throw new IllegalStateException("No config instance");
        }
        return config;
    }

    @Override
    public void addListener(@Nonnull ConfigListener<M, F> listener) {
        synchronized (this) {
            listeners.removeIf(ref -> ref.get() == listener || ref.get() == null);
            listeners.add(new WeakReference<>(listener));
        }
    }

    @Override
    public void removeListener(@Nonnull ConfigListener<M,F> listener) {
        synchronized (this) {
            listeners.removeIf(ref -> ref.get() == null || ref.get() == listener);
        }
    }

    @Override
    public long configTimestamp() {
        return lastUpdateTimestamp.get();
    }

    /**
     * Set a new config value to the supplier. This is protected as it is
     * usually up to the supplier implementation to enable updating the
     * config at later stages.
     *
     * @param config The new config instance.
     */
    protected final void set(M config) {
        ArrayList<WeakReference<ConfigListener<M,F>>> iterateOver;
        synchronized (this) {
            M old = instance.get();
            if (old == config || (old != null && old.equals(config))) {
                return;
            }

            instance.set(config);
            lastUpdateTimestamp.set(clock.millis());
            listeners.removeIf(ref -> ref.get() == null);
            iterateOver = new ArrayList<>(listeners);
        }
        iterateOver.forEach(ref -> {
            ConfigListener<M,F> listener = ref.get();
            if (listener != null) {
                try {
                    listener.onConfigChange(config);
                } catch (Exception ignore) {
                    // Ignored... TODO: At least log?
                }
            }
        });
    }
}
