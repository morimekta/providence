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
package net.morimekta.providence.config;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.serializer.SerializerException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A supplier of a providence message config based on a message reader. For reading a simple
 * message from a readable file, use the FileMessageReader with the PrettySerializer:
 *
 * NOTE: The message reader will be closed after every read, so only message readers that
 * can be reset that way can be supported, e.g. {@link net.morimekta.providence.mio.FileMessageReader}.
 *
 * <code>
 *     MessageConfigSupplier supplier = new MessageConfigSupplier(
 *             prefix,
 *             MyConfig.kDescriptor,
 *             new FileMessageReader(configFile, new PrettySerializer()));
 * </code>
 */
public class MessageConfigSupplier<Message extends PMessage<Message, Field>, Field extends PField>
        implements ReloadableSupplier<MessageConfig<Message, Field>> {
    /**
     * Create a config that wraps a providence message instance. This message
     * will be exposed without any key prefix.
     *
     * @param prefix The config prefix.
     * @param descriptor The message descriptor of the config root message.
     * @param reader The message reader containing the config.
     * @throws IOException If message read failed.
     * @throws SerializerException If message deserialization failed.
     */
    public MessageConfigSupplier(String prefix, PMessageDescriptor<Message, Field> descriptor, MessageReader reader)
            throws IOException {
        this.prefix = prefix;
        this.descriptor = descriptor;
        this.reader = reader;

        this.instance = new AtomicReference<>(loadInternal());
    }

    /**
     * Get the key prefix used in the config wrapper. All keys in the message
     * structure is prefixed by this value.
     *
     * @return The key prefix.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Get the message enclosed in the config wrapper.
     *
     * @return The message.
     */
    @Override
    public MessageConfig<Message, Field> get() {
        return instance.get();
    }

    /**
     * Reload the message into the config.
     */
    @Override
    public void reload() {
        try {
            instance.set(loadInternal());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private MessageConfig<Message, Field> loadInternal() throws IOException {
        try {
            return new MessageConfig<>(prefix, reader.read(descriptor));
        } finally {
            reader.close();
        }
    }

    private final String                                         prefix;
    private final AtomicReference<MessageConfig<Message, Field>> instance;
    private final PMessageDescriptor<Message, Field>             descriptor;
    private final MessageReader                                  reader;
}
