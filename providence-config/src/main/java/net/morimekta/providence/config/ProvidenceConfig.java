/*
 * Copyright 2016,2017 Providence Authors
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
import net.morimekta.providence.config.impl.ProvidenceConfigParser;
import net.morimekta.providence.config.impl.ProvidenceConfigSupplier;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.util.TypeRegistry;
import net.morimekta.util.FileWatcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.morimekta.providence.config.impl.ProvidenceConfigUtil.canonicalFileLocation;

/**
 * Providence config loader. This loads providence configs.
 */
public class ProvidenceConfig implements ConfigResolver {
    /**
     * Make a non-strict config instance.
     *
     * @param registry The type registry used to find message and enum types.
     */
    public ProvidenceConfig(TypeRegistry registry) {
        this(registry, new FileWatcher(), false);
    }

    /**
     * Make a config instance.
     *
     * @param registry The type registry used to find message and enum types.
     * @param watcher File watcher used to detect config file updates.
     * @param strict If the config should be parsed strictly.
     */
    public ProvidenceConfig(@Nonnull TypeRegistry registry,
                            @Nullable FileWatcher watcher, boolean strict) {
        this(registry, watcher, strict, Clock.systemUTC());
    }

    /**
     * Make a config instance.
     *
     * @param registry The type registry used to find message and enum types.
     * @param watcher File watcher used to detect config file updates.
     * @param strict If the config should be parsed strictly.
     * @param clock The clock to use in timing config loads.
     */
    public ProvidenceConfig(@Nonnull TypeRegistry registry,
                            @Nullable FileWatcher watcher,
                            boolean strict,
                            @Nonnull Clock clock) {
        this.loaded = new ConcurrentHashMap<>();
        this.watcher = watcher;
        this.parser = new ProvidenceConfigParser(registry, strict);
        this.clock = clock;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <M extends PMessage<M, F>, F extends PField>
    ConfigSupplier<M, F> resolveConfig(@Nonnull File configFile,
                                       @Nullable ConfigSupplier<M, F> parentConfig)
            throws ProvidenceConfigException {
        Path configPath = canonicalFileLocation(configFile.toPath());

        String path = null;
        if (parentConfig == null) {
            path = configPath.toString();
            if (loaded.containsKey(path)) {
                return (ConfigSupplier<M, F>) loaded.get(path);
            }
        }
        ProvidenceConfigSupplier<M, F> supplier = new ProvidenceConfigSupplier<>(
                configFile, parentConfig, watcher, parser, clock);
        if (parentConfig == null) {
            loaded.put(path, supplier);
        }

        return supplier;
    }

    /**
     * Get config for the given file.
     *
     * @param configFile The file to read config for.
     * @param <M> The config message type.
     * @param <F> The config message field type.
     * @return The config message.
     * @throws ProvidenceConfigException On config load failure.
     */
    @Nonnull
    public <M extends PMessage<M, F>, F extends PField>
    M getConfig(@Nonnull File configFile) throws ProvidenceConfigException {
        ConfigSupplier<M,F> supplier = resolveConfig(configFile);
        return supplier.get();
    }

    /**
     * Get config for the given with parent.
     *
     * @param configFile The file to read config for.
     * @param parent The designated parent config.
     * @param <M> The config message type.
     * @param <F> The config message field type.
     * @return The config message.
     * @throws ProvidenceConfigException On config load failure.
     */
    @Nonnull
    public <M extends PMessage<M, F>, F extends PField>
    M getConfig(@Nonnull File configFile, @Nonnull M parent) throws ProvidenceConfigException {
        ConfigSupplier<M,F> supplier = resolveConfig(configFile, new FixedConfigSupplier<>(parent));
        return supplier.get();
    }

    private final Map<String, ConfigSupplier> loaded;
    private final ProvidenceConfigParser      parser;
    private final FileWatcher                 watcher;
    private final Clock                       clock;
}
