package net.morimekta.providence;

import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.SerializerException;

import java.io.IOException;

/**
 * Interface for handling a call request from a synchronous client.
 */
public interface PClientHandler {
    /**
     * Handle a service call.
     *
     * @param call The request call.
     * @param <RQ> Request type.
     * @param <RS> Response type.
     * @return The response service call object, or null if none (e.g. oneway).
     * @throws IOException On read or write failure.
     * @throws SerializerException On serialization problems.
     */
    <RQ extends PMessage<RQ>, RS extends PMessage<RS>> PServiceCall<RS>
    handleCall(PServiceCall<RQ> call, PService service) throws IOException, SerializerException;
}
