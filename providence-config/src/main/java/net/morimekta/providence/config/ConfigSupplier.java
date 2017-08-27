package net.morimekta.providence.config;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import java.util.function.Supplier;

/**
 * A supplier and instance holder for config objects. This supplier can be
 * listened to for changes in the config object. When something triggers
 * a change (<code>supplier.set(config)</code>) that will cause a config
 * change call to each listener regardless of if the config values actually
 * did change.
 */
public interface ConfigSupplier<M extends PMessage<M,F>, F extends PField> extends Supplier<M> {
    /**
     * Add a listener to changes to this config. Note that this will store a
     * weak reference to the listener instance, so the one adding the listener
     * must make sure the listener is not GC'd.
     *
     * @param listener The config change listener to be added.
     */
    void addListener(ConfigListener<M, F> listener);

    /**
     * Remove a config change listener.
     *
     * @param listener The config change listener to be removed.
     */
    void removeListener(ConfigListener<M,F> listener);

    /**
     * Get a simple descriptive name for this config supplier.
     *
     * @return The supplier name.
     */
    String getName();

    /**
     * Get the last update time as a millisecond timestamp.
     *
     * @return The timestamp of last update of the config.
     */
    long configTimestamp();

    default ConfigSupplier<M,F> snapshot() {
        synchronized (this) {
            return new FixedConfigSupplier<>(get(), configTimestamp());
        }
    }
}
