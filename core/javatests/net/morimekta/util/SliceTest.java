package net.morimekta.util;

import net.morimekta.util.Slice;

import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 16.01.16.
 */
public class SliceTest {
    private byte[] data;

    @Before
    public void setUp() {
        data = "This is a -123.45 long \"test ł→Þħ úñí©øð€.\"".getBytes(StandardCharsets.UTF_8);
    }

    @Test
    public void testSlice() {
        Slice slice = new Slice(data, 0, 7);

        assertEquals(7, slice.length());
        assertEquals("This is", slice.asString());
        assertEquals("slice(off:0,len:7,total:56)", slice.toString());
        assertEquals('s', slice.charAt(6));

        Slice other = new Slice(data, 0, 22);
        assertEquals(22, other.length());
        assertEquals("This is a -123.45 long", other.asString());
        assertEquals("slice(off:0,len:22,total:56)", other.toString());
        assertEquals('s', other.charAt(6));

        assertNotEquals(slice, other);

        other = new Slice(data, 0, 7);

        assertEquals(slice, other);
    }

    @Test
    public void testParseInteger() {
        Slice slice = new Slice(data, 11, 3);

        assertEquals(3, slice.length());
        assertEquals("123", slice.asString());
        assertEquals("slice(off:11,len:3,total:56)", slice.toString());
        assertEquals('1', slice.charAt(0));

        assertEquals(123L, slice.parseInteger());
        assertEquals(123.0, slice.parseDouble(), 0.0001);
    }

    @Test
    public void testParseDouble() {
        Slice slice = new Slice(data, 11, 6);

        assertEquals(6, slice.length());
        assertEquals("123.45", slice.asString());
        assertEquals("slice(off:11,len:6,total:56)", slice.toString());
        assertEquals('1', slice.charAt(0));

        assertEquals(123.45, slice.parseDouble(), 0.0001);
    }
}
