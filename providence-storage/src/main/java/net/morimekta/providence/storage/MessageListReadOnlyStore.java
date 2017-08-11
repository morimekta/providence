package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for storing messages of a single type. This is a read-only part
 * of the store.
 */
public interface MessageListReadOnlyStore<K, M extends PMessage<M,F>, F extends PField> {
    /**
     * Look up a set of keys from the storage.
     *
     * @param keys The keys to look up.
     * @return The map of all the found key value pairs.
     */
    @Nonnull
    Map<K,List<M>> getAll(@Nonnull Collection<K> keys);

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
    default List<M> get(@Nonnull K key) {
        return getAll(ImmutableList.of(key)).get(key);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    List<B> getBuilders(@Nonnull K key) {
        return (List<B>) getAllBuilders(ImmutableList.of(key)).get(key);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    Map<K,List<B>> getAllBuilders(@Nonnull Collection<K> keys) {
        Map<K,List<B>> out = new HashMap<>();
        getAll(keys).forEach((k, list) -> out.put(k, MessageStoreUtils.mutateAll(list)));
        return out;
    }
}
