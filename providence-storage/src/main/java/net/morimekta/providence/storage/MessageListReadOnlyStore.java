package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.morimekta.providence.storage.MessageStoreUtils.mutateAll;

/**
 * Interface for storing messages of a single type. This is a read-only part
 * of the store.
 */
public interface MessageListReadOnlyStore<K, M extends PMessage<M,F>, F extends PField> extends ReadOnlyStore<K, List<M>> {
    /**
     * Get a list of builders for the entry stored. Any modifications
     * to the returned builders will not be reflected onto the store.
     *
     * @param key The key to look up.
     * @param <B> The builder type.
     * @return List of builders for stored on the key or null if not found.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    List<B> getBuilders(@Nonnull K key) {
        return mutateAll(get(key));
    }

    /**
     * Get map of lists of builders for all the messages for the requested keys.
     * The resulting map will not contain key -&gt; null mappings. Any modifications
     * to the returned builders will not be reflected onto the store.
     *
     * @param keys Collection of keys to request.
     * @param <B> The builder type.
     * @return Map of entries found.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    Map<K,List<B>> getAllBuilders(@Nonnull Collection<K> keys) {
        Map<K,List<B>> out = new HashMap<>();
        getAll(keys).forEach((k, list) -> out.put(k, mutateAll(list)));
        return out;
    }
}
