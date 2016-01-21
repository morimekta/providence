package net.morimekta.providence.tools.data;

import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 21.01.16.
 */
public class RandomGeneratorTest {
    private Random          random;
    private RandomGenerator randomGenerator;

    @Before
    public void setUp() {
        random = new Random(Clock.systemUTC().instant().getNano());
        randomGenerator = new RandomGenerator(random);
    }

    @Test
    public void testNextString() {
        // draw out a large string, and assert all characters are printable.
        String string = randomGenerator.nextString(1 << 16);
        for (char c : string.toCharArray()) {
            assertTrue(String.format("'%c': %d / %s is valid character:", c, (int) c, Character.getName(c)),
                       (c == '\t' || c == '\n' || c == '\f' || c == '\r' || c == '\b' ||
                        !(c < 32 || (127 <= c && c < 160) || (8192 <= c && c < 8448))) &&
                       Character.isValidCodePoint(c));
        }
    }
}
