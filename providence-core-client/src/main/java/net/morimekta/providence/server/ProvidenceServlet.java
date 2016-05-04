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
 * Created by morimekta on 5/4/16.
 */
public class ProvidenceServlet extends HttpServlet {
    private final PProcessor         processor;
    private final SerializerProvider serializerProvider;

    public ProvidenceServlet(PProcessor processor, SerializerProvider serializerProvider) {
        this.processor = processor;
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
                processor.process(reader, writer);
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
