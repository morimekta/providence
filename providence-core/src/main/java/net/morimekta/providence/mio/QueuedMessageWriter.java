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

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * A queued message writer that takes in messages onto a queue, and let
 * a single thread handle all the writes to the contained writer. This
 * writer is thread safe, and should be much faster than having multiple
 * threads fight over the file IO.
 * <p>
 * Note that the writer will continue to accept messages after it has been
 * closed.
 */
@Beta
public class QueuedMessageWriter implements MessageWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueuedMessageWriter.class);

    private final Queue<PMessage> messageQueue;
    private final ExecutorService executor;
    private final MessageWriter   writer;

    /**
     * Create a queued message writer.
     *
     * @param writer The message writer to write to.
     */
    public QueuedMessageWriter(MessageWriter writer) {
        this(writer, Executors.newSingleThreadExecutor(
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .setNameFormat("providence-queued-writer")
                        .build()));
    }

    /**
     * Create a queued message writer using the given executor service.
     * Note that the executor service will be shut down with the message queue.
     *
     * @param writer The message writer to write to.
     * @param executor The executor service running the write loop thread.
     */
    public QueuedMessageWriter(MessageWriter writer,
                               ExecutorService executor) {
        this.writer = writer;
        this.executor = executor;
        this.messageQueue = new ConcurrentLinkedQueue<>();
        this.executor.submit(this::writeLoop);
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int write(Message message) throws IOException {
        messageQueue.offer(message);
        return 1;
    }

    @Override
    public <Message extends PMessage<Message, Field>, Field extends PField>
    int write(PServiceCall<Message, Field> call) throws IOException {
        throw new UnsupportedOperationException("Queued message writer does not support service calls.");
    }

    @Override
    public int separator() throws IOException {
        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void close() throws IOException {
        if (!executor.isShutdown()) {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(100L, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while stopping writer loop thread", e);
            } finally {
                try {
                    if (messageQueue.size() > 0) {
                        while (messageQueue.size() > 0) {
                            writer.write(messageQueue.poll());
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error("Unable to write messages on close", e);
                }
                writer.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void writeLoop() {
        try {
            while (!executor.isShutdown()) {
                try {
                    while (messageQueue.size() > 0) {
                        writer.write(messageQueue.poll());
                    }
                    sleep(3L);  // 3ms should be enough to do actual work.
                    // This is a very tight loop, so should be expensive to
                    // to have a short sleep time.
                } catch (IOException e) {
                    LOGGER.error("Unable to write message", e);
                    // Continue but with longer sleep on errors.
                    sleep(137L);
                }
            }
        } catch (InterruptedException ignore) {
            // thread is interrupted, just stop.
            Thread.currentThread().interrupt();
        }
    }
}
