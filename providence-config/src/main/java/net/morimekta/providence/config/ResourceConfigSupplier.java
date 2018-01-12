/*
 * Copyright 2016-2017 Providence Authors
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
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.pretty.TokenizerException;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;

/**
 * A supplier to get a config (aka message) from a resource location. This is
 * a fixed static supplier, so listening to changes will never do anything.
 *
 * <pre>
 *     ConfigSupplier&lt;Service, Service._Field&gt; supplier =
 *             new ResourceConfigSupplier&lt;&gt;(resourceName, Service.kDescriptor);
 * </pre>
 */
public class ResourceConfigSupplier<Message extends PMessage<Message, Field>, Field extends PField>
        extends FixedConfigSupplier<Message, Field> {
    private final String resourceName;

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
        this(resourceName, descriptor, Clock.systemUTC());
    }

    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix. Note that reading from properties
     * are <b>never</b> strict.
     *
     * @param resourceName The resource name to load.
     * @param descriptor The message type descriptor.
     * @param clock The clock to use in timing config loads.
     * @throws ProvidenceConfigException If message overriding failed
     */
    public ResourceConfigSupplier(String resourceName, PMessageDescriptor<Message, Field> descriptor, Clock clock)
            throws ProvidenceConfigException {
        super(loadInternal(resourceName, descriptor), clock.millis());
        this.resourceName = resourceName;
    }

    @Override
    public String getName() {
        return "ResourceConfig{" + resourceName + "}";
    }

    private static <Message extends PMessage<Message, Field>, Field extends PField>
    Message loadInternal(String resourceName, PMessageDescriptor<Message, Field> descriptor) throws ProvidenceConfigException {
        int lastDot = resourceName.lastIndexOf(".");
        if (lastDot < 1) {
            throw new ProvidenceConfigException("No file ending, or no resource file name: " + resourceName);
        }
        int lastSlash = resourceName.lastIndexOf("/");
        String fileName = resourceName;
        if (lastSlash >= 0) {
            fileName = resourceName.substring(lastSlash + 1);
        }
        try {
            String suffix = resourceName.substring(lastDot)
                                        .toLowerCase();
            Serializer serializer;
            switch (suffix) {
                case ".json":
                    serializer = new JsonSerializer();
                    break;
                case ".cfg":
                case ".cnf":
                case ".config":
                case ".pvd":
                case ".providence":
                    serializer = new PrettySerializer().config();
                    break;
                // TODO: Add YAML serializer to the file options. Could be a wrapper around SnakeYAML.
                default:
                    throw new ProvidenceConfigException(String.format("Unrecognized resource config type: %s (%s)",
                                                                      suffix,
                                                                      resourceName));
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
            } catch (TokenizerException te) {
                throw new ProvidenceConfigException(te);
            } catch (SerializerException se) {
                throw new ProvidenceConfigException(se);
            } catch (IOException e) {
                throw  new ProvidenceConfigException(e, "Unknown serializer exception: " + e.getMessage());
            }
        } catch (ProvidenceConfigException pce) {
            pce.setFile(fileName);
            throw pce;
        }
    }
}
