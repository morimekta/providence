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
package net.morimekta.providence.thrift.server;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PProcessor;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.thrift.io.FramedBufferOutputStream;
import net.morimekta.providence.util.ServiceCallInstrumentation;
import net.morimekta.util.io.ByteBufferInputStream;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.thrift.transport.TFramedTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Based heavily on {@link org.apache.thrift.transport.TNonblockingServerTransport},
 * and meant to be a providence replacement for it.
 */
public class NonblockingSocketServer implements AutoCloseable {
    public static class Builder {
        private final PProcessor                 processor;
        private       ServiceCallInstrumentation instrumentation;
        private       InetSocketAddress          bindAddress;

        private int maxFrameSizeInBytes = 16384000; // 16M.
        private int readTimeoutInMs     = 60000;  // 60 seconds
        private int backlog             = 50;
        private int workerThreads       = 10;

        private ThreadFactory receiverThreadFactory;
        private ThreadFactory workerThreadFactory;
        private Serializer    serializer;

        public Builder(@Nonnull PProcessor processor) {
            this.processor = processor;
            this.bindAddress = new InetSocketAddress(0);
            this.workerThreadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("providence-nonblocking-server-%d")
                    .setDaemon(true)
                    .build();
            this.receiverThreadFactory = workerThreadFactory;
            this.serializer = new BinarySerializer();
        }

        public Builder withPort(int port) {
            if (port < 0) {
                throw new IllegalArgumentException();
            }
            this.bindAddress = new InetSocketAddress(port);
            return this;
        }

        public Builder withBindAddress(@Nonnull InetSocketAddress bindAddress) {
            this.bindAddress = bindAddress;
            return this;
        }

        public Builder withMaxBacklog(int maxBacklog) {
            if (maxBacklog < 0) {
                throw new IllegalArgumentException();
            }
            this.backlog = maxBacklog;
            return this;
        }

        public Builder withMaxFrameSizeInBytes(int size) {
            if (size < 1024) {
                throw new IllegalArgumentException();
            }
            this.maxFrameSizeInBytes = size;
            return this;
        }

        public Builder withInstrumentation(@Nonnull ServiceCallInstrumentation instrumentation) {
            this.instrumentation = instrumentation;
            return this;
        }

        public Builder withReadTimeout(int timeoutInMs) {
            if (timeoutInMs < 1) {
                throw new IllegalArgumentException();
            }
            this.readTimeoutInMs = timeoutInMs;
            return this;
        }

        public Builder withWorkerThreads(int numThreads) {
            if (numThreads < 1) {
                throw new IllegalArgumentException();
            }

            this.workerThreads = numThreads;
            return this;
        }

        public Builder withWorkerThreadFactory(ThreadFactory factory) {
            this.workerThreadFactory = factory;
            return this;
        }

        public Builder withReceiverThreadFactory(ThreadFactory factory) {
            this.receiverThreadFactory = factory;
            return this;
        }

        public Builder withSerializer(Serializer serializer) {
            this.serializer = serializer;
            return this;
        }

        public NonblockingSocketServer start() {
            return new NonblockingSocketServer(this);
        }
    }

    public static Builder builder(@Nonnull PProcessor processor) {
        return new Builder(processor);
    }

    public int getPort() {
        if (receiverExecutor.isShutdown())
            return -1;
        return serverSocket.getLocalPort();
    }

