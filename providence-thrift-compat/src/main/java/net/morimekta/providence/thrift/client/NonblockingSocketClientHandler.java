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
package net.morimekta.providence.thrift.client;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallHandler;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.thrift.io.FramedBufferInputStream;
import net.morimekta.providence.thrift.io.FramedBufferOutputStream;
import net.morimekta.providence.util.ServiceCallInstrumentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static net.morimekta.providence.util.ServiceCallInstrumentation.NS_IN_MILLIS;

/**
 * Client handler for thrift RPC using the TNonblockingServer, or similar that
 * uses the TFramedTransport message wrapper. It is able to handle a true
 * async-like message and response order, so even if the server sends responses
 * out of order this client will match to the correct caller.
 *
 * The client handler is dependent on that there is a single client with unique
 * sequence IDs on incoming service calls, otherwise there will be trouble with
 * matching responses to the requesting thread.
 *
 * When using this client handler make sure to close it when no longer in use.
 * Otherwise it will keep the socket channel open almost indefinitely.
 */
public class NonblockingSocketClientHandler implements PServiceCallHandler, Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(NonblockingSocketClientHandler.class);

    private final Serializer    serializer;
    private final SocketAddress address;
    private final int           connect_timeout;
    private final int           read_timeout;
    private final int           response_timeout;

    private final Map<Integer, CompletableFuture<PServiceCall>> responseFutures;
    private final ExecutorService                               responseExecutor;
    private final ServiceCallInstrumentation                    instrumentation;

    private volatile SocketChannel channel;
    private volatile FramedBufferOutputStream out;

    public NonblockingSocketClientHandler(Serializer serializer, SocketAddress address) {
        this(serializer, address, (d, c, r) -> {});
    }

    public NonblockingSocketClientHandler(Serializer serializer, SocketAddress address, ServiceCallInstrumentation instrumentation) {
        this(serializer,
             address,
             instrumentation,
             10000,
             10000);
    }

    public NonblockingSocketClientHandler(Serializer serializer,
                                          SocketAddress address,
                                          int connect_timeout,
                                          int read_timeout ) {
        this(serializer,
             address,
             (d, c, r) -> {},
             connect_timeout,
             read_timeout);
    }

    public NonblockingSocketClientHandler(Serializer serializer,
                                          SocketAddress address,
                                          ServiceCallInstrumentation instrumentation,
                                          int connect_timeout,
                                          int read_timeout) {
        this(serializer,
             address,
             instrumentation,
             connect_timeout,
             read_timeout,
             connect_timeout + 2 * read_timeout);
    }

    public NonblockingSocketClientHandler(Serializer serializer,
                                          SocketAddress address,
                                          ServiceCallInstrumentation instrumentation,
                                          int connect_timeout,
                                          int read_timeout,
                                          int response_timeout) {
        this.serializer = serializer;
        this.address = address;
        this.instrumentation = instrumentation;
        this.connect_timeout = connect_timeout;
        this.read_timeout = read_timeout;
        this.response_timeout = response_timeout;
        this.responseFutures = new ConcurrentHashMap<>();
        this.responseExecutor = Executors.newSingleThreadExecutor();
    }

    private void ensureConnected(PService service) throws IOException {
        if (channel == null || !channel.isConnected()) {
            close();

            channel = SocketChannel.open();
            // The client channel is always in blocking mode. The read and write
            // threads handle the asynchronous nature of the protocol.
            channel.configureBlocking(true);

            Socket socket = channel.socket();
            socket.setSoLinger(false, 0);
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);
            socket.setSoTimeout(read_timeout);
            socket.connect(address, connect_timeout);

            out = new FramedBufferOutputStream(channel);
            responseExecutor.submit(() -> this.handleReadResponses(channel, service));
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (channel != null) {
            try (SocketChannel ignore1 = channel;
                 OutputStream ignore2 = out) {
                channel = null;
                out = null;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Request extends PMessage<Request, RequestField>,
            Response extends PMessage<Response, ResponseField>,
            RequestField extends PField,
            ResponseField extends PField>
    PServiceCall<Response, ResponseField> handleCall(PServiceCall<Request, RequestField> call, PService service)
            throws IOException {
        if (call.getType() == PServiceCallType.EXCEPTION || call.getType() == PServiceCallType.REPLY) {
            throw new PApplicationException("Request with invalid call type: " + call.getType(),
                                            PApplicationExceptionType.INVALID_MESSAGE_TYPE);
        }

        long startTime = System.nanoTime();
        PServiceCall<Response, ResponseField> response = null;
        CompletableFuture<PServiceCall> responseFuture = null;
        if (call.getType() == PServiceCallType.CALL) {
            responseFuture = new CompletableFuture<>();
            // Each sequence No must be unique for the client, otherwise this will be messed up.
            responseFutures.put(call.getSequence(), responseFuture);
        }

        try {
            synchronized (this) {
                try {
                    ensureConnected(service);
                    if (out == null) {
                        throw new IOException("Closed channel");
                    }
                    serializer.serialize(out, call);
                    out.flush();
                } finally {
                    if (out != null) {
                        out.completeFrame();
                    }
                }
            }

            if (responseFuture != null) {
                try {
                    if (response_timeout > 0) {
                        response = (PServiceCall<Response, ResponseField>) responseFuture.get(response_timeout, TimeUnit.MILLISECONDS);
                    } else {
                        response = (PServiceCall<Response, ResponseField>) responseFuture.get();
                    }

                    long endTime = System.nanoTime();
                    double duration = ((double) (endTime - startTime)) / NS_IN_MILLIS;
                    try {
                        instrumentation.onComplete(duration, call, response);
                    } catch (Exception ignore) {}

                    return response;
                } catch (TimeoutException | InterruptedException e) {
                    responseFuture.completeExceptionally(e);
                    throw new IOException(e.getMessage(), e);
                } catch (ExecutionException e) {
                    throw new IOException(e.getMessage(), e);
                } finally {
                    responseFutures.remove(call.getSequence());
                }
            }
        } catch (Exception e) {
            long endTime = System.nanoTime();
            double duration = ((double) (endTime - startTime)) / NS_IN_MILLIS;
            try {
                instrumentation.onTransportException(e, duration, call, response);
            } catch (Exception ie) {
                e.addSuppressed(ie);
            }

            throw e;
        }

        return null;
    }

    private void handleReadResponses(SocketChannel channel, PService service) {
        while (this.channel == channel && channel.isOpen()) {
            FramedBufferInputStream in = new FramedBufferInputStream(channel);
            try {
                in.nextFrame();
                PServiceCall reply = serializer.deserialize(in, service);

                if (reply.getType() == PServiceCallType.CALL || reply.getType() == PServiceCallType.ONEWAY) {
                    throw new PApplicationException("Reply with invalid call type: " + reply.getType(),
                                                    PApplicationExceptionType.INVALID_MESSAGE_TYPE);
                }

                CompletableFuture<PServiceCall> future = responseFutures.get(reply.getSequence());
                if (future == null) {
                    // The item response timed out.
                    LOGGER.debug("No future for sequence ID " + reply.getSequence());
                    continue;
                }

                responseFutures.remove(reply.getSequence());
                future.complete(reply);
            } catch (Exception e) {
                if (!channel.isOpen()) {
                    // If the channel is closed. Should not trigger on disconnected.
                    break;
                }
                LOGGER.error("Exception in channel response reading", e);
            }
        }

        if (responseFutures.size() > 0) {
            LOGGER.warn("Channel closed with {} unfinished calls", responseFutures.size());
            responseFutures.forEach((s, f) -> f.completeExceptionally(new IOException("Channel closed")));
            responseFutures.clear();
        }
    }
}
