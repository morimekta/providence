package net.morimekta.providence.tools.common;

import net.morimekta.testing.rules.ConsoleWatcher;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

public class UtilsTest {
    @Rule
    public ConsoleWatcher console = new ConsoleWatcher();

    @Test
    public void testGetVersionString() throws IOException {
        assertThat(Utils.getVersionString(), startsWith("v"));
    }
}
