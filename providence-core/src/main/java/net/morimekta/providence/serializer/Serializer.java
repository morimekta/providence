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
package net.morimekta.providence.serializer;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PService;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Thrift serializers are stateless injectable implementation classes that
 * transforms messages to binary stream (serializes), or binary stream to
 * messages (deserializes). Since the serializer is state-less it should also
 * be inherently thread safe (including not needing any synchronized methods.
 */
public abstract class Serializer {
    public abstract <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(OutputStream output, Message message) throws IOException;

    public abstract <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(OutputStream output, PServiceCall<Message, Field> call) throws IOException;

    @Nonnull
    public abstract <Message extends PMessage<Message, Field>, Field extends PField>
    Message deserialize(InputStream input, PMessageDescriptor<Message, Field> descriptor) throws IOException;

    @Nonnull
    public abstract <Message extends PMessage<Message, Field>, Field extends PField>
    PServiceCall<Message, Field> deserialize(InputStream input, PService service) throws IOException;

    public abstract boolean binaryProtocol();

    public abstract String mimeType();

    /**
     * Utility method to check if a service call is a request type.
     *
     * @param type The service call type.
     * @return If the service call is a request, and not a response.
     */
    protected boolean isRequestCallType(PServiceCallType type) {
        switch (type) {
            case CALL:
            case ONEWAY:
                return true;
        }
        return false;
    }

    public static final boolean DEFAULT_STRICT = false;
}
