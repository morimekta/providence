package net.morimekta.providence.testing.generator;

import net.morimekta.providence.testing.generator.extra.ByteRangeGenerator;
import net.morimekta.providence.testing.generator.extra.DoubleRangeGenerator;
import net.morimekta.providence.testing.generator.extra.ExtraGenerators;
import net.morimekta.providence.testing.generator.extra.IntRangeGenerator;
import net.morimekta.providence.testing.generator.extra.LongRangeGenerator;
import net.morimekta.providence.testing.generator.extra.OneOfGenerator;
import net.morimekta.providence.testing.generator.extra.ShortRangeGenerator;
import net.morimekta.testing.ExtraMatchers;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static net.morimekta.providence.testing.generator.extra.ExtraGenerators.byteRange;
import static net.morimekta.providence.testing.generator.extra.ExtraGenerators.doubleRange;
import static net.morimekta.providence.testing.generator.extra.ExtraGenerators.intRange;
import static net.morimekta.providence.testing.generator.extra.ExtraGenerators.longRange;
import static net.morimekta.providence.testing.generator.extra.ExtraGenerators.oneOf;
import static net.morimekta.providence.testing.generator.extra.ExtraGenerators.shortRange;
import static net.morimekta.testing.ExtraMatchers.inRange;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ExtraGeneratorsTest {
    @Test
    public void testRange() {
        SimpleGeneratorBase base = new SimpleGeneratorBase();
        SimpleGeneratorContext context = base.createContext();

        ByteRangeGenerator<SimpleGeneratorContext> bytes = byteRange(-12, 12);
        ShortRangeGenerator<SimpleGeneratorContext> shorts = shortRange(-130, 130);
        IntRangeGenerator<SimpleGeneratorContext> ints = intRange(-123456, 123456);
        LongRangeGenerator<SimpleGeneratorContext> longs = longRange(-9876543210L, 9876543210L);
        DoubleRangeGenerator<SimpleGeneratorContext> doubles = doubleRange(-55.7, 55.7);

        for (int i = 0; i < 1000; ++i) {
            assertThat(bytes.generate(context), is(inRange(-12, 12)));
            assertThat(shorts.generate(context), is(inRange(-130, 130)));
            assertThat(ints.generate(context), is(inRange(-123456, 123456)));
            assertThat(longs.generate(context), is(inRange(-9876543210L, 9876543210L)));
            assertThat(doubles.generate(context), is(inRange(-55.7, 55.7)));
        }
    }

    @Test
    public void testRangeFail() {
        try {
            byteRange(-200, 0);
            fail("no exception");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), is("Bad byte value, from: -200"));
        }

        try {
            byteRange(0, 200);
            fail("no exception");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), is("Bad byte value, to: 200"));
        }

        try {
            byteRange(100, 50);
            fail("no exception");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), is("Invalid range [ 100 .. 50 >"));
        }


        try {
            shortRange(-200000, 0);
            fail("no exception");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), is("Bad short value, from: -200000"));
        }

        try {
            shortRange(0, 200000);
            fail("no exception");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), is("Bad short value, to: 200000"));
        }

        try {
            shortRange(200, 100);
            fail("no exception");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), is("Invalid range [ 200 .. 100 >"));
        }

        try {
            intRange(200, 100);
            fail("no exception");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), is("Invalid range [ 200 .. 100 >"));
        }

        try {
            longRange(200, 100);
            fail("no exception");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), is("Invalid range [ 200 .. 100 >"));
        }

        try {
            doubleRange(20.1, 10.2);
            fail("no exception");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), is("Invalid range [ 20.1 .. 10.2 >"));
        }
    }

    @Test
    public void testOneOf() {
        SimpleGeneratorBase base = new SimpleGeneratorBase();
        SimpleGeneratorContext context = base.createContext();

        OneOfGenerator<SimpleGeneratorContext,Integer> fibonnacci =
                oneOf(1, 1, 2, 3, 5, 8, 13, 21, 35);
        for (int i = 0; i < 1000; ++i) {
            assertThat(fibonnacci.generate(context),
                       is(ExtraMatchers.oneOf(1, 2, 3, 5, 8, 13, 21, 35)));
        }

        OneOfGenerator<SimpleGeneratorContext,Integer> none =
                oneOf(ImmutableList.of());
        assertThat(none.generate(context), is(nullValue()));
        assertThat(none.generate(context), is(nullValue()));
    }

    @Test
    public void testConstructor()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<ExtraGenerators> constructor = ExtraGenerators.class.getDeclaredConstructor();
        assertThat(constructor.isAccessible(), is(false));
        constructor.setAccessible(true);
        try {
            assertThat(constructor.newInstance(), isA(ExtraGenerators.class));
        } finally {
            constructor.setAccessible(false);
        }
    }
}
