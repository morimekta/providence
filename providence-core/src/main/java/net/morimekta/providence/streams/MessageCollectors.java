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
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;

import com.google.common.base.Suppliers;

import javax.annotation.Nonnull;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;

/**
 * Collector helpers for writing a number of messages to a output stream, file etc.
 */
public class MessageCollectors {
    /**
     * Write stream of messages to file described by path.
     *
     * @param file The file path.
     * @param serializer The serializer to use.
     * @param <Message> The message type.
     * @param <Field> The field type.
     * @return The collector.
     */
    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Collector<Message, OutputStream, Integer> toPath(Path file,
                                                     Serializer serializer) {
        return toFile(file.toFile(), serializer);
    }

    /**
     * write stream of messages to file.
     *
     * @param file The file to write.
     * @param serializer The serializer to use.
     * @param <Message> The message type.
     * @param <Field> The field type.
     * @return The collector.
     */
    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Collector<Message, OutputStream, Integer> toFile(File file,
                                                     Serializer serializer) {
        final AtomicInteger result = new AtomicInteger(0);
        return Collector.of(Suppliers.memoize(() -> {
            // Delay file creation until the write starts.
            try {
                return new BufferedOutputStream(new FileOutputStream(file));
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to open " + file.getName(), e);
            }
        }), (outputStream, t) -> {
            try {
                synchronized (result) {
                    result.addAndGet(serializer.serialize(outputStream, t));
                    if (!serializer.binaryProtocol()) {
                        result.addAndGet(maybeWriteBytes(outputStream, MessageStreams.READABLE_ENTRY_SEP));
                    }
                }
            } catch (SerializerException e) {
                throw new UncheckedIOException("Bad data", e);
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to write to " + file.getName(), e);
            }
        }, (a, b) -> a, (outputStream) -> {
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to close " + file.getName(), e);
            }
            return result.getAndSet(0);
        });
    }

    /**
     * Serialize stream of messages into stream.
     *
     * @param out The output stream to write to.
     * @param serializer The serializer to use.
     * @param <Message> The message type.
     * @param <Field> The field type.
     * @return The collector.
     */
    @Nonnull
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Collector<Message, AtomicInteger, Integer> toStream(OutputStream out,
                                                        Serializer serializer) {
        return Collector.of(AtomicInteger::new, (counter, t) -> {
            try {
                synchronized (out) {
                    counter.addAndGet(serializer.serialize(out, t));
                    if (!serializer.binaryProtocol()) {
                        counter.addAndGet(maybeWriteBytes(out, MessageStreams.READABLE_ENTRY_SEP));
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e.getMessage(), e);
            }
        }, (a, b) -> {
            a.addAndGet(b.get());
            return a;
        }, i -> {
            try {
                out.flush();
            } catch (IOException e) {
                throw new UncheckedIOException(e.getMessage(), e);
            }
            return i.getAndSet(0);
        });
    }

    private static int maybeWriteBytes(OutputStream out, byte[] bytes) throws IOException {
        if(bytes.length > 0) {
            out.write(bytes);
        }
        return bytes.length;
    }

    private MessageCollectors() {}
}
