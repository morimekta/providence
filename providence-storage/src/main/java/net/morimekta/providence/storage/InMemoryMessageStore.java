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
 * Simple in-memory storage of
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

    @Override
    public void putAll(@Nonnull Map<K, M> values) {
        mutex.lockForWriting(() -> map.putAll(values));
    }

    @Override
    public void removeAll(Collection<K> keys) {
        mutex.lockForWriting(() -> {
            for (K key : keys) {
                map.remove(key);
            }
        });
    }
}
