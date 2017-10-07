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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private final Queue<PServiceCall> callQueue;
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
        this.callQueue = new ConcurrentLinkedQueue<>();
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
        callQueue.offer(call);
        return 1;
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
                if (!executor.awaitTermination(1000L, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while stopping writer loop thread", e);
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                try {
                    while (messageQueue.size() > 0) {
                        writer.write(messageQueue.poll());
                        writer.separator();
                    }
                    while (callQueue.size() > 0) {
                        writer.write(callQueue.poll());
                        writer.separator();
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
            long failDelay = 137L;
            while (!executor.isShutdown()) {
                try {
                    while (messageQueue.size() > 0) {
                        writer.write(messageQueue.poll());
                        failDelay = 137L;
                    }
                    while (callQueue.size() > 0) {
                        writer.write(callQueue.poll());
                        failDelay = 137L;
                    }
                    sleep(3L);  // 3ms should be enough to do actual work.
                    // This is a very tight loop, so should be expensive to
                    // to have a short sleep time.
                } catch (IOException e) {
                    if (failDelay >= 10_000) {
                        LOGGER.error("Unable to write message, sleeping {}s",
                                     (failDelay / 1000), e);
                    } else {
                        LOGGER.error("Unable to write message, sleeping {}ms",
                                     failDelay, e);
                    }

                    // Continue but with longer sleep on errors.
                    try {
                        sleep(failDelay);
                    } finally {
                        failDelay = Math.min(
                                TimeUnit.MINUTES.toMillis(10),
                                // add 2/3 to the time for each consecutive failure.
                                (long) (failDelay * 1.66666667));
                    }
                }
            }
        } catch (InterruptedException ignore) {
            // thread is interrupted, just stop. Not tested.
            Thread.currentThread().interrupt();
        }
    }

    @VisibleForTesting
    protected void sleep(long ms) throws InterruptedException {
        Thread.sleep(ms);
    }
}
