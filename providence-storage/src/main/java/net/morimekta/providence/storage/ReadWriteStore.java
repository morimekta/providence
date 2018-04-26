package net.morimekta.providence.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * Interface to read and write a value or a range of values for a given key or keys.
 *
 * @param <K> Key to use for gathering information.
 * @param <V> Value to fetch, that is a generic method.
 */
public interface ReadWriteStore<K, V> extends ReadOnlyStore<K, V> {
    /**
     * @param values Put all key value pairs form this map into the storage.
     * @return Immutable map of replaced values. Values not already present should not
     *         have an entry in the result map (no key -&gt; null mapping).
     */
    @Nonnull
    Map<K,V> putAll(@Nonnull Map<K,V> values);

    /**
     * Remove the values for the given keys.
     * @param keys Map of removed key value pairs.
     * @return Immutable map of removed key value pairs. Values not removed should not
     *         have an entry in the result map (no key -&gt; null mapping).
     */
    @Nonnull
    Map<K,V> removeAll(Collection<K> keys);

    /**
     * @param key The key to put message at.
     * @param value The value to put.
     * @return Replaced value if any, otherwise null.
     */
    @Nullable
    default V put(@Nonnull K key, @Nonnull V value) {
        return putAll(ImmutableMap.of(key, value)).get(key);
    }

    /**
     * Remove the key value pair from the store.
     *
     * @param key The key to remove.
     * @return The value removed if any, otherwise null.
     */
    @Nullable
    default V remove(@Nonnull K key) {
        return removeAll(ImmutableList.of(key)).get(key);
    }

}
