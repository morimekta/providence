package net.morimekta.providence.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ThriftCollectionTest {
    @Test
    public void testForTag() {
        assertThat(ThriftCollection.forName("sorted"), is(ThriftCollection.SORTED));
        assertThat(ThriftCollection.forName("SORTED"), is(ThriftCollection.SORTED));
        assertThat(ThriftCollection.forName("ordered"), is(ThriftCollection.ORDERED));
        assertThat(ThriftCollection.forName("OrderED"), is(ThriftCollection.ORDERED));
        assertThat(ThriftCollection.forName("boo"), is(ThriftCollection.DEFAULT));
        assertThat(ThriftCollection.forName(""), is(ThriftCollection.DEFAULT));
    }
}
