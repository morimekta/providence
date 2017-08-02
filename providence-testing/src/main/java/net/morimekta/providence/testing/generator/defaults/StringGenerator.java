package net.morimekta.providence.testing.generator.defaults;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

/**
 * Default generator for string fields.
 */
public class StringGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context,String> {
    @Override
    public String generate(Context ctx) {
        return ctx.getFairy().textProducer().sentence();
    }
}
