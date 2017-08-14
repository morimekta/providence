package net.morimekta.providence.storage;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.streams.MessageCollectors;
import net.morimekta.providence.streams.MessageStreams;
import net.morimekta.util.concurrent.ReadWriteMutex;
import net.morimekta.util.concurrent.ReentrantReadWriteMutex;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        implements MessageListStore<K,M,F>, Closeable {
    private static final String TMP_DIR = ".tmp";

    private final File                     directory;
    private final File                     tempDir;
    private final Function<K, String>      keyBuilder;
    private final Function<String, K>      keyParser;
    private final Serializer               serializer;
    private final PMessageDescriptor<M, F> descriptor;

    private final ReadWriteMutex    mutex;
    private final Cache<K, List<M>> cache;
    private final Set<K>            keyset;

    public DirectoryMessageListStore(@Nonnull File directory,
                                     @Nonnull Function<K, String> keyBuilder,
                                     @Nonnull Function<String, K> keyParser,
                                     @Nonnull PMessageDescriptor<M,F> descriptor,
                                     @Nonnull Serializer serializer) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + directory.toString());
        }

        this.directory = directory;
        this.tempDir = new File(directory, TMP_DIR);
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new IllegalStateException("Unable to create temp directory: " + tempDir.toString());
        } else if (!tempDir.isDirectory()) {
            throw new IllegalStateException("File blocking temp directory: " + tempDir.toString());
        }
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
            try {
                Map<K, List<M>> out = new HashMap<>();
                for (K key : keys) {
                    File file = fileFor(key, false);
                    if (file.exists()) {
                        try {
                            out.put(key, cache.get(key, () -> read(key)));
                        } catch (ExecutionException e) {
                            // Best effort, as we could not read the message.
                            // At least it was present.
                            out.put(key, new LinkedList<>());
                        } finally {
                            file.delete();
                        }
                        cache.invalidate(key);
                        keyset.remove(key);
                    }
                }
                return out;
            } catch (IOException e) {
                throw new UncheckedIOException(e.getMessage(), e);
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

    private File fileFor(K key, boolean temp) throws IOException {
        return new File(temp ? tempDir : directory,
                        validateKey(keyBuilder.apply(key)))
                .getCanonicalFile();
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
