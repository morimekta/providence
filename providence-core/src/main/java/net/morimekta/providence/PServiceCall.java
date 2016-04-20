package net.morimekta.providence;

/**
 * Descriptor for a single service method.
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

    public String getMethod() {
        return method;
    }

    public PServiceCallType getType() {
        return type;
    }

    public int getSequence() {
        return sequence;
    }

    public T getMessage() {
        return message;
    }
}
