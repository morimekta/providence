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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.morimekta.providence.PProcessor;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.mio.IOMessageReader;
import net.morimekta.providence.mio.IOMessageWriter;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.server.DefaultProcessorHandler;
import net.morimekta.providence.server.WrappedProcessor;
import net.morimekta.providence.util.ServiceCallInstrumentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static net.morimekta.providence.util.ServiceCallInstrumentation.NS_IN_MILLIS;

/**
 * Based heavily on <code>org.apache.thrift.transport.TServerTransport</code>
 * and meant to be a providence replacement for it.
 */
public class SocketServer implements AutoCloseable {
    public static class Builder {
        private final PProcessor                 processor;
        private       ServiceCallInstrumentation instrumentation;
        private       InetSocketAddress          bindAddress;

        private int clientTimeout       = 60000;  // 60 seconds
        private int backlog             = 50;
        private int workerThreads       = 10;
        private ThreadFactory workerThreadFactory;
        private Serializer serializer;

        public Builder(@Nonnull PProcessor processor) {
            this.processor = processor;
            this.bindAddress = new InetSocketAddress(0);
            this.workerThreadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("providence-server-%d")
                    .setDaemon(false)
                    .build();
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

        public Builder withInstrumentation(@Nonnull ServiceCallInstrumentation instrumentation) {
            this.instrumentation = instrumentation;
            return this;
        }

        public Builder withClientTimeout(int timeoutInMs) {
            if (timeoutInMs < 1) {
                throw new IllegalArgumentException();
            }
            this.clientTimeout = timeoutInMs;
            return this;
        }

        public Builder withWorkerThreads(int numThreads) {
            if (numThreads < 1) {
                throw new IllegalArgumentException();
            }

            this.workerThreads = numThreads;
            return this;
        }

        public Builder withThreadFactory(ThreadFactory factory) {
            this.workerThreadFactory = factory;
            return this;
        }

        public Builder withSerializer(Serializer serializer) {
            this.serializer = serializer;
            return this;
        }

        public SocketServer start() {
            return new SocketServer(this);
        }

    }

    public static Builder builder(@Nonnull PProcessor processor) {
        return new Builder(processor);
    }

    public int getPort() {
        if (workerExecutor.isShutdown()) return -1;
        return serverSocket.getLocalPort();
    }

    @Override
    public void close() {
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
            // really really try to kill it now.
            workerExecutor.shutdownNow();
        }
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);

    private final int                        clientTimeout;
    private final PProcessor                 processor;
    private final ServiceCallInstrumentation instrumentation;
    private final ServerSocket               serverSocket;
    private final ExecutorService            workerExecutor;
    private final Serializer                 serializer;

    private SocketServer(Builder builder) {
        try {
            clientTimeout = builder.clientTimeout;
            processor = builder.processor;
            instrumentation = builder.instrumentation != null
                              ? builder.instrumentation
                              : (duration, call, response) -> {};
            serializer = builder.serializer;

            // Make server socket.
            serverSocket = new ServerSocket();
            // Prevent 2MSL delay problem on server restarts
            serverSocket.setReuseAddress(true);
            // Bind to listening port
            serverSocket.bind(builder.bindAddress, builder.backlog);
            serverSocket.setSoTimeout(0);

            workerExecutor = Executors.newFixedThreadPool(builder.workerThreads,
                                                          builder.workerThreadFactory);
            workerExecutor.submit(this::accept);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void accept() {
        try {
            Socket socket = serverSocket.accept();
            socket.setSoTimeout(clientTimeout);
            long startTime = System.nanoTime();
            workerExecutor.submit(() -> process(startTime, socket));
        } catch (SocketTimeoutException e) {
            // ignore.
        } catch (IOException e) {
            if (workerExecutor.isShutdown()) {
                return;
            }
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
        workerExecutor.submit(this::accept);
    }

    @SuppressWarnings("unchecked")
    private void process(long startTime, Socket socket) {
        try (Socket ignore = socket;
             BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
             BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
             IOMessageReader reader = new IOMessageReader(in, serializer);
             IOMessageWriter writer = new IOMessageWriter(out, serializer)) {
            while (socket.isConnected()) {
                AtomicReference<PServiceCall> callRef = new AtomicReference<>();
                AtomicReference<PServiceCall> responseRef = new AtomicReference<>();
                try {
                    DefaultProcessorHandler handler = new DefaultProcessorHandler(new WrappedProcessor(processor, (c, p) -> {
                        callRef.set(c);
                        responseRef.set(p.handleCall(c));
                        return responseRef.get();
                    }));

                    handler.process(reader, writer);
                    out.flush();

                    long endTime = System.nanoTime();
                    double duration = ((double) (endTime - startTime)) / NS_IN_MILLIS;
                    try {
                        instrumentation.onComplete(duration, callRef.get(), responseRef.get());
                    } catch (Throwable th) {
                        LOGGER.error("Exception in service instrumentation", th);
                    }
                } catch (IOException e) {
                    long endTime = System.nanoTime();
                    double duration = ((double) (endTime - startTime)) / NS_IN_MILLIS;
                    try {
                        instrumentation.onTransportException(e, duration, callRef.get(), responseRef.get());
                    } catch (Throwable th) {
                        LOGGER.error("Exception in service instrumentation", th);
                    }

                    throw new UncheckedIOException(e.getMessage(), e);
                }

                in.mark(1);
                if (in.read() < 0) {
                    return;
                }
                in.reset();

                startTime = System.nanoTime();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }
}
