package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static net.morimekta.providence.storage.MessageStoreUtils.mutateIfNotNull;

/**
 * Interface for storing messages of a single type.
 */
public interface MessageStore<K, M extends PMessage<M,F>, F extends PField>
        extends MessageReadOnlyStore<K, M, F>, ReadWriteStore<K, M> {
    /**
     * Put the message represented by the builder into the store on the given key.
     * Any further modifications to the builder will not be reflected on the store.
     *
     * @param key The key to store the builder on.
     * @param builder The builder to store.
     * @param <B> The builder type.
     * @return The replaced builder if one was replaced. Null if theentry was created.
     *         Any modifications to the returned builder will not be reflected onto
     *         the store.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    B putBuilder(@Nonnull K key, @Nonnull B builder) {
        return mutateIfNotNull(put(key, builder.build()));
    }

    /**
     * Put a collection of key and builder pairs onto the store. Any further modifications
     * to the builders will not be reflected onto the store.
     *
     * @param builders Map of builders to put into the store.
     * @param <B> The builder type.
     * @return Map of replaced entries. Any modifications to the returned builders will
     *         not be reflected onto the store.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    Map<K,B> putAllBuilders(@Nonnull Map<K,B> builders) {
        Map<K,M> instances = new HashMap<>();
        builders.forEach((k,b) -> instances.put(k, b.build()));
        Map<K,M> replaced = putAll(instances);
        Map<K,B> out = new HashMap<>();
        replaced.forEach((k,m) -> out.put(k, (B) m.mutate()));
        return out;
    }
}
