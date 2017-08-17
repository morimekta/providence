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
package net.morimekta.providence.config.core;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.config.utils.ProvidenceConfigException;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;

import java.io.IOException;
import java.io.InputStream;

/**
 * A supplier to get a config (aka message) from a resource location. This is
 * a fixed static supplier, so listening to changes will never do anything.
 *
 * <pre>{@code
 *     Supplier<Service> supplier = new ResourceConfigSupplier<>(resourceName, Service.kDescriptor);
 * }</pre>
 */
public class ResourceConfigSupplier<Message extends PMessage<Message, Field>, Field extends PField>
        extends ConfigSupplier<Message, Field> {
    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix. Note that reading from properties
     * are <b>never</b> strict.
     *
     * @param resourceName The resource name to load.
     * @param descriptor The message type descriptor.
     * @throws ProvidenceConfigException If message overriding failed
     */
    public ResourceConfigSupplier(String resourceName, PMessageDescriptor<Message, Field> descriptor)
            throws ProvidenceConfigException {
        super(loadInternal(resourceName, descriptor));
    }

    @Override
    public void addListener(ConfigListener<Message, Field> listener) {
        // ignore, this never changes.
    }

    @Override
    public void removeListener(ConfigListener<Message, Field> listener) {
        // ignore, this never changes.
    }

    private static <Message extends PMessage<Message, Field>, Field extends PField>
    Message loadInternal(String resourceName, PMessageDescriptor<Message, Field> descriptor) throws ProvidenceConfigException {
        int lastDot = resourceName.lastIndexOf(".");
        String suffix = resourceName.substring(lastDot + 1).toLowerCase();
        Serializer serializer;
        switch (suffix) {
            case ".json":
                serializer = new JsonSerializer();
                break;
            case ".cnf":
            case ".config":
            case ".pvd":
            case ".providence":
                serializer = new PrettySerializer().config();
                break;
            // TODO: Add YAML serializer to the file options. Could be a wrapper around SnakeYAML.
            default:
                throw new ProvidenceConfigException("");
        }
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream in = classLoader.getResourceAsStream(resourceName);
        if (in == null) {
            in = ResourceConfigSupplier.class.getResourceAsStream(resourceName);
            if (in == null) {
                throw new ProvidenceConfigException("No such config resource: " + resourceName);
            }
        }

        try {
            return serializer.deserialize(in, descriptor);
        } catch (SerializerException se) {
            throw new ProvidenceConfigException(se);
        } catch (IOException e) {
            throw new ProvidenceConfigException(e, "Unknown serializer exception: " + e.getMessage());
        }
    }
}