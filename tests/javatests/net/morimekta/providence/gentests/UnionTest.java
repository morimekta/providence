package net.morimekta.providence.gentests;

import net.morimekta.test.alltypes.OneType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 15.01.16.
 */
public class UnionTest {
    @Test
    public void testOneType_bool() {
        OneType bl1 = OneType.builder().setBl(true).build();
        OneType bl2 = OneType.withBl(true);
        OneType bl3 = OneType.withBl(false);

        assertEquals(bl1, bl2);
        assertNotEquals(bl1, bl3);
        assertTrue(bl1.hasBl());
        assertTrue(bl1.isBl());
        assertEquals(OneType._Field.BL, bl1.unionField());
    }
}
