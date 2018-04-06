package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static net.morimekta.providence.storage.MessageStoreUtils.mutateIfNotNull;

/**
 * Interface for storing messages of a single type. This is a read-only part
 * of the store.
 */
public interface MessageReadOnlyStore<K, M extends PMessage<M,F>, F extends PField> extends ReadOnlyStore<K, M> {
    /**
     * Get the builder representing the message on the given key. Any modifications
     * to the returned builder will not be reflected onto the store.
     *
     * @param key The key to find builder for.
     * @param <B> The builder type.
     * @return The builder if message was found or null if not.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    B getBuilder(@Nonnull K key) {
        return mutateIfNotNull(get(key));
    }

    /**
     * Get builders for all keys requested. Any modifications to the returned builders
     * will not be reflected onto the store. The result map fill not contain any
     * (key -&gt; null) entries.
     *
     * @param keys Keys to look up.
     * @param <B> The builder type.
     * @return The map of found entries.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    Map<K,B> getAllBuilders(@Nonnull Collection<K> keys) {
        Map<K,B> out = new HashMap<>();
        getAll(keys).forEach((k, v) -> out.put(k, (B) v.mutate()));
        return out;
    }
}
