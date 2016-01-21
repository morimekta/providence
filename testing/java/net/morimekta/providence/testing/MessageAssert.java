package net.morimekta.providence.testing;

import net.morimekta.providence.PMessage;

import org.junit.Assert;

/**
 * @author Stein Eldar Johnsen
 * @since 21.01.16.
 */
public class MessageAssert {
    public static <T extends PMessage<T>>void assertMessageEquals(T expected, T actual) {
        Assert.assertThat(actual, new MessageEq<>(expected));
    }

    public static <T extends PMessage<T>>void assertMessageEquals(String reason, T expected, T actual) {
        Assert.assertThat(reason, actual, new MessageEq<>(expected));
    }
}
