package net.morimekta.providence;

import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.SerializerException;

import java.io.IOException;

/**
 * Interface for handling a call request from a synchronous client.
 */
@FunctionalInterface
public interface PServiceCallHandler {
    /**
     * Handle a service call.
     *
     * @param call The request call.
     * @param service The service to be handled.
     * @param <Request> Request type.
     * @param <Response> Response type.
     * @param <RequestField> Request type.
     * @param <ResponseField> Response type.
     * @return The response service call object, or null if none (e.g. oneway).
     * @throws IOException On read or write failure.
     * @throws SerializerException On serialization problems.
     */
    <       Request extends PMessage<Request, RequestField>,
            Response extends PMessage<Response, ResponseField>,
            RequestField extends PField,
            ResponseField extends PField>
    PServiceCall<Response, ResponseField> handleCall(PServiceCall<Request, RequestField> call, PService service)
            throws IOException, SerializerException;
}
