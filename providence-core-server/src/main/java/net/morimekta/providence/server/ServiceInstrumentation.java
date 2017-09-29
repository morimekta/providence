package net.morimekta.providence.server;

import net.morimekta.providence.PServiceCall;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface handling the instrumentation of service calls.
 */
@FunctionalInterface
public interface ServiceInstrumentation {
    /**
     * @param call The call triggered.
     * @param response The response returned;
     * @param duration The duration of handling the service call in milliseconds,
     *                including receiving and sending it.
     */
    void afterCall(@Nonnull PServiceCall call,
                   @Nullable PServiceCall response,
                   double duration);
}
