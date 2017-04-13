package net.morimekta.providence;

import net.morimekta.test.providence.core.UnionFields;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 15.01.16.
 */
public class PUnionTest {
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

        assertEquals(bl1, bl2);
        assertNotEquals(bl1, bl3);
    }
}
