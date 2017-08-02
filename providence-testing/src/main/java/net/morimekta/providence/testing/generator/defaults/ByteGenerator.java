package net.morimekta.providence.testing.generator.defaults;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

/**
 * Default generator for byte (i8) fields.
 */
public class ByteGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context, Byte> {
    @Override
    public Byte generate(Context ctx) {
        return (byte) ctx.getRandom().nextInt();
    }
}
