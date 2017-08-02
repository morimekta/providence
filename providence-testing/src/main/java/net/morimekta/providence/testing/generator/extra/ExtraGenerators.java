package net.morimekta.providence.testing.generator.extra;

import net.morimekta.providence.testing.generator.GeneratorContext;

import java.util.Collection;

/**
 * Some common extra convenient value generators.
 */
public class ExtraGenerators {
    public static <Context extends GeneratorContext<Context>>
    ByteRangeGenerator<Context> byteRange(int fromIncluding, int toExcluding) {
        if (fromIncluding < Byte.MIN_VALUE || fromIncluding > Byte.MAX_VALUE) {
            throw new AssertionError("Bad byte value, from: " + fromIncluding);
        }
        if (toExcluding < Byte.MIN_VALUE || toExcluding > Byte.MAX_VALUE) {
            throw new AssertionError("Bad byte value, to: " + toExcluding);
        }
        return byteRange((byte) fromIncluding, (byte) toExcluding);
    }
    public static <Context extends GeneratorContext<Context>>
    ByteRangeGenerator<Context> byteRange(byte fromIncluding, byte toExcluding) {
        return new ByteRangeGenerator<>(fromIncluding, toExcluding);
    }
    public static <Context extends GeneratorContext<Context>>
    ShortRangeGenerator<Context> shortRange(int fromIncluding, int toExcluding) {
        if (fromIncluding < Short.MIN_VALUE || fromIncluding > Short.MAX_VALUE) {
            throw new AssertionError("Bad short value, from: " + fromIncluding);
        }
        if (toExcluding < Short.MIN_VALUE || toExcluding > Short.MAX_VALUE) {
            throw new AssertionError("Bad short value, to: " + toExcluding);
        }
        return shortRange((short) fromIncluding, (short) toExcluding);
    }
    public static <Context extends GeneratorContext<Context>>
    ShortRangeGenerator<Context> shortRange(short fromIncluding, short toExcluding) {
        return new ShortRangeGenerator<>(fromIncluding, toExcluding);
    }
    public static <Context extends GeneratorContext<Context>>
    IntRangeGenerator<Context> intRange(int fromIncluding, int toExcluding) {
        return new IntRangeGenerator<>(fromIncluding, toExcluding);
    }
    public static <Context extends GeneratorContext<Context>>
    LongRangeGenerator<Context> longRange(long fromIncluding, long toExcluding) {
        return new LongRangeGenerator<>(fromIncluding, toExcluding);
    }
    public static <Context extends GeneratorContext<Context>>
    DoubleRangeGenerator<Context> doubleRange(double fromIncluding, double toExcluding) {
        return new DoubleRangeGenerator<>(fromIncluding, toExcluding);
    }

    @SafeVarargs
    public static <Context extends GeneratorContext<Context>, T>
    OneOfGenerator<Context, T> oneOf(T... selection) {
        return new OneOfGenerator<>(selection);
    }
    public static <Context extends GeneratorContext<Context>, T>
    OneOfGenerator<Context, T> oneOf(Collection<T> selection) {
        return new OneOfGenerator<>(selection);
    }

    // -- Defeat instantiation --
    private ExtraGenerators() {}
}
