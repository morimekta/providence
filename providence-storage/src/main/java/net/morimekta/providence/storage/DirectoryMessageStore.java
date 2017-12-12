package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.Serializer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Simple file-based storage of providence messages that keeps
 * an in-memory key index, a message cache, and stores messages
 * to individual files in a single directly.
 * <p>
 * Note that the directory store is parallel compatible between
 * instances, as all of them would be able to read, write etc all
 * the files all the time.
 * <p>
 * <b>TL;DR Each directory can only have one
 * {@link DirectoryMessageStore} instance active at a time.</b>
 */
public class DirectoryMessageStore<K, M extends PMessage<M,F>, F extends PField>
        extends BaseDirectoryStorage<K>
        implements MessageStore<K,M,F>, Closeable {
    private final Serializer               serializer;
    private final PMessageDescriptor<M, F> descriptor;
    private final Cache<K, M>              cache;

    public DirectoryMessageStore(@Nonnull File directory,
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
    public Map<K, M> getAll(@Nonnull Collection<K> keys) {
        return mutex.lockForReading(() -> {
            HashMap<K,M> out = new HashMap<>();
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
    public Map<K,M> putAll(@Nonnull Map<K, M> values) {
        return mutex.lockForWriting(() -> {
            Map<K,M> out = getAll(values.keySet());
            values.forEach((key, value) -> {
                try {
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
    public Map<K,M> removeAll(Collection<K> keys) {
        return mutex.lockForWriting(() -> {
            Map<K,M> out = new HashMap<>();
            for (K key : keys) {
                File file = fileFor(key, false);
                if (file.exists()) {
                    try {
                        out.put(key, cache.get(key, () -> read(key)));
                    } catch (ExecutionException e) {
                        // Best effort, as we could not read the message.
                        // At least it was present.
                        out.put(key, descriptor.builder().build());
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

    private M read(K key) throws IOException {
        try (FileInputStream fis = new FileInputStream(fileFor(key, false));
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            return serializer.deserialize(bis, descriptor);
        } catch (IOException e) {
            throw new IOException("Unable to read " + keyBuilder.apply(key), e);
        }
    }

    private void write(K key, M message) throws IOException {
        File tmp = fileFor(key, true);
        File file = fileFor(key, false);
        try (FileOutputStream fos = new FileOutputStream(tmp, false);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            serializer.serialize(bos, message);
            bos.flush();
        } catch (IOException e) {
            throw new IOException("Unable to write " + keyBuilder.apply(key), e);
        }
        Files.move(tmp.toPath(), file.toPath(), REPLACE_EXISTING);
    }

    @Override
    public void close() throws IOException {
        cache.invalidateAll();
        keyset.clear();
    }
}
