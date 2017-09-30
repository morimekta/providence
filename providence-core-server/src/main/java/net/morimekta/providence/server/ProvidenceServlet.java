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
package net.morimekta.providence.server;

import net.morimekta.providence.PProcessor;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.mio.IOMessageReader;
import net.morimekta.providence.mio.IOMessageWriter;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerProvider;

import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A javax.servlet implementation for providence. Transfers data like the
 * Thrift's <code>org.apache.thrift.server.TServlet</code> server.
 */
public class ProvidenceServlet extends HttpServlet {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProvidenceServlet.class);
    private final static long NS_IN_MILLIS = TimeUnit.MILLISECONDS.toNanos(1);

    private final ProcessorProvider  processorProvider;
    private final SerializerProvider serializerProvider;
    private final ServiceInstrumentation instrumentation;

    /**
     * Creates a providence servlet that uses the same processor every time.
     *
     * @param processor The providence service processor.
     * @param serializerProvider The serializer provider.
     */
    public ProvidenceServlet(@Nonnull PProcessor processor,
                             @Nonnull SerializerProvider serializerProvider) {
        // Default is to always use the same processor.
        this(r -> processor, serializerProvider);
    }

    /**
     * Creates a providence servlet that uses the same processor every time.
     *
     * @param processor The providence service processor.
     * @param serializerProvider The serializer provider.
     */
    public ProvidenceServlet(@Nonnull PProcessor processor,
                             @Nonnull SerializerProvider serializerProvider,
                             @Nonnull ServiceInstrumentation instrumentation) {
        // Default is to always use the same processor.
        this(r -> processor, serializerProvider, instrumentation);
    }

    /**
     * Creates a providence servlet that uses a per request processor.
     *
     * @param processorProvider The processor supplier.
     * @param serializerProvider The serializer provider.
     */
    public ProvidenceServlet(@Nonnull ProcessorProvider processorProvider,
                             @Nonnull SerializerProvider serializerProvider) {
        this(processorProvider, serializerProvider, (time, call, response) -> {});
    }

    /**
     * Creates a providence servlet that uses a per request processor.
     *
     * @param processorProvider The processor supplier.
     * @param serializerProvider The serializer provider.
     */
    public ProvidenceServlet(@Nonnull ProcessorProvider processorProvider,
                             @Nonnull SerializerProvider serializerProvider,
                             @Nonnull ServiceInstrumentation instrumentation) {
        this.processorProvider = processorProvider;
        this.serializerProvider = serializerProvider;
        this.instrumentation = instrumentation;
    }

    /**
     * Override if you want to do fancy stuff with the processor.
     *
     * @param processor The processor to handle the service call.
     * @return The processor handler to be used.
     */
    protected ProcessorHandler getHandler(PProcessor processor) {
        // Create a new processor handler instance for each request, as
        // they may be request context dependent. E.g. depends on
        // information in header, servlet context etc.
        return new DefaultProcessorHandler(processor);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long start = System.nanoTime();

        AtomicReference<PServiceCall> callRef = new AtomicReference<>();
        AtomicReference<PServiceCall> responseRef = new AtomicReference<>();
        PProcessor processor = new WrappedProcessor(processorProvider.processorForRequest(req), (c, r) -> {
            callRef.set(c);
            responseRef.set(r.handleCall(c));
            return responseRef.get();
        });

        try {
            Serializer requestSerializer = serializerProvider.getDefault();
            if (req.getContentType() != null) {
                try {
                    MediaType mediaType = MediaType.parse(req.getContentType());
                    requestSerializer = serializerProvider.getSerializer(mediaType.withoutParameters()
                                                                                  .toString());
                } catch (IllegalArgumentException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown content-type: " + req.getContentType());
                    LOGGER.warn("Unknown content type in request", e);
                    return;
                }
            } else {
                LOGGER.debug("Request is missing content type.");
            }

            Serializer responseSerializer = requestSerializer;
            String acceptHeader = resp.getHeader("Accept");
            if (acceptHeader != null) {
                String[] entries = acceptHeader.split("[,]");
                for (String entry : entries) {
                    entry = entry.trim();
                    if (entry.isEmpty()) {
                        continue;
                    }

                    try {
                        MediaType mediaType = MediaType.parse(entry);
                        responseSerializer = serializerProvider.getSerializer(mediaType.withoutParameters()
                                                                                       .toString());
                        break;
                    } catch (IllegalArgumentException ignore) {
                        // Ignore. Bad header input is pretty common.
                    }
                }
            }

            MessageReader reader = new IOMessageReader(req.getInputStream(), requestSerializer);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MessageWriter writer = new IOMessageWriter(baos, responseSerializer);

            getHandler(processor).process(reader, writer);
            resp.setStatus(HttpServletResponse.SC_OK);
            if (baos.size() > 0) {
                resp.setContentType(responseSerializer.mediaType());
                resp.setContentLength(baos.size());
                resp.getOutputStream().write(baos.toByteArray());
                resp.getOutputStream().flush();
            }
        } catch (Exception e) {
            String logName = processor.getDescriptor().getQualifiedName();
            if (callRef.get() != null) {
                logName = logName + "." + callRef.get().getMethod();
            }

            LOGGER.error("Unhandled exception in service call {}", logName, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        } finally {
            try {
                long duration = System.nanoTime() - start;
                instrumentation.afterCall(((double) duration) / NS_IN_MILLIS, callRef.get(), responseRef.get());
            } catch (Throwable th) {
                LOGGER.error("Exception in service instrumentation", th);
            }
        }
    }
}
