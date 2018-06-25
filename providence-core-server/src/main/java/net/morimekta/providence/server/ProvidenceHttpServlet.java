package net.morimekta.providence.server;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.DefaultSerializerProvider;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.SerializerProvider;

import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

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
     *
     * @param httpRequest The HTTP request.
     * @param request The parsed providence request.
     * @param <T> Thrown exception type.
     * @return The response object.
     * @throws T Any exception thrown.
     */
    @Nonnull
    protected abstract <T extends Throwable>
    RS handle(HttpServletRequest httpRequest, RQ request) throws T;

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
        return 500;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RS response;
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
        String acceptHeader = req.getHeader("Accept");
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
            request = requestSerializer.deserialize(req.getInputStream(), requestDescriptor);
        } catch (SerializerException e) {
            LOGGER.info("Failed to deserialize request to {}: {}", req.getServletPath(), e.asString(), e);

            PApplicationException ex = PApplicationException.builder()
                                                            .setId(PApplicationExceptionType.INVALID_PROTOCOL)
                                                            .setMessage(e.getMessage())
                                                            .build();
            resp.setStatus(400);
            resp.setContentType(responseSerializer.mediaType());
            responseSerializer.serialize(resp.getOutputStream(), ex);
            return;
        }

        try {
            response = handle(req, request);
        } catch (Exception e) {
            Throwable rex = getResponseException(e);
            if (rex instanceof PMessage) {
                PMessage mex = (PMessage) rex;
                resp.setStatus(statusCodeForException(rex));
                resp.setContentType(responseSerializer.mediaType());
                responseSerializer.serialize(resp.getOutputStream(), mex);
            } else {
                resp.sendError(statusCodeForException(rex), rex.getMessage());
            }

            return;
        }

        resp.setStatus(200);
        resp.setContentType(responseSerializer.mediaType());
        responseSerializer.serialize(resp.getOutputStream(), response);
    }
}
