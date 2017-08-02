package net.morimekta.providence.testing.generator.extra;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

import static java.lang.Math.abs;

/**
 * Default generator for selecting one of a set of values of the same type..
 */
public class ShortRangeGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context,Short> {
    private final short fromIncluding;
    private final short toExcluding;

    public ShortRangeGenerator(short fromIncluding, short toExcluding) {
        this.fromIncluding = fromIncluding;
        this.toExcluding = toExcluding;

        if (fromIncluding >= toExcluding) {
            throw new AssertionError("Invalid range [ " + fromIncluding + " .. " + toExcluding + " >");
        }
    }

    @Override
    public Short generate(Context ctx) {
        return (short) (fromIncluding + abs(ctx.getRandom().nextInt() % (toExcluding - fromIncluding)));
    }
}
