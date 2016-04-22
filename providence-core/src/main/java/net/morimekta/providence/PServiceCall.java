package net.morimekta.providence;

/**
 * Enclosed call to a service method.
 */
public class PServiceCall<T extends PMessage<T>> {
    private final String method;
    private final PServiceCallType type;
    private final int sequence;
    private final T message;

    public PServiceCall(String method,
                        PServiceCallType type,
                        int sequence,
                        T message) {
        this.method = method;
        this.type = type;
        this.sequence = sequence;
        this.message = message;
    }

    /**
     * The name of the method called.
     *
     * @return Name of method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * The type of service call.
     *
     * @return Type of call.
     */
    public PServiceCallType getType() {
        return type;
    }

    /**
     * The sequence number of the call. Can be used to
     * match responses with associated calls.
     *
     * @return Sequence Number.
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * The message sent or received. This should map to the request or response
     * message struct / union generated for the method being calles.
     *
     * @return The sent message.
     */
    public T getMessage() {
        return message;
    }
}
