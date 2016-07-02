package net.morimekta.providence.descriptor;

/**
 * Descriptor for a single service method.
 */
@FunctionalInterface
public interface PServiceProvider {
    PService getService();
}
