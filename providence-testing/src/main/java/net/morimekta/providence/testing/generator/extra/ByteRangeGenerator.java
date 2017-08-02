package net.morimekta.providence.testing.generator.extra;

import net.morimekta.providence.testing.generator.Generator;
import net.morimekta.providence.testing.generator.GeneratorContext;

import static java.lang.Math.abs;

/**
 * Default generator for selecting one of a set of values of the same type..
 */
public class ByteRangeGenerator<Context extends GeneratorContext<Context>>
        implements Generator<Context,Byte> {
    private final byte fromIncluding;
    private final byte toExcluding;

    public ByteRangeGenerator(byte fromIncluding, byte toExcluding) {
        this.fromIncluding = fromIncluding;
        this.toExcluding = toExcluding;

        if (fromIncluding >= toExcluding) {
            throw new AssertionError("Invalid range [ " + fromIncluding + " .. " + toExcluding + " >");
        }
    }

    @Override
    public Byte generate(Context ctx) {
        return (byte) (fromIncluding + abs(ctx.getRandom().nextInt(toExcluding - fromIncluding)));
    }
}
