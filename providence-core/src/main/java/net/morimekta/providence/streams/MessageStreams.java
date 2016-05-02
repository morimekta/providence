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

package net.morimekta.providence.streams;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.util.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
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
     * @param <T>        The message type.
     * @param <F>        The message field type.
     * @return The stream that reads the file.
     * @throws IOException when unable to open the stream.
     */
    public static <T extends PMessage<T>, F extends PField> Stream<T> file(File file,
                                                                           Serializer serializer,
                                                                           PStructDescriptor<T, F> descriptor)
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
     * @param <T>        The message type.
     * @param <F>        The message field type.
     * @return The stream that reads the file.
     * @throws IOException when unable to open the stream.
     */
    public static <T extends PMessage<T>, F extends PField> Stream<T> resource(String resource,
                                                                               Serializer serializer,
                                                                               PStructDescriptor<T, F> descriptor)
            throws IOException {
        InputStream in = MessageStreams.class.getResourceAsStream(resource);
        if (in == null) {
            throw new IOException("No such resource " + resource);
        }
        return StreamSupport.stream(new StreamMessageSpliterator<>(new BufferedInputStream(in),
                                                                   serializer,
                                                                   descriptor,
                                                                   is -> {
                                                                       try {
                                                                           is.close();
                                                                           return null;
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
     * @param <T>        The message type.
     * @param <F>        The message field type.
     * @return The stream that reads the file.
     * @throws IOException when unable to open the stream.
     */
    public static <T extends PMessage<T>, F extends PField> Stream<T> stream(InputStream in,
                                                                             Serializer serializer,
                                                                             PStructDescriptor<T, F> descriptor)
            throws IOException {
        return StreamSupport.stream(new StreamMessageSpliterator<>(in, serializer, descriptor, null), false);
    }

    private static abstract class BaseMessageSpliterator<T extends PMessage<T>> implements Spliterator<T> {
        protected abstract T read();

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            T message = read();
            if (message != null) {
                action.accept(message);
                return true;
            }
            return false;
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            T message;
            while ((message = read()) != null) {
                action.accept(message);
            }
        }

        /**
         * Normally we cannot split the stream.
         *
         * @return null (no split).
         */
        @Override
        public Spliterator<T> trySplit() {
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
        public Comparator<? super T> getComparator() {
            return Comparable::compareTo;
        }
    }

    private static class StreamMessageSpliterator<T extends PMessage<T>, F extends PField>
            extends BaseMessageSpliterator<T> {
        private final InputStream             in;
        private final PStructDescriptor<T, F> descriptor;
        private final Serializer              serializer;

        private int                         num;
        private Function<InputStream, Void> closer;

        private StreamMessageSpliterator(InputStream in,
                                         Serializer serializer,
                                         PStructDescriptor<T, F> descriptor,
                                         Function<InputStream, Void> closer) throws IOException {
            this.in = in;
            this.closer = closer;

            this.serializer = serializer;
            this.descriptor = descriptor;

            this.num = 0;
        }

        @Override
        public T read() {
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
                    if (in.read() < 0)
                        return null;
                    in.reset();
                }
                T out = serializer.deserialize(in, descriptor);
                if (out == null) {
                    close();
                }
                return out;
            } catch (SerializerException e) {
                throw new UncheckedIOException(new IOException(e));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } finally {
                ++num;
            }
        }

        void close() {
            if (closer != null) {
                try {
                    closer.apply(in);
                } finally {
                    closer = null;
                }
            }
        }
    }
}
