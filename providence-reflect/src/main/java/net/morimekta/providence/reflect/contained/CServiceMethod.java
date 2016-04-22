package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PServiceMethod;
import net.morimekta.providence.descriptor.PStructDescriptor;

/**
 * Descriptor for a single service method.
 */
public class CServiceMethod<
        P extends PMessage<P>, PF extends PField,
        R extends PMessage<R>, RF extends PField> implements PServiceMethod<P, PF, R, RF> {
    private final String                   name;
    private final boolean                  oneway;
    private final PStructDescriptor<P, PF> requestType;
    private final PStructDescriptor<R, RF> responseType;

    public CServiceMethod(String name,
                          boolean oneway,
                          PStructDescriptor<P, PF> requestType,
                          PStructDescriptor<R, RF> responseType) {
        this.name = name;
        this.oneway = oneway;
        this.requestType = requestType;
        this.responseType = responseType;
    }

    public String getName() {
        return name;
    }

    public boolean isOneway() {
        return oneway;
    }

    public PStructDescriptor<P, PF> getRequestType() {
        return requestType;
    }

    public PStructDescriptor<R, RF> getResponseType() {
        return responseType;
    }
}
