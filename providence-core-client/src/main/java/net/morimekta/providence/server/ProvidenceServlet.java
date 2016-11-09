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
package net.morimekta.providence.server;

import net.morimekta.providence.PProcessor;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.mio.IOMessageReader;
import net.morimekta.providence.mio.IOMessageWriter;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.serializer.ApplicationException;
import net.morimekta.providence.serializer.ApplicationExceptionType;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.SerializerProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A javax.servlet implementation for providence. Transfers data like the
 * Thrift's <code>org.apache.thrift.server.TServlet</code> server.
 */
public class ProvidenceServlet extends HttpServlet {
    private final ProcessorHandler   handler;
    private final SerializerProvider serializerProvider;

    public ProvidenceServlet(PProcessor processor, SerializerProvider serializerProvider) {
        this(new DefaultProcessorHandler(processor), serializerProvider);
    }

    public ProvidenceServlet(ProcessorHandler handler, SerializerProvider serializerProvider) {
        this.handler = handler;
        this.serializerProvider = serializerProvider;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Serializer requestSerializer = serializerProvider.getDefault();
        if (req.getContentType() != null) {
            requestSerializer = serializerProvider.getSerializer(req.getContentType());

            if (requestSerializer == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown content-type: " + req.getContentType());
                return;
            }
        }

        Serializer responseSerializer = requestSerializer;
        String accept = resp.getHeader("Accept");
        if (accept != null) {
            responseSerializer = serializerProvider.getSerializer(accept);
            if (responseSerializer == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown accept content-type: " + accept);
                return;
            }
        } else {
            accept = responseSerializer.mimeType();
        }

        try {
            MessageReader reader = new IOMessageReader(req.getInputStream(), requestSerializer);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MessageWriter writer = new IOMessageWriter(baos, responseSerializer);

            try {
                handler.process(reader, writer);
            } catch (IOException ie) {
                writer.write(new PServiceCall<>(
                        "", PServiceCallType.EXCEPTION, 0,
                        new ApplicationException(ie.getMessage(),
                                                 ApplicationExceptionType.INTERNAL_ERROR)));
                return;
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(accept);
            resp.getOutputStream().write(baos.toByteArray());
        } catch (IOException|SerializerException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error: " + e.getMessage());
        }
    }
}
