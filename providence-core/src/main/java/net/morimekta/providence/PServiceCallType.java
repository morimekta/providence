package net.morimekta.providence;

/**
 * Service method call type.
 */
public enum PServiceCallType {
    CALL(1, true),
    REPLY(2, false),
    EXCEPTION(3, false),
    ONEWAY(4, true),
    ;

    /**
     * Reue if the associated message is the method request struct. If false
     * it is the message response struct.
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

    public static PServiceCallType findByKey(int key) {
        switch (key) {
            case 1: return CALL;
            case 2: return REPLY;
            case 3: return EXCEPTION;
            case 4: return ONEWAY;
        }
        return null;
    }
}
