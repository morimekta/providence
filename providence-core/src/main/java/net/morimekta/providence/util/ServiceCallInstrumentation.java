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
    void afterCall(double duration,
                   @Nullable PServiceCall call,
                   @Nullable PServiceCall reply);

    /**
     * Handy constant for calculating MS duration.
     */
    long NS_IN_MILLIS = TimeUnit.MILLISECONDS.toNanos(1);
}
