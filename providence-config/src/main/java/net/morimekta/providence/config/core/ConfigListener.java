package net.morimekta.providence.config.core;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;

/**
 * Interface for handling reactions to update of a config message.
 */
@FunctionalInterface
public interface ConfigListener<M extends PMessage<M,F>, F extends PField> {
    /**
     * Called when the config is updated. Does not necessary mean the config
     * did change it's values.
     *
     * @param config The new config instance.
     */
    void onConfigChange(@Nonnull M config);
}
