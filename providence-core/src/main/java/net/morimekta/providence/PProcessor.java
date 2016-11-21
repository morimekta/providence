package net.morimekta.providence;

import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.SerializerException;

import java.io.IOException;

/**
 * Service processor is an extension to the service call handler that can
 * provide it's own service definition. This is the base interface for
 * the handlers on the server side processing of a providence call.
 */
public interface PProcessor extends PServiceCallHandler {
    /**
     * Get the descriptor for the given service.
     *
     * @return The service descriptor.
     */
    PService getDescriptor();

    /**
     * Handle a service call.
     *
     * @param call The request call.
     * @param <Request> Request type.
     * @param <Response> Response type.
     * @param <RequestField> Request type.
     * @param <ResponseField> Response type.
     * @return The response service call object, or null if none (e.g. oneway).
     * @throws IOException On read or write failure.
     * @throws SerializerException On serialization problems.
     */
    default <Request extends PMessage<Request, RequestField>,
             Response extends PMessage<Response, ResponseField>,
             RequestField extends PField,
             ResponseField extends PField>
    PServiceCall<Response, ResponseField> handleCall(PServiceCall<Request, RequestField> call)
            throws IOException {
        return handleCall(call, getDescriptor());
    }
}
