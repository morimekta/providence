package net.morimekta.providence.storage.hazelcast;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.storage.MessageStore;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Note that the hazelcast message store is backed by the PMessage
 * serializable property, which makes the message fields <b>not indexable</b>.
 * If that is needed, use the {@link HazelcastMessageBuilderStorage} instead.
 * <p>
 * On the other hand, this type of map is somewhat more efficient, and does not
 * require the message to be generated with hazelcast portable
 * support.
 */
public class HazelcastMessageStorage<Key, Message extends PMessage<Message, Field>, Field extends PField>
        implements MessageStore<Key, Message, Field> {
    private final IMap<Key, Message> hazelcastMap;

    public HazelcastMessageStorage(IMap<Key, Message> hazelcastMap) {
        this.hazelcastMap = hazelcastMap;
    }

    @Nonnull
    @Override
    public Map<Key, Message> putAll(@Nonnull Map<Key, Message> values) {
        Map<Key, ICompletableFuture<Message>> futureMap = new HashMap<>();
        values.forEach((key, message) -> futureMap.put(key, hazelcastMap.putAsync(key, message)));
        Map<Key, Message> ret = new HashMap<>();
        futureMap.forEach((key, future) -> {
            try {
                Message value = future.get();
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
    public Map<Key, Message> removeAll(Collection<Key> keys) {
        Map<Key, ICompletableFuture<Message>> futureMap = new HashMap<>();
        keys.forEach(key -> futureMap.put(key, hazelcastMap.removeAsync(key)));
        Map<Key, Message> ret = new HashMap<>();
        futureMap.forEach((key, future) -> {
            try {
                Message value = future.get();
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
    public Map<Key, Message> getAll(@Nonnull Collection<Key> keys) {
        Map<Key, Message> out = new HashMap<>();
        hazelcastMap.getAll(new HashSet<>(keys)).forEach((key, v) -> {
            if (v != null) {
                out.put(key, v);
            }
        });
        return out;
    }

    @Override
    public boolean containsKey(@Nonnull Key key) {
        return hazelcastMap.containsKey(key);
    }

    @Nonnull
    @Override
    public Collection<Key> keys() {
        return hazelcastMap.keySet();
    }
}
