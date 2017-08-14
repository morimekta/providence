package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;

import com.google.common.collect.ImmutableList;

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
public interface MessageReadOnlyStore<K, M extends PMessage<M,F>, F extends PField> {
    /**
     * Look up a set of keys from the storage.
     *
     * @param keys The keys to look up.
     * @return Immutable map of all the found key value pairs. Values not found should not
     *         have an entry in the result map (no key -&gt; null mapping).
     */
    @Nonnull
    Map<K,M> getAll(@Nonnull Collection<K> keys);

    /**
     * @param key The key to look up.
     * @return True if the key was contained in the map.
     */
    boolean containsKey(@Nonnull K key);

    /**
     * Get a collection of all the keys in the store.
     * @return Key collection.
     */
    @Nonnull
    Collection<K> keys();

    /**
     * Get a single value from the storage.
     *
     * @param key The key to look up.
     * @return The value if present.
     */
    @Nullable
    default M get(@Nonnull K key) {
        return getAll(ImmutableList.of(key)).get(key);
    }

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