    public void close() throws IOException {
        receiverExecutor.shutdown();
        workerExecutor.shutdown();
        try {
            // this should trigger exception in the accept task.
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workerExecutor.awaitTermination(10, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                receiverExecutor.awaitTermination(10, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // really really try to kill it now.
            receiverExecutor.shutdownNow();
            workerExecutor.shutdownNow();
        }
    }

    private final static Logger LOGGER       = LoggerFactory.getLogger(NonblockingSocketServer.class);
    private final static long   NS_IN_MILLIS = ServiceCallInstrumentation.NS_IN_MILLIS;

    private final Selector               selector;
    private final PProcessor             processor;
    private final Serializer             serializer;
    private final ServiceCallInstrumentation instrumentation;
    private final ServerSocketChannel    serverSocketChannel;
    private final ServerSocket           serverSocket;
    private final ExecutorService        receiverExecutor;
    private final ExecutorService        workerExecutor;
    private final int                    maxFrameSizeInBytes;

    private NonblockingSocketServer(Builder builder) {
        try {
            maxFrameSizeInBytes = builder.maxFrameSizeInBytes;

            serializer = builder.serializer;
            processor = builder.processor;
            instrumentation = builder.instrumentation != null
                              ? builder.instrumentation
                              : (duration, call, response) -> {};
            selector = Selector.open();

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            // Make server socket
            serverSocket = serverSocketChannel.socket();
            serverSocketChannel.socket().setSoTimeout(builder.readTimeoutInMs);

            // Prevent 2MSL delay problem on server restarts
            serverSocket.setReuseAddress(true);
            // Bind to listening port
            serverSocket.bind(builder.bindAddress, builder.backlog);

            // Needs one thread for each receiver, and one for each response writer.
            receiverExecutor = Executors.newSingleThreadExecutor(builder.receiverThreadFactory);
            workerExecutor = Executors.newFixedThreadPool(builder.workerThreads, builder.workerThreadFactory);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            receiverExecutor.submit(this::selectLoop);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private synchronized void selectLoop() {
        while (serverSocketChannel.isOpen()) {
            try {
                selector.select();
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys()
                                                              .iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();

                    if (!key.isValid()) {
                        // clean up?
                        selectedKeys.remove();
                        continue;
                    }

                    if (key.isAcceptable()) {
                        accept();
                    } else if (key.isReadable()) {
                        handleRead(key, (Context) key.attachment());
                    } else if (key.isWritable()) {
                        handleWrite(key, (Context) key.attachment());
                    }

                    // only remove successfully handled keys from currently selected.
                    selectedKeys.remove();
                }
            } catch (IOException e) {
                LOGGER.error("Exception in thread: " + e.getMessage(), e);
            }

            // TODO: Figure out if this needs to be in a separate thread.
            for (SelectionKey cleanupKey : selector.keys()) {
                if (cleanupKey.channel() == serverSocketChannel) {
                    continue;
                }

                SocketChannel channel = (SocketChannel) cleanupKey.channel();
                if (!cleanupKey.isValid() || !channel.isOpen() || channel.socket()
                                                                         .isClosed()) {
                    try {
                        cleanupKey.channel()
                                  .close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cleanupKey.cancel();
                }
            }
        }
    }

    private void accept() {
        try {
            SocketChannel socketChannel;
            while ((socketChannel = serverSocketChannel.accept()) != null) {
                // But make the actual accepted channel blocking.
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ, new Context(socketChannel));
            }
        } catch (IOException e) {
            LOGGER.error("Exception when accepting: {}", e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleRead(SelectionKey key, Context context) throws IOException {
        long startTime = System.nanoTime();

        // part a: read into the readBuffer.
        if (context.currentFrameSize == 0) {
            // read frame size.
            try {
                if (context.channel.read(context.sizeBuffer) < 0) {
                    context.close();
                    key.cancel();
                    return;
                }
                if (context.sizeBuffer.position() < 4) {
                    return;
                }
            } catch (IOException e) {
                // LOGGER.error(e.getMessage(), e);
                context.close();
                key.cancel();
                return;
            }

            context.currentFrameSize = TFramedTransport.decodeFrameSize(context.sizeBuffer.array());
            context.sizeBuffer.rewind();

            if (context.currentFrameSize > maxFrameSizeInBytes) {
                LOGGER.warn("Attempting message of " + context.currentFrameSize + " > " + maxFrameSizeInBytes);
                context.close();
                key.cancel();
                return;
            }
            if (context.currentFrameSize < 1) {
                LOGGER.warn("Attempting message of " + context.currentFrameSize);
                context.close();
                key.cancel();
                return;
            }

            context.readBuffer.rewind();
            context.readBuffer.limit(context.currentFrameSize);
        }

        try {
            if (context.channel.read(context.readBuffer) < 0) {
                LOGGER.warn("Closed connection while reading frame");
                context.close();
                key.cancel();
                return;
            }
        } catch (IOException e) {
            LOGGER.warn("Exception reading frame: {}", e.getMessage(), e);
            context.close();
            key.cancel();
            return;
        }

        if (context.readBuffer.position() < context.readBuffer.limit()) {
            // wait until next read, and see if remaining of frame has arrived.
            return;
        }

        // part b: if the read buffer is complete, handle the content.
        PServiceCall call;
        try {
            context.currentFrameSize = 0;
            context.readBuffer.flip();

            call = serializer.deserialize(new ByteBufferInputStream(context.readBuffer),
                                          processor.getDescriptor());
            context.readBuffer.clear();

            workerExecutor.submit(() -> {
                PServiceCall reply;
                try {
                    reply = processor.handleCall(call);
                } catch (Exception e) {
                    reply = new PServiceCall<>(call.getMethod(),
                                               PServiceCallType.EXCEPTION,
                                               call.getSequence(),
                                               PApplicationException.builder()
                                                                    .setMessage(e.getMessage())
                                                                    .setId(PApplicationExceptionType.INTERNAL_ERROR)
                                                                    .initCause(e)
                                                                    .build());
                }

                synchronized (context.writeQueue) {
                    context.writeQueue.offer(new WriteEntry(startTime, call, reply));
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                    selector.wakeup();
                }
            });
        } catch (IOException e) {
            double duration = ((double) System.nanoTime() - startTime) / NS_IN_MILLIS;
            instrumentation.onTransportException(e, duration, null, null);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleWrite(SelectionKey key, Context context) {
        WriteEntry entry;
        while ((entry = context.writeQueue.poll()) != null) {
            Exception ex = null;
            try {
                serializer.serialize(context.out, entry.reply);
            } catch (IOException e) {
                ex = e;
            } finally {
                try {
                    context.out.completeFrame();
                    context.out.flush();
                } catch (IOException e) {
                    LOGGER.error("Failed to write frame: {}", e.getMessage(), e);
                    context.close();
                    key.cancel();
                } finally {
                    double duration = ((double) System.nanoTime() - entry.startTime) / NS_IN_MILLIS;
                    if (ex == null) {
                        instrumentation.onComplete(duration, entry.call, entry.reply);
                    } else {
                        instrumentation.onTransportException(ex, duration, entry.call, entry.reply);
                    }
                }
            }
        }

        synchronized (context.writeQueue) {
            // double-guard as a new write entry may just have been added.
            if (context.writeQueue.isEmpty()) {
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            }
        }
    }

    private class WriteEntry {
        long startTime;
        PServiceCall call;
        PServiceCall reply;

        WriteEntry(long startTime, PServiceCall call, PServiceCall reply) {
            this.startTime = startTime;
            this.call = call;
            this.reply = reply;
        }
    }

    private class Context {
        final SocketChannel            channel;
        final Queue<WriteEntry>        writeQueue;
        final FramedBufferOutputStream out;

        final ByteBuffer               sizeBuffer;
        final ByteBuffer               readBuffer;
        int currentFrameSize;

        private Context(SocketChannel channel) {
            this.channel = channel;
            this.currentFrameSize = 0;
            this.sizeBuffer = ByteBuffer.allocate(4);
            this.readBuffer = ByteBuffer.allocateDirect(maxFrameSizeInBytes);
            this.out = new FramedBufferOutputStream(channel, maxFrameSizeInBytes);
            this.writeQueue = new ConcurrentLinkedQueue<>();
        }

        void close() {
            try {
                channel.socket().close();
                channel.close();
            } catch (IOException e) {
                LOGGER.warn("Exception closing channel: {}", e.getMessage(), e);
            }
        }
    }
}

