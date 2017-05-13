package net.morimekta.providence.reflect.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConstProviderTest {
    @Test
    public void testProvider() {
        ProgramRegistry registry = new ProgramRegistry();
        ConstProvider provider = new ConstProvider(registry, "string", "prog", "\"value\"");

        assertThat(provider.get(), is("value"));
    }
}
