package net.morimekta.providence.compiler.options;

import net.morimekta.providence.generator.Language;

/**
 * Convert params for input or output of providence data.
 */
public class HelpOptions {
    // language to print help about.
    public final Language generator;

    public HelpOptions(Language generator) {
        this.generator = generator;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("help(");
        if (generator != null) {
            builder.append(generator.name());
        }
        builder.append(")");
        return builder.toString();
    }
}
