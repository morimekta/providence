package net.morimekta.providence.testing;

import net.morimekta.providence.PMessage;
import org.hamcrest.Matcher;

/**
 * Assert shorthands for providence messages.
 */
public class ProvidenceMatchers {
    public static <T extends PMessage<T>> Matcher<T> messageEq(T expected) {
        return new MessageEq<>(expected);
    }
}
