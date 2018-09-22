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
package net.morimekta.providence.server;

import com.google.common.net.MediaType;
import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * A simple HTTP POST servlet wrapper that deserializes the POST body
 * as a providence message, and serializes the response message using
 * the requested content type or accept type.
 * <p>
 * Note that the {@link ProvidenceHttpServlet} is <b>NOT</b> usable for
 * thrift services, but is meant to be used to make simple POST based
 * HTTP servlets.
 *
 * <pre>{@code
 * public class MyServlet extends ProvidenceHttpServlet<
 *         MyRequest, MyRequest._Field,
 *         MyResponse, MyResponse._Field> {
 *     {@literal@}Override
 *     protected MyResponse handle(HttpServletRequest httpRequest,
 *                                 MyRequest request)
 *              throws MyException, InternalFailureException {
 *         // ... do stuff with request, or throw MyException or IFE.
 *         return MyResponse.builder()
 *                          .setFieldOut("yes")
 *                          .build();
 *     }
 *
 *     {@literal@}Override
 *     protected int statusCodeForException({@literal@}Nonnull Throwable exception) {
 *         if (exception instanceof MyException) {
 *             return HttpStatus.BAD_REQUEST_400;
 *         }
 *         return super.statusCodeForException(ex);
 *     }
 * }
 * }</pre>
 *
 * This will result in a simple HTTP servlet that can be queries with CURL
 * e.g. like this:
 *
 * <pre>{@code
 * # Simple success
 * $ curl -sS -X POST -d "{\"field_in\": \"value\"}" \
 * >     -H "Content-Type: application/json" \
 * >     localhost:8080/my/servlet
 * {\"field_out\":\"yes\"}
 *
 * # Simple Error
 * $ curl -sSv -X POST -d "{\"field_in\": \"not valid\"}" \
 * >     -H "Content-Type: application/json" \
 * >     localhost:8080/my/servlet
 * ...
 * > Content-Type: application/json
 * > Accept: *{@literal/}*
 * ...
 * < HTTP/1.1 400 Bad Request
 * < Content-Type: application/json
 * ...
 * {\"text\":\"not valid value\"}
 * }</pre>
 *
 * Alternatively you can hijack the whole exception / error response handling, which
 * might be needed where custom headers etc are needed, e.g. with <code>Unauthorized (401)</code>:
 *
 * <pre>{@code
 * public class MyServlet extends ProvidenceHttpServlet&lt;
 *         MyRequest, MyRequest._Field,
 *         MyResponse, MyResponse._Field&gt; {
 *     {@literal@}Override
 *     protected MyResponse handle(HttpServletRequest httpRequest,
 *                                 MyRequest request)
 *              throws MyException, InternalFailureException {
 *         // ... do stuff with request, or throw MyException or IFE.
 *         return MyResponse.builder()
 *                          .setFieldOut("yes")
 *                          .build();
 *     }
 *
 *     {@literal@}Override
 *     protected void handleException({@literal@}Nonnull Throwable exception,
 *                                    {@literal@}Nonnull Serializer responseSerializer,
 *                                    {@literal@}Nonnull HttpServletRequest httpRequest,
 *                                    {@literal@}Nonnull HttpServletResponse httpResponse)
 *             throws IOException {
 *         if (exception instanceof MyException) {
 *             httpResponse.setStatus(HttpStatus.UNAUTHORIZED_401);
 *             httpResponse.setHeader(HttpHeaders.WWW_AUTHENTICATE, "www.my-domain.com");
 *             responseSerializer.serialize(httpResponse.getOutputStream(), (MyException) exception);
 *             return;
 *         }
 *         super.handleException(exception);
 *     }
 * }
 * }</pre>
 *
 * <h3>Overridable Methods</h3>
 *
 * <ul>
 *     <li>
 *         <code>{@link #handle(HttpServletRequest, PMessage)}</code>: The main handle method. This <b>must</b> be
 *         implemented.
 *     </li>
 *     <li>
 *         <code>{@link #handleException(Throwable, Serializer, HttpServletRequest, HttpServletResponse)}</code>:
 *         Complete handling of exceptions thrown by the {@link #handle(HttpServletRequest, PMessage)} method.
 *         Calling super on this will fall back to default exception handling, using the methods below. This
 *         will per default serialize PMessage exceptions normally, and just call {@link HttpServletResponse#sendError(int,String)}
 *         for all the others using the {@link Exception#getMessage()} message.
 *     </li>
 *     <li>
 *         <code>{@link #getResponseException(Throwable)}</code>: Get the response exception given the specific
 *         thrown exception. This method can be used to unwrap wrapped exceptions, or transform non-
 *     </li>
 *     <li>
 *         <code>{@link #statusCodeForException(Throwable)}</code>: Get the HTTP status code to be used for the
 *         error response. Override to specialize, and call super to get default behavior. The default will
 *         handle {@link PApplicationException} errors, and otherwise return <code>500 Internal Server Error</code>.
 *     </li>
 * </ul>
 *
 * @param <RQ> The request type.
 * @param <RQF> The request field type.
 * @param <RS> The response type.
 * @param <RSF> The response field type.
 * @since 1.6.0
 */
public abstract class ProvidenceHttpServlet<
        RQ extends PMessage<RQ, RQF>, RQF extends PField,
        RS extends PMessage<RS, RSF>, RSF extends PField> extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProvidenceHttpServlet.class);

    private final PMessageDescriptor<RQ, RQF> requestDescriptor;
    private final SerializerProvider serializerProvider;

    public ProvidenceHttpServlet(PMessageDescriptor<RQ, RQF> requestDescriptor) {
        this(requestDescriptor, new DefaultSerializerProvider());
    }

    public ProvidenceHttpServlet(PMessageDescriptor<RQ, RQF> requestDescriptor,
                                 SerializerProvider serializerProvider) {
        this.requestDescriptor = requestDescriptor;
        this.serializerProvider = serializerProvider;
    }

    /**
     * Handle the request itself as a simple called method.
     *
     * @param httpRequest The HTTP request.
     * @param request The parsed providence request.
     * @param <T> Thrown exception type.
     * @return The response object.
     * @throws T Any exception thrown.
     */
    @Nonnull
    protected abstract <T extends Throwable>
    RS handle(@Nonnull HttpServletRequest httpRequest,
              @Nonnull RQ request) throws T;

    /**
     * Handle exceptions from the handle method.
     *
     * @param rex The response exception, which is the thrown exception or one
     *            of it's causes. See {@link #getResponseException(Throwable)}.
     * @param responseSerializer The serializer to use to serialize message output.
     * @param httpRequest The HTTP request.
     * @param httpResponse The HTTP response.
     * @throws IOException If writing the response failed.
     */
    @SuppressWarnings("unchecked")
    protected void handleException(@Nonnull Throwable rex,
                                   @Nonnull Serializer responseSerializer,
                                   @Nonnull HttpServletRequest httpRequest,
                                   @Nonnull HttpServletResponse httpResponse) throws IOException {
        if (rex instanceof PMessage) {
            PMessage mex = (PMessage) rex;
            httpResponse.setStatus(statusCodeForException(rex));
            httpResponse.setContentType(responseSerializer.mediaType());
            responseSerializer.serialize(httpResponse.getOutputStream(), mex);
        } else {
            httpResponse.sendError(statusCodeForException(rex), rex.getMessage());
        }
    }

    /**
     * Get the exception to ge handled on failed requests.
     *
     * @param e The exception seen.
     * @return The exception to use as response base.
     */
    @Nonnull
    protected Throwable getResponseException(Throwable e) {
        // Unwrap execution exceptions.
        if (ExecutionException.class.isAssignableFrom(e.getClass())) {
            return getResponseException(e.getCause());
        }
        return e;
    }

    /**
     * With default exception handling, this can simply change the status code used
     * for the response.
     *
     * @param exception The exception seen.
     * @return The status code to be used.
     */
    protected int statusCodeForException(@Nonnull Throwable exception) {
        if (exception instanceof PApplicationException) {
            PApplicationException e = (PApplicationException) exception;
            if (e.getId() == PApplicationExceptionType.INVALID_PROTOCOL ||
                e.getId() == PApplicationExceptionType.PROTOCOL_ERROR ||
                e.getId() == PApplicationExceptionType.BAD_SEQUENCE_ID ||
                e.getId() == PApplicationExceptionType.INVALID_MESSAGE_TYPE) {
                return HttpServletResponse.SC_BAD_REQUEST;
            }
            if (e.getId() == PApplicationExceptionType.UNKNOWN_METHOD) {
                return HttpServletResponse.SC_NOT_FOUND;
            }
        }
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    @Override
    protected final void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        RS response;
        Serializer requestSerializer = serializerProvider.getDefault();
        if (httpRequest.getContentType() != null) {
            try {
                MediaType mediaType = MediaType.parse(httpRequest.getContentType());
                requestSerializer = serializerProvider.getSerializer(mediaType.withoutParameters()
                                                                              .toString());
            } catch (IllegalArgumentException e) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown content-type: " + httpRequest.getContentType());
                LOGGER.warn("Unknown content type in request", e);
                return;
            }
        } else {
            LOGGER.debug("Request is missing content type.");
        }

        Serializer responseSerializer = requestSerializer;
        String acceptHeader = httpRequest.getHeader("Accept");
        if (acceptHeader != null) {
            String[] entries = acceptHeader.split("[,]");
            for (String entry : entries) {
                entry = entry.trim();
                if (entry.isEmpty()) {
                    continue;
                }
                if ("*/*".equals(entry)) {
                    // Then responding same as request is good.
                    break;
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

        RQ request;
        try {
            request = requestSerializer.deserialize(httpRequest.getInputStream(), requestDescriptor);
        } catch (SerializerException e) {
            LOGGER.info("Failed to deserialize request to {}: {}", httpRequest.getServletPath(), e.asString(), e);

            PApplicationException ex = PApplicationException.builder()
                                                            .setId(PApplicationExceptionType.INVALID_PROTOCOL)
                                                            .setMessage(e.getMessage())
                                                            .build();
            httpResponse.setStatus(statusCodeForException(ex));
            httpResponse.setContentType(responseSerializer.mediaType());
            responseSerializer.serialize(httpResponse.getOutputStream(), ex);
            return;
        }

        try {
            response = handle(httpRequest, request);
        } catch (Exception e) {
            try {
                Throwable rex = getResponseException(e);
                handleException(rex, responseSerializer, httpRequest, httpResponse);
                if (!httpResponse.isCommitted()) {
                    httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
            } catch (Exception e1) {
                LOGGER.error("Exception sending error", e1);
                if (!httpResponse.isCommitted()) {
                    httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e1.getMessage());
                }
            }
            return;
        }

        httpResponse.setStatus(HttpServletResponse.SC_OK);
        httpResponse.setContentType(responseSerializer.mediaType());
        responseSerializer.serialize(httpResponse.getOutputStream(), response);
    }
}
