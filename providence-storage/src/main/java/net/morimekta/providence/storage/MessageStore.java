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
     * @return Map of replaced values.
     */
    @Nonnull
    Map<K,M> putAll(@Nonnull Map<K,M> values);

    /**
     * Remove the values for the given keys;
     * @param keys Map of removed key value pairs.
     * @return Map of removed key value pairs.
     */
    @Nonnull
    Map<K,M> removeAll(Collection<K> keys);

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
     * @param key The key to put message at.
     * @param message The message to put.
     * @return Replaced value if any.
     */
    @Nullable
    default M put(@Nonnull K key, @Nonnull M message) {
        return putAll(ImmutableMap.of(key, message)).get(key);
    }

    @Nullable
    default M remove(@Nonnull K key) {
        return removeAll(ImmutableList.of(key)).get(key);
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

    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    B putBuilder(@Nonnull K key, @Nonnull B builder) {
        M tmp = put(key, builder.build());
        if (tmp != null) {
            return (B) tmp.mutate();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    Map<K,B> putAllBuilders(@Nonnull Map<K,B> builders) {
        Map<K,M> instances = new HashMap<>();
        builders.forEach((k,b) -> instances.put(k, b.build()));
        Map<K,M> replaced = putAll(instances);
        Map<K,B> out = new HashMap<>();
        replaced.forEach((k,m) -> out.put(k,m==null?null:(B)m.mutate()));
        return out;
    }
}
