package net.morimekta.providence.testing.generator.defaults;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

/**
 * Default generator for short fields.
 */
public class ShortGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context, Short> {
    @Override
    public Short generate(Context ctx) {
        return (short) ctx.getRandom().nextInt();
    }
}
