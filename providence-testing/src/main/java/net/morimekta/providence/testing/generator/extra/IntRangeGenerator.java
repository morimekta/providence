package net.morimekta.providence.testing.generator.extra;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

import static java.lang.Math.abs;

/**
 * Default generator for selecting one of a set of values of the same type..
 */
public class IntRangeGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context,Integer> {
    private final int fromIncluding;
    private final int toExcluding;

    public IntRangeGenerator(int fromIncluding, int toExcluding) {
        this.fromIncluding = fromIncluding;
        this.toExcluding = toExcluding;

        if (fromIncluding >= toExcluding) {
            throw new AssertionError("Invalid range [ " + fromIncluding + " .. " + toExcluding + " >");
        }
    }

    @Override
    public Integer generate(Context ctx) {
        return fromIncluding + abs(ctx.getRandom().nextInt(toExcluding - fromIncluding));
    }
}
