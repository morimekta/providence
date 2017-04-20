package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.util.concurrent.ReadWriteMutex;
import net.morimekta.util.concurrent.ReentrantReadWriteMutex;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Simple file-based storage of providence messages, that keeps
 * an in-memory key index, a message cache, and stores messages
 * to individual files in a single directly.
 *
 * Note that the directory store is parallel compatible between
 * instances, as all of them would be able to read, write etc all
 * the files all the time. TL;DR Each directory can only have one
 * {@link DirectoryMessageStore} instance.
 */
public class DirectoryMessageStore<K, M extends PMessage<M,F>, F extends PField>
        implements MessageStore<K,M,F>, Closeable {
    private final File directory;
    private final Function<K, String> keyBuilder;
    private final Function<String, K> keyParser;
    private final Serializer serializer;
    private final PMessageDescriptor<M, F> descriptor;

    private final ReadWriteMutex mutex;
    private final Cache<K, M>    cache;
    private final Set<K>         keyset;

    public DirectoryMessageStore(@Nonnull File directory,
                                 @Nonnull Function<K, String> keyBuilder,
                                 @Nonnull Function<String, K> keyParser,
                                 @Nonnull PMessageDescriptor<M,F> descriptor,
                                 @Nonnull Serializer serializer) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("not a directory: " + directory.toString());
        }

        this.directory = directory;
        this.keyBuilder = keyBuilder;
        this.keyParser = keyParser;
        this.descriptor = descriptor;
        this.serializer = serializer;

        this.mutex = new ReentrantReadWriteMutex();
        this.cache = CacheBuilder.newBuilder()
                                 .build();
        this.keyset = initKeySet();
    }

    @Override
    public boolean containsKey(@Nonnull K key) {
        return mutex.lockForReading(() -> keyset.contains(key));
    }

    @Override @Nonnull
    public Collection<K> keys() {
        return mutex.lockForReading(() -> ImmutableSet.copyOf(keyset));
    }

    @Nonnull
    @Override
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

    @Override
    public void putAll(@Nonnull Map<K, M> values) {
        mutex.lockForWriting(() -> values.forEach((key, value) -> {
            try {
                write(key, value);
                cache.put(key, value);
                keyset.add(key);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }));
    }

    @Override
    public void removeAll(Collection<K> keys) {
        mutex.lockForWriting(() -> {
            for (K key : keys) {
                File file = fileFor(key);
                if (file.exists() && file.delete()) {
                    cache.invalidate(key);
                    keyset.remove(key);
                }
            }
        });
    }

    private Set<K> initKeySet() {
        HashSet<K> set = new HashSet<>();
        for (String file : directory.list()) {
            if (new File(directory, file).isFile()) {
                try {
                    set.add(keyParser.apply(file));
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to get key from file: " + file, e);
                }
            }
        }
        return set;
    }

    private M read(K key) throws IOException {
        try (FileInputStream fis = new FileInputStream(fileFor(key));
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            return serializer.deserialize(bis, descriptor);
        } catch (IOException e) {
            throw new IOException("Unable to read " + keyBuilder.apply(key), e);
        }
    }

    private void write(K key, M message) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileFor(key), false);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            serializer.serialize(bos, message);
            bos.flush();
        } catch (IOException e) {
            throw new IOException("Unable to write " + keyBuilder.apply(key), e);
        }
    }

    private File fileFor(K key) {
        return new File(directory, validateKey(keyBuilder.apply(key)));
    }

    private String validateKey(String key) {
        // TODO: Make true file-name validation.
        if (key.contains(File.separator)) {
            throw new IllegalArgumentException("Path name separator in key " + key);
        }
        return key;
    }

    @Override
    public void close() throws IOException {
        cache.invalidateAll();
        keyset.clear();
    }
}
