package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.morimekta.providence.storage.MessageStoreUtils.buildAll;
import static net.morimekta.providence.storage.MessageStoreUtils.mutateAll;

/**
 * Interface for storing messages of a single type.
 */
public interface MessageListStore<K, M extends PMessage<M,F>, F extends PField>
        extends MessageListReadOnlyStore<K, M, F>, ReadWriteStore<K, List<M>> {
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
