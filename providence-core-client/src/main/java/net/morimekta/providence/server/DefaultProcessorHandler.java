package net.morimekta.providence.server;

import net.morimekta.providence.PProcessor;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallHandler;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.serializer.ApplicationException;
import net.morimekta.providence.serializer.ApplicationExceptionType;
import net.morimekta.providence.serializer.SerializerException;

import java.io.IOException;

/**
 * Service processor wraps the service' Processor implementation of the
 * {@link PServiceCallHandler} so that it can read the service call message
 * parsed into a {@link PServiceCall}, and write the response with proper
 * exception handling.
 */
public class DefaultProcessorHandler implements ProcessorHandler {
    private final PProcessor processor;

    /**
     * Wrap a service with a service call handler to work as the server side
     * of a providence service.
     *
     * @param processor The service call handler.
     */
    public DefaultProcessorHandler(PProcessor processor) {
        this.processor = processor;
    }

    @Override
    public boolean process(MessageReader reader, MessageWriter writer) throws IOException {
        PServiceCall call, response, reply;
        try {
            try {
                call = reader.read(processor.getDescriptor());
            } catch (SerializerException e) {
                ApplicationException oe = ApplicationException.builder()
                                                              .setMessage(e.getMessage())
                                                              .setId(ApplicationExceptionType.INVALID_PROTOCOL)
                                                              .build();
                reply = new PServiceCall<>(e.getMethodName(), PServiceCallType.EXCEPTION, e.getSequenceNo(), oe);
                try {
                    writer.write(reply);
                    return false;
                } catch (IOException | SerializerException e2) {
                    IOException e3 = new IOException(e.getMessage(), e);
                    e3.addSuppressed(e2);
                    throw e3;
                }
            }

            try {
                response = processor.handleCall(call);
            } catch (IOException | SerializerException e) {
                ApplicationException oe = ApplicationException.builder()
                                                              .setMessage(e.getMessage())
                                                              .setId(ApplicationExceptionType.INTERNAL_ERROR)
                                                              .build();
                reply = new PServiceCall<>(call.getMethod(), PServiceCallType.EXCEPTION, call.getSequence(), oe);
                try {
                    writer.write(reply);
                    return false;
                } catch (IOException | SerializerException e2) {
                    IOException e3 = new IOException(e.getMessage(), e);
                    e3.addSuppressed(e2);
                    throw e3;
                }
            }

            if (response != null) {
                try {
                    writer.write(response);
                } catch (SerializerException e) {
                    ApplicationException oe = ApplicationException.builder()
                                                                  .setMessage(e.getMessage())
                                                                  .setId(ApplicationExceptionType.INVALID_TRANSFORM)
                                                                  .build();
                    reply = new PServiceCall<>(call.getMethod(), PServiceCallType.EXCEPTION, call.getSequence(), oe);
                    try {
                        writer.write(reply);
                        // Even though the method returned, we didn't return the proper response.
                        return false;
                    } catch (Exception e2) {
                        IOException e3 = new IOException(e.getMessage(), e);
                        e3.addSuppressed(e2);
                        throw e3;
                    }
                }
            }

            return true;
        } catch (Exception e) {
            // Unable to handle exception as part of the message channel, must rethrow.
            throw new IOException(e.getMessage(), e);
        }
    }
}
