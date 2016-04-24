package net.morimekta.providence.compiler;

import java.util.Collection;

/**
 * Convert params for input or output of providence data.
 */
public class GeneratorOptions {
    // expected format.
    public final GeneratorSpec generator;
    public final Collection<String> options;

    public GeneratorOptions(GeneratorSpec generator, Collection<String> options) {
        this.generator = generator;
        this.options = options;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(generator.name());
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
