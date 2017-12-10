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
import java.time.Clock;
import java.util.Objects;

/**
 * A supplier and instance holder for an immutable config instance.
 */
public class FixedConfigSupplier<M extends PMessage<M,F>, F extends PField> implements ConfigSupplier<M,F> {
    private final M instance;
    private final long timestamp;

    /**
     * Initialize with an initial config instance.
     *
     * @param initialConfig The initial config instance.
     */
    public FixedConfigSupplier(@Nonnull M initialConfig) {
        this(initialConfig, Clock.systemUTC().millis());
    }

    /**
     * This essentially makes a static snapshot of the config and keeps the
     * config instance as a fixed (unmodifiable) config.
     *
     * @param supplier The config supplier to copy.
     */
    public FixedConfigSupplier(@Nonnull ConfigSupplier<M, F> supplier) {
        synchronized (Objects.requireNonNull(supplier)) {
            this.instance = supplier.get();
            this.timestamp = supplier.configTimestamp();
        }
    }

    /**
     * Initialize with an initial config instance.
     *
     * @param initialConfig The initial config instance.
     * @param timestamp The config timestamp.
     */
    public FixedConfigSupplier(@Nonnull M initialConfig, long timestamp) {
        this.instance = initialConfig;
        this.timestamp = timestamp;
    }

    @Nonnull
    @Override
    public final M get() {
        return instance;
    }

    @Override
    public final void addListener(@Nonnull ConfigListener<M, F> listener) {
    }

    @Override
    public final void removeListener(@Nonnull ConfigListener<M,F> listener) {
    }

    @Override
    public long configTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return "InMemoryConfig";
    }
}
