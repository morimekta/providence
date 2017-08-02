package net.morimekta.providence.testing.generator.defaults;

import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default generator for map fields.
 */
public class MapGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context, Map<Object,Object>> {
    private final PMap<Object,Object>     map;

    public MapGenerator(PMap<Object,Object> map) {
        this.map = map;
    }

    @Override
    public Map<Object, Object> generate(Context ctx) {
        int num = ctx.nextDefaultCollectionSize();
        Generator<Context, ?> keyGenerator = ctx.generatorFor(map.keyDescriptor());
        Generator<Context, ?> itemGenerator = ctx.generatorFor(map.itemDescriptor());

        // Sets does not necessary allow conflicting items.
        Map<Object,Object> builder = new LinkedHashMap<>();
        for (int i = 0; i < num; ++i) {
            builder.put(keyGenerator.generate(ctx), itemGenerator.generate(ctx));
        }
        return map.builder()
                  .putAll(builder)
                  .build();
    }
}
