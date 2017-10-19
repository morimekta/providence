package net.morimekta.providence.testing.generator.defaults;

import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Default generator for a list field.
 */
public class ListGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context, List<Object>> {
    private final PList<Object> list;

    public ListGenerator(PList<Object> list) {
        this.list = list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> generate(Context ctx) {
        int num = ctx.nextDefaultCollectionSize();
        Generator generator = ctx.generatorFor(list.itemDescriptor());

        // Lists does not necessary allow conflicting items.
        List<Object> builder = new ArrayList<>(num);
        for (int i = 0; i < num; ++i) {
            builder.add(generator.generate(ctx));
        }
        return list.builder()
                   .addAll(builder)
                   .build();
    }
}
