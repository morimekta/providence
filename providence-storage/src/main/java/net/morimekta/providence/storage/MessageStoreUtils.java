package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilities for message store implementations and interfaces.
 */
public class MessageStoreUtils {
    /**
     * Build all items of the collection containing builders. The list must not
     * contain any null items.
     *
     * @param builders List of builders.
     * @param <M> The message type.
     * @param <F> The field type.
     * @param <B> The builder type.
     * @return List of messages or null if null input.
     */
    public static <M extends PMessage<M,F>, F extends PField, B extends PMessageBuilder<M,F>>
    List<M> buildAll(Collection<B> builders) {
        if (builders == null) {
            return null;
        }
        return builders.stream()
                       .map(PMessageBuilder::build)
                       .collect(Collectors.toList());
    }

    /**
     * Mutate all items of the collection containing messages. The list must not
     * contain any null items.
     *
     * @param messages List of messages
     * @param <M> The message type.
     * @param <F> The field type.
     * @param <B> The builder type.
     * @return List of builders or null if null input.
     */
    @SuppressWarnings("unchecked")
    public static <M extends PMessage<M,F>, F extends PField, B extends PMessageBuilder<M,F>>
    List<B> mutateAll(Collection<M> messages) {
        if (messages == null) {
            return null;
        }
        return (List<B>) messages.stream()
                                 .map(PMessage::mutate)
                                 .collect(Collectors.toList());
    }

    /**
     * Build the message from builder if it is not null.
     *
     * @param builder The builder to build.
     * @param <M> The message type.
     * @param <F> The field type.
     * @param <B> The builder type.
     * @return The message or null if null input.
     */
    public static <M extends PMessage<M,F>, F extends PField, B extends PMessageBuilder<M,F>>
    M buildIfNotNull(B builder) {
        if (builder == null) {
            return null;
        }
        return builder.build();
    }

    /**
     * Mutate the message if it is not null.
     *
     * @param message Message to mutate.
     * @param <M> The message type.
     * @param <F> The field type.
     * @param <B> The builder type.
     * @return The builder or null if null input.
     */
    @SuppressWarnings("unchecked")
    public static <M extends PMessage<M,F>, F extends PField, B extends PMessageBuilder<M,F>>
    B mutateIfNotNull(M message) {
        if (message == null) {
            return null;
        }
        return (B) message.mutate();
    }
}
