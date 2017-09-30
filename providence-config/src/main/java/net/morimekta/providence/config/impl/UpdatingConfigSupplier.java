package net.morimekta.providence.config.impl;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.config.ConfigListener;
import net.morimekta.providence.config.ConfigSupplier;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.time.Clock;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A supplier and instance holder for config objects. This supplier can be
 * listened to for changes in the config object. When something triggers
 * a change (<code>supplier.set(config)</code>) that will cause a config
 * change call to each listener regardless of if the config values actually
 * did change.
 */
public abstract class UpdatingConfigSupplier<M extends PMessage<M,F>, F extends PField> implements ConfigSupplier<M,F> {
    private final AtomicReference<M>       instance;
    private final LinkedList<WeakReference<ConfigListener<M,F>>> listeners;
    private final Clock clock;
    private final AtomicLong lastUpdateTimestamp;

    /**
     * Initialize supplier with empty config.
     */
    protected UpdatingConfigSupplier() {
        this(Clock.systemUTC());
    }

    /**
     * Initialize supplier with empty config.
     *
     * @param clock The clock to use in timing config loads.
     */
    protected UpdatingConfigSupplier(Clock clock) {
        this.instance = new AtomicReference<>();
        this.listeners = new LinkedList<>();
        this.clock = clock;
        this.lastUpdateTimestamp = new AtomicLong(0L);
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

    @Override
    public void addListener(ConfigListener<M, F> listener) {
        synchronized (this) {
            listeners.removeIf(ref -> ref.get() == listener || ref.get() == null);
            listeners.add(new WeakReference<>(listener));
        }
    }

    @Override
    public void removeListener(ConfigListener<M,F> listener) {
        synchronized (this) {
            listeners.removeIf(ref -> ref.get() == null || ref.get() == listener);
        }
    }

    @Override
    public long configTimestamp() {
        return lastUpdateTimestamp.get();
    }

    /**
     * Set a new config value to the supplier. This is protected as it is
     * usually up to the supplier implementation to enable updating the
     * config at later stages.
     *
     * @param config The new config instance.
     */
    protected final void set(M config) {
        LinkedList<WeakReference<ConfigListener<M,F>>> iterateOver;
        synchronized (this) {
            if (instance.get() != null && instance.get().equals(config)) {
                return;
            }

            instance.set(config);
            lastUpdateTimestamp.set(clock.millis());
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
