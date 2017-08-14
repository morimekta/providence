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

import static net.morimekta.providence.storage.MessageStoreUtils.mutateIfNotNull;

/**
 * Interface for storing messages of a single type.
 */
public interface MessageStore<K, M extends PMessage<M,F>, F extends PField>
        extends MessageReadOnlyStore<K, M, F> {
    /**
     * @param values Put all key value pairs form this map into the storage.
     * @return Immutable map of replaced values. Values not already present should not
     *         have an entry in the result map (no key -&gt; null mapping).
     */
    @Nonnull
    Map<K,M> putAll(@Nonnull Map<K,M> values);

    /**
     * Remove the values for the given keys.
     * @param keys Map of removed key value pairs.
     * @return Immutable map of removed key value pairs. Values not removed should not
     *         have an entry in the result map (no key -&gt; null mapping).
     */
    @Nonnull
    Map<K,M> removeAll(Collection<K> keys);

    /**
     * @param key The key to put message at.
     * @param message The message to put.
     * @return Replaced value if any.
     */
    @Nullable
    default M put(@Nonnull K key, @Nonnull M message) {
        return putAll(ImmutableMap.of(key, message)).get(key);
    }

    /**
     * Remove the key value pair from the store.
     *
     * @param key The key to remove.
     * @return The message removed if any, otherwise null.
     */
    @Nullable
    default M remove(@Nonnull K key) {
        return removeAll(ImmutableList.of(key)).get(key);
    }

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
