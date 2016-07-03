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
package net.morimekta.providence.client;

import net.morimekta.providence.PClientHandler;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.SerializerProvider;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * HTTP client handler using the google HTTP client interface.
 */
public class HttpClientHandler implements PClientHandler {
    private final HttpRequestFactory   factory;
    private final SerializerProvider   serializerProvider;
    private final Serializer           requestSerializer;
    private final Supplier<GenericUrl> urlSupplier;

    public HttpClientHandler(Supplier<GenericUrl> urlSupplier, HttpRequestFactory factory, SerializerProvider serializerProvider) {
        this.urlSupplier = urlSupplier;
        this.factory = factory;
        this.serializerProvider = serializerProvider;
        this.requestSerializer = serializerProvider.getDefault();

        if (requestSerializer == null) {
            throw new IllegalStateException("Serializer provider has no default serializer");
        }
    }

    @Override
    public <Request extends PMessage<Request, RequestField>,
            Response extends PMessage<Response, ResponseField>,
            RequestField extends PField,
            ResponseField extends PField>
    PServiceCall<Response, ResponseField> handleCall(PServiceCall<Request, RequestField> pServiceCall,
                                                     PService service)
            throws IOException, SerializerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        requestSerializer.serialize(baos, pServiceCall);

        ByteArrayContent content = new ByteArrayContent(requestSerializer.mimeType(), baos.toByteArray());

        HttpRequest request = factory.buildPostRequest(urlSupplier.get(), content);
        HttpResponse response = request.execute();

        Serializer responseSerializer = requestSerializer;
        if (response.getContentType() != null) {
            responseSerializer = serializerProvider.getSerializer(response.getContentType());
            if (responseSerializer == null) {
                throw new IOException("Unknown mime type in response: " + response.getContentType());
            }
        }

        return responseSerializer.deserialize(response.getContent(), service);
    }
}
