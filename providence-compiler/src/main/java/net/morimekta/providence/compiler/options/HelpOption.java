package net.morimekta.providence.compiler.options;

import net.morimekta.providence.generator.Language;

/**
 * Convert params for input or output of providence data.
 */
public class HelpOption {
    // language to print help about.
    public final Language generator;

    public HelpOption(Language generator) {
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
