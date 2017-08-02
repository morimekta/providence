package net.morimekta.providence.testing.generator.extra;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

import static java.lang.Math.abs;

/**
 * Default generator for selecting one of a set of values of the same type..
 */
public class LongRangeGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context,Long> {
    private final long fromIncluding;
    private final long toExcluding;

    public LongRangeGenerator(long fromIncluding, long toExcluding) {
        this.fromIncluding = fromIncluding;
        this.toExcluding = toExcluding;

        if (fromIncluding >= toExcluding) {
            throw new AssertionError("Invalid range [ " + fromIncluding + " .. " + toExcluding + " >");
        }
    }

    @Override
    public Long generate(Context ctx) {
        return fromIncluding + abs(ctx.getRandom().nextLong() % (toExcluding - fromIncluding));
    }
}
