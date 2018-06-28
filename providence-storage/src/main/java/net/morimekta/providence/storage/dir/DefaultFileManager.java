package net.morimekta.providence.storage.dir;

import net.morimekta.util.FileUtil;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
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

    private final Path                directory;
    private final Path                tempDir;
    private final Function<String, K> keyParser;
    private final Function<K, String> keyBuilder;

    public DefaultFileManager(@Nonnull Path directory,
                              @Nonnull Function<K, String> keyBuilder,
                              @Nonnull Function<String, K> keyParser) {
        try {
            if (!Files.isDirectory(directory)) {
                throw new IllegalArgumentException("Not a directory: " + directory.toString());
            }
            this.directory = FileUtil.readCanonicalPath(directory);
            this.tempDir = this.directory.resolve(TMP_DIR);
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            } else if (!Files.isDirectory(tempDir)) {
                throw new IllegalStateException("File blocking temp directory: " + tempDir.toString());
            }
            this.keyBuilder = keyBuilder;
            this.keyParser = keyParser;
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    @Override
    public Path getFileFor(@Nonnull K key) {
        return directory.resolve(validateKey(keyBuilder.apply(key)));
    }

    @Override
    public Path tmpFileFor(@Nonnull K key) {
        return tempDir.resolve(validateKey(keyBuilder.apply(key)));
    }

    @Override
    public Collection<K> initialKeySet() {
        HashSet<K> set = new HashSet<>();
        try {
            Files.list(directory)
                 .forEach(file -> {
                     if (Files.isRegularFile(file)) {
                         try {
                             set.add(keyParser.apply(file.getFileName().toString()));
                         } catch (Exception e) {
                             throw new IllegalStateException("Unable to get key from file: " + file, e);
                         }
                     }
                 });
        } catch (IOException e) {
            throw new IllegalStateException("Storage directory no longer a directory.", e);
        }

        return set;
    }

    private String validateKey(String key) {
        key = Normalizer.normalize(key, Normalizer.Form.NFKC);
        if (key.startsWith(".")) {
            throw new IllegalArgumentException("Special file char in start of key " + key);
        }
        if (key.contains(File.separator)) {
            throw new IllegalArgumentException("Path name separator in key " + key);
        }
        return key;
    }
}
