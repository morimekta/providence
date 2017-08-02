package net.morimekta.providence.testing.generator;

/**
 * Simple non-generic watcher with no extra context on message generation.
 */
public final class SimpleGeneratorWatcher
        extends GeneratorWatcher<SimpleGeneratorBase,SimpleGeneratorContext> {
    public static SimpleGeneratorWatcher create() {
        return new SimpleGeneratorWatcher(new SimpleGeneratorBase());
    }

    /**
     * Make a simple default message generator.
     * @param baseContext The base context.
     */
    private SimpleGeneratorWatcher(SimpleGeneratorBase baseContext) {
        super(baseContext);
    }
}
