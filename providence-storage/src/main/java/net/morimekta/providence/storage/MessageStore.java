package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface for storing messages of a single type.
 */
public interface MessageStore<K, M extends PMessage<M,F>, F extends PField> {
    /**
     * Look up a set of keys from the storage.
     *
     * @param keys The keys to look up.
     * @return The map of all the found key value pairs.
     */
    @Nonnull
    Map<K,M> getAll(@Nonnull Collection<K> keys);

    /**
     * @param values Put all key value pairs form this map into the storage.
     */
    void putAll(@Nonnull Map<K,M> values);

    /**
     * Remove the values for the given keys;
     * @param keys The
     */
    void removeAll(Collection<K> keys);

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
     * @throws IllegalStateException
     */
    @Nullable
    default M get(@Nonnull K key) {
        return getAll(ImmutableList.of(key)).get(key);
    }

    /**
     * @param key The key to put message at.
     * @param message The message to put.
     */
    default void put(@Nonnull K key, @Nonnull M message) {
        putAll(ImmutableMap.of(key, message));
    }

    default void remove(@Nonnull K key) {
        removeAll(ImmutableList.of(key));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    B getBuilder(@Nonnull K key) {
        return (B) getAllBuilders(ImmutableList.of(key)).get(key);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    Map<K,B> getAllBuilders(@Nonnull Collection<K> keys) {
        Map<K,B> out = new HashMap<>();
        getAll(keys).forEach((k, v) -> out.put(k, (B) v.mutate()));
        return out;
    }

    default <B extends PMessageBuilder<M,F>>
    void putBuilder(@Nonnull K key, @Nonnull B builder) {
        put(key, builder.build());
    }

    default <B extends PMessageBuilder<M,F>>
    void putAllBuilders(@Nonnull Map<K,B> builders) {
        Map<K,M> tmp = new HashMap<>();
        builders.forEach((k,b) -> tmp.put(k, b.build()));
        putAll(tmp);
    }
}
