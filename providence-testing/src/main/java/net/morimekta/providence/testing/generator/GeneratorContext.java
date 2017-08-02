package net.morimekta.providence.testing.generator;

import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptor;

import io.codearte.jfairy.Fairy;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Context for generating messages with information related to each other.
 * Also contains references to basic options affecting the generation
 * like the random generator, and default max collection size.
 */
public abstract class GeneratorContext<Context extends GeneratorContext<Context>> {
    private final GeneratorBase base;

    protected GeneratorContext(GeneratorBase base) {
        this.base = base;
    }

    /**
     * @return The current fairy instance.
     */
    @Nonnull
    public Fairy getFairy() {
        return base.getFairy();
    }

    /**
     * @return The current random instance.
     */
    @Nonnull
    public Random getRandom() {
        return base.getRandom();
    }

    /**
     * @return The default max collection size.
     */
    public int getDefaultMaxCollectionSize() {
        return base.getDefaultMaxCollectionSize();
    }

    /**
     * @return The current default fill rate.
     */
    public double getDefaultFillRate() {
        return base.getDefaultFillRate();
    }

    /**
     * Get the default generator for the given type.
     *
     * @param descriptor The type descriptor.
     * @param <T> The instance type.
     * @return The generator.
     */
    @SuppressWarnings("unchecked")
    public <T> Generator<Context, T> generatorFor(@Nonnull PDeclaredDescriptor<T> descriptor) {
        return base.generatorFor(descriptor);
    }

    /**
     * Get the default generator for the given type.
     *
     * @param descriptor The type descriptor.
     * @param <T> The instance type.
     * @return The generator.
     */
    @SuppressWarnings("unchecked")
    public <T> Generator<Context, T> generatorFor(@Nonnull PDescriptor descriptor) {
        return base.generatorFor(descriptor);
    }

    // -------------------------------------------
    // ----                                   ----
    // ----           SPECIAL CASES           ----
    // ----                                   ----
    // -------------------------------------------

    /**
     * Convenience method to get the next collection size based on the default
     * max collection size.
     *
     * @return The next collection size.
     */
    public int nextDefaultCollectionSize() {
        return getRandom().nextInt(getDefaultMaxCollectionSize());
    }
}
