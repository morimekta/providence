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
package net.morimekta.providence.streams;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.Serializer;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Helper class to create streams that read providence messages.
 */
public class MessageStreams {
    public static final byte[] READABLE_ENTRY_SEP  = new byte[]{'\n'};

    /**
     * Read a file containing entries of a given type. Tries to detect the
     * entry format of the file based on file magic. If not detected will try
     * to use the default binary serializer format.
     *
     * @param file       The file to read.
     * @param serializer The serializer to use.
     * @param descriptor The descriptor of the entry type of the file.
     * @param <Message>  The message type.
     * @param <Field>    The message field type.
     * @return The stream that reads the file.
     * @throws IOException when unable to open the stream.
     */
    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Stream<Message> path(Path file,
                         Serializer serializer,
                         PMessageDescriptor<Message, Field> descriptor)
            throws IOException {
        return file(file.toFile(), serializer, descriptor);
    }


    /**
     * Read a file containing entries of a given type. Tries to detect the
     * entry format of the file based on file magic. If not detected will try
     * to use the default binary serializer format.
     *
     * @param file       The file to read.
     * @param serializer The serializer to use.
     * @param descriptor The descriptor of the entry type of the file.
     * @param <Message>  The message type.
     * @param <Field>    The message field type.
     * @return The stream that reads the file.
     * @throws IOException when unable to open the stream.
     */
    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Stream<Message> file(File file,
                         Serializer serializer,
                         PMessageDescriptor<Message, Field> descriptor)
            throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        return stream(in, serializer, descriptor);
    }

    /**
     * Read a file containing entries of a given type. Tries to detect the
     * entry format of the file based on file magic. If not detected will try
     * to use the default binary serializer format.
     *
     * @param resource   The file to read.
     * @param serializer The serializer to use.
     * @param descriptor The descriptor of the entry type of the file.
     * @param <Message>  The message type.
     * @param <Field>    The message field type.
     * @return The stream that reads the file.
     * @throws IOException when unable find the resource.
     */
    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Stream<Message> resource(@Nonnull String resource,
                             @Nonnull Serializer serializer,
                             @Nonnull PMessageDescriptor<Message, Field> descriptor)
            throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream in = classLoader.getResourceAsStream(resource);
        if(in == null) {
            in = MessageStreams.class.getResourceAsStream(resource);
            if (in == null) {
                throw new IOException("No such resource " + resource);
            }
        }
        in = new BufferedInputStream(in);
        return stream(in, serializer, descriptor);
    }

    /**
     * Read a input stream containing entries of a given type.
     *
     * @param in         The input stream to read.
     * @param serializer The serializer to use.
     * @param descriptor The descriptor of the entry type of the file.
     * @param <Message>  The message type.
     * @param <Field>    The message field type.
     * @return The stream that reads the file.
     */
    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Stream<Message> stream(InputStream in,
                           Serializer serializer,
                           PMessageDescriptor<Message, Field> descriptor) {
        return StreamSupport.stream(new MessageSpliterator<>(in, serializer, descriptor), false);
    }

}
