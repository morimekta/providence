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
package net.morimekta.providence.client;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallHandler;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerProvider;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import org.apache.http.HttpHost;
import org.apache.http.conn.HttpHostConnectException;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.function.Supplier;

/**
 * HTTP client handler using the google HTTP client interface.
 */
public class HttpClientHandler implements PServiceCallHandler {
    private final HttpRequestFactory   factory;
    private final SerializerProvider   serializerProvider;
    private final Serializer           requestSerializer;
    private final Supplier<GenericUrl> urlSupplier;

    public HttpClientHandler(Supplier<GenericUrl> urlSupplier, HttpRequestFactory factory, SerializerProvider serializerProvider) {
        this.urlSupplier = urlSupplier;
        this.factory = factory;
        this.serializerProvider = serializerProvider;
        this.requestSerializer = serializerProvider.getDefault();
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

            Serializer responseSerializer = requestSerializer;
            if (response.getContentType() != null) {
                try {
                    responseSerializer = serializerProvider.getSerializer(response.getContentType());
                } catch (IllegalArgumentException e) {
                    throw new PApplicationException("Unknown content-type in response: " + response.getContentType(),
                                                    PApplicationExceptionType.INVALID_PROTOCOL)
                            .initCause(e);
                }
            }

            PServiceCall<Response, ResponseField> reply = null;
            if (call.getType() == PServiceCallType.CALL) {
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

            return reply;
        } catch (HttpHostConnectException e) {
            throw e;
        } catch (ConnectException e) {
            // Normalize connection refused exceptions to HttpHostConnectException.
            // The native exception is not helpful (for when using NetHttpTransport).
            throw new HttpHostConnectException(e, new HttpHost(url.getHost(), url.getPort(), url.getScheme()));
        }
    }
}
