package net.morimekta.providence.tools.compiler.options;

import net.morimekta.providence.generator.Language;

import java.util.Collection;

/**
 * Convert params for input or output of providence data.
 */
public class GeneratorSpec {
    // expected appendEnumClass.
    public final Language           generator;
    public final Collection<String> options;

    public GeneratorSpec(Language generator, Collection<String> options) {
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
