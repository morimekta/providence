package net.morimekta.providence.testing.generator;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.test.providence.testing.OptionalFields;
import net.morimekta.test.providence.testing.UnionFields;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.Assert.assertThat;

public class MessageGeneratorTest {
    static class FixedUuidContext extends GeneratorContext<FixedUuidContext> {
        private UUID uuid;

        FixedUuidContext(FixedUuidBase base) {
            super(base);
        }

        UUID getUuid() {
            if (uuid == null) {
                uuid = UUID.randomUUID();
            }
            return uuid;
        }
    }

    public static class FixedUuidBase
            extends GeneratorBase<FixedUuidBase, FixedUuidContext> {
        FixedUuidBase() {}

        @SuppressWarnings("unused")
        public FixedUuidBase(FixedUuidBase copyOf) {
            super(copyOf);
        }

        @Override
        public FixedUuidContext createContext() {
            return new FixedUuidContext(this);
        }
    }

    private FixedUuidBase                                                             base;
    private MessageGenerator<FixedUuidContext, OptionalFields, OptionalFields._Field> generator;

    @Before
    public void setUp() {
        base = new FixedUuidBase();
        base.setDefaultFillRate(0.5);
        generator = new MessageGenerator<>(OptionalFields.kDescriptor);
        generator = base.messageGeneratorFor(OptionalFields.kDescriptor);
    }

    @Test
    public void testGenerate_fillRateIsRandom() {
        AtomicInteger isSet = new AtomicInteger();
        AtomicInteger notSet = new AtomicInteger();

        for (int i = 0; i < 100; ++i) {
            updateCounters(generator.generate(base.createContext()), isSet, notSet);
        }

        // With this large test set, the chance of any of these getting below
        // 400 ( about 40% of the total fields checked ) should be really
        // minimal.
        assertThat(isSet.get(), is(greaterThan(400)));
        assertThat(notSet.get(), is(greaterThan(400)));
    }

    private <M extends PMessage<M, F>, F extends PField>
    void updateCounters(M msg, AtomicInteger isSet, AtomicInteger notSet) {
        for (F field : msg.descriptor().getFields()) {
            if (msg.has(field)) {
                isSet.incrementAndGet();
            } else {
                notSet.incrementAndGet();
            }
        }
    }

    @Test
    public void testGenerate_presenceOverrides() {
        base.withMessageGenerator(OptionalFields.kDescriptor, gen ->
                gen.setAlwaysPresent(OptionalFields._Field.BINARY_VALUE,
                                     OptionalFields._Field.BOOLEAN_VALUE,
                                     OptionalFields._Field.BYTE_VALUE)
                   .setAlwaysAbsent(OptionalFields._Field.INTEGER_VALUE,
                                    OptionalFields._Field.SHORT_VALUE,
                                    OptionalFields._Field.LONG_VALUE));

        OptionalFields res = generator.generate(base.createContext());

        assertThat(res.hasBinaryValue(), is(true));
        assertThat(res.hasBooleanValue(), is(true));
        assertThat(res.hasByteValue(), is(true));

        assertThat(res.hasIntegerValue(), is(false));
        assertThat(res.hasShortValue(), is(false));
        assertThat(res.hasLongValue(), is(false));
    }

    @Test
    public void testGenerate_unionPresence_oneRequired() {
        MessageGenerator<FixedUuidContext, UnionFields, UnionFields._Field>
                unionGenerator = base.messageGeneratorFor(UnionFields.kDescriptor);

        unionGenerator.setAlwaysPresent(UnionFields._Field.INTEGER_VALUE);

        for (int i = 0; i < 10; ++i) {
            UnionFields unionFields = unionGenerator.generate(base.createContext());
            assertThat(unionFields.unionField(), is(UnionFields._Field.INTEGER_VALUE));
        }

        unionGenerator.resetDefaultPresence()
                      .setAlwaysAbsent(UnionFields._Field.INTEGER_VALUE,
                                       UnionFields._Field.SHORT_VALUE,
                                       UnionFields._Field.LONG_VALUE,
                                       UnionFields._Field.STRING_VALUE,
                                       UnionFields._Field.BINARY_VALUE);

        for (int i = 0; i < 10; ++i) {
            UnionFields unionFields = unionGenerator.generate(base.createContext());
            assertThat(unionFields.unionField(), not(isOneOf(
                    UnionFields._Field.INTEGER_VALUE,
                    UnionFields._Field.SHORT_VALUE,
                    UnionFields._Field.LONG_VALUE,
                    UnionFields._Field.STRING_VALUE,
                    UnionFields._Field.BINARY_VALUE)));
        }
    }

    @Test
    public void testGenerate_withCutsomContext() {
        base.setDefaultFillRate(1.0)
            .withMessageGenerator(
                       OptionalFields.kDescriptor,
                       generator -> generator.setValueGenerator(OptionalFields._Field.STRING_VALUE,
                                                                context -> context.getUuid().toString())
                                             .setValueGenerator(OptionalFields._Field.LONG_VALUE,
                                                                context -> context.getUuid()
                                                                                  .getMostSignificantBits()));

        FixedUuidContext reused = base.createContext();

        OptionalFields first = generator.generate(reused);
        OptionalFields second = generator.generate(base.createContext());

        assertThat(first.getStringValue(), is(not(second.getStringValue())));

        long firstMost = UUID.fromString(first.getStringValue()).getMostSignificantBits();
        long secondMost = UUID.fromString(second.getStringValue()).getMostSignificantBits();

        assertThat(first.getLongValue(), is(firstMost));
        assertThat(second.getLongValue(), is(secondMost));

        OptionalFields whenReused = generator.generate(reused);

        assertThat(whenReused.getStringValue(), is(first.getStringValue()));
    }
}
