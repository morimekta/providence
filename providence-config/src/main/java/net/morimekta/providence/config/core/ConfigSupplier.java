package net.morimekta.providence.config.core;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * A supplier and instance holder for config objects. This supplier can be
 * listened to for changes in the config object. When something triggers
 * a change (<code>supplier.set(config)</code>) that will cause a config
 * change call to each listener regardless of if the config values actually
 * did change.
 */
public class ConfigSupplier<M extends PMessage<M,F>, F extends PField> implements Supplier<M> {
    private final AtomicReference<M>       instance;
    private final LinkedList<WeakReference<ConfigListener<M,F>>> listeners;

    /**
     * Initialize supplier with empty config.
     */
    public ConfigSupplier() {
        this.instance = new AtomicReference<>();
        this.listeners = new LinkedList<>();
    }

    /**
     * Initialize with an initial config instance.
     *
     * @param initialConfig The initial config instance.
     */
    public ConfigSupplier(@Nonnull M initialConfig) {
        this();
        this.instance.set(initialConfig);
    }

    @Nonnull
    @Override
    public final M get() {
        synchronized (this) {
            M config = instance.get();
            if (config == null) {
                throw new IllegalStateException("No config instance");
            }
            return config;
        }
    }

    /**
     * Add a listener to changes to this config. Note that this will store a
     * weak reference to the listener instance, so the one adding the listener
     * must make sure the listener is not GC'd.
     *
     * @param listener The config change listener to be added.
     */
    public void addListener(ConfigListener<M, F> listener) {
        synchronized (this) {
            listeners.removeIf(ref -> ref.get() == listener || ref.get() == null);
            listeners.add(new WeakReference<>(listener));
        }
    }

    /**
     * Remove a config change listener.
     *
     * @param listener The config change listener to be removed.
     */
    public void removeListener(ConfigListener<M,F> listener) {
        synchronized (this) {
            listeners.removeIf(ref -> ref.get() == null || ref.get() == listener);
        }
    }

    /**
     * Set a new config value to the supplier.
     *
     * @param config The new config instance.
     */
    public final void set(M config) {
        LinkedList<WeakReference<ConfigListener<M,F>>> iterateOver;
        synchronized (this) {
            instance.set(config);
            listeners.removeIf(Objects::isNull);
            iterateOver = new LinkedList<>(listeners);
        }
        iterateOver.forEach(ref -> {
            ConfigListener<M,F> listener = ref.get();
            if (listener != null) {
                try {
                    listener.onConfigChange(config);
                } catch (Exception ignore) {
                    // Ignored... TODO: At least log?
                }
            }
        });
    }
}
