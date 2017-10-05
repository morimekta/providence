/*
 * Copyright 2015-2016 Providence Authors
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
package net.morimekta.providence.mio;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PService;

import java.io.Closeable;
import java.io.IOException;

/**
 * An interface for reading messages and service calls.
 */
public interface MessageReader extends Closeable {
    /**
     * Read a message from the stream.
     *
     * @param descriptor The descriptor for the message to be read.
     * @param <Message> The message type.
     * @param <Field> The message field type.
     * @return The message read.
     * @throws IOException If the message could not be read.
     */
    <Message extends PMessage<Message, Field>, Field extends PField>
    Message read(PMessageDescriptor<Message, Field> descriptor)
            throws IOException;

    /**
     * Read a service call from the stream.
     *
     * @param service The service whose call should be read.
     * @param <Message> The type of the contained params or response message.
     * @param <Field> The field type of the contained params or response message.
     * @return The service call read.
     * @throws IOException If the service call could not be read.
     */
    <Message extends PMessage<Message, Field>, Field extends PField>
    PServiceCall<Message, Field> read(PService service)
            throws IOException;
}
