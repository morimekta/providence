package net.morimekta.providence.testing.generator.defaults;

import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default generator for set fields.
 */
public class SetGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context, Set<Object>> {
    private final PSet<Object>            set;

    public SetGenerator(PSet<Object> set) {
        this.set = set;
    }

    @Override
    public Set<Object> generate(Context ctx) {
        int num = ctx.nextDefaultCollectionSize();
        Generator<Context, ?> generator = ctx.generatorFor(set.itemDescriptor());

        // Sets does not necessary allow conflicting items.
        Set<Object> builder = new LinkedHashSet<>();
        for (int i = 0; i < num; ++i) {
            builder.add(generator.generate(ctx));
        }
        return set.builder()
                  .addAll(builder)
                  .build();
    }
}
