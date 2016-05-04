package net.morimekta.providence;

/**
 * Service method call type.
 */
public enum PServiceCallType {
    /**
     * Call with parameter wrapper.
     */
    CALL(1, true),
    /**
     * Reply with response wrapper with return value or exception.
     */
    REPLY(2, false),
    /**
     * Reply with Application exception.
     */
    EXCEPTION(3, false),
    /**
     * Call with parameter wrapper that does not want a reply (not even for
     * exception).
     */
    ONEWAY(4, true),
    ;

    /**
     * True if the associated message is the method request struct. If false
     * it is the message response struct or application exception.
     */
    public final boolean request;

    /**
     * The call type ID used for recognizing the type of call by the service
     * processor / client.
     */
    public final int key;

    PServiceCallType(int key, boolean request) {
        this.key = key;
        this.request = request;
    }

    @Override
    public  String toString() {
        return name().toLowerCase();
    }

    public static PServiceCallType findByKey(int key) {
        switch (key) {
            case 1: return CALL;
            case 2: return REPLY;
            case 3: return EXCEPTION;
            case 4: return ONEWAY;
        }
        return null;
    }

    public static PServiceCallType findByName(String key) {
        switch (key) {
            case "call": return CALL;
            case "reply": return REPLY;
            case "exception": return EXCEPTION;
            case "oneway": return ONEWAY;
        }
        return null;
    }
}
