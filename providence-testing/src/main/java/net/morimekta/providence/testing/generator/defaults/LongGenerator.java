package net.morimekta.providence.testing.generator.defaults;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

/**
 * Default generator for a long field.
 */
public class LongGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context, Long> {
    @Override
    public Long generate(Context ctx) {
        return ctx.getRandom().nextLong();
    }
}
