/*
 * Copyright (c) 2016, Stein Eldar Johnsen
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

import net.morimekta.config.IncompatibleValueException;
import net.morimekta.config.KeyNotFoundException;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.util.TypeRegistry;

import java.util.HashMap;
import java.util.Map;

import static net.morimekta.providence.config.ProvidenceConfigUtil.getInMessage;

/**
 * Providence enhanced config interface.
 */
public class ProvidenceConfig {
    private final TypeRegistry                  registry;
    private final Map<String, ProvidenceConfig> includes;
    private final Map<String, PMessage>         entries;
    private final Map<String, Object>           params;

    public ProvidenceConfig(TypeRegistry registry) {
        this.registry = registry;
        this.includes = new HashMap<>();
        this.entries = new HashMap<>();
        this.params = new HashMap<>();
    }

    /**
     * Get value from the config.
     *
     * @param key The key to look for.
     * @return The value at the given key, or exception if not found.
     */
    @SuppressWarnings("unchecked")
    public <V> V get(String key) {
        try {
            if (key.contains(".")) {
                int idx = key.indexOf(".");
                String name = key.substring(0, idx);
                String sub = key.substring(idx + 1);
                if (entries.containsKey(name)) {
                    return (V) getInMessage(entries.get(name), sub);
                } else if (includes.containsKey(name)) {
                    return includes.get(name)
                                   .get(sub);
                }
                throw new IllegalArgumentException("Name " + name + " not available in config: " + key);
            } else if (entries.containsKey(key)) {
                return (V) entries.get(key);
            }
            throw new IllegalArgumentException("Name " + key + " not available in config.");
        } catch (KeyNotFoundException e) {
            throw new KeyNotFoundException(e.getMessage() + ": " + key);
        } catch (IncompatibleValueException e) {
            throw new IncompatibleValueException(e.getMessage() + ": " + key);
        }
    }
}
