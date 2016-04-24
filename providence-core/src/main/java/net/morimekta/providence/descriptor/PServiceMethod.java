package net.morimekta.providence.descriptor;

/**
 * Descriptor for a single service method.
 */
public interface PServiceMethod {
    String getName();

    boolean isOneway();

    PStructDescriptor getRequestType();

    PUnionDescriptor getResponseType();
}
