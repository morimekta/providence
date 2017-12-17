package net.morimekta.providence.tools.compiler.options;

import net.morimekta.providence.generator.GeneratorFactory;

/**
 * Convert params for input or output of providence data.
 */
public class HelpSpec {
    // language to print help about.
    public final GeneratorFactory factory;

    public HelpSpec(GeneratorFactory factory) {
        this.factory = factory;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("help");
        if (factory != null) {
            builder.append(':').append(factory.generatorName());
        }
        return builder.toString();
    }
}
