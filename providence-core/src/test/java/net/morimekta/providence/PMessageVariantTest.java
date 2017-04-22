package net.morimekta.providence;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PMessageVariantTest {
    @Test
    public void testVariant() {
        assertThat(PMessageVariant.EXCEPTION.toString(), is("exception"));
        assertThat(PMessageVariant.UNION.toString(), is("union"));
        assertThat(PMessageVariant.STRUCT.toString(), is("struct"));
    }
}
