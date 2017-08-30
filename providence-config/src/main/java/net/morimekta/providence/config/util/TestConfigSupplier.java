package net.morimekta.providence.config.util;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.config.impl.UpdatingConfigSupplier;
import net.morimekta.providence.descriptor.PField;

import com.google.common.annotations.VisibleForTesting;

import javax.annotation.Nonnull;
import java.time.Clock;

/**
 * Config supplier meant for testing only. It is an updating config supplier, but that
 * exposes the config update method itself.
 *
 * @param <M> The message type.
 * @param <F> The message field type.
 */
@VisibleForTesting
public class TestConfigSupplier<M extends PMessage<M,F>, F extends PField> extends UpdatingConfigSupplier<M,F> {
    /**
     * Start with no initial config. This is usually now allowed for "normal"
     * config suppliers.
     */
    public TestConfigSupplier() {
    }

    /**
     * Start with no initial config. This is usually now allowed for "normal"
     * config suppliers.
     *
     * @param clock The clock to use for timing.
     */
    public TestConfigSupplier(@Nonnull Clock clock) {
        super(clock);
    }

    /**
     * Start with an initial config value.
     *
     * @param clock The clock to use for timing.
     * @param initialConfig The initial config value.
     */
    public TestConfigSupplier(@Nonnull Clock clock, @Nonnull M initialConfig) {
        super(clock);
        set(initialConfig);
    }

    /**
     * Start with an initial config value.
     *
     * @param initialConfig The initial config value.
     */
    public TestConfigSupplier(@Nonnull M initialConfig) {
        set(initialConfig);
    }

    /**
     * Update the current config and trigger updates.
     *
     * @param newInstance The new config instance.
     */
    public void testUpdate(@Nonnull M newInstance) {
        set(newInstance);
    }

    @Override
    public String toString() {
        return getName() + "{}";
    }

    @Override
    public String getName() {
        return "TestConfig";
    }
}
