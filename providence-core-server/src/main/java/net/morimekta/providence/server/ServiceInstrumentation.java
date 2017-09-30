package net.morimekta.providence.server;

import net.morimekta.providence.PServiceCall;

import javax.annotation.Nullable;

/**
 * Interface handling the instrumentation of service calls.
 */
@FunctionalInterface
public interface ServiceInstrumentation {
    /**
     * After each service call this method is called with the duration, call and
     * response objects. Exceptions from the call is ignored, and will not affect
     * the response in any way.
     *
     * @param duration The duration of handling the service call in milliseconds,
     *                 including receiving and sending it. It does not include the
     *                 time receiving the first HTTP packet with the headers, or
     *                 waiting for free worker threads.
     * @param call     The call triggered.
     * @param response The response returned.
     */
    void afterCall(double duration,
                   @Nullable PServiceCall call,
                   @Nullable PServiceCall response);
}
