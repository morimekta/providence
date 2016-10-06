/*
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
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.descriptor.PStructDescriptor;

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
    int serialize(OutputStream output, Message message) throws IOException, SerializerException;

    public abstract <Message extends PMessage<Message, Field>, Field extends PField>
    int serialize(OutputStream output, PServiceCall<Message, Field> call) throws IOException, SerializerException;

    public abstract <Message extends PMessage<Message, Field>, Field extends PField>
    Message deserialize(InputStream input, PStructDescriptor<Message, Field> descriptor) throws IOException, SerializerException;

    public abstract <Message extends PMessage<Message, Field>, Field extends PField>
    PServiceCall<Message, Field> deserialize(InputStream input, PService service) throws SerializerException, IOException;

    public abstract boolean binaryProtocol();

    public abstract String mimeType();
}
