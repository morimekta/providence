package net.morimekta.providence.testing.generator.defaults;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

/**
 * Default generator for an int field.
 */
public class IntGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context, Integer> {
    @Override
    public Integer generate(Context ctx) {
        return ctx.getRandom().nextInt();
    }
}
