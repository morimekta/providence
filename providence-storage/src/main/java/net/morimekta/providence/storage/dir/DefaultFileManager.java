package net.morimekta.providence.storage.dir;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

/**
 * File manager for the {@link net.morimekta.providence.storage.DirectoryMessageStore}
 * and {@link net.morimekta.providence.storage.DirectoryMessageListStore} store
 * classes that keeps all files in a single directory, and keeps a <code>.tmp</code>
 * directory for temporary files.
 *
 * @param <K> The key type.
 */
public class DefaultFileManager<K> implements FileManager<K> {
    private static final String TMP_DIR = ".tmp";

    private final File                directory;
    private final File                tempDir;
    private final Function<String, K> keyParser;
    private final Function<K, String> keyBuilder;

    public DefaultFileManager(@Nonnull File directory,
                              @Nonnull Function<K, String> keyBuilder,
                              @Nonnull Function<String, K> keyParser) {
        try {
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException("Not a directory: " + directory.toString());
            }
            this.directory = directory.getCanonicalFile();
            this.tempDir = new File(directory, TMP_DIR);
            if (!tempDir.exists() && !tempDir.mkdirs()) {
                throw new IllegalStateException("Unable to create temp directory: " + tempDir.toString());
            } else if (!tempDir.isDirectory()) {
                throw new IllegalStateException("File blocking temp directory: " + tempDir.toString());
            }
            this.keyBuilder = keyBuilder;
            this.keyParser = keyParser;
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    @Override
    public File getFileFor(@Nonnull K key) {
        return new File(directory, validateKey(keyBuilder.apply(key)));
    }

    @Override
    public File tmpFileFor(@Nonnull K key) {
        return new File(tempDir, validateKey(keyBuilder.apply(key)));
    }

    @Override
    public Collection<K> initialKeySet() {
        HashSet<K> set = new HashSet<>();
        String[] list = directory.list();
        if (list != null) {
            for (String file : list) {
                if (new File(directory, file).isFile()) {
                    try {
                        set.add(keyParser.apply(file));
                    } catch (Exception e) {
                        throw new IllegalStateException("Unable to get key from file: " + file, e);
                    }
                }
            }
        } else {
            throw new IllegalStateException("Storage directory no longer a directory.");
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
