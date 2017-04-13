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
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.io.IOUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Helper class to create streams that read providence messages.
 */
public class MessageStreams {
    @SuppressFBWarnings(justification = "We need this constant some place anyway, and" +
                                        "java does still not support truly const arrays.",
                        value = "MS_MUTABLE_ARRAY")
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
     * @throws IOException when unable to open the stream.
     */
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Stream<Message> resource(String resource,
                             Serializer serializer,
                             PMessageDescriptor<Message, Field> descriptor)
            throws IOException {
        InputStream in = MessageStreams.class.getResourceAsStream(resource);
        if (in == null) {
            throw new IOException("No such resource " + resource);
        }
        return StreamSupport.stream(new StreamMessageSpliterator<>(
                new BufferedInputStream(in),
                serializer,
                descriptor,
                is -> {
                    try {
                        is.close();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }), false);
    }

    /**
     * Read a input stream containing entries of a given type. Tries to detect the
     * entry format of the file based on file magic. If not detected will try
     * to use the default binary serializer format.
     *
     * @param in         The input stream to read.
     * @param serializer The serializer to use.
     * @param descriptor The descriptor of the entry type of the file.
     * @param <Message>  The message type.
     * @param <Field>    The message field type.
     * @return The stream that reads the file.
     * @throws IOException when unable to open the stream.
     */
    public static <Message extends PMessage<Message, Field>, Field extends PField> Stream<Message> stream(InputStream in,
                                                                                                          Serializer serializer,
                                                                                                          PMessageDescriptor<Message, Field> descriptor)
            throws IOException {
        return StreamSupport.stream(new StreamMessageSpliterator<>(in, serializer, descriptor, null), false);
    }

    private static abstract class BaseMessageSpliterator<Message extends PMessage<Message, Field>, Field extends PField>
            implements Spliterator<Message> {
        protected abstract Message read();

        @Override
        public boolean tryAdvance(Consumer<? super Message> action) {
            Message message = read();
            if (message != null) {
                action.accept(message);
                return true;
            }
            return false;
        }

        /**
         * Normally we cannot split the stream.
         *
         * @return null (no split).
         */
        @Override
        public Spliterator<Message> trySplit() {
            return null;
        }

        /**
         * We mostly never know the number of messages in a message stream
         * until the last message has been read.
         *
         * @return Long.MAX_VALUE (not known).
         */
        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        /**
         * We mostly never know the number of messages in a message stream
         * until the last message has been read.
         *
         * @return -1 (not known).
         */
        @Override
        public long getExactSizeIfKnown() {
            return -1;
        }

        /**
         * Ordered, non-null and immutable.
         *
         * @return The characteristics.
         */
        @Override
        public int characteristics() {
            return ORDERED | NONNULL | IMMUTABLE;
        }

        /**
         * Messages are comparable.
         *
         * @return Comparable compareTo method.
         */
        @Override
        public Comparator<? super Message> getComparator() {
            return Comparable::compareTo;
        }
    }

    private static class StreamMessageSpliterator<Message extends PMessage<Message, Field>, Field extends PField>
            extends BaseMessageSpliterator<Message, Field> {
        private final InputStream                        in;
        private final PMessageDescriptor<Message, Field> descriptor;
        private final Serializer                         serializer;

        private int                   num;
        private Consumer<InputStream> closer;

        private StreamMessageSpliterator(InputStream in,
                                         Serializer serializer,
                                         PMessageDescriptor<Message, Field> descriptor,
                                         Consumer<InputStream> closer) throws IOException {
            this.in = in;
            this.closer = closer;

            this.serializer = serializer;
            this.descriptor = descriptor;

            this.num = 0;
        }

        @Override
        public Message read() {
            try {
                if (num > 0) {
                    if (!serializer.binaryProtocol()) {
                        if (!IOUtils.skipUntil(in, READABLE_ENTRY_SEP)) {
                            // no next entry found.
                            close();
                            return null;
                        }
                    }
                }
                // Try to check if there is a byte available. Since the
                // available() method ony checks for available non-blocking
                // reads, we need to actually try to read a byte.
                //
                // Sadly this means it's only available when marks are
                // supported.
                if (in.markSupported()) {
                    in.mark(2);
                    if (in.read() < 0) {
                        return null;
                    }
                    in.reset();
                }
                return serializer.deserialize(in, descriptor);
            } catch (SerializerException e) {
                close();
                throw new UncheckedIOException(new IOException(e));
            } catch (IOException e) {
                close();
                throw new UncheckedIOException(e);
            } finally {
                ++num;
            }
        }

        void close() {
            if (closer != null) {
                try {
                    closer.accept(in);
                } finally {
                    closer = null;
                }
            }
        }
    }
}
