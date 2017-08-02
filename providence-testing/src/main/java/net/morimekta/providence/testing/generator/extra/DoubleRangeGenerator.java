package net.morimekta.providence.testing.generator.extra;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

import static java.lang.Math.abs;

/**
 * Default generator for selecting one of a set of values of the same type..
 */
public class DoubleRangeGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context,Double> {
    private final double fromIncluding;
    private final double toExcluding;

    public DoubleRangeGenerator(double fromIncluding, double toExcluding) {
        this.fromIncluding = fromIncluding;
        this.toExcluding = toExcluding;

        if (fromIncluding >= toExcluding) {
            throw new AssertionError("Invalid range [ " + fromIncluding + " .. " + toExcluding + " >");
        }
    }

    @Override
    public Double generate(Context ctx) {
        return fromIncluding + abs(ctx.getRandom().nextDouble() * (toExcluding - fromIncluding));
    }
}
