package net.morimekta.providence;

import net.morimekta.providence.test_internal.CompactFields;
import net.morimekta.providence.test_internal.Containers;
import net.morimekta.providence.test_internal.OptionalFields;

import org.junit.Test;

import static net.morimekta.providence.test_internal.Containers._Field.STRING_SET;
import static net.morimekta.providence.test_internal.OptionalFields._Field.BOOLEAN_VALUE;
import static net.morimekta.providence.test_internal.OptionalFields._Field.COMPACT_VALUE;
import static net.morimekta.providence.util_internal.EqualToMessage.equalToMessage;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class PMessageBuilderTest {
    @Test
    public void testBuilder() {
        OptionalFields._Builder b = OptionalFields.builder();

        assertThat(b.collectSetFields(), hasSize(0));
        assertThat(b.modifiedFields(), hasSize(0));

        assertThat(b.mutator(COMPACT_VALUE), isA(PMessageBuilder.class));
        assertThat(b.setBooleanValue(false), is(sameInstance(b)));

        assertThat(b.collectSetFields(), hasSize(2));
        assertThat(b.collectSetFields(),
                   hasItems(COMPACT_VALUE, BOOLEAN_VALUE));

        assertThat(b.build(), is(equalToMessage(OptionalFields.builder()
                                                              .setCompactValue(CompactFields.builder().build())
                                                              .setBooleanValue(false)
                                                              .build())));

        assertThat(b.clear(BOOLEAN_VALUE), is(sameInstance(b)));

        assertThat(b.modifiedFields(), hasSize(2));
        assertThat(b.modifiedFields(),
                   hasItems(COMPACT_VALUE, BOOLEAN_VALUE));

        assertThat(b.build(), is(equalToMessage(OptionalFields.builder()
                                                              .setCompactValue(CompactFields.builder().build())
                                                              .build())));
    }

    @Test
    public void testCollections() {
        Containers._Builder b1 = Containers.builder();

        b1.addToBooleanList(true, false, false, true);
        b1.addTo(STRING_SET, "boo");

        Containers._Builder b2 = b1.build().mutate();

        assertThat(b2.modifiedFields(), hasSize(0));
        assertThat(b2.collectSetFields(), hasSize(2));
    }
}
