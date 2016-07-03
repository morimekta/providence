package net.morimekta.providence.testing;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import org.hamcrest.Matcher;

/**
 * Assert shorthands for providence messages.
 */
public class ProvidenceMatchers {
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    Matcher<Message> messageEq(Message expected) {
        return new MessageEq<>(expected);
    }
}
