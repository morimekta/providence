package net.morimekta.providence.storage;

import net.morimekta.util.concurrent.ReadWriteMutex;
import net.morimekta.util.concurrent.ReentrantReadWriteMutex;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

class BaseDirectoryStorage<K> {
    private static final String TMP_DIR = ".tmp";

    final File                directory;
    final File                tempDir;
    final Function<String, K> keyParser;
    final Function<K, String> keyBuilder;
    final ReadWriteMutex      mutex;
    final Set<K>              keyset;

    BaseDirectoryStorage(@Nonnull File directory,
                         @Nonnull Function<K, String> keyBuilder,
                         @Nonnull Function<String, K> keyParser) {
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
        this.mutex = new ReentrantReadWriteMutex();
        this.keyset = initKeySet();
    }

    File fileFor(K key, boolean temp) {
        try {
            return new File(temp ? tempDir : directory,
                            validateKey(keyBuilder.apply(key)))
                    .getCanonicalFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
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

    private String validateKey(String key) {
        // TODO: Make true file-name validation.
        if (key.contains(File.separator)) {
            throw new IllegalArgumentException("Path name separator in key " + key);
        }
        return key;
    }

}
