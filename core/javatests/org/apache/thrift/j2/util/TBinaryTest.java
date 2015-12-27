package org.apache.thrift.j2.util;

import org.apache.thrift.j2.TBinary;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by SteinEldar on 24.12.2015.
 */
public class TBinaryTest {
    private static final byte[] a1 = new byte[]{'a', 'b', 'c'};
    private static final byte[] a2 = new byte[]{'a', 'b', 'c'};
    private static final byte[] b1 = new byte[]{'a', 'b', 'd'};
    private static final byte[] b2 = new byte[]{'a', 'b', 'd'};
    private static final byte[] c1 = new byte[]{'a', 'b', 'c', 'd'};
    private static final byte[] c2 = new byte[]{'a', 'b', 'c', 'd'};

    @Test
    public void testHashCode() {
        Assert.assertEquals(TBinary.wrap(a1).hashCode(), TBinary.wrap(a2).hashCode());
        Assert.assertEquals(TBinary.wrap(b1).hashCode(), TBinary.wrap(b2).hashCode());
        Assert.assertEquals(TBinary.wrap(c1).hashCode(), TBinary.wrap(c2).hashCode());

        assertNotEquals(TBinary.wrap(b1).hashCode(), TBinary.wrap(a2).hashCode());
        assertNotEquals(TBinary.wrap(c1).hashCode(), TBinary.wrap(b2).hashCode());
        assertNotEquals(TBinary.wrap(a1).hashCode(), TBinary.wrap(c2).hashCode());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(TBinary.wrap(a1), TBinary.wrap(a2));
        Assert.assertEquals(TBinary.wrap(b1), TBinary.wrap(b2));
        Assert.assertEquals(TBinary.wrap(c1), TBinary.wrap(c2));

        assertNotEquals(TBinary.wrap(b1), TBinary.wrap(a2));
        assertNotEquals(TBinary.wrap(c1), TBinary.wrap(b2));
        assertNotEquals(TBinary.wrap(a1), TBinary.wrap(c2));
    }

    @Test
    public void testCompareTo() {
        TreeSet<TBinary> set = new TreeSet<>();
        set.add(TBinary.wrap(a1));
        set.add(TBinary.wrap(a2));
        set.add(TBinary.wrap(b1));
        set.add(TBinary.wrap(b2));
        set.add(TBinary.wrap(c1));
        set.add(TBinary.wrap(c2));

        assertEquals(3, set.size());
        ArrayList<TBinary> list = new ArrayList<>(set);
        assertEquals(list.get(0), TBinary.wrap(a1));
        assertEquals(list.get(1), TBinary.wrap(c1));
        assertEquals(list.get(2), TBinary.wrap(b1));
    }

    @Test
    public void testBase64() {
        String a = TBase64Utils.encode(a1);

        assertEquals(a, TBinary.wrap(a1).toBase64());
        assertEquals(TBinary.wrap(a2), TBinary.fromBase64(a));
    }
}
