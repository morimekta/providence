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
package net.morimekta.providence.util;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.streams.MessageStreams;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Convenience methods for handling providence messages.
 */
public class ProvidenceHelper {
    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Message fromJsonResource(String path,
                             PMessageDescriptor<Message, Field> descriptor)
            throws IOException {
        return fromResource(path, descriptor, new JsonSerializer(true));
    }

    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    ArrayList<Message> arrayListFromJsonResource(String path,
                                                 PMessageDescriptor<Message, Field> descriptor)
            throws IOException {
        return arrayListFromResource(path, descriptor, new JsonSerializer(true));
    }

    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Message fromResource(String resource,
                         PMessageDescriptor<Message, Field> descriptor,
                         Serializer serializer)
            throws IOException {
        InputStream in = ProvidenceHelper.class.getResourceAsStream(resource);
        if(in == null) {
            throw new IOException("No such resource " + resource);
        }
        return serializer.deserialize(new BufferedInputStream(in), descriptor);
    }

    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    ArrayList<Message> arrayListFromResource(String path,
                                             PMessageDescriptor<Message, Field> descriptor,
                                             Serializer serializer)
            throws IOException {
        return MessageStreams.resource(path, serializer, descriptor)
                             .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Prints a pretty formatted string that is optimized for diffing (mainly
     * for testing and debugging).
     *
     * @param message The message to stringify.
     * @param <Message> The message type.
     * @param <Field> The message field type.
     * @return The resulting string.
     */
    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    String debugString(Message message) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DEBUG_STRING_SERIALIZER.serialize(baos, message);
        return new String(baos.toByteArray(), UTF_8);
    }

    /**
     * Parses a pretty formatted string, and makes exceptions unchecked.
     *
     * @param string The message string to parse.
     * @param descriptor The message descriptor.
     * @param <Message> The message type.
     * @param <Field> The message field type.
     * @return The parsed message.
     */
    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Message parseDebugString(String string, PMessageDescriptor<Message, Field> descriptor) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes(UTF_8));
            return DEBUG_STRING_SERIALIZER.deserialize(bais, descriptor);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    private static final PrettySerializer DEBUG_STRING_SERIALIZER = new PrettySerializer().debug();

    private ProvidenceHelper() {}
}
