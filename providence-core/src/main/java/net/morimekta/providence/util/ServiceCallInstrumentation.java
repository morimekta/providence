package net.morimekta.providence.util;

import net.morimekta.providence.PServiceCall;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * Interface handling the instrumentation of service calls.
 */
@FunctionalInterface
public interface ServiceCallInstrumentation {
    /**
     * After each service call this method is called with the duration, call and
     * response objects. Exceptions from the call is ignored, and will not affect
     * the response in any way.
     * <p>
     * Note that the timing may not include the whole stack time since receiving
     * the first packet, that is dependent on the specific implementation and the
     * limitations there. E.g. it does not include the time receiving the first
     * HTTP packet with the headers, or waiting for free worker threads when using
     * <code>ProvidenceServlet</code>.
     *
     * @param duration The duration of handling the service call in milliseconds,
     *                 including receiving and sending it.
     * @param call     The call triggered.
     * @param reply    The reply returned.
     */
    void onComplete(double duration,
                    @Nullable PServiceCall call,
                    @Nullable PServiceCall reply);

    /**
     * Called when the service call failed in the transport layer itself with something
     * not related to the actual service call. E.g. in server side when the read
     * message failed, write back failed etc. {@link #onComplete(double, PServiceCall, PServiceCall)}
     * will NOT be called after the exception call, but will be chained if this method
     * has no override.
     *
     * @param e        The exception thrown.
     * @param duration The duration of handling the service call in milliseconds,
     *                 including receiving and sending it.
     * @param call     The service call.
     * @param reply    The service reply.
     */
    default void onTransportException(Exception e,
                                      double duration,
                                      @Nullable PServiceCall call,
                                      @Nullable PServiceCall reply) {
        onComplete(duration, call, reply);
    }

    /**
     * Handy constant for calculating MS duration.
     */
    long NS_IN_MILLIS = TimeUnit.MILLISECONDS.toNanos(1);
}
