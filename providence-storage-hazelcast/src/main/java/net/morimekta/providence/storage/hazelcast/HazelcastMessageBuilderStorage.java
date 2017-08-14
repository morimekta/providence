package net.morimekta.providence.storage.hazelcast;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.storage.MessageStore;

import com.google.common.collect.ImmutableMap;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.Portable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * A message store containing message builders. Note that there are
 * no 'list' variants of this type of store. The benefit of using
 * the {@link HazelcastMessageBuilderStorage} is that it can be
 * combined with using the hazelcast Portable indexing and query
 * systems.
 *
 * @see Portable And
 *     <a href="http://docs.hazelcast.org/docs/3.8.4/manual/html-single/index.html">Hazelcast Docs</a>
 *     for reference on how to utilize portable and querying the
 *     data grid.
 */
public class HazelcastMessageBuilderStorage<
        Key,
        Message extends PMessage<Message, Field>,
        Field extends PField,
        Builder extends PMessageBuilder<Message, Field> & Portable>
        implements MessageStore<Key, Message, Field> {
    private final IMap<Key, Builder> hazelcastMap;

    public HazelcastMessageBuilderStorage(IMap<Key, Builder> hazelcastMap) {
        this.hazelcastMap = hazelcastMap;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Map<Key, Message> putAll(@Nonnull Map<Key, Message> values) {
        Map<Key, Builder> tmpIn = new HashMap<>();
        values.forEach((key, message) -> tmpIn.put(key, (Builder) message.mutate()));
        Map<Key, Builder> tmpOut = putAllBuilders(tmpIn);
        Map<Key, Message> ret = new HashMap<>();
        tmpOut.forEach((key, builder) -> ret.put(key, builder.build()));
        return ImmutableMap.copyOf(ret);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <B extends PMessageBuilder<Message, Field>> Map<Key, B> putAllBuilders(@Nonnull Map<Key, B> builders) {
        Map<Key, ICompletableFuture<Builder>> futureMap = new HashMap<>();
        builders.forEach((key, builder) -> futureMap.put(key, hazelcastMap.putAsync(key, (Builder) builder)));
        Map<Key, B> ret = new HashMap<>();
        futureMap.forEach((key, future) -> {
            try {
                Builder value = future.get();
                if (value != null) {
                    ret.put(key, (B) value);
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
        Map<Key, ICompletableFuture<Builder>> futureMap = new HashMap<>();
        keys.forEach(key -> futureMap.put(key, hazelcastMap.removeAsync(key)));
        Map<Key, Message> ret = new HashMap<>();
        futureMap.forEach((key, builder) -> {
            try {
                Builder value = builder.get();
                if (value != null) {
                    ret.put(key, value.build());
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
        Map<Key, Message> ret = new HashMap<>();
        getAllBuilders(keys).forEach((key, builder) -> ret.put(key, builder.build()));
        return ImmutableMap.copyOf(ret);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <B extends PMessageBuilder<Message, Field>> Map<Key, B> getAllBuilders(@Nonnull Collection<Key> keys) {
        Map<Key, B> out = new HashMap<>();
        hazelcastMap.getAll(new HashSet<>(keys)).forEach((key, v) -> {
            if (v != null) {
                out.put(key, (B) v);
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
