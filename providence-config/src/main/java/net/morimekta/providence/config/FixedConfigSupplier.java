package net.morimekta.providence.config;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.util.Objects;

/**
 * A supplier and instance holder for an immutable config instance.
 */
public class FixedConfigSupplier<M extends PMessage<M,F>, F extends PField> implements ConfigSupplier<M,F> {
    private final M instance;
    private final long timestamp;

    /**
     * Initialize with an initial config instance.
     *
     * @param initialConfig The initial config instance.
     */
    public FixedConfigSupplier(@Nonnull M initialConfig) {
        this(initialConfig, Clock.systemUTC().millis());
    }

    /**
     * This essentially makes a static snapshot of the config and keeps the
     * config instance as a fixed (unmodifiable) config.
     *
     * @param supplier The config supplier to copy.
     */
    public FixedConfigSupplier(@Nonnull ConfigSupplier<M, F> supplier) {
        synchronized (Objects.requireNonNull(supplier)) {
            this.instance = supplier.get();
            this.timestamp = supplier.configTimestamp();
        }
    }

    /**
     * Initialize with an initial config instance.
     *
     * @param initialConfig The initial config instance.
     * @param timestamp The config timestamp.
     */
    public FixedConfigSupplier(@Nonnull M initialConfig, long timestamp) {
        this.instance = initialConfig;
        this.timestamp = timestamp;
    }

    @Nonnull
    @Override
    public final M get() {
        return instance;
    }

    @Override
    public final void addListener(@Nonnull ConfigListener<M, F> listener) {
    }

    @Override
    public final void removeListener(@Nonnull ConfigListener<M,F> listener) {
    }

    @Override
    public long configTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return "InMemoryConfig";
    }
}
