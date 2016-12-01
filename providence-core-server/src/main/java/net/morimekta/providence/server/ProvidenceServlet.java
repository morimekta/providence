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
import net.morimekta.providence.mio.IOMessageReader;
import net.morimekta.providence.mio.IOMessageWriter;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerProvider;

import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final static Logger LOGGER = LoggerFactory.getLogger(ProvidenceServlet.class);

    private final ProcessorProvider  processorProvider;
    private final SerializerProvider serializerProvider;

    /**
     * Creates a providence servlet that uses the same processor every time.
     *
     * @param processor The providence service processor.
     * @param serializerProvider The serializer provider.
     */
    public ProvidenceServlet(PProcessor processor, SerializerProvider serializerProvider) {
        // Default is to always use the same processor.
        this(r -> processor, serializerProvider);
    }

    /**
     * Creates a providence servlet that uses a per request processor.
     *
     * @param processorProvider The processor supplier.
     * @param serializerProvider The serializer provider.
     */
    public ProvidenceServlet(ProcessorProvider processorProvider, SerializerProvider serializerProvider) {
        this.processorProvider = processorProvider;
        this.serializerProvider = serializerProvider;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PProcessor processor = processorProvider.processorForRequest(req);
        try {
            Serializer requestSerializer = serializerProvider.getDefault();
            if (req.getContentType() != null) {
                requestSerializer = serializerProvider.getSerializer(req.getContentType());

                if (requestSerializer == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown content-type: " + req.getContentType());
                    return;
                }
            }

            Serializer responseSerializer = requestSerializer;
            String acceptHeader = resp.getHeader("Accept");
            if (acceptHeader != null) {
                String[] entries = acceptHeader.split("[,]");
                for (String entry : entries) {
                    if (entry.trim().isEmpty()) {
                        continue;
                    }

                    try {
                        MediaType mediaType = MediaType.parse(entry.trim());
                        Serializer tmp = serializerProvider.getSerializer(mediaType.type() + "/" + mediaType.subtype());
                        if (tmp != null) {
                            responseSerializer = tmp;
                            break;
                        }
                    } catch (IllegalArgumentException ignore) {
                        // Ignore. Bad header input is pretty common.
                    }
                }
                if (responseSerializer == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No supported accept media-type: " + acceptHeader);
                    return;
                }
            }

            MessageReader reader = new IOMessageReader(req.getInputStream(), requestSerializer);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MessageWriter writer = new IOMessageWriter(baos, responseSerializer);

            // Create a new processor handler instance for each request, as
            // they may be request context dependent. E.g. depends on
            // information in header, servlet context etc.
            new DefaultProcessorHandler(processor).process(reader, writer);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(responseSerializer.mimeType());
            resp.setContentLength(baos.size());
            resp.getOutputStream().write(baos.toByteArray());
        } catch (Exception e) {
            LOGGER.error("Exception in service call for " + processor.getDescriptor().getQualifiedName(), e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error: " + e.getMessage());
        }
    }
}
