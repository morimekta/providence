package net.morimekta.providence.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ThriftContainerTest {
    @Test
    public void testForTag() {
        assertThat(ThriftContainer.forName("sorted"), is(ThriftContainer.SORTED));
        assertThat(ThriftContainer.forName("SORTED"), is(ThriftContainer.SORTED));
        assertThat(ThriftContainer.forName("ordered"), is(ThriftContainer.ORDERED));
        assertThat(ThriftContainer.forName("OrderED"), is(ThriftContainer.ORDERED));
        assertThat(ThriftContainer.forName("boo"), is(ThriftContainer.DEFAULT));
        assertThat(ThriftContainer.forName(""), is(ThriftContainer.DEFAULT));
        assertThat(ThriftContainer.forName(null), is(ThriftContainer.DEFAULT));
    }
}
