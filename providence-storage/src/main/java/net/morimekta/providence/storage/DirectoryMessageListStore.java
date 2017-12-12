package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.streams.MessageCollectors;
import net.morimekta.providence.streams.MessageStreams;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Simple file-based storage of lists of providence messages that keeps
 * an in-memory key index, a message cache, and stores message lists
 * to individual files in a single directly.
 * <p>
 * Note that the directory store is parallel compatible between
 * instances, as all of them would be able to read, write etc all
 * the files all the time.
 * <p>
 * <b>TL;DR Each directory can only have one
 * {@link DirectoryMessageListStore} instance active at a time.</b>
 */
public class DirectoryMessageListStore<K, M extends PMessage<M,F>, F extends PField>
        extends BaseDirectoryStorage<K>
        implements MessageListStore<K,M,F>, Closeable {
    private final Serializer               serializer;
    private final PMessageDescriptor<M, F> descriptor;
    private final Cache<K, List<M>>        cache;

    public DirectoryMessageListStore(@Nonnull File directory,
                                     @Nonnull Function<K, String> keyBuilder,
                                     @Nonnull Function<String, K> keyParser,
                                     @Nonnull PMessageDescriptor<M,F> descriptor,
                                     @Nonnull Serializer serializer) {
        super(directory, keyBuilder, keyParser);
        this.descriptor = descriptor;
        this.serializer = serializer;
        this.cache = CacheBuilder.newBuilder()
                                 .build();
    }

    @Override
    public boolean containsKey(@Nonnull K key) {
        return mutex.lockForReading(() -> keyset.contains(key));
    }

    @Override @Nonnull
    public Collection<K> keys() {
        return mutex.lockForReading(() -> ImmutableSet.copyOf(keyset));
    }

    @Override @Nonnull
    public Map<K, List<M>> getAll(@Nonnull Collection<K> keys) {
        return mutex.lockForReading(() -> {
            HashMap<K,List<M>> out = new HashMap<>();
            TreeSet<K> tmp = new TreeSet<>(keys);
            tmp.retainAll(keyset);
            for (K key : tmp) {
                try {
                    out.put(key, cache.get(key, () -> read(key)));
                } catch (ExecutionException e) {
                    throw new RuntimeException("Unable to read " + keyBuilder.apply(key), e);
                }
            }
            return out;
        });
    }

    @Override @Nonnull
    public Map<K,List<M>> putAll(@Nonnull Map<K, List<M>> values) {
        return mutex.lockForWriting(() -> {
            Map<K,List<M>> out = new HashMap<>();
            values.forEach((key, value) -> {
                try {
                    value = ImmutableList.copyOf(value);
                    write(key, value);
                    cache.put(key, value);
                    keyset.add(key);
                } catch (IOException e) {
                    throw new UncheckedIOException(e.getMessage(), e);
                }
            });
            return out;
        });
    }

    @Override @Nonnull
    public Map<K,List<M>> removeAll(Collection<K> keys) {
        return mutex.lockForWriting(() -> {
            Map<K, List<M>> out = new HashMap<>();
            for (K key : keys) {
                File file = fileFor(key, false);
                if (file.exists()) {
                    try {
                        out.put(key, cache.get(key, () -> read(key)));
                    } catch (ExecutionException e) {
                        // Best effort, as we could not read the message.
                        // At least it was present.
                        out.put(key, new ArrayList<>());
                    } finally {
                        file.delete();
                    }
                    cache.invalidate(key);
                    keyset.remove(key);
                }
            }
            return out;
        });
    }

    private List<M> read(K key) throws IOException {
        try {
            return MessageStreams.file(fileFor(key, false), serializer, descriptor)
                                 .collect(Collectors.toList());
        } catch (UncheckedIOException e) {
            throw new IOException("Unable to read " + keyBuilder.apply(key), e.getCause());
        }
    }

    private void write(K key, List<M> message) throws IOException {
        File tmp = fileFor(key, true);
        File file = fileFor(key, false);
        if (tmp.exists() && !tmp.delete()) {
            throw new IOException("Unable to delete old tmp file: " + tmp.getAbsolutePath());
        }
        try {
            message.stream().collect(MessageCollectors.toFile(tmp, serializer));
        } catch (UncheckedIOException e) {
            throw new IOException("Unable to write " + keyBuilder.apply(key), e.getCause());
        }
        Files.move(tmp.toPath(), file.toPath(), REPLACE_EXISTING);
    }

    @Override
    public void close() throws IOException {
        cache.invalidateAll();
        keyset.clear();
    }
}
