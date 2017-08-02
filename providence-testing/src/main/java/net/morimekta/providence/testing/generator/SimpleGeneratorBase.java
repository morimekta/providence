package net.morimekta.providence.testing.generator;

/**
 * Simple generator base that holds so special context.
 *
 * This is mainly a separate class to get rid of unneeded generics.
 */
public final class SimpleGeneratorBase
        extends GeneratorBase<SimpleGeneratorBase, SimpleGeneratorContext> {
    /**
     * Default constructor.
     */
    public SimpleGeneratorBase() {}

    /**
     * Copy constructor.
     *
     * @param copyOf Copy of instance.
     */
    @SuppressWarnings("unused")
    public SimpleGeneratorBase(SimpleGeneratorBase copyOf) {
        super(copyOf);
    }

    @Override
    public SimpleGeneratorContext createContext() {
        return new SimpleGeneratorContext(this);
    }
}
