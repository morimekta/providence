package net.morimekta.providence;

/**
 * Descriptor for a single service method.
 */
public enum PServiceCallType {
    CALL(0, true),
    ONEWAY(1, true),
    RESPONSE(2, false),
    EXCEPTION(3, false),
    ;

    public final boolean request;
    public final int key;

    PServiceCallType(int key, boolean request) {
        this.key = key;
        this.request = request;
    }

    public static PServiceCallType findByKey(int key) {
        switch (key) {
            case 0: return CALL;
            case 1: return ONEWAY;
            case 2: return RESPONSE;
            case 3: return EXCEPTION;
        }
        return null;
    }
}
