package net.morimekta.providence.storage.hazelcast;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.storage.MessageListStore;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Note that the hazelcast message store is backed by the PMessage serializable
 * properties, which makes the message field not indexed. If that is needed,
 * use the {@link HazelcastMessageBuilderStorage} instead.
 */
public class HazelcastMessageListStorage<
        K, M extends PMessage<M, F>, F extends PField>
        implements MessageListStore<K,M,F> {
    private final IMap<K, List<M>> hazelcastMap;

    public HazelcastMessageListStorage(IMap<K, List<M>> hazelcastMap) {
        this.hazelcastMap = hazelcastMap;
    }

    @Nonnull
    @Override
    public Map<K, List<M>> putAll(@Nonnull Map<K, List<M>> values) {
        Map<K, ICompletableFuture<List<M>>> futureMap = new HashMap<>();
        values.forEach((key, message) -> futureMap.put(key, hazelcastMap.putAsync(key, message)));
        Map<K, List<M>> ret = new HashMap<>();
        futureMap.forEach((key, future) -> {
            try {
                List<M> value = future.get();
                if (value != null) {
                    ret.put(key, value);
                }
            } catch (ExecutionException | InterruptedException e) {
                // TODO: Figure out if we timed out or were interrupted...
                throw new RuntimeException(e.getMessage(), e);
            }
        });
        return ret;
    }

    @Nonnull
    @Override
    public Map<K, List<M>> removeAll(Collection<K> keys) {
        Map<K, ICompletableFuture<List<M>>> futureMap = new HashMap<>();
        keys.forEach(key -> futureMap.put(key, hazelcastMap.removeAsync(key)));
        Map<K, List<M>> ret = new HashMap<>();
        futureMap.forEach((key, future) -> {
            try {
                List<M> value = future.get();
                if (value != null) {
                    ret.put(key, value);
                }
            } catch (ExecutionException | InterruptedException e) {
                // TODO: Figure out if we timed out or were interrupted...
                throw new RuntimeException(e.getMessage(), e);
            }

        });
        return ret;
    }

    @Nonnull
    @Override
    public Map<K, List<M>> getAll(@Nonnull Collection<K> keys) {
        Map<K, List<M>> ret = new HashMap<>();
        hazelcastMap.getAll(new HashSet<>(keys)).forEach((k, v) -> {
            if (v != null) {
                ret.put(k, v);
            }
        });
        return ret;
    }

    @Override
    public boolean containsKey(@Nonnull K key) {
        return hazelcastMap.containsKey(key);
    }

    @Nonnull
    @Override
    public Collection<K> keys() {
        return hazelcastMap.keySet();
    }
}
