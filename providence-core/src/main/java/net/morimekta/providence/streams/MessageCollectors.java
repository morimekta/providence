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
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.Serializer;

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
 * @author Stein Eldar Johnsen
 * @since 28.01.16.
 */
public class MessageCollectors {
    public static <T extends PMessage<T>> Collector<T, OutputStream, Integer> toFile(File file,
                                                                                     Serializer serializer) {
        return toPath(file.toPath(), serializer);
    }

    public static <T extends PMessage<T>> Collector<T, OutputStream, Integer> toPath(Path file,
                                                                                     Serializer serializer) {
        final AtomicInteger result = new AtomicInteger(0);
        return Collector.of(() -> {
            try {
                return new BufferedOutputStream(new FileOutputStream(file.toFile()));
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to open " + file.getFileName(), e);
            }
        }, (outputStream, t) -> {
            try {
                result.addAndGet(serializer.serialize(outputStream, t));
                if (!serializer.binaryProtocol()) {
                    result.addAndGet(maybeWriteBytes(outputStream, MessageStreams.READABLE_ENTRY_SEP));
                }
            } catch (SerializerException e) {
                e.printStackTrace();
                throw new UncheckedIOException("Bad data", new IOException(e));
            } catch (IOException e) {
                e.printStackTrace();
                throw new UncheckedIOException("Unable to write to " + file.getFileName(), e);
            }
        }, (a, b) -> null, (outputStream) -> {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new UncheckedIOException("Unable to close " + file.getFileName(), e);
            }
            return result.get();
        });
    }

    public static <T extends PMessage<T>> Collector<T, OutputStream, Integer> toStream(OutputStream out,
                                                                                       Serializer serializer) {
        final AtomicInteger result = new AtomicInteger(0);
        return Collector.of(() -> new BufferedOutputStream(out), (outputStream, t) -> {
            try {
                synchronized (outputStream) {
                    result.addAndGet(serializer.serialize(outputStream, t));
                    if (!serializer.binaryProtocol()) {
                        result.addAndGet(maybeWriteBytes(outputStream, MessageStreams.READABLE_ENTRY_SEP));
                    }
                }
            } catch (SerializerException e) {
                e.printStackTrace();
                throw new UncheckedIOException("Bad data", new IOException(e));
            } catch (IOException e) {
                e.printStackTrace();
                throw new UncheckedIOException("Broken pipe", e);
            }
        }, (a, b) -> null, (outputStream) -> {
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new UncheckedIOException("Broken pipe", e);
            }
            return result.get();
        });
    }

    private static int maybeWriteBytes(OutputStream out, byte[] bytes) {
        if(bytes.length > 0) {
            try {
                out.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                throw new UncheckedIOException(e);
            }
        }
        return bytes.length;
    }
}
