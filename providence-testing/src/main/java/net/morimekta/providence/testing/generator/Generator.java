package net.morimekta.providence.testing.generator;

/**
 * Basic generator interface.
 */
@FunctionalInterface
public interface Generator<Context extends GeneratorContext<Context>, T> {
    /**
     * @param context The context to use when generating.
     * @return The generated value.
     */
    T generate(Context context);
}
