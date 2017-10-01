package net.morimekta.providence.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ThriftAnnotationTest {
    @Test
    public void testForTag() {
        assertThat(ThriftAnnotation.forTag("container"), is(ThriftAnnotation.CONTAINER));
        assertThat(ThriftAnnotation.forTag("gurba"), is(ThriftAnnotation.NONE));
    }
}
