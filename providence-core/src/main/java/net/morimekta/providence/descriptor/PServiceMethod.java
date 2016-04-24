package net.morimekta.providence.descriptor;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PUnion;

/**
 * Descriptor for a single service method.
 */
public interface PServiceMethod<
        P extends PMessage<P>, PF extends PField,
        R extends PUnion<R>, RF extends PField> {
    String getName();

    boolean isOneway();

    PStructDescriptor<P, PF> getRequestType();

    PUnionDescriptor<R, RF> getResponseType();
}
