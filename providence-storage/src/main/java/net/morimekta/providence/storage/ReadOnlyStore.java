package net.morimekta.providence.storage;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * Interface to read a value or a range of values for a given key or keys.
 *
 * @param <K> Key to use for gathering information.
 * @param <V> Value to fetch, that is a generic method.
 */
public interface ReadOnlyStore<K, V> {
    /**
     * Look up a set of keys from the storage.
     *
     * @param keys The keys to look up.
     * @return Immutable map of all the found key value pairs. Values not found should not
     *         have an entry in the result map (no key -&gt; null mapping).
     */
    @Nonnull
    Map<K, V> getAll(@Nonnull Collection<K> keys);

    /**
     * @param key The key to look up.
     * @return True if the key was contained in the map.
     */
    boolean containsKey(@Nonnull K key);

    /**
     * Get a collection of all the keys in the store.
     *
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
    default V get(@Nonnull K key) {
        return getAll(ImmutableList.of(key)).get(key);
    }

}
