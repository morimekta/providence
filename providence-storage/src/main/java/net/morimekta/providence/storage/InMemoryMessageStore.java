package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.util.concurrent.ReadWriteMutex;
import net.morimekta.util.concurrent.ReentrantReadWriteMutex;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory storage of providence messages. Uses a local hash map for
 * storing the instances. The store is thread safe through using reentrand
 * read-write mutex handling, so reading can happen in parallel.
 */
public class InMemoryMessageStore<K, M extends PMessage<M,F>, F extends PField> implements MessageStore<K,M,F> {
    private final Map<K, M>      map;
    private final ReadWriteMutex mutex;

    public InMemoryMessageStore() {
        map   = new HashMap<>();
        mutex = new ReentrantReadWriteMutex();
    }

    @Nonnull
    @Override
    public Map<K, M> getAll(@Nonnull Collection<K> keys) {
        return mutex.lockForReading(() -> {
            Map<K, M> out = new HashMap<>();
            for (K key : keys) {
                if (map.containsKey(key)) {
                    out.put(key, map.get(key));
                }
            }
            return ImmutableMap.copyOf(out);
        });
    }

    @Override
    public boolean containsKey(@Nonnull K key) {
        return mutex.lockForReading(() -> map.containsKey(key));
    }

    @Override @Nonnull
    public Collection<K> keys() {
        return mutex.lockForReading(() -> ImmutableSet.copyOf(map.keySet()));
    }

    @Override @Nonnull
    public Map<K,M> putAll(@Nonnull Map<K, M> values) {
        return mutex.lockForWriting(() -> {
            Map<K,M> out = new HashMap<>();
            for (Map.Entry<K,M> entry : values.entrySet()) {
                M tmp = map.put(entry.getKey(), entry.getValue());
                if (tmp != null) {
                    out.put(entry.getKey(), tmp);
                }
            }
            return out;
        });
    }

    @Override @Nonnull
    public Map<K,M> removeAll(Collection<K> keys) {
        return mutex.lockForWriting(() -> {
            Map<K,M> out = new HashMap<>();
            for (K key : keys) {
                M tmp = map.remove(key);
                if (tmp != null) {
                    out.put(key, tmp);
                }
            }
            return out;
        });
    }
}
