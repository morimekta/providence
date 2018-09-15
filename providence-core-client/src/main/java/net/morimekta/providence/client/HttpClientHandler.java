/*
 * Copyright 2016-2017 Providence Authors
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
package net.morimekta.providence.client;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.net.MediaType;
import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallHandler;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerProvider;
import net.morimekta.providence.util.ServiceCallInstrumentation;
import org.apache.http.HttpHost;
import org.apache.http.conn.HttpHostConnectException;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.function.Supplier;

import static net.morimekta.providence.util.ServiceCallInstrumentation.NS_IN_MILLIS;

/**
 * HTTP client handler using the google HTTP client interface.
 */
public class HttpClientHandler implements PServiceCallHandler {
    private final HttpRequestFactory factory;
    private final SerializerProvider         serializerProvider;
    private final Serializer                 requestSerializer;
    private final Supplier<GenericUrl>       urlSupplier;
    private final ServiceCallInstrumentation instrumentation;

    /**
     * Create a HTTP client with default transport, serialization and no instrumentation.
     *
     * @param urlSupplier The HTTP url supplier.
     */
    public HttpClientHandler(@Nonnull Supplier<GenericUrl> urlSupplier) {
        this(urlSupplier, new NetHttpTransport().createRequestFactory());
    }

    /**
     * Create a HTTP client with default serialization and no instrumentation.
     *
     * @param urlSupplier The HTTP url supplier.
     * @param factory The HTTP request factory.
     */
    public HttpClientHandler(@Nonnull Supplier<GenericUrl> urlSupplier,
                             @Nonnull HttpRequestFactory factory) {
        this(urlSupplier, factory, new DefaultSerializerProvider());
    }

    /**
     * Create a HTTP client with no instrumentation.
     *
     * @param urlSupplier The HTTP url supplier.
     * @param factory The HTTP request factory.
     * @param serializerProvider The serializer provider.
     */
    public HttpClientHandler(@Nonnull Supplier<GenericUrl> urlSupplier,
                             @Nonnull HttpRequestFactory factory,
                             @Nonnull SerializerProvider serializerProvider) {
        this(urlSupplier, factory, serializerProvider, (d, c, r) -> {});
    }

    /**
     * Create a HTTP client.
     *
     * @param urlSupplier The HTTP url supplier.
     * @param factory The HTTP request factory.
     * @param serializerProvider The serializer provider.
     * @param instrumentation The service call instrumentation.
     */
    public HttpClientHandler(@Nonnull Supplier<GenericUrl> urlSupplier,
                             @Nonnull HttpRequestFactory factory,
                             @Nonnull SerializerProvider serializerProvider,
                             @Nonnull ServiceCallInstrumentation instrumentation) {
        this.urlSupplier = urlSupplier;
        this.factory = factory;
        this.serializerProvider = serializerProvider;
        this.requestSerializer = serializerProvider.getDefault();
        this.instrumentation = instrumentation;
    }

    @Override
    public <Request extends PMessage<Request, RequestField>,
            Response extends PMessage<Response, ResponseField>,
            RequestField extends PField,
            ResponseField extends PField>
    PServiceCall<Response, ResponseField> handleCall(PServiceCall<Request, RequestField> call,
                                                     PService service) throws IOException {
        if (call.getType() == PServiceCallType.EXCEPTION || call.getType() == PServiceCallType.REPLY) {
            throw new PApplicationException("Request with invalid call type: " + call.getType(),
                                            PApplicationExceptionType.INVALID_MESSAGE_TYPE);
        }

        long startTime = System.nanoTime();
        PServiceCall<Response, ResponseField> reply = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            requestSerializer.serialize(baos, call);

            ByteArrayContent content = new ByteArrayContent(requestSerializer.mediaType(), baos.toByteArray());

            @Nonnull
            GenericUrl url = urlSupplier.get();
            try {
                HttpRequest request = factory.buildPostRequest(url, content);
                request.getHeaders()
                       .setAccept(requestSerializer.mediaType());
                HttpResponse response = request.execute();
                try {
                    if (call.getType() == PServiceCallType.CALL) {
                        Serializer responseSerializer = requestSerializer;
                        if (response.getContentType() != null) {
                            try {
                                MediaType mediaType = MediaType.parse(response.getContentType());
                                responseSerializer = serializerProvider.getSerializer(mediaType.withoutParameters()
                                                                                               .toString());
                            } catch (IllegalArgumentException e) {
                                throw new PApplicationException("Unknown content-type in response: " + response.getContentType(),
                                                                PApplicationExceptionType.INVALID_PROTOCOL).initCause(e);
                            }
                        }

                        // non 200 responses should have triggered a HttpResponseException,
                        // so this is safe.
                        reply = responseSerializer.deserialize(response.getContent(), service);

                        if (reply.getType() == PServiceCallType.CALL || reply.getType() == PServiceCallType.ONEWAY) {
                            throw new PApplicationException("Reply with invalid call type: " + reply.getType(),
                                                            PApplicationExceptionType.INVALID_MESSAGE_TYPE);
                        }
                        if (reply.getSequence() != call.getSequence()) {
                            throw new PApplicationException(
                                    "Reply sequence out of order: call = " + call.getSequence() + ", reply = " + reply.getSequence(),
                                    PApplicationExceptionType.BAD_SEQUENCE_ID);
                        }
                    }

                    long endTime = System.nanoTime();
                    double duration = ((double) (endTime - startTime)) / NS_IN_MILLIS;
                    try {
                        instrumentation.onComplete(duration, call, reply);
                    } catch (Exception ignore) {
                    }

                    return reply;
                } finally {
                    // Ignore whatever is left of the response when returning, in
                    // case we did'nt read the whole response.
                    response.ignore();
                }
            } catch (HttpHostConnectException e) {
                throw e;
            } catch (ConnectException e) {
                // Normalize connection refused exceptions to HttpHostConnectException.
                // The native exception is not helpful (for when using NetHttpTransport).
                throw new HttpHostConnectException(e, new HttpHost(url.getHost(), url.getPort(), url.getScheme()));
            }
        } catch (IOException | RuntimeException e) {
            long endTime = System.nanoTime();
            double duration = ((double) (endTime - startTime)) / NS_IN_MILLIS;
            try {
                instrumentation.onTransportException(e, duration, call, reply);
            } catch (Throwable ie) {
                e.addSuppressed(ie);
            }

            throw e;
        }
    }
}
