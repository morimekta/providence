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
class MessageStoreUtils {
    static <M extends PMessage<M,F>, F extends PField, B extends PMessageBuilder<M,F>>
    List<M> buildAll(Collection<B> builders) {
        if (builders == null) return null;
        return builders.stream().map(PMessageBuilder::build).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    static <M extends PMessage<M,F>, F extends PField, B extends PMessageBuilder<M,F>>
    List<B> mutateAll(Collection<M> messages) {
        if (messages == null) return null;
        return (List<B>) messages.stream()
                       .map(PMessage::mutate)
                       .collect(Collectors.toList());
    }
}
