package net.morimekta.providence.testing.generator.defaults;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

/**
 * Default generator for bool fields.
 */
public class BoolGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context, Boolean> {
    @Override
    public Boolean generate(Context ctx) {
        return ctx.getRandom().nextBoolean();
    }
}
