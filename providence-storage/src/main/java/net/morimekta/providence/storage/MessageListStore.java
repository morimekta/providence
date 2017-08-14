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
import java.util.List;
import java.util.Map;

import static net.morimekta.providence.storage.MessageStoreUtils.buildAll;
import static net.morimekta.providence.storage.MessageStoreUtils.mutateAll;

/**
 * Interface for storing messages of a single type.
 */
public interface MessageListStore<K, M extends PMessage<M,F>, F extends PField>
        extends MessageListReadOnlyStore<K, M, F> {
    /**
     * @param values Put all key value pairs form this map into the storage.
     *               The list of messages should not contain null items.
     * @return Immutable map of replaced values. Values not already present should not
     *         have an entry in the result map (no key -&gt; null mapping).
     */
    @Nonnull
    Map<K,List<M>> putAll(@Nonnull Map<K, List<M>> values);

    /**
     * Remove the values for the given keys;
     * @param keys Map of removed key value pairs.
     * @return Immutable map of removed key value pairs. Values not removed should not
     *         have an entry in the result map (no key -&gt; null mapping).
     */
    @Nonnull
    Map<K,List<M>> removeAll(Collection<K> keys);

    /**
     * @param key The key to put message at.
     * @param list The list of message to put. The list should not contain
     *             null items.
     * @return Replaced value if any.
     */
    @Nullable
    default List<M> put(@Nonnull K key, @Nonnull List<M> list) {
        return putAll(ImmutableMap.of(key, list)).get(key);
    }

    /**
     * @param key Entry key to remove from map.
     * @return The list of messages removed or null if nothing was removed.
     */
    @Nullable
    default List<M> remove(@Nonnull K key) {
        return removeAll(ImmutableList.of(key)).get(key);
    }

    /**
     * Put messages into the map represented by their builders. Further
     * modifications to the builders will not be reflected onto the contents
     * of the store.
     *
     * @param key The key to put builders to.
     * @param builders The list of builders to put.
     * @param <B> The builder type.
     * @return The list of builders representing messages replaced, or null
     *         if new entry was created.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    List<B> putBuilders(@Nonnull K key, @Nonnull List<B> builders) {
        return mutateAll(put(key, buildAll(builders)));
    }

    /**
     * Put messages into the map represented by their builders. Further
     * modifications to the builders will not be reflected onto the contents
     * of the store.
     *
     * @param builders Map of key to list of builders.
     * @param <B> The builder type.
     * @return Map of builders that were replaced in the store. Will not
     *         contain (key -&gt; null) mappings.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    Map<K,List<B>> putAllBuilders(@Nonnull Map<K, List<B>> builders) {
        Map<K,List<M>> instances = new HashMap<>();
        builders.forEach((k,bList) -> instances.put(k, buildAll(bList)));
        Map<K,List<M>> replaced = putAll(instances);
        Map<K,List<B>> result = new HashMap<>();
        replaced.forEach((k,list) -> result.put(k, mutateAll(list)));
        return result;
    }
}
