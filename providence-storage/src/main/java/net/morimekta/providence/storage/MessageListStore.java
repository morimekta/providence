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
     * @return Map of replaced values.
     */
    @Nonnull
    Map<K,List<M>> putAll(@Nonnull Map<K, List<M>> values);

    /**
     * Remove the values for the given keys;
     * @param keys Map of removed key value pairs.
     * @return Map of removed key value pairs.
     */
    @Nonnull
    Map<K,List<M>> removeAll(Collection<K> keys);

    /**
     * @param key The key to put message at.
     * @param message The message to put.
     * @return Replaced value if any.
     */
    @Nullable
    default List<M> put(@Nonnull K key, @Nonnull List<M> message) {
        return putAll(ImmutableMap.of(key, message)).get(key);
    }

    @Nullable
    default List<M> remove(@Nonnull K key) {
        return removeAll(ImmutableList.of(key)).get(key);
    }

    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    List<B> putBuilders(@Nonnull K key, @Nonnull List<B> builders) {
        List<M> tmp = put(key, buildAll(builders));
        if (tmp != null) {
            return mutateAll(tmp);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    default <B extends PMessageBuilder<M,F>>
    Map<K,List<B>> putAllBuilders(@Nonnull Map<K, List<B>> builders) {
        Map<K,List<M>> instances = new HashMap<>();
        builders.forEach((k,bList) -> instances.put(k, buildAll(bList)));
        Map<K,List<M>> replaced = putAll(instances);
        Map<K,List<B>> out = new HashMap<>();
        replaced.forEach((k,list) -> out.put(k,mutateAll(list)));
        return out;
    }
}
