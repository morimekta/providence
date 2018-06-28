package net.morimekta.providence.storage.dir;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;

public interface FileManager<K> {
    Path getFileFor(@Nonnull K key);

    Path tmpFileFor(@Nonnull K key);

    Collection<K> initialKeySet();
}
