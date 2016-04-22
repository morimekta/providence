package net.morimekta.providence.descriptor;

import net.morimekta.providence.PMessage;

/**
 * Descriptor for a single service method.
 */
public interface PServiceMethod<
        P extends PMessage<P>, PF extends PField,
        R extends PMessage<R>, RF extends PField> {
    String getName();

    boolean isOneway();

    PStructDescriptor<P, PF> getRequestType();

    PStructDescriptor<R, RF> getResponseType();
}
