package net.morimekta.providence.tools.compiler.options;

import net.morimekta.providence.generator.GeneratorFactory;

import java.util.Collection;

/**
 * Convert params for input or output of providence data.
 */
public class GeneratorSpec {
    // expected appendEnumClass.
    public final GeneratorFactory   factory;
    public final Collection<String> options;

    public GeneratorSpec(GeneratorFactory factory, Collection<String> options) {
        this.factory = factory;
        this.options = options;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(factory.generatorName());
        boolean first = true;
        for (String option : options) {
            if (first) {
                first = false;
                builder.append(':');
            } else {
                builder.append(',');
            }
            builder.append(option);
        }
        return builder.toString();
    }
}
