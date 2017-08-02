package net.morimekta.providence.testing.generator.defaults;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;
import net.morimekta.util.Binary;

/**
 * Default generator for binary fields.
 */
public class BinaryGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context, Binary> {
    @Override
    public Binary generate(Context ctx) {
        byte[] tmp = new byte[ctx.nextDefaultCollectionSize()];
        ctx.getRandom().nextBytes(tmp);
        return Binary.wrap(tmp);
    }
}
