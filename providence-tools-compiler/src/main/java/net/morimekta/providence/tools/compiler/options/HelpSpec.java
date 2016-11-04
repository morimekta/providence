package net.morimekta.providence.tools.compiler.options;

import net.morimekta.providence.generator.Language;

/**
 * Convert params for input or output of providence data.
 */
public class HelpSpec {
    // language to print help about.
    public final Language generator;

    public HelpSpec(Language generator) {
        this.generator = generator;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("help");
        if (generator != null) {
            builder.append(':').append(generator.name());
        }
        return builder.toString();
    }
}
