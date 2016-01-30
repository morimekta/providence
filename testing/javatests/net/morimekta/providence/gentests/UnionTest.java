package net.morimekta.providence.gentests;

import net.morimekta.test.providence.UnionFields;

import org.junit.Test;

import static net.morimekta.providence.testing.ProvidenceMatchers.messageEq;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 15.01.16.
 */
public class UnionTest {
    @Test
    public void testUnionFields_bool() {
        UnionFields bl1 = UnionFields.builder()
                                     .setBooleanValue(true)
                                     .build();
        UnionFields bl2 = UnionFields.withBooleanValue(true);
        UnionFields bl3 = UnionFields.withBooleanValue(false);

        assertEquals(bl1, bl2);
        assertNotEquals(bl1, bl3);
        assertTrue(bl1.hasBooleanValue());
        assertTrue(bl1.isBooleanValue());
        assertEquals(UnionFields._Field.BOOLEAN_VALUE, bl1.unionField());

        assertThat(bl1, messageEq(bl2));
        assertThat(bl1, not(messageEq(bl3)));
    }
}
