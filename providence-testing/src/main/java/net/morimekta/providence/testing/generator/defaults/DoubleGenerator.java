package net.morimekta.providence.testing.generator.defaults;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

/**
 * Default generator for double fields.
 */
public class DoubleGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context, Double> {
    @Override
    public Double generate(Context ctx) {
        return ctx.getRandom().nextDouble();
    }
}
