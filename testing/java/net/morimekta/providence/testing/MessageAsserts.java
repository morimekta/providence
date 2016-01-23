package net.morimekta.providence.testing;

import net.morimekta.providence.PMessage;
import net.morimekta.util.Binary;

import static org.junit.Assert.assertThat;

/**
 * Assert shorthands for providence messages.
 */
public class MessageAsserts {
    public static <T extends PMessage<T>>void assertMessageEquals(T expected, T actual) {
        assertThat(actual, new MessageEq<>(expected));
    }

    public static <T extends PMessage<T>>void assertMessageEquals(String reason, T expected, T actual) {
        assertThat(reason, actual, new MessageEq<>(expected));
    }
}
